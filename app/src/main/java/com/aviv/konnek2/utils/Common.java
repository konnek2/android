package com.aviv.konnek2.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.aviv.konnek2.Konnnek2;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

/**
 * Created by Lenovo on 22-06-2017.
 */

public class Common {

private  Context context;
    private static Activity activity ;
    private static final String TAG = Common.class.getSimpleName();

    public  Common(Context context) {
        this.context = context;

    }
    public static void displayToast(String str)
    {
        Toast.makeText(Konnnek2.getAppContext(),str,Toast.LENGTH_SHORT);
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


//    public   static void SnackBarShow(Context con) {
//
//        activity = (Activity) con.getApplicationContext();
//        SnackbarManager.show(
//                Snackbar.with(con) // context
//                        .text("No InterNet Connection...") // text to display
//                        .actionLabel("Close")
//                        .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)//
//                        .color(Color.BLACK) // change the background color// action button label
//                        .actionListener(new ActionClickListener() {
//                            @Override
//                            public void onActionClicked(Snackbar snackbar) {
//                                Log.d(TAG, "Undoing something");
//
//                            }
//                        }) // action button's ActionClickListener
//                , activity);
//    }
//
//    public static void SnackBarHide() {
//        SnackbarManager.dismiss();
//    }
}
