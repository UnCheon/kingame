package com.ypunval.pcbang.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import io.realm.Realm;

public abstract class BaseRealmActivity extends BaseActivity {

    Realm realm;
    SharedPreferences mPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
