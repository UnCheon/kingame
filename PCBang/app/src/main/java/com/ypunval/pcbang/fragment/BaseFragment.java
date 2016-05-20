package com.ypunval.pcbang.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.ypunval.pcbang.util.Util;


public class BaseFragment extends Fragment {
    private static final String FLURRY_KEY = "G869MPMXFV6NB74JSSDJ";


    @Override
    public void onResume() {
        super.onResume();
        Util.setGlobalFont(getContext(), getView());
    }

    @Override
    public void onStart() {
        super.onStart();
//        FlurryAgent.onStartSession(getActivity().getApplicationContext(), FLURRY_KEY);
    }

    @Override
    public void onStop() {
        super.onStop();
//        FlurryAgent.onEndSession(getActivity().getApplicationContext());
    }

    public void onFragmentClick(View v){
        Log.i("baseFragment", "click");
    }
}
