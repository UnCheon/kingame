package com.ypunval.pcbang.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lapism.searchview.adapter.SearchAdapter;
import com.lapism.searchview.adapter.SearchItem;
import com.lapism.searchview.history.SearchHistoryTable;
import com.lapism.searchview.view.SearchCodes;
import com.lapism.searchview.view.SearchView;
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.fragment.CategoryFragment;
import com.ypunval.pcbang.fragment.HomeFragment;
import com.ypunval.pcbang.fragment.MapFragment;
import com.ypunval.pcbang.fragment.NearByFragment;
import com.ypunval.pcbang.model.Convenience;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.util.Constant;
import com.ypunval.pcbang.util.GpsInfo;
import com.ypunval.pcbang.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends BaseRealmActivity {


    private final String TAG = "MAINACTIVITY";
    static final int SPLASH_REQUEST_CODE = 1234;
    static final int WRITE_REVIEW_CODE = 22;
    private final long FINSH_INTERVAL_TIME = 2000;

    private long backPressedTime = 0;

    ArrayList<Drawable> unselected_icons = new ArrayList<>();
    ArrayList<Drawable> selected_icons = new ArrayList<>();
    String[] titles = {"홈", "내 주변", "지역별", "지도"};

    public static RealmResults<Convenience> conveniences;
    public static ArrayList<Convenience> selected_conveniences;
    public static String[] sorts;
    public static String current_sort;




    SharedPreferences mPref;


    @Bind(R.id.tabs)
    TabLayout tabLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar)
    AppBarLayout appBarLayout;
    @Bind(R.id.container)
    ViewPager mViewPager;

    @Bind(R.id.searchView)
    SearchView mSearchView;

    GpsInfo gpsInfo;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_main_map);
        startActivityForResult(new Intent(this, SplashActivity.class), SPLASH_REQUEST_CODE); // 로딩이 끝난후 이동할 Activity
        Log.i(TAG, "onCreate: ??");

        ButterKnife.bind(this);

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void update() {

    }



    private void setOnCreateMethod() {
        Log.i(TAG, "onCreate");

        sorts = new String[]{"가까운 거리순", "낮은 가격순", "별점 높은순", "후기 많은순"};
        current_sort = sorts[0];

        selected_conveniences = new ArrayList<>();
        conveniences = realm.where(Convenience.class).findAllSorted("order");

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        setSupportActionBar(toolbar);

        setView();
        setSearchView();
//        UpdateHelper updateHelper = new UpdateHelper(this);
//        updateHelper.register_update();
//
//        setLocation();
    }


    private void setView() {

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);
        setTabTitlesToIcons();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setTitle(titles[position]);
                for (int i = 0; i < selected_icons.size(); i++) {
                    if (i == position) {
                        //noinspection ConstantConditions
                        tabLayout.getTabAt(i).setIcon(selected_icons.get(i));
                    } else {
                        //noinspection ConstantConditions
                        tabLayout.getTabAt(i).setIcon(unselected_icons.get(i));
                    }
                }

                if (position == 3)
                    appBarLayout.setExpanded(false, true);
                else
                    appBarLayout.setExpanded(true, true);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void getNearPCBang() {

        float latitude = mPref.getFloat("latitude", 0);
        float longitude = mPref.getFloat("longitude", 0);

        Log.i("lat & lon", latitude + "," + longitude);

        if (latitude != 0 && longitude != 0) {

            RealmQuery<PCBang> query = realm.where(PCBang.class).equalTo("exist", true);
            float latitude_range = Constant.LATITUDE_CONSTANT * Constant.DEFAULT_KILOMETER;
            float longitude_range = Constant.LONGITUDE_CONSTANT * Constant.DEFAULT_KILOMETER;
            query.between("latitude", latitude - latitude_range, latitude + latitude_range)
                    .between("longitude", longitude - longitude_range, longitude + longitude_range);
            RealmResults<PCBang> results = query.findAll();

            Constant.near_pcBangs.clear();


            for (int i = 0; i < results.size(); i++) {
                float dist = Util.calDistance(latitude, longitude, results.get(i).getLatitude(), results.get(i).getLongitude());
                PCBang pcBang = results.get(i);
                Constant.near_pcBangs.add(i, pcBang);
                pcBang.setDistance(dist);

            }
            Collections.sort(Constant.near_pcBangs, new Comparator<PCBang>() {
                @Override
                public int compare(PCBang obj1, PCBang obj2) {
                    return (obj1.getDistance() < obj2.getDistance()) ? -1 : (obj1.getDistance() > obj2.getDistance()) ? 1 : 0;
                }
            });

        } else {
            Log.i("location", "0,0");
        }
    }


    public void setLocation() {
        if (gpsInfo == null) {
            gpsInfo = new GpsInfo(this) {
                @Override
                public void onLocationFinish() {
                    setView();
                }
            };


        }
        gpsInfo.getLocation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                showSearchView();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("aa", "aaa");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public void movePage(int i) {
        mViewPager.setCurrentItem(i);
        Log.i("move_page", "clicked");
    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MapFragment.newInstance();
                case 1:
                    return NearByFragment.newInstance();
                case 2:
                    return CategoryFragment.newInstance();
                case 3:
                    return HomeFragment.newInstance();
            }
            return HomeFragment.newInstance();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return null;
        }
    }

    public void setTabTitlesToIcons() {

        setTitle(titles[0]);
        Drawable selected_icon_1 = ContextCompat.getDrawable(this, R.drawable.ic_home_24dp);
        Drawable selected_icon_2 = ContextCompat.getDrawable(this, R.drawable.ic_gps_fixed_24dp);
        Drawable selected_icon_3 = ContextCompat.getDrawable(this, R.drawable.ic_room_24dp);
        Drawable selected_icon_4 = ContextCompat.getDrawable(this, R.drawable.ic_map_24dp);

        Drawable unselected_icon_1 = ContextCompat.getDrawable(this, R.drawable.ic_home_24dp_2);
        Drawable unselected_icon_2 = ContextCompat.getDrawable(this, R.drawable.ic_gps_fixed_24dp_2);
        Drawable unselected_icon_3 = ContextCompat.getDrawable(this, R.drawable.ic_room_24dp_2);
        Drawable unselected_icon_4 = ContextCompat.getDrawable(this, R.drawable.ic_map_24dp_2);

        selected_icons.add(selected_icon_1);
        selected_icons.add(selected_icon_2);
        selected_icons.add(selected_icon_3);
        selected_icons.add(selected_icon_4);

        unselected_icons.add(unselected_icon_1);
        unselected_icons.add(unselected_icon_2);
        unselected_icons.add(unselected_icon_3);
        unselected_icons.add(unselected_icon_4);

        for (int i = 0; i < unselected_icons.size(); i++) {
            if (i == 0) {
                //noinspection ConstantConditions
                tabLayout.getTabAt(i).setIcon(selected_icons.get(i));
            } else {
                //noinspection ConstantConditions
                tabLayout.getTabAt(i).setIcon(unselected_icons.get(i));
            }
        }
    }


    private SearchHistoryTable mHistoryDatabase;
    private List<SearchItem> mSuggestionsList;
    private int mVersion = SearchCodes.VERSION_MENU_ITEM;
    private int mStyle = SearchCodes.STYLE_MENU_ITEM_CLASSIC;
    private int mTheme = SearchCodes.THEME_LIGHT;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchView.SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && results.size() > 0) {
                String searchWrd = results.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    mSearchView.setQuery(searchWrd);
                }
            }
        }

        if (requestCode == SPLASH_REQUEST_CODE && resultCode == RESULT_OK) {
            setOnCreateMethod();

        }

        if (requestCode == WRITE_REVIEW_CODE && resultCode == RESULT_OK) {

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void setSearchView() {

        mHistoryDatabase = new SearchHistoryTable(this);
        mSuggestionsList = new ArrayList<>();

        // SearchView basic attributes  ------------------------------------------------------------
        mSearchView.setVersion(mVersion);
        mSearchView.setStyle(mStyle);
        mSearchView.setTheme(mTheme);
        // -----------------------------------------------------------------------------------------
        mSearchView.setDivider(false);
        mSearchView.setHint(R.string.search_hint);
        mSearchView.setHintSize(getResources().getDimension(R.dimen.search_text_medium));
        mSearchView.setVoice(true);
        mSearchView.setVoiceText("Voice");
        mSearchView.setAnimationDuration(300);

        mSearchView.setShadowColor(ContextCompat.getColor(this, R.color.search_shadow_layout));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.hide(false);
                mHistoryDatabase.addItem(new SearchItem(query));
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("type", "search");
                intent.putExtra("id", 0);
                intent.putExtra("name", "검색결과");
                intent.putExtra("query", query);
                startActivity(intent);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
//                fab.hide();
            }

            @Override
            public void onSearchViewClosed() {
//                fab.show();
            }
        });

        List<SearchItem> mResultsList = new ArrayList<>();
        SearchAdapter mSearchAdapter = new SearchAdapter(this, mResultsList, mSuggestionsList, mTheme);
        mSearchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mSearchView.hide(false);
                TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                CharSequence text = textView.getText();
                mHistoryDatabase.addItem(new SearchItem(text));
                Toast.makeText(getApplicationContext(), text + ", position: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        mSearchView.setAdapter(mSearchAdapter);
    }

    private void showSearchView() {
        mSuggestionsList.clear();
        mSuggestionsList.addAll(mHistoryDatabase.getAllItems());
        mSuggestionsList.add(new SearchItem("Google"));
        mSuggestionsList.add(new SearchItem("Android"));
        mSearchView.show(true);
    }


    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "'뒤로'버튼을한번더누르시면종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

}
