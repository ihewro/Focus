package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ihewro.focus.R;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.helper.CustomTabActivityHelper;
import com.ihewro.focus.helper.WebviewFallback;
import com.ihewro.focus.htmltextview.HtmlTextView;
import com.ihewro.focus.util.ArticleUtil;
import com.ihewro.focus.util.Constants;
import com.ihewro.focus.util.DateUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PostDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.post_title)
    TextView postTitle;
    @BindView(R.id.post_time)
    TextView postTime;
    @BindView(R.id.post_content)
    HtmlTextView postContent;
    @BindView(R.id.markRead)
    RelativeLayout markRead;
    @BindView(R.id.click_link)
    RelativeLayout clickLink;
    @BindView(R.id.star)
    RelativeLayout star;
    @BindView(R.id.share)
    RelativeLayout share;
    private String mId;
    private int mIndex;
    private FeedItem feedItem;

    public static void activityStart(Activity activity, String feedItemId, int indexInList) {
        Intent intent = new Intent(activity, PostDetailActivity.class);
        intent.putExtra(Constants.KEY_STRING_FEED_ITEM_ID, feedItemId);
        intent.putExtra(Constants.KEY_INT_INDEX, indexInList);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        mId = intent.getStringExtra(Constants.KEY_STRING_FEED_ITEM_ID);
        mIndex = intent.getIntExtra(Constants.KEY_INT_INDEX, 0);
        initData();
        initListener();
    }

    private void initListener() {


        clickLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = feedItem.getUrl();
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
                CustomTabActivityHelper.openCustomTab(
                        PostDetailActivity.this, customTabsIntent, Uri.parse(url), new WebviewFallback());
            }
        });
    }


    public void initData() {
        feedItem = LitePal.where("iid = ?", mId).limit(1).find(FeedItem.class).get(0);
        ArticleUtil.setContent(this, feedItem, postContent);
        postTitle.setText(feedItem.getTitle());
        postTime.setText(DateUtil.getTimeStringByInt(feedItem.getDate()));
        //将该文章标记为已读，并且通知首页修改布局
        feedItem.setRead(true);
        feedItem.save();
        if (mIndex != -1) {//如果是-1表示不需要传递该修改信息
            EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_READ_STATUS, mIndex));
        }
    }

    private void initView(){
        //设置已读状态

        //设置收藏状态

    }

}
