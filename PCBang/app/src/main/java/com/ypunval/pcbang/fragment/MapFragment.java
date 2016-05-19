package com.ypunval.pcbang.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.rey.material.widget.Button;
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.activity.MainActivity;
import com.ypunval.pcbang.activity.PCBangInfoActivity;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.util.Constant;
import com.ypunval.pcbang.util.PCBangClusterItem;
import com.ypunval.pcbang.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MapFragment extends BaseRealmFragment implements ClusterManager.OnClusterClickListener<PCBangClusterItem>, ClusterManager.OnClusterInfoWindowClickListener<PCBangClusterItem>, ClusterManager.OnClusterItemClickListener<PCBangClusterItem>, ClusterManager.OnClusterItemInfoWindowClickListener<PCBangClusterItem>, GoogleMap.OnMapClickListener {
    @Bind(R.id.mapView)
    MapView mMapView;

    @Bind(R.id.ll_pcBang_info)
    LinearLayout ll_pcBang_info;
    @Bind(R.id.iv_image)
    ImageView iv_image;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.tv_rate)
    TextView tv_rate;
    @Bind(R.id.tv_review_count)
    TextView tv_review_count;
    @Bind(R.id.tv_left_seat)
    TextView tv_left_seat;
    @Bind(R.id.tv_total_seat)
    TextView tv_total_seat;
    @Bind(R.id.tv_address)
    TextView tv_address;
    @Bind(R.id.tv_distance)
    TextView tv_distance;
    @Bind(R.id.tv_min_price)
    TextView tv_min_price;
    @Bind(R.id.tv_convenience)
    TextView tv_convenience;



    Integer pcBangId = null;
    int resultCount = 0;
    boolean isFirst = true;



    static final LatLng SEOUL = new LatLng(37.56, 126.97);

    @OnClick(R.id.btnMore)
    public void onMoreClick(View v) {
        moreClick(v);
    }

    @OnClick(R.id.ll_pcBang_info)
    public void goPCBangInfoActivity(View v) {
        if (pcBangId != null) {
            Intent intent = new Intent(getContext(), PCBangInfoActivity.class);
            intent.putExtra("pcBangId", pcBangId);
            getContext().startActivity(intent);
        }
    }


    @OnClick(R.id.btn_left)
    public void moveLeft(View v) {
        ((MainActivity) getContext()).movePage(2);
        Log.i("move_left", "clicked");
    }

    private ClusterManager<PCBangClusterItem> mClusterManager;
    private GoogleMap googleMap;
    private PCBangRenderer pcBangRenderer;
    Marker mSelectedMarker = null;
    int selected_alliance_level = 0;
    float lat = 0;
    float lon = 0;

    RealmResults<PCBang> pcBangs;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container,
                false);
        ButterKnife.bind(this, v);

        // latitude and longitude


        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Perform any camera updates here
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        lat = mPref.getFloat("latitude", 0);
        lon = mPref.getFloat("longitude", 0);

        Constant.mapRangeKm = 3;
        setMapView();
        getData();
//        setMarker();
    }

    private void showSnackBar() {
        if (isFirst) {
            isFirst = false;
            return;
        }

        String snackBarMessage = "검색된 PC방 개수 : " + resultCount + "개" + "  (" + (Constant.mapRangeKm) + "km)";
        Snackbar snackbar = Snackbar.make(getView(), snackBarMessage, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void moreClick(View v) {
        Constant.mapRangeKm += 3;
        getData();
//        setMarker();
        ((Button) v).setText((Constant.mapRangeKm + 3) + "km 더보기");

        float zoom = googleMap.getCameraPosition().zoom - 1;
        LatLng latLng = googleMap.getCameraPosition().target;

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void getData() {
        if (lat != 0 && lon != 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmQuery<PCBang> query = realm.where(PCBang.class).equalTo("exist", true);
                    float lat_small = lat - Constant.LATITUDE_CONSTANT * Constant.mapRangeKm;
                    float lat_big = lat + Constant.LATITUDE_CONSTANT * Constant.mapRangeKm;

                    float lon_small = lon - Constant.LONGITUDE_CONSTANT * Constant.mapRangeKm;
                    float lon_big = lon + Constant.LONGITUDE_CONSTANT * Constant.mapRangeKm;
                    pcBangs = query.between("latitude", lat_small, lat_big)
                            .between("longitude", lon_small, lon_big).findAll();

                    resultCount = pcBangs.size();

                    mClusterManager.clearItems();
                    if (pcBangs != null) {
                        for (PCBang pcBang : pcBangs) {
                            PCBangClusterItem item = new PCBangClusterItem();
                            mClusterManager.addItem(item);
                        }
                    }


                }
            }, new Realm.Transaction.Callback() {
                @Override
                public void onSuccess() {
                    showSnackBar();
                }
            });
        }


        if (lat != 0 && lon != 0) {

        }
    }

    private void setMarker() {
        Log.i("start", "query all start");


        Log.i("start", "query all end");
    }

    private void setMapView() {

        googleMap = mMapView.getMap();

        mClusterManager = new ClusterManager<PCBangClusterItem>(getContext(), googleMap);
        pcBangRenderer = new PCBangRenderer();
        mClusterManager.setRenderer(pcBangRenderer);


        googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);
        googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.setOnMapClickListener(this);


        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);


        mClusterManager.cluster();

        googleMap.setMyLocationEnabled(true);

//        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(14).build();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(SEOUL).zoom(14).build();

        googleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    private void setPCBangData(int pcBangId) {

        PCBang pcBang = realm.where(PCBang.class).equalTo("id", pcBangId).findFirst();

        final Animation animation = AnimationUtils.loadAnimation(
                getContext(), R.anim.ani_apear_upside);
        ll_pcBang_info.startAnimation(animation);
        ll_pcBang_info.setVisibility(View.VISIBLE);
        tv_title.setText(pcBang.getName());
        tv_rate.setText(pcBang.getAverageRate() + "");
        tv_review_count.setText(pcBang.getReviewCount() + "");
        tv_left_seat.setText(pcBang.getLeftSeat() + "");
        tv_total_seat.setText(pcBang.getTotalSeat() + "");
        tv_address.setText(pcBang.getAddress1());


        float km = Util.calDistance(lat, lon, pcBang.getLatitude(), pcBang.getLongitude());
        String dist = "";
        if (km < 1) {
            int i_meter = (int) (km * 1000);
            dist = i_meter + "m";
        } else {
            dist = String.format("%.1f", km) + "km";
        }
        tv_distance.setText(dist);
        if (pcBang.getMinPrice() == 1000000)
            tv_min_price.setText("?");
        else
            tv_min_price.setText(pcBang.getMinPrice() + "");
    }


    @Override
    public void onClusterItemInfoWindowClick(PCBangClusterItem pcBangClusterItem) {
        Log.i("cluster item infoWindow", "click");
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private class PCBangRenderer extends DefaultClusterRenderer<PCBangClusterItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getContext());
        private final ImageView mImageView;
        private final int mDimension;

        public PCBangRenderer() {
            super(getContext(), googleMap, mClusterManager);

            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView = new ImageView(getContext());
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
//            mImageView.setPadding(0, 0, 0, 0);
            mIconGenerator.setContentView(mImageView);
            mIconGenerator.setBackground(getResources().getDrawable(R.drawable.bg_transparent));
        }

        public ImageView getmImageView() {
            return mImageView;
        }

        public IconGenerator getmIconGenerator() {
            return mIconGenerator;
        }

        @Override
        protected void onBeforeClusterItemRendered(PCBangClusterItem pcBangClusterItem, MarkerOptions markerOptions) {

//            switch (pcBang.getAllianceLevel()) {
//                case 0:
//                    mImageView.setImageResource(R.drawable.marker_red_unselected);
//                    break;
//                case 1:
//                    mImageView.setImageResource(R.drawable.marker_red_unselected);
//                    break;
//                case 2:
//                    mImageView.setImageResource(R.drawable.marker_red_unselected);
//                    break;
//            }


            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(pcBangClusterItem.getPcBangName());

        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {
        deselectMarker();
    }

    private void deselectMarker() {

        if (ll_pcBang_info.getVisibility() == View.VISIBLE) {
            final Animation animation = AnimationUtils.loadAnimation(
                    getContext(), R.anim.ani_disapear_downside);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ll_pcBang_info.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            ll_pcBang_info.startAnimation(animation);
        }

        if (mSelectedMarker != null) {
            ImageView imageView = pcBangRenderer.getmImageView();
            IconGenerator iconGenerator = pcBangRenderer.getmIconGenerator();
            Bitmap icon;

            switch (selected_alliance_level) {
                case 0:
                    imageView.setImageResource(R.drawable.marker_red_unselected);
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.marker_red_unselected);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.marker_red_unselected);
                    break;
            }
            iconGenerator.setContentView(imageView);
            icon = iconGenerator.makeIcon();
            mSelectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
            mSelectedMarker = null;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<PCBangClusterItem> cluster) {
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        deselectMarker();

        Log.i("cluster", "click");
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<PCBangClusterItem> cluster) {
        Log.i("clusterInfowindow", "click");

    }

    @Override
    public boolean onClusterItemClick(PCBangClusterItem pcBangClusterItem) {
        ImageView imageView = pcBangRenderer.getmImageView();
        IconGenerator iconGenerator = pcBangRenderer.getmIconGenerator();
        Bitmap icon;
        switch (selected_alliance_level) {
            case 0:
                imageView.setImageResource(R.drawable.marker_red_unselected);
                break;
            case 1:
                imageView.setImageResource(R.drawable.marker_red_unselected);
                break;
            case 2:
                imageView.setImageResource(R.drawable.marker_red_unselected);
                break;
        }
        iconGenerator.setContentView(imageView);
        icon = iconGenerator.makeIcon();


        if (mSelectedMarker != null) {
            try {
                mSelectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
            } catch (IllegalArgumentException e) {
                mSelectedMarker = null;
            }


        }


        switch (pcBangClusterItem.getAllianceLevel()) {
            case 0:
                imageView.setImageResource(R.drawable.marker_red_selected);
                break;
            case 1:
                imageView.setImageResource(R.drawable.marker_red_selected);
                break;
            case 2:
                imageView.setImageResource(R.drawable.marker_red_selected);
                break;
        }
        iconGenerator.setContentView(imageView);
        icon = iconGenerator.makeIcon();

        mSelectedMarker = pcBangRenderer.getMarker(pcBangClusterItem);
        mSelectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        setPCBangData(pcBangClusterItem.getId());

        Log.i("cluster item", "click");
        return false;
    }
}

