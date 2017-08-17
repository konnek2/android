package com.quickblox.sample.chat.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBMessageStatusesManager;
import com.quickblox.chat.listeners.QBChatDialogMessageSentListener;
import com.quickblox.chat.listeners.QBChatDialogParticipantListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.listeners.QBMessageStatusListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.FileHelper;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.messages.model.QBPushType;
import com.quickblox.sample.chat.App;
import com.quickblox.sample.chat.R;
import com.quickblox.sample.chat.models.MessageStatusModel;
import com.quickblox.sample.chat.ui.adapter.AttachmentPreviewAdapter;
import com.quickblox.sample.chat.ui.adapter.ChatAdapter;
import com.quickblox.sample.chat.ui.widget.AttachmentPreviewAdapterView;
import com.quickblox.sample.chat.utils.Common;
import com.quickblox.sample.chat.utils.Constant;
import com.quickblox.sample.chat.utils.StringUtils;
import com.quickblox.sample.chat.utils.chat.ChatHelper;
import com.quickblox.sample.chat.utils.qb.PaginationHistoryListener;
import com.quickblox.sample.chat.utils.qb.QbChatDialogMessageListenerImp;
import com.quickblox.sample.chat.utils.qb.QbDialogHolder;
import com.quickblox.sample.chat.utils.qb.QbDialogUtils;
import com.quickblox.sample.chat.utils.qb.QbUsersHolder;
import com.quickblox.sample.chat.utils.qb.VerboseQbChatConnectionListener;
import com.quickblox.sample.core.ui.dialog.ProgressDialogFragment;
import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.sample.core.utils.imagepick.ImagePickHelper;
import com.quickblox.sample.core.utils.imagepick.OnImagePickedListener;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.quickblox.sample.chat.R.id.menu_chat_action_add;
import static com.quickblox.sample.chat.R.id.menu_chat_action_delete;
import static com.quickblox.sample.chat.R.id.menu_chat_action_info;
import static com.quickblox.sample.chat.R.id.menu_chat_action_leave;
import static com.quickblox.sample.groupchatwebrtc.fragments.ContactFragment.groupChatCall;
import static com.quickblox.sample.groupchatwebrtc.utils.FolderCreator.getAudioFilePath;


public class ChatActivity extends BaseActivity implements OnImagePickedListener, QBChatDialogTypingListener {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final int REQUEST_CODE_ATTACHMENT = 721;
    private static final int REQUEST_CODE_SELECT_PEOPLE = 752;
    public static final String RESULT_CODE = "resultCode";
    public static final String REQUEST_CODE = "requestCode";
    private static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
    public static final String EXTRA_DIALOG_ID = "dialogId";

    private ProgressBar progressBar;
    private StickyListHeadersListView messagesListView;
    private EditText messageEditText;
    private LinearLayout attachmentPreviewContainerLayout;
    private Snackbar snackbar;
    private ChatAdapter chatAdapter;
    private AttachmentPreviewAdapter attachmentPreviewAdapter;
    private ConnectionListener chatConnectionListener;
    public QBChatDialog qbChatDialog;
    private ArrayList<QBChatMessage> unShownMessages;
    private int skipPagination = 0;
    private ChatMessageListener chatMessageListener;
    private String typeIndicatorUpdate;

    //Type Indicatpor
    private QBMessageStatusesManager messageStatusesManager;
    private ArrayList<MessageStatusModel> messageStatusModelArrayList;
    private MessageStatusModel messageStatusModel;
    ActionBar actionBar;
    private Menu menu;
    List<QBUser> qbUsers;
    List<Integer> oppOnentList;
    private android.app.AlertDialog alertDialog;
    private String userId;
    private Toolbar toolbar;
    TextView mTittle, mSubTitle;

    // reoced Voice
    private MediaRecorder mediaRecorder;
    ImageView recordImage;

    public static void startForResult(Activity activity, int code, QBChatDialog dialogId) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);
        activity.startActivityForResult(intent, code);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbar_chat_dialog);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chat_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Log.v(TAG, "onCreate ChatActivity on Thread ID = " + Thread.currentThread().getId());
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);

        qbChatDialog.initForChat(QBChatService.getInstance());
        messageStatusesManager = QBChatService.getInstance().getMessageStatusesManager();
        chatMessageListener = new ChatMessageListener();
        qbChatDialog.addIsTypingListener(typingListener);
        qbChatDialog.addMessageSentListener(messageSentListener);
        qbChatDialog.addParticipantListener(participantListener);
        messageStatusesManager.addMessageStatusListener(messageStatusListener);
        qbChatDialog.addMessageListener(chatMessageListener);
        messageStatusesManager.addMessageStatusListener(messageStatusListener);
        initChatConnectionListener();
        initViews();
        initChat();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (qbChatDialog != null) {
            outState.putString(EXTRA_DIALOG_ID, qbChatDialog.getDialogId());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (qbChatDialog == null) {
            qbChatDialog = QbDialogHolder.getInstance().getChatDialogById(savedInstanceState.getString(EXTRA_DIALOG_ID));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatHelper.getInstance().addConnectionListener(chatConnectionListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatHelper.getInstance().removeConnectionListener(chatConnectionListener);
    }

    @Override
    public void onBackPressed() {
        releaseChat();
        sendDialogId();

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat, menu);
        this.menu = menu;
        MenuItem menu_ItemInfo = menu.findItem(R.id.menu_chat_action_info);
        MenuItem menuItemLeave = menu.findItem(R.id.menu_chat_action_leave);
        MenuItem menuItemAdd = menu.findItem(R.id.menu_chat_action_add);
        MenuItem menuItemDelete = menu.findItem(R.id.menu_chat_action_delete);
        MenuItem menuItemAudioCall = menu.findItem(R.id.menu_chat_audio_call);
        MenuItem menuItemVideoCall = menu.findItem(R.id.menu_chat_video_call);
        if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
            menuItemLeave.setVisible(false);
            menuItemAdd.setVisible(false);
        } else {
            menuItemDelete.setVisible(false);
        }
        if (qbChatDialog.getOccupants().size() < Constant.CHAT_AUDIO_CALL_LIMIT) {
            menuItemAudioCall.setVisible(true);
        }
        if (qbChatDialog.getOccupants().size() < Constant.CHAT_VIDEO_CALL_LIMIT) {
            menuItemVideoCall.setVisible(true);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == menu_chat_action_info) {
            Log.d("CHATINFO ", "ChatInfoActivity  onCreate  qbChatDialog " + qbChatDialog);
            ChatInfoActivity.start(this, qbChatDialog);
            return true;
        } else if (id == R.id.menu_chat_action_add) {
            SelectUsersActivity.startForResult(this, REQUEST_CODE_SELECT_PEOPLE, qbChatDialog);
            return true;
        } else if (id == R.id.menu_chat_action_leave) {
            leaveGroupChat();
            return true;
        } else if (id == R.id.menu_chat_action_add) {
            joinGroupChat();
            return true;
        } else if (id == R.id.menu_chat_action_delete) {
            deleteChat();
            return true;
        } else if (id == R.id.menu_chat_audio_call) {
            groupCall(false);
            return true;
        } else if (id == R.id.menu_chat_video_call) {
            groupCall(true);
            return true;
        } else if (id == android.R.id.home) {
            Intent intent = new Intent(Constant.NOTIFY_MULTIPLE);
            intent.putExtra(RESULT_CODE, -1);
            intent.putExtra(REQUEST_CODE, 165);
            intent.putExtra(EXTRA_DIALOG_ID, qbChatDialog.getDialogId());
            sendBroadcast(intent);
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void sendDialogId() {
        Log.d("DELETEGROUP ", "Deletedialog    sendDialogId " + qbChatDialog);
        Intent result = new Intent();
        result.putExtra(EXTRA_DIALOG_ID, qbChatDialog.getDialogId());
        setResult(RESULT_OK, result);
    }

    private void leaveGroupChat() {


        progressBar.setVisibility(View.VISIBLE);
//        ProgressDialogFragment.show(getSupportFragmentManager());
        ChatHelper.getInstance().exitFromDialog(qbChatDialog, new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbDialog, Bundle bundle) {

                progressBar.setVisibility(View.GONE);
                QbDialogHolder.getInstance().deleteDialog(qbDialog);
                Intent intent = new Intent(Constant.NOTIFY_MULTIPLE);
                intent.putExtra(RESULT_CODE, -2);
                intent.putExtra(REQUEST_CODE, 160);
                sendBroadcast(intent);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);

                showErrorSnackbar(R.string.error_leave_chat, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leaveGroupChat();
                    }
                });
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CODE_SELECT_PEOPLE) {
                ArrayList<QBUser> selectedUsers = (ArrayList<QBUser>) data.getSerializableExtra(
                        SelectUsersActivity.EXTRA_QB_USERS);
                updateDialog(selectedUsers);
            }
        } else {
            Log.d("DELETEGROUP", "   ChatActivity   onActivityResult  RESULT_OK ELSE ");
        }
    }

    @Override
    public void onImagePicked(int requestCode, File file) {
        switch (requestCode) {


            case REQUEST_CODE_ATTACHMENT:
                attachmentPreviewAdapter.add(file);
                break;
        }
    }

    @Override
    public void onImagePickError(int requestCode, Exception e) {

        showErrorSnackbar(0, e, null);
    }

    @Override
    public void onImagePickClosed(int requestCode) {
        // ignore
    }

    @Override
    protected View getSnackbarAnchorView() {
        return findViewById(R.id.list_chat_messages);
    }

    public void onSendChatClick(View view) {

        if (Common.checkAvailability(ChatActivity.this)) {
//            SnackBarhide();
            int totalAttachmentsCount = attachmentPreviewAdapter.getCount();
            Collection<QBAttachment> uploadedAttachments = attachmentPreviewAdapter.getUploadedAttachments();
            if (!uploadedAttachments.isEmpty()) {
                if (uploadedAttachments.size() == totalAttachmentsCount) {
                    for (QBAttachment attachment : uploadedAttachments) {
                        sendChatMessage(null, attachment);
                    }
                } else {
                    Toaster.shortToast(R.string.chat_wait_for_attachments_to_upload);
                }
            }

        } else {
//            SnackBar();
        }
        String text = messageEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            sendPushMessage(text);
            sendChatMessage(text, null);
        }
    }

    public void onAttachmentsClick(View view) {
        new ImagePickHelper().pickAnImage(this, REQUEST_CODE_ATTACHMENT);
    }

    public void showMessage(QBChatMessage message) {


        if (chatAdapter != null) {
            chatAdapter.add(message);
            scrollMessageListDown();
        } else {
            if (unShownMessages == null) {
                unShownMessages = new ArrayList<>();
            }
            unShownMessages.add(message);
        }
    }

    private void initViews() {
        initializeMediaRecord();
        messageStatusModelArrayList = new ArrayList<>();
        mTittle = _findViewById(R.id.text_title);
        mSubTitle = _findViewById(R.id.text_sub_title);
        messagesListView = _findViewById(R.id.list_chat_messages);
        messageEditText = _findViewById(R.id.edit_chat_message);
        progressBar = _findViewById(R.id.progress_chat);
        recordImage = (ImageView) findViewById(R.id.button_voice_record);
        attachmentPreviewContainerLayout = _findViewById(R.id.layout_attachment_preview_container);

        attachmentPreviewAdapter = new AttachmentPreviewAdapter(this,
                new AttachmentPreviewAdapter.OnAttachmentCountChangedListener() {
                    @Override
                    public void onAttachmentCountChanged(int count) {
                        attachmentPreviewContainerLayout.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
                    }
                },
                new AttachmentPreviewAdapter.OnAttachmentUploadErrorListener() {
                    @Override
                    public void onAttachmentUploadError(QBResponseException e) {
                        showErrorSnackbar(0, e, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onAttachmentsClick(v);
                            }
                        });
                    }
                });
        AttachmentPreviewAdapterView previewAdapterView = _findViewById(R.id.adapter_view_attachment_preview);
        previewAdapterView.setAdapter(attachmentPreviewAdapter);

        messageEditText.addTextChangedListener(watcher);
        recordImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                switch (action) {

                    case MotionEvent.ACTION_DOWN:
//                        startAudioRecording();
                        Common.displayToast(Constant.TOST_PROGRESS);
                        break;

                    case MotionEvent.ACTION_UP:
//                        stopAudioRecording();

                        break;
                }
                return false;
            }
        });

    }


    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            sendTypingStatusToServer(true);
        }

        @Override
        public void afterTextChanged(Editable s) {

            sendTypingStatusToServer(false);
        }
    };

    private void sendChatMessage(String text, QBAttachment attachment) {


        QBChatMessage chatMessage = new QBChatMessage();
        if (attachment != null) {

            chatMessage.addAttachment(attachment);
        } else {


            chatMessage.setBody(text);
        }
        chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(System.currentTimeMillis() / 1000);
        chatMessage.setMarkable(true);

        if (!QBDialogType.PRIVATE.equals(qbChatDialog.getType()) && !qbChatDialog.isJoined()) {
            Toaster.shortToast("You're still joining a group chat, please wait a bit");
            return;
        }

        try {
            qbChatDialog.sendMessage(chatMessage);

            if (QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
                showMessage(chatMessage);

            }

            if (attachment != null) {
                attachmentPreviewAdapter.remove(attachment);
            } else {
                messageEditText.setText("");
            }
        } catch (SmackException.NotConnectedException e) {
            Log.w(TAG, e);
            Toaster.shortToast("Can't send a message, You are not connected to chat");
        }
    }

    private void initChat() {

        switch (qbChatDialog.getType()) {
            case GROUP:
            case PUBLIC_GROUP:
                joinGroupChat();
                break;

            case PRIVATE:
                loadDialogUsers();
                break;

            default:
                Toaster.shortToast(String.format("%s %s", getString(R.string.chat_unsupported_type), qbChatDialog.getType().name()));
                finish();
                break;
        }
    }

    private void joinGroupChat() {

        try {

            progressBar.setVisibility(View.VISIBLE);
            ChatHelper.getInstance().join(qbChatDialog, new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void result, Bundle b) {
                    if (snackbar != null) {
                        snackbar.dismiss();
                    }
                    loadDialogUsers();
                }

                @Override
                public void onError(QBResponseException e) {
                    progressBar.setVisibility(View.GONE);
                    snackbar = showErrorSnackbar(R.string.connection_error, e, null);
                }
            });

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void leaveGroupDialog() {
        try {
            ChatHelper.getInstance().leaveChatDialog(qbChatDialog);
        } catch (XMPPException | SmackException.NotConnectedException e) {
            Log.w(TAG, e);
        }
    }

    private void releaseChat() {
        qbChatDialog.removeMessageListrener(chatMessageListener);
        if (!QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
            leaveGroupDialog();
        }
    }

    private void updateDialog(final ArrayList<QBUser> selectedUsers) {

        try {
            ChatHelper.getInstance().updateDialogUsers(qbChatDialog, selectedUsers,
                    new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(QBChatDialog dialog, Bundle args) {
                            qbChatDialog = dialog;
                            loadDialogUsers();
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            showErrorSnackbar(R.string.chat_info_add_people_error, e,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            updateDialog(selectedUsers);
                                        }
                                    });
                        }
                    }
            );

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void loadDialogUsers() {

        try {

            ChatHelper.getInstance().getUsersFromDialog(qbChatDialog, new QBEntityCallback<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {

                    setChatNameToActionBar();
                    loadChatHistory();
                }

                @Override
                public void onError(QBResponseException e) {

                    showErrorSnackbar(R.string.chat_load_users_error, e,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadDialogUsers();
                                }
                            });
                }
            });

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void setChatNameToActionBar() {

        String chatName = QbDialogUtils.getDialogName(qbChatDialog);
        mTittle.setText(chatName);
    }


    private void loadChatHistory() {
        try {

            ChatHelper.getInstance().loadChatHistory(qbChatDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {

                    Collections.reverse(messages);
                    if (chatAdapter == null) {
                        chatAdapter = new ChatAdapter(ChatActivity.this, qbChatDialog, messages);
                        chatAdapter.setPaginationHistoryListener(new PaginationHistoryListener() {
                            @Override
                            public void downloadMore() {
                                loadChatHistory();
                            }
                        });
                        chatAdapter.setOnItemInfoExpandedListener(new ChatAdapter.OnItemInfoExpandedListener() {
                            @Override
                            public void onItemInfoExpanded(final int position) {
                                if (isLastItem(position)) {
                                    // HACK need to allow info textview visibility change so posting it via handler
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            messagesListView.setSelection(position);
                                        }
                                    });
                                } else {
                                    messagesListView.smoothScrollToPosition(position);
                                }
                            }

                            private boolean isLastItem(int position) {
                                return position == chatAdapter.getCount() - 1;
                            }
                        });
                        if (unShownMessages != null && !unShownMessages.isEmpty()) {
                            List<QBChatMessage> chatList = chatAdapter.getList();
                            for (QBChatMessage message : unShownMessages) {
                                if (!chatList.contains(message)) {
                                    chatAdapter.add(message);
                                }
                            }
                        }

                        try {
                            messagesListView.setAdapter(chatAdapter);
                            messagesListView.setAreHeadersSticky(false);
                            messagesListView.setDivider(null);
                            progressBar.setVisibility(View.GONE);

                        } catch (Exception e) {
                            e.getMessage();
                        }

                    } else {
                        chatAdapter.addList(messages);
                        messagesListView.setSelection(messages.size());
                    }
                }

                @Override
                public void onError(QBResponseException e) {

                    progressBar.setVisibility(View.GONE);
                    skipPagination -= ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
                    snackbar = showErrorSnackbar(R.string.connection_error, e, null);
                }
            });
            skipPagination += ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void scrollMessageListDown() {
        messagesListView.setSelection(messagesListView.getCount() - 1);
    }

    private void deleteChat() {


        ChatHelper.getInstance().deleteDialog(qbChatDialog, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

                setResult(RESULT_OK);
                QbDialogHolder.getInstance().deleteDialog(qbChatDialog);
                Intent intent = new Intent(Constant.NOTIFY_MULTIPLE);
                intent.putExtra(RESULT_CODE, -2);
                intent.putExtra(REQUEST_CODE, 160);
                sendBroadcast(intent);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {

                showErrorSnackbar(R.string.dialogs_deletion_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteChat();
                            }
                        });
            }
        });
    }

    private void initChatConnectionListener() {

        try {

            chatConnectionListener = new VerboseQbChatConnectionListener(getSnackbarAnchorView()) {
                @Override
                public void reconnectionSuccessful() {
                    super.reconnectionSuccessful();
                    skipPagination = 0;
                    chatAdapter = null;
                    switch (qbChatDialog.getType()) {
                        case PRIVATE:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadChatHistory();
                                }
                            });
                            break;
                        case GROUP:
                            // Join active room if we're in Group Chat
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    joinGroupChat();
                                }
                            });
                            break;
                    }
                }
            };

        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void processUserIsTyping(String s, Integer integer) {
        typeIndicatorUpdate = s.toString();

    }

    @Override
    public void processUserStopTyping(String s, Integer integer) {
        typeIndicatorUpdate = s.toString();

    }

    public class ChatMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            showMessage(qbChatMessage);
        }
    }

    private void sendPushMessage(final String outMessage) {


        QBEvent qbEvent = new QBEvent();
        qbEvent.setNotificationType(QBNotificationType.PUSH);
        qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);
        qbEvent.setPushType(QBPushType.GCM);
        qbEvent.setMessage(outMessage);

        StringifyArrayList<Integer> toIds = new StringifyArrayList<>();
        toIds.add(qbChatDialog.getRecipientId());  // to Id
        qbEvent.setUserIds(toIds);
        qbEvent.setId(QBSessionManager.getInstance().getSessionParameters().getUserId()); //   my id  (From Id )
        QBPushNotifications.createEvent(qbEvent).performAsync(new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle bundle) {
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.INVISIBLE);
//
            }
        });
        progressBar.setVisibility(View.VISIBLE);
    }

    public void sendTypingStatusToServer(boolean startTyping) {
        try {
            List<Integer> opId = new ArrayList<>();
            opId.add(qbChatDialog.getRecipientId());
            if (startTyping) {
//
                qbChatDialog.setOccupantsIds(opId);
                qbChatDialog.sendIsTypingNotification();
            } else {
//
                qbChatDialog.sendStopTypingNotification();
            }
        } catch (XMPPException | SmackException.NotConnectedException e) {
        }

    }

    // Typing Indicator

    QBChatDialogTypingListener typingListener = new QBChatDialogTypingListener() {
        @Override
        public void processUserIsTyping(String dialogId, Integer senderId) {
//
            mSubTitle.setText(Constant.EXTRA_IS_TYPING);

        }

        @Override
        public void processUserStopTyping(String dialogId, Integer senderId) {
//            actionBar.setSubtitle(Constant.EXTRA_STOP_TYPING);
            mSubTitle.setText(Constant.EXTRA_STOP_TYPING);
        }
    };


    QBChatDialogMessageSentListener messageSentListener = new QBChatDialogMessageSentListener() {
        @Override
        public void processMessageSent(String dialogId, QBChatMessage qbChatMessage) {
            try {
//
                messageStatusModelArrayList.clear();
                messageStatusModel = new MessageStatusModel();
                messageStatusModel.setUserId("0");
                messageStatusModel.setRecipientId(String.valueOf(qbChatDialog.getRecipientId()));
                messageStatusModel.setMessageId(qbChatMessage.getId());
                messageStatusModel.setIsUpdateServer(1);
                messageStatusModel.setIsDelivered(0);
                messageStatusModel.setIsRead(0);
                messageStatusModelArrayList.add(messageStatusModel);
                App.messageStatusTableDAO.messageStatusUpdate(messageStatusModelArrayList);
                App.tableManagerDAO.databaseDB(ChatActivity.this);
                chatAdapter.notifyDataSetChanged();
            } catch (Exception ex) {
                ex.getMessage();
            }

        }

        @Override
        public void processMessageFailed(String dialogId, QBChatMessage qbChatMessage) {


        }

    };

    QBChatDialogParticipantListener participantListener = new QBChatDialogParticipantListener() {
        @Override
        public void processPresence(String s, QBPresence qbPresence) {


        }
    };

    //  deliver status report from use2 to User1  &
    QBMessageStatusListener messageStatusListener = new QBMessageStatusListener() {
        @Override
        public void processMessageDelivered(String messageId, String dialogId, Integer userId) {

            try {
                messageStatusModelArrayList.clear();
                messageStatusModel = new MessageStatusModel();
                messageStatusModel.setUserId("0");
                messageStatusModel.setRecipientId(String.valueOf(userId));
                messageStatusModel.setMessageId(messageId);
                messageStatusModel.setIsUpdateServer(1);
                messageStatusModel.setIsDelivered(1);
                messageStatusModel.setIsRead(0);
                messageStatusModelArrayList.add(messageStatusModel);
                App.messageStatusTableDAO.messageStatusUpdate(messageStatusModelArrayList);
                App.tableManagerDAO.databaseDB(ChatActivity.this);

                chatAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                e.getMessage();
            }
//            Toaster.longToast("Message  deliver status from user2");

        }

        // read status from User2 to user1
        @Override
        public void processMessageRead(String messageId, String dialogId, Integer userId) {


            try {
                messageStatusModelArrayList.clear();
                messageStatusModel = new MessageStatusModel();
                messageStatusModel.setUserId("0");
                messageStatusModel.setRecipientId(String.valueOf(userId));
                messageStatusModel.setMessageId(messageId);
                messageStatusModel.setIsUpdateServer(1);
                messageStatusModel.setIsDelivered(1);
                messageStatusModel.setIsRead(1);
                messageStatusModelArrayList.add(messageStatusModel);
                App.messageStatusTableDAO.messageStatusUpdate(messageStatusModelArrayList);
                App.tableManagerDAO.databaseDB(ChatActivity.this);
//                Toaster.longToast("Message red status from user2");
                chatAdapter.notifyDataSetChanged();
            } catch (Exception ex) {
                ex.getMessage();
            }

        }
    };

    public void groupCall(final boolean callType) {

        LayoutInflater li = LayoutInflater.from(ChatActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ChatActivity.this);
        final View view = inflater.inflate(R.layout.item_chat_group_call, null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layout_chat_group_call);
        TextView callOk = (TextView) view.findViewById(R.id.textview_group_ok);
        TextView alertHeader = (TextView) view.findViewById(R.id.textview_group_header);
        TextView callCancel = (TextView) view.findViewById(R.id.textview_group_cancel);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(40, 5, 5, 5);

        if (callType) {
            alertHeader.setText(getResources().getString(R.string.outgoing_video));

        } else {
            alertHeader.setText(getResources().getString(R.string.outgoing_audio));
        }
        QBUser currentUser = ChatHelper.getCurrentUser();
        qbUsers = new ArrayList<>();
        oppOnentList = new ArrayList<>();
        for (Integer id : qbChatDialog.getOccupants()) {
            oppOnentList.add(id);
            qbUsers = QbUsersHolder.getInstance().getUsersByIds(oppOnentList);
            if (qbUsers == null) {
                throw new RuntimeException(Constant.CHAT_EXCEPTION);

            }
//                qbUsers.add(qbUsers.get(id));
            qbUsers.remove(currentUser);

        }

        TextView[] opponentsName = new TextView[qbUsers.size()];
        for (int l = 0; l < opponentsName.length; l++) {
            opponentsName[l] = new TextView(this);
            opponentsName[l].setTextSize(15);
            opponentsName[l].setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            opponentsName[l].setLayoutParams(params);
            opponentsName[l].setId(l);
            opponentsName[l].setText(qbUsers.get(l).getFullName());
            linearLayout.addView(opponentsName[l]);
        }
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        callOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupChatCall(qbUsers, callType, 1);
                alertDialog.dismiss();
            }
        });

        callCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

            }
        });

        alertDialog.show();

    }

    // Voice Reocrd

    private void initializeMediaRecord() {
        Random r = new Random();
        int i1 = r.nextInt(80 - 65) + 65;
        int userId = Common.getCurrentUser().getId();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(getAudioFilePath(userId + "_" + String.valueOf(i1)));
    }

    private void startAudioRecording() {
        try {
            if (mediaRecorder == null) {
                initializeMediaRecord();
            }

            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
//
    }

    private void stopAudioRecording() {

        try {
            if (mediaRecorder != null) {

                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                avFileUpload();
            }

        } catch (Exception e) {
            e.getMessage();
        }

    }


    public void avFileUpload() throws IOException {
        InputStream is = getApplicationContext().getAssets().open("sample.mp3");

        File file = FileHelper.getFileInputStream(is, "sample.mp3", "myfile");

        Boolean isPublic = false;

        QBContent.uploadFileTask(file, isPublic, null).performAsync(new QBEntityCallback<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
//
                QBUser currentUser = ChatHelper.getCurrentUser();
                int uploadedFileID = qbFile.getId();
                QBUser user = new QBUser();
                user.setId(currentUser.getId());
                user.setFileId(uploadedFileID);

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

}