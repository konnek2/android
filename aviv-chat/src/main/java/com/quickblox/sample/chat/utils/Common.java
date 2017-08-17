package com.quickblox.sample.chat.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.sample.chat.utils.chat.ChatHelper;
import com.quickblox.sample.groupchatwebrtc.App;
import com.quickblox.users.model.QBUser;

/**
 * Created by Lenovo on 29-06-2017.
 */

public class Common {
    private Context context;

    public Common(Context context) {
        this.context = context;
    }

    public static void displayToast(String str) {
        Toast.makeText(App.getInstance(), str, Toast.LENGTH_SHORT);
    }

    public static boolean checkAvailability(Context context) {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                return true;
            } else {
                String reason = "";
                if (netInfo != null) {
                    reason = netInfo.getReason();
                }
//                Log.i("Common", "Network not availale : " + reason);
            }
            NetworkInfo i = cm.getActiveNetworkInfo();
            if (i == null)
                return false;
            if (!i.isConnected())
                return false;
            if (!i.isAvailable())
                return false;
        } catch (Exception e) {
            Log.i("Common", "Error checking network information");
            e.printStackTrace();
        }
        return false;
    }


    public static QBUser getCurrentUser() {
        QBUser userName = ChatHelper.getCurrentUser();
        return userName;
    }
}
