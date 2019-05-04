package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ihewro.focus.R;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.htmltextview.HtmlTextView;
import com.ihewro.focus.util.ArticleUtil;
import com.ihewro.focus.util.Constants;
import com.ihewro.focus.util.DateUtil;

import org.litepal.LitePal;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PostDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.post_title)
    TextView postTitle;
    @BindView(R.id.post_time)
    TextView postTime;
    @BindView(R.id.post_content)
    HtmlTextView postContent;
    private String mId;


    public static void activityStart(Activity activity, String feedItemId) {
        Intent intent = new Intent(activity, PostDetailActivity.class);
        intent.putExtra(Constants.KEY_STRING_FEED_ITEM_ID, feedItemId);
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

        initData();
    }


    public void initData() {
        FeedItem feedItem = LitePal.where("iid = ?", mId).limit(1).find(FeedItem.class).get(0);
        ArticleUtil.setContent(this, feedItem, postContent);
        postTitle.setText(feedItem.getTitle());
        postTime.setText(DateUtil.getTimeStringByInt(feedItem.getDate()));
        //将该文章标记为已读，并且通知首页修改布局
        feedItem.setRead(true);
    }
}
