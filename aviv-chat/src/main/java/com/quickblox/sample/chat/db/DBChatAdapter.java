package com.quickblox.sample.chat.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Lenovo on 30-06-2017.
 */

public class DBChatAdapter {

    //    private static final String TAG = DBChatAdapter.class.getSimpleName();
    private static final String TAG = "DBChatAdapter ";
    private static final String DATABASE_NAME = "messageStatus";
    @SuppressLint("SdCardPath")
    public static final String DB_FULL_PATH = "/data/data/com.aviv.konnek2/databases/" + DATABASE_NAME;
    private static final int DATABASE_CURRENT_VERSION = 1;
    private static final int DATABASE_PREVIOUS_VERSION = 0;

    private static Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    private static DBChatAdapter mDBAdapter = null;

    public DBChatAdapter(Context context) {

        Log.d(TAG, "DBChatAdapter");
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }

    public static DBChatAdapter getInstance(Context context) {
        Log.d(TAG, "DBChatAdapter  getInstance");
        if (mDBAdapter == null) {
            mDBAdapter = new DBChatAdapter(context);
        }
        return mDBAdapter;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_CURRENT_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "DBChatAdapter  onCreate");

            db.execSQL(MessageStatusTable.TABLE_MESSAGE__STATUS_CREATE);
            Log.d(TAG, "DBChatAdapter   TABLE_MESSAGE__STATUS_CREATE  Created");
            Log.d("DBChatAdapter", "DBChatAdapter   TABLE_MESSAGE__STATUS_CREATE  Created");


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public SQLiteDatabase getDataBase() throws SQLException {
        Log.d(TAG, "DBChatAdapter  getDataBase");
        return db;
    }


    public DBChatAdapter open() throws SQLException {
        Log.d(TAG, "DBChatAdapter  open()");
        try {
            db = DBHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void close() {
        Log.d(TAG, "DBChatAdapter  close()");
        try {
            if (DBHelper != null) {
                DBHelper.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
