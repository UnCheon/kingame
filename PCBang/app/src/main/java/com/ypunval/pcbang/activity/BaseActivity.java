package com.ypunval.pcbang.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String FLURRY_KEY = "G869MPMXFV6NB74JSSDJ";
    private static final String TAG = "BaseActivity";

    private Dialog.Builder builder = null;
    DialogFragment dialogFragment;

    private ProgressDialog pd = null;

    public void showUpdateDialog() {
        pd = ProgressDialog.show(this, "", "동기화 중입니다.", true);

    }

    public void dismissUpdateDialog() {
        if (pd.isShowing()){
            pd.dismiss();
            pd = null;
        }

    }


    public void showReviewDialog() {
        pd = ProgressDialog.show(this, "", "잠시만 기다려주세요.", true);
    }

    public void dismissReviewDialog() {
        if (pd != null){
            if (pd.isShowing()){
                pd.dismiss();
                pd = null;
            }
        }

    }

    public void showNicknameDialog() {

    }




    public void onActivityClick(View v){
        Log.i("baseActivity", "click");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


//    @Override
//    public void setContentView(int resId) {
//        super.setContentView(resId);
//        Util.setGlobalFont(getApplicationContext(), getWindow().getDecorView());
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart(){
        super.onStart();
//        FlurryAgent.onStartSession(this, FLURRY_KEY);
    }

    @Override
    protected void onStop(){
        super.onStop();
//        FlurryAgent.onEndSession(this);
    }
}
