package com.quickblox.sample.groupchatwebrtc.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.sample.groupchatwebrtc.App;
import com.quickblox.users.model.QBUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lenovo on 28-06-2017.
 */

public class Common {
    private  Context context;

    public  Common(Context context) {
        this.context = context;
    }

    public static void displayToast(String str)
    {
        Toast.makeText(App.getInstance(),str,Toast.LENGTH_SHORT);
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

    public static String currentTime() {
        String strlTime = null;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        strlTime =  mdformat.format(calendar.getTime());

        return strlTime;
    }

    public static String currentDate() {
        String strDate = null;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat(" dd/ MM / yyyy");
        strDate =  mdformat.format(calendar.getTime());

        return strDate;
    }

    public static String getDateStr(Date date) {
        String dateStr = null;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            dateStr = sf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static QBUser getCurrentUser() {
        return QBChatService.getInstance().getUser();
    }
}
