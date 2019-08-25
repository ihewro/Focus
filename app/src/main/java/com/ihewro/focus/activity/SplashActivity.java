package com.ihewro.focus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.task.AppStartTask;
import com.ihewro.focus.task.listener.TaskListener;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeHelper;

import skin.support.utils.SkinPreference;

public class SplashActivity extends AppCompatActivity {

    private WelcomeHelper sampleWelcomeScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(SkinPreference.getInstance().getSkinName().equals("night")){
            super.setTheme(R.style.AppTheme_Dark);
        }else {
            super.setTheme(R.style.AppTheme);
        }


        setContentView(R.layout.activity_welcome);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏


        if (false){

            // The welcome screen for this app (only one that automatically shows)
            sampleWelcomeScreen = new WelcomeHelper(this, NewComerActivity.class);
            sampleWelcomeScreen.forceShow();
        }

        Intent intent = getIntent();

        if (!isTaskRoot()) {
            finish();
            return;
        }

        ImageView imageview= findViewById(R.id.imageView);
        AnimatedVectorDrawable animatedVectorDrawable =  ((AnimatedVectorDrawable) imageview.getDrawable());
        animatedVectorDrawable.start();

        //跳转到 {@link MainActivity}
        new AppStartTask(new TaskListener() {
            @Override
            public void onFinish(String jsonString) {

                finish();
                MainActivity.activityStart(SplashActivity.this);

            }
        }).execute();


    }

/*
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // This is needed to prevent welcome screens from being
        // automatically shown multiple times

        // This is the only one needed because it is the only one that
        // is shown automatically. The others are only force shown.
        sampleWelcomeScreen.onSaveInstanceState(outState);
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        ALog.d("什么鬼" + resultCode + "|" + requestCode);
        if (requestCode == WelcomeHelper.DEFAULT_WELCOME_SCREEN_REQUEST) {
            // The key of the welcome screen is in the Intent
            String welcomeKey = data.getStringExtra(WelcomeActivity.WELCOME_SCREEN_KEY);
            if (resultCode == RESULT_OK) {
                // Code here will run if the welcome screen was completed
            } else {
                // Code here will run if the welcome screen was canceled
                // In most cases you'll want to call finish() here
            }

            finish();
            MainActivity.activityStart(SplashActivity.this);

        }

    }


}
