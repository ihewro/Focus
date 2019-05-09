package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.helper.CustomTabActivityHelper;
import com.ihewro.focus.helper.WebviewFallback;
import com.ihewro.focus.view.htmltextview.HtmlTextView;
import com.ihewro.focus.util.ArticleUtil;
import com.ihewro.focus.util.Constants;
import com.ihewro.focus.util.DateUtil;
import com.ihewro.focus.util.ShareUtil;
import com.ihewro.focus.view.PostFooter;
import com.ihewro.focus.view.PostHeader;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

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
    @BindView(R.id.main_body)
    LinearLayout mainBody;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;


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

        initView();

        initListener();

    }

    private void initView() {

        refreshLayout.setRefreshHeader(new PostHeader(this,feedItem));
        refreshLayout.setRefreshFooter(new PostFooter(this,feedItem));
        //使上拉加载具有弹性效果
        refreshLayout.setEnableAutoLoadMore(false);
        //禁止越界拖动（1.0.4以上版本）
        refreshLayout.setEnableOverScrollDrag(false);
        //关闭越界回弹功能
        refreshLayout.setEnableOverScrollBounce(false);
        // 这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的：
        refreshLayout.setEnableAutoLoadMore(false);

    }

    private void initListener() {

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //TODO: 载入文章内容

            }
        });


        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();
                //打开外链
                openLink();
            }
        });
        //文章的touch事件
        final GestureDetector gestureDetector = new GestureDetector(PostDetailActivity.this, new GestureDetector.SimpleOnGestureListener(){

            /**
             * 发生确定的单击时执行
             * @param e
             * @return
             */
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {//单击事件
//                ALog.d("单击");
//                Toast.makeText(PostDetailActivity.this, "这是单击事件", Toast.LENGTH_SHORT).show();
                return super.onSingleTapConfirmed(e);
            }

            /**
             * 双击发生时的通知
             * @param e
             * @return
             */
            @Override
            public boolean onDoubleTap(MotionEvent e) {//双击事件
                ALog.d("双击");
                likeButton.performClick();
//                likeButton.setLiked(!likeButton.isLiked());
                return super.onDoubleTap(e);
            }


            /**
             * 双击手势过程中发生的事件，包括按下、移动和抬起事件
             * @param e
             * @return
             */
            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                ALog.d("其他");
                return super.onDoubleTapEvent(e);
            }
        });


        postContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                v.performClick();
                return gestureDetector.onTouchEvent(event);
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
        if (mIndex == -1) {//TODO:如果是-1表示不需要传递该修改信息
            EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_READ_STATUS_BY_ID, mId));
        } else {
            EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_READ_STATUS_BY_INDEX, mIndex));
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
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_link://访问外链
                openLink();
                break;
            case R.id.action_share://分享
                ShareUtil.shareBySystem(PostDetailActivity.this, "text", feedItem.getTitle() + "\n" + feedItem.getUrl());
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 显示自定义的收藏的图标
     */
    private void showStarActionView(MenuItem item) {
        starItem = item;
        //这里使用一个ImageView设置成MenuItem的ActionView，这样我们就可以使用这个ImageView显示旋转动画了
        likeButton = (LikeButton) getLayoutInflater().inflate(R.layout.action_bar_star, null);
        item.setActionView(likeButton);

        setLikeButton();

    }

    private void setLikeButton() {
        //设置收藏状态
        if (feedItem == null) {
            feedItem = LitePal.where("iid = ?", mId).limit(1).find(FeedItem.class).get(0);
        }
        likeButton.setLiked(feedItem.isFavorite());
        //收藏的点击事件
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                feedItem.setFavorite(true);
                feedItem.save();
                if (mIndex == -1) {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_ID, Integer.parseInt(mId), true));
                } else {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_INDEX, mIndex, true));
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                feedItem.setFavorite(false);
                feedItem.save();
                if (mIndex == -1) {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_ID, Integer.parseInt(mId), false));
                } else {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_INDEX, mIndex, false));
                }
            }
        });
    }

    private void openLink(){
        String url = feedItem.getUrl();
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        CustomTabActivityHelper.openCustomTab(
                PostDetailActivity.this, customTabsIntent, Uri.parse(url), new WebviewFallback());
    }
}
