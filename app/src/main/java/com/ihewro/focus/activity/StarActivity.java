package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ihewro.focus.R;
import com.ihewro.focus.adapter.BaseViewPagerAdapter;
import com.ihewro.focus.adapter.UserFeedPostsVerticalAdapter;
import com.ihewro.focus.bean.CollectionFolder;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.fragemnt.CollectionListFragment;
import com.ihewro.focus.task.CollectionTabLayoutTask;
import com.ihewro.focus.task.listener.CollectionTabLayoutListener;
import com.ihewro.focus.util.UIUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import skin.support.utils.SkinPreference;

public class StarActivity extends BackActivity {

    List<FeedItem> eList = new ArrayList<>();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;


    UserFeedPostsVerticalAdapter adapter;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.loading)
    RelativeLayout loading;

    CollectionListFragment fragment;


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

        createTabLayout();
    }


    private void createTabLayout() {

        loading.setVisibility(View.VISIBLE);
        new CollectionTabLayoutTask(new CollectionTabLayoutListener() {
            @Override
            public void onFinish(List<Fragment> fragmentList, List<String> pageTitleList) {
                BaseViewPagerAdapter adapter = new BaseViewPagerAdapter(getSupportFragmentManager(), fragmentList, pageTitleList);

                //设置ViewPager
                viewPager.setAdapter(adapter);
                viewPager.setOffscreenPageLimit(1);
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


                //适配夜间模式
                if (SkinPreference.getInstance().getSkinName().equals("night")) {
                    tabLayout.setBackgroundColor(ContextCompat.getColor(StarActivity.this, R.color.colorPrimary_night));
                } else {
                    tabLayout.setBackgroundColor(ContextCompat.getColor(StarActivity.this, R.color.colorPrimary));
                }

                //关闭等待加载框
                loading.setVisibility(View.GONE);
            }
        },StarActivity.this).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.collect, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.manage:
                CollectionFolderManageActivity.activityStart(StarActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshUI(EventMessage eventBusMessage) {
        if (Objects.equals(eventBusMessage.getType(), EventMessage.COLLECTION_FOLDER_OPERATION)){//收藏状态修改
            //重新构造tablayout
            createTabLayout();
        }
    }


}
