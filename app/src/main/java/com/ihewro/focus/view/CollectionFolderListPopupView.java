package com.ihewro.focus.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.CollectionFolderListAdapter;
import com.ihewro.focus.bean.Collection;
import com.ihewro.focus.bean.CollectionAndFolderRelation;
import com.ihewro.focus.bean.CollectionFolder;
import com.ihewro.focus.callback.OperationCallback;
import com.ihewro.focus.callback.UICallback;
import com.ihewro.focus.util.UIUtil;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;

import org.litepal.LitePal;
import org.litepal.exceptions.LitePalSupportException;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/06/27
 *     desc   : 收藏列表的选择弹窗
 *     version: 1.0
 * </pre>
 */
public class CollectionFolderListPopupView extends BottomPopupView {



    private CollectionFolderListAdapter adapter;
    private List<CollectionFolder> collectionFolderList = new ArrayList<>();
    private String info;
    private Collection collection;
    private UICallback uiCallback;


    TextView listTitle;
    ImageView actionAdd;
    ImageView actionClose;
    RecyclerView recyclerView;
    TextView textInfo;
    Button collect;







    public CollectionFolderListPopupView(Activity context, Collection collection, UICallback callback) {
        super(context);
        this.collection = collection;
        this.uiCallback = callback;
    }


    public CollectionFolderListPopupView(@NonNull Context context) {
        super(context);
    }



    @Override
    protected void onCreate() {
        super.onCreate();


        initView();

        initRecycler();

        initListener();

    }



    private void initListener() {
        actionClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        collect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //将该内容添加到当前列表选择的文件夹中
                try {
                    collection.save();
//                    Toasty.success(getContext(),"第一次收藏该内容").show();
                }catch (LitePalSupportException e){
//                    Toasty.info(getContext(),"已存在同样内容").show();
                }

                //TODO:保存收藏
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //已经存在的关联
//                        List<CollectionAndFolderRelation> list = LitePal.where("collectionid = ?", String.valueOf(collection.getId())).find(CollectionAndFolderRelation.class);

                        //先删掉旧的关联，再新建新的关联
                        LitePal.deleteAll(CollectionAndFolderRelation.class,"collectionid = ?", String.valueOf(collection.getId()));

                        final List<Integer> folderIds = adapter.getSelectFolderIds();
                        for (int i = 0;i<folderIds.size();i++){
                            new CollectionAndFolderRelation(collection.getId(),folderIds.get(i)).save();
                        }



                        UIUtil.runOnUiThread((Activity) getContext(), new Runnable() {
                            @Override
                            public void run() {

                                //如果size = 0说明取消收藏了，否则说明仍然是收藏
                                uiCallback.doUIWithIds(folderIds);


                                if (folderIds.size()>0){
                                    uiCallback.doUIWithFlag(true);
                                }else {
                                    uiCallback.doUIWithFlag(false);
                                }
                            }
                        });

                    }
                }).start();

                dismiss();
            }
        });



        actionAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                CollectionFolder.addNewFolder(getContext(), new OperationCallback() {
                    @Override
                    public void run(Object o) {
                        collectionFolderList.add((CollectionFolder) o);
                        if (adapter!=null){
                            adapter.notifyItemInserted(collectionFolderList.size());
                        }
                    }
                });
            }
        });




    }

    private void initRecycler() {
        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //查询数据库
        collectionFolderList = LitePal.findAll(CollectionFolder.class);
        //TODO: 给文件夹的isSelect变量赋值
        List<Integer> folderIds = new ArrayList<>();
        List<Collection> tempCollections = LitePal.where("url = ?",collection.getUrl()).find(Collection.class);

        if (tempCollections.size() > 0){
            Collection tempCollection = tempCollections.get(0);//如果数据库已经存在该收藏，直接使用数据库的
            collection.setId(tempCollection.getId());
            List<CollectionAndFolderRelation> collectionAndFolderRelations = LitePal.where("collectionid = ?", String.valueOf(collection.getId())).find(CollectionAndFolderRelation.class);
            for (CollectionAndFolderRelation collectionAndFolderRelation:collectionAndFolderRelations){
                folderIds.add(collectionAndFolderRelation.getCollectionFolderId());
            }
        }

        adapter = new CollectionFolderListAdapter(collectionFolderList,folderIds);
        adapter.bindToRecyclerView(recyclerView);

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


//    protected int getMaxHeight() {
//        return (int) (XPopupUtils.getWindowHeight(getContext())*.85f);
//    }


    @Override
    public int getMinimumHeight() {
        return (int) (XPopupUtils.getWindowHeight(getContext())*.65f);
    }



}
