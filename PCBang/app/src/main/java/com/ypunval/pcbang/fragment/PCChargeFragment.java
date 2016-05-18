package com.ypunval.pcbang.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.model.PCBang;


public class PCChargeFragment extends BaseRealmFragment {

    int pcBangId;
    PCBang pcBang;
    public PCChargeFragment(){

    }

    public static PCChargeFragment newInstance(int pcBangId) {
        PCChargeFragment fragment = new PCChargeFragment();
        Bundle args = new Bundle();
        args.putInt("pcBangId", pcBangId);
        fragment.setArguments(args);
        return fragment;
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
        View view = null;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            view = inflater.inflate(R.layout.fragment_pc_charge, container, false);
        }else{
            view = inflater.inflate(R.layout.fragment_pc_charge_pre_5, container, false);
        }

        return view;
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
