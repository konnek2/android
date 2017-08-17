package com.quickblox.sample.chat.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.sample.chat.App;
import com.quickblox.sample.chat.R;
import com.quickblox.sample.chat.ui.adapter.CheckboxUsersAdapter;
import com.quickblox.sample.chat.utils.Common;
import com.quickblox.sample.chat.utils.Constant;
import com.quickblox.sample.chat.utils.StringUtils;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SelectUsersActivity extends BaseActivity {

    public static final String RESULT_CODE = "resultCode";
    public static final String REQUEST_CODE = "requestCode";
    public static final String EXTRA_QB_USERS = "qb_users";
    public static final int MINIMUM_CHAT_OCCUPANTS_SIZE = 2;
    private static final long CLICK_DELAY = TimeUnit.SECONDS.toMillis(2);

    private static final String EXTRA_QB_DIALOG = "qb_dialog";

    private ListView usersListView;
    private ProgressBar progressBar;
    private CheckboxUsersAdapter usersAdapter;
    private long lastClickTime = 0l;
    Toolbar toolbar;
    SharedPrefsHelper sharedPrefsHelper;
    ArrayList<QBUser> selectedUsers;
    ArrayList<QBUser> qbUserArrayList;

    public static void start(Context context) {
        Intent intent = new Intent(context, SelectUsersActivity.class);
        context.startActivity(intent);
    }

    public static void startForResult(Activity activity, int code) {
        startForResult(activity, code, null);
    }

    public static void startForResult(Activity activity, int code, QBChatDialog dialog) {
        Intent intent = new Intent(activity, SelectUsersActivity.class);
        intent.putExtra(EXTRA_QB_DIALOG, dialog);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_users);
        toolbar = (Toolbar) findViewById(R.id.toolbar_select_user);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chat_back);
        progressBar = _findViewById(R.id.progress_select_users);
        usersListView = _findViewById(R.id.list_select_users);

        if (isEditingChat()) {
            setActionBarTitle(R.string.select_users_edit_chat);
        } else {
            setActionBarTitle(R.string.select_users_create_chat);
        }

        loadUsersFromQb();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_select_users, menu);
        if (qbUserArrayList.size() == 0 && qbUserArrayList == null) {

            menu.getItem(0).setVisible(false);

        } else {
            menu.getItem(0).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((SystemClock.uptimeMillis() - lastClickTime) < CLICK_DELAY) {
            return super.onOptionsItemSelected(item);
        }
        lastClickTime = SystemClock.uptimeMillis();

        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
        }
        if (i == R.id.menu_select_people_action_done) {

            Intent intent = new Intent(Constant.NOTIFY_MULTIPLE);

            if (qbUserArrayList != null && usersAdapter.getSelectedUsers().size() > Constant.SELECTED_USER_SIZE) {
                selectedUsers = new ArrayList<>(usersAdapter.getSelectedUsers());
                intent.putExtra(EXTRA_QB_USERS, selectedUsers);
                intent.putExtra(RESULT_CODE, -1);
                intent.putExtra(REQUEST_CODE, 174);
                sendBroadcast(intent);
                onBackPressed();

            } else {
                Toaster.shortToast(R.string.select_users_choose_users);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected View getSnackbarAnchorView() {
        return findViewById(R.id.layout_root);
    }

    private void passResultToCallerActivity() {
        Intent result = new Intent();
        ArrayList<QBUser> selectedUsers = new ArrayList<>(usersAdapter.getSelectedUsers());
        result.putExtra(EXTRA_QB_USERS, selectedUsers);
        setResult(RESULT_OK, result);
        finish();
    }

    public void loadUsersFromQb() {
        qbUserArrayList = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        tags.add(App.getSampleConfigs().getUsersTag());

        progressBar.setVisibility(View.VISIBLE);
        QBUsers.getUsersByTags(tags, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                QBChatDialog dialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_QB_DIALOG);
                sharedPrefsHelper = SharedPrefsHelper.getInstance();
                QBUser currentUser = sharedPrefsHelper.getQbUser();
                int i = 0;
                while (i < result.size() && result.size() != 0) {
                    if (result.get(i).equals(currentUser)) {
                        result.remove(currentUser);
                        i = 0;
                    } else {
                        i++;
                    }
                }
                qbUserArrayList = result;
                usersAdapter = new CheckboxUsersAdapter(SelectUsersActivity.this, result, true);
                if (dialog != null) {
                    usersAdapter.addSelectedUsers(dialog.getOccupants());
                }
                usersListView.setAdapter(usersAdapter);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean isEditingChat() {
        return getIntent().getSerializableExtra(EXTRA_QB_DIALOG) != null;
    }
}
