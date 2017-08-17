package com.aviv.konnek2;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.aviv.konnek2.adapters.DBAdapter;
import com.aviv.konnek2.data.database.TableBackUpManager;
import com.aviv.konnek2.data.database.UsersTable;
import com.aviv.konnek2.data.preference.AppPreference;
import com.aviv.konnek2.utils.AlarmReceiver;
import com.aviv.konnek2.utils.Common;
import com.crashlytics.android.Crashlytics;
import com.quickblox.sample.chat.App;

import io.fabric.sdk.android.Fabric;

import org.jivesoftware.smackx.muc.packet.Destroy;

import java.util.List;

/**
 * Created by Lenovo on 22-06-2017.
 */

public class Konnnek2 extends App {

    private static Context context;
    public AppPreference appPreference;
    private static DBAdapter dbAdapter;
    public static UsersTable usersTableDAO;
    public static TableBackUpManager tableBackUpManagerDAO;
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        context = this;
        Konnnek2.context = getApplicationContext();
        appPreference = new AppPreference(context);
        createTable();
    }


    public static Context getAppContext() {
        return Konnnek2.context;
    }

    private void createTable() {
//        Log.d("T1Application", "createTable");
        dbAdapter = DBAdapter.getInstance(context);
        dbAdapter.open();
        usersTableDAO = new UsersTable(dbAdapter.getDataBase(), context);
        // for Creating TableBackUp
        tableBackUpManagerDAO = new TableBackUpManager(dbAdapter.getDataBase(), context);
        tableBackUpManagerDAO.databaseDB(context);
        initAlaram();
    }


    public void initAlaram() {
        try {
            if (Common.checkAvailability(this)) {
//                Common.SnackBarHide();
                Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
                manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                int interval = 3 * 60 * 60;
                manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), interval, pendingIntent);

            } else {
//                Common.SnackBarShow(this);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }


    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

        public static boolean isAppRunning(final Context context, final String packageName) {
            final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
            if (procInfos != null)
            {
                for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                    if (processInfo.processName.equals(packageName)) {
                        return true;
                    }
                }
            }
            return false;

    }


}