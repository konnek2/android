package com.quickblox.sample.chat.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.sample.chat.R;
import com.quickblox.sample.chat.interfaces.ImageIdDownloadView;
import com.quickblox.sample.chat.network.AppImageIdPresenter;
import com.quickblox.sample.chat.utils.Constant;
import com.quickblox.sample.chat.utils.FolderCreator;
import com.quickblox.sample.chat.utils.qb.QbDialogUtils;
import com.quickblox.sample.chat.utils.qb.QbUsersHolder;
import com.quickblox.sample.core.ui.adapter.BaseSelectableListAdapter;
import com.quickblox.sample.core.utils.ResourceUtils;
import com.quickblox.sample.core.utils.UiUtils;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DialogsAdapter extends BaseSelectableListAdapter<QBChatDialog> {

    QBChatDialog dialog;
    List<QBUser> user;
    long currentTime;
    long userLastRequestAtTime;

    public DialogsAdapter(Context context, List<QBChatDialog> dialogs) {
        super(context, dialogs);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        try {
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.list_item_dialog, parent, false);
                holder = new ViewHolder();
                holder.rootLayout = (ViewGroup) convertView.findViewById(R.id.root);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.text_dialog_name);
                holder.lastMessageTextView = (TextView) convertView.findViewById(R.id.text_dialog_last_message);
                holder.dialogImageView = (ImageView) convertView.findViewById(R.id.image_dialog_icon);
                holder.userStatus = (ImageView) convertView.findViewById(R.id.image_dialog_userStatus);
                holder.unreadCounterTextView = (TextView) convertView.findViewById(R.id.text_dialog_unread_count);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            dialog = getItem(position);


            Uri imageUri = Uri.fromFile(FolderCreator.getImageFileFromSdCard(String.valueOf(dialog.getRecipientId())));
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_chat_face)
                    .into(holder.dialogImageView);


            if (dialog.getType().equals(QBDialogType.GROUP)) {
                holder.dialogImageView.setBackgroundDrawable(UiUtils.getColoredCircleDrawable(ResourceUtils.getColor(R.color.colorPrimary)));
//                holder.dialogImageView.setImageResource(R.drawable.ic_chat_groups);
            } else {
                holder.dialogImageView.setBackgroundDrawable(UiUtils.getColoredCircleDrawable(ResourceUtils.getColor(R.color.colorPrimary)));
//                holder.dialogImageView.setImageResource(R.drawable.ic_chat_face);
//                holder.dialogImageView.setImageResource(R.drawable.n1);
            }
//            getQbUsers(dialog);

            holder.nameTextView.setText(QbDialogUtils.getDialogName(dialog));
            holder.nameTextView.setTextColor(ResourceUtils.getColor(R.color.colorPrimary));
            holder.lastMessageTextView.setText(prepareTextLastMessage(dialog));
            holder.lastMessageTextView.setTextColor(ResourceUtils.getColor(R.color.colorPrimary));

            int unreadMessagesCount = dialog.getUnreadMessageCount();
            if (unreadMessagesCount == 0) {

                holder.unreadCounterTextView.setVisibility(View.GONE);
            } else {

                holder.unreadCounterTextView.setVisibility(View.VISIBLE);
                holder.unreadCounterTextView.setText(String.valueOf(unreadMessagesCount > 99 ? 99 : unreadMessagesCount));
            }

            holder.rootLayout.setBackgroundColor(isItemSelected(position) ? ResourceUtils.getColor(R.color.selected_list_item_color) :
                    ResourceUtils.getColor(android.R.color.transparent));

        } catch (Exception e) {
            e.getMessage();
        }
        getOpponents(dialog);
        return convertView;
    }

    private int getUnreadMsgCount(QBChatDialog chatDialog) {

        Integer unreadMessageCount = chatDialog.getUnreadMessageCount();
        if (unreadMessageCount == null) {
            return 0;
        } else {
            return unreadMessageCount;
        }
    }

    private boolean isLastMessageAttachment(QBChatDialog dialog) {

        String lastMessage = dialog.getLastMessage();
        Integer lastMessageSenderId = dialog.getLastMessageUserId();
        return TextUtils.isEmpty(lastMessage) && lastMessageSenderId != null;
    }

    public void getOpponents(QBChatDialog dialog) {


        if (dialog.getOccupants().size() < 3) {
            List<Integer> users = dialog.getOccupants();
            user = QbUsersHolder.getInstance().getUsersByIds(users);

        } else {

        }
    }

    private String prepareTextLastMessage(QBChatDialog chatDialog) {
        if (isLastMessageAttachment(chatDialog)) {
            return context.getString(R.string.chat_attachment);
        } else {
            return chatDialog.getLastMessage();
        }
    }



    private static class ViewHolder {
        ViewGroup rootLayout;
        ImageView dialogImageView;
        TextView nameTextView;
        TextView lastMessageTextView;
        TextView unreadCounterTextView;
        ImageView userStatus;
    }


}
