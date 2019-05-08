package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.ihewro.focus.fragemnt.UserFeedUpdateContentFragment;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.ExpandableBadgeDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
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


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    private UserFeedUpdateContentFragment feedPostsFragment;
    private Fragment currentFragment = null;
    private List<IDrawerItem> subItems = new ArrayList<>();
    private Drawer drawer;
    private List<FeedItem> searchResults = new ArrayList<>();
    private FeedSearchAdapter adapter;
    private AccountHeader headerResult;
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

        EventBus.getDefault().register(this);

        initEmptyView();

        clickFeedPostsFragment(new ArrayList<String>());

        initListener();

        initSearchAdapter();
    }


    private void initSearchAdapter(){
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                //开始同步搜索
                queryFeedItemByText(newText);
                adapter.setNewData(searchResults);
                recyclerView.setVisibility(View.VISIBLE);

                adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        FeedItem item = searchResults.get(position);
                        PostDetailActivity.activityStart(MainActivity.this, item.getIid(), -1);

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
        updateDrawer();

    }


    public void updateDrawer(){
        //初始化侧边栏
        refreshLeftDrawerFeedList();


        drawer = new DrawerBuilder().withActivity(this)
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeaderPadding(true)
                .withSelectedItem(-1)
                .addDrawerItems((IDrawerItem[]) Objects.requireNonNull(subItems.toArray(new IDrawerItem[subItems.size()])))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 0:
                                //当前fragment显示所有数据
                                clickAndUpdateMainFragmentData(new ArrayList<String>(),"全部文章");
                                break;
                            case 1:
                                StarActivity.activityStart(MainActivity.this);
                                break;
                            case 2:
                                FeedCategoryActivity.activityStart(MainActivity.this);
                                break;

                        }

                        switch ((int) drawerItem.getIdentifier()) {
                            case -100://启用分类管理
                                FeedManageActivity.activityStart(MainActivity.this);
                                break;
                            case -200://应用设置界面
                                SettingActivity.activityStart(MainActivity.this);
                                break;
                            case -300://捐赠支持界面
                                MiniPayUtils.setupPay(MainActivity.this, new Config.Builder("FKX07840DBMQMUHP92W1DD", R.drawable.alipay, R.drawable.wechatpay).build());
                                break;
                            case 1://TODO:表示为切换fragment显示的内容
                                ALog.d("名称为"+((SecondaryDrawerItem)drawerItem).getName() + "id为" + drawerItem.getTag());
                                ArrayList<String> list = new ArrayList<>();
                                list.add(String.valueOf(drawerItem.getTag()));
                                clickAndUpdateMainFragmentData(list,((SecondaryDrawerItem)drawerItem).getName().toString());
                                break;

                        }
                        return false;
                    }
                })
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName("分类管理").withIcon(GoogleMaterial.Icon.gmd_swap_horiz).withIdentifier(10).withIdentifier(-100),
                        new SecondaryDrawerItem().withName("应用设置").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(10).withIdentifier(-200),
                        new SecondaryDrawerItem().withName("捐赠支持").withIcon(GoogleMaterial.Icon.gmd_account_balance_wallet).withIdentifier(10).withIdentifier(-300)

                )
                .build();
    }


    /**
     * 初始化主fragment
     * @param feedIdList
     */
    private void clickFeedPostsFragment(ArrayList<String> feedIdList) {
        if (feedPostsFragment == null) {
            feedPostsFragment =  UserFeedUpdateContentFragment.newInstance(feedIdList);
        }
        toolbar.setTitle("全部文章");
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), feedPostsFragment);
    }

    /**
     * 更新主fragment的内部数据并修改UI
     * @param feedIdList
     * @param title
     */
    private void clickAndUpdateMainFragmentData(ArrayList<String> feedIdList,String title){
        if (feedPostsFragment == null){
            ALog.d("出现未知错误");
        }else {
            toolbar.setTitle(title);
            feedPostsFragment.updateData(feedIdList);
        }

    }


    /**
     * 获取用户的订阅数据，显示在左侧边栏的drawer中
     */
    public void refreshLeftDrawerFeedList() {



        subItems.clear();
        subItems.add(new SecondaryDrawerItem().withName("全部").withIcon(GoogleMaterial.Icon.gmd_home).withSelectable(false));
        subItems.add(new SecondaryDrawerItem().withName("收藏").withIcon(GoogleMaterial.Icon.gmd_star).withSelectable(false));
        subItems.add(new SecondaryDrawerItem().withName("发现").withIcon(GoogleMaterial.Icon.gmd_explore).withSelectable(false));
        subItems.add(new SectionDrawerItem().withName("订阅源"));


        List<FeedFolder> feedFolderList = LitePal.findAll(FeedFolder.class);
        for (int i = 0 ;i < feedFolderList.size();i++){

            int notReadNum =10;//TODO:未读文章数目

            List<IDrawerItem> feedItems = new ArrayList<>();
            List<Feed> feedList = LitePal.where("feedfolderid = ?", String.valueOf(feedFolderList.get(i).getId())).find(Feed.class);

            for (int j = 0; j < feedList.size(); j++) {

                Feed temp = feedList.get(j);
                SecondaryDrawerItem secondaryDrawerItem = new SecondaryDrawerItem().withName(temp.getName()).withIcon(GoogleMaterial.Icon.gmd_rss_feed).withSelectable(false).withIdentifier(1).withTag(feedList.get(j).getId());
                feedItems.add(secondaryDrawerItem);

                notReadNum += temp.getUnreadNum();
            }

            ExpandableBadgeDrawerItem one = new ExpandableBadgeDrawerItem().withName(feedFolderList.get(i).getName()).withIdentifier(18).withSelectable(false).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700)).withBadge(notReadNum+"").withSubItems(
                    feedItems
            );
            //添加文件夹
            subItems.add(one);
        }
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
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

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
        if (Objects.equals(eventBusMessage.getType(), EventMessage.ADD_FEED)) {
            ALog.d("收到新的订阅添加，更新！");
            updateDrawer();
        }
    }


}
