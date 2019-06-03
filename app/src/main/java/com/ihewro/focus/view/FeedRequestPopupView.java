package com.ihewro.focus.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.FeedListAdapter;
import com.ihewro.focus.adapter.FeedRequestAdapter;
import com.ihewro.focus.adapter.FeedRequireListAdapter;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedRequest;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.http.HttpInterface;
import com.ihewro.focus.http.HttpUtil;
import com.ihewro.focus.util.UIUtil;
import com.lxj.xpopup.core.BottomPopupView;

import org.litepal.LitePal;

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
 *     time   : 2019/06/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FeedRequestPopupView extends BottomPopupView{
    public FeedRequestPopupView(@NonNull Context context) {
        super(context);
    }

    private FeedRequestAdapter adapter;
    private List<FeedRequest> feedRequestList = new ArrayList<>();
    private String title;
    private String info;
    private Help help;


    TextView listTitle;
    ImageView actionHelp;
    ImageView actionClose;
    RecyclerView recyclerView;
    TextView textInfo;

    private int feedid;



    public FeedRequestPopupView(@NonNull Activity context, String title, String info, Help help,int feedid) {
        super(context);
        this.title = title;
        this.info = info;
        this.help = help;
        this.feedid = feedid;
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
        feedRequestList = LitePal.where("feedid = ?", String.valueOf(feedid)).find(FeedRequest.class);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FeedRequestAdapter( (Activity) getContext(),feedRequestList);
        View view = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.component_request_header, (ViewGroup) recyclerView.getParent(), false);
        adapter.addHeaderView(view);
        adapter.bindToRecyclerView(recyclerView);

        if (feedRequestList.size() == 0){
            adapter.setNewData(null);
            adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);
        }

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
            actionHelp.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //打开帮助页面。是一个网页
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



}
