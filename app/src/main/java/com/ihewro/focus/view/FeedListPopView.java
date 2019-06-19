package com.ihewro.focus.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.FeedListActivity;
import com.ihewro.focus.adapter.FeedListAdapter;
import com.ihewro.focus.adapter.RequireListAdapter;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedRequire;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.http.HttpInterface;
import com.ihewro.focus.http.HttpUtil;
import com.ihewro.focus.util.UIUtil;
import com.lxj.xpopup.core.BottomPopupView;

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
 *     time   : 2019/05/29
 *     desc   : 订阅市场页面用来显示订阅列表的，取代用activity来显示
 *     version: 1.0
 * </pre>
 */
public class FeedListPopView extends BottomPopupView {

    private FeedListAdapter adapter;
    private List<Feed> feedList = new ArrayList<>();
    private String title;
    private String info;
    private Help help;


    TextView listTitle;
    ImageView actionHelp;
    ImageView actionClose;
    RecyclerView recyclerView;
    TextView textInfo;

    FragmentManager fragmentManager;

    public FeedListPopView(@NonNull Context context) {
        super(context);
    }

    public FeedListPopView(@NonNull FragmentManager fragmentManager,Activity context, String title, String info, Help help) {
        super(context);
        this.fragmentManager = fragmentManager;
        this.title = title;
        this.info = info;
        this.help = help;
    }



    @Override
    protected int getImplLayoutId() {
        return R.layout.componenet_opertaion_popup;
    }


    @Override
    protected void onCreate() {
        super.onCreate();


        initView();

        initRecycler();

        initListener();

        requestData();
    }

    private void initListener() {
        actionClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initRecycler() {
        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FeedListAdapter(feedList, (Activity) getContext(),title);
        adapter.bindToRecyclerView(recyclerView);
    }


    private void initView() {
        listTitle = findViewById(R.id.title);
        actionHelp = findViewById(R.id.action_help);
        actionClose = findViewById(R.id.action_close);
        recyclerView = findViewById(R.id.recycler_view);
        textInfo = findViewById(R.id.subtitle);

        if (!help.isHelp()){
            actionHelp.setVisibility(View.GONE);
        }else {
            actionHelp.setVisibility(View.VISIBLE);
            actionHelp.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //打开帮助页面。是一个网页
                    int accentColor = ContextCompat.getColor(getContext(), R.color.accent);
                    HelpDialog.create(false, accentColor, help.getContent()).show(fragmentManager, "changelog");
                }
            });
        }

        listTitle.setText(title);

        if (!info.trim().equals("")){
            textInfo.setText(info.trim());
        }else {
            textInfo.setVisibility(View.GONE);
        }


    }



    /**
     * 请求一个网站的可订阅列表
     */
    public void requestData(){

        //显示加载界面
        adapter.setNewData(null);
        adapter.setEmptyView(R.layout.simple_loading_view,recyclerView);

        Retrofit retrofit = HttpUtil.getRetrofit("bean", GlobalConfig.serverUrl,10,10,10);
        ALog.d("名称为" + title);
        Call<List<Feed>> request = retrofit.create(HttpInterface.class).getFeedListByWebsite(title);

        request.enqueue(new Callback<List<Feed>>() {
            @Override
            public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                if (response.isSuccessful()){
                    feedList.clear();
                    feedList.addAll(response.body());

                    adapter.setNewData(feedList);

                    if (feedList.size()==0){
                        adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);
                    }
                    Toasty.success(UIUtil.getContext(),"请求成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toasty.error(UIUtil.getContext(),"请求失败2" + response.errorBody(), Toast.LENGTH_SHORT).show();
                }
            }

            @SuppressLint("CheckResult")
            @Override
            public void onFailure(Call<List<Feed>> call, Throwable t) {
                Toasty.error(UIUtil.getContext(),"请求失败2" + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}

