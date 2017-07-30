package com.quickblox.sample.chat.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickblox.chat.QBChatService;
import com.quickblox.sample.chat.R;
import com.quickblox.sample.core.ui.adapter.BaseListAdapter;
import com.quickblox.sample.core.utils.ResourceUtils;
import com.quickblox.sample.core.utils.UiUtils;
import com.quickblox.users.model.QBUser;

import java.util.List;

public class UsersAdapter extends BaseListAdapter<QBUser> {

    protected QBUser currentUser;

    public UsersAdapter(Context context, List<QBUser> users) {
        super(context, users);
        currentUser = QBChatService.getInstance().getUser();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QBUser user = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_user, parent, false);
            holder = new ViewHolder();
            holder.layout_userList = (RelativeLayout) convertView.findViewById(R.id.Linear_userList);
            holder.userImageView = (ImageView) convertView.findViewById(R.id.image_user);
            holder.userStatus = (ImageView) convertView.findViewById(R.id.image_userStatus);
            holder.loginTextView = (TextView) convertView.findViewById(R.id.text_user_login);
            holder.userCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox_user);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (isUserMe(user)) {
            holder.loginTextView.setText(context.getString(R.string.placeholder_username_you, user.getFullName()));


        } else {
            holder.loginTextView.setText(user.getFullName());
        }

        if (isAvailableForSelection(user)) {

            holder.loginTextView.setTextColor(ResourceUtils.getColor(R.color.colorPrimary));
            holder.loginTextView.setTextSize(16);
            holder.loginTextView.setTypeface(null, Typeface.BOLD);
        } else {
            holder.loginTextView.setTextColor(ResourceUtils.getColor(R.color.colorPrimary));
            holder.loginTextView.setTextSize(16);
            holder.loginTextView.setTypeface(null, Typeface.BOLD);
        }

        holder.userImageView.setBackgroundDrawable(UiUtils.getColoredCircleDrawable(ResourceUtils.getColor(R.color.colorPrimary)));
        holder.userImageView.setImageResource(R.drawable.ic_chat_face);
        holder.userCheckBox.setVisibility(View.GONE);

        return convertView;
    }

    protected boolean isUserMe(QBUser user) {
        return currentUser != null && currentUser.getId().equals(user.getId());
    }

    protected boolean isAvailableForSelection(QBUser user) {
        return currentUser == null || !currentUser.getId().equals(user.getId());
    }

    protected static class ViewHolder {
        RelativeLayout layout_userList;
        ImageView userStatus;
        ImageView userImageView;
        TextView loginTextView;
        CheckBox userCheckBox;
    }
}
