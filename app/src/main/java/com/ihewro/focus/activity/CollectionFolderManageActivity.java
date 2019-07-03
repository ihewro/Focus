package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.blankj.ALog;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.CollectionFolderListAdapter;
import com.ihewro.focus.adapter.CollectionFolderManageAdapter;
import com.ihewro.focus.adapter.FeedListManageAdapter;
import com.ihewro.focus.bean.CollectionFolder;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.callback.OperationCallback;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CollectionFolderManageActivity extends BackActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<CollectionFolder> list = new ArrayList<>();
    CollectionFolderManageAdapter adapter;

    public static void activityStart(Activity activity) {
        Intent intent = new Intent(activity, CollectionFolderManageActivity.class);
        activity.startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_folder_manage);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initRecyclerView();
    }


    private void initRecyclerView(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        list = LitePal.findAll(CollectionFolder.class);


        adapter = new CollectionFolderManageAdapter(list,this);
        adapter.bindToRecyclerView(recyclerView);



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
                if (end!=0 && end!=list.size()-1){
                    list.get(end).setOrderValue((list.get(end-1).getOrderValue() + list.get(end+1).getOrderValue())*1.0/2);
                }else {
                    if (end == 0){
                        list.get(end).setOrderValue(list.get(1).getOrderValue()*1.0/2);
                    }else {
                        list.get(end).setOrderValue(list.get(end -1).getOrderValue() + 1);
                    }
                }
                list.get(end).save();//保存到数据库

                EventBus.getDefault().post(new EventMessage(EventMessage.COLLECTION_FOLDER_OPERATION));
            }

        };

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.enableDragItem(itemTouchHelper, R.id.move_logo, true);
        adapter.setOnItemDragListener(onItemDragListener);


        if (list.size()==0){
            adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collect_folder, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.add_folder:
                //弹窗

                CollectionFolder.addNewFolder(this, new OperationCallback() {
                    @Override
                    public void run(Object o) {
                        list.add((CollectionFolder) o);
                        if (adapter!=null){
                            adapter.notifyItemInserted(list.size());
                            //更新收藏页面的tablayout
                            EventBus.getDefault().post(new EventMessage(EventMessage.COLLECTION_FOLDER_OPERATION));
                            adapter.notifyItemChanged(list.size()-1);
                        }
                    }
                });

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
