package com.quickblox.sample.groupchatwebrtc.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.quickblox.sample.groupchatwebrtc.activities.CallActivity;

/**
 * Created by Lenovo on 27-07-2017.
 */

public class WakeLockReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1=new Intent(context, CallActivity.class);
        startWakefulService(context,intent1);
    }
}
