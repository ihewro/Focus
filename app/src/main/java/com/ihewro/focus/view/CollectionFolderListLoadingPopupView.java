package com.ihewro.focus.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihewro.focus.R;
import com.ihewro.focus.adapter.CollectionFolderListAdapter;
import com.ihewro.focus.bean.Collection;
import com.ihewro.focus.bean.CollectionAndFolderRelation;
import com.ihewro.focus.bean.CollectionFolder;
import com.ihewro.focus.callback.OperationCallback;
import com.ihewro.focus.callback.UICallback;
import com.ihewro.focus.util.UIUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;

import org.litepal.LitePal;
import org.litepal.exceptions.LitePalSupportException;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/06/27
 *     desc   : 收藏列表的选择弹窗
 *     version: 1.0
 * </pre>
 */
public class CollectionFolderListLoadingPopupView extends BottomPopupView {



    private CollectionFolderListAdapter adapter;
    private List<CollectionFolder> collectionFolderList = new ArrayList<>();
    private String info;
    private Collection collection;
    private UICallback uiCallback;

    BasePopupView basePopupView;

    TextView listTitle;
    ImageView actionAdd;
    ImageView actionClose;
    RecyclerView recyclerView;
    TextView textInfo;
    Button collect;


    private Activity activity;





    public CollectionFolderListLoadingPopupView(Activity context, Collection collection, UICallback callback) {
        super(context);
        this.activity = context;
        this.collection = collection;
        this.uiCallback = callback;
    }


    public CollectionFolderListLoadingPopupView(@NonNull Context context) {
        super(context);
    }



    @Override
    protected void onCreate() {
        super.onCreate();


        initView();

        initRecycler();


    }




    private void initRecycler() {
        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);




        adapter = new CollectionFolderListAdapter(collectionFolderList,null);
        adapter.bindToRecyclerView(recyclerView);

        adapter.setEmptyView(R.layout.simple_loading_view,recyclerView);


        //查询数据库\
        new Thread(new Runnable() {
            @Override
            public void run() {

                collectionFolderList = LitePal.findAll(CollectionFolder.class);
                //TODO: 给文件夹的isSelect变量赋值
                final List<Integer> folderIds = new ArrayList<>();
                List<Collection> tempCollections = LitePal.where("url = ?",collection.getUrl()).find(Collection.class);

                if (tempCollections.size() > 0){
                    Collection tempCollection = tempCollections.get(0);//如果数据库已经存在该收藏，直接使用数据库的
                    collection.setId(tempCollection.getId());
                    List<CollectionAndFolderRelation> collectionAndFolderRelations = LitePal.where("collectionid = ?", String.valueOf(collection.getId())).find(CollectionAndFolderRelation.class);
                    for (CollectionAndFolderRelation collectionAndFolderRelation:collectionAndFolderRelations){
                        folderIds.add(collectionAndFolderRelation.getCollectionFolderId());
                    }
                }



                UIUtil.runOnUiThread(activity, new Runnable() {
                    @Override
                    public void run() {
                        CollectionFolderListLoadingPopupView.this.dismiss();

                        new XPopup.Builder(activity)
                                .asCustom(new CollectionFolderListPopupView(activity,collection,uiCallback,collectionFolderList,folderIds))
                                .show();
                    }
                });
            }
        }).start();



    }


    private void initView() {
        listTitle = findViewById(R.id.title);
        actionAdd = findViewById(R.id.action_add);
        actionClose = findViewById(R.id.action_close);
        recyclerView = findViewById(R.id.recycler_view);
        textInfo = findViewById(R.id.subtitle);
        collect = findViewById(R.id.btn_finish);


    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.componenet_collection_popup;
    }


    protected int getMaxHeight() {
        return (int) (XPopupUtils.getWindowHeight(getContext())*.85f);
    }


    @Override
    public int getMinimumHeight() {
        return (int) (XPopupUtils.getWindowHeight(getContext())*.65f);
    }


    public void setBasePopupView(BasePopupView basePopupView) {
        this.basePopupView = basePopupView;
    }
}
