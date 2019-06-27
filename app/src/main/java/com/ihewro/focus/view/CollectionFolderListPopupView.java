package com.ihewro.focus.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
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
import com.ihewro.focus.bean.CollectionFolder;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedFolder;
import com.ihewro.focus.bean.Help;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;

import org.greenrobot.eventbus.EventBus;
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
    private List<CollectionFolder> collectionList = new ArrayList<>();
    private String info;
    private Collection collection;

    TextView listTitle;
    ImageView actionAdd;
    ImageView actionClose;
    RecyclerView recyclerView;
    TextView textInfo;
    Button collect;




    public CollectionFolderListPopupView(Activity context, Collection collection) {
        super(context);
        this.info = info;
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

            }
        });



        actionAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //新建收藏单弹窗
                new MaterialDialog.Builder(getContext())
                        .title("输入新增的收藏分类名称：")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog2, CharSequence input) {
                                //TODO:不能重命名
                                String name = dialog2.getInputEditText().getText().toString().trim();
                                CollectionFolder collectionFolder = new CollectionFolder(name);
                                try {
                                    collectionFolder.saveThrows();
                                    Toasty.success(getContext(),"新建成功！").show();
//在当前弹窗的recyclerView更新界面
                                    if (adapter!=null){
                                        adapter.notifyItemInserted(collectionList.size());
                                    }
                                    collectionList.add(collectionFolder);
                                }catch (LitePalSupportException e){
                                    //名称重复了
                                    Toasty.info(getContext(),"已经有该收藏了！").show();
                                }

                            }
                        }).show();


            }
        });




    }

    private void initRecycler() {
        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //查询数据库
        collectionList = LitePal.findAll(CollectionFolder.class);
        //TODO: 给文件夹的isSelect变量赋值

        adapter = new CollectionFolderListAdapter(collectionList);
        adapter.bindToRecyclerView(recyclerView);
    }


    private void initView() {
        listTitle = findViewById(R.id.title);
        actionAdd = findViewById(R.id.action_help);
        actionClose = findViewById(R.id.action_close);
        recyclerView = findViewById(R.id.recycler_view);
        textInfo = findViewById(R.id.subtitle);
        collect = findViewById(R.id.btn_finish);





    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.componenet_opertaion_popup;
    }


    protected int getMaxHeight() {
        return (int) (XPopupUtils.getWindowHeight(getContext())*.85f);
    }


    @Override
    public int getMinimumHeight() {
        return (int) (XPopupUtils.getWindowHeight(getContext())*.65f);
    }
}
