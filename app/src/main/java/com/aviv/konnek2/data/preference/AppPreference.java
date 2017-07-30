package com.aviv.konnek2.data.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.aviv.konnek2.utils.Constant;

/**
 * Created by Lenovo on 27-06-2017.
 */

public class AppPreference {

    private Context context;
    public static SharedPreferences sigInPreference;
    public static SharedPreferences profilePreference;
    public static SharedPreferences.Editor signInEditor;
    public static SharedPreferences.Editor profileEditor;


    public AppPreference(Context context) {
        this.context = context;

        sigInPreference = context.getSharedPreferences(Constant.SIGN_PREFERENCE, 0);
        profilePreference = context.getSharedPreferences(Constant.PROFILE_PREFERENCE, 0);
        signInEditor = sigInPreference.edit();
        profileEditor = profilePreference.edit();

    }

    // put Values in Preference

    public static void putUserId(String userName) {
        signInEditor.putString(Constant.USER_ID, userName);
        signInEditor.apply();

    }

    public static void putUserName(String userName) {
        signInEditor.putString(Constant.PREF_USER_NAME, userName);
        signInEditor.apply();

    }

    public static void putMobileNumber(String userName) {
        signInEditor.putString(Constant.USER_MOBILE_NUMBER, userName);
        signInEditor.apply();

    }

    public static void putCountry(String userName) {
        signInEditor.putString(Constant.USER_COUNTRY, userName);
        signInEditor.apply();

    }

    public static void putLoginStatus(boolean status) {
        signInEditor.putBoolean(Constant.IS_LOGGED_IN, status);
        signInEditor.apply();

    }

    public static void putQbUserLogin(String userLogin) {

        signInEditor.putString(Constant.QBUSER_LOGIN, userLogin);
        signInEditor.apply();
    }

    public static void putQUserPassword(String userPassword) {

        signInEditor.putString(Constant.QBUSER_LOGIN, userPassword);
        signInEditor.apply();
    }

    public static void putQbLoginStatus(boolean status) {
        signInEditor.putBoolean(Constant.IS_QB_LOGGED_IN, status);
        signInEditor.apply();

    }

    public static void putProfileImagePath(String filePath) {
        signInEditor.putString(Constant.USER_PROFILE_IMAGE, filePath);
        signInEditor.apply();

    }

    public static void putOtp(String otp) {
        signInEditor.putString(Constant.OTP_PREFERENCE, otp);
        signInEditor.apply();

    }

    // get Values from Preference

    public static String getUserId() {
        return sigInPreference.getString(Constant.USER_ID, null);
    }


    public static String getUserName() {
        return sigInPreference.getString(Constant.PREF_USER_NAME, null);
    }

    public static String getMobileNumber() {
        return sigInPreference.getString(Constant.USER_MOBILE_NUMBER, null);
    }

    public static String getCountry() {
        return sigInPreference.getString(Constant.USER_COUNTRY, null);
    }

    public static boolean getLoginStatus() {
        return sigInPreference.getBoolean(Constant.IS_LOGGED_IN, false);
    }

    public static boolean getQBUserLoginStatus() {
        return sigInPreference.getBoolean(Constant.IS_QB_LOGGED_IN, false);
    }

    public static String getProfileImagePath() {
        return sigInPreference.getString(Constant.USER_PROFILE_IMAGE, null);
    }

    public static String getOtp() {
        return sigInPreference.getString(Constant.OTP_PREFERENCE, null);
    }

    public static SharedPreferences.Editor signInEditorMhd() {
        return signInEditor;
    }
}
