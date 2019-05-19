package com.ihewro.focus.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.FeedListAdapter;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedRequire;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.http.HttpInterface;
import com.ihewro.focus.http.HttpUtil;
import com.ihewro.focus.util.Constants;
import com.ihewro.focus.util.RSSUtil;
import com.ihewro.focus.util.StringUtil;
import com.ihewro.focus.util.UIUtil;
import com.ihewro.focus.view.RequireListPopupView;
import com.lxj.xpopup.XPopup;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FeedListActivity extends BackActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private FeedListAdapter feedListAdapter;
    private List<Feed> feedList = new ArrayList<>();
    private List<FeedRequire> feedRequireList = new ArrayList<>();

    public static void activityStart(Activity activity, String websiteName) {
        Intent intent = new Intent(activity, FeedListActivity.class);
        intent.putExtra(Constants.KEY_STRING_WEBSITE_ID, websiteName);
        activity.startActivity(intent);
    }


    String mName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mName = intent.getStringExtra(Constants.KEY_STRING_WEBSITE_ID);

        setSupportActionBar(toolbar);
        toolbar.setTitle(mName+"的可订阅列表");
        initView();
        bindListener();
        refreshLayout.autoRefresh();
        refreshLayout.setEnableLoadMore(false);
    }


    public void initView(){
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        feedListAdapter = new FeedListAdapter(feedList);
        feedListAdapter.bindToRecyclerView(recyclerView);
//        feedListAdapter.setEmptyView(R.layout.simple_loading_view,recyclerView);
    }

    /**
     * 请求一个网站的可订阅列表
     */
    public void requestData(){
        Retrofit retrofit = HttpUtil.getRetrofit("bean", GlobalConfig.serverUrl,10,10,10);
        ALog.d("名称为" + mName);
        Call<List<Feed>> request = retrofit.create(HttpInterface.class).getFeedListByWebsite(mName);

        request.enqueue(new Callback<List<Feed>>() {
            @Override
            public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                if (response.isSuccessful()){
                    feedList.clear();
                    feedList.addAll(response.body());

                    feedListAdapter.setNewData(feedList);

                    if (feedList.size()==0){
                        feedListAdapter.setEmptyView(R.layout.simple_empty_view,recyclerView);
                    }
                    Toasty.success(UIUtil.getContext(),"请求成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toasty.error(UIUtil.getContext(),"请求失败2" + response.errorBody(), Toast.LENGTH_SHORT).show();
                }
                refreshLayout.finishRefresh(true);
            }

            @SuppressLint("CheckResult")
            @Override
            public void onFailure(Call<List<Feed>> call, Throwable t) {
                Toasty.error(UIUtil.getContext(),"请求失败2" + t.toString(), Toast.LENGTH_SHORT).show();
                refreshLayout.finishRefresh(false);
            }
        });

    }


    public void bindListener(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestData();
            }
        });

        feedListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final BaseQuickAdapter adapter, View view, final int position) {
                showRequireList(position);
            }
        });
    }


    /**
     * 显示参数列表
     * @param position
     */
    private void showRequireList(final int position){
        //显示弹窗，填写参数进行订阅
        final MaterialDialog loading = new MaterialDialog.Builder(FeedListActivity.this)
                .title("加载参数")
                .content("正在网络请求参数")
                .progress(false, 0, true)
                .build();

        loading.show();
        Retrofit retrofit = HttpUtil.getRetrofit("bean",GlobalConfig.serverUrl,10,10,10);
        Call<List<FeedRequire>> request = retrofit.create(HttpInterface.class).getFeedRequireListByWebsite(feedList.get(position).getIid());
        request.enqueue(new Callback<List<FeedRequire>>() {
            @SuppressLint("CheckResult")
            @Override
            public void onResponse(@NonNull Call<List<FeedRequire>> call, @NonNull Response<List<FeedRequire>> response) {
                if (response.isSuccessful()) {
                    //结束ui
                    loading.dismiss();
                    refreshLayout.finishRefresh(true);

                    assert response.body() != null;
                    feedRequireList.clear();
                    //feed更新到当前的时间流中。
                    feedRequireList.addAll(response.body());
                    feedRequireList.add(new FeedRequire("订阅名称","取一个名字吧",FeedRequire.SET_NAME,mName+"的"+feedList.get(position).getName()));

                    Feed feed = feedList.get(position);

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
                    new XPopup.Builder(FeedListActivity.this)
                            .asCustom(new RequireListPopupView(FeedListActivity.this,feedRequireList,"订阅参数填写","",help,feed,getSupportFragmentManager()))
                            .show();


                } else {
                    ALog.d("请求失败" + response.errorBody());
                    Toasty.error(UIUtil.getContext(),"请求失败" + response.errorBody(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<FeedRequire>> call, @NonNull Throwable t) {

                loading.dismiss();
                refreshLayout.finishRefresh(true);
            }
        });
    }

}
