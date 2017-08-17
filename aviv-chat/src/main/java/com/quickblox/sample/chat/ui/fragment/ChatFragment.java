
package com.quickblox.sample.chat.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.content.QBContent;
import com.quickblox.core.Consts;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.Lo;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.sample.chat.App;
import com.quickblox.sample.chat.R;
import com.quickblox.sample.chat.interfaces.ImageIdDownloadView;
import com.quickblox.sample.chat.managers.DialogsManager;
import com.quickblox.sample.chat.models.MessageStatusModel;
import com.quickblox.sample.chat.network.AppImageIdPresenter;
import com.quickblox.sample.chat.ui.activity.ChatActivity;
import com.quickblox.sample.chat.ui.activity.DialogsActivity;
import com.quickblox.sample.chat.ui.activity.LoginActivity;
import com.quickblox.sample.chat.ui.activity.SelectUsersActivity;
import com.quickblox.sample.chat.ui.adapter.CheckboxUsersAdapter;
import com.quickblox.sample.chat.ui.adapter.DialogsAdapter;
import com.quickblox.sample.chat.ui.adapter.UsersAdapter;
import com.quickblox.sample.chat.utils.Common;
import com.quickblox.sample.chat.utils.Constant;
import com.quickblox.sample.chat.utils.FolderCreator;
import com.quickblox.sample.chat.utils.StringUtils;
import com.quickblox.sample.chat.utils.chat.ChatHelper;
import com.quickblox.sample.chat.utils.qb.QbChatDialogMessageListenerImp;
import com.quickblox.sample.chat.utils.qb.QbDialogHolder;
import com.quickblox.sample.chat.utils.qb.callback.QbEntityCallbackImpl;
import com.quickblox.sample.core.gcm.GooglePlayServicesHelper;
import com.quickblox.sample.core.ui.dialog.ProgressDialogFragment;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.sample.core.utils.constant.GcmConsts;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.quickblox.sample.chat.ui.activity.ChatActivity.EXTRA_DIALOG_ID;
import static com.quickblox.sample.chat.ui.activity.SelectUsersActivity.EXTRA_QB_USERS;
import static com.quickblox.sample.chat.ui.activity.SelectUsersActivity.REQUEST_CODE;
import static com.quickblox.sample.chat.ui.activity.SelectUsersActivity.RESULT_CODE;

public class ChatFragment extends Fragment implements DialogsManager.ManagingDialogsCallbacks, ImageIdDownloadView {

    private static final String TAG = DialogsActivity.class.getSimpleName();
    private static final int REQUEST_SELECT_PEOPLE = 174;
    private static final int REQUEST_DIALOG_ID_FOR_UPDATE = 165;
    private static final String EXTRA_QB_DIALOG = "qb_dialog";
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private ActionMode currentActionMode;
    private QBRequestGetBuilder requestBuilder;
    private Menu menu;
    private int skipRecords = 0;
    private boolean isProcessingResultInProgress;
    LinearLayout emptyHintLayout;
    private BroadcastReceiver pushBroadcastReceiver;
    private GooglePlayServicesHelper googlePlayServicesHelper;
    private DialogsAdapter dialogsAdapter;
    private QBChatDialogMessageListener allDialogsMessagesListener;
    private SystemMessagesListener systemMessagesListener;
    private QBSystemMessagesManager systemMessagesManager;
    private QBIncomingMessagesManager incomingMessagesManager;
    private DialogsManager dialogsManager;
    private QBUser currentUser;
    ListView dialogsListView;
    private CheckboxUsersAdapter usersAdapter;
    SharedPrefsHelper sharedPrefsHelper;
    ArrayList<QBUser> qbUsersList;
    ArrayList<QBUser> qbUsers;
    AppImageIdPresenter appImageIdPresenter;

    /*New Implemendation*/
    private ArrayList<QBChatDialog> qbChatDialogs;
    long currentTime;
    long userLastRequestAtTime;

    public static void start(Context context) {
        Intent intent = new Intent(context, DialogsActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        googlePlayServicesHelper = new GooglePlayServicesHelper();
        pushBroadcastReceiver = new PushBroadcastReceiver();
        allDialogsMessagesListener = new AllDialogsMessageListener();
        systemMessagesListener = new SystemMessagesListener();
        dialogsManager = new DialogsManager();
        currentUser = ChatHelper.getCurrentUser();

        registerQbChatListeners();
        if (QbDialogHolder.getInstance().getDialogs().size() > 0) {

            loadDialogsFromQb(true, true);
        } else {
            loadDialogsFromQb(false, true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.activity_dialogs, container, false);
        setHasOptionsMenu(true);
        initUi(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        googlePlayServicesHelper.checkPlayServicesAvailable(getActivity());

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(pushBroadcastReceiver,
                new IntentFilter(GcmConsts.ACTION_NEW_GCM_EVENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(pushBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterQbChatListeners();
        getActivity().unregisterReceiver(MultipleUpdate);
        getActivity().unregisterReceiver(profileImageBroadCast);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_dialogs, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//
        int i = item.getItemId();
        if (i == R.id.menu_dialogs_action_logout) {
            userLogout();
            item.setEnabled(false);
            getActivity().invalidateOptionsMenu();
            return true;
        } else if (i == R.id.menu_refresh_dialogs) {
            loadUsersFromQb();
            try {
//                adapterNotify();
            } catch (Exception e) {
                e.getMessage();
            }
            getActivity().invalidateOptionsMenu();
            return true;
        } else if (i == R.id.menu_group_chat1) {

            SelectUsersActivity.startForResult(getActivity(), REQUEST_SELECT_PEOPLE);
            getActivity().invalidateOptionsMenu();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            isProcessingResultInProgress = true;
            if (requestCode == REQUEST_SELECT_PEOPLE) {
                ArrayList<QBUser> selectedUsers = (ArrayList<QBUser>) data
                        .getSerializableExtra(EXTRA_QB_USERS);

                if (isPrivateDialogExist(selectedUsers)) {

                    selectedUsers.remove(ChatHelper.getCurrentUser());
                    QBChatDialog existingPrivateDialog = QbDialogHolder.getInstance().getPrivateDialogWithUser(selectedUsers.get(0));
                    isProcessingResultInProgress = false;
                    ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog);
                } else {

//                    ProgressDialogFragment.show(getFragmentManager(), R.string.create_chat);
                    progressBar.setVisibility(View.VISIBLE);
                    createDialog(selectedUsers);
                }
            } else if (requestCode == REQUEST_DIALOG_ID_FOR_UPDATE) {

                if (data != null) {

                    String dialogId = data.getStringExtra(EXTRA_DIALOG_ID);
                    loadUpdatedDialog(dialogId);
                } else {

                    isProcessingResultInProgress = false;
                    updateDialogsList();
                }
            }
        } else {
            updateDialogsAdapter();
        }
    }

    private boolean isPrivateDialogExist(ArrayList<QBUser> allSelectedUsers) {
        ArrayList<QBUser> selectedUsers = new ArrayList<>();
        try {

            selectedUsers.addAll(allSelectedUsers);
            selectedUsers.remove(ChatHelper.getCurrentUser());
        } catch (Exception e) {
            e.getMessage();
        }
        return selectedUsers.size() == 1 && QbDialogHolder.getInstance().hasPrivateDialogWithUser(selectedUsers.get(0));
    }

    private void
    loadUpdatedDialog(String dialogId) {

        try {


            ChatHelper.getInstance().getDialogById(dialogId, new QbEntityCallbackImpl<QBChatDialog>() {
                @Override
                public void onSuccess(QBChatDialog result, Bundle bundle) {

                    isProcessingResultInProgress = false;
                    QbDialogHolder.getInstance().addDialog(result);

                    updateDialogsAdapter();
                }

                @Override
                public void onError(QBResponseException e) {

                    isProcessingResultInProgress = false;
                }
            });

        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void adapterNotify() {


        dialogsAdapter = new DialogsAdapter(getActivity(), new ArrayList<>(QbDialogHolder.getInstance().getDialogs().values()));
        dialogsListView.setAdapter(dialogsAdapter);
        dialogsAdapter.notifyDataSetChanged();

    }

    public ActionMode startSupportActionMode(ActionMode.Callback callback) {

        currentActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(callback);
        return currentActionMode;
    }

    private void userLogout() {
        ChatHelper.getInstance().destroy();
        SubscribeService.unSubscribeFromPushes(getActivity());
        SharedPrefsHelper.getInstance().removeQbUser();
        LoginActivity.start(getActivity());
        QbDialogHolder.getInstance().clear();
//        ProgressDialogFragment.hide(getSupportFragmentManager());
        getActivity().finish();
    }

    private void updateDialogsList() {
        requestBuilder.setSkip(skipRecords = 0);
        loadDialogsFromQb(true, true);
    }

    private void initUi(View view) {

        appImageIdPresenter = new AppImageIdPresenter(this);
        qbUsersList = new ArrayList<>();
        getActivity().registerReceiver(MultipleUpdate, new IntentFilter(Constant.NOTIFY_MULTIPLE));
        getActivity().registerReceiver(profileImageBroadCast, new IntentFilter(Constant.NOTIFY_PROFILE_IMAGE));
        dialogsListView = (ListView) view.findViewById(R.id.list_dialogs_chats);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_dialogs);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_dialogs_new_chat);

        TextView listHeader = (TextView) LayoutInflater.from(getActivity())
                .inflate(R.layout.include_list_hint_header, dialogsListView, false);
        listHeader.setText(R.string.select_users_list_hint);
        listHeader.setTextColor(getResources().getColor(R.color.colorPrimary));
        dialogsListView.setEmptyView(emptyHintLayout);
        dialogsListView.addHeaderView(listHeader, null, false);
        dialogsListView.setAdapter(dialogsAdapter);
        loadUsersFromQb();
        dialogsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                QBChatDialog selectedDialog = (QBChatDialog) parent.getItemAtPosition(position);
                if (currentActionMode == null) {
                    Log.d("ChatFragment", " currentActionMode  != null IF OnItemClick  list 2 " + parent.getItemAtPosition(position));
                    ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, selectedDialog);
                } else {
                    dialogsAdapter.toggleSelection(selectedDialog);
                }
            }
        });
        dialogsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                QBChatDialog selectedDialog = (QBChatDialog) parent.getItemAtPosition(position);
                startSupportActionMode(new DeleteActionModeCallback());
                dialogsAdapter.selectItem(selectedDialog);
                return true;
            }
        });
        requestBuilder = new QBRequestGetBuilder();


    }


    private void registerQbChatListeners() {
        try {
            incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
            systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
            if (incomingMessagesManager != null) {
                incomingMessagesManager.addDialogMessageListener(allDialogsMessagesListener != null
                        ? allDialogsMessagesListener : new AllDialogsMessageListener());
            }
            if (systemMessagesManager != null) {
                systemMessagesManager.addSystemMessageListener(systemMessagesListener != null
                        ? systemMessagesListener : new SystemMessagesListener());
            }
            dialogsManager.addManagingDialogsCallbackListener(this);
        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void unregisterQbChatListeners() {
        if (incomingMessagesManager != null) {
            incomingMessagesManager.removeDialogMessageListrener(allDialogsMessagesListener);
        }
        if (systemMessagesManager != null) {
            systemMessagesManager.removeSystemMessageListener(systemMessagesListener);
        }
        dialogsManager.removeManagingDialogsCallbackListener(this);
    }

    private void createDialog(final ArrayList<QBUser> selectedUsers) {
        try {
            ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers,
                    new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(QBChatDialog dialog, Bundle args) {

                            isProcessingResultInProgress = false;
                            dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, dialog);
                            ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, dialog);
//                            ProgressDialogFragment.hide(getFragmentManager());
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(QBResponseException e) {

                            isProcessingResultInProgress = false;
//                            ProgressDialogFragment.hide(getFragmentManager());
                            progressBar.setVisibility(View.GONE);
//                            showErrorSnackbar(R.string.dialogs_creation_error, null, null);
                            Toaster.longToast(R.string.dialogs_creation_error);
                        }
                    }
            );
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void loadDialogsFromQb(final boolean silentUpdate, final boolean clearDialogHolder) {
        try {

            isProcessingResultInProgress = true;
            if (!silentUpdate) {
                progressBar.setVisibility(View.VISIBLE);
            }
            ChatHelper.getInstance().getDialogs(requestBuilder, new QBEntityCallback<ArrayList<QBChatDialog>>() {
                @Override
                public void onSuccess(ArrayList<QBChatDialog> dialogs, Bundle bundle) {

                    isProcessingResultInProgress = false;
                    progressBar.setVisibility(View.GONE);
                    if (clearDialogHolder) {

                        QbDialogHolder.getInstance().clear();
                    }
                    QbDialogHolder.getInstance().addDialogs(dialogs);
                    updateDialogsAdapter();
                }

                @Override
                public void onError(QBResponseException e) {

                    isProcessingResultInProgress = false;
                    progressBar.setVisibility(View.GONE);
                }
            });

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void updateDialogsAdapter() {

        if (dialogsAdapter != null) {
            dialogsAdapter.updateList(new ArrayList<>(QbDialogHolder.getInstance().getDialogs().values()));
        } else {
            Common.displayToast("Error while Update Adapter");
        }

    }


    @Override
    public void onDialogCreated(QBChatDialog chatDialog) {

    }

    @Override
    public void onDialogUpdated(String chatDialog) {

    }

    @Override
    public void onNewDialogLoaded(QBChatDialog chatDialog) {

    }

    @Override
    public void setImageIdError() {

    }

    @Override
    public void setQbIdError() {

    }

    @Override
    public void imageIdCallBack(String userId, String imageId) {

        if (!FolderCreator.isCheckFileExit(userId))
            userImageDonload(userId, imageId);

    }

    private class DeleteActionModeCallback implements ActionMode.Callback {
        public DeleteActionModeCallback() {
//            fab.hide();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.action_mode_dialogs, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int i = item.getItemId();
            if (i == R.id.menu_dialogs_action_delete) {
                deleteSelectedDialogs();
                if (currentActionMode != null) {
                    currentActionMode.finish();
                }
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            currentActionMode = null;
            dialogsAdapter.clearSelection();
//            fab.show();
        }

        private void deleteSelectedDialogs() {

            final Collection<QBChatDialog> selectedDialogs = dialogsAdapter.getSelectedItems();
            try {
                ChatHelper.getInstance().deleteDialogs(selectedDialogs, new QBEntityCallback<ArrayList<String>>() {
                    @Override
                    public void onSuccess(ArrayList<String> dialogsIds, Bundle bundle) {

                        QbDialogHolder.getInstance().deleteDialogs(dialogsIds);
                        updateDialogsAdapter();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        deleteSelectedDialogs();

                        Toaster.longToast(R.string.dialogs_deletion_error);
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    private class PushBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            requestBuilder.setSkip(skipRecords = 0);
            loadDialogsFromQb(true, true);
        }
    }

    private class SystemMessagesListener implements QBSystemMessageListener {
        @Override
        public void processMessage(final QBChatMessage qbChatMessage) {
            dialogsManager.onSystemMessageReceived(qbChatMessage);
            adapterNotify();
            try {
            } catch (Exception e) {
                e.getMessage();
            }
        }

        @Override
        public void processError(QBChatException e, QBChatMessage qbChatMessage) {
        }
    }

    private class AllDialogsMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(final String dialogId, final QBChatMessage qbChatMessage, Integer senderId) {
            if (!senderId.equals(ChatHelper.getCurrentUser().getId())) {
                dialogsManager.onGlobalMessageReceived(dialogId, qbChatMessage);
            }
        }
    }

    private void loadUsersFromQb() {

        List<String> tags = new ArrayList<>();
        dialogsListView.setVisibility(View.GONE);
        try {


            tags.add(App.getSampleConfigs().getUsersTag());
            progressBar.setVisibility(View.VISIBLE);
            QBUsers.getUsersByTags(tags, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> result, Bundle params) {

                    try {

                        QBUser currentUser = ChatHelper.getCurrentUser();
                        result.remove(currentUser);
                        qbUsersList.clear();
                        qbUsersList = result;
                        App.messageStatusTableDAO.saveAllUsers(qbUsersList);
//                        App.tableManagerDAO.databaseDB(App.getInstance());
                        loadQbUsers(qbUsersList);

                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }

                @Override
                public void onError(QBResponseException e) {
                    loadUsersFromQb();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void loadQbUsers(ArrayList<QBUser> qbUsersList) {

        progressBar.setVisibility(View.VISIBLE);
        qbChatDialogs = new ArrayList<QBChatDialog>();
        qbChatDialogs.clear();
        qbUsers = App.messageStatusTableDAO.getAllQBUsers(qbUsersList);
        if (qbUsers.size() != 0 && qbUsers != null) {
            for (int j = 0; j < qbUsers.size(); j++) {
                ArrayList<QBUser> qbUserslist = new ArrayList<>();
                qbUserslist.add(qbUsers.get(j));
                oneToOneCreatedDialogs(qbUserslist);
                getProfileImageId(qbUsers.get(j).getId());

            }
        }

//
    }

    BroadcastReceiver profileImageBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dialogsAdapter.notifyDataSetChanged();
        }
    };

    BroadcastReceiver MultipleUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {


                int resultCode = (int) intent.getSerializableExtra(RESULT_CODE);
                int requestCode = (int) intent.getSerializableExtra(REQUEST_CODE);
                String qbChatDialogID = (String) intent.getSerializableExtra(EXTRA_DIALOG_ID);

                if (resultCode == RESULT_OK) {
                    isProcessingResultInProgress = true;
                    if (requestCode == REQUEST_SELECT_PEOPLE) {

                        ArrayList<QBUser> selectedUsers = (ArrayList<QBUser>) intent
                                .getSerializableExtra(EXTRA_QB_USERS);

                        if (isPrivateDialogExist(selectedUsers)) {

                            selectedUsers.remove(ChatHelper.getCurrentUser());
                            QBChatDialog existingPrivateDialog = QbDialogHolder.getInstance().getPrivateDialogWithUser(selectedUsers.get(0));
                            isProcessingResultInProgress = false;
                            ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog);
                        } else {
//                    ProgressDialogFragment.show(getFragmentManager(), R.string.create_chat);
                            progressBar.setVisibility(View.VISIBLE);
                            createDialogs(selectedUsers);
                        }
                    } else if (requestCode == REQUEST_DIALOG_ID_FOR_UPDATE) {
                        if (intent != null) {
                            String dialogId = intent.getStringExtra(EXTRA_DIALOG_ID);
                            loadUpdatedDialog(dialogId);
                        } else {
                            isProcessingResultInProgress = false;
                            updateDialogsList();
                        }
                    }
                } else {

                    updateDialogsAdapter();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };


    public void createDialogs(final ArrayList<QBUser> selectedUsers) {

        ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers,
                new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {

                        if (selectedUsers.size() > 0) {

                            isProcessingResultInProgress = false;
                            dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, dialog);
                            ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, dialog);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toaster.shortToast("Select at least one User Many");
                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        isProcessingResultInProgress = false;
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }


    public void oneToOneCreatedDialogs(final ArrayList<QBUser> selectedUsers) {

        ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers,
                new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {

                        if (selectedUsers.size() > 0) {
                            isProcessingResultInProgress = false;
                            dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, dialog);
                            qbChatDialogs.add(dialog);
                            dialogsAdapter = new DialogsAdapter(getActivity(), qbChatDialogs);
                            dialogsListView.setAdapter(dialogsAdapter);
                            progressBar.setVisibility(View.GONE);
                            dialogsListView.setVisibility(View.VISIBLE);
//
                        } else {
                            Toaster.shortToast("Select at least one User Many");
                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        isProcessingResultInProgress = false;
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }


    public void getProfileImageId(int userId) {
        String UserId = String.valueOf(userId);
        if (UserId != null) {

            appImageIdPresenter.validateImageId(String.valueOf(userId));
        }
//        }


    }

    public void userImageDonload(final String userId, String imageId) {

        final int fileId = Integer.parseInt(imageId);
        Bundle params = new Bundle();

        QBContent.downloadFileById(fileId, params, new QBProgressCallback() {
            @Override
            public void onProgressUpdate(int progress) {

            }
        }).performAsync(new QBEntityCallback<InputStream>() {
            @Override
            public void onSuccess(final InputStream inputStream, Bundle params) {

                long length = params.getLong(Consts.CONTENT_LENGTH_TAG);
                downloadFile(getActivity(), userId, inputStream, getActivity());
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });


    }

    public static void downloadFile(final Context context, final String userId, final InputStream inputStream, final Context ctx) {

        Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    String filePath = FolderCreator.getProfileImagePath() + "/" + userId + ".png";
                    File file = new File(filePath);
                    OutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        stream.write(buffer, 0, len);

                    }
                    if (stream != null) {
                        stream.close();
                        Intent intent = new Intent(Constant.NOTIFY_PROFILE_IMAGE);
                        context.sendBroadcast(intent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }


    public static void chatTrigger() {
        ChatFragment ctactFragment = new ChatFragment();

    }


}
