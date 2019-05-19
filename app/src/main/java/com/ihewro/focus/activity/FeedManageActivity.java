package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedRequire;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.fragemnt.FeedFolderListManageFragment;
import com.ihewro.focus.fragemnt.FeedListManageFragment;
import com.ihewro.focus.util.OPMLCreateHelper;
import com.ihewro.focus.util.OPMLReadHelper;
import com.ihewro.focus.view.RequireListPopupView;
import com.lxj.xpopup.XPopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 分类管理页面，对分类、分类文件夹增加、删除、修改、排序
 */
public class FeedManageActivity extends BackActivity implements EasyPermissions.PermissionCallbacks,FileChooserDialog.FileCallback {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fl_main_body)
    FrameLayout flMainBody;

    private Fragment currentFragment = null;
    OPMLReadHelper opmlReadHelper;
    OPMLCreateHelper opmlCreateHelper;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        }else {
            //更新数据
            feedListManageFragment.updateData(id);
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
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_import://导入
                opmlReadHelper = new OPMLReadHelper(FeedManageActivity.this);
                opmlReadHelper.run();
                break;

            case R.id.action_export://导出
                opmlCreateHelper = new OPMLCreateHelper(FeedManageActivity.this);
                opmlCreateHelper.run();
                break;

            case R.id.action_add_by_url:
                //弹窗
                List<FeedRequire> list = new ArrayList<>();
                list.add(new FeedRequire("订阅地址","举例：https://www.ihewro.com/feed",FeedRequire.SET_URL));
                list.add(new FeedRequire("订阅名称","随意给订阅取一个名字",FeedRequire.SET_NAME));
                new XPopup.Builder(FeedManageActivity.this)
//                        .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                        .asCustom(new RequireListPopupView(FeedManageActivity.this,list,"手动订阅","适用于高级玩家",new Help(false),new Feed(),getSupportFragmentManager()))
                        .show();
                break;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /**
         * 2. easyPermission 接管权限管理
         * {@link OPMLReadHelper#run}
         */
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment == null || currentFragment == feedFolderListManageFragment){
            finish();
        }else if(currentFragment == feedListManageFragment){
            showFeedFolderListManageFragment();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //3.1 申请成功
        if (requestCode == OPMLReadHelper.RQUEST_STORAGE_READ){
            if (opmlReadHelper==null){
                opmlReadHelper = new OPMLReadHelper(FeedManageActivity.this);
            }
            opmlReadHelper.run();
        }else if (requestCode == OPMLCreateHelper.REQUEST_STORAGE_WRITE){
            if (opmlCreateHelper==null){
                opmlCreateHelper = new OPMLCreateHelper(FeedManageActivity.this);
            }
            opmlCreateHelper.run();
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //3.2 申请失败
        // 用户因为之前点了不再显示权限申请提示导致的申请失败，显示一个引导去设置界面的对话框
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }else {//用户拒绝的申请,显示通知
            Toasty.error(FeedManageActivity.this,"请允许读存储器的权限，这样才能导入文件哦！").show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理3.2 的第一种情况
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Toast.makeText(this, "从设置界面返回", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onFileSelection(@NonNull FileChooserDialog dialog, @NonNull File file) {
        //选择了某个文件
        opmlReadHelper.add(file.getAbsolutePath());
    }

    @Override
    public void onFileChooserDismissed(@NonNull FileChooserDialog dialog) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
