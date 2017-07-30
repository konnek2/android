package com.aviv.konnek2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aviv.konnek2.data.database.UsersTable;

/**
 * Created by Lenovo on 27-06-2017.
 */

public class DBAdapter {


    private static final String DATABASE_NAME = "konnek2";
    @SuppressLint("SdCardPath")
    public static final String DB_FULL_PATH = "/data/data/com.aviv.konnek2/databases/" + DATABASE_NAME;
    private static final int DATABASE_CURRENT_VERSION = 5;
    private static final int DATABASE_PREVIOUS_VERSION = 1;

    private static Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    private static DBAdapter mDBAdapter = null;

    public DBAdapter(Context context) {
        Log.d("DBAdapter", "DBAdapter  ");
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }

    public static DBAdapter getInstance(Context context) {
        Log.d("DBAdapter", "getInstance  ");
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

            Log.d("DBAdapter", "onCreate ");
            db.execSQL(UsersTable.TABLE_REG_USERS_CREATE);
            Log.d("DBAdapter", "  TABLE_REG_USERS_CREATE Table Created ");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }


    public SQLiteDatabase getDataBase() throws SQLException {

        Log.d("DBAdapter", "getDataBase ");
        return db;
    }


    public DBAdapter open() throws SQLException {

        Log.d("DBAdapter", "open() ");
        try {
            db = DBHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void close() {
        Log.d("DBAdapter", "close() ");
        try {
            if (DBHelper != null) {
                DBHelper.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
