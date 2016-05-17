package com.ypunval.pcbang.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ypunval.pcbang.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SortActivity extends BaseActivity {

    @Bind(R.id.iv_radio_distance_0)
    ImageView iv_radio_distance_0;
    @Bind(R.id.iv_radio_distance_1)
    ImageView iv_radio_distance_1;
    @Bind(R.id.iv_radio_distance_2)
    ImageView iv_radio_distance_2;
    @Bind(R.id.iv_radio_distance_3)
    ImageView iv_radio_distance_3;

    @Bind(R.id.ll_radio_distance_0)
    LinearLayout ll_radio_distance_0;
    @Bind(R.id.ll_radio_distance_1)
    LinearLayout ll_radio_distance_1;
    @Bind(R.id.ll_radio_distance_2)
    LinearLayout ll_radio_distance_2;
    @Bind(R.id.ll_radio_distance_3)
    LinearLayout ll_radio_distance_3;

    @Bind(R.id.iv_radio_sort_0)
    ImageView iv_radio_sort_0;
    @Bind(R.id.iv_radio_sort_1)
    ImageView iv_radio_sort_1;
    @Bind(R.id.iv_radio_sort_2)
    ImageView iv_radio_sort_2;
    @Bind(R.id.iv_radio_sort_3)
    ImageView iv_radio_sort_3;

    @Bind(R.id.ll_radio_sort_0)
    LinearLayout ll_radio_sort_0;
    @Bind(R.id.ll_radio_sort_1)
    LinearLayout ll_radio_sort_1;
    @Bind(R.id.ll_radio_sort_2)
    LinearLayout ll_radio_sort_2;
    @Bind(R.id.ll_radio_sort_3)
    LinearLayout ll_radio_sort_3;

    @OnClick({R.id.ll_radio_distance_0, R.id.ll_radio_distance_1, R.id.ll_radio_distance_2, R.id.ll_radio_distance_3})
    public void onRadioDistanceClick(View v){
        radioDistanceClick(v);
    }

    @OnClick({R.id.ll_radio_sort_0, R.id.ll_radio_sort_1, R.id.ll_radio_sort_2, R.id.ll_radio_sort_3})
    public void onRadioSortClick(View v){
        radioSortClick(v);
    }


    ArrayList<LinearLayout> al_ll_distance;
    ArrayList<ImageView> al_iv_distance;

    ArrayList<LinearLayout> al_ll_sort;
    ArrayList<ImageView> al_iv_sort;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            setContentView(R.layout.activity_sort);

        }else{
            setContentView(R.layout.activity_sort_pre_5);
        }

        ButterKnife.bind(this);

        initRadio();

    }

    private void initRadio(){
        al_iv_distance = new ArrayList<>();
        al_ll_distance = new ArrayList<>();
        al_iv_sort = new ArrayList<>();
        al_ll_sort = new ArrayList<>();

        al_iv_distance.add(iv_radio_distance_0);
        al_iv_distance.add(iv_radio_distance_1);
        al_iv_distance.add(iv_radio_distance_2);
        al_iv_distance.add(iv_radio_distance_3);

        al_ll_distance.add(ll_radio_distance_0);
        al_ll_distance.add(ll_radio_distance_1);
        al_ll_distance.add(ll_radio_distance_2);
        al_ll_distance.add(ll_radio_distance_3);

        al_iv_sort.add(iv_radio_sort_0);
        al_iv_sort.add(iv_radio_sort_1);
        al_iv_sort.add(iv_radio_sort_2);
        al_iv_sort.add(iv_radio_sort_3);

        al_ll_sort.add(ll_radio_sort_0);
        al_ll_sort.add(ll_radio_sort_1);
        al_ll_sort.add(ll_radio_sort_2);
        al_ll_sort.add(ll_radio_sort_3);
    }

    private void radioDistanceClick(View v){
        for (int i = 0 ; i < al_ll_distance.size() ; i++ ){
            LinearLayout ll_distance = al_ll_distance.get(i);
            ImageView iv_distance = al_iv_distance.get(i);
            if (v.getId() == ll_distance.getId()){
                iv_distance.setImageResource(R.drawable.ic_radio_button_on_24dp);
            }else{
                iv_distance.setImageResource(R.drawable.ic_radio_button_off_24dp);
            }
        }
    }

    private void radioSortClick(View v){
        for (int i = 0 ; i < al_ll_sort.size() ; i++ ){
            LinearLayout ll_distance = al_ll_sort.get(i);
            ImageView iv_distance = al_iv_sort.get(i);
            if (v.getId() == ll_distance.getId()){
                iv_distance.setImageResource(R.drawable.ic_radio_button_on_24dp);
            }else{
                iv_distance.setImageResource(R.drawable.ic_radio_button_off_24dp);
            }
        }
    }
}
