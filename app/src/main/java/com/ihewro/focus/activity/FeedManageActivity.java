package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.FrameLayout;

import com.ihewro.focus.R;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.fragemnt.FeedFolderListManageFragment;
import com.ihewro.focus.fragemnt.FeedListManageFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 分类管理页面，对分类、分类文件夹增加、删除、修改、排序
 */
public class FeedManageActivity extends BaseActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fl_main_body)
    FrameLayout flMainBody;

    private Fragment currentFragment = null;

    private FeedListManageFragment feedListManageFragment;
    private FeedFolderListManageFragment feedFolderListManageFragment;

    public static void activityStart(Activity activity) {
        Intent intent = new Intent(activity, FeedManageActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_manage);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setSupportActionBar(toolbar);

        showFeedFolderListManageFragment();
    }

    /**
     * 显示订阅文件中订阅列表的碎片
     *
     * @param id
     */
    private void showFeedListManageFragment(int id) {
        if (feedListManageFragment == null) {
            feedListManageFragment = FeedListManageFragment.newInstance(id);
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), feedListManageFragment);
    }

    /**
     * 初始化主fragment
     */
    private void showFeedFolderListManageFragment() {
        if (feedFolderListManageFragment == null) {
            feedFolderListManageFragment = FeedFolderListManageFragment.newInstance();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), feedFolderListManageFragment);
    }

    /**
     * 添加或者显示 fragment
     *
     * @param transaction
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //设置动画
        transaction.setCustomAnimations(
                R.anim.right_in,
                R.anim.right_out);
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


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshUI(EventMessage eventBusMessage) {
        //切换进入feedListFragment
        if (Objects.equals(eventBusMessage.getType(), EventMessage.SHOW_FEED_LIST_MANAGE)) {
            int id = Integer.parseInt(eventBusMessage.getMessage());
            showFeedListManageFragment(id);
        }else if (Objects.equals(eventBusMessage.getType(), EventMessage.SHOW_FEED_FOLDER_MANAGE)){
            showFeedFolderListManageFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (currentFragment == null || currentFragment == feedFolderListManageFragment){
            finish();
        }else if(currentFragment == feedListManageFragment){
            showFeedFolderListManageFragment();
        }
    }
}
