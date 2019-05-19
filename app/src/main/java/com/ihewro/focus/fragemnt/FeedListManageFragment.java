package com.ihewro.focus.fragemnt;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.FeedFolderListAdapter;
import com.ihewro.focus.adapter.FeedListManageAdapter;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;
import com.ihewro.focus.bean.FeedItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedListManageFragment extends Fragment {

    private static final String FEED_FOLDER_ID = "FEED_FOLDER_ID";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;


    private FeedListManageAdapter adapter;
    private List<Feed> feedList = new ArrayList<>();
    private int mFeedFolderId = 1;

    /**
     * 新建一个新的碎片
     *
     * @return 返回实例
     */
    public static FeedListManageFragment newInstance(int feedFolderId) {
        FeedListManageFragment fragment = new FeedListManageFragment();
        Bundle args = new Bundle();
        args.putInt(FeedListManageFragment.FEED_FOLDER_ID, feedFolderId);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFeedFolderId = getArguments().getInt(FeedListManageFragment.FEED_FOLDER_ID);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_list_manage, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRecyclerView();
    }


    private void setRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        feedList = LitePal.where("feedfolderid = ?", String.valueOf(mFeedFolderId)).find(Feed.class);

        //获取未读数目
        for (int i = 0;i<feedList.size();i++){
            int num = LitePal.where("feedid = ?", String.valueOf(feedList.get(i).getId())).count(FeedItem.class);
            feedList.get(i).setUnreadNum(num);
        }

        adapter = new FeedListManageAdapter(feedList,getActivity());
        adapter.bindToRecyclerView(recyclerView);

        if (feedList.size()==0){
            adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshUI(EventMessage eventBusMessage) {
        if(EventMessage.feedOperation.contains(eventBusMessage.getType())){
            ALog.d("收到新的订阅添加，更新！" + eventBusMessage);
            setRecyclerView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void updateData(int id) {
        mFeedFolderId = id;
        setRecyclerView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
