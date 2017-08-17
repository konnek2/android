package com.quickblox.sample.groupchatwebrtc.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.groupchatwebrtc.App;
import com.quickblox.sample.groupchatwebrtc.R;
import com.quickblox.sample.groupchatwebrtc.adapters.CallHistoryAdapter;
import com.quickblox.sample.groupchatwebrtc.model.CallLogModel;
import com.quickblox.sample.groupchatwebrtc.utils.Common;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;


public class CallHistoryFragment extends Fragment {

    private static final String TAG = CallHistoryFragment.class.getSimpleName();

    private CallHistoryAdapter callHistoryAdapter;
    private ListView callListivew;
    private ArrayList<CallLogModel> callLogModels;
    SharedPrefsHelper sharedPrefsHelper;
    private QBUser currentUser;
    private String currentUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_call_history, container, false);

        initViews(view);
        return view;
    }

    public void initViews(View view) {

        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        try {
            currentUser = sharedPrefsHelper.getQbUser();
            currentUserName = currentUser.getFullName();

            callListivew = (ListView) view.findViewById(R.id.call_logList);
            callLogModels = new ArrayList<CallLogModel>();
            if (currentUserName != null && !currentUserName.isEmpty()) {
                callLogModels = App.callLogTableDAO.getCallHistory(currentUserName);
            }
            callHistoryAdapter = new CallHistoryAdapter(getActivity(), callLogModels);
            if (callHistoryAdapter.getCount() > 0) {
                callListivew.setAdapter(callHistoryAdapter);
            } else {
//            callListivew.setAdapter(null);
            }

        } catch (Exception e) {
            e.getMessage();
        }

    }

    public static void callHistoryTrigger() {
        CallHistoryFragment callHistoryFragment = new CallHistoryFragment();

    }

}
