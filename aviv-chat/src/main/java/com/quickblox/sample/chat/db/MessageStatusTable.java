package com.quickblox.sample.chat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.sample.chat.models.MessageStatusModel;
import com.quickblox.users.model.QBUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lenovo on 30-06-2017.
 */

public class MessageStatusTable {


    private static final String TAG = MessageStatusTable.class.getSimpleName();
    private SQLiteDatabase db;
    private Context context;


    public MessageStatusTable(SQLiteDatabase mdb, Context context) {
        super();
        this.context = context;
        db = mdb;
    }

    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String QB_USER_NAME = "user_name";
    public static final String QB_USER_LOGIN = "qb_user_login";
    public static final String QB_USER_ID = "qb_user_id";
    public static final String QB_USER_PASSWORD = "qb_user_password";
    public static final String QB_USER_TAG = "qb_user_tag";
    public static final String RECIPIENT_ID = "recipient_id";
    public static final String MESSAGE_ID = "message_id";
    public static final String IS_UPDATE_SERVER = "is_update_server";
    public static final String IS_DELIVERED = "is_delivered";
    public static final String IS_READ = "is_read";
    public static final String TABLE_MESSAGE_STATUS = "table_message_status";
//    static String PRIMARYKEY_REG = MESSAGE_ID;

    public static final String TABLE_MESSAGE__STATUS_CREATE = " create table IF NOT EXISTS " +
            TABLE_MESSAGE_STATUS + " ("
            + ID + " integer primary key autoincrement,"
            + USER_ID + " text,"
            + RECIPIENT_ID + " text,"
            + MESSAGE_ID + " text,"
            + QB_USER_NAME + " text,"
            + QB_USER_LOGIN + " text,"
            + QB_USER_ID + " text,"
            + QB_USER_PASSWORD + " text,"
            + QB_USER_TAG + " text,"
            + IS_UPDATE_SERVER + " integer DEFAULT 0 , "
            + IS_DELIVERED + " integer DEFAULT 0 , "
            + IS_READ + " integer DEFAULT 0 "
            + ")";

    public long messageStatusUpdate(ArrayList<MessageStatusModel> messageStatusModels) {
        long flag = 0;
        try {

            ContentValues initialValues = gerContentValues(messageStatusModels);
            Cursor mCursor = db.query(TABLE_MESSAGE_STATUS, new String[]
                            {
                                    RECIPIENT_ID,
                                    MESSAGE_ID,
                            },

                    RECIPIENT_ID + "=?" + " AND " +
                            MESSAGE_ID + "=?",
                    new String[]{
                            messageStatusModels.get(0).getRecipientId(),
                            messageStatusModels.get(0).getMessageId(),
                    },
                    null,
                    null,
                    null);

            if (mCursor == null || mCursor.getCount() <= 0) {
                if (mCursor != null) {
                    mCursor.close();
                }
                flag = db.insert(TABLE_MESSAGE_STATUS, null, initialValues);



            } else {

                flag = db.update(TABLE_MESSAGE_STATUS, initialValues,
                        RECIPIENT_ID + "=?" + " AND " +
                                MESSAGE_ID + "=?", new String[]{
                                messageStatusModels.get(0).getRecipientId(),
                                messageStatusModels.get(0).getMessageId()
                        });

            }

        } catch (Exception e) {
            e.getMessage();

        }
        return flag;

    }

    public ContentValues gerContentValues(ArrayList<MessageStatusModel> messageStatusModels) {
        ContentValues initialValues = new ContentValues();
        try {
            initialValues.put(USER_ID, messageStatusModels.get(0).getUserId());
            initialValues.put(RECIPIENT_ID, messageStatusModels.get(0).getRecipientId());
            initialValues.put(MESSAGE_ID, messageStatusModels.get(0).getMessageId());
            initialValues.put(IS_UPDATE_SERVER, messageStatusModels.get(0).getIsUpdateServer());
            initialValues.put(IS_DELIVERED, messageStatusModels.get(0).getIsDelivered());
            initialValues.put(IS_READ, messageStatusModels.get(0).getIsRead());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return initialValues;
    }

    public int getSurveyStatus(String messageID) {
        int status = 0;
        Cursor cursor = null;
        try {

            cursor = db.query(TABLE_MESSAGE_STATUS, new String[]{
                            IS_UPDATE_SERVER},
                    MESSAGE_ID + "=?",
                    new String[]{messageID},
                    null,
                    null,
                    null);
            if (cursor == null || cursor.getCount() <= 0) {
                if (cursor != null)
                    cursor.close();
                return status;
            }

            cursor.moveToFirst();
            for (int counter = 0; counter < cursor.getCount(); counter++) {
                status = cursor.getInt(cursor.getColumnIndex(IS_UPDATE_SERVER));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (SQLiteException sqlex) {
            sqlex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return status;
    }

    public int getDeliveryStatus(String messageID) {
        int status = 0;
        Cursor cursor = null;
        try {

            cursor = db.query(TABLE_MESSAGE_STATUS, new String[]{
                            IS_DELIVERED},
                    MESSAGE_ID + "=?",
                    new String[]{messageID},
                    null,
                    null,
                    null);
            if (cursor == null || cursor.getCount() <= 0) {
                if (cursor != null)
                    cursor.close();
                return status;
            }

            cursor.moveToFirst();
            for (int counter = 0; counter < cursor.getCount(); counter++) {
                status = cursor.getInt(cursor.getColumnIndex(IS_DELIVERED));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (SQLiteException sqlex) {
            sqlex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return status;
    }

    public int getReadStatus(String messageID) {
        int status = 0;
        Cursor cursor = null;
        try {

            cursor = db.query(TABLE_MESSAGE_STATUS, new String[]{
                            IS_READ},
                    MESSAGE_ID + "=?",
                    new String[]{messageID},
                    null,
                    null,
                    null);
            if (cursor == null || cursor.getCount() <= 0) {
                if (cursor != null)
                    cursor.close();
                return status;
            }

            cursor.moveToFirst();
            for (int counter = 0; counter < cursor.getCount(); counter++) {
                status = cursor.getInt(cursor.getColumnIndex(IS_READ));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (SQLiteException sqlex) {
            sqlex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return status;
    }

    public long saveAllUsers(ArrayList<QBUser> qbUserArrayList) {
        long flag = 0;
        Cursor mCursor = null;

        try {
            for (int i = 0; i < qbUserArrayList.size(); i++) {
                QBUser qbUsers = qbUserArrayList.get(i);
                ContentValues initialValues = gerContentValue(qbUsers);
                mCursor = db.query(TABLE_MESSAGE_STATUS, new String[]{
                                QB_USER_ID},
                        QB_USER_ID + "=?",
                        new String[]{String.valueOf(qbUsers.getId())},
                        null,
                        null,
                        null);
                if (mCursor == null || mCursor.getCount() <= 0) {
                    if (mCursor != null) {
                        mCursor.close();
                    }

                    flag = db.insert(TABLE_MESSAGE_STATUS, null, initialValues);


                } else {

                    flag = db.update(TABLE_MESSAGE_STATUS, initialValues,
                            QB_USER_ID + "=?",
                            new String[]{String.valueOf(qbUsers.getId())});
                }
            }
            mCursor.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return flag;
    }


    private ContentValues gerContentValue(QBUser qbUserslist) {
        ContentValues initialValues = new ContentValues();
        try {

            initialValues.put(QB_USER_ID, qbUserslist.getId());
            initialValues.put(QB_USER_NAME, qbUserslist.getFullName());
            initialValues.put(QB_USER_LOGIN, qbUserslist.getLogin());
            initialValues.put(QB_USER_PASSWORD, qbUserslist.getPassword());
            initialValues.put(QB_USER_TAG, qbUserslist.getTags().getItemsAsString());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return initialValues;
    }


    public ArrayList<QBUser> getAllQBUsers(ArrayList<QBUser> qbUsersList) {

        ArrayList<QBUser> qbUserArrayList = new ArrayList<>();
        for (int i = 0; i < qbUsersList.size(); i++) {
            Cursor mCursor = db.query(TABLE_MESSAGE_STATUS, new String[]
                            {
                                    QB_USER_ID,
                                    QB_USER_NAME,
                                    QB_USER_LOGIN,
                                    QB_USER_PASSWORD,
                                    QB_USER_TAG},
                    QB_USER_ID + "=?",
                    new String[]{String.valueOf(qbUsersList.get(i).getId())},
                    null,
                    null,
                    null);
            if (mCursor == null || mCursor.getCount() <= 0) {
                if (mCursor != null)
                    mCursor.close();
                return qbUserArrayList;
            } else {
                mCursor.moveToFirst();
                for (int counter = 0; counter < mCursor.getCount(); counter++) {
                    QBUser qbUser = new QBUser();
                    qbUser.setId(mCursor.getInt(mCursor.getColumnIndex(QB_USER_ID)));
                    qbUser.setFullName(mCursor.getString(mCursor.getColumnIndex(QB_USER_NAME)));
                    qbUser.setLogin(mCursor.getString(mCursor.getColumnIndex(QB_USER_LOGIN)));
                    qbUser.setPassword(mCursor.getString(mCursor.getColumnIndex(QB_USER_PASSWORD)));
                    StringifyArrayList<String> tags = new StringifyArrayList<>();
                    tags.add(mCursor.getString(mCursor.getColumnIndex(QB_USER_TAG)).split(","));
                    qbUser.setTags(tags);
                    qbUserArrayList.add(qbUser);
                    mCursor.moveToNext();
                }
            }

            mCursor.close();
        }

        return qbUserArrayList;
    }

}


