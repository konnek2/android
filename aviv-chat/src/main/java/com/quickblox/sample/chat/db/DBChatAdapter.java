package com.quickblox.sample.chat.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.quickblox.sample.chat.utils.Constant;

/**
 * Created by Lenovo on 30-06-2017.
 */

public class DBChatAdapter {

    //    private static final String TAG = DBChatAdapter.class.getSimpleName();
    private static final String TAG = "DBChatAdapter ";
    private static final String DATABASE_NAME = Constant.DATABASE_NAME;
    @SuppressLint("SdCardPath")
    public static final String DB_FULL_PATH = Constant.DB_FULL_PATH + DATABASE_NAME;
    private static final int DATABASE_CURRENT_VERSION = 1;
    private static final int DATABASE_PREVIOUS_VERSION = 0;

    private static Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    private static DBChatAdapter mDBAdapter = null;

    public DBChatAdapter(Context context) {


        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }

    public static DBChatAdapter getInstance(Context context) {

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

            db.execSQL(MessageStatusTable.TABLE_MESSAGE__STATUS_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public SQLiteDatabase getDataBase() throws SQLException {

        return db;
    }


    public DBChatAdapter open() throws SQLException {

        try {
            db = DBHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void close() {

        try {
            if (DBHelper != null) {
                DBHelper.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
