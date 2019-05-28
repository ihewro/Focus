package com.ihewro.focus.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.PostDetailListAdapter;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.PostSetting;
import com.ihewro.focus.bean.StarItem;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.helper.RecyclerViewPageChangeListenerHelper;
import com.ihewro.focus.util.Constants;
import com.ihewro.focus.util.ShareUtil;
import com.ihewro.focus.util.WebViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import skin.support.utils.SkinPreference;


public class PostDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;



    private PostDetailListAdapter adapter;


    private MaterialDialog ReadSettingDialog;
    private PostSetting postSetting;
    Class useClass;

    private int mId;
    private int mIndex;
    private FeedItem currentFeedItem;
    private MenuItem starItem;
    private boolean isUpdateMainReadMark;//true表示从首页进来的。false表示从搜索结果中进来的
    private boolean isStar;//true表示从收藏页面进来的，false表示从首页进来的

    private List<Integer> feedItemIdList;
    private int notReadNum = 0;




    public static void activityStart(Activity activity,int indexInList, ArrayList<Integer> feedItemIdList,boolean flag,boolean isStar) {
        Intent intent = new Intent(activity, PostDetailActivity.class);
        intent.putExtra(Constants.KEY_INT_INDEX, indexInList);
        intent.putExtra(Constants.IS_UPDATE_MAIN_READ_MARK,flag);
        intent.putExtra(Constants.IS_FROM_STAR_ACTIVITY,isStar);
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
        isStar = intent.getBooleanExtra(Constants.IS_FROM_STAR_ACTIVITY,false);


        initData();

        initRecyclerView();


        initListener();


    }





    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {




    }


    public void initData() {
        if (isStar){
            currentFeedItem = LitePal.find(StarItem.class,mId);
        }else {
            currentFeedItem = LitePal.find(FeedItem.class,mId);
        }
    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //移动到当前文章的位置
        linearLayoutManager.scrollToPositionWithOffset(mIndex, 0);
        linearLayoutManager.setStackFromEnd(true);


        //获取所有文章的对象
        this.notReadNum = 0;
        final List<FeedItem> feedItemList = new ArrayList<>();
        for (Integer id: feedItemIdList){
            FeedItem feedItem;
            if (isStar){
                feedItem = LitePal.find(StarItem.class,id);
            }else {
                feedItem = LitePal.find(FeedItem.class,id);
            }
            if (!feedItem.isRead()){
                this.notReadNum ++;
            }
            feedItemList.add(feedItem);
        }

        if (notReadNum <= 0){
            toolbar.setTitle("");
        }else {
            toolbar.setTitle(notReadNum+"");
        }

        adapter = new PostDetailListAdapter(PostDetailActivity.this,isUpdateMainReadMark,postSetting, feedItemList);
        adapter.bindToRecyclerView(recyclerView);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerViewPageChangeListenerHelper(snapHelper, new RecyclerViewPageChangeListenerHelper.OnPageChangeListener() {
            @Override
            public void onFirstScroll() {
                initPostClickListener();
                setCurrentItemStatus();
                ALog.d("首次加载");

            }

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
                ALog.d("onPageSelected" + position + feedItemList.get(position));
                mIndex = position;
                mId = feedItemIdList.get(mIndex);
                initData();
                //修改顶部导航栏的收藏状态
                setLikeButton();
                initPostClickListener();
                setCurrentItemStatus();


            }
        }));

    }


    /**
     * 为什么不在adapter里面写，因为recyclerview有缓存机制，没滑到这个时候就给标记为已读了
     */
    private void setCurrentItemStatus(){
        //将该文章标记为已读，并且通知首页修改布局
        if (!currentFeedItem.isRead()){
            currentFeedItem.setRead(true);
            currentFeedItem.save();
            if (!isUpdateMainReadMark) {//isUpdateMainReadMark 为false表示不是首页进来的
                EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_READ_STATUS_BY_ID, currentFeedItem.getId()));
            } else {
                EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_READ_STATUS_BY_INDEX, mIndex));
            }
            updateNotReadNum();
        }
    }

    private void initPostClickListener(){

        //文章的touch事件
        final GestureDetector gestureDetector = new GestureDetector(PostDetailActivity.this, new GestureDetector.SimpleOnGestureListener() {
            /**
             * 发生确定的单击时执行
             * @param e
             * @return
             */
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {//单击事件
                ALog.d("单击");
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
                if (currentFeedItem.isFavorite()){
                    currentFeedItem.setFavorite(false);
                }else {
                    currentFeedItem.setFavorite(true);
                }
                currentFeedItem.save();
                if (!isUpdateMainReadMark) {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_ID, mId, currentFeedItem.isFavorite()));
                } else {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_INDEX, mIndex, currentFeedItem.isFavorite()));
                }
                setLikeButton();
                return true;
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

         adapter.getViewByPosition(mIndex,R.id.post_content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(SkinPreference.getInstance().getSkinName().equals("night")){
            getMenuInflater().inflate(R.menu.post_night, menu);
        }else {
            getMenuInflater().inflate(R.menu.post, menu);
        }

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
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //重置设置
                                UserPreference.updateOrSaveValueByKey(PostSetting.FONT_SIZE, PostSetting.FONT_SIZE_DEFAULT);
                                UserPreference.updateOrSaveValueByKey(PostSetting.FONT_SPACING, PostSetting.FONT_SPACING_DEFAULT);
                                UserPreference.updateOrSaveValueByKey(PostSetting.LINE_SPACING, PostSetting.LINE_SPACING_DEFAULT);
                                adapter.notifyItemChanged(mIndex);
                            }
                        })
                        .show();

                initReadSettingView();
                initReadSettingListener();
                break;

            case R.id.action_star:
                currentFeedItem.setFavorite(!currentFeedItem.isFavorite());
                currentFeedItem.save();

                if (!isUpdateMainReadMark) {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_ID, mId, currentFeedItem.isFavorite()));
                } else {
                    EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_INDEX, mIndex, currentFeedItem.isFavorite()));
                }
                setLikeButton();

                break;

            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    //根据现有的设置，恢复布局
    private void initReadSettingView(){
        if (ReadSettingDialog.isShowing()){
            if (ReadSettingDialog.isShowing()) {
                //设置字号
                ((SeekBar)ReadSettingDialog.findViewById(R.id.size_setting)).setProgress(Integer.parseInt(PostSetting.getFontSize()));
                ((TextView)ReadSettingDialog.findViewById(R.id.size_setting_info)).setText(PostSetting.getFontSize());

                //设置字间距
                ((SeekBar)ReadSettingDialog.findViewById(R.id.font_space_setting)).setProgress(Integer.parseInt(PostSetting.getFontSpace()));
                ((TextView)ReadSettingDialog.findViewById(R.id.font_space_setting_info)).setText(PostSetting.getFontSpace());


                //设置行间距
                ((SeekBar)ReadSettingDialog.findViewById(R.id.line_space_setting)).setProgress(Integer.parseInt(PostSetting.getLineSpace()));
                ((TextView)ReadSettingDialog.findViewById(R.id.line_space_setting_info)).setText(PostSetting.getLineSpace());
            }
        }
    }
    private void initReadSettingListener() {
        if (ReadSettingDialog.isShowing()) {
            //字号改变
            ((SeekBar)ReadSettingDialog.findViewById(R.id.size_setting)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //修改左侧数字
                    ((TextView)ReadSettingDialog.findViewById(R.id.size_setting_info)).setText(String.valueOf(seekBar.getProgress()));

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //修改文章配置UI
                    UserPreference.updateOrSaveValueByKey(PostSetting.FONT_SIZE, String.valueOf(seekBar.getProgress()));
                    adapter.notifyItemChanged(mIndex);//更新UI


                }
            });

            //字间距改变
            ((SeekBar)ReadSettingDialog.findViewById(R.id.font_space_setting)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //修改左侧数字
                    ALog.d("");
                    ((TextView)ReadSettingDialog.findViewById(R.id.font_space_setting_info)).setText(String.valueOf(seekBar.getProgress()));

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {


                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //修改文章配置UI
                    UserPreference.updateOrSaveValueByKey(PostSetting.FONT_SPACING, String.valueOf(seekBar.getProgress()));
                    adapter.notifyItemChanged(mIndex);//更新UI
                }
            });

            //行间距改变
            ((SeekBar)ReadSettingDialog.findViewById(R.id.line_space_setting)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //修改左侧数字
                    ((TextView)ReadSettingDialog.findViewById(R.id.line_space_setting_info)).setText(String.valueOf(seekBar.getProgress()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    ALog.d("");

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //修改文章配置UI
                    UserPreference.updateOrSaveValueByKey(PostSetting.LINE_SPACING, String.valueOf(seekBar.getProgress()));
                    adapter.notifyItemChanged(mIndex);//更新UI

                }
            });
        }
    }


    /**
     * 显示自定义的收藏的图标
     */
    private void showStarActionView(MenuItem item) {
        starItem = item;
        setLikeButton();
    }

    private void setLikeButton() {
        //设置收藏状态
        if (currentFeedItem == null) {
            if (isStar){
                currentFeedItem = LitePal.where("id = ?", String.valueOf(mId)).limit(1).find(StarItem.class).get(0);
            }else {
                currentFeedItem = LitePal.where("id = ?", String.valueOf(mId)).limit(1).find(FeedItem.class).get(0);
            }
        }
        if (currentFeedItem.isFavorite()){
            starItem.setIcon(R.drawable.star_on);
        }else {
            if(SkinPreference.getInstance().getSkinName().equals("night")){
                starItem.setIcon(R.drawable.star_off_night);
            }else {
                starItem.setIcon(R.drawable.star_off);
            }
        }
    }



    private void openLink(FeedItem feedItem) {
        String url = currentFeedItem.getUrl();
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (pattern.matcher(url).matches()){
            Toasty.info(this,"该文章没有外链哦").show();
        }else {
            WebViewUtil.openLink(url,PostDetailActivity.this);
        }
    }



    private void updateNotReadNum(){
        this.notReadNum --;
        if (notReadNum <= 0){
            toolbar.setTitle("");
        }else {
            toolbar.setTitle(notReadNum+"");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ALog.d("postDetail 被销毁");
    }
}
