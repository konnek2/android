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

        Log.d("ALARAMTEST", "AlarmReceiver class  1");
        if (Common.checkAvailability(context)) {
//            Common.SnackBarHide();
            Log.d("ALARAMTEST", "INTERNET CONNECTION ON ");
            context.startService(new Intent(context, SignInQbService.class));
        } else {
            Log.d("ALARAMTEST", "INTERNET CONNECTION OFF ");
//            Common.SnackBarShow(JoyChatApplication.getAppContext());
        }
    }
}
