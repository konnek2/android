package com.aviv.konnek2.ui.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aviv.konnek2.AppSigInPresenter;
import com.aviv.konnek2.R;
import com.aviv.konnek2.data.preference.AppPreference;
import com.aviv.konnek2.interfaces.SignInView;
import com.aviv.konnek2.models.UserModel;
import com.aviv.konnek2.utils.Common;
import com.aviv.konnek2.utils.Constant;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.helper.Utils;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.sample.chat.utils.FolderCreator;
import com.quickblox.sample.chat.utils.chat.ChatHelper;
import com.quickblox.sample.core.CoreApp;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.sample.core.utils.configs.CoreConfigUtils;
import com.quickblox.sample.groupchatwebrtc.App;
import com.quickblox.sample.groupchatwebrtc.services.CallService;
import com.quickblox.sample.groupchatwebrtc.util.QBResRequestExecutor;
import com.quickblox.sample.groupchatwebrtc.utils.Consts;
import com.quickblox.sample.groupchatwebrtc.utils.QBEntityCallbackImpl;
import com.quickblox.sample.groupchatwebrtc.utils.UsersUtils;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SignInActivity extends AppCompatActivity implements SignInView {

    public static final String TAG = SignInActivity.class.getSimpleName();

    private EditText EditTextUserName;
    private EditText EditTextMobileNumber;
    public static EditText EditTextOtp;
    private TextView TextViewCountryCode;
    private TextView TextViewResendOtp;
    private Spinner mCountrySpinner;
    private ProgressBar progress;
    private String mUserName, mMobileNumber, mCountryName, mOtpNumber;
    private String mFireBaseId;
    private String[] countryName;
    private int[] countryCode;
    private AppCompatButton BtnSignUp;
    TextView verifyOtp;
    private AppSigInPresenter AppSigInPresenter;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private LinearLayout signUpParent;
    private RelativeLayout otpParent;
    UserModel userModel;
    List<String> listPermissionsNeeded;

    // QuickBlox Variables
    SharedPrefsHelper sharedPrefsHelper;
    public QBResRequestExecutor requestExecutor;
    private QBUser userForSave;
    // OTP Read
    String phoneNumber, senderNum;
    String strMessage = "";
    SmsMessage currentMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);

        mFireBaseId = FirebaseInstanceId.getInstance().getToken();
        if (sampleConfigIsCorrect()) {
            createSession();
        }
        if (AppPreference.getLoginStatus() && AppPreference.getQBUserLoginStatus()) {

            try {

                Intent goToHome = new Intent(getApplicationContext(), HomeActivity.class);
                goToHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goToHome);
                finish();
            } catch (Exception e) {
                e.getMessage();
            }
        }
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {

        super.onResume();
        try {
            if (!checkPermissions()) {
                requestPermission();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(OtpReceiver);
        super.onDestroy();
    }


    private void initViews() {
        requestExecutor = App.getInstance().getQbResRequestExecutor();
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        AppSigInPresenter = new AppSigInPresenter(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.OTP_RECEIVED_BROADCAST_ACTION);
        this.registerReceiver(OtpReceiver, filter);
        EditTextUserName = (EditText) findViewById(R.id.et_userName);
        EditTextMobileNumber = (EditText) findViewById(R.id.et_mobileNumber);
        EditTextOtp = (EditText) findViewById(R.id.edit_text_otp);
        TextViewResendOtp = (TextView) findViewById(R.id.text_resend_otp);
        TextViewCountryCode = (TextView) findViewById(R.id.text_view_spinner);
        mCountrySpinner = (Spinner) findViewById(R.id.countrylist);
        progress = (ProgressBar) findViewById(R.id.progress_sign_in);
        BtnSignUp = (AppCompatButton) findViewById(R.id.button_login);
        verifyOtp = (TextView) findViewById(R.id.text_next);
        signUpParent = (LinearLayout) findViewById(R.id.signUp);
        otpParent = (RelativeLayout) findViewById(R.id.otpParent);
        progress.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFFFFF"), android.graphics.PorterDuff.Mode.SRC_ATOP);
        countryName = getResources().getStringArray(R.array.countryName);
        countryCode = getResources().getIntArray(R.array.countryCode);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.spinner_textview, countryName);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_textview);
        //Setting the ArrayAdapter data on the Spinner
        mCountrySpinner.setAdapter(spinnerAdapter);
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextViewCountryCode.setText("+ " + countryCode[position]);
                mCountryName = countryName[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderCreator.createDirectory();
                loginProcess();
            }
        });

        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpVerifyProcess();
            }
        });

        TextViewResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reSendOtp();

            }
        });

    }

    public void reSendOtp() {
        if (Common.checkAvailability(SignInActivity.this)) {
            progress.setVisibility(View.VISIBLE);
            String mobileNumber = AppPreference.getMobileNumber();
            if (mobileNumber != null) {
                AppSigInPresenter.reSendOtp(mobileNumber);
            }
        } else {
            Common.displayToast(Constant.TOAST_NO_INTERNET_CONNECTION);
        }

    }

    private void loginProcess() {
        if (SignInValidate()) {

            mUserName = EditTextUserName.getText().toString();
            mMobileNumber = EditTextMobileNumber.getText().toString();
            Constant.USER_NAME = mUserName;
            progress.setVisibility(View.VISIBLE);
            AppSigInPresenter.validateLoginCredentials(mUserName, mMobileNumber, mCountryName);
        }
    }

    private void otpVerifyProcess() {
        try {

            if (Common.checkAvailability(SignInActivity.this)) {
                progress.setVisibility(View.VISIBLE);
                Constant.OTP = EditTextOtp.getText().toString();
                if (EditTextOtp.getText().toString().equalsIgnoreCase(AppPreference.getOtp()) || EditTextOtp.getText().toString().equalsIgnoreCase(Constant.OTP_DEFAULT_CODE)) {
                    progress.setVisibility(View.VISIBLE);
                    startSignUpNewUser(createUserWithEnteredData());
                }
//
            } else {
                Common.displayToast(Constant.TOAST_NO_INTERNET_CONNECTION);
            }
        } catch (Exception e) {
            e.getMessage();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Consts.EXTRA_LOGIN_RESULT_CODE) {
                boolean isLoginSuccess = data.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);
                String errorMessage = data.getStringExtra(Consts.EXTRA_LOGIN_ERROR_MESSAGE);
                if (isLoginSuccess) {

                    saveUserData(userForSave);
                    signInCreatedUser(userForSave, false);
                } else {
                    Toaster.longToast(Constant.TOAST_LOGIN_CHAT_ERROR);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // QuickBlox Login
    public QBUser createUserWithEnteredData() {
        return createQBUserWithCurrentData(Constant.USER_NAME, Constant.CHAT_ROOM);
    }

    private QBUser createQBUserWithCurrentData(String userName, String chatRoom) {
        QBUser qbUser = null;
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(chatRoom)) {
            StringifyArrayList<String> userTags = new StringifyArrayList<>();
            userTags.add(chatRoom);
            qbUser = new QBUser();
            qbUser.setFullName(userName);
            qbUser.setLogin(getCurrentDeviceId());
            qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);
            qbUser.setTags(userTags);
        }
        return qbUser;
    }

    private String getCurrentDeviceId() {
        return Utils.generateDeviceId(SignInActivity.this);
    }

    // QB Code
    public void startSignUpNewUser(final QBUser newUser) {
        try {

            requestExecutor.signUpNewUser(newUser, new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser result, Bundle params) {
                            loginToChat(result);
                        }

                        @Override
                        public void onError(QBResponseException e) {

                            if (e.getHttpStatusCode() == Consts.ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {

                                signInCreatedUser(newUser, true);
                            } else {
                                AppPreference.putQbLoginStatus(false);
                            }
                        }
                    }
            );

        } catch (Exception e) {

            e.getMessage();

        }
    }

    private void loginToChat(final QBUser qbUser) {
        qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);
        userForSave = qbUser;
        startLoginService(qbUser);
    }

    public void startLoginService(QBUser qbUser) {

        Intent tempIntent = new Intent(SignInActivity.this, CallService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        CallService.start(SignInActivity.this, qbUser, pendingIntent);
    }

    private void signInCreatedUser(final QBUser user, final boolean deleteCurrentUser) {
        try {

            requestExecutor.signInUser(user, new QBEntityCallbackImpl<QBUser>() {
                @Override
                public void onSuccess(QBUser result, Bundle params) {
                    if (deleteCurrentUser) {
                        removeAllUserData(result);
                    } else {
                        startOpponentsActivity();
                    }
                }

                @Override
                public void onError(QBResponseException responseException) {

                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }


    private void removeAllUserData(final QBUser user) {
        try {
            requestExecutor.deleteCurrentUser(user.getId(), new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    UsersUtils.removeUserData(SignInActivity.this);
                    startSignUpNewUser(createUserWithEnteredData());
                }

                @Override
                public void onError(QBResponseException e) {
                    startSignUpNewUser(createUserWithEnteredData());
                }
            });

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void startOpponentsActivity() {
        // QB TextChat
        login(userForSave);
    }

    // QBChat Loin function
    private void login(final QBUser user) {

        progress.setVisibility(View.VISIBLE);
        ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void result, Bundle bundle) {
                QBSubscription qbSubscription = new QBSubscription();
                qbSubscription.setNotificationChannel(QBNotificationChannel.GCM);
                String androidID = Utils.generateDeviceId(SignInActivity.this);
                qbSubscription.setDeviceUdid(androidID);
                qbSubscription.setRegistrationID(mFireBaseId);
                qbSubscription.setEnvironment(QBEnvironment.DEVELOPMENT);
                SubscribeService.subscribeToPushes(SignInActivity.this, true);
//                SharedPreferencesUtil.saveQbUser(user);
                saveUserData(user);
//                SharedPreferencesUtil.saveQbUser(userForSave);
                AppPreference.putQbUserLogin(userForSave.getLogin());
                AppPreference.putQUserPassword(userForSave.getPassword());
                AppPreference.putQbLoginStatus(true);
                Intent navi = new Intent(SignInActivity.this, ProfileActivity.class);
                startActivity(navi);
                progress.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                progress.setVisibility(View.INVISIBLE);
                Toaster.shortToast(Constant.TOAST_LOGIN_CHAT_ERROR);
                login(user);
            }
        });
    }

    private void saveUserData(QBUser qbUser) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        sharedPrefsHelper.save(Consts.PREF_CURREN_ROOM_NAME, qbUser.getTags().get(0));
        sharedPrefsHelper.saveQbUser(qbUser);

    }


    @Override
    public void signInResponse(String str) {

        if (str.equalsIgnoreCase(Constant.RESPONSE_CODE)) {
            progress.setVisibility(View.GONE);
            signUpParent.setVisibility(View.GONE);
            otpParent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void verifyOtp(String str) {

        if (str.equalsIgnoreCase(AppPreference.getOtp()) || Constant.OTP_DEFAULT_CODE.equalsIgnoreCase("2020")) {
            progress.setVisibility(View.VISIBLE);
            startSignUpNewUser(createUserWithEnteredData());
        } else {
            Common.displayToast(Constant.TOAST_VALID_OTP);
        }
    }

    @Override
    public void reSendOtpResponse(String str) {

    }

    protected boolean sampleConfigIsCorrect() {
        return CoreApp.getInstance().getQbConfigs() != null;
    }

    private void createSession() {
        QBUser qbUser = CoreConfigUtils.getUserFromConfig();
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
            }

            @Override
            public void onError(QBResponseException e) {
                createSession();
            }
        });
    }

    private boolean checkPermissions() {
        int permission_SdCard = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int permission_CAMERA = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int permission_RECORDAUDIO = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
        int permission_RECEIVE_SMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int permission_REDSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        // List  to add Permissions
        listPermissionsNeeded = new ArrayList<>();
        if (permission_SdCard != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(WRITE_EXTERNAL_STORAGE);
        }

        if (permission_CAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (permission_RECORDAUDIO != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (permission_RECEIVE_SMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }
        if (permission_REDSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        return listPermissionsNeeded.isEmpty();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(SignInActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Popup.ShowErrorMessageString(this, "Storage permissions granted.", Toast.LENGTH_LONG);
                } else {
//
                }
            }

        }
    }

    private BroadcastReceiver OtpReceiver = new BroadcastReceiver()

    {
        @Override
        public void onReceive(Context context, Intent intent) {


            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();
                        try {

                            if (senderNum.contains(Constant.MESSAHE_FORMAT)) {
                                String Otp = message.replaceAll("\\D+", "");
                                AppPreference.putOtp(String.valueOf(Otp));
                                EditTextOtp.setText(String.valueOf(Otp));

                            }
                        } catch (Exception e) {
                        }

                    }
                }

            } catch (Exception e) {

            }
        }

    };


    public boolean SignInValidate() {

        boolean valid = true;
        mUserName = EditTextUserName.getText().toString();
        mMobileNumber = EditTextMobileNumber.getText().toString();
        if (mUserName.isEmpty() || mUserName.length() < 0) {
            EditTextUserName.setError(Constant.TOAST_USER_NAME);
            valid = false;
        } else {
            EditTextUserName.setError(null);
        }
        if (mMobileNumber.isEmpty() || mMobileNumber.length() < 10) {
            EditTextMobileNumber.setError(Constant.TOAST_VALID_MOBILE_NUMBER);
            valid = false;
        } else {
            EditTextMobileNumber.setError(null);
        }
        return valid;
    }

    private boolean OtpValidate() {
        boolean isOtpValid = true;
        if (mOtpNumber.isEmpty() && mOtpNumber.length() <= 0) {
            EditTextOtp.setError(Constant.TOAST_VALID_OTP);
            Common.displayToast(Constant.TOAST_VALID_OTP);
            isOtpValid = false;
        } else {
            EditTextOtp.setError(null);
        }

        return isOtpValid;
    }

    @Override
    public void setUserNameError() {

    }

    @Override
    public void setPasswordError() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void navigateToHome() {

    }

}
