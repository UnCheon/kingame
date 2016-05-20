package com.ypunval.pcbang.fragment;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.realm.Realm;


public class BaseRealmFragment extends BaseFragment {
    public Realm realm;
    SharedPreferences mPref;




    @Override
    public void onStart() {
        realm = Realm.getDefaultInstance();
        mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        super.onStart();



    }

    @Override
    public void onStop() {
        realm.close();
        super.onStop();

    }
}
