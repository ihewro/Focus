package com.ihewro.focus.activity;

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
                //判断是否是第一次打开引导页面
                /*if (true){
                    NewComerActivity.activityStart(SplashActivity.this);
                }else {
                    MainActivity.activityStart(SplashActivity.this);
                }*/

                finish();
                MainActivity.activityStart(SplashActivity.this);

            }
        }).execute();


    }



}
