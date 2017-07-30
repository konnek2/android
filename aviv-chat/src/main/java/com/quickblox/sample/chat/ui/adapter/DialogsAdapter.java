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
import com.quickblox.users.model.QBUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DialogsAdapter extends BaseSelectableListAdapter<QBChatDialog> {

    QBChatDialog dialog;
    QBUser user;

    public DialogsAdapter(Context context, List<QBChatDialog> dialogs) {
        super(context, dialogs);

        Log.d("QB_USER_SIZE", "DialogsAdapter  dialogs.size()  " + dialogs.size());


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
            Log.d("ONE_TO_ONE", ":::" + dialog.getRecipientId() + " NAME :::" + dialog.getName());
            Log.d("IMAGE_URI 1 ", "DIALOGADAPTER  CHAT IMAGE_URI   ");

            Uri imageUri = Uri.fromFile(FolderCreator.getImageFileFromSdCard(String.valueOf(dialog.getRecipientId())));
            Log.d("IMAGE_URI 2 ", "ADPATER onSuccess ====>  CHAT IMAGE_URI   " + imageUri);
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
            Log.d("ChatFragment", "DialogsAdapter 1 getDialogName " + QbDialogUtils.getDialogName(dialog));

            holder.nameTextView.setText(QbDialogUtils.getDialogName(dialog));
            holder.nameTextView.setTextColor(ResourceUtils.getColor(R.color.colorPrimary));
            holder.lastMessageTextView.setText(prepareTextLastMessage(dialog));
            holder.lastMessageTextView.setTextColor(ResourceUtils.getColor(R.color.colorPrimary));

            int unreadMessagesCount = dialog.getUnreadMessageCount();
            if (unreadMessagesCount == 0) {
                Log.d("ChatFragment", "unreadMessagesCount == 0 IF ");
                holder.unreadCounterTextView.setVisibility(View.GONE);
            } else {
                Log.d("ChatFragment", "unreadMessagesCount == 0  ELSE ");
                holder.unreadCounterTextView.setVisibility(View.VISIBLE);
                holder.unreadCounterTextView.setText(String.valueOf(unreadMessagesCount > 99 ? 99 : unreadMessagesCount));
            }

            holder.rootLayout.setBackgroundColor(isItemSelected(position) ? ResourceUtils.getColor(R.color.selected_list_item_color) :
                    ResourceUtils.getColor(android.R.color.transparent));

        } catch (Exception e) {
            e.getMessage();
        }
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

//    public void getQbUsers(QBChatDialog dialog) {
//
//        Log.d("ChatFragment", " DialogsAdapter getQbUsers ");
//        if (dialog.getOccupants().size() < 3) {
//            Log.d("ChatFragment", " DialogsAdapter getQbUsers  dialog.getOccupants().size() < 3 ");
//            dialog.getUserId();
//            List<Integer> users = dialog.getOccupants();
//            for (Integer id : users) {
//                user = QbUsersHolder.getInstance().getUserById(id);
//                Log.d("ChatFragment", " DialogsAdapter getQbUsers  FOR  USER 123" + user);
//            }
//        } else {
//            Log.d("MULTIUPDATE", "   DialogsAdapter   Group  no read or green ");
////            holder.userStatus.setImageResource(0);
//        }
//    }

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
