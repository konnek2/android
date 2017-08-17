package com.quickblox.sample.chat;

import android.content.Context;
import android.util.Log;

import com.quickblox.sample.chat.db.ChatTableManager;
import com.quickblox.sample.chat.db.DBChatAdapter;
import com.quickblox.sample.chat.db.MessageStatusTable;
import com.quickblox.sample.chat.models.SampleConfigs;
import com.quickblox.sample.chat.utils.Consts;
import com.quickblox.sample.chat.utils.configs.ConfigUtils;
import com.quickblox.sample.core.CoreApp;
import com.quickblox.sample.core.utils.ActivityLifecycle;

import java.io.IOException;

public class App extends com.quickblox.sample.groupchatwebrtc.App {
    private static final String TAG = App.class.getSimpleName();
    private static SampleConfigs sampleConfigs;

    public static DBChatAdapter dbChatAdapter;
    public static MessageStatusTable messageStatusTableDAO;
    public static ChatTableManager tableManagerDAO;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        ActivityLifecycle.init(this);
        initSampleConfigs();
        createTable();
        tableManagerDAO = new ChatTableManager(dbChatAdapter.getDataBase(), context);
        tableManagerDAO.databaseDB(context);
    }

    private void initSampleConfigs() {
        try {
            sampleConfigs = ConfigUtils.getSampleConfigs(Consts.SAMPLE_CONFIG_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SampleConfigs getSampleConfigs() {
        return sampleConfigs;
    }

    private void createTable() {

        dbChatAdapter = DBChatAdapter.getInstance(context);
        dbChatAdapter.open();
        messageStatusTableDAO = new MessageStatusTable(dbChatAdapter.getDataBase(), context);

    }


}