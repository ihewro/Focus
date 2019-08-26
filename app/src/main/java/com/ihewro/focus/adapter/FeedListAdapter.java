package com.ihewro.focus.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.FeedListActivity;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedRequire;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.http.HttpInterface;
import com.ihewro.focus.http.HttpUtil;
import com.ihewro.focus.util.ImageLoaderManager;
import com.ihewro.focus.util.RSSUtil;
import com.ihewro.focus.util.StringUtil;
import com.ihewro.focus.util.UIUtil;
import com.ihewro.focus.view.RequireListPopupView;
import com.lxj.xpopup.XPopup;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FeedListAdapter extends BaseQuickAdapter<Feed, BaseViewHolder> {

    private String mName;
    private Activity activity;
    private List<FeedRequire> feedRequireList = new ArrayList<>();

    public FeedListAdapter(@Nullable List<Feed> data, Activity activity,String name) {
        super(R.layout.item_feed,data);
        this.activity = activity;
        this.mName = name;
        initListener();

    }

    @Override
    protected void convert(BaseViewHolder helper, Feed item) {
        helper.setText(R.id.name,item.getName());
        helper.setText(R.id.desc,item.getDesc());
        if (!StringUtil.trim(item.getIcon()).equals("")){//显示图标
            ImageLoader.getInstance().displayImage(StringUtil.trim(String.valueOf(item.getIcon())), (ImageView) helper.getView(R.id.account_avatar),ImageLoaderManager.getSubsciptionIconOptions(activity));
        }else {
            helper.setImageResource(R.id.account_avatar,R.drawable.ic_rss_feed_grey_24dp);
        }
    }

    private void initListener(){
        this.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final BaseQuickAdapter adapter, View view, final int position) {
                showRequireList(position,FeedListAdapter.this.getData().get(position));
            }
        });
    }


    /**
     * 显示参数列表
     * @param position
     */
    private void showRequireList(final int position, final Feed feed){
        //显示弹窗，填写参数进行订阅
        final MaterialDialog loading = new MaterialDialog.Builder(activity)
                .title("加载参数")
                .content("正在网络请求参数")
                .progress(false, 0, true)
                .build();

        loading.show();
        Retrofit retrofit = HttpUtil.getRetrofit("bean", GlobalConfig.serverUrl,10,10,10);
        Call<List<FeedRequire>> request = retrofit.create(HttpInterface.class).getFeedRequireListByWebsite(feed.getIid());
        request.enqueue(new Callback<List<FeedRequire>>() {
            @SuppressLint("CheckResult")
            @Override
            public void onResponse(@NonNull Call<List<FeedRequire>> call, @NonNull Response<List<FeedRequire>> response) {
                if (response.isSuccessful()) {
                    //结束ui
                    loading.dismiss();

                    assert response.body() != null;
                    feedRequireList.clear();
                    //feed更新到当前的时间流中。
                    feedRequireList.addAll(response.body());
                    feedRequireList.add(new FeedRequire("订阅名称","取一个名字吧",FeedRequire.SET_NAME,mName+"的"+feed.getName()));


                    String url = feed.getUrl();
                    if (feed.getUrl().charAt(0) != '/'){
                        url = "/"+url;
                    }
                    if (feed.getType().equals("rsshub") && RSSUtil.urlIsContainsRSSHub(feed.getUrl()) == -1){//只有当在线市场的源标记为rsshub，且url中没有rsshub前缀，才会添加当前选择的前缀。
                        feed.setUrl(UserPreference.getRssHubUrl() + url);
                    }

                    Help help;
                    if (!StringUtil.trim(feed.getExtra()).equals("")){
                        help = new Help(true,feed.getExtra());
                    }else {
                        help = new Help(false);
                    }
                    //用一个弹窗显示参数列表
                    new XPopup.Builder(activity)
                            .asCustom(new RequireListPopupView(activity,feedRequireList,"订阅参数填写","",help,feed,((FragmentActivity)activity).getSupportFragmentManager()))
                            .show();

                } else {
                    ALog.d("请求失败" + response.errorBody());
                    Toasty.error(UIUtil.getContext(),"请求失败" + response.errorBody(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<FeedRequire>> call, @NonNull Throwable t) {
                loading.dismiss();
            }
        });
    }
}
