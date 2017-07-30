package com.aviv.konnek2.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.aviv.konnek2.ui.activity.SignInActivity;

/**
 * Created by Lenovo on 10-07-2017.
 */

public class IncomingSms {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Log.d("APPLOIGNOTP ", " IncomingSms");
//        final Bundle bundle = intent.getExtras();
//        try {
//            if (bundle != null) {
//                Log.d("APPLOIGNOTP ", " IncomingSms");
//                final Object[] pdusObj = (Object[]) bundle.get("pdus");
//                for (int i = 0; i < pdusObj.length; i++) {
//                    Log.d("APPLOIGNOTP ", " IncomingSms");
//                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
//                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
//                    String senderNum = phoneNumber;
//                    String message = currentMessage.getDisplayMessageBody();
//                    try {
//                        Log.d("APPLOIGNOTP ", " IncomingSms try===>");
//                        if (senderNum.contains("RM-WAYSMS"))
//                        {
//                           String Otp = message.replaceAll("\\D+","");
//                            Log.d("APPLOIGNOTP ", " IncomingSms  senderNum .equals Otp "+Otp);
//                            SignInActivity signInActivity = new SignInActivity();
//                            signInActivity.readSms(Otp);
//
//                        }
//                    } catch (Exception e) {
//                    }
//
//                }
//            }
//
//        } catch (Exception e) {
//
//        }
//    }
}
