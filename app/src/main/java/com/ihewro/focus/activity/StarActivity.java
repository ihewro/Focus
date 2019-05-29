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
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.decoration.SuspensionDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StarActivity extends BackActivity {

    List<FeedItem> eList = new ArrayList<>();
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
        EventBus.getDefault().register(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
    }


    public void initData() {

        eList = LitePal.where("favorite = ?","1").find(FeedItem.class);
        adapter = new UserFeedPostsVerticalAdapter(eList,StarActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.bindToRecyclerView(recyclerView);

        if (eList.size()==0){
            adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);
        }
        recyclerView.addItemDecoration(new SuspensionDecoration(this, eList));
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshUI(EventMessage eventBusMessage) {
        if (Objects.equals(eventBusMessage.getType(), EventMessage.MAKE_STAR_STATUS_BY_ID)){//收藏状态修改
            //寻找到id的位置
            int pos = -1;
            for (int i = 0;i<eList.size();i++){
                if (eList.get(i).getId() == eventBusMessage.getInteger()){
                    pos = i;
                    break;
                }
            }
            boolean flag = eventBusMessage.isFlag();
            eList.get(pos).setFavorite(flag);
            adapter.notifyItemChanged(pos);
        }
    }


}
