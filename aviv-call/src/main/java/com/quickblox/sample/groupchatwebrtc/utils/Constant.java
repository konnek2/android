package com.quickblox.sample.groupchatwebrtc.utils;

import android.os.Environment;

import com.quickblox.users.model.QBUser;

import java.io.File;
import java.util.List;

/**
 * Created by Lenovo on 28-06-2017.
 */

public class Constant {


    public static String QBJoyuserName = "qbuser";
    public static String QBJoychatRoom = "qbchatroom";
    public static String CallingStatus = "callstatus";
    public static String CALL_STATUS_DIALED = "call_dialed";
    public static String CALL_STATUS_MISSED = "call_missed";
    public static String CALL_STATUS_RECEIVED = "call_received";
    public static String CALL_STATUS_REJECTED = "call_rejected";
    public static String CALL_PRIORITY_HIGH = "High Priority";
    public static String CALL_PRIORITY_MEDIUM = "Medium Priority";
    public static String CALL_PRIORITY_LOW= "Low Priority";
    public static String CALL_DURATION = "call_duration";
    public static String CALL_VIDEO = "call_video";
    public static String CALL_AUDIO= "call_audio";
    public static String QB_CONFERENCE_TYPE_VIDEO= "1";
    public static String QB_CONFERENCE_TYPE_AUDIO= "2";

    // for Callling process
    public static String CALL_PRIORITY= "call_priority";
    public static String USER_ID= "user_id";
    public static String DATE= "date";
    public static String TIME= "time";
    public static String STATUS= "status";
    public static String OPPONENTS= "opponent";
    public static String GROUP_CALL= "Group Call";
    public static int CHAT_CALL;
    public static int CHAT_CALL_STATUS=1;
    public static boolean CALL_TYPE= false;
    public static List<QBUser> QB_USER_LIST;

    // BrodCastReceiver

    public static String NOTIFY_CALL_TRIGER = "notify_call_triger";
    public static int CALL_LIST_SIZE = 2;
    public static int VIDEO_CALL_HIDE = 1;


    /*for Create Folder*/

    public static String APP_NAME = "KONNEK2";
    public static final String FOLDER_SEPARATOR = File.separator;
    public static final String EXT_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String IMAGE_FOLDER = "KONNEK2_IMAGE";
    public static String AUDIO_FOLDER = "KONNEK2_AUDIO";
    public static String VIDEO_FOLDER = "KONNEK2_VIDEO";
    public static String PROFILE_FOLDER = "KONNEK2_PROFILE";




    // Tab

    public static String HOME = "Home";
    public static String CONNECT = "Connect";
    public static String GREATER_THAN = " > ";
    public static String TAB_POSITION = "100";
    //Tab Header
    public static String TAB_ONE = "Tab 1";
    public static String TAB_TWO = "Tab 2";
    public static String TAB_THREE = "Tab 3";
    public static String TAB_CALL_HISTORY = "CALL HISTORY";
    public static String TAB_CHAT = "CHAT";
    public static String TAB_CONTACTS = "CONTACTS";
    public static String Call_CONFRENCE_LIMIT = "4 Users only allowed in Conference call";

    // db
    public static String CALL_DB_CURRENT_PATH = "//data//" + "com.aviv.konnek2" + "//databases//" + "konnek2";
    public static String CALL_DB_BACKUP_PATH = "konnek2call" + ".backup";
    public static String DB_FULL_PATH = "/data/data/com.aviv.konnek2/databases/";
    public static String DB_NAME = "groupchatwebrtcDB";
    public static String DATABASE_CALL_NAME = "calllog";
    public static String PING_ALARM_ACTION = "com.quickblox.chat.ping.ACTION";
    public static String ANDROID_NS = "http://schemas.android.com/apk/res/android";
    public static String SEEKBAR_NS = "http://schemas.android.com/apk/res-auto";


    // Exaption

    public static String ACTIVITY_EXCEPTION ="This Activity needs to be launched using the static startActivityForResult() method .";

}
