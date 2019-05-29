package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.ihewro.focus.R;
import com.ihewro.focus.adapter.BaseViewPagerAdapter;
import com.ihewro.focus.fragemnt.setting.DataFragment;
import com.ihewro.focus.fragemnt.setting.DisplayFragment;
import com.ihewro.focus.fragemnt.setting.GestureFragment;
import com.ihewro.focus.fragemnt.setting.OtherFragment;
import com.ihewro.focus.fragemnt.setting.SynchroFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import skin.support.utils.SkinPreference;

public class SettingActivity extends BackActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private List<Fragment> fragmentList = new ArrayList<>();

    private SynchroFragment synchroFragment;
    private GestureFragment gestureFragment;
    private DataFragment dataFragment;
    private DisplayFragment displayFragment;
    private OtherFragment otherFragment;


    public static void activityStart(Activity activity) {
        Intent intent = new Intent(activity, SettingActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        createTabLayout();

    }

    private void createTabLayout() {
        //碎片列表
        fragmentList.clear();
        synchroFragment = new SynchroFragment();
        gestureFragment = new GestureFragment();
        displayFragment = new DisplayFragment();
        dataFragment = new DataFragment();
        otherFragment = new OtherFragment();

        fragmentList.add(synchroFragment);
        fragmentList.add(gestureFragment);
        fragmentList.add(displayFragment);
        fragmentList.add(dataFragment);
        fragmentList.add(otherFragment);

        //标题列表
        List<String> pageTitleList = new ArrayList<>();
        pageTitleList.add("同步");
        pageTitleList.add("手势");
        pageTitleList.add("显示");
        pageTitleList.add("数据");
        pageTitleList.add("其他");

        //新建适配器
        BaseViewPagerAdapter adapter = new BaseViewPagerAdapter(getSupportFragmentManager(), fragmentList, pageTitleList);


        //适配夜间模式
        if (SkinPreference.getInstance().getSkinName().equals("night")) {
            tabLayout.setBackgroundColor(ContextCompat.getColor(SettingActivity.this,R.color.colorPrimary_night));
        } else {
            tabLayout.setBackgroundColor(ContextCompat.getColor(SettingActivity.this,R.color.colorPrimary));
        }

        //设置ViewPager
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

    }


}
