package com.aviv.konnek2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.aviv.konnek2.adapters.DBAdapter;
import com.aviv.konnek2.data.database.TableBackUpManager;
import com.aviv.konnek2.data.database.UsersTable;
import com.aviv.konnek2.data.preference.AppPreference;
import com.aviv.konnek2.utils.AlarmReceiver;
import com.aviv.konnek2.utils.Common;
import com.quickblox.sample.chat.App;

import org.jivesoftware.smackx.muc.packet.Destroy;

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
                Log.d("ALARAMTEST", "initAlaram() ");
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


}