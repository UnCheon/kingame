package com.ypunval.pcbang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.adapter.ResultRVA;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.Convenience;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.model.Subway;
import com.ypunval.pcbang.util.Constant;
import com.ypunval.pcbang.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ResultActivity extends BaseRealmActivity {

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.tv_theme)
    TextView tv_theme;
    @Bind(R.id.tv_sort)
    TextView tv_sort;

    @Bind(R.id.rv_list)
    RecyclerView rv_list;

    @OnClick({R.id.tv_theme, R.id.tv_sort})
    public void onThemeSortClick(View v) {
        themeSortClick(v);
    }

    private PCBangListenerInterface.OnNearByClickListener listener;
    ResultRVA adapter;
    ArrayList<PCBang> pcBangs;

    RealmResults<Convenience> conveniences;
    ArrayList<Convenience> selected_conveniences;
    String current_sort;
    public static String[] sorts;
    String type;
    int id;
    String searchQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);




        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pcBangs = new ArrayList<>();
        selected_conveniences = new ArrayList<>();
        sorts = new String[]{"가까운 거리순", "낮은 가격순", "별점 높은순", "후기 많은순"};
        current_sort = sorts[0];
        Constant.rangeKm = 3;
        conveniences = realm.where(Convenience.class).findAllSorted("order");

        type = getIntent().getStringExtra("type");
        id = getIntent().getIntExtra("id", 0);

        if (type.equals("search")){
            searchQuery = getIntent().getStringExtra("query");
            this.setTitle("검색결과 - "+searchQuery);
        }else{
            this.setTitle(getIntent().getStringExtra("name") + " - 전체 " + getIntent().getIntExtra("count", 0)+"개 ");
        }


//        type : subway, si, search, theme

        getData();


        listener = new PCBangListenerInterface.OnNearByClickListener() {
            @Override
            public void onNearByHeaderAddConvenience(Convenience convenience) {

            }

            @Override
            public void onNearByHeaderRemoveConvenience(Convenience convenience) {

            }

            @Override
            public void onNearByItemClick(PCBang pcBang) {
                Log.i("NearBy", "click");
                Intent intent = new Intent(ResultActivity.this, PCBangInfoActivity.class);
                intent.putExtra("pcBangId", pcBang.getId());
                ResultActivity.this.startActivity(intent);
            }

            @Override
            public void onNearByFooterClick(){
//                getData();
//                doSort();
//                adapter.notifyDataSetChanged();
//                showSnackBar();
            }
        };

        adapter = new ResultRVA(pcBangs, listener, type);
        doSort();

        Log.i("result", pcBangs.size()+"");

        rv_list.setLayoutManager(new LinearLayoutManager(this));
        rv_list.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(){
        switch (type){
            case "si":
                getSiData();
                break;
            case "subway":
                getSubwayData();
                break;
            case "search":
                getSearchData();
                break;
        }
    }

    private void getSiData(){
        pcBangs.clear();
        RealmQuery<PCBang> query = realm.where(PCBang.class).equalTo("exist", true).equalTo("si.id", id);
        for (Convenience convenience : selected_conveniences) {
            query.equalTo("convenience.id", convenience.getId());
        }

        RealmResults<PCBang> results = query.findAll();
        Log.i("si size", results.size()+"");
        calDistance(results);
    }

    private void getSubwayData(){
        pcBangs.clear();
        Subway subway = realm.where(Subway.class).equalTo("id", id).findFirst();
        float lat =  subway.getLattitude();
        float lon = subway.getLongitude();

        RealmQuery<PCBang> query = realm.where(PCBang.class).equalTo("exsit", true);
        float lat_small = lat - Constant.LATITUDE_CONSTANT * Constant.subwayRangeKm;
        float lat_big = lat + Constant.LATITUDE_CONSTANT * Constant.subwayRangeKm;

        float lon_small = lon - Constant.LONGITUDE_CONSTANT * Constant.subwayRangeKm;
        float lon_big = lon + Constant.LONGITUDE_CONSTANT * Constant.subwayRangeKm;

        query.between("latitude", lat_small, lat_big)
                .between("longitude", lon_small, lon_big);

        for (Convenience convenience : selected_conveniences) {
            query.equalTo("convenience.id", convenience.getId());
        }

        RealmResults<PCBang> results = query.findAll();
        Log.i("subway size", results.size()+"");
        calDistance(results);

    }


    private void getSearchData(){
        pcBangs.clear();

        RealmQuery<PCBang> query = realm.where(PCBang.class).equalTo("exist", true);
        query.contains("doe.name", searchQuery)
                .or().contains("si.name", searchQuery)
                .or().contains("dong.name", searchQuery)
                .or().contains("subways.name", searchQuery);



        for (Convenience convenience : selected_conveniences) {
            query.equalTo("convenience.id", convenience.getId());
        }

        RealmResults<PCBang> results = query.findAll();
        Log.i("search size", results.size()+"");
        calDistance(results);

    }

    private void showSnackBar(){
        String snackBarMessage = "검색된 PC방 개수 : " + pcBangs.size() + "개" + "  (" + (Constant.rangeKm)+"km)";
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackBarMessage, Snackbar.LENGTH_LONG);
        snackbar.show();
    }



    private void calDistance(RealmResults<PCBang> results){
        float lat = mPref.getFloat("latitude", 0);
        float lon = mPref.getFloat("longitude", 0);

        for (int i = 0; i < results.size(); i++) {
            float dist = Util.calDistance(lat, lon, results.get(i).getLatitude(), results.get(i).getLongitude());
            PCBang pcBang = results.get(i);
            pcBang.setDistance(dist);
            pcBangs.add(pcBang);
        }
    }



    private void doTheme(){

        int theme_count = selected_conveniences.size();
        String st_theme = "";
        if (theme_count == 0) {
            st_theme = "테마 선택하기";
        } else {
            st_theme = "선택된 테마 : " + theme_count + "개";
        }

        tv_theme.setText(st_theme);
        getData();
        doSort();
        adapter.notifyDataSetChanged();
        rv_list.smoothScrollToPosition(0);
        showSnackBar();

    }

    private void doSort() {

        tv_sort.setText(current_sort);
        switch (current_sort) {
            case "가까운 거리순":
                Collections.sort(pcBangs, new Comparator<PCBang>() {
                    @Override
                    public int compare(PCBang obj1, PCBang obj2) {
                        return (obj1.getDistance() < obj2.getDistance()) ? -1 : (obj1.getDistance() > obj2.getDistance()) ? 1 : 0;
                    }
                });
                break;
            case "낮은 가격순":
                Collections.sort(pcBangs, new Comparator<PCBang>() {
                    @Override
                    public int compare(PCBang obj1, PCBang obj2) {
                        return (obj1.getMinPrice() < obj2.getMinPrice()) ? -1 : (obj1.getMinPrice() > obj2.getMinPrice()) ? 1 : 0;
                    }
                });
                break;
            case "높은 별점순":
                Collections.sort(pcBangs, new Comparator<PCBang>() {
                    @Override
                    public int compare(PCBang obj1, PCBang obj2) {
                        return (obj1.getAverageRate() > obj2.getAverageRate()) ? -1 : (obj1.getAverageRate() < obj2.getAverageRate()) ? 1 : 0;
                    }
                });
                break;
            case "후기 많은순":
                Collections.sort(pcBangs, new Comparator<PCBang>() {
                    @Override
                    public int compare(PCBang obj1, PCBang obj2) {
                        return (obj1.getReviewCount() > obj2.getReviewCount()) ? -1 : (obj1.getReviewCount() < obj2.getReviewCount()) ? 1 : 0;
                    }
                });
                break;
        }

    }


    private void themeSortClick(View v) {
        Dialog.Builder builder = null;
        switch (v.getId()) {
            case R.id.tv_theme:
                builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        super.onPositiveActionClicked(fragment);
                        int[] values = getSelectedIndexes();
                        selected_conveniences.clear();
                        if (values != null) {
                            for (int i = 0; i < values.length; i++) {
                                selected_conveniences.add(conveniences.get(values[i]));
                            }
                        }
                        doTheme();
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        Toast.makeText(ResultActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        super.onNegativeActionClicked(fragment);
                    }
                };

                String[] theme_list = new String[conveniences.size()];
                for (int i = 0 ; i < conveniences.size(); i++){
                    theme_list[i] = conveniences.get(i).getName();
                }

                int[] selected_list = new int[selected_conveniences.size()];
                for (int i = 0 ; i < selected_conveniences.size(); i++){

//                    order -1 => because order start 1
                    selected_list[i] = (selected_conveniences.get(i).getOrder()-1);
                }

                ((SimpleDialog.Builder) builder).multiChoiceItems(theme_list, selected_list)
                        .title("테마선택")
                        .positiveAction("확인")
                        .negativeAction("취소");
                break;
            case R.id.tv_sort:
                builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        super.onPositiveActionClicked(fragment);
                        Toast.makeText(ResultActivity.this, "You have selected " + getSelectedValue() + " as phone ringtone.", Toast.LENGTH_SHORT).show();
                        current_sort = getSelectedValue().toString();
                        doSort();
                        adapter.notifyDataSetChanged();
                        rv_list.smoothScrollToPosition(0);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        Toast.makeText(ResultActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        super.onNegativeActionClicked(fragment);
                    }
                };

                int selected = 0;

                switch (current_sort) {
                    case "가까운 거리순":
                        selected = 0;
                        break;
                    case "낮은 가격순":
                        selected = 1;
                        break;
                    case "별점 높은순":
                        selected = 2;
                        break;
                    case "후기 많은순":
                        selected = 3;
                }

                ((SimpleDialog.Builder) builder).items(sorts, selected)
                        .title("정렬기준")
                        .positiveAction("확인")
                        .negativeAction("취소");
                break;
        }

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), null);

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
