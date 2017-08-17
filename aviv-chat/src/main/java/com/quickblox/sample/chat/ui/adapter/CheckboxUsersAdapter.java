package com.quickblox.sample.chat.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.quickblox.sample.chat.R;
import com.quickblox.sample.chat.utils.Common;
import com.quickblox.sample.chat.utils.Constant;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class CheckboxUsersAdapter extends UsersAdapter {

    private List<Integer> initiallySelectedUsers;
    private List<QBUser> selectedUsers;
    private boolean checkBoxFlag;

    public CheckboxUsersAdapter(Context context, List<QBUser> users,boolean checkBoxFlag) {
        super(context, users);
        this.selectedUsers = new ArrayList<>();
        this.selectedUsers.add(currentUser);
        this.initiallySelectedUsers = new ArrayList<>();
        this.checkBoxFlag = checkBoxFlag;
    }

    public void addSelectedUsers(List<Integer> userIds) {
        for (QBUser user : objectsList) {
            for (Integer id : userIds) {
                if (user.getId().equals(id)) {
                    selectedUsers.add(user);
                    initiallySelectedUsers.add(user.getId());
                    break;
                }
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);


        final QBUser user = getItem(position);
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.layout_userList.setTag(user);
        try {
            userOnlineStatus(user, holder);

        } catch (Exception e) {
            e.getMessage();
        }

        holder.layout_userList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedUsers.add(user);

                holder.layout_userList.setBackgroundResource(R.drawable.listselector);
                Intent intent = new Intent(Constant.NOTIFY_ONE_TO_ONE);
                context.sendBroadcast(intent);

            }
        });

        if (checkBoxFlag) {
            holder.userCheckBox.setVisibility(View.VISIBLE);
            holder.layout_userList.setOnClickListener(null);
            holder.userCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.userCheckBox.isChecked()) {
                        selectedUsers.add(user);

                    } else {
                        selectedUsers.remove(user);
                    }

                }
            });

        } else {
            holder.userCheckBox.setVisibility(View.GONE);

        }

        return view;
    }

    public List<QBUser> getSelectedUsers() {
        return selectedUsers;
    }

    public void clearSelectedUsers() {
        selectedUsers.clear();

    }

    public void userOnlineStatus(QBUser user, ViewHolder holder) {

        long currentTime = System.currentTimeMillis();
        long userLastRequestAtTime = user.getLastRequestAt().getTime();
        if ((currentTime - userLastRequestAtTime) > 5 * 60 * 1000) {
            holder.userStatus.setImageResource(R.drawable.signal_read);

        }

    }

    @Override
    protected boolean isAvailableForSelection(QBUser user) {
        return super.isAvailableForSelection(user) && !initiallySelectedUsers.contains(user.getId());
    }
}
