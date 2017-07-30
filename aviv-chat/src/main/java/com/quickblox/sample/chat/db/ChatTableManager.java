package com.quickblox.sample.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Lenovo on 30-06-2017.
 */

public class ChatTableManager {

    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public ChatTableManager(SQLiteDatabase sqLiteDatabase, Context context) {
        this.context = context;
        this.sqLiteDatabase = sqLiteDatabase;
    }


    public void databaseDB(Context context) {
        try {

            Log.d("TableManager", "databaseDB inside");
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "com.aviv.konnek2" + "//databases//" + "konnek2";
                String backupDBPath = "konnek2Chat" + ".backup";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Log.d("TableManager", "databaseDB inside if");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TableManager", "databaseDB inside Exception" + e);
        }
    }
}
