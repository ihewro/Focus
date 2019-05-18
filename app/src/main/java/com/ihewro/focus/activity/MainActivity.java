package com.ihewro.focus.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;
import com.canking.minipay.Config;
import com.canking.minipay.MiniPayUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.FeedSearchAdapter;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.fragemnt.UserFeedUpdateContentFragment;
import com.ihewro.focus.view.FeedFolderOperationPopupView;
import com.ihewro.focus.view.FeedListShadowPopupView;
import com.ihewro.focus.view.FeedOperationPopupView;
import com.ihewro.focus.view.FilterPopupView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.interfaces.XPopupCallback;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.ExpandableBadgeDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import skin.support.SkinCompatManager;
import skin.support.utils.SkinPreference;


public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.playButton)
    ButtonBarLayout playButton;
    @BindView(R.id.fl_main_body)
    FrameLayout flMainBody;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_container)
    FrameLayout toolbarContainer;

    private static final int DRAWER_FOLDER_ITEM = 847;
    private static final int DRAWER_FOLDER = 301;
    private static final int SHOW_ALL = 14;
    private static final int SHOW_STAR = 876;
    private static final int SHOW_DISCOVER = 509;
    private static final int FEED_MANAGE = 460;
    private static final int SETTING = 911;
    private static final int PAY_SUPPORT = 71;
    @BindView(R.id.recycler_view_wrap)
    LinearLayout recyclerViewWrap;


    private UserFeedUpdateContentFragment feedPostsFragment;
    private Fragment currentFragment = null;
    private List<IDrawerItem> subItems = new ArrayList<>();
    private Drawer drawer;
    private List<FeedItem> searchResults = new ArrayList<>();
    private FeedSearchAdapter adapter;
    private FeedListShadowPopupView popupView;//点击顶部标题的弹窗
    private FilterPopupView drawerPopupView;//右侧边栏弹窗
    private List<String> errorFeedIdList = new ArrayList<>();

    public static void activityStart(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbarTitle.setText("全部文章");
        EventBus.getDefault().register(this);


        initEmptyView();

        clickFeedPostsFragment(new ArrayList<String>());

        initListener();

        initSearchAdapter();
    }


    /**
     * 初始化
     */
    private void initSearchAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FeedSearchAdapter(searchResults);
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEmptyView(R.layout.simple_empty_view);
    }

    private void initListener() {

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                recyclerView.setVisibility(View.GONE);
                recyclerViewWrap.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                //开始同步搜索
                queryFeedItemByText(newText);
                adapter.setNewData(searchResults);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewWrap.setVisibility(View.VISIBLE);

                adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        FeedItem item = searchResults.get(position);
                        ArrayList<Integer> list = new ArrayList<>();
                        for (FeedItem feedItem : searchResults) {
                            list.add(feedItem.getId());
                        }
                        PostDetailActivity.activityStart(MainActivity.this, position, list, false);

                    }
                });
                return false;
            }

        });


        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                recyclerView.setVisibility(View.GONE);
                recyclerViewWrap.setVisibility(View.GONE);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //显示弹窗
                if (popupView == null) {
                    popupView = (FeedListShadowPopupView) new XPopup.Builder(MainActivity.this)
                            .atView(playButton)
                            .setPopupCallback(new XPopupCallback() {
                                @Override
                                public void onShow() {
                                    popupView.getAdapter().setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                                        @Override
                                        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                                            if (view.getId() == R.id.long_click) {
                                                int feedFolderId = popupView.getFeedFolders().get(position).getId();
                                                List<Feed> feeds = LitePal.where("feedfolderid = ?", String.valueOf(feedFolderId)).find(Feed.class);
                                                ArrayList<String> list = new ArrayList<>();

                                                for (int i = 0; i < feeds.size(); i++) {
                                                    list.add(String.valueOf(feeds.get(i).getId()));
                                                }
                                                //切换到指定文件夹下
                                                clickAndUpdateMainFragmentData(list, popupView.getFeedFolders().get(position).getName(), drawerPopupView.getOrderChoice(), drawerPopupView.getFilterChoice());
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onDismiss() {
                                }
                            })
                            .asCustom(new FeedListShadowPopupView(MainActivity.this));
                }
                popupView.toggle();
            }
        });
    }


    /**
     * 全文搜索🔍
     *
     * @param text
     * @return
     */
    public void queryFeedItemByText(String text) {
        text = "%" + text + "%";
        searchResults.clear();
        searchResults = LitePal.where("title like ? or summary like ?", text, text).find(FeedItem.class);
    }

    public void initEmptyView() {
        initDrawer();
    }

    //初始化侧边栏
    public void initDrawer() {

        //构造侧边栏项目
        createDrawer();

        //构造右侧栏目
        createRightDrawer();

    }


    public void createDrawer() {
        //初始化侧边栏
        refreshLeftDrawerFeedList(false);

        //夜间模式控制开关
        boolean flag = false;
        if (SkinPreference.getInstance().getSkinName().equals("night")) {
            flag = true;
        }

        SwitchDrawerItem mode = new SwitchDrawerItem().withName("夜间").withIcon(GoogleMaterial.Icon.gmd_brightness_medium).withChecked(flag).withOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                ALog.d("点击状态", isChecked);
                if (isChecked) {
                    SkinCompatManager.getInstance().loadSkin("night", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recreate();
                        }
                    }, 200); // 延时1秒
                } else {
                    SkinCompatManager.getInstance().restoreDefaultTheme();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recreate();
                        }
                    }, 200); // 延时1秒
                }
            }
        });

        //初始化侧边栏
        drawer = new DrawerBuilder().withActivity(this)
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems((IDrawerItem[]) Objects.requireNonNull(subItems.toArray(new IDrawerItem[subItems.size()])))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        drawerItemClick(drawerItem);
                        return false;
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(View view, int position, IDrawerItem drawerItem) {
                        drawerLongClick(drawerItem);
                        return true;
                    }
                })
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName("订阅").withIcon(GoogleMaterial.Icon.gmd_swap_horiz).withIdentifier(10).withTag(FEED_MANAGE).withSelectable(false),
                        new SecondaryDrawerItem().withName("设置").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(10).withTag(SETTING).withSelectable(false),
                        new SecondaryDrawerItem().withName("工具").withIcon(GoogleMaterial.Icon.gmd_pan_tool).withIdentifier(10).withTag(-400).withSelectable(false),
                        new SecondaryDrawerItem().withName("捐赠").withIcon(GoogleMaterial.Icon.gmd_account_balance_wallet).withIdentifier(10).withTag(PAY_SUPPORT).withSelectable(false), mode)
                .build();
        drawer.setHeader(getLayoutInflater().inflate(R.layout.padding, null), false);
    }


    private void updateDrawer() {
        //初始化侧边栏
        refreshLeftDrawerFeedList(true);
        drawer.setItems(subItems);
    }

    private void drawerItemClick(IDrawerItem drawerItem) {
        if (drawerItem.getTag() != null) {
            switch ((int) drawerItem.getTag()) {
                case SHOW_ALL:
                    clickAndUpdateMainFragmentData(new ArrayList<String>(), "全部文章", drawerPopupView.getOrderChoice(), drawerPopupView.getFilterChoice());
                    break;
                case SHOW_STAR:
                    StarActivity.activityStart(MainActivity.this);
                    break;
                case SHOW_DISCOVER:
                    FeedCategoryActivity.activityStart(MainActivity.this);
                    break;
                case FEED_MANAGE://启用分类管理
                    FeedManageActivity.activityStart(MainActivity.this);
                    break;
                case SETTING://应用设置界面
                    SettingActivity.activityStart(MainActivity.this);
                    break;
                case PAY_SUPPORT://捐赠支持界面
                    MiniPayUtils.setupPay(MainActivity.this, new Config.Builder("FKX07840DBMQMUHP92W1DD", R.drawable.alipay, R.drawable.wechatpay).build());
                    break;
                case DRAWER_FOLDER_ITEM:
                    ALog.d("名称为" + ((SecondaryDrawerItem) drawerItem).getName() + "id为" + drawerItem.getIdentifier());
                    ArrayList<String> list = new ArrayList<>();
                    list.add(String.valueOf(drawerItem.getIdentifier()));
                    clickAndUpdateMainFragmentData(list, ((SecondaryDrawerItem) drawerItem).getName().toString(), drawerPopupView.getOrderChoice(), drawerPopupView.getFilterChoice());
                    break;

            }
        }

    }


    private void drawerLongClick(IDrawerItem drawerItem) {

        if (drawerItem.getTag() != null) {
            switch ((int) drawerItem.getTag()) {
                case DRAWER_FOLDER:
                    //获取到这个文件夹的数据
                    new XPopup.Builder(MainActivity.this)
                            .asCustom(new FeedFolderOperationPopupView(MainActivity.this, drawerItem.getIdentifier(), ((ExpandableBadgeDrawerItem) drawerItem).getName().toString(), "", new Help(false)))
                            .show();


                    break;
                case DRAWER_FOLDER_ITEM:
                    //获取到这个feed的数据
                    new XPopup.Builder(MainActivity.this)
                            .asCustom(new FeedOperationPopupView(MainActivity.this, drawerItem.getIdentifier(), ((SecondaryDrawerItem) drawerItem).getName().toString(), "", new Help(false)))
                            .show();
                    break;
            }
        }
    }

    /**
     * 初始化主fragment
     *
     * @param feedIdList
     */
    private void clickFeedPostsFragment(ArrayList<String> feedIdList) {
        if (feedPostsFragment == null) {
            feedPostsFragment = UserFeedUpdateContentFragment.newInstance(feedIdList, toolbar);
        }
        toolbar.setTitle("全部文章");
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), feedPostsFragment);
    }

    /**
     * 更新主fragment的内部数据并修改UI
     *
     * @param feedIdList
     * @param title
     */
    private void clickAndUpdateMainFragmentData(ArrayList<String> feedIdList, String title, int oderChoice, int filterChoice) {
        if (feedPostsFragment == null) {
            ALog.d("出现未知错误");
        } else {
            toolbarTitle.setText(title);
            feedPostsFragment.updateData(feedIdList, oderChoice, filterChoice);
        }

    }


    /**
     * 获取用户的订阅数据，显示在左侧边栏的drawer中
     */
    public void refreshLeftDrawerFeedList(boolean isUpdate) {
        subItems.clear();
        subItems.add(new SecondaryDrawerItem().withName("全部").withIcon(GoogleMaterial.Icon.gmd_home).withSelectable(true).withTag(SHOW_ALL));
        subItems.add(new SecondaryDrawerItem().withName("收藏").withIcon(GoogleMaterial.Icon.gmd_star).withSelectable(false).withTag(SHOW_STAR));
        subItems.add(new SecondaryDrawerItem().withName("发现").withIcon(GoogleMaterial.Icon.gmd_explore).withSelectable(false).withTag(SHOW_DISCOVER));
        subItems.add(new SectionDrawerItem().withName("订阅源").withDivider(false));


        List<FeedFolder> feedFolderList = LitePal.findAll(FeedFolder.class);
        for (int i = 0; i < feedFolderList.size(); i++) {

            int notReadNum = 0;

            List<IDrawerItem> feedItems = new ArrayList<>();
            List<Feed> feedList = LitePal.where("feedfolderid = ?", String.valueOf(feedFolderList.get(i).getId())).find(Feed.class);

            for (int j = 0; j < feedList.size(); j++) {
                Feed temp = feedList.get(j);
                int current_notReadNum = LitePal.where("read = ? and feedid = ?", "0", String.valueOf(temp.getId())).count(FeedItem.class);

                SecondaryDrawerItem secondaryDrawerItem = new SecondaryDrawerItem().withName(temp.getName()).withSelectable(true).withTag(DRAWER_FOLDER_ITEM).withIdentifier(feedList.get(j).getId());
                if (feedList.get(j).isErrorGet()) {
                    secondaryDrawerItem.withIcon(GoogleMaterial.Icon.gmd_signal_wifi_off);
                } else {
                    secondaryDrawerItem.withIcon(GoogleMaterial.Icon.gmd_rss_feed);
                }
                if (current_notReadNum != 0) {
                    secondaryDrawerItem.withBadge(current_notReadNum + "");
                }
                if (isUpdate) {
                    drawer.updateItem(secondaryDrawerItem);
                }
                feedItems.add(secondaryDrawerItem);

                notReadNum += current_notReadNum;
            }

            ExpandableBadgeDrawerItem one = new ExpandableBadgeDrawerItem().withName(feedFolderList.get(i).getName()).withIdentifier(feedFolderList.get(i).getId()).withTag(DRAWER_FOLDER).withSelectable(false).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700)).withSubItems(
                    feedItems
            );
            if (notReadNum != 0) {
                one.withBadge(notReadNum + "");
            }
            //添加文件夹
            subItems.add(one);
        }

        //要记得把这个list置空
        errorFeedIdList.clear();
        ;

    }


    /**
     * 添加或者显示 fragment
     *
     * @param transaction
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        //当前的fragment就是点击切换的目标fragment，则不用操作
        if (currentFragment == fragment) {
            return;
        }

        Fragment willCloseFragment = currentFragment;//上一个要切换掉的碎片
        currentFragment = fragment;//当前要显示的碎片

        if (willCloseFragment != null) {
            transaction.hide(willCloseFragment);
        }
        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.add(R.id.fl_main_body, currentFragment).commitAllowingStateLoss();
        } else {
            transaction.show(currentFragment).commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (SkinPreference.getInstance().getSkinName().equals("night")) {
            getMenuInflater().inflate(R.menu.main_night, menu);
        } else {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        if (SkinPreference.getInstance().getSkinName().equals("night")) {
//            item.tin
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_filter:

                drawerPopupView.toggle();
                break;
        }
        return true;
    }

    long startTime = 0;

    @Override
    public void onBackPressed() {
        //返回键关闭🔍搜索
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - startTime) >= 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                startTime = currentTime;
            } else {
                finish();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshUI(EventMessage eventBusMessage) {
        if (EventMessage.feedAndFeedFolderAndItemOperation.contains(eventBusMessage.getType())) {
            ALog.d("收到新的订阅添加，更新！" + eventBusMessage);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateDrawer();
                }
            }, 800); // 延迟一下，因为数据异步存储需要时间
        } else if (Objects.equals(eventBusMessage.getType(), EventMessage.FEED_PULL_DATA_ERROR)) {
            ALog.d("收到错误FeedId List");
//            errorFeedIdList = eventBusMessage.getIds();
        }
    }


    private void createRightDrawer() {
        drawerPopupView = (FilterPopupView) new XPopup.Builder(this)
                .popupPosition(PopupPosition.Right)//右边
                .hasStatusBarShadow(true) //启用状态栏阴影
                .setPopupCallback(new XPopupCallback() {
                    @Override
                    public void onShow() {

                    }

                    @Override
                    public void onDismiss() {
                        //刷新当前页面的数据，因为筛选的规则变了
                        if (drawerPopupView.isNeedUpdate()) {
                            clickAndUpdateMainFragmentData(feedPostsFragment.getFeedIdList(), toolbarTitle.getText().toString(), drawerPopupView.getOrderChoice(), drawerPopupView.getFilterChoice());
                            drawerPopupView.setNeedUpdate(false);
                        }
                    }
                })
                .asCustom(new FilterPopupView(MainActivity.this));
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (currentFragment == null && fragment instanceof UserFeedUpdateContentFragment) {
            currentFragment = fragment;
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }
}
