package com.ypunval.pcbang.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.activity.PCBangInfoActivity;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.util.Constant;
import com.ypunval.pcbang.util.SliderPCBangAdvertiseView;
import com.ypunval.pcbang.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class HomeFragment extends BaseRealmFragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{
    @Bind(R.id.slider)
    SliderLayout slider;
    @Bind(R.id.custom_indicator)
    PagerIndicator indicator;
    @Bind(R.id.tv_address)
    TextView tv_address;
    @Bind(R.id.tv_desc)
    TextView tv_desc;



    ArrayList<PCBang> pcBangs = new ArrayList<>();

    private static final String TAG = "HomeFragment";


    @OnClick(R.id.ib_refresh_location)
    public void refreshLocation() {}


    public HomeFragment() {}

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        setData();
        setSlider();
    }

    @Override
    public void onStop() {
        super.onStop();
        slider.stopAutoCycle();
    }

    private void setSlider(){

        pcBangs.clear();
        RealmResults<PCBang> results = realm.where(PCBang.class).equalTo("exist", true).equalTo("allianceLevel", 2).findAll();

        for (int i = 0; i < results.size(); i++) {
            float dist = Util.calDistance(mPref.getFloat("latitude", 0), mPref.getFloat("longitude", 0), results.get(i).getLatitude(), results.get(i).getLongitude());
            PCBang pcBang = results.get(i);
            pcBangs.add(i, pcBang);
            pcBang.setDistance(dist);
            Collections.sort(pcBangs, new Comparator<PCBang>() {
                @Override
                public int compare(PCBang obj1, PCBang obj2) {
                    return (obj1.getDistance() < obj2.getDistance()) ? -1 : (obj1.getDistance() > obj2.getDistance()) ? 1 : 0;
                }
            });

        }

        slider.removeAllSliders();


        for (int i = 0 ; i < pcBangs.size() ; i++){
            if (i == 5)
                break;

            PCBang pcBang = pcBangs.get(i);
            SliderPCBangAdvertiseView textSliderView = new SliderPCBangAdvertiseView(getContext(), pcBang);
            // initialize a SliderLayout
            textSliderView
                    .image(pcBang.getImageMain())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", pcBang.getName());
            textSliderView.getBundle().putInt("pcBangId", pcBang.getId());

            slider.addSlider(textSliderView);
        }


        slider.setPresetTransformer(SliderLayout.Transformer.Tablet);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setDuration(4000);
        slider.addOnPageChangeListener(this);
    }


    private void setData(){
        float latitude = mPref.getFloat("latitude", 0);
        float longitude = mPref.getFloat("longitude", 0);

        int count = 0;

        Log.i("HOMEFRAGMENT", latitude + "," +longitude);

        if (latitude != 0 && longitude != 0) {

            RealmQuery<PCBang> query = realm.where(PCBang.class).equalTo("exist", true);
            float latitude_range = Constant.LATITUDE_CONSTANT * Constant.DEFAULT_KILOMETER;
            float longitude_range = Constant.LONGITUDE_CONSTANT * Constant.DEFAULT_KILOMETER;
            query.between("latitude", latitude - latitude_range, latitude + latitude_range)
                    .between("longitude", longitude - longitude_range, longitude + longitude_range);
            RealmResults<PCBang> results = query.findAll();
            count = results.size();
        }

        tv_address.setText(mPref.getString("address", "위치정보가 없습니다."));
        tv_desc.setText(count + "");
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Intent intent = new Intent(getContext(), PCBangInfoActivity.class);
        int id = slider.getBundle().getInt("pcBangId");
        intent.putExtra("pcBangId", id);
        getContext().startActivity(intent);
    }
}
