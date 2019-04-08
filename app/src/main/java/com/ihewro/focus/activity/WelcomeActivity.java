package com.ihewro.focus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.ihewro.focus.R;
import com.ihewro.focus.task.AppStartTask;
import com.ihewro.focus.task.listener.TaskListener;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏


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
