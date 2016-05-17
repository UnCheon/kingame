package com.ypunval.pcbang.gcm;

/**
 * Created by yusongyi on 16. 4. 27..
 */

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     *
     * @param from SenderID 값을 받아온다.
     * @param data Set형태로 GCM으로 받은 데이터 payload이다.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {

        String title = data.getString("title");
        String message = data.getString("message");

        System.out.println(data);
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Message: " + message);

        // GCM으로 받은 메세지를 디바이스에 알려주는 sendNotification()을 호출한다.
        sendNotification(title, message);
    }


    /**
     * 실제 디바에스에 GCM으로부터 받은 메세지를 알려주는 함수이다. 디바이스 Notification Center에 나타난다.
     * @param title
     * @param message
     */
    private void sendNotification(String title, String message) {
        /*
        Intent intent = new Intent(this, GCMActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 Request code , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_ic_googleplayservices)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 ID of notification , notificationBuilder.build());
        */
    }
}