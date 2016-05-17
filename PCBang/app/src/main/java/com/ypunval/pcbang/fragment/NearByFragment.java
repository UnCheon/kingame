package com.ypunval.pcbang.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.activity.MainActivity;
import com.ypunval.pcbang.activity.PCBangInfoActivity;
import com.ypunval.pcbang.adapter.NearRVA;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.Convenience;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.util.Constant;
import com.ypunval.pcbang.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class NearByFragment extends BaseRealmFragment {

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
    NearRVA adapter;
    ArrayList<PCBang> pcBangs;

    public NearByFragment() {
    }

    @SuppressWarnings("unused")
    public static NearByFragment newInstance() {
        return new NearByFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        pcBangs = new ArrayList<>();

        realm = Realm.getDefaultInstance();
        Constant.rangeKm = 3;

        getData();



        listener = new PCBangListenerInterface.OnNearByClickListener() {
            @Override
            public void onNearByHeaderAddConvenience(Convenience convenience){
                Constant.addSelectedConvenience(convenience);
                getData();
                doSort();
                adapter.notifyDataSetChanged();
                rv_list.smoothScrollToPosition(0);
                showSnackBar();
            }

            @Override
            public void onNearByHeaderRemoveConvenience(Convenience convenience){
                Constant.removeSelectedConvenience(convenience);
                getData();
                doSort();
                adapter.notifyDataSetChanged();
                rv_list.smoothScrollToPosition(0);
                showSnackBar();
            }

            @Override
            public void onNearByItemClick(PCBang pcBang) {
                Log.i("NearBy", "click");
                Intent intent = new Intent(getContext(), PCBangInfoActivity.class);
                intent.putExtra("pcBangId", pcBang.getId());
                getContext().startActivity(intent);
            }

            @Override
            public void onNearByFooterClick(){
                getData();
                doSort();
                adapter.notifyDataSetChanged();
                showSnackBar();
            }
        };

        Constant.conveniences.clear();
        Constant.selectedConveniences.clear();
        RealmResults<Convenience> conveniencesResult = realm.where(Convenience.class).findAll();
        for (int i = 0 ; i < conveniencesResult.size() ; i++){
            Constant.conveniences.add(conveniencesResult.get(i));
            Collections.sort(Constant.conveniences, new Comparator<Convenience>() {
                @Override
                public int compare(Convenience obj1, Convenience obj2) {
                    return (obj1.getOrder() < obj2.getOrder()) ? -1 : (obj1.getOrder() > obj2.getOrder()) ? 1 : 0;
                }
            });
        }

        realm.close();


        adapter = new NearRVA(pcBangs, listener);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);
        ButterKnife.bind(this, view);

        doSort();
        rv_list.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_list.setAdapter(adapter);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void getData(){
        float lat = mPref.getFloat("latitude", 0);
        float lon = mPref.getFloat("longitude", 0);
        if (lat !=0 && lon !=0) {
            pcBangs.clear();

            RealmQuery<PCBang> query = realm.where(PCBang.class).equalTo("exist", true);
            float lat_small = lat - Constant.LATITUDE_CONSTANT * Constant.rangeKm;
            float lat_big = lat + Constant.LATITUDE_CONSTANT * Constant.rangeKm;

            float lon_small = lon - Constant.LONGITUDE_CONSTANT * Constant.rangeKm;
            float lon_big = lon + Constant.LONGITUDE_CONSTANT * Constant.rangeKm;

            query.between("latitude", lat_small, lat_big)
                    .between("longitude", lon_small, lon_big);

            for (Convenience convenience : Constant.selectedConveniences) {
                query.equalTo("convenience.id", convenience.getId());
            }

            RealmResults<PCBang> results = query.findAll();

            calDistance(results);
        }
    }


    private void showSnackBar(){
        String snackBarMessage = "검색된 PC방 개수 : " + pcBangs.size() + "개" + "  (" + (Constant.rangeKm)+"km)";
        Snackbar snackbar = Snackbar.make(getView(), snackBarMessage, Snackbar.LENGTH_LONG);
        snackbar.show();
    }



    private void calDistance(RealmResults<PCBang> results){
        float lat = mPref.getFloat("latitude", 0);
        float lon = mPref.getFloat("longitude", 0);

        for (int i = 0; i < results.size(); i++) {
            float dist = Util.calDistance(lat, lon, results.get(i).getLatitude(), results.get(i).getLongitude());
            PCBang pcBang = results.get(i);
            pcBang.setDistance(dist);
            if (dist <= Constant.rangeKm)
                pcBangs.add(pcBang);
        }
    }



    private void doTheme(){

        int theme_count = ((MainActivity) getContext()).selected_conveniences.size();
        String st_theme = "";
        if (theme_count == 0) {
            st_theme = "테마 선택하기";
        } else {
            st_theme = "선택된 테마 : " + theme_count + "개";
        }

        tv_theme.setText(st_theme);

        adapter.notifyDataSetChanged();
        rv_list.smoothScrollToPosition(0);

    }

    private void doSort() {

        tv_sort.setText(((MainActivity) getContext()).current_sort);
        switch (((MainActivity) getContext()).current_sort) {
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
                        ((MainActivity)getActivity()).selected_conveniences.clear();
                        if (values != null) {
                            for (int i = 0; i < values.length; i++) {
                                ((MainActivity) getActivity()).selected_conveniences.add(((MainActivity) getActivity()).conveniences.get(values[i]));
                            }
                        }
                        doTheme();
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                        super.onNegativeActionClicked(fragment);
                    }
                };

                String[] theme_list = new String[((MainActivity)getActivity()).conveniences.size()];
                for (int i = 0 ; i < ((MainActivity)getActivity()).conveniences.size(); i++){
                    theme_list[i] = ((MainActivity)getActivity()).conveniences.get(i).getName();
                }

                int[] selected_list = new int[((MainActivity)getActivity()).selected_conveniences.size()];
                for (int i = 0 ; i < ((MainActivity)getActivity()).selected_conveniences.size(); i++){

//                    order -1 => because order start 1
                    selected_list[i] = (((MainActivity)getActivity()).selected_conveniences.get(i).getOrder()-1);
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
                        Toast.makeText(getContext(), "You have selected " + getSelectedValue() + " as phone ringtone.", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getContext()).current_sort = getSelectedValue().toString();
                        doSort();
                        adapter.notifyDataSetChanged();
                        rv_list.smoothScrollToPosition(0);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                        super.onNegativeActionClicked(fragment);
                    }
                };

                int selected = 0;

                switch (((MainActivity) getContext()).current_sort) {
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

                ((SimpleDialog.Builder) builder).items(((MainActivity) getContext()).sorts, selected)
                        .title("정렬기준")
                        .positiveAction("확인")
                        .negativeAction("취소");
                break;
        }

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }


}
