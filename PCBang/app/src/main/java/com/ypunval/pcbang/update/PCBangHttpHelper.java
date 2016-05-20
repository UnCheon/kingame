package com.ypunval.pcbang.update;

import android.util.Log;

import com.ypunval.pcbang.listener.PCBangListenerInterface;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by uncheon on 16. 4. 28..
 */
public class PCBangHttpHelper {
    static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    private static final String TAG = "PCBangHttpHelper";

    public String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public void post(RequestBody body, String url, final PCBangListenerInterface.OnPostFinishListener listener) {
        Log.i(TAG, "post : " + body.toString());
        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: " + call.toString());
                e.printStackTrace();
                listener.onPostFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();

                if (!response.isSuccessful()) {
                    Log.i(TAG, "onResponse: fail");

                    throw new IOException("Unexpected code " + response);
                }
                listener.onPostSuccess(responseData);
            }
        });
    }
}
