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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.FeedFolderListAdapter;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedFolder;
import com.ihewro.focus.bean.FeedItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFolderListManageFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;

    FeedFolderListAdapter adapter;
    private List<FeedFolder> feedFolders;
    /**
     * 新建一个新的碎片
     *
     * @return 返回实例
     */
    public static FeedFolderListManageFragment newInstance() {
        FeedFolderListManageFragment fragment = new FeedFolderListManageFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_folder_list_manage, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createRecyclerView();
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
                if (end!=0 && end!=feedFolders.size()-1){
                    feedFolders.get(end).setOrderValue((feedFolders.get(end-1).getOrderValue() + feedFolders.get(end+1).getOrderValue())*1.0/2);
                }else {
                    if (end == 0){
                        feedFolders.get(end).setOrderValue(feedFolders.get(1).getOrderValue()*1.0/2);
                    }else {
                        feedFolders.get(end).setOrderValue(feedFolders.get(end -1).getOrderValue() + 1);
                    }
                }
                feedFolders.get(end).save();//保存到数据库

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

    private void createRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        feedFolders = LitePal.order("ordervalue").find(FeedFolder.class);
        //更新顺序
        for (int i = 0; i < feedFolders.size() ; i++) {
            feedFolders.get(i).setOrderValue(i+1);
            feedFolders.get(i).save();
        }
        //更新
        adapter = new FeedFolderListAdapter(feedFolders,getActivity());
        adapter.bindToRecyclerView(recyclerView);
        if (feedFolders.size() == 0){
            adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshUI(EventMessage eventBusMessage) {
        if(EventMessage.feedFolderOperation.contains(eventBusMessage.getType())){
            ALog.d("收到新的订阅添加，更新！" + eventBusMessage);
            createRecyclerView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
