package com.ypunval.pcbang.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ypunval.pcbang.R;

/**
 * Created by uncheon on 2016. 3. 4..
 */
public class Util {
    private static Typeface typeface = null;
    private static BroadcastReceiver mRegistrationBroadcastReceiver;
    public static void setGlobalFont(Context context, View view) {

        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "NanumGothic.ttf");
        }

        if (view != null) {
            if (view instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) view;
                int len = vg.getChildCount();
                for (int i = 0; i < len; i++) {
                    View v = vg.getChildAt(i);
                    if (v instanceof TextView) {
                        ((TextView) v).setTypeface(typeface);
                    }
                    setGlobalFont(context, v);
                }
            }
        }else {
            Log.i("global font", "this is null");
        }

    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private static double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }
    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private static double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }
    public static float calDistance(double lat1, double lon1, double lat2, double lon2){

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
//        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return (float)dist;
    }


    public static void setMarkerLinearLayout(Context context, LinearLayout linearLayout, int level){
        int marker_width = (int) context.getResources().getDimension(R.dimen.marker_width);
        int marker_height = (int) context.getResources().getDimension(R.dimen.marker_height);

        int marker_padding_left = (int) context.getResources().getDimension(R.dimen.marker_padding_left);
        int marker_padding_top = (int) context.getResources().getDimension(R.dimen.marker_padding_top);
        int marker_padding_right = (int) context.getResources().getDimension(R.dimen.marker_padding_right);
        int marker_padding_bottom = (int) context.getResources().getDimension(R.dimen.marker_padding_bottom);


        int marker_small_width = (int) context.getResources().getDimension(R.dimen.marker_small_width);
        int marker_small_height = (int) context.getResources().getDimension(R.dimen.marker_small_height);

        int marker_small_padding_left = (int) context.getResources().getDimension(R.dimen.marker_small_padding_left);
        int marker_small_padding_top = (int) context.getResources().getDimension(R.dimen.marker_small_padding_top);
        int marker_small_padding_right = (int) context.getResources().getDimension(R.dimen.marker_small_padding_right);
        int marker_small_padding_bottom = (int) context.getResources().getDimension(R.dimen.marker_small_padding_bottom);

        int width = 0;
        int height = 0;
        int paddingLeft = 0;
        int paddingTop = 0;
        int paddingRight = 0;
        int paddingBottom = 0;

        if (level == 0){
            width = marker_small_width;
            height = marker_small_height;
            paddingLeft = marker_small_padding_left;
            paddingTop = marker_small_padding_top;
            paddingRight = marker_small_padding_right;
            paddingBottom = marker_small_padding_bottom;
        }else{
            width = marker_width;
            height = marker_height;
            paddingLeft = marker_padding_left;
            paddingTop = marker_padding_top;
            paddingRight = marker_padding_right;
            paddingBottom = marker_padding_bottom;
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        linearLayout.setLayoutParams(params);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }


}
