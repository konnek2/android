package com.quickblox.sample.chat.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBMessageStatusesManager;
import com.quickblox.chat.listeners.QBChatDialogMessageSentListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.listeners.QBMessageStatusListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.quickblox.sample.chat.R.id.menu_chat_action_add;
import static com.quickblox.sample.chat.R.id.menu_chat_action_delete;
import static com.quickblox.sample.chat.R.id.menu_chat_action_info;
import static com.quickblox.sample.chat.R.id.menu_chat_action_leave;
import static com.quickblox.sample.groupchatwebrtc.fragments.ContactFragment.groupChatCall;


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

    public static void startForResult(Activity activity, int code, QBChatDialog dialogId) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);
        activity.startActivityForResult(intent, code);
        Log.d("WATCHER123", "    ChatActivity startForResult : " + dialogId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Log.v(TAG, "onCreate ChatActivity on Thread ID = " + Thread.currentThread().getId());
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);
        Log.d("WATCHER123", " QBChatDialog    ChatActivity startForResult : " + qbChatDialog);

        qbChatDialog.initForChat(QBChatService.getInstance());
        messageStatusesManager = QBChatService.getInstance().getMessageStatusesManager();
        chatMessageListener = new ChatMessageListener();
        qbChatDialog.addIsTypingListener(typingListener);
        qbChatDialog.addMessageSentListener(messageSentListener);
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
            ChatInfoActivity.start(this, qbChatDialog);
        } else if (id == menu_chat_action_add) {
            SelectUsersActivity.startForResult(this, REQUEST_CODE_SELECT_PEOPLE, qbChatDialog);
        } else if (id == menu_chat_action_leave) {
            leaveGroupChat();
        } else if (id == menu_chat_action_delete) {
            deleteChat();
        }

        if (id == R.id.menu_chat_action_info) {
            ChatInfoActivity.start(this, qbChatDialog);
            return true;
        } else if (id == R.id.menu_chat_action_add) {
            SelectUsersActivity.startForResult(this, REQUEST_CODE_SELECT_PEOPLE, qbChatDialog);
            return true;
        } else if (id == R.id.menu_chat_action_leave) {
            leaveGroupChat();
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
//                ProgressDialogFragment.hide(getSupportFragmentManager());
                progressBar.setVisibility(View.GONE);
                QbDialogHolder.getInstance().deleteDialog(qbDialog);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
//                ProgressDialogFragment.hide(getSupportFragmentManager());
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
            Log.d("CHATFRAGMENT", "   ChatActivity   onActivityResult  RESULT_OK");
            if (requestCode == REQUEST_CODE_SELECT_PEOPLE) {

                Log.d("CHATFRAGMENT", "   ChatActivity   onActivityResult  REQUEST_CODE_SELECT_PEOPLE");
                ArrayList<QBUser> selectedUsers = (ArrayList<QBUser>) data.getSerializableExtra(
                        SelectUsersActivity.EXTRA_QB_USERS);
                updateDialog(selectedUsers);
            }
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
        Log.d("CHATFRAGMENT", "   ChatActivity   onSendChatClick ");
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
        messageStatusModelArrayList = new ArrayList<>();
        Log.d("CHATFRAGMENT", "   ChatActivity   initViews ");
        messagesListView = _findViewById(R.id.list_chat_messages);
        messageEditText = _findViewById(R.id.edit_chat_message);
        progressBar = _findViewById(R.id.progress_chat);
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


    }


    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d("WATCHER", "   beforeTextChanged  Text   " + s.toString());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("WATCHER", "   onTextChanged    Text " + s.toString());
            sendTypingStatusToServer(true);
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d("WATCHER", "   afterTextChanged  Text   " + s.toString());
            sendTypingStatusToServer(false);
        }
    };

    private void sendChatMessage(String text, QBAttachment attachment) {
        Log.d("MULTIUPDATE", "   ChatActivity    sendChatMessage ");
        Log.d("CHATFRAGMENT", "   ChatActivity   sendChatMessage  text   " + text);
        QBChatMessage chatMessage = new QBChatMessage();
        if (attachment != null) {
            chatMessage.addAttachment(attachment);
        } else {
            Log.d("MULTIUPDATE", "   ChatActivity    sendChatMessage ELSE " + text);
            chatMessage.setBody(text);
        }
        chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(System.currentTimeMillis() / 1000);
        chatMessage.setMarkable(true);

        if (!QBDialogType.PRIVATE.equals(qbChatDialog.getType()) && !qbChatDialog.isJoined()) {

            Log.d("CHATFRAGMENT", "   ChatActivity   sendChatMessage QBDialogType.PRIVATE  " + text);
            Toaster.shortToast("You're still joining a group chat, please wait a bit");
            return;
        }

        try {
            qbChatDialog.sendMessage(chatMessage);

            if (QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {

                Log.d("CHATFRAGMENT", "   ChatActivity   showMessage  IF    PRIVATE ");
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
        Log.d("MULTIUPDATE", " chatActivity initChat   ");
        switch (qbChatDialog.getType()) {
            case GROUP:
            case PUBLIC_GROUP:

                Log.d("MULTIUPDATE", "   ChatActivity    PUBLIC_GROUP  initChat");
                joinGroupChat();
                break;

            case PRIVATE:
                Log.d("MULTIUPDATE", "   ChatActivity     PRIVATE  initChat ");
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
            Log.d("MULTIUPDATE", "   ChatActivity    joinGroupChat");
            Log.d("CHATFRAGMENT", "   ChatActivity     joinGroupChat  ");

            progressBar.setVisibility(View.VISIBLE);
            ChatHelper.getInstance().join(qbChatDialog, new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void result, Bundle b) {

                    Log.d("MULTIUPDATE", "   ChatActivity    joinGroupChat onSuccess ");
                    Log.d("CHATFRAGMENT", "   ChatActivity     joinGroupChat  onSuccess  ");
                    if (snackbar != null) {
                        snackbar.dismiss();
                    }
                    loadDialogUsers();
                }

                @Override
                public void onError(QBResponseException e) {

                    Log.d("MULTIUPDATE", "   ChatActivity    joinGroupChat onError ");
                    Log.d("CHATFRAGMENT", "   ChatActivity     joinGroupChat  onError  ");
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

        Log.d("CHATFRAGMENT", "   ChatActivity     updateDialog ");
        try {
            ChatHelper.getInstance().updateDialogUsers(qbChatDialog, selectedUsers,
                    new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(QBChatDialog dialog, Bundle args) {
                            Log.d("CHATFRAGMENT", "   ChatActivity     updateDialog  onSuccess ");
                            qbChatDialog = dialog;
                            loadDialogUsers();
                        }

                        @Override
                        public void onError(QBResponseException e) {

                            Log.d("CHATFRAGMENT", "   ChatActivity     updateDialog  onError ");
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
            Log.d("MULTIUPDATE", "   ChatActivity    loadDialogUsers ");

            Log.d("CHATFRAGMENT", "   ChatActivity     loadDialogUsers  ");
            ChatHelper.getInstance().getUsersFromDialog(qbChatDialog, new QBEntityCallback<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {
                    Log.d("MULTIUPDATE", "   ChatActivity    loadDialogUsers  onSuccess ");

                    Log.d("CHATFRAGMENT", "   ChatActivity     loadDialogUsers  onSuccess  ");
                    setChatNameToActionBar();
                    loadChatHistory();
                }

                @Override
                public void onError(QBResponseException e) {

                    Log.d("CHATFRAGMENT", "   ChatActivity    loadDialogUsers   onError  ");
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

        Log.d("MULTIUPDATE", "   ChatActivity    setChatNameToActionBar 1 ");
        String chatName = QbDialogUtils.getDialogName(qbChatDialog);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(chatName);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);


        }
    }


    private void loadChatHistory() {
        try {

            Log.d("MULTIUPDATE", "   ChatActivity    loadChatHistory ");
            Log.d("CHATFRAGMENT", "   ChatActivity     loadChatHistory  ");
            ChatHelper.getInstance().loadChatHistory(qbChatDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {

                    Log.d("MULTIUPDATE", "   ChatActivity    loadChatHistory onSuccess  ");
                    Log.d("CHATFRAGMENT", "   ChatActivity     loadChatHistory  onSuccess ");
                    // The newest messages should be in the end of list,
                    // so we need to reverse list to show messages in the right order
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

                    Log.d("CHATFRAGMENT", "   ChatActivity     loadChatHistory  onError ");
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
        Log.d("CHATFRAGMENT", "   ChatActivity     deleteChat  ");
        ChatHelper.getInstance().deleteDialog(qbChatDialog, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.d("CHATFRAGMENT", "   ChatActivity     deleteChat   onSuccess ");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("CHATFRAGMENT", "   ChatActivity     deleteChat   onError ");
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
            Log.d("MULTIUPDATE", " chatActivity initChatConnectionListener   ");
            Log.d("CHATFRAGMENT", "   ChatActivity   initChatConnectionListener ");
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
            Log.d("MULTIUPDATE", "   ChatActivity    QbChatDialogMessageListenerImp qbChatMessage  " + qbChatMessage);
            Log.d("CHATFRAGMENT", "   ChatActivity     ChatMessageListener   processMessage  qbChatMessage " + qbChatMessage);
            showMessage(qbChatMessage);
        }
    }

    private void sendPushMessage(final String outMessage) {

        Log.d("CHATFRAGMENT", "sendPushMessage  outMessage " + outMessage);
        QBEvent qbEvent = new QBEvent();
        qbEvent.setNotificationType(QBNotificationType.PUSH);
        qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);
        qbEvent.setPushType(QBPushType.GCM);
        qbEvent.setMessage(outMessage);
        Log.d("CHATFRAGMENT", " sendPushMessage getRecipientId123   " + qbChatDialog.getRecipientId());
        StringifyArrayList<Integer> toIds = new StringifyArrayList<>();
        toIds.add(qbChatDialog.getRecipientId());  // to Id
        qbEvent.setUserIds(toIds);
        qbEvent.setId(QBSessionManager.getInstance().getSessionParameters().getUserId()); //   my id  (From Id )
        QBPushNotifications.createEvent(qbEvent).performAsync(new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle bundle) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("CHATFRAGMENT", " sendPushMessage  onSuccess  qbEvent123 " + qbEvent.getMessage());
            }

            @Override
            public void onError(QBResponseException e) {
//                sendPushMessage(outMessage);
                Log.d("CHATFRAGMENT", "  sendPushMessage onError123   " + e.getMessage());
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
//                Log.d("WATCHER", "startTyping : " + startTyping);
                qbChatDialog.setOccupantsIds(opId);
                qbChatDialog.sendIsTypingNotification();
            } else {
//                Log.d("WATCHER", "startTyping : " + startTyping);
                qbChatDialog.sendStopTypingNotification();
            }
        } catch (XMPPException | SmackException.NotConnectedException e) {
        }

    }

    // Typing Indicator

    QBChatDialogTypingListener typingListener = new QBChatDialogTypingListener() {
        @Override
        public void processUserIsTyping(String dialogId, Integer senderId) {
//            Log.d("WATCHER", "processUserIsTyping : " + dialogId);
            actionBar.setSubtitle(Constant.EXTRA_IS_TYPING);

        }

        @Override
        public void processUserStopTyping(String dialogId, Integer senderId) {
            actionBar.setSubtitle(Constant.EXTRA_STOP_TYPING);
        }
    };
    // Status Report after message reach to server

    QBChatDialogMessageSentListener messageSentListener = new QBChatDialogMessageSentListener() {
        @Override
        public void processMessageSent(String dialogId, QBChatMessage qbChatMessage) {
            try {
//            Log.d("WATCHER123", "   serve status   getRecipientId   qbChatDialog : " + qbChatDialog.getRecipientId());
//            Log.d("WATCHER123", "   serve status   getRecipientId   Message ID  : " + qbChatMessage.getId());
//            Toaster.longToast("Message  sent  to server ");
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
            Log.d("CHATFRAGMENT", "   QBChatDialogMessageSentListener   processMessageFailed");

//            Toaster.longToast("Message  sent  to server failure ");

        }

    };

    //  deliver status report from use2 to User1  &
    QBMessageStatusListener messageStatusListener = new QBMessageStatusListener() {
        @Override
        public void processMessageDelivered(String messageId, String dialogId, Integer userId) {

            try {
                Log.d("WATCHER123", "   deliver  status   getRecipientId   userId : " + userId);
                Log.d("WATCHER123", "   deliver  status   getRecipientId   Message ID  : " + messageId);
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

            Log.d("WATCHER123", "   read  status   getRecipientId   userId : " + userId);
            Log.d("WATCHER123", "   read  status   getRecipientId   Message ID  : " + messageId);
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


        Log.d("CAHTCALLLL", "   CAHTCALLLL groupCall   start " + callType);
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
                throw new RuntimeException("User from dialog is not in memory. This should never happen, or we are screwed");

            }
//                qbUsers.add(qbUsers.get(id));
            qbUsers.remove(currentUser);
            Log.d("CHATFRAGMENT", "groupCall()==>  user NAME " + qbUsers);
        }
        Log.d("CHATFRAGMENT", "groupCall()==>  qbUsers.size() " + qbUsers.size());
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
}