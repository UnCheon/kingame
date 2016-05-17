package com.ypunval.pcbang.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.gcm.QuickstartPreferences;
import com.ypunval.pcbang.gcm.RegistrationIntentService;

public class SplashActivity extends AppCompatActivity {

    Bundle extra;

    Intent intent;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "SplashActivity";
    public BroadcastReceiver mRegistrationBroadcastReceiver;
    long startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.i(TAG, "oncreated");
        startTime = System.currentTimeMillis();


        extra = new Bundle();
        intent = new Intent();


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String regId = pref.getString("regId", "");
        String phoneNumber = pref.getString("phoneNumber", "");

        Log.i(TAG, phoneNumber + regId + " ? ? ? ?");

        if (regId == "" || phoneNumber == "") {
            if (phoneNumber == "")
                getPhoneNumber();
            if (regId == "")
                getRegId();
        } else {
            startMainActivity();
        }

    }

    private void startMainActivity() {
        long leftTime = System.currentTimeMillis() - startTime;
        if (leftTime < 2000) {
            Handler hd = new Handler();
            hd.postDelayed(new splashHandler(), leftTime); // 2초 후에 hd Handler 실행
        } else {
            Handler hd = new Handler();
            hd.postDelayed(new splashHandler(), 10); // 2초 후에 hd Handler 실행
        }
    }


    private void getPhoneNumber() {
        String phoneNumber;
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = telManager
                .getLine1Number();
        if (phoneNumber.startsWith("+82")) {
            phoneNumber = phoneNumber.replace("+82", "0");
        }

        SharedPreferences pref = getSharedPreferences("myInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.commit();

    }

    private void getRegId() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "broadcast receiver received0");
                String action = intent.getAction();
                if (action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)) {
                    Log.i(TAG, "broadcast receiver received1");
                    // 액션이 COMPLETE일 경우
                    String token = intent.getStringExtra("token");

                    if (token != "") {
                        SharedPreferences pref = getSharedPreferences("myInfo", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("regId", token);
                        editor.commit();


                        startMainActivity();
                    } else {
//                        error message notification 권한이 없을때를 테스트 해보자

                    }

                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }


    private class splashHandler implements Runnable {
        public void run() {
            intent.putExtras(extra);
            SplashActivity.this.setResult(RESULT_OK, intent);
            SplashActivity.this.finish();
        }
    }
}

