package com.ypunval.pcbang.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.model.PCBang;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PCPriceFramgnt extends BaseRealmFragment {
    private static final String TAG = PCPriceFramgnt.class.getName();

    int pcBangId;
    PCBang pcBang;

    @Bind(R.id.price_cardview)
    CardView price_cardview;

    @Bind(R.id.price_table)
    TableLayout price_table;

    public PCPriceFramgnt(){

    }

    public static PCPriceFramgnt newInstance(int pcBangId) {
        PCPriceFramgnt fragment = new PCPriceFramgnt();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View parent_view = null;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            parent_view = inflater.inflate(R.layout.fragment_pc_price, container, false);
        }else{
            parent_view = inflater.inflate(R.layout.fragment_pc_price_pre_5, container, false);
        }
        ButterKnife.bind(this, parent_view);



        makePriceView(inflater, parent_view);



        return parent_view;
    }
    public void makePriceView(LayoutInflater inflater, View parent_view){

        for(int i = 0; i < 3; i++) {
            View price_table_row = inflater.inflate(R.layout.price_table_row, null, false);
            price_table.addView(price_table_row);
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        pcBang = realm.where(PCBang.class).equalTo("id", pcBangId).findFirst();

    }

    @Override
    public void onStop() {
        super.onStop();
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
