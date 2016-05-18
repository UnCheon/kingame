package com.ypunval.pcbang.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.fragment.PCBasicFragment;
import com.ypunval.pcbang.fragment.PCMapFragment;
import com.ypunval.pcbang.fragment.PCPriceFragment;
import com.ypunval.pcbang.fragment.PCReviewFragment;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.util.SliderPCBangInfoView;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PCBangInfoActivity extends BaseRealmActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Bind(R.id.appbar)
    AppBarLayout appBarLayout;

    @Bind(R.id.slider)
    SliderLayout slider;
    @Bind(R.id.custom_indicator)
    PagerIndicator indicator;

    private ViewPager mViewPager;
    PCBang pcBang;
    int pcBangId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            setContentView(R.layout.activity_pcbang_info);
        } else {
            setContentView(R.layout.activity_pcbang_info_pre_5);
        }

        ButterKnife.bind(this);

        pcBangId = getIntent().getIntExtra("pcBangId", 0);
        pcBang = realm.where(PCBang.class).equalTo("id", pcBangId).findFirst();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.htab_collapse_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3)
                    appBarLayout.setExpanded(false, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setTitle(pcBang.getName());

        setSlider();

    }

    private void setSlider() {

        HashMap<String, String> url_maps = new HashMap<String, String>();
        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        for (String name : url_maps.keySet()) {
            SliderPCBangInfoView sliderView = new SliderPCBangInfoView(this);
            sliderView
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            sliderView.bundle(new Bundle());
            sliderView.getBundle()
                    .putString("extra", name);

            slider.addSlider(sliderView);
        }

        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setDuration(4000);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pcbang_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void movePage(int i) {
        mViewPager.setCurrentItem(i);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
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
