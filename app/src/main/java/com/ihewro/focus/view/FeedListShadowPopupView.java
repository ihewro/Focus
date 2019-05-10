package com.ihewro.focus.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ihewro.focus.R;
import com.ihewro.focus.adapter.FeedFolderListAdapter;
import com.ihewro.focus.adapter.FeedFolderListMainAdapter;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;
import com.lxj.xpopup.impl.PartShadowPopupView;

import org.litepal.LitePal;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/09
 *     desc   : 顶部的用来选择切换到哪个文件夹的弹窗
 *     version: 1.0
 * </pre>
 */
public class FeedListShadowPopupView extends PartShadowPopupView {
    public FeedListShadowPopupView(@NonNull Context context) {
        super(context);
    }
    @Override
    protected int getImplLayoutId() {
        return R.layout.component_feed_list_popup;
    }

    private RecyclerView recyclerView;
    private FeedFolderListMainAdapter adapter;
    private List<FeedFolder> feedFolders;

    @Override
    protected void onCreate() {
        super.onCreate();
        recyclerView = findViewById(R.id.recycler_view);
        //加载list
        feedFolders = LitePal.findAll(FeedFolder.class);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FeedFolderListMainAdapter(feedFolders);
        adapter.bindToRecyclerView(recyclerView);

    }

    @Override
    protected void onShow() {
        super.onShow();
        Log.e("tag","CustomPartShadowPopupView onShow");
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        Log.e("tag","CustomPartShadowPopupView onDismiss");
    }


    public FeedFolderListMainAdapter getAdapter() {
        return adapter;
    }

    public List<FeedFolder> getFeedFolders() {
        return feedFolders;
    }
}
