package com.quickblox.sample.groupchatwebrtc.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.sample.groupchatwebrtc.App;
import com.quickblox.sample.groupchatwebrtc.R;
import com.quickblox.sample.groupchatwebrtc.activities.CallActivity;
import com.quickblox.sample.groupchatwebrtc.activities.LoginActivity;
import com.quickblox.sample.groupchatwebrtc.activities.OpponentsActivity;
import com.quickblox.sample.groupchatwebrtc.activities.SettingsActivity;
import com.quickblox.sample.groupchatwebrtc.adapters.OpponentsAdapter;
import com.quickblox.sample.groupchatwebrtc.db.QbUsersDbManager;
import com.quickblox.sample.groupchatwebrtc.model.CallLogModel;
import com.quickblox.sample.groupchatwebrtc.services.CallService;
import com.quickblox.sample.groupchatwebrtc.util.QBResRequestExecutor;
import com.quickblox.sample.groupchatwebrtc.utils.CollectionsUtils;
import com.quickblox.sample.groupchatwebrtc.utils.Common;
import com.quickblox.sample.groupchatwebrtc.utils.Constant;
import com.quickblox.sample.groupchatwebrtc.utils.Consts;
import com.quickblox.sample.groupchatwebrtc.utils.DialogUtil;
import com.quickblox.sample.groupchatwebrtc.utils.PermissionsChecker;
import com.quickblox.sample.groupchatwebrtc.utils.RingtonePlayer;
import com.quickblox.sample.groupchatwebrtc.utils.UsersUtils;
import com.quickblox.sample.groupchatwebrtc.utils.WebRtcSessionManager;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment implements CallLogInterface {

    private static final String TAG = ContactFragment.class.getSimpleName();
    private OpponentsAdapter opponentsAdapter;
    private ListView opponentsListView;
    private QBUser currentUser;
    private ArrayList<QBUser> currentOpponentsList;
    private QbUsersDbManager dbManager;
    private WebRtcSessionManager webRtcSessionManager;
    SharedPrefsHelper sharedPrefsHelper;
    protected QBResRequestExecutor requestExecutor;
    private PermissionsChecker checker;
    private String user;
    private boolean isRunForCall;
    private RadioButton highPriority, mediumPriority, lowPriority, other;
    private RadioGroup priorityGroup;
    private TextView okTxt, cancelTxt;
    private EditText otherEditTxt;
    private android.app.AlertDialog alertDialog;
    private ProgressBar progress;
    private String CallingPriority, CallingPrioritytxt;
    private DialogUtil dialogUtil;
    Map<String, String> userInfo;
    ProgressBar contactProgressBar;
    LinearLayout contactLayout1, contactLayout2;
    private CallLogModel callLogModel;
    private ArrayList<CallLogModel> callLogModelList;
    private String ContactName, ContactTime, ContactDate, ContactCallStatus, callType;
    private ArrayList<Integer> opponentsList;
    MenuItem menuItemVideoCall;


    public static void start(Context context, boolean isRunForCall) {
        Log.d("CALL2020", "ContactFragment  start methodTop");
        Intent intent = new Intent(context, OpponentsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, isRunForCall);
        context.startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_opponents, container, false);
        opponentsListView = (ListView) view.findViewById(R.id.list_opponents);
        setHasOptionsMenu(true);
        initUi(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            sharedPrefsHelper = SharedPrefsHelper.getInstance();
            currentUser = sharedPrefsHelper.getQbUser();
            requestExecutor = App.getInstance().getQbResRequestExecutor();
            dbManager = QbUsersDbManager.getInstance(getActivity());
            webRtcSessionManager = WebRtcSessionManager.getInstance(getActivity());
            initFields();
            if (Common.checkAvailability(getActivity())) {
//                Toaster.shortToast("SnakerBar Hide");
                startLoadUsers();
            } else {
//                Toaster.shortToast("SnakerBar Show");
            }
            if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
                CallActivity.start(getActivity(), true);
            }
            checker = new PermissionsChecker(getActivity());
        } catch (Exception e) {
            e.getMessage();
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            Log.d(TAG, "CONonViewCreated ");
            startLoadUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFields() {
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            isRunForCall = extras.getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
        }
        currentUser = sharedPrefsHelper.getQbUser();
        dbManager = QbUsersDbManager.getInstance(getActivity());
        webRtcSessionManager = WebRtcSessionManager.getInstance(getActivity());
    }

    private void initUi(View view) {

        getActivity().registerReceiver(callTrig, new IntentFilter(Constant.NOTIFY_CALL_TRIGER));
        callLogModel = new CallLogModel();
        callLogModelList = new ArrayList<CallLogModel>();
        contactProgressBar = (ProgressBar) view.findViewById(R.id.contactPrograss);
        contactLayout1 = (LinearLayout) view.findViewById(R.id.contactFragmentLayout1);
        contactLayout2 = (LinearLayout) view.findViewById(R.id.contactFragmentLayout2);
        opponentsListView = (ListView) view.findViewById(R.id.list_opponents);
    }

    public void startLoadUsers() {

        try {
            contactLayout2.setVisibility(View.GONE);
            contactLayout1.setVisibility(View.VISIBLE);
            String currentRoomName = SharedPrefsHelper.getInstance().get(Consts.PREF_CURREN_ROOM_NAME);
            Log.d("ContactsFragment ", "currentRoomName  " + currentRoomName);
            requestExecutor.loadUsersByTag(currentRoomName, new QBEntityCallback<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                    Log.d("ContactsFragment ", " startLoadUsers ==> loadUsersByTag   onSuccess size " + result.get(0).getPassword());
                    contactLayout2.setVisibility(View.VISIBLE);
                    contactLayout1.setVisibility(View.GONE);
                    dbManager.saveAllUsers(result, true);
                    initUsersList();
                }

                @Override
                public void onError(QBResponseException responseException) {
                    Log.d("ContactsFragment ", "loadUsersByTag   onError " + responseException.getMessage());
                    contactLayout2.setVisibility(View.VISIBLE);
                    contactLayout1.setVisibility(View.GONE);
                    responseException.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initUsersList();
    }

    private void initUsersList() {
//      checking whether currentOpponentsList is actual, if yes - return
        if (currentOpponentsList != null) {
            Log.d("ContactsFragment ", "initUsersList   getAllUsers  " + dbManager.getAllUsers());
            ArrayList<QBUser> actualCurrentOpponentsList = dbManager.getAllUsers();

            actualCurrentOpponentsList.remove(sharedPrefsHelper.getQbUser());
            if (isCurrentOpponentsListActual(actualCurrentOpponentsList)) {
                return;
            }
        }
        proceedInitUsersList();
    }

    private boolean isCurrentOpponentsListActual(ArrayList<QBUser> actualCurrentOpponentsList) {
        boolean equalActual = actualCurrentOpponentsList.retainAll(currentOpponentsList);
        boolean equalCurrent = currentOpponentsList.retainAll(actualCurrentOpponentsList);
        return !equalActual && !equalCurrent;
    }

    private void proceedInitUsersList() {
        dbManager = QbUsersDbManager.getInstance(getActivity());
        if (dbManager.getAllUsers().size() != 0 || dbManager.getAllUsers() != null) {
            Log.d("GETALLUSER", "proceedInitUsersList  IF  !=null  " + dbManager.getAllUsers().size());
            currentOpponentsList = dbManager.getAllUsers();
            currentOpponentsList.remove(sharedPrefsHelper.getQbUser());
            opponentsAdapter = new OpponentsAdapter(getActivity(), currentOpponentsList, this);
            opponentsAdapter.setSelectedItemsCountsChangedListener(new OpponentsAdapter.SelectedItemsCountsChangedListener() {
                @Override
                public void onCountSelectedItemsChanged(int count) {
                    getActivity().invalidateOptionsMenu();
                    Log.d(TAG, "SelectedItems count  " + count);
                    updateActionBar(count);
                }
            });
            opponentsListView.setAdapter(opponentsAdapter);
        } else {
            Log.d(TAG, "proceedInitUsersList  ELSE !=null  " + dbManager.getAllUsers().size());
            opponentsListView.setVisibility(View.INVISIBLE);
        }
    }

    private void updateActionBar(int countSelectedUsers) {
        if (countSelectedUsers < 1) {
        } else {
//            initActionBarWithSelectedUsers(countSelectedUsers);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (opponentsAdapter != null && !opponentsAdapter.getSelectedItems().isEmpty()) {
            inflater.inflate(R.menu.activity_selected_opponents, menu);

            menuItemVideoCall = menu.findItem(R.id.start_video_call);
            if (opponentsAdapter.getSelectedItems().size() < 2) {
                Log.d(TAG, "onCreateOptionsMenu IF  1 ");
                menuItemVideoCall.setVisible(true);
            } else {
                Log.d(TAG, "onCreateOptionsMenu  ELSE   2");
                menuItemVideoCall.setVisible(false);
            }
        } else {

            inflater.inflate(R.menu.activity_opponents, menu);
        }


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.update_opponents_list) {

            if (Common.checkAvailability(getActivity())) {
//                Toaster.shortToast("SnackBarHide");
                startLoadUsers();
            } else {
//                Toaster.shortToast("SnackBarShow");
            }
            return true;

        }
//        else if (id == R.id.settings)
//        {
//            showSettings();
//            return true;
//        } else if (id == R.id.log_out)
//        {
//            logOut();
//            return true;
//        }

        else if (id == R.id.start_video_call) {
            if (Common.checkAvailability(getActivity())) {

//                Toaster.shortToast("SnackBarHide");
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toaster.longToast("Please Enable Audio and Camera Permission");
                } else {
                    if (isLoggedInChat() && opponentsAdapter.getSelectedItems().size() <= Consts.MAX_OPPONENTS_VIDEO_COUNT) {
                        intentCalling(true);
                    } else {
                        Toaster.shortToast(" One user only allowed to make a  video call ");
                    }
                }

            } else {
//                Toaster.shortToast("SnackBarShow");
            }
            return true;
        } else if (id == R.id.start_audio_call) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                Toaster.longToast("Please Enable Audio and Camera Permission");
            } else {
                if (isLoggedInChat() && opponentsAdapter.getSelectedItems().size() <= Consts.MAX_OPPONENTS_COUNT) {
                    intentCalling(false);
                } else {
                    Toaster.shortToast(" 1 Members  only allowed to make a  video call ");
                }
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean isLoggedInChat() {
        if (!QBChatService.getInstance().isLoggedIn()) {
            Toaster.shortToast(R.string.dlg_signal_error);
            tryReLoginToChat();
            return false;
        }
        return true;
    }

    private void tryReLoginToChat() {
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            CallService.start(getActivity(), qbUser);
        }
    }

    private void showSettings() {
        SettingsActivity.start(getActivity());
    }

    private void logOut() {
        unsubscribeFromPushes();
        startLogoutCommand();
        removeAllUserData();
        startLoginActivity();
    }

    private void unsubscribeFromPushes() {
        SubscribeService.unSubscribeFromPushes(getActivity());
    }

    private void startLogoutCommand() {
        CallService.logout(getActivity());
    }

    private void removeAllUserData() {
        UsersUtils.removeUserData(getActivity());
        requestExecutor.deleteCurrentUser(currentUser.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.d(TAG, "Current user was deleted from QB ");
            }

            @Override
            public void onError(QBResponseException e) {
//                Log.e(TAG, "Current user wasn't deleted from QB " + e);
            }
        });
    }

    private void startLoginActivity() {
        LoginActivity.start(getActivity());
        getActivity().finish();
    }

    public void intentCalling(final boolean callType) {

        Log.d(TAG, "intentCalling callType " + callType);
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getContext());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.intent_calling, null);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.item_intent_calling, null);
        progress = (ProgressBar) view.findViewById(R.id.intentCalling_progress);
        otherEditTxt = (EditText) view.findViewById(R.id.otherEtxt);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        priorityGroup = (RadioGroup) view.findViewById(R.id.PriorityGroup);
        highPriority = (RadioButton) view.findViewById(R.id.HighPriority);
        mediumPriority = (RadioButton) view.findViewById(R.id.MediumPriority);
        lowPriority = (RadioButton) view.findViewById(R.id.LowPriority);
        okTxt = (TextView) view.findViewById(R.id.RadioOk);
        cancelTxt = (TextView) view.findViewById(R.id.RadioCancel);
        priorityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    int id = priorityGroup.getCheckedRadioButtonId();
                    View radioButton = priorityGroup.findViewById(id);
                    int radioId = priorityGroup.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) priorityGroup.getChildAt(radioId);
                    CallingPriority = btn.getText().toString();
                    if (radioId == 3) {
                        otherEditTxt.setVisibility(View.VISIBLE);
                    } else {
                        otherEditTxt.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });

        okTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (IsValidate()) {
                        if (otherEditTxt.getText().toString().length() > 0) {
                            CallingPrioritytxt = otherEditTxt.getText().toString();
                            Constant.CALL_PRIORITY = CallingPrioritytxt;
                            Constant.CALL_TYPE = callType;
                            startCall();
                        }
                        Constant.CALL_PRIORITY = CallingPriority;
                        Constant.CALL_TYPE = callType;
                        startCall();
//                        Log.d("INCOMCALL   OKBTN", CallingPriority);
                        alertDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });
        cancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    public static void groupChatCall(List<QBUser> qbUsersList, boolean callType, int chatCall) {

        Log.d("ContactFragment", " startCall groupChatCall from chat" + qbUsersList.size());
        if (qbUsersList.size() > 1) {
            Constant.OPPONENTS = Constant.GROUP_CALL;
            Constant.USER_ID = Constant.GROUP_CALL;
        } else {
            Constant.OPPONENTS = qbUsersList.get(0).getFullName();
            Constant.USER_ID = String.valueOf(qbUsersList.get(0).getId());
        }

        Constant.QB_USER_LIST = qbUsersList;
        Constant.CALL_TYPE = callType;
        Constant.DATE = Common.currentDate();
        Constant.TIME = Common.currentTime();
        Constant.CHAT_CALL = chatCall;
        Constant.CALL_PRIORITY = Constant.CALL_PRIORITY_HIGH;

        Intent intent = new Intent(Constant.NOTIFY_CALL_TRIGER);
        App.getInstance().sendBroadcast(intent);

    }

    public void startCall() {

        Log.d("SELETED_USER", "callTrig 222   Constant.USER_ID " + Constant.USER_ID);
        try {
            Constant.STATUS = Constant.CALL_STATUS_DIALED;
            if (Constant.CALL_TYPE == true) {

                callType = Constant.CALL_VIDEO;
            } else {

                callType = Constant.CALL_AUDIO;
            }
            QBUser currentUser = Common.getCurrentUser();
            callLogModel.setCallUserName(currentUser.getFullName());
            callLogModel.setCallOpponentName(Constant.OPPONENTS);
            callLogModel.setCallDate(Constant.DATE);
            callLogModel.setCallTime(Constant.TIME);
            callLogModel.setCallStatus(Constant.STATUS);
            callLogModel.setCallPriority(Constant.CALL_PRIORITY);
            callLogModel.setCallType(callType);
            callLogModel.setUserId(Constant.USER_ID);
            callLogModelList.add(callLogModel);

            App.callLogTableDAO.saveCallLog(callLogModelList);

            userInfo = new HashMap<>();
            userInfo.put("CallStatus", Constant.CALL_PRIORITY);


            if (Constant.CHAT_CALL == Constant.CHAT_CALL_STATUS) {
                opponentsList = CollectionsUtils.getIdsSelectedOpponentsFromChat(Constant.QB_USER_LIST);
            } else {
                Log.d("ContactFragment", " CHAT_CALL ELSE ");
                opponentsList = CollectionsUtils.getIdsSelectedOpponents(opponentsAdapter.getSelectedItems());
            }

            QBRTCTypes.QBConferenceType conferenceType = Constant.CALL_TYPE
                    ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                    : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

            QBRTCClient qbrtcClient = QBRTCClient.getInstance(getActivity());
            QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
            WebRtcSessionManager.getInstance(getActivity()).setCurrentSession(newQbRtcSession);
//            PushNotificationSender.sendPushMessage(opponentsList, currentUser.getFullName());
            Log.d("RINGTONEPLAY", " Contact Fragment  startCall  ringPlay");
            Log.d(TAG, "startCall() 3 ");
            newQbRtcSession.startCall(userInfo);
            CallActivity.start(getActivity(), false);

        } catch (Exception e) {
            e.getMessage();
        }


    }

    private boolean IsValidate() {
        try {
            if (priorityGroup.getCheckedRadioButtonId() == -1) {
                Common.displayToast("Select call Priority ");
                return false;
            }
            if (otherEditTxt.getText().toString() == null || otherEditTxt.getText().toString().length() < 0) {
                Common.displayToast("Please enter message ");
                return false;
            }

        } catch (Resources.NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void selectedUser(String userName, String Date, String Time, String callStatus, String userId) {
        Log.d("SELETED_USER", "callTrig   selectedUser USER_ID   " + userId + "  USER_NAME  " + userName);

        Constant.OPPONENTS = userName;
        Constant.DATE = Date;
        Constant.TIME = Time;
        Constant.STATUS = callStatus;
        Constant.USER_ID = userId;

    }

    BroadcastReceiver callTrig = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("ContactFragment", "callTrig   BroadcastReceiver======>>>>>>>> ");
            startCall();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(callTrig);
    }
}
