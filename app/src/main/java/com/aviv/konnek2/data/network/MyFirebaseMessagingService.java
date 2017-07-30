package com.aviv.konnek2.data.network;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aviv.konnek2.R;
import com.aviv.konnek2.ui.activity.SignInActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Lenovo on 30-06-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String msg ;
    CharSequence Name = "AvivGlobalTechApp";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        Log.d("APPTOEKN", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        if (remoteMessage.getNotification() != null) {
            Log.d("APPTOEKN", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        msg = remoteMessage.getData().get("message");

//        Log.d("TTTTOOOKEENN", "MyFirebaseMessagingService   msg   " + msg);


        // Create Notification
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1410,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_chat_notification)
                .setContentTitle("SmartChapp")
                .setColor(Color.parseColor("#182681"))
                .setTicker(Name.toString())
                .setSound(alarmSound)
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1410, notificationBuilder.build());
    }
}
