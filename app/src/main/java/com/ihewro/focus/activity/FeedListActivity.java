package com.ihewro.focus.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.FeedListAdapter;
import com.ihewro.focus.adapter.FeedRequireListAdapter;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedRequire;
import com.ihewro.focus.http.HttpInterface;
import com.ihewro.focus.http.HttpUtil;
import com.ihewro.focus.util.Constants;
import com.ihewro.focus.util.UIUtil;
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

public class FeedListActivity extends AppCompatActivity {


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

    public static void activityStart(Activity activity, String websiteId) {
        Intent intent = new Intent(activity, FeedListActivity.class);
        intent.putExtra(Constants.KEY_STRING_WEBSITE_ID, websiteId);
        activity.startActivity(intent);
    }


    String mId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mId = intent.getStringExtra(Constants.KEY_STRING_WEBSITE_ID);
        initView();
        bindListener();
        refreshLayout.autoRefresh();
    }


    public void initView(){
        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        feedListAdapter = new FeedListAdapter(feedList);
        feedListAdapter.bindToRecyclerView(recyclerView);
        feedListAdapter.setEmptyView(R.layout.simple_empty_view);
    }

    public void requestData(){
        Retrofit retrofit = HttpUtil.getRetrofit("bean", GlobalConfig.serverUrl,10,10,10);
        Call<List<Feed>> request = retrofit.create(HttpInterface.class).getFeedListByWebsite(mId);

        request.enqueue(new Callback<List<Feed>>() {
            @Override
            public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                if (response.isSuccessful()){
                    feedList.clear();
                    feedList.addAll(response.body());
                    feedListAdapter.setNewData(feedList);
                    Toasty.success(UIUtil.getContext(),"请求成功", Toast.LENGTH_SHORT);
                }else {
                    Toasty.error(UIUtil.getContext(),"请求失败2" + response.errorBody(), Toast.LENGTH_SHORT);
                }
                refreshLayout.finishRefresh(true);
            }

            @SuppressLint("CheckResult")
            @Override
            public void onFailure(Call<List<Feed>> call, Throwable t) {
                Toasty.error(UIUtil.getContext(),"请求失败2" + t.toString(), Toast.LENGTH_SHORT);
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

                            assert response.body() != null;
                            feedRequireList.clear();
                            //feed更新到当前的时间流中。
                            feedRequireList.addAll(response.body());
                            //用一个弹窗显示参数列表
                            final FeedRequireListAdapter feedRequireListAdapter = new FeedRequireListAdapter(feedRequireList);
                            MaterialDialog requireDialog = new MaterialDialog.Builder(FeedListActivity.this)
                                    .title("填写参数")
                                    .adapter(feedRequireListAdapter,new LinearLayoutManager(FeedListActivity.this))
                                    .positiveText("订阅")
                                    .negativeText("取消")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            Feed feed = feedList.get(position);
                                            String url = feed.getUrl();
                                            for (int i =0 ; i < feedRequireList.size();i++){
                                                EditText editText = (EditText) feedRequireListAdapter.getViewByPosition(dialog.getRecyclerView(),i,R.id.input);
                                                char split;
                                                if (i == 0){
                                                    split = '?';
                                                }else {
                                                    split = '&';
                                                }
//                                                url += split+ feedRequireList.get(i).getName() +"="+ editText.getText().toString();
                                                url += url + "/" + editText.getText().toString();

                                            }
                                            feed.setUrl(url);
                                            feed.save();//添加新的订阅，存储到数据库中

                                        }
                                    })
                                    .show();


                            Toasty.success(UIUtil.getContext(),"请求成功", Toast.LENGTH_SHORT);
                        } else {
                            ALog.d("请求失败" + response.errorBody());
                            Toasty.error(UIUtil.getContext(),"请求失败" + response.errorBody(), Toast.LENGTH_SHORT);

                        }
                        loading.dismiss();
                        refreshLayout.finishRefresh(true);
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<FeedRequire>> call, @NonNull Throwable t) {

                        loading.dismiss();
                        refreshLayout.finishRefresh(true);
                    }
                });

            }
        });
    }
}
