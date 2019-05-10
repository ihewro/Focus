package com.ihewro.focus.fragemnt;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.PostDetailActivity;
import com.ihewro.focus.adapter.UserFeedPostsVerticalAdapter;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.callback.RequestFeedItemListCallback;
import com.ihewro.focus.decoration.SuspensionDecoration;
import com.ihewro.focus.task.RequestFeedListDataTask;
import com.ihewro.focus.view.FilterPopupView;
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
import es.dmoral.toasty.Toasty;

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

    private View view;
    private boolean isFirstOpen = true;//首次打开
    ArrayList<String> feedIdList = new ArrayList<>();

    private int orderChoice = FilterPopupView.ORDER_BY_NEW;
    private int filterChoice = FilterPopupView.SHOW_ALL;

    @SuppressLint("ValidFragment")
    public UserFeedUpdateContentFragment(View view) {
        this.view = view;
    }

    public UserFeedUpdateContentFragment() {
    }

    /**
     * 新建一个新的碎片
     *
     * @return 返回实例
     */
    public static UserFeedUpdateContentFragment newInstance(ArrayList<String> feedIdList,View view) {
        UserFeedUpdateContentFragment fragment = new UserFeedUpdateContentFragment(view);
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
        //使上拉加载具有弹性效果
        refreshLayout.setEnableAutoLoadMore(false);
        //禁止越界拖动（1.0.4以上版本）
        refreshLayout.setEnableOverScrollDrag(false);
        //关闭越界回弹功能
        refreshLayout.setEnableOverScrollBounce(false);
        // 这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的：
        refreshLayout.setEnableAutoLoadMore(false);
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
        RequestFeedListDataTask task = new RequestFeedListDataTask(orderChoice,filterChoice,getActivity(),view,isFirstOpen,feedList, new RequestFeedItemListCallback() {
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
        if (Objects.equals(eventBusMessage.getType(), EventMessage.MAKE_READ_STATUS_BY_INDEX)) {
            //更新已读标志
            int indexInList = eventBusMessage.getIndex();
            if (indexInList!=-1){
                eList.get(indexInList).setRead(true);
                adapter.notifyItemChanged(indexInList);
            }
        }else if (Objects.equals(eventBusMessage.getType(), EventMessage.MAKE_STAR_STATUS_BY_INDEX)){
            //更新收藏状态
            int indexInList = eventBusMessage.getIndex();
            boolean flag = eventBusMessage.isFlag();
            eList.get(indexInList).setFavorite(flag);
            adapter.notifyItemChanged(indexInList);
        }
    }

    public ArrayList<String> getFeedIdList() {
        return feedIdList;
    }

    public void updateData(ArrayList<String> feedIdList, int oderChoice, int filterChoice) {
        this.feedIdList = feedIdList;
        this.isFirstOpen = true;
        this.orderChoice = oderChoice;
        this.filterChoice = filterChoice;
        refreshLayout.autoRefresh();
    }
}
