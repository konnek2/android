package com.aviv.konnek2.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aviv.konnek2.models.UserModel;

/**
 * Created by Lenovo on 27-06-2017.
 */

public class UsersTable {


    private SQLiteDatabase db;
    private Context context;

    public UsersTable(SQLiteDatabase mdb, Context context) {
        super();
        this.context = context;
        db = mdb;
    }

    public static final String USER_ID = "user_id";
    public static final String FIRST_NAME = "first_name";
    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String EMAIL = "email";
    public static final String DATA_OF_BIRTH = "data_of_birth";
    public static final String GENDER = "gender";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String ZIPCODE = "zipcode";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String TABLE_REG_USERS = "register_users";


    public static final String TABLE_REG_USERS_CREATE = " create table IF NOT EXISTS " +
            TABLE_REG_USERS + "(" +
            USER_ID + " text not null," +
            FIRST_NAME + " text, " +
            MOBILE_NUMBER + " text , " +
            EMAIL + " text ," +
            DATA_OF_BIRTH + " text," +
            GENDER + " text, " +
            CITY + " text, " +
            COUNTRY + " text, " +
            ZIPCODE + " text, " +
            CREATED_AT + " text, " +
            UPDATED_AT + " text "
            + ")";


    public boolean insertuserDetails(UserModel userModel) {

        long flag1 = 0;
        boolean isUpdated = false;
        try {
            ContentValues initialValues1 = gerContentValues(userModel);
            Cursor mCursor1 = db.query(TABLE_REG_USERS, new String[]
                            {USER_ID, FIRST_NAME, MOBILE_NUMBER, EMAIL, DATA_OF_BIRTH, GENDER, CITY, COUNTRY,
                                    ZIPCODE, CREATED_AT, UPDATED_AT},
                    MOBILE_NUMBER + "=? " + " AND " +
                            USER_ID + "=?"
                    , new String[]{String.valueOf(userModel.getMobileNumber()), userModel.getUserId()}, null, null, null, null);
            if (mCursor1 == null || mCursor1.getCount() <= 0) {
                if (mCursor1 != null) {
                    mCursor1.close();
                }
                flag1 = db.insert(TABLE_REG_USERS, null, initialValues1);

                isUpdated = true;

            } else {
                flag1 = db.update(TABLE_REG_USERS, initialValues1, MOBILE_NUMBER + "=? " + " AND " +
                        USER_ID + "=?", new String[]{String.valueOf(userModel.getMobileNumber()), userModel.getUserId()});

            }

            mCursor1.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
//            LogTrace.e(TAG, "Error inserting customer information");
        }
        return isUpdated;
    }

    private ContentValues gerContentValues(UserModel userModel) {
        ContentValues initialValues = new ContentValues();
        try {
            initialValues.put(USER_ID, userModel.getUserId());
            initialValues.put(FIRST_NAME, userModel.getName());
            initialValues.put(EMAIL, userModel.getEmail());
            initialValues.put(MOBILE_NUMBER, userModel.getMobileNumber());
            initialValues.put(DATA_OF_BIRTH, userModel.getDateOfBirth());
            initialValues.put(GENDER, userModel.getGender());
            initialValues.put(CITY, userModel.getCity());
            initialValues.put(COUNTRY, userModel.getCountry());
            initialValues.put(ZIPCODE, userModel.getZipCode());
            initialValues.put(CREATED_AT, userModel.getCreatedAt());
            initialValues.put(UPDATED_AT, userModel.getUpdatedAt());


            // Date  to  String
        } catch (Exception e) {
            e.printStackTrace();
        }
        return initialValues;
    }


    public UserModel getUserDetails(String mobileNumber, String userId) {

        UserModel userModel = null;
        try {
            Cursor mCursor = db.query(TABLE_REG_USERS, new String[]{
                            USER_ID,
                            FIRST_NAME,
                            EMAIL,
                            MOBILE_NUMBER,
                            CITY,
                            COUNTRY,
                            DATA_OF_BIRTH,
                            GENDER,
                            ZIPCODE,
                            CREATED_AT,
                            UPDATED_AT},
                    MOBILE_NUMBER + "=? " + " AND " +
                            USER_ID + "=?", new String[]{String.valueOf(mobileNumber), userId},
                    null,
                    null,
                    null,
                    null);
            if (mCursor == null || mCursor.getCount() <= 0) {
                if (mCursor != null)
                    mCursor.close();
                return userModel;
            }
            mCursor.moveToFirst();
            userModel = new UserModel();
            for (int counter = 0; counter < mCursor.getCount(); counter++) {


                userModel.setUserId(mCursor.getString(mCursor.getColumnIndex(USER_ID)));
                userModel.setName(mCursor.getString(mCursor.getColumnIndex(FIRST_NAME)));
                userModel.setEmail(mCursor.getString(mCursor.getColumnIndex(EMAIL)));
                userModel.setMobileNumber(mCursor.getString(mCursor.getColumnIndex(MOBILE_NUMBER)));
                userModel.setDateOfBirth(mCursor.getString(mCursor.getColumnIndex(DATA_OF_BIRTH)));
                userModel.setGender(mCursor.getString(mCursor.getColumnIndex(GENDER)));
                userModel.setCity(mCursor.getString(mCursor.getColumnIndex(CITY)));
                userModel.setCountry(mCursor.getString(mCursor.getColumnIndex(COUNTRY)));
                userModel.setZipCode(mCursor.getString(mCursor.getColumnIndex(ZIPCODE)));
                userModel.setCreatedAt(mCursor.getString(mCursor.getColumnIndex(CREATED_AT)));
                userModel.setUpdatedAt(mCursor.getString(mCursor.getColumnIndex(UPDATED_AT)));


                mCursor.moveToNext();
            }
            mCursor.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return userModel;
    }
}
