package com.ypunval.pcbang.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;
import com.lapism.searchview.adapter.SearchAdapter;
import com.lapism.searchview.adapter.SearchItem;
import com.lapism.searchview.history.SearchHistoryTable;
import com.lapism.searchview.view.SearchCodes;
import com.lapism.searchview.view.SearchView;
import com.rey.material.widget.FrameLayout;
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.fragment.PCBasicFragment;
import com.ypunval.pcbang.fragment.PCMapFragment;
import com.ypunval.pcbang.fragment.PCPriceFragment;
import com.ypunval.pcbang.fragment.PCReviewFragment;
import com.ypunval.pcbang.model.Convenience;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.util.Constant;
import com.ypunval.pcbang.util.CustomBottomSheetBehavior;
import com.ypunval.pcbang.util.CustomSearchView;
import com.ypunval.pcbang.util.PCBangClusterItem;
import com.ypunval.pcbang.util.PCBangRenderer;
import com.ypunval.pcbang.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainMapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, ClusterManager.OnClusterClickListener<PCBangClusterItem>,
        ClusterManager.OnClusterInfoWindowClickListener<PCBangClusterItem>, ClusterManager.OnClusterItemClickListener<PCBangClusterItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<PCBangClusterItem>, GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.searchView)
    CustomSearchView searchView;

    @Bind(R.id.ll_pcBang_info)
    NestedScrollView ll_pcBang_info;
    @Bind(R.id.infoTabLayout)
    TabLayout infoTabLayout;
    @Bind(R.id.vpInfo)
    ViewPager vpInfo;

    @Bind(R.id.ll_conveniences)
    LinearLayout ll_conveniences;
    @Bind(R.id.tv_condition)
    TextView tv_condition;

    private static final String TAG = MainMapActivity.class.getName();

    //    Google Map
    MapFragment mapFragment;
    private GoogleMap googleMap;
    private static final int ZOOM = 14;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest = createLocationRequest();
    int pcBangId = 1;
    int resultCount = 0;
    boolean isFirst = true;
    int range = 10;
    private ClusterManager<PCBangClusterItem> mClusterManager;
    private PCBangRenderer pcBangRenderer;
    Marker mSelectedMarker = null;
    int selected_alliance_level = 0;
    float lat = 0;
    float lon = 0;
    private InfoPagerAdapter infoPagerAdapter;
    RealmResults<PCBang> pcBangs;
    CustomBottomSheetBehavior customBottomSheetBehavior;

    //    search view
    int checkedMenuItem = 0;
    private SearchHistoryTable mHistoryDatabase;
    private List<SearchItem> mSuggestionsList;
    private int mVersion = SearchCodes.VERSION_TOOLBAR;
    private int mStyle = SearchCodes.STYLE_TOOLBAR_CLASSIC;
    private int mTheme = SearchCodes.THEME_LIGHT;

    //    Realm & SharedPreperence
    Realm realm;
    SharedPreferences mPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map3);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ButterKnife.bind(this);


        setSearchView();
        setInfoView();
        googleApiClient = getLocationApiClient();

    }

    public void setCanBottomSheetScroll(boolean can){
        customBottomSheetBehavior.setCanScroll(can);
    }


    private void setInfoView(){
        customBottomSheetBehavior = (CustomBottomSheetBehavior) BottomSheetBehavior.from(ll_pcBang_info);
        customBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        infoPagerAdapter = new InfoPagerAdapter(getSupportFragmentManager());
        vpInfo.setAdapter(infoPagerAdapter);
        infoTabLayout.setupWithViewPager(vpInfo);
    }


    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchView != null && searchView.isSearchOpen()) {
            searchView.hide(true);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setSearchView() {

        mHistoryDatabase = new SearchHistoryTable(this);
        mSuggestionsList = new ArrayList<>();

        // SearchView basic attributes  ------------------------------------------------------------
        searchView.setVersion(mVersion);
        searchView.setStyle(mStyle);
        searchView.setTheme(mTheme);
        // -----------------------------------------------------------------------------------------
        searchView.setDivider(true);
        searchView.setHint(R.string.search_hint);
        searchView.setHintSize(getResources().getDimension(R.dimen.search_text_medium));
        searchView.setVoice(false);
        searchView.setAnimationDuration(300);
        searchView.setShadowColor(ContextCompat.getColor(this, R.color.search_shadow_layout));
        searchView.setOnQueryTextListener(new CustomSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mHistoryDatabase.addItem(new SearchItem(query));
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        searchView.setOnSearchMenuListener(new CustomSearchView.SearchMenuListener() {
            @Override
            public void onMenuClick() {
                drawer.openDrawer(GravityCompat.START);
            }
        });


        List<SearchItem> mResultsList = new ArrayList<>();
        SearchAdapter mSearchAdapter = new SearchAdapter(this, mResultsList, mSuggestionsList, mTheme);
        mSearchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                CharSequence text = textView.getText();
                mHistoryDatabase.addItem(new SearchItem(text));
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setAdapter(mSearchAdapter);
        showSearchView();
        CardView cardView = searchView.getCardView();
        FrameLayout.LayoutParams cvParams = (FrameLayout.LayoutParams) cardView.getLayoutParams();
        cvParams.topMargin += getStatusBarHeight();
        cardView.setLayoutParams(cvParams);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setup();
    }

    private void setup() {
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                if (searchView != null && searchView.isSearchOpen()) {
                    searchView.hide(true);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        });


        navigationView.setNavigationItemSelectedListener(this);
        if (checkedMenuItem > -1) {
            navigationView.getMenu().getItem(checkedMenuItem).setChecked(true);
        }

    }


    private void showSearchView() {
        mSuggestionsList.clear();
        mSuggestionsList.addAll(mHistoryDatabase.getAllItems());
        mSuggestionsList.add(new SearchItem("Google"));
        mSuggestionsList.add(new SearchItem("Android"));
    }


    @Override
    protected void onStart() {
        realm = Realm.getDefaultInstance();
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        googleApiClient.connect();
        initCategory();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop: called");
        realm.close();
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchView.SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && results.size() > 0) {
                String searchWrd = results.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG, "getStatusBarHeight: " + result);
        return result;
    }


    //    Map View Start

    @Override
    public void onMapReady(GoogleMap _googleMap) {
        googleMap = _googleMap;
        setMapData();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(120000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    private GoogleApiClient getLocationApiClient() {
        return new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
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
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        View zoomButton = ((View) mapFragment.getView().findViewById(0x1).getParent()).findViewById(0x1);
        View locationButton = ((View) mapFragment.getView().findViewById(0x1).getParent()).findViewById(0x2);

        RelativeLayout.LayoutParams locationParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        RelativeLayout.LayoutParams newLocationParams = new RelativeLayout.LayoutParams(locationParams.width, locationParams.height);
        newLocationParams.rightMargin = locationParams.rightMargin;
        newLocationParams.topMargin = (int)getResources().getDimension(R.dimen.my_location_top_margin);
        newLocationParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        newLocationParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

        locationParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        locationParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        locationButton.setLayoutParams(newLocationParams);

        mClusterManager = new ClusterManager<PCBangClusterItem>(this, googleMap);
        pcBangRenderer = new PCBangRenderer(this, googleMap, mClusterManager);
        mClusterManager.setRenderer(pcBangRenderer);

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(this, "위치권한을 설정해주세요", Toast.LENGTH_SHORT).show();
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
        int margin = (int) this.getResources().getDimension(R.dimen.theme_margin);
        int padding = (int) this.getResources().getDimension(R.dimen.theme_padding);
        int height = (int) this.getResources().getDimension(R.dimen.theme_height);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
        params.setMargins(margin, 0, 0, 0);
        int count = 0;
        for (int i = 0; i < Constant.conveniences.size(); i++) {
            final Convenience convenience = Constant.conveniences.get(i);
            TextView textView = null;
            if (convenience.isSelected()) {
                count++;
                textView = (TextView) LayoutInflater.from(this).inflate(R.layout.item_selected_convenience, null, false);
            } else {
                textView = (TextView) LayoutInflater.from(this).inflate(R.layout.item_convenience, null, false);
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

    }


    private void setPCBangData(int pcBangId) {
        final Animation animation = AnimationUtils.loadAnimation(
                this, R.anim.ani_apear_upside);
        customBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        ((PCBasicFragment) infoPagerAdapter.getItem(0)).selectedPCBang(pcBangId);
        ((PCPriceFragment) infoPagerAdapter.getItem(1)).selectedPCBang(pcBangId);
        ((PCReviewFragment) infoPagerAdapter.getItem(2)).selectedPCBang(pcBangId);
        ((PCMapFragment) infoPagerAdapter.getItem(3)).selectedPCBang(pcBangId);
    }


    @Override
    public void onMapClick(LatLng latLng) {
        deselectMarker();
    }

    @Override
    public boolean onClusterClick(Cluster<PCBangClusterItem> cluster) {
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
//        deselectMarker();
        Log.i("cluster", "click");
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(PCBangClusterItem pcBangClusterItem) {
        Log.i("cluster item infoWindow", "click");
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<PCBangClusterItem> cluster) {
        Log.i("clusterInfowindow", "click");
    }


    @Override
    public boolean onClusterItemClick(PCBangClusterItem pcBangClusterItem) {
        Log.i("cluster item", "click");

        deselectMarker();
        selectMarker(pcBangClusterItem);
        setPCBangData(pcBangClusterItem.getId());
        vpInfo.setCurrentItem(0);

        return false;
    }

    private void deselectMarker() {

        customBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

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


    private void selectMarker(PCBangClusterItem pcBangClusterItem){
        customBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        ImageView imageView = pcBangRenderer.getmImageView();
        TextView textView = pcBangRenderer.getTextView();
        IconGenerator iconGenerator = pcBangRenderer.getmIconGenerator();
        Bitmap icon;

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
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "위치를 가져올수 있는 권한이없습니다.", Toast.LENGTH_SHORT).show();
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
        if (isFirst) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(ZOOM).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 300, null);
            isFirst = false;
        }

        SharedPreferences.Editor editor = mPref.edit();
        editor.putFloat("latitude", (float) location.getLatitude());
        editor.putFloat("longitude", (float) location.getLongitude());
        editor.commit();
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
}
