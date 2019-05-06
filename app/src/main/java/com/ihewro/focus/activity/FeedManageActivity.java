package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ihewro.focus.R;

/**
 * 分类管理页面，对分类、分类文件夹增加、删除、修改、排序
 */
public class FeedManageActivity extends AppCompatActivity {


    public static void activityStart(Activity activity) {
        Intent intent = new Intent(activity, FeedManageActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_manage);
    }
}
