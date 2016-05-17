package com.ypunval.pcbang.update;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.activity.BaseActivity;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.Sync;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

/**
 * Created by uncheon on 16. 5. 3..
 */
public class UpdateHelper {
    Context context;
    private static final String TAG = "UpdateHelper";

    public UpdateHelper(Context context) {
        this.context = context;
    }

    public void register_update() {
        if (isRegistered()) {
            if (isUpdated()) {
                return;
            } else {
                update();
            }

        } else {
            register();
        }
    }

    public boolean isRegistered() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean("isRegister", false);
    }


    public boolean isUpdated() {
        boolean isUpdated = true;
        Realm realm = Realm.getDefaultInstance();
        Sync sync = realm.where(Sync.class).equalTo("id", 1).findFirst();

        Log.i(TAG, "sync : " + sync.getUpdated() + sync.getId());
        int period = sync.getPeriod();
        long timeDifference = System.currentTimeMillis() - sync.getLastRequsetTime();
        Log.i(TAG, "time difference : " + timeDifference);

        if (timeDifference > period * 360000)
            isUpdated = false;

        realm.close();
        return isUpdated;
    }

    public void register() {
        Log.i(TAG, "register");


        PCBangListenerInterface.OnFinishRegisterListener listener = new PCBangListenerInterface.OnFinishRegisterListener() {
            @Override
            public void onSuccessRegister(String responseString) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity)context).dismissUpdateDialog();
                    }
                });
                Log.i(TAG, "onSuccessRegister");
                JSONToRealm jsonToRealm = new JSONToRealm(context);
                String status = jsonToRealm.registerResultToRealm(responseString);
                if (status.equals("success")) {
                    SharedPreferences pref = context.getSharedPreferences("myInfo", context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isRegister", true);
                    update();
                } else{
//                    Todo: 회원가입 fail 처리
                    Log.i(TAG, "onSuccessRegister: fail");
                }
            }

            @Override
            public void onFailureNetwork() {
//                    Todo: 네트워크 fail 처리
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity)context).dismissUpdateDialog();
                    }
                });
                Log.i(TAG, "onFailureNetwork");
            }
        };

        SharedPreferences pref = context.getSharedPreferences("myInfo", context.MODE_PRIVATE);
        String phoneNumber = pref.getString("phoneNumber", "");
        String regId = pref.getString("regId", "");

        Log.i(TAG, "phoneNumber : " + phoneNumber);
        Log.i(TAG, "regId : " + regId);

        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject obj = new JSONObject();
            obj.put("phoneNumber", phoneNumber);
            obj.put("deviceToken", regId);
            obj.put("deviceType", "android");
            obj.put("password", "");
            jsonObject.put("data", obj);
            jsonObject.put("label", context.getResources().getText(R.string.http_label_register));

        } catch (JSONException e) {
            e.printStackTrace();
//            Todo : json fail 처리
        }

        ((BaseActivity)context).showUpdateDialog();
        PCBangHttpHelper pcBangHttpHelper = new PCBangHttpHelper();
        pcBangHttpHelper.register(jsonObject, listener);
    }

    public void update() {
        PCBangListenerInterface.OnUpdateListener listener = new PCBangListenerInterface.OnUpdateListener() {
            @Override
            public void onSuccessUpdate(String responseString) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity)context).dismissUpdateDialog();
                    }
                });
                Log.i(TAG, "onSuccessUpdate");
                JSONToRealm jsonToRealm = new JSONToRealm(context);
                String status = jsonToRealm.updateResultToRealm(responseString);
                if (status.equals("success")){

                }else if (status.equals("empty")){

                }else if (status.equals("fail")){

                }

            }

            @Override
            public void onFailureNetwork() {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity)context).dismissUpdateDialog();
                    }
                });
                Log.i(TAG, "onFailureNetwork");
//            Todo: network fail 처리
            }
        };

        JSONObject jsonObject = new JSONObject();

        try {
            Realm realm = Realm.getDefaultInstance();
            Sync sync = realm.where(Sync.class).equalTo("id", 1).findFirst();
            jsonObject.put("last_updated", sync.getUpdated());
        } catch (JSONException e) {
//            Todo: json fail 처리
            e.printStackTrace();
        }

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((BaseActivity)context).showUpdateDialog();
            }
        });

        PCBangHttpHelper pcBangHttpHelper = new PCBangHttpHelper();
        pcBangHttpHelper.update_or_writeReview(jsonObject, listener);
    }


    private void writeReview(JSONObject reviewObj) {
        PCBangListenerInterface.OnUpdateListener listener = new PCBangListenerInterface.OnUpdateListener() {
            @Override
            public void onSuccessUpdate(String responseString) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity)context).dismissReviewDialog();
                    }
                });

                JSONToRealm jsonToRealm = new JSONToRealm(context);
                String status = jsonToRealm.updateResultToRealm(responseString);

            }

            @Override
            public void onFailureNetwork() {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity)context).dismissReviewDialog();
                    }
                });
                Log.i(TAG, "onFailureNetwork");
            }
        };

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("last_updated", "2016-04-28 09:02:30.972153");
            jsonObject.put("review", reviewObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        PCBangHttpHelper pcBangHttpHelper = new PCBangHttpHelper();
        pcBangHttpHelper.update_or_writeReview(jsonObject, listener);
        ((BaseActivity)context).showReviewDialog();
    }


}
