package com.quickblox.sample.groupchatwebrtc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.quickblox.sample.groupchatwebrtc.utils.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Lenovo on 28-06-2017.
 */

public class CallTableManager {

    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public CallTableManager(SQLiteDatabase sqLiteDatabase, Context context) {
        this.context = context;
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public void databaseDB(Context context) {
        try {


            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = Constant.CALL_DB_CURRENT_PATH;
                String backupDBPath = Constant.CALL_DB_BACKUP_PATH;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
