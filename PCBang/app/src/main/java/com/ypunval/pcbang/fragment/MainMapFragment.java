package com.ypunval.pcbang.fragment;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.activity.MainActivity;
import com.ypunval.pcbang.model.Convenience;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.util.Constant;
import com.ypunval.pcbang.util.PCBangClusterItem;
import com.ypunval.pcbang.util.PCBangRenderer;
import com.ypunval.pcbang.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainMapFragment extends BaseFragment implements ClusterManager.OnClusterClickListener<PCBangClusterItem>,
        ClusterManager.OnClusterInfoWindowClickListener<PCBangClusterItem>, ClusterManager.OnClusterItemClickListener<PCBangClusterItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<PCBangClusterItem>, GoogleMap.OnMapClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

//    @Bind(R.id.mapView)
    MapView mMapView;

    @Bind(R.id.ll_pcBang_info)
    LinearLayout ll_pcBang_info;
    private InfoPagerAdapter infoPagerAdapter;
//    @Bind(R.id.appbar)
    AppBarLayout appBarLayout;
//    @Bind(R.id.infoTabLayout)
    TabLayout infoTabLayout;
//    @Bind(R.id.vpInfo)
    ViewPager vpInfo;

//    @Bind(R.id.ll_conveniences)
    LinearLayout ll_conveniences;
//    @Bind(R.id.tv_condition)
    TextView tv_condition;

//    @Bind(R.id.ll_selected_container)
//    LinearLayout ll_selected_container;
//    @Bind(R.id.ll_selected_conveniences)
//    LinearLayout ll_selected_conveniences;

    private static final String TAG = MainMapFragment.class.getName();
    private static final int ZOOM = 14;

    Realm realm;
    SharedPreferences mPref;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest = createLocationRequest();


    int pcBangId = 1;
    int resultCount = 0;
    boolean isFirst = true;

    int range = 10;
    int range_comparator = 3;

    static final LatLng SEOUL = new LatLng(37.56, 126.97);


    @OnClick(R.id.btn_left)
    public void moveLeft(View v) {
        ((MainActivity) getContext()).movePage(1);
        Log.i("move_left", "clicked");
    }


    private GoogleMap googleMap;
    private ClusterManager<PCBangClusterItem> mClusterManager;
    private PCBangRenderer pcBangRenderer;
    Marker mSelectedMarker = null;
    int selected_alliance_level = 0;
    float lat = 0;
    float lon = 0;

    RealmResults<PCBang> pcBangs;

    public MainMapFragment() {
        // Required empty public constructor
    }

    public static MainMapFragment newInstance() {
        return new MainMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_map, container, false);
        mMapView = (MapView)v.findViewById(R.id.mapView);
        ll_conveniences = (LinearLayout)v.findViewById(R.id.ll_conveniences);
        tv_condition = (TextView)v.findViewById(R.id.tv_condition);
//        @Bind(R.id.mapView)
//        @Bind(R.id.ll_conveniences)
//        @Bind(R.id.tv_condition)

//        View v_content = (View)v.findViewById(R.id.content_main_map);
        ll_pcBang_info = (LinearLayout)v.findViewById(R.id.ll_pcBang_info);
        appBarLayout = (AppBarLayout)v.findViewById(R.id.appbar);
        infoTabLayout = (TabLayout)v.findViewById(R.id.infoTabLayout);
        vpInfo = (ViewPager)v.findViewById(R.id.vpInfo);

//        ButterKnife.bind(this, v);

        Log.i(TAG, "onCreateView: start");
        realm = Realm.getDefaultInstance();
        mPref = PreferenceManager.getDefaultSharedPreferences(getContext());


        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleApiClient = getLocationApiClient();


        infoPagerAdapter = new InfoPagerAdapter(getFragmentManager());
        vpInfo.setAdapter(infoPagerAdapter);
        infoTabLayout.setupWithViewPager(vpInfo);

        initCategory();
        setMapData();

        View zoomButton = ((View) mMapView.findViewById(0x1).getParent()).findViewById(0x1);
        View locationButton = ((View) mMapView.findViewById(0x1).getParent()).findViewById(0x2);

        RelativeLayout.LayoutParams locationParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        RelativeLayout.LayoutParams newLocationParams = new RelativeLayout.LayoutParams(locationParams.width, locationParams.height);
        newLocationParams.rightMargin = locationParams.rightMargin;
        newLocationParams.topMargin = (int)getResources().getDimension(R.dimen.my_location_top_margin);
        newLocationParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        newLocationParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

        locationParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        locationParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        locationButton.setLayoutParams(newLocationParams);

        return v;
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(120000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    private GoogleApiClient getLocationApiClient() {
        return new GoogleApiClient.Builder(getContext()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }


    private void setMapData() {

        lat = mPref.getFloat("latitude", 0);
        lon = mPref.getFloat("longitude", 0);

        Constant.mapRangeKm = 3;
        setMapView();
        getData();
        setCategory();
    }

    private void setMapView() {
        googleMap = mMapView.getMap();
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        mClusterManager = new ClusterManager<PCBangClusterItem>(getContext(), googleMap);
        pcBangRenderer = new PCBangRenderer(getContext(), googleMap, mClusterManager);
        mClusterManager.setRenderer(pcBangRenderer);

        mMapView.getMapAsync(this);

        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);
        MultiListener ml = new MultiListener();
        ml.registerListener(mClusterManager);
        ml.registerListener(new GoogleMap.OnCameraChangeListener() {
            private float pastZoom = -1;
            private LatLng pastLatlng = new LatLng(lat, lon);

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (Math.abs(cameraPosition.zoom - pastZoom) >= 1) {
                    pastZoom = cameraPosition.zoom;
                } else {
                    if (cameraPosition.zoom > 12) {
                        float dist = Util.calDistance(pastLatlng.latitude, pastLatlng.longitude, cameraPosition.target.latitude, cameraPosition.target.longitude);
                        Log.i(TAG, "onCameraChange: dist : " + dist);
                        if (dist > 5) {

                            lat = (float) cameraPosition.target.latitude;
                            lon = (float) cameraPosition.target.longitude;
                            Log.i(TAG, "onCameraChange: start get data");
                            getData();
                            pastLatlng = cameraPosition.target;

                        }
                    } else {

                    }
                }

                if (cameraPosition.zoom != pastZoom) {
                    pastZoom = cameraPosition.zoom;
                    calculate();
                }
                Log.i(TAG, "onCameraChange: " + cameraPosition.zoom);

            }
        });
        googleMap.setOnCameraChangeListener(ml);

        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        mClusterManager.cluster();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(getContext(), "위치권한을 설정해주세요", Toast.LENGTH_SHORT).show();
        }


        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(ZOOM).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private void getData() {
        if (lat != 0 && lon != 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Log.i(TAG, "execute: " + System.currentTimeMillis());
                    RealmQuery<PCBang> query = realm.where(PCBang.class).equalTo("exist", true);
                    float lat_small = lat - Constant.LATITUDE_CONSTANT * range;
                    float lat_big = lat + Constant.LATITUDE_CONSTANT * range;

                    float lon_small = lon - Constant.LONGITUDE_CONSTANT * range;
                    float lon_big = lon + Constant.LONGITUDE_CONSTANT * range;
                    pcBangs = query.between("latitude", lat_small, lat_big)
                            .between("longitude", lon_small, lon_big).findAll();

                    resultCount = pcBangs.size();

                    mClusterManager.clearItems();
                    if (pcBangs != null) {
                        for (PCBang pcBang : pcBangs) {
                            PCBangClusterItem item = new PCBangClusterItem(pcBang);
                            mClusterManager.addItem(item);
                        }
                    }

                }
            }, new Realm.Transaction.Callback() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "execute: " + System.currentTimeMillis());
                    mClusterManager.cluster();

                }
            });
        }


        if (lat != 0 && lon != 0) {

        }
    }


    private void initCategory() {
        Constant.conveniences.clear();
        Constant.selectedConveniences.clear();
        RealmResults<Convenience> conveniencesResult = realm.where(Convenience.class).findAll();
        for (int i = 0; i < conveniencesResult.size(); i++) {
            Constant.conveniences.add(conveniencesResult.get(i));
            Collections.sort(Constant.conveniences, new Comparator<Convenience>() {
                @Override
                public int compare(Convenience obj1, Convenience obj2) {
                    return (obj1.getOrder() < obj2.getOrder()) ? -1 : (obj1.getOrder() > obj2.getOrder()) ? 1 : 0;
                }
            });
        }
    }


    private void setCategory() {

        ll_conveniences.removeAllViews();
        int margin = (int) getContext().getResources().getDimension(R.dimen.theme_margin);
        int padding = (int) getContext().getResources().getDimension(R.dimen.theme_padding);
        int height = (int) getContext().getResources().getDimension(R.dimen.theme_height);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
        params.setMargins(margin, 0, 0, 0);
        int count = 0;
        for (int i = 0; i < Constant.conveniences.size(); i++) {
            final Convenience convenience = Constant.conveniences.get(i);
            TextView textView = null;
            if (convenience.isSelected()) {
                count++;
                textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_selected_convenience, null, false);
            } else {
                textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_convenience, null, false);
            }

            textView.setText(convenience.getName());
            textView.setLayoutParams(params);
            textView.setPadding(padding, 0, padding, 0);
            ll_conveniences.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Constant.addSelectedConvenience(convenience);
                    if (convenience.isSelected())
                        convenience.setSelected(false);
                    else
                        convenience.setSelected(true);

                    setCategory();
                }
            });
        }
        if (count == 0)
            tv_condition.setText("검색조건    : ");
        else
            tv_condition.setText("검색조건(" + count + ") : ");

/*
        if (Constant.selectedConveniences.size() == 0) {
            ll_selected_container.setVisibility(View.GONE);
        } else {
            ll_selected_container.setVisibility(View.VISIBLE);
            ll_selected_conveniences.removeAllViews();
            for (int i = 0; i < Constant.selectedConveniences.size(); i++) {
                final Convenience convenience = Constant.selectedConveniences.get(i);
                TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_selected_convenience, null, false);
                textView.setText(Constant.selectedConveniences.get(i).getName());
                textView.setLayoutParams(params);
                textView.setPadding(padding, 0, padding, 0);
                ll_selected_conveniences.addView(textView);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Constant.removeSelectedConvenience(convenience);
                        setCategory();
                    }
                });
            }
        }
        */
    }


    private void setPCBangData(int pcBangId) {
        final Animation animation = AnimationUtils.loadAnimation(
                getContext(), R.anim.ani_apear_upside);
        ll_pcBang_info.startAnimation(animation);
        ll_pcBang_info.setVisibility(View.VISIBLE);

        ((PCBasicFragment) infoPagerAdapter.getItem(0)).selectedPCBang(pcBangId);
        ((PCPriceFragment) infoPagerAdapter.getItem(1)).selectedPCBang(pcBangId);
        ((PCReviewFragment) infoPagerAdapter.getItem(2)).selectedPCBang(pcBangId);
        ((PCMapFragment) infoPagerAdapter.getItem(3)).selectedPCBang(pcBangId);
    }


    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }


    @Override
    public void onMapClick(LatLng latLng) {
        deselectMarker();
    }


    private void deselectMarker() {
        Log.i(TAG, "deselectMarker: ok");

        if (ll_pcBang_info.getVisibility() == View.VISIBLE) {
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.ani_disapear_downside);
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
            TextView textView = pcBangRenderer.getTextView();
            IconGenerator iconGenerator = pcBangRenderer.getmIconGenerator();
            Bitmap icon;

            switch (selected_alliance_level) {
                case 0:
//                    imageView.setImageResource(R.drawable.marker_red_unselected);
                    textView.setBackgroundResource(R.drawable.marker_unselected);
                    break;
                case 1:
//                    imageView.setImageResource(R.drawable.marker_red_unselected);
                    textView.setBackgroundResource(R.drawable.marker_unselected);
                    break;
                case 2:
//                    imageView.setImageResource(R.drawable.marker_red_unselected);
                    textView.setBackgroundResource(R.drawable.marker_unselected);
                    break;
            }
//            iconGenerator.setContentView(imageView);
            icon = iconGenerator.makeIcon();
            mSelectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
            mSelectedMarker = null;
        }
    }


    @Override
    public void onClusterItemInfoWindowClick(PCBangClusterItem pcBangClusterItem) {
        Log.i("cluster item infoWindow", "click");
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
        TextView textView = pcBangRenderer.getTextView();
        IconGenerator iconGenerator = pcBangRenderer.getmIconGenerator();
        Bitmap icon;
        switch (selected_alliance_level) {
            case 0:
                textView.setBackgroundResource(R.drawable.marker_unselected);
                break;
            case 1:
                textView.setBackgroundResource(R.drawable.marker_unselected);
                break;
            case 2:
                textView.setBackgroundResource(R.drawable.marker_unselected);
                break;
        }
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
                textView.setBackgroundResource(R.drawable.marker_selected);
                break;
            case 1:
                textView.setBackgroundResource(R.drawable.marker_selected);
                break;
            case 2:
                textView.setBackgroundResource(R.drawable.marker_selected);
                break;
        }
        icon = iconGenerator.makeIcon();

        mSelectedMarker = pcBangRenderer.getMarker(pcBangClusterItem);
        mSelectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        setPCBangData(pcBangClusterItem.getId());

        Log.i("cluster item", "click");


        LatLng latLng = new LatLng(pcBangClusterItem.getLatLng().latitude, pcBangClusterItem.getLatLng().longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(googleMap.getCameraPosition().zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 300, null);

        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: ok");
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getContext(), "위치를 가져올수 있는 권한이없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (isFirst){
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(ZOOM).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 300, null);
            isFirst = false;
        }

        SharedPreferences.Editor editor = mPref.edit();
        editor.putFloat("latitude", (float) location.getLatitude());
        editor.putFloat("longitude", (float) location.getLongitude());
        editor.commit();
    }


    public class InfoPagerAdapter extends FragmentPagerAdapter {

        public InfoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PCBasicFragment.newInstance(pcBangId);
                case 1:
                    return PCPriceFragment.newInstance(pcBangId);
                case 2:
                    return PCReviewFragment.newInstance(pcBangId);
                case 3:
                    return PCMapFragment.newInstance(pcBangId);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "기본정보";
                case 1:
                    return "요금정보";
                case 2:
                    return "후기·별점";
                case 3:
                    return "지도보기";
            }
            return null;
        }
    }





    private void calculate() {
        VisibleRegion vr = googleMap.getProjection().getVisibleRegion();
        double left = vr.latLngBounds.southwest.longitude;
        double top = vr.latLngBounds.northeast.latitude;
        double right = vr.latLngBounds.northeast.longitude;
        double bottom = vr.latLngBounds.southwest.latitude;

        Location center = new Location("center");
        center.setLatitude(vr.latLngBounds.getCenter().latitude);
        center.setLongitude(vr.latLngBounds.getCenter().longitude);
        Location center_top = new Location("center_top");//(center's latitude,vr.latLngBounds.southwest.longitude)
        center_top.setLatitude(center.getLatitude());
        center_top.setLongitude(vr.latLngBounds.northeast.longitude);

        float dis = Util.calDistance(center.getLatitude(), center.getLongitude(), center_top.getLatitude(), center_top.getLongitude());

        Log.i(TAG, "calculate: " + dis);
    }


    public class MultiListener implements GoogleMap.OnCameraChangeListener {
        private List<GoogleMap.OnCameraChangeListener> mListeners = new ArrayList<GoogleMap.OnCameraChangeListener>();

        public void registerListener(GoogleMap.OnCameraChangeListener listener) {
            mListeners.add(listener);
        }

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            for (GoogleMap.OnCameraChangeListener ccl : mListeners) {
                ccl.onCameraChange(cameraPosition);
            }
        }
    }
}

