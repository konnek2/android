package com.quickblox.sample.groupchatwebrtc.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.quickblox.sample.core.utils.UiUtils;
import com.quickblox.sample.groupchatwebrtc.R;
import com.quickblox.sample.groupchatwebrtc.activities.CallActivity;
import com.quickblox.sample.groupchatwebrtc.utils.CollectionsUtils;
import com.quickblox.sample.groupchatwebrtc.utils.Common;
import com.quickblox.sample.groupchatwebrtc.utils.DialogUtil;
import com.quickblox.sample.groupchatwebrtc.utils.MysensorManager;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tereha on 25.05.16.
 */
public class AudioConversationFragment extends BaseConversationFragment implements CallActivity.OnChangeDynamicToggle {
    private static final String TAG = AudioConversationFragment.class.getSimpleName();

    private ToggleButton audioSwitchToggleButton;
    private TextView alsoOnCallText;
    private TextView firstOpponentNameTextView;
    private TextView otherOpponentsTextView;
    private boolean headsetPlugged;

    // Indicator with Sensor

    ImageView signal_indicator;
    private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;
    private ConnectionClassManager mConnectionClassManager;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    boolean IsVedioCall_l = false;
    TextView KbSpeedDisplay;
    public String createdAt;
    public String CALL_TYPE;
    boolean flag = true;
    MysensorManager mysensorManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        conversationFragmentCallbackListener.addOnChangeDynamicToggle(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mysensorManager = new MysensorManager(getActivity());
        mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void configureOutgoingScreen() {
        outgoingOpponentsRelativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background_fragment));
        allOpponentsTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        ringingTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
    }

    @Override
    protected void configureToolbar() {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        toolbar.setSubtitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
    }

    @Override
    protected void configureActionBar() {
        actionBar.setTitle(currentUser.getTags().get(0));
        actionBar.setSubtitle(String.format(getString(R.string.subtitle_text_logged_in_as), currentUser.getFullName()));
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);
        signal_indicator = (ImageView) view.findViewById(R.id.signal_indicator);
        KbSpeedDisplay = (TextView) view.findViewById(R.id.kbSpeed1);
        timerChronometer = (Chronometer) view.findViewById(R.id.chronometer_timer_audio_call);

        ImageView firstOpponentAvatarImageView = (ImageView) view.findViewById(R.id.image_caller_avatar);
//        firstOpponentAvatarImageView.setBackgroundDrawable(UiUtils.getColorCircleDrawable(opponents.get(0).getId()));

        alsoOnCallText = (TextView) view.findViewById(R.id.text_also_on_call);
        setVisibilityAlsoOnCallTextView();

        firstOpponentNameTextView = (TextView) view.findViewById(R.id.text_caller_name);
        firstOpponentNameTextView.setText(opponents.get(0).getFullName());

        otherOpponentsTextView = (TextView) view.findViewById(R.id.text_other_inc_users);
        otherOpponentsTextView.setText(getOtherOpponentsNames());

        audioSwitchToggleButton = (ToggleButton) view.findViewById(R.id.toggle_speaker);
        audioSwitchToggleButton.setVisibility(View.VISIBLE);

        actionButtonsEnabled(false);
    }

    public void ShowIndicator() {


        mDeviceBandwidthSampler.startSampling();
        if (!isStarted) {

            timerChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {

                    if (flag) {
                        signal_indicator.setVisibility(View.VISIBLE);
                        flag = false;
                    } else {
                        signal_indicator.setVisibility(View.INVISIBLE);
                        flag = true;
                    }
                    applyIndicator(mConnectionClassManager.getDownloadKBitsPerSecond());

                }
            });

        }


    }

    private void applyIndicator(double downloadKBitsPerSecond) {
        try {

            if (IsVedioCall_l) {
                KbSpeedDisplay.setText("Kbps  :" + String.valueOf(Math.round(downloadKBitsPerSecond)));
//                Log.d("BaseLogedUserActivity ", "applyIndicator()  " + downloadKBitsPerSecond);
                createdAt = Common.getDateStr(new Date());
                if (downloadKBitsPerSecond < 100) {
                    signal_indicator.setBackgroundResource(R.drawable.signal_read);
                    //red
                } else if (downloadKBitsPerSecond < 500) {
                    //yellow
                    signal_indicator.setBackgroundResource(R.drawable.signal_yellow);
                } else {
                    //green
                    signal_indicator.setBackgroundResource(R.drawable.signal_green);
                }
            } else {

                KbSpeedDisplay.setText("Kbps  :" + String.valueOf(Math.round(downloadKBitsPerSecond)));
                createdAt = Common.getDateStr(new Date());
                if (downloadKBitsPerSecond < 10) {
                    signal_indicator.setBackgroundResource(R.drawable.signal_read);
                    //red
                } else if (downloadKBitsPerSecond < 20) {
                    //yellow
                    signal_indicator.setBackgroundResource(R.drawable.signal_yellow);
                } else {
                    //green
                    signal_indicator.setBackgroundResource(R.drawable.signal_green);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setVisibilityAlsoOnCallTextView() {
        if (opponents.size() < 2) {
            alsoOnCallText.setVisibility(View.INVISIBLE);
        }
    }

    private String getOtherOpponentsNames() {
        ArrayList<QBUser> otherOpponents = new ArrayList<>();
        otherOpponents.addAll(opponents);
        otherOpponents.remove(0);

        return CollectionsUtils.makeStringFromUsersFullNames(otherOpponents);
    }

    @Override
    public void onStop() {
        super.onStop();
        conversationFragmentCallbackListener.removeOnChangeDynamicToggle(this);
    }

    @Override
    protected void initButtonsListener() {
        super.initButtonsListener();

        audioSwitchToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversationFragmentCallbackListener.onSwitchAudio();
            }
        });
    }

    @Override
    protected void actionButtonsEnabled(boolean inability) {
        super.actionButtonsEnabled(inability);
        if (!headsetPlugged) {
            audioSwitchToggleButton.setEnabled(inability);
        }
        audioSwitchToggleButton.setActivated(inability);
    }

    @Override
    int getFragmentLayout() {
        return R.layout.fragment_audio_conversation;
    }

    @Override
    public void onOpponentsListUpdated(ArrayList<QBUser> newUsers) {
        super.onOpponentsListUpdated(newUsers);
        firstOpponentNameTextView.setText(opponents.get(0).getFullName());
        otherOpponentsTextView.setText(getOtherOpponentsNames());
    }

    @Override
    public void enableDynamicToggle(boolean plugged, boolean previousDeviceEarPiece) {
        headsetPlugged = plugged;

        if (isStarted) {
            audioSwitchToggleButton.setEnabled(!plugged);

            if (plugged) {
                audioSwitchToggleButton.setChecked(true);
            }else if(previousDeviceEarPiece){
                audioSwitchToggleButton.setChecked(true);
            } else {
                audioSwitchToggleButton.setChecked(false);
            }
        }
    }
}
