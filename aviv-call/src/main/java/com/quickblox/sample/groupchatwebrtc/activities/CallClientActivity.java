package com.quickblox.sample.groupchatwebrtc.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.sample.groupchatwebrtc.R;
import com.quickblox.sample.groupchatwebrtc.fragments.ContactFragment;
import com.quickblox.sample.groupchatwebrtc.utils.Consts;

public class CallClientActivity extends AppCompatActivity {

    private static final String TAG = CallClientActivity.class.getSimpleName();
    ContactFragment fragment;
    FragmentManager fm;
    FragmentTransaction fragmentTransaction;
    boolean activityFlag;


    public static void start(Context context, boolean isRunForCall) {


        Intent intent = new Intent(context, CallClientActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, isRunForCall);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_client);
        activityFlag = false;
        initFragment();
    }

    public void initFragment() {
        try {

            fragment = new ContactFragment();
            fm = getSupportFragmentManager();
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.navigation_container, fragment);
            fragmentTransaction.commit();

        } catch (Exception ex) {
            ex.getMessage();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(activityFlag)
            finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityFlag = true;
    }
}
