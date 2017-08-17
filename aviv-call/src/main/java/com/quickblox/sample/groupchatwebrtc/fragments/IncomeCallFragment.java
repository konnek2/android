package com.quickblox.sample.groupchatwebrtc.fragments;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quickblox.chat.QBChatService;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.core.utils.UiUtils;
import com.quickblox.sample.groupchatwebrtc.App;
import com.quickblox.sample.groupchatwebrtc.db.QbUsersDbManager;
import com.quickblox.sample.groupchatwebrtc.model.CallLogModel;
import com.quickblox.sample.groupchatwebrtc.utils.Common;
import com.quickblox.sample.groupchatwebrtc.utils.Constant;
import com.quickblox.sample.groupchatwebrtc.utils.DialogUtil;
import com.quickblox.sample.groupchatwebrtc.utils.FolderCreator;
import com.quickblox.sample.groupchatwebrtc.utils.RingtonePlayer;
import com.quickblox.sample.groupchatwebrtc.R;
import com.quickblox.sample.groupchatwebrtc.utils.CollectionsUtils;
import com.quickblox.sample.groupchatwebrtc.utils.UsersUtils;
import com.quickblox.sample.groupchatwebrtc.utils.WebRtcSessionManager;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * QuickBlox team
 */
public class IncomeCallFragment extends Fragment implements Serializable, View.OnClickListener {

    private static final String TAG = IncomeCallFragment.class.getSimpleName();
    private static final long CLICK_DELAY = TimeUnit.SECONDS.toMillis(2);
    private ImageButton rejectButton;
    private ImageButton takeButton;
    private ImageView callerAvatarImageView;
    private List<Integer> opponentsIds;
    private Vibrator vibrator;
    private QBRTCTypes.QBConferenceType conferenceType;
    private long lastClickTime = 0l;
    private RingtonePlayer ringtonePlayer;
    private IncomeCallFragmentCallbackListener incomeCallFragmentCallbackListener;
    private QBRTCSession currentSession;
    private QbUsersDbManager qbUserDbManager;
    private TextView alsoOnCallText;

    // konnek2 Code
    SharedPrefsHelper sharedPrefsHelper;
    private QBUser currentUser;
    Map<String, String> userInfo;
    private String platform;
    private CallLogModel callLogModel;
    private ArrayList<CallLogModel> callLogModelList;
    private String callTypes;
    private String opponentName, currentUserName;
    ArrayList<QBUser> usersFromDb;
    private TextView callTypeTextView, callPriorityTextview;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            incomeCallFragmentCallbackListener = (IncomeCallFragmentCallbackListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCallEventsController");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_call, container, false);

        initFields();
        hideToolBar();

        if (currentSession != null) {
            initUI(view);
            setDisplayedTypeCall(conferenceType);
            initButtonsListener();
        }

        ringtonePlayer = new RingtonePlayer(getActivity());
        return view;
    }

    private void initFields() {
        currentSession = WebRtcSessionManager.getInstance(getActivity()).getCurrentSession();
        qbUserDbManager = QbUsersDbManager.getInstance(getActivity().getApplicationContext());
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        currentUser = sharedPrefsHelper.getQbUser();
        currentUserName = currentUser.getFullName();

        if (currentSession != null) {
            opponentsIds = currentSession.getOpponents();
            conferenceType = currentSession.getConferenceType();
            Log.d(TAG, conferenceType.toString() + "From onCreateView()");
        }

    }

    public void hideToolBar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_call);
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        startCallNotification();
    }

    private void initButtonsListener() {
        rejectButton.setOnClickListener(this);
        takeButton.setOnClickListener(this);
    }

    private void initUI(View view) {
        callTypeTextView = (TextView) view.findViewById(R.id.call_type);

        callerAvatarImageView = (ImageView) view.findViewById(R.id.image_caller_avatar);
        TextView callerNameTextView = (TextView) view.findViewById(R.id.text_caller_name);

        QBUser callerUser = qbUserDbManager.getUserById(currentSession.getCallerID());
        callerNameTextView.setText(UsersUtils.getUserNameOrId(callerUser, currentSession.getCallerID()));
        opponentName = UsersUtils.getUserNameOrId(callerUser, currentSession.getCallerID());

//        userInfo = currentSession.getUserInfo();
//        platform = userInfo.get("CallStatus");
//        if (platform != null) {
//            callPriorityTextview.setText(platform);
//
//        } else {
//            callPriorityTextview.setText("");
//        }


        TextView otherIncUsersTextView = (TextView) view.findViewById(R.id.text_other_inc_users);
        String str = UsersUtils.getUserNameOrId(callerUser, currentSession.getCallerID());

        String cap = str.substring(0, 1).toUpperCase() + str.substring(1);
        usersFromDb = qbUserDbManager.getUsersByIds(opponentsIds);

        if (usersFromDb.size() < 2) {

//            otherIncUsersTextView.setText(getOtherIncUsersNames() + " , " + cap);
            callTypeTextView.setText(getResources().getString(R.string.text_audio__call));
        } else {
            callTypeTextView.setText(getResources().getString(R.string.text_audio_conference_call));
            otherIncUsersTextView.setText(getOtherIncUsersNames() + " ," + cap);
        }

        alsoOnCallText = (TextView) view.findViewById(R.id.text_also_on_call);
        setVisibilityAlsoOnCallTextView();

        rejectButton = (ImageButton) view.findViewById(R.id.image_button_reject_call);
        takeButton = (ImageButton) view.findViewById(R.id.image_button_accept_call);



        Uri imageUri = Uri.fromFile(FolderCreator.getImageFileFromSdCard(String.valueOf(currentSession.getCallerID())));
        Glide.with(getActivity())
                .load(imageUri)
                .placeholder(R.drawable.ic_chat_face)
                .into(callerAvatarImageView);
    }

    private void setVisibilityAlsoOnCallTextView() {
        if (opponentsIds.size() < 2) {
            alsoOnCallText.setVisibility(View.INVISIBLE);
        }
    }

    private Drawable getBackgroundForCallerAvatar(int callerId) {
        return UiUtils.getColorCircleDrawable(callerId);
    }

    public void startCallNotification() {

        ringtonePlayer.play(false);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        long[] vibrationCycle = {0, 1000, 1000};
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationCycle, 1);
        }

    }

    private void stopCallNotification() {

        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private String getOtherIncUsersNames() {
        usersFromDb = qbUserDbManager.getUsersByIds(opponentsIds);
        ArrayList<QBUser> opponents = new ArrayList<>();
        opponents.addAll(UsersUtils.getListAllUsersFromIds(usersFromDb, opponentsIds));
        opponents.remove(QBChatService.getInstance().getUser());
        opponents.remove(currentSession.getCallerID());
        return CollectionsUtils.makeStringFromUsersFullNames(opponents);
    }

    private void setDisplayedTypeCall(QBRTCTypes.QBConferenceType conferenceType) {
        boolean isVideoCall = conferenceType == QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

        if (isVideoCall) {


            callTypeTextView.setText(getResources().getString(R.string.text_video__call));
        }

    }

    @Override
    public void onStop() {
        stopCallNotification();
        super.onStop();

    }

    @Override
    public void onClick(View v) {

        if ((SystemClock.uptimeMillis() - lastClickTime) < CLICK_DELAY) {
            return;
        }
        lastClickTime = SystemClock.uptimeMillis();

        int i = v.getId();
        if (i == R.id.image_button_reject_call) {
            reject();

        } else if (i == R.id.image_button_accept_call) {
            accept();

        } else {
        }
    }

    private void accept() {
        try {
            enableButtons(false);
            stopCallNotification();
            incomeCallFragmentCallbackListener.onAcceptCurrentSession();
            callLogModel.setCallUserName(currentUserName);
            callLogModel.setCallOpponentName(opponentName);
            callLogModel.setCallDate(Common.currentDate());
            callLogModel.setCallTime(Common.currentTime());
            callLogModel.setCallType(callTypes);
            callLogModel.setCallPriority(platform);
            callLogModel.setCallStatus(Constant.CALL_STATUS_RECEIVED);
            callLogModelList.add(callLogModel);
            App.callLogTableDAO.saveCallLog(callLogModelList);

        } catch (Exception e) {
            e.getMessage();
        }


    }

    private void reject() {

        try {


            ringtonePlayer.stop();
            enableButtons(false);
            stopCallNotification();
            incomeCallFragmentCallbackListener.onRejectCurrentSession();
            callLogModel.setCallUserName(currentUserName);
            callLogModel.setCallOpponentName(opponentName);
            callLogModel.setCallDate(Common.currentDate());
            callLogModel.setCallTime(Common.currentTime());
            callLogModel.setCallType(callTypes);
            callLogModel.setCallPriority(platform);
            callLogModel.setCallStatus(Constant.CALL_STATUS_REJECTED);
            callLogModelList.add(callLogModel);
            App.callLogTableDAO.saveCallLog(callLogModelList);

        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void enableButtons(boolean enable) {
        takeButton.setEnabled(enable);
        rejectButton.setEnabled(enable);
    }
}
