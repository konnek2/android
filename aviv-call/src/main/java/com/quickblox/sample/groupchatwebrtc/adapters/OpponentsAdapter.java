package com.quickblox.sample.groupchatwebrtc.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quickblox.sample.core.ui.adapter.BaseSelectableListAdapter;
import com.quickblox.sample.core.utils.ResourceUtils;
import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.sample.core.utils.UiUtils;
import com.quickblox.sample.groupchatwebrtc.R;
import com.quickblox.sample.groupchatwebrtc.fragments.CallLogInterface;
import com.quickblox.sample.groupchatwebrtc.utils.Common;
import com.quickblox.sample.groupchatwebrtc.utils.Constant;
import com.quickblox.sample.groupchatwebrtc.utils.Consts;
import com.quickblox.sample.groupchatwebrtc.utils.DialogUtil;
import com.quickblox.sample.groupchatwebrtc.utils.FolderCreator;
import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * QuickBlox team
 */
public class OpponentsAdapter extends BaseSelectableListAdapter<QBUser> {

    private SelectedItemsCountsChangedListener selectedItemsCountChangedListener;
    private CallLogInterface callLogInterface;
    private String name, time, date, callStatus, userId;

    public OpponentsAdapter(Context context, List<QBUser> users, CallLogInterface callLogInterface) {
        super(context, users);
        this.callLogInterface = callLogInterface;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_opponents_list, null);
            holder = new ViewHolder();
            holder.opponentIcon = (ImageView) convertView.findViewById(R.id.image_opponent_icon);

            holder.opponentName = (TextView) convertView.findViewById(R.id.opponentsName);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.boxlist);
            holder.oppUserLayout = (TableLayout) convertView.findViewById(R.id.oppUserLayout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final QBUser user = getItem(position);

        if (user != null) {
            Log.d("CONTAST_FRAGMENT", " Opponents adapterr Suser != null  " + user);
            Uri imageUri = Uri.fromFile(FolderCreator.getImageFileFromSdCard(String.valueOf(user.getId())));
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_chat_face)
                    .into(holder.opponentIcon);

            holder.opponentName.setText(user.getFullName());
            holder.opponentName.setTag(user.getFullName());
            holder.opponentName.setTextColor(ResourceUtils.getColor(R.color.colorPrimary));
            holder.opponentName.setTextSize(16);
            holder.opponentName.setTypeface(null, Typeface.BOLD);
            holder.opponentIcon.setBackgroundDrawable(UiUtils.getColoredCircleDrawable(ResourceUtils.getColor(R.color.colorPrimary)));
//

        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {

                    if (selectedItems.size() < Consts.MAX_OPPONENTS_COUNT) {

                        Log.d("CONTAST_FRAGMENT", " Opponents adapterr SelectedItems user  " + user);
                        selectedItems.add(user);

                        name = holder.opponentName.getTag().toString();
                        date = Common.currentDate();
                        time = Common.currentTime();
                        callStatus = Constant.CALL_STATUS_DIALED;
                        userId=String.valueOf(user.getId());

                        if (name != null && date != null
                                && time != null && callStatus != null) {
                            callLogInterface.selectedUser(name, date, time, callStatus,userId);
                        } else {
                            Toaster.shortToast("Call status Missing");
                        }
                        selectedItemsCountChangedListener.onCountSelectedItemsChanged(selectedItems.size());
                    } else {
                        holder.checkBox.setChecked(false);
                        Toaster.shortToast("  4 Users only allowed in Conference call ");

                    }
                } else {
                    selectedItems.remove(user);

                    if (selectedItems.size() < Consts.MIN_OPPONENTS_COUNT) {
                        selectedItemsCountChangedListener.onCountSelectedItemsChanged(0);
                    }
                }
            }

        });

        return convertView;
    }

    public static class ViewHolder {
        TableLayout oppUserLayout;
        ImageView opponentIcon;
        TextView opponentName;
        CheckBox checkBox;
    }

    public void setSelectedItemsCountsChangedListener(SelectedItemsCountsChangedListener selectedItemsCountsChanged) {
        if (selectedItemsCountsChanged != null) {
            this.selectedItemsCountChangedListener = selectedItemsCountsChanged;
        }
    }

    public interface SelectedItemsCountsChangedListener {
        void onCountSelectedItemsChanged(int count);
    }
}
