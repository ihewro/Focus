package com.ihewro.focus.fragemnt;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.FeedFolderListAdapter;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedFolder;

import org.greenrobot.eventbus.EventBus;
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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createRecyclerView();
        initListener();
    }


    private void initListener(){
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                EventBus.getDefault().post(new EventMessage(EventMessage.SHOW_FEED_LIST_MANAGE,feedFolders.get(position).getId()+""));
            }
        });

        //TODO：拖拽排序事件


        //TODO:长按修改名称


        //TODO:右滑退订
    }

    private void createRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        feedFolders = LitePal.findAll(FeedFolder.class);
        adapter = new FeedFolderListAdapter(feedFolders);
        adapter.bindToRecyclerView(recyclerView);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
