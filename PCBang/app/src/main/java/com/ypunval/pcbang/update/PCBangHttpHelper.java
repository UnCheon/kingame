package com.ypunval.pcbang.update;

import android.util.Log;

import com.ypunval.pcbang.listener.PCBangListenerInterface;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by uncheon on 16. 4. 28..
 */
public class PCBangHttpHelper {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static OkHttpClient client = new OkHttpClient();
    public static final String UPDATE_URL = "https://xxky578lv7.execute-api.ap-northeast-1.amazonaws.com/prod/pcbangUpdate";
    public static final String REGISTER_URL = "https://xxky578lv7.execute-api.ap-northeast-1.amazonaws.com/prod/pcbangRegister";
    public static final String NICKNAME_URL = "https://xxky578lv7.execute-api.ap-northeast-1.amazonaws.com/prod/pcbangRegister";

    private static final String TAG = "PCBangHttpHelper";


    public String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    public void register(JSONObject json, final PCBangListenerInterface.OnFinishRegisterListener listener) {
        Log.i(TAG, "register : " + json.toString());
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder().url(REGISTER_URL).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailureNetwork();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    listener.onFailureNetwork();
                    throw new IOException("Unexpected code " + response);
                }
                final String responseData = response.body().string();
                Log.i(TAG, "register response : " + responseData);
                listener.onSuccessRegister(responseData);

            }
        });
    }


    public void update_or_writeReview(JSONObject json, final PCBangListenerInterface.OnUpdateListener listener) {
        Log.i(TAG, "update_or_write : " + json.toString());
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder().url(UPDATE_URL).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailureNetwork();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    listener.onFailureNetwork();
                    throw new IOException("Unexpected code " + response);
                }

                final String responseData = response.body().string();
                listener.onSuccessUpdate(responseData);
            }
        });
    }

    public void nickname(JSONObject json, final PCBangListenerInterface.OnNicknameListener listener){
        Log.i(TAG, "registerNickname: " + json.toString());

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder().url(NICKNAME_URL).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailureNetwork();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    listener.onFailureNetwork();
                    throw new IOException("Unexpected code " + response);
                }

                final String responseData = response.body().string();
                listener.onSuccessNickname(responseData);
            }
        });

    }
}
