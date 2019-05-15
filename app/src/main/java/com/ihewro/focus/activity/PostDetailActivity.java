package com.ihewro.focus.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.PostDetailListAdapter;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.PostSetting;
import com.ihewro.focus.helper.CustomTabActivityHelper;
import com.ihewro.focus.helper.RecyclerViewPageChangeListenerHelper;
import com.ihewro.focus.helper.WebviewFallback;
import com.ihewro.focus.util.Constants;
import com.ihewro.focus.util.ShareUtil;
import com.ihewro.focus.view.PostFooter;
import com.ihewro.focus.view.PostHeader;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import skin.support.SkinCompatManager;
import skin.support.utils.SkinPreference;


public class PostDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private PostDetailListAdapter adapter;


    private MaterialDialog ReadSettingDialog;
    private PostSetting postSetting;


    private int mId;
    private int mIndex;
    private FeedItem currentFeedItem;
    private MenuItem starItem;
    private LikeButton likeButton;
    private boolean isUpdateMainReadMark;

    private List<Integer> feedItemIdList;

        public static void activityStart(Activity activity, int feedItemId, int indexInList, ArrayList<Integer> feedItemIdList,boolean flag) {
        Intent intent = new Intent(activity, PostDetailActivity.class);
        intent.putExtra(Constants.KEY_STRING_FEED_ITEM_ID, feedItemId);
        intent.putExtra(Constants.KEY_INT_INDEX, indexInList);
        intent.putExtra(Constants.IS_UPDATE_MAIN_READ_MARK,flag);
        intent.putIntegerArrayListExtra(Constants.KEY_FEED_ITEM_ID_LIST,feedItemIdList);
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
        mIndex = intent.getIntExtra(Constants.KEY_INT_INDEX, 0);
        feedItemIdList = intent.getIntegerArrayListExtra(Constants.KEY_FEED_ITEM_ID_LIST);
        mId = feedItemIdList.get(mIndex);
        isUpdateMainReadMark = intent.getBooleanExtra(Constants.IS_UPDATE_MAIN_READ_MARK,false);
        initData();

        initRecyclerView();

        initView();

    }

    private void initView() {


    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {


        refreshLayout.setRefreshHeader(new PostHeader(this, currentFeedItem));
        refreshLayout.setRefreshFooter(new PostFooter(this, currentFeedItem));
        //使上拉加载具有弹性效果
        refreshLayout.setEnableAutoLoadMore(false);
        //禁止越界拖动（1.0.4以上版本）
        refreshLayout.setEnableOverScrollDrag(false);
        //关闭越界回弹功能
        refreshLayout.setEnableOverScrollBounce(false);
        // 这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的：
        refreshLayout.setEnableAutoLoadMore(false);

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();
                //打开外链
                openLink(currentFeedItem);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();
                //打开外链
                openLink(currentFeedItem);
            }
        });
        //文章的touch事件

        final GestureDetector gestureDetector = new GestureDetector(PostDetailActivity.this, new GestureDetector.SimpleOnGestureListener() {

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

       /* adapter.getViewByPosition(mIndex,R.id.post_content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });*/


    }


    public void initData() {
        currentFeedItem = LitePal.where("id = ?", String.valueOf(mId)).limit(1).find(FeedItem.class).get(0);

    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        long[] ids = new long[feedItemIdList.size()];
        for (int i = 0; i<feedItemIdList.size();i++){
            ids[i] = feedItemIdList.get(i);
        }
        //获取所有文章的对象
        List<FeedItem> feedItemList = LitePal.findAll(FeedItem.class,ids);
        adapter = new PostDetailListAdapter(this,isUpdateMainReadMark,postSetting, feedItemList);
        adapter.bindToRecyclerView(recyclerView);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);


        recyclerView.addOnScrollListener(new RecyclerViewPageChangeListenerHelper(snapHelper, new RecyclerViewPageChangeListenerHelper.OnPageChangeListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                ALog.d("onScrollStateChanged");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ALog.d("onScrolled");

            }

            @Override
            public void onPageSelected(int position) {
                ALog.d("onPageSelected");
            }
        }));

        initListener();
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
                openLink(currentFeedItem);
                break;
            case R.id.action_share://分享
                ShareUtil.shareBySystem(PostDetailActivity.this, "text", currentFeedItem.getTitle() + "\n" + currentFeedItem.getUrl());
                break;

            case R.id.text_setting:
                ReadSettingDialog = new MaterialDialog.Builder(this)
                        .customView(R.layout.read_setting, true)
                        .neutralText("重置")
                        .positiveText("确定")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //重置设置

                            }
                        })

                        .show();

                initReadSettingView();
                initReadSettingListener();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    //根据现有的设置，恢复布局
    private void initReadSettingView(){
        if (ReadSettingDialog.isShowing()){
            //获取夜间模式
            if(SkinPreference.getInstance().getSkinName().equals("night")){
                ((Switch)ReadSettingDialog.findViewById(R.id.mode_switch)).setChecked(true);
            }else {
                ((Switch)ReadSettingDialog.findViewById(R.id.mode_switch)).setChecked(false);

            }
        }
    }
    private void initReadSettingListener() {
        if (ReadSettingDialog.isShowing()) {
            //字号改变
            ((SeekBar)ReadSettingDialog.findViewById(R.id.size_setting)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            //字间距改变

            //行间距改变

            //切换夜间模式
        }
    }


    /**
     * 显示自定义的收藏的图标
     */
    private void showStarActionView(MenuItem item) {
        starItem = item;
        //这里使用一个ImageView设置成MenuItem的ActionView，这样我们就可以使用这个ImageView显示旋转动画了
        likeButton = getLayoutInflater().inflate(R.layout.action_bar_star, null).findViewById(R.id.star_button);
        item.setActionView(likeButton);

        setLikeButton();

    }

    private void setLikeButton() {
        //设置收藏状态
        if (currentFeedItem == null) {
            currentFeedItem = LitePal.where("id = ?", String.valueOf(mId)).limit(1).find(FeedItem.class).get(0);
        }
        likeButton.setLiked(currentFeedItem.isFavorite());
        //收藏的点击事件
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                currentFeedItem.setFavorite(true);
                currentFeedItem.save();
                if (mIndex == -1) {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_ID, mId, true));
                } else {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_INDEX, mIndex, true));
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                currentFeedItem.setFavorite(false);
                currentFeedItem.save();
                if (mIndex == -1) {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_ID, mId, false));
                } else {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_INDEX, mIndex, false));
                }
            }
        });
    }



    private void openLink(FeedItem feedItem) {
        String url = currentFeedItem.getUrl();
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        CustomTabActivityHelper.openCustomTab(
                PostDetailActivity.this, customTabsIntent, Uri.parse(url), new WebviewFallback());
    }
}
