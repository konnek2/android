package com.quickblox.sample.groupchatwebrtc;

import android.content.Context;
import android.util.Log;

import com.quickblox.sample.core.CoreApp;
import com.quickblox.sample.groupchatwebrtc.db.CallLogTable;
import com.quickblox.sample.groupchatwebrtc.db.CallTableManager;
import com.quickblox.sample.groupchatwebrtc.db.DBAdapter;
import com.quickblox.sample.groupchatwebrtc.util.QBResRequestExecutor;

public class App extends CoreApp {

    private static Context context;
    private static DBAdapter dbAdapter;
    public static CallLogTable callLogTableDAO;
    public static CallTableManager callTableManagerDAO;
    private static App instance;
    private QBResRequestExecutor qbResRequestExecutor;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
        context=this;
        createTable();
        callTableManagerDAO = new CallTableManager(dbAdapter.getDataBase(), context);
        callTableManagerDAO.databaseDB(context);
    }

    private void initApplication(){
        instance = this;
    }

    public synchronized QBResRequestExecutor getQbResRequestExecutor() {
        return qbResRequestExecutor == null
                ? qbResRequestExecutor = new QBResRequestExecutor()
                : qbResRequestExecutor;
    }


    private void createTable() {
        Log.d("T1Application", "createTable");
        dbAdapter = DBAdapter.getInstance(context);
        dbAdapter.open();
        callLogTableDAO = new CallLogTable(dbAdapter.getDataBase(), context);

    }
}
