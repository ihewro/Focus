package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.ihewro.focus.util.ShareUtil;
import com.like.LikeButton;
import com.like.OnLikeListener;

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



    private String mId;
    private int mIndex;
    private FeedItem feedItem;
    private MenuItem starItem;
    private LikeButton likeButton;

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

    }


    public void initData() {
        feedItem = LitePal.where("iid = ?", mId).limit(1).find(FeedItem.class).get(0);
        ArticleUtil.setContent(this, feedItem, postContent);
        postTitle.setText(feedItem.getTitle());
        postTime.setText(DateUtil.getTimeStringByInt(feedItem.getDate()));
        //将该文章标记为已读，并且通知首页修改布局
        feedItem.setRead(true);
        feedItem.save();
        if (mIndex != -1) {//TODO:如果是-1表示不需要传递该修改信息
            EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_READ_STATUS_BY_ID, mId));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);

        starItem = menu.findItem(R.id.action_star);
        showStarActionView(starItem);
        return true;
    }


    /**
     * 目录按钮的点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_link://访问外链
                String url = feedItem.getUrl();
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
                CustomTabActivityHelper.openCustomTab(
                        PostDetailActivity.this, customTabsIntent, Uri.parse(url), new WebviewFallback());
                break;
            case R.id.action_share://分享
                ShareUtil.shareBySystem(PostDetailActivity.this,"text",feedItem.getTitle()+"\n"+feedItem.getUrl());
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 显示自定义的收藏的图标
     */
    private void showStarActionView(MenuItem item){
        starItem = item;
        //这里使用一个ImageView设置成MenuItem的ActionView，这样我们就可以使用这个ImageView显示旋转动画了
        likeButton = (LikeButton) getLayoutInflater().inflate(R.layout.action_bar_star, null);
        item.setActionView(likeButton);

        setLikeButton();

    }

    private void setLikeButton(){
        //设置收藏状态
        if (feedItem == null){
            feedItem = LitePal.where("iid = ?", mId).limit(1).find(FeedItem.class).get(0);
        }
        likeButton.setLiked(feedItem.isFavorite());
        //收藏的点击事件
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                feedItem.setFavorite(true);
                feedItem.save();
                if (mIndex == -1){
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_ID, Integer.parseInt(mId),true));
                }else {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_INDEX,mIndex,true));
                }
            }
            @Override
            public void unLiked(LikeButton likeButton) {
                feedItem.setFavorite(false);
                feedItem.save();
                if (mIndex == -1){
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_ID, Integer.parseInt(mId),false));
                }else {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_INDEX,mIndex,false));
                }
            }
        });

    }
}
