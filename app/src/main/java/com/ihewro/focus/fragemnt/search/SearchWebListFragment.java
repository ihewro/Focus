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
import com.ihewro.focus.adapter.FeedCategoryRightAdapter;
import com.ihewro.focus.adapter.FeedListAdapter;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.Website;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchWebListFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;
    private FeedCategoryRightAdapter adapter;
    private List<Website> list = new ArrayList<>();
    private Activity activity;

    public SearchWebListFragment() {}

    //这种写法Google不推荐原因是，当activity recreate时候，碎片的参数不会重新调用。我们又不用保存碎片状态
    @SuppressLint("ValidFragment")
    public SearchWebListFragment(Activity activity) {
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


    private void initSearchAdapter() {
        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FeedCategoryRightAdapter(list,activity);
        adapter.bindToRecyclerView(recyclerView);
    }


    public void showLoading(){
        adapter.setNewData(null);
        adapter.setEmptyView(R.layout.simple_loading_view,recyclerView);
    }

    public void updateData(List<Website> list){
        this.list = list;
        if (list!=null && list.size() >0){
            adapter.setNewData(list);
        }else {
            adapter.setNewData(null);
            adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);
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
