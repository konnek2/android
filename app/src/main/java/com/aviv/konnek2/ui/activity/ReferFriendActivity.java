package com.aviv.konnek2.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aviv.konnek2.R;

public class ReferFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_friend);

        referFriend();
    }

    private void referFriend() {

        String info = "";
        info += "Hi I am using Konnek2 app please join with me";
        info += "\n App Link:" + "https://play.google.com/store/apps";
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Konnek2");
        intent.putExtra(Intent.EXTRA_TEXT, info);
        startActivity(Intent.createChooser(intent, ""));
    }
}
