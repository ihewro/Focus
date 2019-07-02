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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ihewro.focus.R;
import com.ihewro.focus.adapter.BaseViewPagerAdapter;
import com.ihewro.focus.adapter.UserFeedPostsVerticalAdapter;
import com.ihewro.focus.bean.CollectionFolder;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.fragemnt.CollectionListFragment;
import com.ihewro.focus.util.UIUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

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

    private List<Fragment> fragmentList = new ArrayList<>();
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
//        EventBus.getDefault().register(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createTabLayout();
    }


    private void createTabLayout() {

        loading.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //碎片列表
                fragmentList.clear();


                //扫描所有的文件夹
                List<CollectionFolder> list = LitePal.findAll(CollectionFolder.class);

                //标题列表
                final List<String> pageTitleList = new ArrayList<>();


                for (CollectionFolder collectionFolder : list) {
                    pageTitleList.add(collectionFolder.getName());
                    fragment = CollectionListFragment.newInstance(collectionFolder.getId(), StarActivity.this);
                    fragmentList.add(fragment);
                }

                UIUtil.runOnUiThread(StarActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        //新建适配器
                        BaseViewPagerAdapter adapter = new BaseViewPagerAdapter(getSupportFragmentManager(), fragmentList, pageTitleList);

                        //设置ViewPager
                        viewPager.setAdapter(adapter);
                        viewPager.setOffscreenPageLimit(3);
                        tabLayout.setupWithViewPager(viewPager);
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);


                        //适配夜间模式
                        if (SkinPreference.getInstance().getSkinName().equals("night")) {
                            tabLayout.setBackgroundColor(ContextCompat.getColor(StarActivity.this, R.color.colorPrimary_night));
                        } else {
                            tabLayout.setBackgroundColor(ContextCompat.getColor(StarActivity.this, R.color.colorPrimary));
                        }

                        //关闭等待加载框
                        loading.setVisibility(View.GONE);
                    }
                });
            }
        }).start();


    }



    /*@Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
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
    }*/


}
