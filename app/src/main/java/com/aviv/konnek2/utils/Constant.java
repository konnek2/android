package com.aviv.konnek2.utils;

/**
 * Created by Lenovo on 21-06-2017.
 */

public class Constant {

    //  it is a dummy URL  replace these by your  Server URl
    public static final String BASE_URL = "http://example/chat/index.php/";

    public static final String CHAT_BOT_URL = "http://avivglobaltech.azurewebsites.net/";
    // Preference Login & profile
    public static String APP_NAME="Konnek2";
    public static String APP_PACKAGE="com.aviv.konnek2";
    public static final String SIGN_PREFERENCE = "sign_preference";
    public static final String PROFILE_PREFERENCE = "profile_preference";
    public static String USER_ID = "user_id";
    public static String USER_NAME = "user_name";
    public static String PREF_USER_NAME = "pref_user_name";
    public static String USER_MOBILE_NUMBER = "user_mobile_number";
    public static String USER_COUNTRY = "user_country";
    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String IS_QB_LOGGED_IN = "isqb_LoggedIn";
    public static final String USER_PROFILE_IMAGE = "user_profile_image";
    // OTP
    public static String OTP = "otp";
    public static String OTP_PREFERENCE = "otp";
    public static String OTP_DEFAULT_CODE = "2020";

    public static String TAB_POSITION = "100";
    //  Tag
    public static String TAG_LOGIN = "login";
    public static String TAG_PROFILE_UPDATE = "profileupdate";
    public static String TAG_RESEND_OTP = "reSendOtp";
    public static String TAG_VERIFY_OTP = "verifyOTP";
    public static String TAG_IMAGE_ID = "UploadImageId";
    //Server Response Code
    public static String RESPONSE_CODE = "0";
    //QuickBlox Constant
    public static String CHAT_ROOM = "konnek2";
    public static String QBUSER_LOGIN = "qbuser_login";

    // BroadCast Receiver
    public static String NOTIFY_OTP = "notify_otp";
    public static String MESSAHE_FORMAT = "KONNEK";


    //Tab Header
    public static String TAB_ONE = "Tab 1";
    public static String TAB_TWO = "Tab 2";
    public static String TAB_THREE = "Tab 3";
    public static String TAB_CALL_HISTORY = "CALL HISTORY";
    public static String TAB_CHAT = "CHAT";
    public static String TAB_CONTACTS = "CONTACTS";

    // TabView Title
    public static String HOME = "Home";
    public static String CONNECT = "Connect";
    public static String GREATER_THAN = " > ";
    public static String CHARITY = "Charity";
    public static String SARAI = "Sarai";
    public static String HANGOUTS = "Hangouts";
    public static String M_STORE = "m-Store";
    public static String MONEY = "Money";
    public static String TRAVEL = "Travel";
    public static String SUB_TITLE_ONE = "chat bot";
    public static String SUB_TITLE_TWO = "chat & call";
    public static String USER_PROFILE = "User Profile";
    public static String OTP_RECEIVED_BROADCAST_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    // Splash screen
    public static int SPLASH_TIME = 3000;

    // Toast Messgae
    public static String TOAST_MESSAGE = "In progress";
    public static String TOAST_DATE_OF_BIRTH = "Enter the valid Data of Birth";
    public static String TOAST_PROFILE_IMAGE_FAILURER = "Profile set failure";
    public static String TOAST_FILE_FORMAT_NOT_SUPPORT = "File Format not Supported";
    public static String TOAST_NO_INTERNET_CONNECTION = "No InterNet Connection";
    public static String TOAST_LOGIN_CHAT_ERROR = "Login Chat error. Please relogin";
    public static String TOAST_VALID_OTP = "Enter Valid OTP";
    public static String TOAST_USER_NAME = "Enter the User Name";
    public static String TOAST_VALID_MOBILE_NUMBER = "Enter 10 digits Valid Mobile Number";


    // Image Format

    public static String IMAGE_FORMAT_JPG = ".jpg";
    public static String IMAGE_FORMAT_PNG = ".png";
    public static String IMAGE_INTENT_TYPE = "image/png";
    public static String REFER_FRIEND_ACTION_TYPE ="text/plain";
    public static String DATE_FORMAT = "yyyy-MM-dd";

    //Refer Friend Message
    public static String REFER_FRIEND_MESSGAE = "Hi I am using Konnek2 app please join with me";
    public static String PLAY_STORE_LINK = "\n App Link:" + "https://play.google.com/store/apps";

    // Sqlite  DB BackUp StoragePath
    public static String SQLITE_CURRENT_PATH = "//data//" + "com.aviv.konnek2" + "//databases//" + "konnek2";
    public static String SQLITE_BACKUP_PATH = "konnek2" + ".backup";
    public static String DB_FULL_PATH = "/data/data/com.aviv.konnek2/databases/";

}
