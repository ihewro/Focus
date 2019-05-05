package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ihewro.focus.R;
import com.ihewro.focus.adapter.ViewPagerAdapter;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.fragemnt.UserFeedUpdateContentFragment;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.ExpandableBadgeDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

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


    private ViewPagerAdapter adapter;
    private UserFeedUpdateContentFragment feedPostsFragment;
    private Fragment currentFragment = null;
    private List<IDrawerItem> subItems = new ArrayList<>();
    private Drawer drawer;

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

        initEmptyView();

        clickFeedPostsFragment();

        initListener();
    }

    private void initListener() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
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
            }
        });
    }

    public void initEmptyView() {
        initDrawer();
    }

    //初始化侧边栏
    public void initDrawer() {

        ProfileDrawerItem currentUser = new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(getResources().getDrawable(R.drawable.ic_logo));
        currentUser.withSelectedTextColor(Color.BLACK);

        ExpandableBadgeDrawerItem one = new ExpandableBadgeDrawerItem().withName("Collapsable Badge").withIdentifier(18).withSelectable(false).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700)).withBadge("100").withSubItems(
                new SecondaryDrawerItem().withName("CollapsableItem").withLevel(2).withIdentifier(2000),
                new SecondaryDrawerItem().withName("CollapsableItem 2").withLevel(2).withIdentifier(2001)
        );

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.ic_logo)
                .addProfiles(currentUser)
                .withTextColor(Color.BLACK)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        refreshLeftDrawerFeedList();

        drawer = new DrawerBuilder().withActivity(this)
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withFullscreen(true)
                .withSelectedItem(-1)
                .addDrawerItems((IDrawerItem[]) Objects.requireNonNull(subItems.toArray(new IDrawerItem[subItems.size()])))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                //当前fragment显示所有数据
                                break;
                            case 2:
                                StarActivity.activityStart(MainActivity.this);
                                break;
                            case 3:
                                FeedCategoryActivity.activityStart(MainActivity.this);
                                break;

                        }
                        return false;
                    }
                })
                .build();

    }


    private void clickFeedPostsFragment() {
        if (feedPostsFragment == null) {
            feedPostsFragment = new UserFeedUpdateContentFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), feedPostsFragment);
    }


    /**
     * 获取用户的订阅数据，显示在左侧边栏的drawer中
     */
    public void refreshLeftDrawerFeedList() {
        subItems.clear();
        List<Feed> feedList = LitePal.findAll(Feed.class);
        subItems.add(new SecondaryDrawerItem().withName("全部").withIcon(GoogleMaterial.Icon.gmd_home).withSelectable(false));
        subItems.add(new SecondaryDrawerItem().withName("收藏").withIcon(GoogleMaterial.Icon.gmd_star).withSelectable(false));
        subItems.add(new SecondaryDrawerItem().withName("发现").withIcon(GoogleMaterial.Icon.gmd_explore).withSelectable(false));
        subItems.add(new SectionDrawerItem().withName("订阅源"));

        for (int i = 0; i < feedList.size(); i++) {
            Feed temp = feedList.get(i);
            SecondaryDrawerItem secondaryDrawerItem = new SecondaryDrawerItem().withName(temp.getName()).withIcon(GoogleMaterial.Icon.gmd_rss_feed).withSelectable(false);
            subItems.add(secondaryDrawerItem);
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

}
