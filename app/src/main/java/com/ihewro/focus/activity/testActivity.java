package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.util.Constants;
import com.prolificinteractive.parallaxpager.ParallaxContainer;
import com.prolificinteractive.parallaxpager.ParallaxContextWrapper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class testActivity extends AppCompatActivity {

    @BindView(R.id.parallax_container)
    ParallaxContainer parallaxContainer;




    public static void activityStart(Activity activity, int indexInList, List<FeedItem> feedItemList, int origin) {
        Intent intent = new Intent(activity, testActivity.class);

        Bundle bundle = new Bundle();

        //使用静态变量传递数据
        GlobalConfig.feedItemList = feedItemList;

        bundle.putInt(Constants.KEY_INT_INDEX, indexInList);
        bundle.putInt(Constants.POST_DETAIL_ORIGIN, origin);

        intent.putExtras(bundle);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);


        // find the parallax container
        ParallaxContainer parallaxContainer = (ParallaxContainer) findViewById(R.id.parallax_container);

// specify whether pager will loop
        parallaxContainer.setLooping(true);

// wrap the inflater and inflate children with custom attributes
        parallaxContainer.setupChildren(getLayoutInflater(),
                R.layout.item_test,
                R.layout.item_test,
                R.layout.item_test,
                R.layout.item_test);

        parallaxContainer.getViewPager();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ParallaxContextWrapper(newBase));
    }


}


