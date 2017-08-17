package com.quickblox.sample.groupchatwebrtc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.quickblox.sample.groupchatwebrtc.model.CallLogModel;

import java.util.ArrayList;

/**
 * Created by Lenovo on 28-06-2017.
 */

public class CallLogTable {


    private static final String TAG = CallLogTable.class.getSimpleName();
    private SQLiteDatabase db;
    private Context context;


    public CallLogTable(SQLiteDatabase mdb, Context context) {
        super();
        this.context = context;
        db = mdb;
    }

    public static final String DB_COLUMN_ID = "id";
    public static final String DB_COLUMN_USER = "call_user";
    public static final String DB_COLUMN_USER_ID = "call_user_id";
    public static final String DB_COLUMN_OPPONENT = "call_opponent";
    public static final String DB_COLUMN_TIME = "time";
    public static final String DB_COLUMN_DATE = "date";
    public static final String DB_COLUMN_CALL_STATUS = "call_status";
    public static final String DB_COLUMN_CALL_PRIORITY = "call_priority";
    public static final String DB_COLUMN_CALL_DURATION = "call_duration";
    public static final String DB_COLUMN_CALL_OPPONENT_STATUS = "call_opponet_status";
    public static final String DB_COLUMN_CALL_TYPE = "call_type";
    public static final String TABLE_CALL_LOG = "call_log";
    static String PRIMARYKEY_REG = DB_COLUMN_ID;

    public static final String TABLE_CALL_LOG_CREATE = " create table IF NOT EXISTS " +
            TABLE_CALL_LOG + " ("
            + DB_COLUMN_ID + " integer primary key autoincrement,"
            + DB_COLUMN_USER + " text,"
            + DB_COLUMN_USER_ID + " text,"
            + DB_COLUMN_OPPONENT + " text,"
            + DB_COLUMN_TIME + " text,"
            + DB_COLUMN_DATE + " text,"
            + DB_COLUMN_CALL_STATUS + " text,"
            + DB_COLUMN_CALL_PRIORITY + " text,"
            + DB_COLUMN_CALL_DURATION + " text,"
            + DB_COLUMN_CALL_OPPONENT_STATUS + " text,"
            + DB_COLUMN_CALL_TYPE + " text"
            + ")";


    public boolean saveCallLog(ArrayList<CallLogModel> callLogModel) {
        boolean isUpdated = false;
        long flag = 0;
        try {

            ContentValues initialValues = gerContentValues(callLogModel);
            Cursor mCursor = db.query(TABLE_CALL_LOG, new String[]
                            {DB_COLUMN_USER,
                                    DB_COLUMN_USER_ID,
                                    DB_COLUMN_OPPONENT,
                                    DB_COLUMN_TIME,
                                    DB_COLUMN_DATE,
                                    DB_COLUMN_CALL_STATUS,
                                    DB_COLUMN_CALL_PRIORITY,
                                    DB_COLUMN_CALL_DURATION,
                                    DB_COLUMN_CALL_OPPONENT_STATUS,
                                    DB_COLUMN_CALL_TYPE},
                    DB_COLUMN_USER + "=?" + " AND " +
                            DB_COLUMN_USER_ID + "=?" + " AND " +
                            DB_COLUMN_OPPONENT + "=?" + " AND " +
                            DB_COLUMN_TIME + "=?" + " AND " +
                            DB_COLUMN_DATE + "=?" + " AND " +
                            DB_COLUMN_CALL_STATUS + "=?" + " AND " +
                            DB_COLUMN_CALL_PRIORITY + "=?" + " AND " +
                            DB_COLUMN_CALL_DURATION + "=?" + " AND " +
                            DB_COLUMN_CALL_OPPONENT_STATUS + "=?" + " AND " +
                            DB_COLUMN_CALL_TYPE + "=?",
                    new String[]{
                            callLogModel.get(0).getCallUserName(),
                            callLogModel.get(0).getUserId(),
                            callLogModel.get(0).getCallOpponentName(),
                            callLogModel.get(0).getCallDate(),
                            callLogModel.get(0).getCallTime(),
                            callLogModel.get(0).getCallStatus(),
                            callLogModel.get(0).getCallPriority(),
                            callLogModel.get(0).getCallType()

                    },
                    null,
                    null,
                    null);

            if (mCursor == null || mCursor.getCount() <= 0) {
                if (mCursor != null) {
                    mCursor.close();
                }
                flag = db.insert(TABLE_CALL_LOG, null, initialValues);
                isUpdated = true;

            } else {

                flag = db.update(TABLE_CALL_LOG, initialValues, DB_COLUMN_USER + "=?" + " AND " +
                        DB_COLUMN_USER_ID + "=?" + " AND " +
                        DB_COLUMN_OPPONENT + "=?" + " AND " +
                        DB_COLUMN_DATE + "=?" + " AND " +
                        DB_COLUMN_TIME + "=?" + " AND " +
                        DB_COLUMN_CALL_STATUS + "=?" + " AND " +
                        DB_COLUMN_CALL_PRIORITY + "=?" + " AND " +
                        DB_COLUMN_CALL_TYPE + "=?", new String[]{callLogModel.get(0).getCallUserName(),
                        callLogModel.get(0).getUserId(),
                        callLogModel.get(0).getCallOpponentName(),
                        callLogModel.get(0).getCallDate(),
                        callLogModel.get(0).getCallTime(),
                        callLogModel.get(0).getCallStatus(),
                        callLogModel.get(0).getCallPriority(),
                        callLogModel.get(0).getCallType()
                });

            }

        } catch (Exception e) {
            e.getMessage();

        }
        return isUpdated;
    }

    public ContentValues gerContentValues(ArrayList<CallLogModel> callLogModels) {
        ContentValues initialValues = new ContentValues();
        try {

            initialValues.put(DB_COLUMN_USER, callLogModels.get(0).getCallUserName());
            initialValues.put(DB_COLUMN_USER_ID, callLogModels.get(0).getUserId());
            initialValues.put(DB_COLUMN_OPPONENT, callLogModels.get(0).getCallOpponentName());
            initialValues.put(DB_COLUMN_DATE, callLogModels.get(0).getCallDate());
            initialValues.put(DB_COLUMN_TIME, callLogModels.get(0).getCallTime());
            initialValues.put(DB_COLUMN_CALL_STATUS, callLogModels.get(0).getCallStatus());
            initialValues.put(DB_COLUMN_CALL_PRIORITY, callLogModels.get(0).getCallPriority());
            initialValues.put(DB_COLUMN_CALL_TYPE, callLogModels.get(0).getCallType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return initialValues;
    }


    public ArrayList<CallLogModel> getCallHistory(String UserName) {

        ArrayList<CallLogModel> callLogModelArrayList = null;
        try {
            callLogModelArrayList = new ArrayList<CallLogModel>();
            Cursor mCursor = db.query(TABLE_CALL_LOG, new String[]
                            {
                                    DB_COLUMN_USER,
                                    DB_COLUMN_USER_ID,
                                    DB_COLUMN_OPPONENT,
                                    DB_COLUMN_TIME,
                                    DB_COLUMN_DATE,
                                    DB_COLUMN_CALL_STATUS,
                                    DB_COLUMN_CALL_PRIORITY,
                                    DB_COLUMN_CALL_TYPE
                            },
                    DB_COLUMN_USER + "=?", new String[]{UserName},
                    null,
                    null,
                    null,
                    null);
            if (mCursor == null || mCursor.getCount() <= 0) {
                if (mCursor != null)
                    mCursor.close();
                return callLogModelArrayList;
            } else {
                mCursor.moveToFirst();
                for (int counter = 0; counter < mCursor.getCount(); counter++) {
                    CallLogModel callLogModel = new CallLogModel();


                    callLogModel.setCallUserName(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_USER)));
                    callLogModel.setUserId(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_USER_ID)));
                    callLogModel.setCallOpponentName(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_OPPONENT)));
                    callLogModel.setCallDate(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_DATE)));
                    callLogModel.setCallTime(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_TIME)));
                    callLogModel.setCallStatus(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_CALL_STATUS)));
                    callLogModel.setCallPriority(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_CALL_PRIORITY)));
                    callLogModel.setCallType(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_CALL_TYPE)));
                    callLogModelArrayList.add(callLogModel);
                    mCursor.moveToNext();

                }
            }
            mCursor.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return callLogModelArrayList;
    }
}
