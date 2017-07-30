package com.quickblox.sample.groupchatwebrtc.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Lenovo on 28-06-2017.
 */

public class DBAdapter {

    private static final String TAG = DBAdapter.class.getSimpleName();

    private static final String DATABASE_NAME = "calllog";
    @SuppressLint("SdCardPath")
    public static final String DB_FULL_PATH = "/data/data/com.aviv.konnek2/databases/" + DATABASE_NAME;
    private static final int DATABASE_CURRENT_VERSION = 1;
    private static final int DATABASE_PREVIOUS_VERSION = 0;

    private static Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    private static DBAdapter mDBAdapter = null;


    public DBAdapter(Context context) {

        Log.d(TAG, "DBAdapter");
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }

    public static DBAdapter getInstance(Context context) {


        Log.d(TAG, "getInstance");
        if (mDBAdapter == null) {
            mDBAdapter = new DBAdapter(context);
        }
        return mDBAdapter;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_CURRENT_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("CALLDBAdapter","  DatabaseHelper  onCreate");
            db.execSQL(CallLogTable.TABLE_CALL_LOG_CREATE);
            Log.d("CALLDBAdapter","  TABLE_CALL_LOG_CREATE  Table Created");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public SQLiteDatabase getDataBase() throws SQLException {
        Log.d(TAG, "getDataBase");
        return db;
    }


    public DBAdapter open() throws SQLException {
        Log.d(TAG, "open()");
        try {
            db = DBHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void close() {
        Log.d(TAG, "close() ");
        try {
            if (DBHelper != null) {
                DBHelper.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
