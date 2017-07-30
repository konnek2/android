package com.aviv.konnek2.data.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.aviv.konnek2.Konnnek2;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.ServiceZone;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.sample.chat.utils.chat.ChatHelper;
import com.quickblox.sample.core.models.QbConfigs;
import com.quickblox.sample.core.utils.configs.CoreConfigUtils;
import com.quickblox.sample.groupchatwebrtc.util.ChatPingAlarmManager;
import com.quickblox.sample.groupchatwebrtc.utils.SettingsUtil;
import com.quickblox.sample.groupchatwebrtc.utils.WebRtcSessionManager;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;

import org.jivesoftware.smackx.ping.PingFailedListener;

/**
 * Created by Lenovo on 30-06-2017.
 */

public class SignInQbService extends Service {

    private QBUser userForSave;
    private SharedPreferences sharedPreferences;
    private QBChatService chatService;
    private static final String SHARED_PREFS_NAME = "qb";

    private String QB_USER_ID = "qb_user_id";
    private String QB_USER_LOGIN = "qb_user_login";
    private String QB_USER_PASSWORD = "qb_user_password";
    private String QB_USER_FULL_NAME = "qb_user_full_name";
    private String QB_USER_TAGS = "qb_user_tags";
    private QbConfigs qbConfigs;
    private QBRTCClient rtcClient;
    private static final String QB_CONFIG_DEFAULT_FILE_NAME = "qb_config.json";
    private Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ALARAMTEST", "  SignInQbService onStartCommand   Service Started");
        try {
            initQbConfigs();
            initCredentials();
            sharedPreferences = this.getSharedPreferences("qb", Context.MODE_PRIVATE);
            if (chatService == null) {
                QBChatService.setDebugEnabled(true);
                chatService = QBChatService.getInstance();
                Log.d("ALARAMTEST", "   SignInQbService  QBChatService.getInstance  " + chatService);
            }
            createSession();
            QBUser user = getQBUser();
            Log.d("ALARAMTEST", "   SignInQbService   SharedPreferencesUtil.getQbUser() " + user);
            if (user == null) {
                Log.d("ALARAMTEST", "    user ==null ");
                loginToChat(user);
                reloginToChat(user);
            } else {
                loginToChat(user);
                reloginToChat(user);
                Log.d("ALARAMTEST", "  SignInQbService  user !=null ");
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
        return START_STICKY;
    }

    // Chat Config Creation
    private void initQbConfigs() {
        Log.d("ALARAMTEST", "  SignInQbService QB CONFIG FILE NAME: " + getQbConfigFileName());
        qbConfigs = CoreConfigUtils.getCoreConfigsOrNull(getQbConfigFileName());
    }

    protected String getQbConfigFileName() {
        Log.d("ALARAMTEST", "   SignInQbService getQbConfigFileName ");
        return QB_CONFIG_DEFAULT_FILE_NAME;
    }

    public void initCredentials() {
        if (qbConfigs != null) {
            Log.d("ALARAMTEST", "  getAppId      " + qbConfigs.getAppId());
            Log.d("ALARAMTEST", " getAuthKey      " + qbConfigs.getAuthKey());
            Log.d("ALARAMTEST", "  getAuthSecret  " + qbConfigs.getAuthSecret());
            Log.d("ALARAMTEST", "  getAccountKey    " + qbConfigs.getAccountKey());
            QBSettings.getInstance().init(getApplicationContext(), qbConfigs.getAppId(), qbConfigs.getAuthKey(), qbConfigs.getAuthSecret());
            QBSettings.getInstance().setAccountKey(qbConfigs.getAccountKey());

            if (!TextUtils.isEmpty(qbConfigs.getApiDomain()) && !TextUtils.isEmpty(qbConfigs.getChatDomain())) {
                QBSettings.getInstance().setEndpoints(qbConfigs.getApiDomain(), qbConfigs.getChatDomain(), ServiceZone.PRODUCTION);
                QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);
            }
        }

    }

    private void createSession() {
//        QBUser qbUser = CoreConfigUtils.getUserFromConfig();
        QBUser qbUser = getQBUser();
        Log.d("ALARAMTEST", "createSession");
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                Log.d("ALARAMTEST ", "  SignInQbService  createSession onSuccess getToken  " + qbSession.getToken());
                Log.d("ALARAMTEST ", "  SignInQbService createSession  onSuccess   getUserId " + qbSession.getUserId());
            }

            @Override
            public void onError(QBResponseException e) {
                createSession();
                Log.d("ALARAMTEST ", "SignInQbService  onError ");
            }
        });
    }

    public QBUser getQBUser() {
        Log.d("ALARAMTEST", " SignInQbService getQBUser");
        QBUser user = null;
        try {

            Integer id = sharedPreferences.getInt(QB_USER_ID, 0);
            String login = sharedPreferences.getString(QB_USER_LOGIN, null);
            String password = sharedPreferences.getString(QB_USER_PASSWORD, null);
            String fullName = sharedPreferences.getString(QB_USER_FULL_NAME, null);
            String tagsInString = sharedPreferences.getString(QB_USER_TAGS, null);

            if (id != 0 && login != null && password != null && fullName != null && tagsInString != null) {
                StringifyArrayList<String> tags = null;
                if (tagsInString != null) {
                    tags = new StringifyArrayList<>();
                    tags.add(tagsInString.split(","));
                }
                user = new QBUser(login, password);
                user.setId(id);
                user.setFullName(fullName);
                user.setTags(tags);
                ;
            } else {
                user = null;
            }

        } catch (Exception ex) {
            ex.getMessage();
        }
        return user;
    }

    private void loginToChat(QBUser qbUser) {
        try {
            Log.d("ALARAMTEST ", "  SignInQbService  LoginService loginToChat ");
            chatService.login(qbUser, new QBEntityCallback<QBUser>() {
                @Override
                public void onSuccess(QBUser qbUser, Bundle bundle) {
                    Log.d("ALARAMTEST ", "   LoginService loginToChat  onSuccess  ");
                    startActionsOnSuccessLogin();
                }

                @Override
                public void onError(QBResponseException e) {
//                    Log.d(TAG, "login onError " + e.getMessage());
                    Log.d("ALARAMTEST ", "  SignInQbService  LoginService loginToChat  onError  ");
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void reloginToChat(final QBUser user) {
        try {
            Log.d("ALARAMTEST", " SignInQbService LoginService reloginToChat  ");
            ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void result, Bundle bundle) {
                    Log.d("ALARAMTEST", "  LoginService reloginToChat   onSuccess ");
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d("ALARAMTEST", " SignInQbService  LoginService  reloginToChat   onError ");
                    reloginToChat(user);
                }
            });

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void startActionsOnSuccessLogin() {
        Log.d("ALARAMTEST ", " SignInQbService   LoginService startActionsOnSuccessLogin ");
        initPingListener();
        initQBRTCClient();

    }

    private void initPingListener() {
        Log.d("ALARAMTEST ", "  SignInQbService  LoginService initPingListener ");
        ChatPingAlarmManager.onCreate(this);
        ChatPingAlarmManager.getInstanceFor().addPingListener(new PingFailedListener() {
            @Override
            public void pingFailed() {
                Log.d("ALARAMTEST ", "  SignInQbService  LoginService initPingListener  pingFailed ");
            }
        });
    }

    private void initQBRTCClient() {
        try {
            Log.d("ALARAMTEST ", "  SignInQbService LoginService initQBRTCClient ");
            rtcClient = QBRTCClient.getInstance(getApplicationContext());
            // Add signalling manager
            chatService.getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
                @Override
                public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                    if (!createdLocally) {
                        rtcClient.addSignaling((QBWebRTCSignaling) qbSignaling);
                    }
                }
            });
            // Configure
            QBRTCConfig.setDebugEnabled(true);
            Log.d("ALARAMTEST", " SignInQbServiceLoginService  initQBRTCClient getAppContext  " + Konnnek2.getAppContext());
            SettingsUtil.configRTCTimers(Konnnek2.getAppContext());
            // Add service as callback to RTCClient
            rtcClient.addSessionCallbacksListener(WebRtcSessionManager.getInstance(this));
            rtcClient.prepareToProcessCalls();

        } catch (Exception e) {
            e.getMessage();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
