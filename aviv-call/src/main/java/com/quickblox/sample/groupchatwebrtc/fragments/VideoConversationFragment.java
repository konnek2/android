package com.quickblox.sample.groupchatwebrtc.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.sample.groupchatwebrtc.R;
import com.quickblox.sample.groupchatwebrtc.activities.CallActivity;
import com.quickblox.sample.groupchatwebrtc.adapters.OpponentsFromCallAdapter;
import com.quickblox.sample.groupchatwebrtc.utils.Common;
import com.quickblox.sample.groupchatwebrtc.utils.DialogUtil;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionEventsCallback;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionStateCallback;
import com.quickblox.videochat.webrtc.view.QBRTCSurfaceView;
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack;

import org.webrtc.CameraVideoCapturer;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;


/**
 * QuickBlox team
 */
public class VideoConversationFragment extends BaseConversationFragment implements Serializable, QBRTCClientVideoTracksCallbacks,
        QBRTCSessionStateCallback, QBRTCSessionEventsCallback, OpponentsFromCallAdapter.OnAdapterEventListener {

    private static final int DEFAULT_ROWS_COUNT = 2;
    private static final int DEFAULT_COLS_COUNT = 3;
    private static final long TOGGLE_CAMERA_DELAY = 1000;
    private static final long LOCAL_TRACk_INITIALIZE_DELAY = 500;
    private static final int RECYCLE_VIEW_PADDING = 2;
    private static final long UPDATING_USERS_DELAY = 2000;
    private static final long FULL_SCREEN_CLICK_DELAY = 1000;
    private static final int REQUEST_CODE_ATTACHMENT = 100;
    private String TAG = VideoConversationFragment.class.getSimpleName();
    private ToggleButton cameraToggle;
    private View view;
    private LinearLayout actionVideoButtonsLayout;
    private QBRTCSurfaceView remoteFullScreenVideoView;
    private QBRTCSurfaceView localVideoView;
    private CameraState cameraState = CameraState.DISABLED_FROM_USER;
    private RecyclerView recyclerView;
    private SparseArray<OpponentsFromCallAdapter.ViewHolder> opponentViewHolders;
    private boolean isPeerToPeerCall;
    private QBRTCVideoTrack localVideoTrack;
    private TextView connectionStatusLocal;

    private Map<Integer, QBRTCVideoTrack> videoTrackMap;
    private OpponentsFromCallAdapter opponentsAdapter;
    private LocalViewOnClickListener localViewOnClickListener;
    private boolean isRemoteShown;

    private int amountOpponents;
    private int userIDFullScreen;
    private List<QBUser> allOpponents;
    private boolean connectionEstablished;
    private boolean allCallbacksInit;
    private boolean isCurrentCameraFront;
    private boolean isLocalVideoFullScreen;

    // Indicator

    ImageView signal_indicator;
    private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;
    private ConnectionClassManager mConnectionClassManager;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    boolean IsVedioCall_l = false;
    TextView KbSpeedDisplay;
    public String createdAt;
    public String CALL_TYPE;
    boolean flag = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
        return view;
    }

    @Override
    protected void configureOutgoingScreen() {
        outgoingOpponentsRelativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_transparent_50));
        allOpponentsTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        ringingTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
    }

    @Override
    protected void configureActionBar() {
        actionBar = ((AppCompatActivity) getActivity()).getDelegate().getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void configureToolbar() {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        toolbar.setSubtitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
    }

    @Override
    int getFragmentLayout() {
        return R.layout.fragment_video_conversation;
    }

    @Override
    protected void initFields() {
        super.initFields();
        localViewOnClickListener = new LocalViewOnClickListener();
        amountOpponents = opponents.size();
        allOpponents = Collections.synchronizedList(new ArrayList<QBUser>(opponents.size()));
        allOpponents.addAll(opponents);

        timerChronometer = (Chronometer) getActivity().findViewById(R.id.timer_chronometer_action_bar);

        isPeerToPeerCall = opponents.size() == 1;
    }

    public void ShowIndicator() {

        mDeviceBandwidthSampler.startSampling();
        if (!isStarted) {
            timerChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (flag) {
//                        signal_indicator.setVisibility(View.VISIBLE);
                        flag = false;
                    } else {
//                        signal_indicator.setVisibility(View.INVISIBLE);
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

    public void setDuringCallActionBar() {
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(currentUser.getFullName());
        if (isPeerToPeerCall) {
            actionBar.setSubtitle(getString(R.string.opponent, opponents.get(0).getFullName()));
        } else {
            actionBar.setSubtitle(getString(R.string.opponents, amountOpponents));
        }

        actionButtonsEnabled(true);
    }

    private void initVideoTrackSListener() {
        if (currentSession != null) {
            currentSession.addVideoTrackCallbacksListener(this);
        }
    }

    private void removeVideoTrackSListener() {
        if (currentSession != null) {
            currentSession.removeVideoTrackCallbacksListener(this);
        }
    }

    @Override
    protected void actionButtonsEnabled(boolean inability) {
        super.actionButtonsEnabled(inability);
        cameraToggle.setEnabled(inability);
        // inactivate toggle buttons
        cameraToggle.setActivated(inability);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        if (!allCallbacksInit) {
            conversationFragmentCallbackListener.addTCClientConnectionCallback(this);
            conversationFragmentCallbackListener.addRTCSessionEventsCallback(this);
            initVideoTrackSListener();
            allCallbacksInit = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setHasOptionsMenu(true);
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);

        opponentViewHolders = new SparseArray<>(opponents.size());
        isRemoteShown = false;
        isCurrentCameraFront = true;
        localVideoView = (QBRTCSurfaceView) view.findViewById(R.id.local_video_view);
        initCorrectSizeForLocalView();
        localVideoView.setZOrderMediaOverlay(true);

        remoteFullScreenVideoView = (QBRTCSurfaceView) view.findViewById(R.id.remote_video_view);
        remoteFullScreenVideoView.setOnClickListener(localViewOnClickListener);

        if (!isPeerToPeerCall) {
            recyclerView = (RecyclerView) view.findViewById(R.id.grid_opponents);

            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.dimen.grid_item_divider));
            recyclerView.setHasFixedSize(true);
            final int columnsCount = defineColumnsCount();
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getActivity(), HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);

//          for correct removing item in adapter
            recyclerView.setItemAnimator(null);
            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    setGrid(columnsCount);
                    recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }
        connectionStatusLocal = (TextView) view.findViewById(R.id.connectionStatusLocal);

        cameraToggle = (ToggleButton) view.findViewById(R.id.toggle_camera);
        cameraToggle.setVisibility(View.VISIBLE);

        actionVideoButtonsLayout = (LinearLayout) view.findViewById(R.id.element_set_video_buttons);

        actionButtonsEnabled(false);
        restoreSession();
    }

    private void restoreSession() {
        Log.d(TAG, "restoreSession ");
        if (currentSession.getState() != QBRTCSession.QBRTCSessionState.QB_RTC_SESSION_ACTIVE) {
            return;
        }
        onCallStarted();
        Map<Integer, QBRTCVideoTrack> videoTrackMap = getVideoTrackMap();
        if (!videoTrackMap.isEmpty()) {
            for (final Iterator<Map.Entry<Integer, QBRTCVideoTrack>> entryIterator
                 = videoTrackMap.entrySet().iterator(); entryIterator.hasNext(); ) {
                final Map.Entry<Integer, QBRTCVideoTrack> entry = entryIterator.next();

                //if connection with peer wasn't closed do restore it otherwise remove from collection
                if (currentSession.getPeerChannel(entry.getKey()).getState() !=
                        QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_CLOSED) {
                    Log.d(TAG, "execute restoreSession for user:" + entry.getKey());
                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onConnectedToUser(currentSession, entry.getKey());
                            onRemoteVideoTrackReceive(currentSession, entry.getValue(), entry.getKey());
                        }
                    }, LOCAL_TRACk_INITIALIZE_DELAY);
                } else {
                    entryIterator.remove();
                }
            }
        }
    }

    private void initCorrectSizeForLocalView() {
        ViewGroup.LayoutParams params = localVideoView.getLayoutParams();
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        int screenWidthPx = displaymetrics.widthPixels;
        Log.d(TAG, "screenWidthPx " + screenWidthPx);
        params.width = (int) (screenWidthPx * 0.3);
        params.height = (params.width / 2) * 3;
        localVideoView.setLayoutParams(params);
    }

    private void setGrid(int columnsCount) {
        int gridWidth = view.getMeasuredWidth();

        float itemMargin = getResources().getDimension(R.dimen.grid_item_divider);
        int cellSizeWidth = defineSize(gridWidth, columnsCount, itemMargin);

        opponentsAdapter = new OpponentsFromCallAdapter(getActivity(), currentSession, opponents, cellSizeWidth,
                (int) getResources().getDimension(R.dimen.item_height));
        opponentsAdapter.setAdapterListener(VideoConversationFragment.this);
        recyclerView.setAdapter(opponentsAdapter);
    }

    private Map<Integer, QBRTCVideoTrack> getVideoTrackMap() {
        if (videoTrackMap == null) {
            videoTrackMap = new HashMap<>();
        }
        return videoTrackMap;
    }

    private int defineSize(int measuredWidth, int columnsCount, float padding) {
        return measuredWidth / columnsCount - (int) (padding * 2) - RECYCLE_VIEW_PADDING;
    }

    private int defineColumnsCount() {
        return opponents.size() - 1;
    }


    @Override
    public void onResume() {
        super.onResume();

        // If user changed camera state few times and last state was CameraState.ENABLED_FROM_USER
        // than we turn on cam, else we nothing change
        if (cameraState != CameraState.DISABLED_FROM_USER) {
            toggleCamera(true);
        }
    }


    @Override
    public void onPause() {
        // If camera state is CameraState.ENABLED_FROM_USER or CameraState.NONE
        // than we turn off cam
        if (cameraState != CameraState.DISABLED_FROM_USER) {
            toggleCamera(false);
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (connectionEstablished) {
            allCallbacksInit = false;
        } else {
            Log.d(TAG, "We are in dialing process yet!");
        }
    }

    private void removeVideoTrackRenderers() {

        Map<Integer, QBRTCVideoTrack> videoTrackMap = getVideoTrackMap();
        for (QBRTCVideoTrack videoTrack : videoTrackMap.values()) {
            if (videoTrack.getRenderer() != null) {
                Log.d(TAG, "remove opponent video Tracks");
                videoTrack.removeRenderer(videoTrack.getRenderer());
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        releaseViewHolders();
        removeConnectionStateListeners();
        removeVideoTrackSListener();
        removeVideoTrackRenderers();
        releaseViews();
    }

    private void releaseViewHolders() {
        opponentViewHolders.clear();
    }

    private void removeConnectionStateListeners() {
        conversationFragmentCallbackListener.removeRTCClientConnectionCallback(this);
        conversationFragmentCallbackListener.removeRTCSessionEventsCallback(this);
    }

    private void releaseViews() {
        if (localVideoView != null) {
            localVideoView.release();
        }
        if (remoteFullScreenVideoView != null) {
            remoteFullScreenVideoView.release();
        }
        remoteFullScreenVideoView = null;
        if (!isPeerToPeerCall) {
            releseOpponentsViews();
        }
    }

    @Override
    public void onCallStopped() {
        super.onCallStopped();

    }

    protected void initButtonsListener() {
        super.initButtonsListener();

        cameraToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cameraState != CameraState.DISABLED_FROM_USER) {
                    toggleCamera(isChecked);
                }
            }
        });
    }

    private void switchCamera(final MenuItem item) {
        if (cameraState == CameraState.DISABLED_FROM_USER) {
            return;
        }
        cameraToggle.setEnabled(false);
        conversationFragmentCallbackListener.onSwitchCamera(new CameraVideoCapturer.CameraSwitchHandler() {
            @Override
            public void onCameraSwitchDone(boolean b) {

                isCurrentCameraFront = b;
                updateSwitchCameraIcon(item);
                toggleCameraInternal();
            }

            @Override
            public void onCameraSwitchError(String s) {

                Toaster.shortToast(getString(R.string.camera_swicth_failed) + s);
                cameraToggle.setEnabled(true);
            }
        });
    }

    private void updateSwitchCameraIcon(final MenuItem item) {
        if (isCurrentCameraFront) {

            item.setIcon(R.drawable.ic_camera_front);
        } else {

            item.setIcon(R.drawable.ic_camera_rear);
        }
    }

    private void toggleCameraInternal() {

        updateVideoView(isLocalVideoFullScreen ? remoteFullScreenVideoView : localVideoView, isCurrentCameraFront);
        toggleCamera(true);
    }

    private void runOnUiThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    private void toggleCamera(boolean isNeedEnableCam) {
        if (currentSession != null && currentSession.getMediaStreamManager() != null) {
            conversationFragmentCallbackListener.onSetVideoEnabled(isNeedEnableCam);
        }
        if (connectionEstablished && !cameraToggle.isEnabled()) {
            cameraToggle.setEnabled(true);
        }
    }

    ////////////////////////////  callbacks from QBRTCClientVideoTracksCallbacks ///////////////////
    @Override
    public void onLocalVideoTrackReceive(QBRTCSession qbrtcSession, final QBRTCVideoTrack videoTrack) {

        localVideoTrack = videoTrack;
        isLocalVideoFullScreen = true;
        cameraState = CameraState.NONE;


        if (remoteFullScreenVideoView != null) {
            fillVideoView(remoteFullScreenVideoView, localVideoTrack, false);
        }
    }

    @Override
    public void onRemoteVideoTrackReceive(QBRTCSession session, final QBRTCVideoTrack videoTrack, final Integer userID) {


        if (localVideoTrack != null) {
            fillVideoView(localVideoView, localVideoTrack, false);
        }
        isLocalVideoFullScreen = false;

        if (isPeerToPeerCall) {
            setDuringCallActionBar();
            fillVideoView(userID, remoteFullScreenVideoView, videoTrack, true);
            updateVideoView(remoteFullScreenVideoView, false);
        } else {
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRemoteViewMultiCall(userID, videoTrack);
                }
            }, LOCAL_TRACk_INITIALIZE_DELAY);
        }
    }
    /////////////////////////////////////////    end    ////////////////////////////////////////////

    //last opponent view is bind
    @Override
    public void OnBindLastViewHolder(final OpponentsFromCallAdapter.ViewHolder holder, final int position) {


    }


    @Override
    public void onItemClick(int position) {
        int userId = opponentsAdapter.getItem(position);

        if (!getVideoTrackMap().containsKey(userId) ||
                currentSession.getPeerChannel(userId).getState().ordinal() == QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_CLOSED.ordinal()) {
            return;
        }

        replaceUsersInAdapter(position);

        updateViewHolders(position);

        swapUsersFullscreenToPreview(userId);
    }

    private void replaceUsersInAdapter(int position) {
        for (QBUser qbUser : allOpponents) {
            if (qbUser.getId() == userIDFullScreen) {
                opponentsAdapter.replaceUsers(position, qbUser);
                break;
            }
        }
    }

    private void updateViewHolders(int position) {
        View childView = recyclerView.getChildAt(position);
        OpponentsFromCallAdapter.ViewHolder childViewHolder = (OpponentsFromCallAdapter.ViewHolder) recyclerView.getChildViewHolder(childView);
        opponentViewHolders.put(position, childViewHolder);
    }

    @SuppressWarnings("ConstantConditions")
    private void swapUsersFullscreenToPreview(int userId) {
//      get opponentVideoTrack - opponent's video track from recyclerView
        QBRTCVideoTrack opponentVideoTrack = getVideoTrackMap().get(userId);

//      get mainVideoTrack - opponent's video track from full screen
        QBRTCVideoTrack mainVideoTrack = getVideoTrackMap().get(userIDFullScreen);

        QBRTCSurfaceView remoteVideoView = findHolder(userId).getOpponentView();

        if (mainVideoTrack != null) {
            fillVideoView(0, remoteVideoView, mainVideoTrack);

        }
        if (opponentVideoTrack != null) {
            fillVideoView(userId, remoteFullScreenVideoView, opponentVideoTrack);

        }
    }


    private void setRemoteViewMultiCall(int userID, QBRTCVideoTrack videoTrack) {

        final OpponentsFromCallAdapter.ViewHolder itemHolder = getViewHolderForOpponent(userID);
        if (itemHolder == null) {

            return;
        }
        final QBRTCSurfaceView remoteVideoView = itemHolder.getOpponentView();

        if (remoteVideoView != null) {
            remoteVideoView.setZOrderMediaOverlay(true);
            updateVideoView(remoteVideoView, false);


            if (isRemoteShown) {

                fillVideoView(userID, remoteVideoView, videoTrack, true);
            } else {
                isRemoteShown = true;
                opponentsAdapter.removeItem(itemHolder.getAdapterPosition());
                setDuringCallActionBar();
                setRecyclerViewVisibleState();
                //setOpponentsVisibility(View.VISIBLE);
                fillVideoView(userID, remoteFullScreenVideoView, videoTrack);
                updateVideoView(remoteFullScreenVideoView, false);
            }
        }
    }

    private void setRecyclerViewVisibleState() {
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (int) getResources().getDimension(R.dimen.item_height);
        recyclerView.setLayoutParams(params);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private OpponentsFromCallAdapter.ViewHolder getViewHolderForOpponent(Integer userID) {
        OpponentsFromCallAdapter.ViewHolder holder = opponentViewHolders.get(userID);
        if (holder == null) {

            holder = findHolder(userID);
            if (holder != null) {
                opponentViewHolders.append(userID, holder);
            }
        }
        return holder;
    }

    private OpponentsFromCallAdapter.ViewHolder findHolder(Integer userID) {

        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = recyclerView.getChildAt(i);
            OpponentsFromCallAdapter.ViewHolder childViewHolder = (OpponentsFromCallAdapter.ViewHolder) recyclerView.getChildViewHolder(childView);
            if (userID.equals(childViewHolder.getUserId())) {
                return childViewHolder;
            }
        }
        return null;
    }

    ;

    private void releseOpponentsViews() {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int childCount = layoutManager.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childView = layoutManager.getChildAt(i);
            OpponentsFromCallAdapter.ViewHolder childViewHolder = (OpponentsFromCallAdapter.ViewHolder) recyclerView.getChildViewHolder(childView);
            childViewHolder.getOpponentView().release();
        }
    }

    /**
     * @param userId set userId if it from fullscreen videoTrack
     */
    private void fillVideoView(int userId, QBRTCSurfaceView videoView, QBRTCVideoTrack videoTrack,
                               boolean remoteRenderer) {
        videoTrack.removeRenderer(videoTrack.getRenderer());
        videoTrack.addRenderer(new VideoRenderer(videoView));
        if (userId != 0) {
            getVideoTrackMap().put(userId, videoTrack);
        }
        if (!remoteRenderer) {
            updateVideoView(videoView, isCurrentCameraFront);
        }
        Log.d(TAG, (remoteRenderer ? "remote" : "local") + " Track is rendering");
    }

    private void fillVideoView(QBRTCSurfaceView videoView, QBRTCVideoTrack videoTrack, boolean remoteRenderer) {
        fillVideoView(0, videoView, videoTrack, remoteRenderer);
    }

    /**
     * @param userId set userId if it from fullscreen videoTrack
     */
    private void fillVideoView(int userId, QBRTCSurfaceView videoView, QBRTCVideoTrack videoTrack) {
        if (userId != 0) {
            userIDFullScreen = userId;
        }
        fillVideoView(userId, videoView, videoTrack, true);
    }

    protected void updateVideoView(SurfaceViewRenderer surfaceViewRenderer, boolean mirror) {
        updateVideoView(surfaceViewRenderer, mirror, RendererCommon.ScalingType.SCALE_ASPECT_FILL);
    }

    protected void updateVideoView(SurfaceViewRenderer surfaceViewRenderer, boolean mirror, RendererCommon.ScalingType scalingType) {

        surfaceViewRenderer.setScalingType(scalingType);
        surfaceViewRenderer.setMirror(mirror);
        surfaceViewRenderer.requestLayout();
    }

    private void setStatusForOpponent(int userId, final String status) {
        if (isPeerToPeerCall) {
            connectionStatusLocal.setText(status);
            return;
        }

        final OpponentsFromCallAdapter.ViewHolder holder = findHolder(userId);
        if (holder == null) {
            return;
        }

        holder.setStatus(status);
    }

    private void updateNameForOpponent(int userId, String newUserName) {
        if (isPeerToPeerCall) {
            actionBar.setSubtitle(getString(R.string.opponent, newUserName));
        } else {
            OpponentsFromCallAdapter.ViewHolder holder = findHolder(userId);
            if (holder == null) {
                return;
            }


            holder.setUserName(newUserName);
        }
    }

    private void setProgressBarForOpponentGone(int userId) {
        if (isPeerToPeerCall) {
            return;
        }
        final OpponentsFromCallAdapter.ViewHolder holder = getViewHolderForOpponent(userId);
        if (holder == null) {
            return;
        }

        holder.getProgressBar().setVisibility(View.GONE);

    }

    private void setBackgroundOpponentView(final Integer userId) {
        final OpponentsFromCallAdapter.ViewHolder holder = findHolder(userId);
        if (holder == null) {
            return;
        }

        if (userId != userIDFullScreen) {
            holder.getOpponentView().setBackgroundColor(Color.parseColor("#000000"));
        }
    }

    ///////////////////////////////  QBRTCSessionConnectionCallbacks ///////////////////////////

    @Override
    public void onConnectedToUser(QBRTCSession qbrtcSession, final Integer userId) {
        connectionEstablished = true;
        setStatusForOpponent(userId, getString(R.string.text_status_connected));
        setProgressBarForOpponentGone(userId);
    }

    @Override
    public void onConnectionClosedForUser(QBRTCSession qbrtcSession, Integer userId) {
        setStatusForOpponent(userId, getString(R.string.text_status_closed));
        if (!isPeerToPeerCall) {

            getVideoTrackMap().remove(userId);
            setBackgroundOpponentView(userId);
        }
    }

    @Override
    public void onDisconnectedFromUser(QBRTCSession qbrtcSession, Integer integer) {
        setStatusForOpponent(integer, getString(R.string.text_status_disconnected));
    }

    //////////////////////////////////   end     //////////////////////////////////////////


    /////////////////// Callbacks from CallActivity.QBRTCSessionUserCallback //////////////////////
    @Override
    public void onUserNotAnswer(QBRTCSession session, Integer userId) {
        setProgressBarForOpponentGone(userId);
        setStatusForOpponent(userId, getString(R.string.text_status_no_answer));
    }

    @Override
    public void onCallRejectByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo) {
        setStatusForOpponent(userId, getString(R.string.text_status_rejected));
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo) {
        setStatusForOpponent(userId, getString(R.string.accepted));
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession session, Integer userId, Map<String, String> userInfo) {
        setStatusForOpponent(userId, getString(R.string.text_status_hang_up));

        if (!isPeerToPeerCall) {
            if (userId == userIDFullScreen) {
                setAnotherUserToFullScreen();
            }
        }
    }

    @Override
    public void onSessionClosed(QBRTCSession session) {

    }

    //////////////////////////////////   end     //////////////////////////////////////////

    private void setAnotherUserToFullScreen() {
        if (opponentsAdapter.getOpponents().isEmpty()) {
            return;
        }
        int userId = opponentsAdapter.getItem(0);
//      get opponentVideoTrack - opponent's video track from recyclerView
        QBRTCVideoTrack opponentVideoTrack = getVideoTrackMap().get(userId);
        if (opponentVideoTrack == null) {

            return;
        }

        fillVideoView(userId, remoteFullScreenVideoView, opponentVideoTrack);

        OpponentsFromCallAdapter.ViewHolder itemHolder = findHolder(userId);
        if (itemHolder != null) {
            opponentsAdapter.removeItem(itemHolder.getAdapterPosition());
            itemHolder.getOpponentView().release();

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.conversation_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.camera_switch) {

            switchCamera(item);
            return true;
        } else if (i == R.id.screen_share) {
            startScreenSharing();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void startScreenSharing() {
        conversationFragmentCallbackListener.onStartScreenSharing();
    }

    @Override
    public void onOpponentsListUpdated(ArrayList<QBUser> newUsers) {
        super.onOpponentsListUpdated(newUsers);
        updateAllOpponentsList(newUsers);

        runUpdateUsersNames(newUsers);
    }

    private void updateAllOpponentsList(ArrayList<QBUser> newUsers) {

        for (int i = 0; i < allOpponents.size(); i++) {
            for (QBUser updatedUser : newUsers) {
                if (updatedUser.equals(allOpponents.get(i))) {
                    allOpponents.set(i, updatedUser);
                }
            }
        }
    }

    private void runUpdateUsersNames(final ArrayList<QBUser> newUsers) {
        //need delayed for synchronization with recycler view initialization
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (QBUser user : newUsers) {
                    updateNameForOpponent(user.getId(), user.getFullName());
                }
            }
        }, UPDATING_USERS_DELAY);
    }

    private enum CameraState {
        NONE,
        DISABLED_FROM_USER,
        ENABLED_FROM_USER
    }


    class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public DividerItemDecoration(@NonNull Context context, @DimenRes int dimensionDivider) {
            this.space = context.getResources().getDimensionPixelSize(dimensionDivider);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(space, space, space, space);
        }
    }

    class LocalViewOnClickListener implements View.OnClickListener {
        private long lastFullScreenClickTime = 0L;

        @Override
        public void onClick(View v) {
            if ((SystemClock.uptimeMillis() - lastFullScreenClickTime) < FULL_SCREEN_CLICK_DELAY) {
                return;
            }
            lastFullScreenClickTime = SystemClock.uptimeMillis();

            if (connectionEstablished) {
                setFullScreenOnOff();
            }
        }

        private void setFullScreenOnOff() {
            if (actionBar.isShowing()) {
                hideToolBarAndButtons();
            } else {
                showToolBarAndButtons();
            }
        }

        private void hideToolBarAndButtons() {
            actionBar.hide();

            localVideoView.setVisibility(View.INVISIBLE);

            actionVideoButtonsLayout.setVisibility(View.GONE);

            if (!isPeerToPeerCall) {
                shiftBottomListOpponents();
            }
        }

        private void showToolBarAndButtons() {
            actionBar.show();

            localVideoView.setVisibility(View.VISIBLE);

            actionVideoButtonsLayout.setVisibility(View.VISIBLE);

            if (!isPeerToPeerCall) {
                shiftMarginListOpponents();
            }
        }

        private void shiftBottomListOpponents() {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.setMargins(0, 0, 0, 0);

            recyclerView.setLayoutParams(params);
        }

        private void shiftMarginListOpponents() {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            params.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.margin_common));

            recyclerView.setLayoutParams(params);
        }
    }
}


