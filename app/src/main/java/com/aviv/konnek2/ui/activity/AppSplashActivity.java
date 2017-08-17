package com.aviv.konnek2.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.aviv.konnek2.R;
import com.aviv.konnek2.utils.Constant;

public class AppSplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = Constant.SPLASH_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_app_splash);
        displaySplash();
    }

    public void displaySplash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(AppSplashActivity.this, SignInActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
