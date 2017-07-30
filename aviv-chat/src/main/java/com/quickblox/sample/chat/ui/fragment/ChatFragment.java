
package com.quickblox.sample.chat.ui.fragment;

import android.app.Activity;
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
import android.widget.Toast;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
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
            Log.d("ChatFragment", "getDialogs().size() > 0 IF ");
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_dialogs, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (isProcessingResultInProgress) {
//            return super.onOptionsItemSelected(item);
//        }
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
//            Toaster.shortToast("In Progress");
//            Intent intent = new Intent(com.quickblox.sample.chat.utils.StringUtils.NOTIFY_ONE_TO_ONE);
//            getActivity().send""Broadcast(intent);
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
        Log.d("ChatFragment", "onActivityResult ");
        if (resultCode == RESULT_OK) {
            isProcessingResultInProgress = true;
            if (requestCode == REQUEST_SELECT_PEOPLE) {
                ArrayList<QBUser> selectedUsers = (ArrayList<QBUser>) data
                        .getSerializableExtra(EXTRA_QB_USERS);

                if (isPrivateDialogExist(selectedUsers)) {
                    Log.d("ChatFragment", "onActivityResult isPrivateDialogExist ");
                    selectedUsers.remove(ChatHelper.getCurrentUser());
                    QBChatDialog existingPrivateDialog = QbDialogHolder.getInstance().getPrivateDialogWithUser(selectedUsers.get(0));
                    isProcessingResultInProgress = false;
                    ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog);
                } else {
                    Log.d("ChatFragment", "onActivityResult isPrivateDialogExist ELSE ");
//                    ProgressDialogFragment.show(getFragmentManager(), R.string.create_chat);
                    progressBar.setVisibility(View.VISIBLE);
                    createDialog(selectedUsers);
                }
            } else if (requestCode == REQUEST_DIALOG_ID_FOR_UPDATE) {

                Log.d("ChatFragment", "REQUEST_DIALOG_ID_FOR_UPDATE else if ");
                if (data != null) {
                    Log.d("ChatFragment", "REQUEST_DIALOG_ID_FOR_UPDATE else if data != null ");
                    String dialogId = data.getStringExtra(EXTRA_DIALOG_ID);
                    loadUpdatedDialog(dialogId);
                } else {
                    Log.d("ChatFragment", "REQUEST_DIALOG_ID_FOR_UPDATE  ELSE ");
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
            Log.d("ChatFragment", "isPrivateDialogExist ");
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
            Log.d("ChatFragment", "    loadUpdatedDialog  ");
            Log.d("ChatFragment", "loadUpdatedDialog dialogId " + dialogId);

            ChatHelper.getInstance().getDialogById(dialogId, new QbEntityCallbackImpl<QBChatDialog>() {
                @Override
                public void onSuccess(QBChatDialog result, Bundle bundle) {

                    Log.d("ChatFragment", "    loadUpdatedDialog  onSuccess  " + result);
                    isProcessingResultInProgress = false;
                    QbDialogHolder.getInstance().addDialog(result);

                    updateDialogsAdapter();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d("ChatFragment", "loadUpdatedDialog  onError");
                    isProcessingResultInProgress = false;
                }
            });

        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void adapterNotify() {
        Log.d("MULTIUPDATE", "adapterNotify 1 ");
        dialogsAdapter = new DialogsAdapter(getActivity(), new ArrayList<>(QbDialogHolder.getInstance().getDialogs().values()));
        dialogsListView.setAdapter(dialogsAdapter);
        dialogsAdapter.notifyDataSetChanged();
        Log.d("MULTIUPDATE", "adapterNotify 2 ");
    }

    public ActionMode startSupportActionMode(ActionMode.Callback callback) {

        Log.d("ChatFragment", "ActionMode ");
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
        Log.d("ChatFragment", "  updateDialogsList  ");
        requestBuilder.setSkip(skipRecords = 0);
        Log.d("MULTIUPDATE", "updateDialogsList  IF ");
        loadDialogsFromQb(true, true);
    }

    private void initUi(View view) {

        Log.d("ChatFragment", "initUi  ");
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


                Log.d("ChatFragment", "OnItemClick  list 1 " + parent.getItemAtPosition(position));
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

                Log.d("ChatFragment", "LongClick  list 2 " + parent.getItemAtPosition(position));
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
                            Log.d("ChatFragment", "createDialog  onSuccess  dialog" + dialog.getRoomJid());
                            Log.d("ChatFragment", "createDialog  onSuccess  dialog" + dialog.getDialogId());
                            isProcessingResultInProgress = false;
                            dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, dialog);
                            ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, dialog);
//                            ProgressDialogFragment.hide(getFragmentManager());
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Log.d("ChatFragment", "  createDialog onError");
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
            Log.d("ChatFragment", "loadDialogsFromQb " + silentUpdate + "====> " + clearDialogHolder);
            isProcessingResultInProgress = true;
            if (!silentUpdate) {
                progressBar.setVisibility(View.VISIBLE);
            }
            ChatHelper.getInstance().getDialogs(requestBuilder, new QBEntityCallback<ArrayList<QBChatDialog>>() {
                @Override
                public void onSuccess(ArrayList<QBChatDialog> dialogs, Bundle bundle) {
                    Log.d("ChatFragment", "  loadDialogsFromQb  onSuccess");
                    isProcessingResultInProgress = false;
                    progressBar.setVisibility(View.GONE);
                    if (clearDialogHolder) {
                        Log.d("ChatFragment", "  loadDialogsFromQb  clearDialogHolder IF ");
                        QbDialogHolder.getInstance().clear();
                    }
                    QbDialogHolder.getInstance().addDialogs(dialogs);
                    updateDialogsAdapter();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d("ChatFragment", "  loadDialogsFromQb  onError");
                    isProcessingResultInProgress = false;
                    progressBar.setVisibility(View.GONE);
                }
            });

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void updateDialogsAdapter() {
        Log.d("ChatFragment", "updateDialogsAdapter   ");
        if (dialogsAdapter != null) {
            Log.d("ChatFragment", "updateDialogsAdapter dialogsAdapter !=null IF   ");
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
        Log.d("IMAGEDOWN", "imageIdCallBack : " + imageId);
        if (!FolderCreator.isCheckFileExit(userId))
            userImageDonload(userId, imageId);

    }

    private class DeleteActionModeCallback implements ActionMode.Callback {
        public DeleteActionModeCallback() {
            fab.hide();
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
            fab.show();
        }

        private void deleteSelectedDialogs() {
            Log.d("ChatFragment", "  deleteSelectedDialogs");
            final Collection<QBChatDialog> selectedDialogs = dialogsAdapter.getSelectedItems();
            try {
                ChatHelper.getInstance().deleteDialogs(selectedDialogs, new QBEntityCallback<ArrayList<String>>() {
                    @Override
                    public void onSuccess(ArrayList<String> dialogsIds, Bundle bundle) {
                        Log.d("ChatFragment", "  deleteSelectedDialogs  onSuccess ");
                        QbDialogHolder.getInstance().deleteDialogs(dialogsIds);
                        updateDialogsAdapter();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        deleteSelectedDialogs();
                        Log.d("ChatFragment", "  deleteSelectedDialogs  onError ");
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
            Log.d("ChatFragment", "  PushBroadcastReceiver   EXTRA_GCM_MESSAGE " + intent.getStringExtra("message"));
            // Get extra data included in the Intent
//            String message = intent.getStringExtra(GcmConsts.EXTRA_GCM_MESSAGE);
            String message = intent.getStringExtra("message");
            Log.v(TAG, "Received broadcast " + intent.getAction() + " with data: " + message);
            requestBuilder.setSkip(skipRecords = 0);
            Log.d("ChatFragment", "  PushBroadcastReceiver   message " + message);
            loadDialogsFromQb(true, true);
        }
    }

    private class SystemMessagesListener implements QBSystemMessageListener {
        @Override
        public void processMessage(final QBChatMessage qbChatMessage) {
            dialogsManager.onSystemMessageReceived(qbChatMessage);
            adapterNotify();
            Log.d("ChatFragment", "SystemMessagesListener 1234 ");
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
        Log.d("ChatFragment", "  loadUsersFromQb");
        List<String> tags = new ArrayList<>();
        dialogsListView.setVisibility(View.GONE);
        try {

            Log.d("ChatFragment", "  loadUsersFromQb  .getUsersTag() " + App.getSampleConfigs().getUsersTag());
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
                        App.tableManagerDAO.databaseDB(App.getInstance());
                        loadQbUsers(qbUsersList);
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d("ChatFragment", "  loadUsersFromQb onError" + e.getMessage());
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
        Log.d("QB_USER_SIZE", " SIZE FROM SQLITE (): " + qbUsers.size());
        if (qbUsers.size() != 0 && qbUsers != null) {
            for (int j = 0; j < qbUsers.size(); j++) {
                ArrayList<QBUser> qbUserslist = new ArrayList<>();
                qbUserslist.add(qbUsers.get(j));
                oneToOneCreatedDialogs(qbUserslist);
                getProfileImageId(qbUsers.get(j).getId());
            }
        }
        Log.d("QB_USER_SIZE", " SIZE FROM SQLITE (): " + qbUsers.size());
        dialogsAdapter = new DialogsAdapter(getActivity(), qbChatDialogs);
        dialogsListView.setAdapter(dialogsAdapter);
        progressBar.setVisibility(View.GONE);
        dialogsListView.setVisibility(View.VISIBLE);

    }

    private void passResultToCallerActivity() {

        Log.d("ChatFragment", "  passResultToCallerActivity");
        Intent result = new Intent();
        ArrayList<QBUser> selectedUsers = new ArrayList<>(usersAdapter.getSelectedUsers());
        result.putExtra(EXTRA_QB_USERS, selectedUsers);
        getActivity().setResult(RESULT_OK, result);

//        getActivity().finish();
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
            Log.d("MULTIUPDATE", "BroadcastReceiver   ");
            Log.d("MULTIUPDATE", "BroadcastReceiver onReceive RESULT_CODE   " + intent.getSerializableExtra(RESULT_CODE));
            Log.d("MULTIUPDATE", "BroadcastReceiver onReceive   REQUEST_CODE  " + intent.getSerializableExtra(REQUEST_CODE));
            Log.d("MULTIUPDATE", "BroadcastReceiver onReceive   EXTRA_DIALOG_ID  " + intent.getSerializableExtra(EXTRA_DIALOG_ID));
            try {
                int resultCode = (int) intent.getSerializableExtra(RESULT_CODE);
                int requestCode = (int) intent.getSerializableExtra(REQUEST_CODE);
                String qbChatDialogID = (String) intent.getSerializableExtra(EXTRA_DIALOG_ID);

                Log.d("MULTIUPDATE", "BroadcastReceiver resultCode : : :   " + resultCode + "  requestCode  : : : " + requestCode + "qbChatDialogID : :  :" + qbChatDialogID);

                if (resultCode == RESULT_OK) {
                    Log.d("MULTIUPDATE", "BroadcastReceiver RESULT_OK  IF  " + RESULT_OK);
                    isProcessingResultInProgress = true;
                    if (requestCode == REQUEST_SELECT_PEOPLE) {
                        Log.d("MULTIUPDATE", "BroadcastReceiver REQUEST_SELECT_PEOPLE  " + REQUEST_SELECT_PEOPLE);
                        ArrayList<QBUser> selectedUsers = (ArrayList<QBUser>) intent
                                .getSerializableExtra(EXTRA_QB_USERS);
                        Log.d("MULTIUPDATE", "BroadcastReceiver selectedUsers    " + selectedUsers.size());
                        Log.d("MULTIUPDATE", "BroadcastReceiver selectedUsers getFileId   " + selectedUsers.get(0).getFileId());
                        Log.d("SURESH123", "BroadcastReceiver selectedUsers getFileId   " + selectedUsers.get(0).getFileId());
                        Log.d("SURESH123", "BroadcastReceiver selectedUsers getId   " + selectedUsers.get(0).getId());

                        if (isPrivateDialogExist(selectedUsers)) {
                            Log.d("MULTIUPDATE", "BroadcastReceiver selectedUsers IF   " + selectedUsers.size());
                            selectedUsers.remove(ChatHelper.getCurrentUser());
                            QBChatDialog existingPrivateDialog = QbDialogHolder.getInstance().getPrivateDialogWithUser(selectedUsers.get(0));
                            isProcessingResultInProgress = false;
                            ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog);
                        } else {
                            Log.d("MULTIUPDATE", "BroadcastReceiver selectedUsers ELSE 007   " + selectedUsers.size());
//                    ProgressDialogFragment.show(getFragmentManager(), R.string.create_chat);
                            progressBar.setVisibility(View.VISIBLE);
                            createDialogs(selectedUsers);
                        }
                    } else if (requestCode == REQUEST_DIALOG_ID_FOR_UPDATE) {
                        Log.d("MULTIUPDATE", "BroadcastReceiver REQUEST_DIALOG_ID_FOR_UPDATE  " + REQUEST_DIALOG_ID_FOR_UPDATE);
                        if (intent != null) {
                            Log.d("MULTIUPDATE", "BroadcastReceiver intent !=null  ");
                            String dialogId = intent.getStringExtra(EXTRA_DIALOG_ID);
                            Log.d("MULTIUPDATE", "BroadcastReceiver intent !=null  dialogId  " + dialogId);
                            loadUpdatedDialog(dialogId);
                        } else {
                            Log.d("MULTIUPDATE", "BroadcastReceiver REQUEST_DIALOG_ID_FOR_UPDATE ELSE  ");
                            isProcessingResultInProgress = false;
                            updateDialogsList();
                        }
                    }
                } else {
                    Log.d("MULTIUPDATE", "updateDialogsList  RESULT_OK  ELSE");
                    updateDialogsAdapter();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };


    public void createDialogs(final ArrayList<QBUser> selectedUsers) {

        Log.d("SURESH123", "createDialogs  selectedUsers " + selectedUsers);
        Log.d("SURESH123", "createDialogs  selectedUsers getId ===>" + selectedUsers.get(0).getId());
        Log.d("SURESH123", "createDialogs  selectedUsers getFileId ===>" + selectedUsers.get(0).getFileId());
        ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers,
                new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {
                        Log.d("MULTIUPDATE", "createDialogs  onSuccess " + dialog);
                        Log.d("ChatFragment", "createDialogs  onSuccess " + dialog);
                        Log.d("MULTIUPDATE", "createDialogs  onSuccess  dialog getRoomJid " + dialog.getRoomJid());
                        Log.d("MULTIUPDATE", "createDialogs  systemMessagesManager  " + systemMessagesManager);
                        if (selectedUsers.size() > 0) {
                            Log.d("MULTIUPDATE", "createDialogs  IF >0 ");
                            Log.d("ChatFragment", "createDialogs  IF >0 ");
                            isProcessingResultInProgress = false;
                            dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, dialog);
                            ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, dialog);
//                                ProgressDialogFragment.hide(getFragmentManager());
                            progressBar.setVisibility(View.GONE);
//                                    usersAdapter.clearSelectedUsers();
                        } else {
                            Log.d("MULTIUPDATE", "createDialogs  ELSE");
                            Toaster.shortToast("Select at least one User Many");
                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.d("CHATFRAGMENT", " oneToManyUpdate createDialog  onError  ");
                        Log.d("ChatFragment", " oneToManyUpdate createDialog  onError  ");
                        isProcessingResultInProgress = false;
//                                ProgressDialogFragment.hide(getFragmentManager());
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }


    public void oneToOneCreatedDialogs(final ArrayList<QBUser> selectedUsers) {
        Log.d("ONE_TO_ONE", "oneToOneCreatedDialogs  selectedUsers SIZE " + selectedUsers.size());
        Log.d("ONE_TO_ONE", "oneToOneCreatedDialogs  selectedUsers SIZE " + selectedUsers.get(0).getFullName());
        ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers,
                new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {
//                        Log.d("ChatFragment", "oneToOneCreatedDialogs  onSuccess " + dialog);
                        Log.d("ONE_TO_ONE", "oneToOneCreatedDialogs  onSuccess  dialog getRoomJid " + dialog.getRoomJid());
                        Log.d("ONE_TO_ONE", "oneToOneCreatedDialogs  systemMessagesManager  " + systemMessagesManager);
                        if (selectedUsers.size() > 0) {
                            Log.d("ONE_TO_ONE", "oneToOneCreatedDialogs  IF >0 ");
                            isProcessingResultInProgress = false;
                            dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, dialog);
//                            ArrayList<QBChatDialog> qbChatDialogs = new ArrayList<QBChatDialog>();
                            qbChatDialogs.add(dialog);
//                            dialogsAdapter = new DialogsAdapter(getActivity(), qbChatDialogs);
////                            loadUpdatedDialog(dialog.getDialogId());
//                            dialogsListView.setAdapter(dialogsAdapter);
//                            progressBar.setVisibility(View.GONE);
//                                    usersAdapter.clearSelectedUsers();
                        } else {
                            Log.d("ONE_TO_ONE", "oneToOneCreatedDialogs  ELSE");
                            Toaster.shortToast("Select at least one User Many");
                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.d("ONE_TO_ONE", " oneToOneCreatedDialogs Error getMessage " + e.getMessage());
                        isProcessingResultInProgress = false;
//                                ProgressDialogFragment.hide(getFragmentManager());
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }


    public void getProfileImageId(int userId) {

//        if (!FolderCreator.isCheckFileExit(String.valueOf(userId))) {

        String UserId = String.valueOf(userId);
        Log.d("profileImageDownload", "userId : " + UserId);
        if (UserId != null) {

            appImageIdPresenter.validateImageId(String.valueOf(userId));
        }
//        }


    }

    public void userImageDonload(final String userId, String imageId) {
        Log.d("IMAGEDOWN", "down======imageId  >>>>>>" + imageId);

        final int fileId = Integer.parseInt(imageId);
        Bundle params = new Bundle();

        QBContent.downloadFileById(fileId, params, new QBProgressCallback() {
            @Override
            public void onProgressUpdate(int progress) {
                Log.i("IMAGEDOWN", "onProgressUpdate " + progress);

            }
        }).performAsync(new QBEntityCallback<InputStream>() {
            @Override
            public void onSuccess(final InputStream inputStream, Bundle params) {

                long length = params.getLong(Consts.CONTENT_LENGTH_TAG);
                Log.i(TAG, "content.length: " + length);
                Log.d("IMAGEDOWN", "down  onSuccess  length =====" + length);

                downloadFile(getActivity(), userId, inputStream, getActivity());
            }

            @Override
            public void onError(QBResponseException errors) {

                Log.d("IMAGEDOWN", "down  onError  length getMessage  =====" + errors.getMessage());

            }
        });


    }

    public static void downloadFile(final Context context, final String userId, final InputStream inputStream, final Context ctx) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                Log.d("IMAGEDOWN", "down  downloadFile  length =====");
                try {
                    String filePath = FolderCreator.getProfileImagePath() + "/" + userId + ".png";
                    File file = new File(filePath);
                    OutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        stream.write(buffer, 0, len);
                        Log.d("IMAGEDOWN", "down  downloadFile  while");
                    }
                    if (stream != null) {
                        Lo.g("download done");
                        Log.d("IMAGEDOWN", "down download done");
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

//


}