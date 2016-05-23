package com.ypunval.pcbang.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
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
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.fragment.BaseFragment;
import com.ypunval.pcbang.fragment.PCBasicFragment;
import com.ypunval.pcbang.fragment.PCMapFragment;
import com.ypunval.pcbang.fragment.PCPriceFragment;
import com.ypunval.pcbang.fragment.PCReviewFragment;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.Convenience;
import com.ypunval.pcbang.model.Dong;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.model.Si;
import com.ypunval.pcbang.model.Subway;
import com.ypunval.pcbang.model.Sync;
import com.ypunval.pcbang.update.JSONToRealm;
import com.ypunval.pcbang.update.PCBangHttpHelper;
import com.ypunval.pcbang.util.Constant;
import com.ypunval.pcbang.util.CustomBottomSheetBehavior;
import com.ypunval.pcbang.util.CustomSearchView;
import com.ypunval.pcbang.util.PCBangClusterItem;
import com.ypunval.pcbang.util.PCBangRenderer;
import com.ypunval.pcbang.util.SliderPCBangInfoView;
import com.ypunval.pcbang.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MainMapActivity extends BaseRealmActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, ClusterManager.OnClusterClickListener<PCBangClusterItem>,
        ClusterManager.OnClusterInfoWindowClickListener<PCBangClusterItem>, ClusterManager.OnClusterItemClickListener<PCBangClusterItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<PCBangClusterItem>, GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.fl_slider)
    FrameLayout fl_slider;
    @Bind(R.id.slider)
    SliderLayout slider;
    @Bind(R.id.custom_indicator)
    PagerIndicator indicator;

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
    static final int SPLASH_REQUEST_CODE = 1234;
    static final int WRITE_REVIEW_CODE = 22;
    private final long FINISH_INTERVAL_TIME = 2000;

    private long backPressedTime = 0;

    //    Google Map
    MapFragment mapFragment;
    private GoogleMap googleMap;
    private static final int ZOOM = 16;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest = createLocationRequest();
    int pcBangId = 1;
    int resultCount = 0;
    boolean isFirst = true;
    int range = 3;
    private ClusterManager<PCBangClusterItem> mClusterManager;
    private PCBangRenderer pcBangRenderer;
    Marker mSelectedMarker = null;
    int selected_alliance_level = 0;
    float lat = 0;
    float lon = 0;
    private InfoPagerAdapter infoPagerAdapter;
    CustomBottomSheetBehavior customBottomSheetBehavior;

    //  Bottom Sheet State
    int preBottomSheetState = BottomSheetBehavior.STATE_HIDDEN;
    float preSlideOffset = -1;
    int preVpInfoPosition = 0;
    boolean isAnimating = false;
    int selectedPCBangId = 1;



    //    search view
    int checkedMenuItem = 0;
    private SearchHistoryTable mHistoryDatabase;
    private List<SearchItem> mSuggestionsList;
    private int mVersion = SearchCodes.VERSION_TOOLBAR;
    private int mStyle = SearchCodes.STYLE_TOOLBAR_CLASSIC;
    private int mTheme = SearchCodes.THEME_LIGHT;

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

        setSlider();
        // TODO: 2016. 5. 20. 업데이트하기 주석 제거

//        update();


    }

    public void setCanBottomSheetScroll(boolean can) {
        customBottomSheetBehavior.setCanScroll(can);
    }


    private void setInfoView() {
        customBottomSheetBehavior = (CustomBottomSheetBehavior) BottomSheetBehavior.from(ll_pcBang_info);
        customBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        infoPagerAdapter = new InfoPagerAdapter(getSupportFragmentManager());
        infoPagerAdapter.addFrag(new PCBasicFragment(), "기본정보");
        infoPagerAdapter.addFrag(new PCPriceFragment(), "요금정보");
        infoPagerAdapter.addFrag(new PCReviewFragment(), "후기");
        vpInfo.setAdapter(infoPagerAdapter);
        infoTabLayout.setupWithViewPager(vpInfo);

        vpInfo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected: "+position);
//                if (preVpInfoPosition == 2 && customBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//                    setCanBottomSheetScroll(true);
//                }
//                preVpInfoPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG, "onPageScrollStateChanged: "+state);
            }
        });


        customBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if (preBottomSheetState == BottomSheetBehavior.STATE_HIDDEN)
//                            setPCBangData();
                        Log.i(TAG, "onStateChanged: collapsed");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i(TAG, "onStateChanged: expanded");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        if (preBottomSheetState == BottomSheetBehavior.STATE_EXPANDED)
                            animateSlider(false);
                        Log.i(TAG, "onStateChanged: dragging");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i(TAG, "onStateChanged: hidden");
                        break;
                }

                preBottomSheetState = newState;
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0 && slideOffset < 1) {
                    if (slideOffset > preSlideOffset) {
                        animateSlider(true);
                    }else{
                        animateSlider(false);
                    }


                    preSlideOffset = slideOffset;


                }
            }
        });

    }

    private void animateSlider(boolean isShowUp) {
        if (isShowUp) {
            if (fl_slider.getVisibility() == View.VISIBLE)
                return;

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_slider_appear);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fl_slider.startAnimation(animation);
            fl_slider.setVisibility(View.VISIBLE);
            isAnimating = false;

        } else {
            if (isAnimating)
                return;

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_slider_disappear);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fl_slider.setVisibility(View.GONE);
                    isAnimating =false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fl_slider.startAnimation(animation);

        }
    }


    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: "+searchView.isSearchOpen()+"");
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchView != null && searchView.isSearchOpen()) {
            searchView.hide(true);
        } else if (ll_pcBang_info != null && customBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            customBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {

            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime && backPressedTime > 0) {
                super.onBackPressed();
            } else {
                backPressedTime = tempTime;
                Toast.makeText(getApplicationContext(), "'뒤로'버튼을한번더누르시면종료됩니다.", Toast.LENGTH_SHORT).show();
            }
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
                searchPlace(query);
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
                searchPlace(text.toString());
            }
        });
        mSuggestionsList.clear();
        mSuggestionsList.addAll(mHistoryDatabase.getAllItems());
        searchView.setAdapter(mSearchAdapter);

        CardView cardView = searchView.getCardView();
        FrameLayout.LayoutParams cvParams = (FrameLayout.LayoutParams) cardView.getLayoutParams();
        cvParams.topMargin += getStatusBarHeight();
        cardView.setLayoutParams(cvParams);

        searchView.hide(false);

    }

    private void searchPlace(String query) {
        float lat_search = 0;
        float lon_search = 0;
        Dong dong = null;
        Subway subway = null;
        Si si = null;
        int count = 0;
        while (count < 5) {
            switch (count) {
                case 0:
                    dong = realm.where(Dong.class).equalTo("name", query).findFirst();
                    break;
                case 1:
                    dong = realm.where(Dong.class).equalTo("name", query + "동").findFirst();
                    break;
                case 2:
                    dong = realm.where(Dong.class).equalTo("name", query + "읍").findFirst();
                    break;
                case 3:
                    dong = realm.where(Dong.class).equalTo("name", query + "면").findFirst();
                    break;
                case 4:
                    dong = realm.where(Dong.class).equalTo("name", query + "리").findFirst();
            }

            if (dong != null) {
                lat_search = dong.getLatitude();
                lon_search = dong.getLongitude();
                break;
            }

            count++;

        }

        if (dong == null) {
            count = 0;
            while (count < 2) {
                switch (count) {
                    case 0:
                        subway = realm.where(Subway.class).equalTo("name", query).findFirst();
                        break;
                    case 1:
                        subway = realm.where(Subway.class).equalTo("name", query + "역").findFirst();
                        break;

                }

                if (subway != null) {
                    lat_search = subway.getLatitude();
                    lon_search = subway.getLongitude();
                    break;
                }

                count++;
            }
        }


        if (dong == null && subway == null) {
            count = 0;
            while (count < 3) {
                switch (count) {
                    case 0:
                        si = realm.where(Si.class).equalTo("name", query).findFirst();
                        break;
                    case 1:
                        si = realm.where(Si.class).equalTo("name", query + "구").findFirst();
                        break;
                    case 2:
                        si = realm.where(Si.class).equalTo("name", query + "시").findFirst();
                        break;
                }

                if (si != null) {
                    lat_search = si.getLatitude();
                    lon_search = si.getLongitude();
                    break;
                }

                count++;
            }
        }

        if (lat_search == 0 || lon_search == 0) {
            Toast.makeText(getApplicationContext(), "\"" + query + "\"" + " 로 검색된 결과가 없습니다.", Toast.LENGTH_SHORT).show();

        } else {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat_search, lon_search)).zoom(ZOOM).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 300, null);
            searchView.hide(true);
        }
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

    @Override
    protected void onStart() {
        googleApiClient.connect();
        initCategory();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop: called");
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: ");
        if (requestCode == SearchView.SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && results.size() > 0) {
                String searchWrd = results.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd);
                }
            }
        }

        if (requestCode == PCReviewFragment.WRITE_REVIEW_CODE && resultCode == RESULT_OK) {

        }

        if (requestCode == SPLASH_REQUEST_CODE && resultCode == RESULT_OK) {

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void update() {

        Sync sync = realm.where(Sync.class).equalTo("id", 1).findFirst();
        int period = sync.getPeriod();
        long timeDifference = System.currentTimeMillis() - sync.getLastRequsetTime();

        Log.i(TAG, "update: "+sync.getUpdated());

        if (timeDifference < period * 360000) {
            Log.i(TAG, "update: 아직 업데이트할만큼 시간이 안됐음 - " + timeDifference);
            return;
        }


        PCBangListenerInterface.OnPostFinishListener listener = new PCBangListenerInterface.OnPostFinishListener() {
            @Override
            public void onPostSuccess(String responseString) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissUpdateDialog();
                    }
                });
                Log.i(TAG, "onPostSuccess");
                JSONToRealm jsonToRealm = new JSONToRealm(MainMapActivity.this);
                String status = jsonToRealm.updateResultToRealm(responseString);
                if (status.equals("success")) {

                } else if (status.equals("empty")) {

                } else if (status.equals("fail")) {

                }
            }

            @Override
            public void onPostFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissUpdateDialog();
                    }
                });
                Log.i(TAG, "onPostFailure");
//            Todo: network fail 처리
            }
        };


        RequestBody formBody = new FormBody.Builder()
                .add("last_updated", sync.getUpdated())
                .build();

        PCBangHttpHelper pcBangHttpHelper = new PCBangHttpHelper();
        pcBangHttpHelper.post(formBody, getResources().getString(R.string.url_pcbang_update), listener);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showUpdateDialog();
            }
        });
    }


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
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
        newLocationParams.topMargin = (int) getResources().getDimension(R.dimen.my_location_top_margin);
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
//                    if (cameraPosition.zoom > 12) {
                    float dist = Util.calDistance(pastLatlng.latitude, pastLatlng.longitude, cameraPosition.target.latitude, cameraPosition.target.longitude);
                    Log.i(TAG, "onCameraChange: dist : " + dist);
                    if (dist > 3) {

                        lat = (float) cameraPosition.target.latitude;
                        lon = (float) cameraPosition.target.longitude;
                        Log.i(TAG, "onCameraChange: start get data");
                        getData();
                        pastLatlng = cameraPosition.target;

//                        }
//                    } else {

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

//        mClusterManager.cluster();

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

            realm.beginTransaction();
            mClusterManager.clearItems();

            RealmQuery<PCBang> query = realm.where(PCBang.class).equalTo("exist", true);
            float lat_small = lat - Constant.LATITUDE_CONSTANT * range;
            float lat_big = lat + Constant.LATITUDE_CONSTANT * range;

            float lon_small = lon - Constant.LONGITUDE_CONSTANT * range;
            float lon_big = lon + Constant.LONGITUDE_CONSTANT * range;

            query.between("latitude", lat_small, lat_big).between("longitude", lon_small, lon_big);

            for (Convenience convenience : Constant.conveniences) {
                if (convenience.isSelected())
                    query.equalTo("convenience.id", convenience.getId());
            }

            RealmResults<PCBang> pcBangs = query.findAll();

            Log.i(TAG, "execute: pcbang count = " + pcBangs.size());

            if (pcBangs != null) {
                for (PCBang pcBang : pcBangs) {
                    PCBangClusterItem item = new PCBangClusterItem();
                    item.setLatLng(new LatLng(pcBang.getLatitude(), pcBang.getLongitude()));
                    item.setId(pcBang.getId());
                    item.setPcBangName(pcBang.getName());
                    item.setAllianceLevel(pcBang.getAllianceLevel());
                    item.setMinPrice(pcBang.getMinPrice());
                    mClusterManager.addItem(item);
                }
            }

            realm.commitTransaction();
            mClusterManager.cluster();
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

                    lat = (float) googleMap.getCameraPosition().target.latitude;
                    lon = (float) googleMap.getCameraPosition().target.longitude;

                    getData();

                }
            });
        }
        if (count == 0)
            tv_condition.setText("검색조건    : ");
        else
            tv_condition.setText("검색조건(" + count + ") : ");

    }


    private void setPCBangData() {
        customBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

//        for (int i = 0 ; i < getSupportFragmentManager().getFragments().size() ; i++) {
//            if (getSupportFragmentManager().getFragments().get(i) instanceof PCBasicFragment) {
//                ((PCBasicFragment) getSupportFragmentManager().getFragments().get(i)).setData();
//            } else if (getSupportFragmentManager().getFragments().get(i) instanceof PCPriceFragment) {
//                ((PCPriceFragment) getSupportFragmentManager().getFragments().get(i)).setData();
//            } else if (getSupportFragmentManager().getFragments().get(i) instanceof PCReviewFragment) {
//                ((PCReviewFragment) getSupportFragmentManager().getFragments().get(i)).setData();
//            }
//        }
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
        Log.i("cluster item", "pcBangId : "+pcBangClusterItem.getId());
        Constant.setPcBangId(pcBangClusterItem.getId());
        deselectMarker();
        selectMarker(pcBangClusterItem);
        setPCBangData();
        vpInfo.setCurrentItem(0);

        return false;
    }

    public int getPCBangId(){
        return selectedPCBangId;
    }

    private void deselectMarker() {

        customBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        if (mSelectedMarker != null) {
            LinearLayout linearLayout = pcBangRenderer.getLinearLayout();
            TextView tv_name = (TextView) linearLayout.findViewById(R.id.tv_name);
            TextView tv_price = (TextView) linearLayout.findViewById(R.id.tv_price);

            IconGenerator mIconGenerator = pcBangRenderer.getmIconGenerator();
            Bitmap icon;

            Util.setMarkerLinearLayout(this, linearLayout, selected_alliance_level);

            switch (selected_alliance_level) {
                case 0:
                    tv_price.setVisibility(View.GONE);
                    linearLayout.setBackgroundResource(R.drawable.bubble_grey_small);
                    tv_price.setTextColor(ContextCompat.getColor(this, R.color.colorDarkAccent));
                    tv_name.setTextColor(ContextCompat.getColor(this, R.color.colorDarkAccent));
                    break;
                case 1:
                    tv_price.setVisibility(View.VISIBLE);
                    linearLayout.setBackgroundResource(R.drawable.bubble_grey);
                    tv_price.setTextColor(ContextCompat.getColor(this, R.color.colorDarkAccent));
                    tv_name.setTextColor(ContextCompat.getColor(this, R.color.colorDarkAccent));

                    break;
                case 2:
                    linearLayout.setBackgroundResource(R.drawable.bubble_primary);
                    tv_price.setTextColor(ContextCompat.getColor(this, R.color.white));
                    tv_name.setTextColor(ContextCompat.getColor(this, R.color.white));
                    tv_price.setVisibility(View.VISIBLE);

                    break;
            }
            try {
                icon = mIconGenerator.makeIcon();
                mSelectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            mSelectedMarker = null;
        }
    }


    private void selectMarker(PCBangClusterItem pcBangClusterItem) {
        customBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        IconGenerator mIconGenerator = pcBangRenderer.getmIconGenerator();
        LinearLayout linearLayout = pcBangRenderer.getLinearLayout();


        TextView tv_name = (TextView) linearLayout.findViewById(R.id.tv_name);
        TextView tv_price = (TextView) linearLayout.findViewById(R.id.tv_price);

        tv_name.setText(pcBangClusterItem.getPcBangName());
        tv_price.setText("1,200");

        Bitmap icon;

        int level = pcBangClusterItem.getAllianceLevel();

        // TODO: 2016. 5. 19. level 을 pcBangClusterItem에서 가져온것을 넣어준다.  
        Random random = new Random();
        int _level = random.nextInt(3);

        selected_alliance_level = _level;
        Util.setMarkerLinearLayout(this, linearLayout, selected_alliance_level);

        switch (selected_alliance_level) {
            case 0:
                linearLayout.setBackgroundResource(R.drawable.bubble_black_small);
                tv_price.setVisibility(View.GONE);
                tv_price.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv_name.setTextColor(ContextCompat.getColor(this, R.color.white));
                break;
            case 1:
                linearLayout.setBackgroundResource(R.drawable.bubble_black);
                tv_price.setVisibility(View.VISIBLE);
                tv_price.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv_name.setTextColor(ContextCompat.getColor(this, R.color.white));

                break;
            case 2:
                linearLayout.setBackgroundResource(R.drawable.bubble_black);
                tv_price.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv_name.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv_price.setVisibility(View.VISIBLE);

                break;
        }

        icon = mIconGenerator.makeIcon();

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

        Location center = new Location("center");
        center.setLatitude(vr.latLngBounds.getCenter().latitude);
        center.setLongitude(vr.latLngBounds.getCenter().longitude);
        Location center_top = new Location("center_top");//(center's latitude,vr.latLngBounds.southwest.longitude)
        center_top.setLatitude(center.getLatitude());
        center_top.setLongitude(vr.latLngBounds.northeast.longitude);

        float dis = Util.calDistance(center.getLatitude(), center.getLongitude(), center_top.getLatitude(), center_top.getLongitude());

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
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public void addFrag(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }



        public InfoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }



        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }




    private void setSlider() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int device_width = displayMetrics.widthPixels;
        int device_height = displayMetrics.heightPixels;

        int ll_pcbang_info_height = (int) getResources().getDimension(R.dimen.ll_pcbang_info_height);

        int height = device_height - ll_pcbang_info_height;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
        slider.setLayoutParams(params);
        


        HashMap<String, String> url_maps = new HashMap<String, String>();
        url_maps.put("Hannibal", "https://s3-ap-northeast-1.amazonaws.com/ypunval4/pcbang/test_images/pcbang_sample3.jpg?X-Amz-Date=20160523T013147Z&X-Amz-Expires=300&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Signature=0490e0f53047b7c918bfc74bd96a29dcef5b6c467daa5d09e7f8f24e8569db49&X-Amz-Credential=ASIAJO2R6Z7SNQJTWPTQ/20160523/ap-northeast-1/s3/aws4_request&X-Amz-SignedHeaders=Host&x-amz-security-token=FQoDYXdzEDsaDCE3IOS8QX0dZRk3YiLHAS5jHW27dhFsZMCnLfEKJZlgYQ23DIgNoZToe9QlTivtS78CyB%2Bw/Cbi5dONH7l/Yd3t7IJXDrnGsddTnxkIeKpDNlQOAxO3g5Ih0UQKoAikPCIfAFhFx/NlXzdn3e1a4Ry%2BP1jfxojK7hs7vMS3dyILsJ7EIeV9iE7wErujsNeZ5cYoWhtuiqcGxXdisRBdKZbFzDKgjd9rhdyy0qaH2tU4gvkhknnsS6M1DtbNc8JdXd70%2BMvMcIMbKRexS9Md0QRoOLiuboQorrqJugU%3D");
        url_maps.put("Big Bang Theory", "https://s3-ap-northeast-1.amazonaws.com/ypunval4/pcbang/test_images/pc2.jpg?X-Amz-Date=20160523T013110Z&X-Amz-Expires=300&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Signature=bdea04f38fea20ee177b44d4f5eef139d35c1dc0e2e2412dbedd631531c25b12&X-Amz-Credential=ASIAJO2R6Z7SNQJTWPTQ/20160523/ap-northeast-1/s3/aws4_request&X-Amz-SignedHeaders=Host&x-amz-security-token=FQoDYXdzEDsaDCE3IOS8QX0dZRk3YiLHAS5jHW27dhFsZMCnLfEKJZlgYQ23DIgNoZToe9QlTivtS78CyB%2Bw/Cbi5dONH7l/Yd3t7IJXDrnGsddTnxkIeKpDNlQOAxO3g5Ih0UQKoAikPCIfAFhFx/NlXzdn3e1a4Ry%2BP1jfxojK7hs7vMS3dyILsJ7EIeV9iE7wErujsNeZ5cYoWhtuiqcGxXdisRBdKZbFzDKgjd9rhdyy0qaH2tU4gvkhknnsS6M1DtbNc8JdXd70%2BMvMcIMbKRexS9Md0QRoOLiuboQorrqJugU%3D");
        url_maps.put("House of Cards", "https://s3-ap-northeast-1.amazonaws.com/ypunval4/pcbang/test_images/pcbang_sample2.jpg?X-Amz-Date=20160523T013135Z&X-Amz-Expires=300&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Signature=c33178b44d0a009c7f20ac512ba6eda10275fb119ad89b246fb9352197ab8043&X-Amz-Credential=ASIAJO2R6Z7SNQJTWPTQ/20160523/ap-northeast-1/s3/aws4_request&X-Amz-SignedHeaders=Host&x-amz-security-token=FQoDYXdzEDsaDCE3IOS8QX0dZRk3YiLHAS5jHW27dhFsZMCnLfEKJZlgYQ23DIgNoZToe9QlTivtS78CyB%2Bw/Cbi5dONH7l/Yd3t7IJXDrnGsddTnxkIeKpDNlQOAxO3g5Ih0UQKoAikPCIfAFhFx/NlXzdn3e1a4Ry%2BP1jfxojK7hs7vMS3dyILsJ7EIeV9iE7wErujsNeZ5cYoWhtuiqcGxXdisRBdKZbFzDKgjd9rhdyy0qaH2tU4gvkhknnsS6M1DtbNc8JdXd70%2BMvMcIMbKRexS9Md0QRoOLiuboQorrqJugU%3D");
        url_maps.put("Game of Thrones", "https://s3-ap-northeast-1.amazonaws.com/ypunval4/pcbang/test_images/pcbang_sample4.jpg?X-Amz-Date=20160523T013200Z&X-Amz-Expires=300&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Signature=eebbfe264aacf8422096930e2b28eb679f7bdf1c5ef80ade30bb84301b52c0f1&X-Amz-Credential=ASIAJO2R6Z7SNQJTWPTQ/20160523/ap-northeast-1/s3/aws4_request&X-Amz-SignedHeaders=Host&x-amz-security-token=FQoDYXdzEDsaDCE3IOS8QX0dZRk3YiLHAS5jHW27dhFsZMCnLfEKJZlgYQ23DIgNoZToe9QlTivtS78CyB%2Bw/Cbi5dONH7l/Yd3t7IJXDrnGsddTnxkIeKpDNlQOAxO3g5Ih0UQKoAikPCIfAFhFx/NlXzdn3e1a4Ry%2BP1jfxojK7hs7vMS3dyILsJ7EIeV9iE7wErujsNeZ5cYoWhtuiqcGxXdisRBdKZbFzDKgjd9rhdyy0qaH2tU4gvkhknnsS6M1DtbNc8JdXd70%2BMvMcIMbKRexS9Md0QRoOLiuboQorrqJugU%3D");

        for (String name : url_maps.keySet()) {
            SliderPCBangInfoView sliderView = new SliderPCBangInfoView(this);
            sliderView.image(url_maps.get(name)).setScaleType(BaseSliderView.ScaleType.Fit);
            sliderView.bundle(new Bundle());
            sliderView.getBundle().putString("extra", name);

            slider.addSlider(sliderView);
        }

        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setDuration(4000);

    }
}
