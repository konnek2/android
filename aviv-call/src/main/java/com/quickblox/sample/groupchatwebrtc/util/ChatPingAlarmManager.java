package com.quickblox.sample.groupchatwebrtc.util;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPingManager;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.sample.groupchatwebrtc.utils.Constant;

import org.jivesoftware.smackx.ping.PingFailedListener;

import java.util.concurrent.TimeUnit;

public class ChatPingAlarmManager {

    //Change interval for your behaviour
    private static final long PING_INTERVAL = TimeUnit.SECONDS.toMillis(60);

    private static final String TAG = ChatPingAlarmManager.class.getSimpleName();
    private static final String PING_ALARM_ACTION = Constant.PING_ALARM_ACTION;

    private static final BroadcastReceiver ALARM_BROADCAST_RECEIVER = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (enabled) {

                final QBPingManager pingManager = QBChatService.getInstance().getPingManager();
                if (pingManager != null) {
                    pingManager.pingServer(new QBEntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void result, Bundle params) {
                        }

                        @Override
                        public void onError(QBResponseException responseException) {
                            if (pingFailedListener != null) {
                                pingFailedListener.pingFailed();
                            }
                        }
                    });
                }

            } else {

            }
        }
    };


    private static Context sContext;
    private static PendingIntent sPendingIntent;
    private static AlarmManager sAlarmManager;
    private static boolean enabled = true;
    private static ChatPingAlarmManager instance;
    private static PingFailedListener pingFailedListener;

    public static void setEnabled(boolean enabled) {
        ChatPingAlarmManager.enabled = enabled;
    }

    private ChatPingAlarmManager() {
    }

    public void addPingListener(PingFailedListener pingFailedListener) {
        this.pingFailedListener = pingFailedListener;
    }

    public static synchronized ChatPingAlarmManager getInstanceFor() {
        if (instance == null) {
            instance = new ChatPingAlarmManager();
        }
        return instance;
    }


    public static void onCreate(Context context) {
        sContext = context;
        context.registerReceiver(ALARM_BROADCAST_RECEIVER, new IntentFilter(PING_ALARM_ACTION));
        sAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        sPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(PING_ALARM_ACTION), 0);
        sAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + PING_INTERVAL,
                PING_INTERVAL, sPendingIntent);
    }


    public static void onDestroy() {
        if (sContext != null) {
            sContext.unregisterReceiver(ALARM_BROADCAST_RECEIVER);
        }
        if (sAlarmManager != null) {
            sAlarmManager.cancel(sPendingIntent);
        }
        pingFailedListener = null;
        instance = null;
    }
}
