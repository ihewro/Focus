package com.ihewro.focus.activity;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.eftimoff.androipathview.PathView;
import com.ihewro.focus.R;
import com.ihewro.focus.task.AppStartTask;
import com.ihewro.focus.task.listener.TaskListener;

import skin.support.utils.SkinPreference;

public class WelcomeActivity extends AppCompatActivity {

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
                finish();
                MainActivity.activityStart(WelcomeActivity.this);
            }
        }).execute();


    }
}
