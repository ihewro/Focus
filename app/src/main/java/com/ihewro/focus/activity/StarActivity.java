package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ihewro.focus.R;

public class StarActivity extends AppCompatActivity {


    public static void activityStart(Activity activity) {
        Intent intent = new Intent(activity, StarActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);
    }
}
