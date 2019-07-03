package com.ihewro.focus.fragemnt.search;


import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.ihewro.focus.activity.MainActivity;
import com.ihewro.focus.activity.PostDetailActivity;
import com.ihewro.focus.adapter.FeedListAdapter;
import com.ihewro.focus.adapter.FeedSearchAdapter;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFeedItemListFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;
    private FeedSearchAdapter adapter;
    private List<FeedItem> searchResults = new ArrayList<>();
    private Activity activity;

    public SearchFeedItemListFragment() {}

    //这种写法Google不推荐原因是，当activity recreate时候，碎片的参数不会重新调用。我们又不用保存碎片状态
    @SuppressLint("ValidFragment")
    public SearchFeedItemListFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSearchAdapter();
    }




    /**
     * 初始化
     */
    private void initSearchAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FeedSearchAdapter(searchResults);
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);


        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FeedItem item = searchResults.get(position);
                ArrayList<Integer> list = new ArrayList<>();
                for (FeedItem feedItem : searchResults) {
                    list.add(feedItem.getId());
                }
                PostDetailActivity.activityStart(activity, position, searchResults, PostDetailActivity.ORIGIN_SEARCH);

            }
        });
    }

    public void showLoading(){
        adapter.setNewData(null);
        adapter.setEmptyView(R.layout.simple_loading_view,recyclerView);
    }

    public void updateData(List<FeedItem> list){
        if (adapter != null){
            this.searchResults = list;
            if (list!=null && list.size() >0){
                adapter.setNewData(searchResults);
            }else {
                adapter.setNewData(null);
                adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);
            }
        }
    }

    public BaseQuickAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
