package com.ihewro.focus.fragemnt;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.ALog;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
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
        initListener();

    }


    private void initListener(){
        // 拖拽排序事件
        OnItemDragListener onItemDragListener = new OnItemDragListener() {
            int start = 0;
            int end = 0;
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos){
                start = pos;
                ALog.d("开始" + pos);
            }
            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
                ALog.d("开始" + from + " || 目标" + to);

            }
            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
                //修改表情中表情包权值，移动的表情包权值 = 移动后的位置
                ALog.d("结束" + pos);
                end = pos;//结果的位置
                if (end!=0 && end!=feedList.size()-1){
                    feedList.get(end).setOrderValue((feedList.get(end-1).getOrderValue() + feedList.get(end+1).getOrderValue())*1.0/2);
                }else {
                    if (end == 0){
                        feedList.get(end).setOrderValue(feedList.get(1).getOrderValue()*1.0/2);
                    }else {
                        feedList.get(end).setOrderValue(feedList.get(end -1).getOrderValue() + 1);
                    }
                }
                feedList.get(end).save();//保存到数据库

                EventBus.getDefault().post(new EventMessage(EventMessage.ORDER_FOLDER));
            }

        };
        // 开启拖拽
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.enableDragItem(itemTouchHelper, R.id.move_logo, true);
        adapter.setOnItemDragListener(onItemDragListener);
    }

    private void setRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        feedList = LitePal.where("feedfolderid = ?", String.valueOf(mFeedFolderId)).find(Feed.class);

        //更新顺序
        for (int i = 0; i < feedList.size() ; i++) {
            feedList.get(i).setOrderValue(i+1);
            feedList.get(i).save();
        }


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
