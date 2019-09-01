package com.ihewro.focus.fragemnt;


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

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.CollectionListAdapter;
import com.ihewro.focus.adapter.FeedListAdapter;
import com.ihewro.focus.bean.Collection;
import com.ihewro.focus.bean.CollectionAndFolderRelation;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.decoration.SuspensionDecoration;
import com.ihewro.focus.util.UIUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindCallback;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionListFragment extends Fragment {

    private List<Collection> collectionList = new ArrayList<>();

    private static final String COLLECTION_FOLDER_ID = "COLLECTION_FOLDER_ID";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;

    public static CollectionListFragment newInstance(int id,Activity activity) {

        Bundle args = new Bundle();
        args.putInt(COLLECTION_FOLDER_ID, id);

        CollectionListFragment fragment = new CollectionListFragment(activity);
        fragment.setArguments(args);
        return fragment;
    }


    private int collectionFolderId;
    private Activity activity;
    private CollectionListAdapter adapter;

    public CollectionListFragment() {
    }

    @SuppressLint("ValidFragment")
    public CollectionListFragment(Activity activity) {
        this.activity = activity;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            collectionFolderId = getArguments().getInt(COLLECTION_FOLDER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_folder, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createRecyclerView();
        initListener();

        initData();
    }

    private void initListener() {
    }

    private void createRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CollectionListAdapter(collectionFolderId,collectionList,activity);
        adapter.bindToRecyclerView(recyclerView);

        adapter.setNewData(null);
        adapter.setEmptyView(R.layout.simple_loading_view,recyclerView);
    }


    private void initData(){
        //查询数据库

        new Thread(new Runnable() {
            @Override
            public void run() {
                collectionList.clear();
                final List<CollectionAndFolderRelation> list = LitePal.where("collectionfolderid = ?", String.valueOf(collectionFolderId)).find(CollectionAndFolderRelation.class);

                for (CollectionAndFolderRelation collectionAndFolderRelation : list) {
                    collectionList.add(LitePal.find(Collection.class, collectionAndFolderRelation.getCollectionId()));
                }

                if (collectionList.size() > 1){
                    Collections.sort(collectionList, new Comparator<Collection>() {
                        @Override
                        public int compare(Collection o1, Collection o2) {
                            if (o1.getTime() > o2.getTime()){
                                return -1;
                            }else {
                                return 1;
                            }
                        }
                    });
                }

                //切换到主线程更新界面
                UIUtil.runOnUiThread(activity, new Runnable() {
                    @Override
                    public void run() {
                        if (recyclerView!=null){
                            if (collectionList.size() == 0){
                                adapter.setEmptyView(R.layout.collction_empty_view,recyclerView);
                            }else {
                                adapter.setNewData(collectionList);
                                recyclerView.addItemDecoration(new SuspensionDecoration(getActivity(), collectionList));
                            }
                        }
                    }
                });
            }
        }).start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
