package com.ihewro.focus.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.PostDetailListPagerAdapter;
import com.ihewro.focus.adapter.ReadBackgroundAdapter;
import com.ihewro.focus.bean.Background;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.PostSetting;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.callback.UICallback;
import com.ihewro.focus.helper.ParallaxTransformer;
import com.ihewro.focus.util.Constants;
import com.ihewro.focus.util.ShareUtil;
import com.ihewro.focus.util.StatusBarUtil;
import com.ihewro.focus.util.UIUtil;
import com.ihewro.focus.util.WebViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.callback.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import skin.support.utils.SkinPreference;


public class PostDetailActivity extends BackActivity {

    public static final int ORIGIN_SEARCH = 688;
    public static final int ORIGIN_MAIN = 350;
    public static final int ORIGIN_STAR = 836;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;


    private List<View> viewList = new ArrayList<>();
    private List<Integer> readList = new ArrayList<>();

    private static final List<Integer> colorList = Arrays.asList(R.color.white, R.color.green, R.color.yellow, R.color.pink, R.color.blue, R.color.blue2, R.color.color3, R.color.color4, R.color.color5);

    private List<Background> backgroundList = new ArrayList<>();

    private PostDetailListPagerAdapter adapter;

    private MaterialDialog ReadSettingDialog;
    private PostSetting postSetting;
    Class useClass;

    private int mIndex;
    private FeedItem currentFeedItem;
    private MenuItem starItem;

    private int origin;

    private int notReadNum = 0;

    private LinearLayoutManager linearLayoutManager;

    private List<FeedItem> feedItemList = new ArrayList<>();


    public static void activityStart(Activity activity, int indexInList, List<FeedItem> feedItemList, int origin) {
        Intent intent = new Intent(activity, PostDetailActivity.class);

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
        setContentView(R.layout.activity_post_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mIndex = bundle.getInt(Constants.KEY_INT_INDEX, 0);
        feedItemList = GlobalConfig.feedItemList;
        origin = bundle.getInt(Constants.POST_DETAIL_ORIGIN);

        initData();


    }


    private void initToolbarColor() {
        //根据偏好设置背景颜色修改toolbar的背景颜色
        if (!SkinPreference.getInstance().getSkinName().equals("night")) {
            toolbar.setBackgroundColor(PostSetting.getBackgroundInt(PostDetailActivity.this));
            StatusBarUtil.setColor(this, PostSetting.getBackgroundInt(PostDetailActivity.this), 0);
        }
    }




    public void initData() {
        currentFeedItem = feedItemList.get(mIndex);

    }

    private void initRecyclerView() {

        adapter = new PostDetailListPagerAdapter(getSupportFragmentManager(),PostDetailActivity.this);

        //初始化当前文章的对象
        initData();

        //显示未读数目
        new Thread(new Runnable() {
            @Override
            public void run() {
                PostDetailActivity.this.notReadNum = 0;
                if (origin != ORIGIN_STAR) {
                    for (FeedItem feedItem : feedItemList) {
                        if (!feedItem.isRead()) {
                            PostDetailActivity.this.notReadNum++;
                        }
                    }
                }

                adapter.setData(feedItemList);


                UIUtil.runOnUiThread(PostDetailActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setAdapter(adapter);

                        final float PARALLAX_COEFFICIENT = 0.6f;
                        final float DISTANCE_COEFFICIENT = 0.2f;

                        viewPager.setPageTransformer(true, new ParallaxTransformer(adapter,null,PARALLAX_COEFFICIENT, DISTANCE_COEFFICIENT));

                        //移动到当前文章的位置
                        viewPager.setCurrentItem(mIndex);


                        ALog.d("首次加载");
                        setLikeButton();
                        setCurrentItemStatus();
                        initPostClickListener();

                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int i, float v, int i1) {

                            }

                            @Override
                            public void onPageSelected(int i) {
                                ALog.d("onPageSelected");
                                mIndex = i;
                                //UI修改
                                initData();
                                setLikeButton();
                                //修改顶部导航栏的收藏状态
                                setCurrentItemStatus();
                                initPostClickListener();
                            }

                            @Override
                            public void onPageScrollStateChanged(int i) {

                            }
                        });



                        if (notReadNum <= 0) {
                            toolbar.setTitle("");
                        } else {
                            toolbar.setTitle(notReadNum + "");
                        }

                    }
                });
            }
        }).start();
    }


    /**
     * 为什么不在adapter里面写，因为recyclerview有缓存机制，没滑到这个时候就给标记为已读了
     */
    private void setCurrentItemStatus() {
        //将该文章标记为已读，并且通知首页修改布局
        if (!currentFeedItem.isRead()) {
            currentFeedItem.setRead(true);
            updateNotReadNum();
            currentFeedItem.saveAsync().listen(new SaveCallback() {
                @Override
                public void onFinish(boolean success) {
                    if (origin == ORIGIN_SEARCH) {//isUpdateMainReadMark 为false表示不是首页进来的
                        readList.add(currentFeedItem.getId());
                    } else if (origin == ORIGIN_MAIN) {
                        readList.add(mIndex);
                    }
                }
            });
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPostClickListener() {

        //文章双击收藏事件
        final GestureDetector gestureDetector = new GestureDetector(PostDetailActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(final MotionEvent e) {//双击事件
                ALog.d("双击");

                clickStarButton();

                return true;
            }
        });


        //双击顶栏回顶部事件
        final GestureDetector gestureDetector1 = new GestureDetector(PostDetailActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                NestedScrollView scrollView = (NestedScrollView) adapter.getViewByPosition(mIndex, R.id.post_turn);
                if (scrollView!=null){
                    scrollView.fullScroll(View.FOCUS_UP);
                }
                return super.onDoubleTap(e);
            }
        });


        if (UserPreference.queryValueByKey(UserPreference.notToTop, "0").equals("0")) {
            toolbar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return gestureDetector1.onTouchEvent(motionEvent);
                }
            });
        }

        initToolbarColor();

        if (UserPreference.queryValueByKey(UserPreference.notStar, "0").equals("0")) {
            //第一篇文章进入的时候这个view为null，我也不知道为什么！
            new Handler().postDelayed(new Runnable() {//做一个延迟绑定
                @Override
                public void run() {

                    View content = adapter.getViewByPosition(mIndex, R.id.post_content);
                    if (content != null) {
                        content.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
//                                ALog.d("什么情况？？双击事件");

                                return gestureDetector.onTouchEvent(event);
                            }
                        });
                    }
                }
            }, 500);


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.setTitle("");
        if (SkinPreference.getInstance().getSkinName().equals("night")) {
            getMenuInflater().inflate(R.menu.post_night, menu);
        } else {
            getMenuInflater().inflate(R.menu.post, menu);
        }

        starItem = menu.findItem(R.id.action_star);
        showStarActionView(starItem);

        //加载完menu才去加载后面的内容
        initRecyclerView();


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
//                                adapter.notifyItemChanged(mIndex);
                            }
                        })
                        .show();

                initReadSettingView();
                initReadSettingListener();


                initReadBackgroundView();

                break;

            case R.id.action_star:

                clickStarButton();

                break;

            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    private void clickStarButton() {

        FeedItem.clickWhenNotFavorite(PostDetailActivity.this, currentFeedItem, new UICallback() {
            @Override
            public void doUIWithFlag(boolean flag) {
                currentFeedItem.setFavorite(flag);
                if (flag) {//收藏了
                    Toasty.success(PostDetailActivity.this, "收藏成功").show();
                } else {
                    Toasty.success(PostDetailActivity.this, "取消收藏成功").show();
                }

                setLikeButton();

                currentFeedItem.saveAsync().listen(new SaveCallback() {
                    @Override
                    public void onFinish(boolean success) {
                        if (origin == ORIGIN_SEARCH) {
                            EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_ID, currentFeedItem.getId(), currentFeedItem.isFavorite()));
                        } else if (origin == ORIGIN_MAIN) {
                            EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_STAR_STATUS_BY_INDEX, mIndex, currentFeedItem.isFavorite()));
                        }
                    }
                });

            }
        });
    }

    private void initReadBackgroundView() {
        if (ReadSettingDialog.isShowing()) {
            RecyclerView recyclerView = (RecyclerView) ReadSettingDialog.findViewById(R.id.recycler_view);

            backgroundList.clear();
            for (Integer color : colorList) {
                backgroundList.add(new Background(ContextCompat.getColor(PostDetailActivity.this, color)));
            }
            linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            final ReadBackgroundAdapter adapter1 = new ReadBackgroundAdapter(PostDetailActivity.this, backgroundList);
            adapter1.bindToRecyclerView(recyclerView);
            adapter1.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter2, View view, int position) {
                    //改变背景颜色，并写入到数据库
                    UserPreference.updateOrSaveValueByKey(UserPreference.READ_BACKGROUND, String.valueOf(backgroundList.get(position).getColor()));
                    //刷新页面
                    //更新UI
                    adapter1.notifyDataSetChanged();
                    //修改背景颜色
                    //根据偏好设置背景颜色修改toolbar的背景颜色
                    initToolbarColor();
                    adapter.notifyItemChanged(mIndex);
                }
            });
        }
    }

    //根据现有的设置，恢复布局
    private void initReadSettingView() {
        if (ReadSettingDialog.isShowing()) {
            //设置字号
            ((SeekBar) ReadSettingDialog.findViewById(R.id.size_setting)).setProgress(Integer.parseInt(PostSetting.getFontSize()));
            ((TextView) ReadSettingDialog.findViewById(R.id.size_setting_info)).setText(PostSetting.getFontSize());

            //设置字间距
            ((SeekBar) ReadSettingDialog.findViewById(R.id.font_space_setting)).setProgress(Integer.parseInt(PostSetting.getFontSpace()));
            ((TextView) ReadSettingDialog.findViewById(R.id.font_space_setting_info)).setText(PostSetting.getFontSpace());


            //设置行间距
            ((SeekBar) ReadSettingDialog.findViewById(R.id.line_space_setting)).setProgress(Integer.parseInt(PostSetting.getLineSpace()));
            ((TextView) ReadSettingDialog.findViewById(R.id.line_space_setting_info)).setText(PostSetting.getLineSpace());
        }
    }

    private void initReadSettingListener() {
        if (ReadSettingDialog.isShowing()) {
            //字号改变
            ((SeekBar) ReadSettingDialog.findViewById(R.id.size_setting)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //修改左侧数字
                    ((TextView) ReadSettingDialog.findViewById(R.id.size_setting_info)).setText(String.valueOf(seekBar.getProgress()));
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
            ((SeekBar) ReadSettingDialog.findViewById(R.id.font_space_setting)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //修改左侧数字
                    ALog.d("");
                    ((TextView) ReadSettingDialog.findViewById(R.id.font_space_setting_info)).setText(String.valueOf(seekBar.getProgress()));

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
            ((SeekBar) ReadSettingDialog.findViewById(R.id.line_space_setting)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //修改左侧数字
                    ((TextView) ReadSettingDialog.findViewById(R.id.line_space_setting_info)).setText(String.valueOf(seekBar.getProgress()));
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

        if (currentFeedItem.isFavorite()) {
            starItem.setIcon(R.drawable.star_on);
        } else {
            if (SkinPreference.getInstance().getSkinName().equals("night")) {
                starItem.setIcon(R.drawable.star_off_night);
            } else {
                starItem.setIcon(R.drawable.star_off);
            }
        }
    }


    private void openLink(FeedItem feedItem) {
        String url = currentFeedItem.getUrl();
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (pattern.matcher(url).matches()) {
            Toasty.info(this, "该文章没有外链哦").show();
        } else {
            /*if (url.startsWith("/")){//相对地址
                Feed feed = LitePal.find(Feed.class,currentFeedItem.getFeedId());
                String origin = feed.getLink();
                if (!origin.endsWith("/")){
                    origin = origin + "/";
                }
                url = origin + url;
            }*/
            WebViewUtil.openLink(url, PostDetailActivity.this);
        }
    }


    private void updateNotReadNum() {
        this.notReadNum--;

        //UI修改
        if (notReadNum <= 0) {
            toolbar.setTitle("");
        } else {
            toolbar.setTitle(notReadNum + "");
        }

    }

    @Override
    protected void onDestroy() {

        //将首页中已读的文章样式标记为已读
        if (readList.size() > 0) {
            if (origin == ORIGIN_SEARCH) {//isUpdateMainReadMark 为false表示不是首页进来的
                EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_READ_STATUS_BY_ID_LIST, readList));
            } else if (origin == ORIGIN_MAIN) {
                EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_READ_STATUS_BY_INDEX_LIST, readList));
            }
            //修改首页未读数目
            EventBus.getDefault().post(new EventMessage(EventMessage.MAIN_READ_NUM_EDIT, mIndex));
        }


        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ALog.d("postDetail 被销毁");
    }

}
