package com.aviv.konnek2.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aviv.konnek2.data.services.SignInQbService;

/**
 * Created by Lenovo on 27-06-2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Common.checkAvailability(context)) {
//
            context.startService(new Intent(context, SignInQbService.class));
        } else {
//
        }
    }
}
