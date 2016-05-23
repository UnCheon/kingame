package com.ypunval.pcbang.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.activity.MainMapActivity;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.util.Constant;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;


public class PCPriceFragment extends BaseFragment {
    private static final String TAG = PCPriceFragment.class.getName();

    int pcBangId;
    PCBang pcBang;

    @Bind(R.id.nsv)
    NestedScrollView nsv;

    @Bind(R.id.price_cardview)
    CardView price_cardview;

    @Bind(R.id.price_table)
    TableLayout price_table;
    @Bind(R.id.report_information)
    CardView report_information;

    public PCPriceFragment(){

    }

    public static PCPriceFragment newInstance(int pcBangId) {
        PCPriceFragment fragment = new PCPriceFragment();
        Bundle args = new Bundle();
        args.putInt("pcBangId", pcBangId);
        fragment.setArguments(args);
        return fragment;
    }


    public void selectedPCBang(int pcBangId){
        this.pcBangId = pcBangId;

        Log.i(TAG, "selectedPCBang: clicked");
    }

    public void setData() {
        Realm realm = Realm.getDefaultInstance();
        try{
            PCBang pcBang = realm.where(PCBang.class).equalTo("id", Constant.pcBangId).findFirst();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        realm.close();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            pcBangId = getArguments().getInt("pcBangId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");

        View parent_view = null;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            parent_view = inflater.inflate(R.layout.fragment_pc_price, container, false);
        }else{
            parent_view = inflater.inflate(R.layout.fragment_pc_price_pre_5, container, false);
        }
        ButterKnife.bind(this, parent_view);

        makePriceView(inflater, parent_view);

        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0 && scrollY - oldScrollY < 0) {
                    ((MainMapActivity) getContext()).setCanBottomSheetScroll(true);
                } else {
                    ((MainMapActivity) getContext()).setCanBottomSheetScroll(false);
                }
            }
        });


        report_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = " 정보 제공하기 ";
                String body = "<h1> 정보를 제공해주세요! <h1><br> 가격표에 해당하는 사진을 찍어 첨부해주세요";

//                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//                emailIntent.setType("text/html");
//                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
//                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body));
//                startActivity(Intent.createChooser(emailIntent, "Email:"));

                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("plain/text");
                sendIntent.setData(Uri.parse("ypunval@gmail.com"));
                sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ypunval@gmail.com"});
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "피씨방 가격정보 제보");
                sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
                startActivity(sendIntent);


            }
        });

        
        return parent_view;
    }
    public void makePriceView(LayoutInflater inflater, View parent_view){
        int data_length = 0;
        if (data_length == 0) {
            View price_table_empty = inflater.inflate(R.layout.price_table_empty, null, false);
            View user_submit_button = inflater.inflate(R.layout.user_submit_button, null, false);
            price_table.addView(price_table_empty);
//            price_table.addView(user_submit_button);

        } else {
            for (int i = 0; i < data_length; i++) {
                View price_table_row = inflater.inflate(R.layout.price_table_row, null, false);
                price_table.addView(price_table_row);
            }
        }


    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView: ");
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
