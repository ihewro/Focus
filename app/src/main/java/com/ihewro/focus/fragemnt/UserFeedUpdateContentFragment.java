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

import com.ihewro.focus.R;
import com.ihewro.focus.adapter.UserFeedPostsVerticalAdapter;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.callback.RequestFeedItemListCallback;
import com.ihewro.focus.decoration.SuspensionDecoration;
import com.ihewro.focus.task.RequestFeedListDataTask;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 用户的最新订阅信息文章列表的碎片
 */
public class UserFeedUpdateContentFragment extends Fragment {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private SuspensionDecoration mDecoration;
    List<FeedItem> eList = new ArrayList<FeedItem>();
    public static final String FEED_LIST_ID = "FEED_LIST_ID";

    UserFeedPostsVerticalAdapter adapter;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;

    private boolean isFirstOpen = true;//首次打开
    ArrayList<String> feedIdList = new ArrayList<>();

    public UserFeedUpdateContentFragment() {
    }

    /**
     * 新建一个新的碎片
     *
     * @return 返回实例
     */
    public static UserFeedUpdateContentFragment newInstance(ArrayList<String> feedIdList) {
        UserFeedUpdateContentFragment fragment = new UserFeedUpdateContentFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(UserFeedUpdateContentFragment.FEED_LIST_ID,feedIdList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            feedIdList = getArguments().getStringArrayList(UserFeedUpdateContentFragment.FEED_LIST_ID);
        }
        EventBus.getDefault().register(this);
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

        refreshLayout.setEnableLoadMore(false);//禁止加载更多
    }

    public void initEmptyView() {
        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new UserFeedPostsVerticalAdapter(eList, getActivity());
        recyclerView.setAdapter(adapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.addItemDecoration(mDecoration = new SuspensionDecoration(getActivity(), eList));
    }

    /**
     * 获取用户的所有订阅的文章
     */
    public void requestAllData(){
        List<Feed> feedList = new ArrayList<>();
        if (feedIdList.size() >0){
            for (int i = 0; i < feedIdList.size(); i++) {
                feedList.add(LitePal.find(Feed.class,Integer.parseInt(feedIdList.get(i))));
            }
        }else {//为空表示显示所有的feedId
            feedList = LitePal.findAll(Feed.class);
        }
        RequestFeedListDataTask task = new RequestFeedListDataTask(getActivity(),isFirstOpen,feedList, new RequestFeedItemListCallback() {
            @Override
            public void onBegin() {
                refreshLayout.finishRefresh(true);
            }

            @Override
            public void onFinish(List<FeedItem> feedList) {
                eList.clear();
                eList.addAll(feedList);
                adapter.setNewData(eList);
                isFirstOpen = false;
            }
        });
        task.run();
    }

    public void bindListener(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestAllData();
            }
        });


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshUI(EventMessage eventBusMessage) {
        if (Objects.equals(eventBusMessage.getType(), EventMessage.MAKE_READ_STATUS)) {
            //更新已读标志
            int indexInList = eventBusMessage.getIndex();
            eList.get(indexInList).setRead(true);
            adapter.notifyItemChanged(indexInList);
        }
    }

    public void updateData(ArrayList<String> feedIdList) {
        this.feedIdList = feedIdList;
        this.isFirstOpen = true;
        refreshLayout.autoRefresh();
    }
}
