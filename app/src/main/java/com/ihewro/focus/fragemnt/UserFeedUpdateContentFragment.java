package com.ihewro.focus.fragemnt;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.UserFeedPostsVerticalAdapter;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.decoration.DividerItemDecoration;
import com.ihewro.focus.decoration.SuspensionDecoration;
import com.ihewro.focus.http.HttpInterface;
import com.ihewro.focus.http.HttpUtil;
import com.ihewro.focus.util.FeedParser;
import com.ihewro.focus.util.UIUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 用户的最新订阅信息文章列表的碎片
 */
public class UserFeedUpdateContentFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private SuspensionDecoration mDecoration;
    List<FeedItem> eList = new ArrayList<FeedItem>();

    UserFeedPostsVerticalAdapter adapter;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;


    public UserFeedUpdateContentFragment() {
    }

    /**
     * 新建一个新的碎片
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return 返回实例
     */
    public static UserFeedUpdateContentFragment newInstance(String param1, String param2) {
        UserFeedUpdateContentFragment fragment = new UserFeedUpdateContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_feed_update_content, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEmptyView();

        bindListener();

        refreshLayout.autoRefresh();
    }

    public void initEmptyView() {
        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new UserFeedPostsVerticalAdapter(eList, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL_LIST));

        recyclerView.addItemDecoration(mDecoration = new SuspensionDecoration(getActivity(), eList));

    }


    /**
     * 获取用户的所有订阅的文章
     */
    public void requestAllData(){

    }


    public void requestData() {
        Retrofit retrofit = HttpUtil.getRetrofit("String", "https://rsshub.app/douyin/user/93610979153/", 100, 100, 100);
        HttpInterface request = retrofit.create(HttpInterface.class);
        Call<String> call = request.getRSSData();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {

                    assert response.body() != null;
                    Feed feed = FeedParser.parseStr2Feed(response.body());
                    ALog.dTag("feed233", feed);
                    eList.clear();
                    //feed更新到当前的时间流中。
                    assert feed != null;
                    eList.addAll(feed.getFeedItemList());
                    eList = new ArrayList<>(new LinkedHashSet<>(eList));
                    adapter.setNewData(eList);
                    Toasty.success(UIUtil.getContext(),"请求成功", Toast.LENGTH_SHORT);
                } else {
                    ALog.d("请求失败" + response.errorBody());
                    Toasty.info(UIUtil.getContext(),"请求失败" + response.errorBody(), Toast.LENGTH_SHORT);

                }
                refreshLayout.finishRefresh(true);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ALog.d("请求失败2" + t.toString());
                Toasty.info(UIUtil.getContext(),"请求失败2" + t.toString(), Toast.LENGTH_SHORT);
                refreshLayout.finishRefresh(true);
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
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
