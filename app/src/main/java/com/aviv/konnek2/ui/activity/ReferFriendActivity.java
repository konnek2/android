package com.aviv.konnek2.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aviv.konnek2.R;
import com.aviv.konnek2.utils.Constant;

public class ReferFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_friend);

        referFriend();
    }

    private void referFriend() {

        String info = "";
        info += Constant.REFER_FRIEND_MESSGAE;
        info += Constant.PLAY_STORE_LINK;
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType(Constant.REFER_FRIEND_ACTION_TYPE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, Constant.APP_NAME);
        intent.putExtra(Intent.EXTRA_TEXT, info);
        startActivity(Intent.createChooser(intent, ""));
    }
}
