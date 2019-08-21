package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.AuthListAdapter;
import com.ihewro.focus.bean.Auth;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthListActivity extends BackActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private AuthListAdapter adapter;

    public static void activityStart(Activity activity) {
        Intent intent = new Intent(activity, AuthListActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initRecyclerView();

    }



    private void initRecyclerView(){
        List<Auth> list = new ArrayList<>();

        list.add(new Auth("蚁阅","国内开发者搭建的第三方云服务"));
        list.add(new Auth("inoreader","国外知名第三方云阅读器"));
        list.add(new Auth("feedly","国外知名第三方云阅读器"));

        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AuthListAdapter(list,AuthListActivity.this);
        adapter.bindToRecyclerView(recyclerView);


    }


}
