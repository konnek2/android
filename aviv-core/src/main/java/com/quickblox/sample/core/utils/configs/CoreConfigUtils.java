package com.quickblox.sample.core.utils.configs;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.quickblox.sample.core.models.QbConfigs;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CoreConfigUtils {

//    public static final String USER_LOGIN_FIELD_NAME = "user_login";
//    public static final String USER_PASSWORD_FIELD_NAME = "user_password";

    public static final String USER_LOGIN_FIELD_NAME = "suresh.konnek2";
    public static final String USER_PASSWORD_FIELD_NAME = "Password@123";

    public static QbConfigs getCoreConfigs(String fileName) throws IOException {
        ConfigParser configParser = new ConfigParser();
        Gson gson = new Gson();
        return gson.fromJson(configParser.getConfigsAsJsonString(fileName), QbConfigs.class);
    }

    public static QbConfigs getCoreConfigsOrNull(String fileName){
        QbConfigs qbConfigs = null;

        try {
            qbConfigs = getCoreConfigs(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return qbConfigs;
    }

    public static String getStringConfigFromFile(String fileName, String fieldName) throws IOException, JSONException {
        JSONObject appConfigs = new ConfigParser().getConfigsAsJson(fileName);
        return appConfigs.getString(fieldName);
    }

    public static String getStringConfigFromFileOrNull(String fileName, String fieldName) {
        String fieldValue = null;

        try {
            fieldValue = getStringConfigFromFile(fileName, fieldName);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return fieldValue;
    }

    public static boolean isStringConfigFromFileNotEmpty(String fileName, String fieldName){
        return !TextUtils.isEmpty(getStringConfigFromFileOrNull(fileName, fieldName));
    }

    public static QBUser getUserFromConfig(){
        QBUser qbUser = null;
        String userLogin;
        String userPassword;


        try {
            userLogin = "suresh.konnek2";
            userPassword = "Password@123";
            qbUser = new QBUser(userLogin, userPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return qbUser;
    }
}
