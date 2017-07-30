package com.quickblox.sample.groupchatwebrtc.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quickblox.sample.core.utils.ResourceUtils;
import com.quickblox.sample.core.utils.UiUtils;
import com.quickblox.sample.groupchatwebrtc.R;
import com.quickblox.sample.groupchatwebrtc.model.CallLogModel;
import com.quickblox.sample.groupchatwebrtc.utils.Constant;
import com.quickblox.sample.groupchatwebrtc.utils.DialogUtil;
import com.quickblox.sample.groupchatwebrtc.utils.FolderCreator;

import java.util.ArrayList;

/**
 * Created by Lenovo on 28-06-2017.
 */

public class CallHistoryAdapter extends BaseAdapter {

    private static final String TAG = CallHistoryAdapter.class.getSimpleName();
    private ArrayList<CallLogModel> callLogModelArrayList;
    private Context context;
    private static LayoutInflater inflater = null;


    public CallHistoryAdapter(Context context, ArrayList<CallLogModel> callLogModelArrayList) {
        this.context = context;
        this.callLogModelArrayList = callLogModelArrayList;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return callLogModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.item_call_history, null);
        try {


            Log.d(TAG, " getView ");
            holder.call_UserImage = (ImageView) rowView.findViewById(R.id.image_user);
            holder.call_Priority = (ImageView) rowView.findViewById(R.id.call_priority);
            holder.call_status = (ImageView) rowView.findViewById(R.id.call_status);
            holder.callType = (ImageView) rowView.findViewById(R.id.Image_call_type);
            holder.call_userName = (TextView) rowView.findViewById(R.id.call_userName);
            holder.call_LogDate = (TextView) rowView.findViewById(R.id.call_LogDate);
            holder.call_LogTime = (TextView) rowView.findViewById(R.id.call_LogTime);


            holder.call_userName.setTextColor(ResourceUtils.getColor(R.color.colorPrimary));
            holder.call_UserImage.setBackgroundDrawable(UiUtils.getColoredCircleDrawable(ResourceUtils.getColor(R.color.colorPrimary)));
//            final TypedArray testArrayIcon = getResources().obtainTypedArray(R.array.ten_eleven_icon);


            holder.call_LogDate.setTextColor(ResourceUtils.getColor(R.color.colorPrimary));
            holder.call_LogTime.setTextColor(ResourceUtils.getColor(R.color.colorPrimary));
            holder.call_userName.setTextSize(16);
            holder.call_userName.setTypeface(null, Typeface.BOLD);


            if (callLogModelArrayList.size() > 0 && callLogModelArrayList != null) {

                if (!(callLogModelArrayList.get(position).getUserId() == Constant.GROUP_CALL)) {
                    Log.d("IMAGE_URI", "Call History  IF  " + Constant.GROUP_CALL);

                    Uri imageUri =
                            Uri.fromFile(FolderCreator.getImageFileFromSdCard(String.valueOf(callLogModelArrayList.get(position).getUserId())));
                    Glide.with(context)
                            .load(imageUri)
                            .placeholder(R.drawable.ic_chat_face)
                            .into(holder.call_UserImage);

                } else {

                    Log.d("SELETED_USER", "Constant.GROUP_CALL  ELSE  " + Constant.GROUP_CALL);
                    holder.call_UserImage.setImageResource(R.drawable.ic_chat_face);
                }


                Log.d("CALLLISTADEPTER", "getCallUsername  " + callLogModelArrayList.get(position).getCallOpponentName());
                Log.d("CALLLISTADEPTER", "getCallDate " + callLogModelArrayList.get(position).getCallDate());
                Log.d("CALLLISTADEPTER", "getCallTime  " + callLogModelArrayList.get(position).getCallTime());
                Log.d("CALLLISTADEPTER", "getCallStatus  " + callLogModelArrayList.get(position).getCallStatus());
                Log.d("CALLLISTADEPTER", "getCallPriority   " + callLogModelArrayList.get(position).getCallPriority());
                Log.d("CALLLISTADEPTER", "getCallType   " + callLogModelArrayList.get(position).getCallType());


                holder.call_userName.setText(callLogModelArrayList.get(position).getCallOpponentName());
                holder.call_LogDate.setText(callLogModelArrayList.get(position).getCallDate());
                holder.call_LogTime.setText(callLogModelArrayList.get(position).getCallTime());

                if (callLogModelArrayList.get(position).getCallType().equalsIgnoreCase(Constant.CALL_AUDIO)) {

                    holder.callType.setImageResource(R.drawable.ic_call_audiocall);
                } else {
                    holder.callType.setImageResource(R.drawable.ic_call_videocall);
                }


                if (callLogModelArrayList.get(position).getCallPriority().equalsIgnoreCase(Constant.CALL_PRIORITY_HIGH)) {

                    holder.call_Priority.setImageResource(R.drawable.ic_call_high_priority);
                } else {

                    if (callLogModelArrayList.get(position).getCallPriority().equalsIgnoreCase(Constant.CALL_PRIORITY_MEDIUM)) {
                        holder.call_Priority.setImageResource(R.drawable.ic_call_medium_priority);

                    } else {
                        holder.call_Priority.setImageResource(R.drawable.ic_call_low_priority);
                    }

                }


                if (callLogModelArrayList.get(position).getCallStatus().equalsIgnoreCase(Constant.CALL_STATUS_DIALED)) {

                    holder.call_status.setImageResource(R.drawable.ic_call_uparrow);
                } else {

                    if (callLogModelArrayList.get(position).getCallStatus().equalsIgnoreCase(Constant.CALL_STATUS_RECEIVED)) {

                        Log.d("CALLLISTADEPTER", "getCallType   " + callLogModelArrayList.get(position).getCallStatus());
                        holder.call_status.setImageResource(R.drawable.ic_call_downarrow);

                    } else {
                        Log.d("CALLLISTADEPTER", "getCallType   " + callLogModelArrayList.get(position).getCallStatus());
                        holder.call_status.setImageResource(R.drawable.ic_call_missedcall);
                    }

                }


            }

        } catch (Exception e) {
            e.getMessage();
        }

        return rowView;
    }


    public class Holder {

        TextView call_userName;
        TextView call_LogDate;
        TextView call_LogTime;
        ImageView call_UserImage;
        ImageView call_Priority;
        ImageView callType;
        ImageView call_status;


    }
}



