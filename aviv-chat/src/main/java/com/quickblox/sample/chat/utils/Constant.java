package com.quickblox.sample.chat.utils;

import android.os.Environment;

import com.quickblox.chat.model.QBChatDialog;

import java.io.File;

/**
 * Created by Lenovo on 29-06-2017.
 */

public class Constant {


    // TMR URL
//    public static final String BASE_URL = "http://182.75.161.194/chat/smartchapp/index.php/";
    public static final String BASE_URL = "http://182.75.161.194/chat/tmrkonnek/index.php/";

    //Local System URL
//    public static final String BASE_URL = "http://192.168.1.5/konnek/index.php/";

    // Bamgalore Office Ip address
//    public static final String BASE_URL = "http://192.168.0.8/konnek/index.php/";
//
    //Konnek2 URL
//    public static final String BASE_URL = "http://smartchapp.azurewebsites.net/";
    public static final String CHAT_BOT_URL = "http://avivglobaltech.azurewebsites.net/";

    public static String NOTIFY_ONE_TO_ONE = "notify_one_to_one";
    public static String NOTIFY_MULTIPLE = "notify_multiple";
    public static String NOTIFY_PROFILE_IMAGE = "notify_profile_image";
    public static final String EXTRA_IS_TYPING = "typing...";
    public static final String EXTRA_STOP_TYPING = " ";
    public static final String CONTENT_LENGTH_TAG = "15 ";
    public static QBChatDialog DIALOG = null;
    public static int SELECTED_USER_SIZE = 1;
    public static int CHAT_AUDIO_CALL_LIMIT = 5;
    public static int CHAT_VIDEO_CALL_LIMIT = 3;
    public static int CALL_LIST_SIZE = 3;
    public static String TAG_IMAGE_ID_DOWNLOAD = "downloadImageId";

    /*FOLDER CREATION*/
    public static String APP_NAME = "KONNEK2";
    public static final String FOLDER_SEPARATOR = File.separator;
    public static final String EXT_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String IMAGE_FOLDER = "KONNEK2_IMAGE";
    public static String AUDIO_FOLDER = "KONNEK2_AUDIO";
    public static String VIDEO_FOLDER = "KONNEK2_VIDEO";
    public static String PROFILE_FOLDER = "KONNEK2_PROFILE";

}
