package com.ypunval.pcbang.fragment;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.activity.PCBangInfoActivity;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.util.PCBangClusterItem;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PCMapFragment extends BaseRealmFragment implements ClusterManager.OnClusterClickListener<PCBangClusterItem>, ClusterManager.OnClusterInfoWindowClickListener<PCBangClusterItem>, ClusterManager.OnClusterItemClickListener<PCBangClusterItem>, ClusterManager.OnClusterItemInfoWindowClickListener<PCBangClusterItem>, GoogleMap.OnMapClickListener {
    private static final String TAG = PCMapFragment.class.getName();


    int pcBangId = 0;


    @OnClick(R.id.btn_left)
    public void moveLeft(View v) {
        ((PCBangInfoActivity) getContext()).movePage(2);
        Log.i("move_left", "clicked");
    }


    private ClusterManager<PCBangClusterItem> mClusterManager;
    MapView mMapView;
    private GoogleMap googleMap;
    PCBangRenderer pcBangRenderer;
    Marker mSelectedMarker = null;
    int selected_alliance_level = 0;
    float latitude = 0;
    float longitude = 0;

    public PCMapFragment() {
        // Required empty public constructor
    }

    public static PCMapFragment newInstance(int pcBangId) {
        PCMapFragment fragment = new PCMapFragment();
        Bundle args = new Bundle();
        args.putInt("pcBangId", pcBangId);
        fragment.setArguments(args);
        return fragment;
    }

    public void selectedPCBang(int pcBangId){
        this.pcBangId = pcBangId;
        Log.i(TAG, "selectedPCBang: clicked");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            pcBangId = getArguments().getInt("pcBangId");
        }
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
            // Draw a single person.
            // Set the info window to show their name.
            switch (pcBangClusterItem.getAllianceLevel()) {
                case 0:
                    mImageView.setImageResource(R.drawable.marker_red_unselected);
                    break;
                case 1:
                    mImageView.setImageResource(R.drawable.marker_red_unselected);
                    break;
                case 2:
                    mImageView.setImageResource(R.drawable.marker_red_unselected);
                    break;
            }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_map, container,
                false);

        ButterKnife.bind(this, v);

        // latitude and longitude
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        latitude = pref.getFloat("latitude", 0);
        longitude = pref.getFloat("longitude", 0);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();



        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

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

        PCBang pcBang = realm.where(PCBang.class).equalTo("id", pcBangId).findFirst();
        PCBangClusterItem item = new PCBangClusterItem();
        mClusterManager.addItem(item);
        mClusterManager.cluster();

        googleMap.setMyLocationEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(item.getPosition()).zoom(14).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onMapClick(LatLng latLng) {
        deselectMarker();
    }

    private void deselectMarker() {

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


        Log.i("cluster item", "click");
        return false;
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
}

