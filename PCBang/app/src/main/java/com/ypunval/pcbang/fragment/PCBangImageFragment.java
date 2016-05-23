package com.ypunval.pcbang.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.util.Constant;

/**
 * Created by uncheon on 2016. 3. 2..
 */
public class PCBangImageFragment extends BaseFragment {
    Context context;
    PCBang pcBang;

    public PCBangImageFragment(){ }

    public static PCBangImageFragment newInstance() {
        return new PCBangImageFragment();
    }


    public void setContext(Context context){
        this.context = context;
    }

    public void setImage(int resId){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pcbang_image, container, false);

        pcBang = Constant.clicked_pcBang;
        
        return view;

    }


}
