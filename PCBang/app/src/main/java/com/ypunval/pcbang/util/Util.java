package com.ypunval.pcbang.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


}
