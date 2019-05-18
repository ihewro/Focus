package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.ihewro.focus.R;
import com.ihewro.focus.adapter.UserFeedPostsVerticalAdapter;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.decoration.DividerItemDecoration;
import com.ihewro.focus.decoration.SuspensionDecoration;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StarActivity extends BackActivity {

    List<FeedItem> eList = new ArrayList<FeedItem>();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    UserFeedPostsVerticalAdapter adapter;

    public static void activityStart(Activity activity) {
        Intent intent = new Intent(activity, StarActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
    }


    public void initData() {
        eList = LitePal.where("favorite = ?", "1").find(FeedItem.class);
        adapter = new UserFeedPostsVerticalAdapter(eList,StarActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.bindToRecyclerView(recyclerView);

        if (eList.size()==0){
            adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);
        }

//        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(this), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.addItemDecoration(new SuspensionDecoration(this, eList));
    }
}
