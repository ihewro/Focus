package com.ihewro.focus.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.OperationListAdapter;
import com.ihewro.focus.adapter.RequireListAdapter;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedRequire;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.bean.Operation;
import com.ihewro.focus.callback.DialogCallback;
import com.ihewro.focus.task.ShowFeedFolderListDialogTask;
import com.ihewro.focus.util.StringUtil;
import com.ihewro.focus.util.UIUtil;
import com.lxj.xpopup.core.BottomPopupView;

import org.greenrobot.eventbus.EventBus;
import org.litepal.exceptions.LitePalSupportException;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/12
 *     desc   : 从底部弹出的来的功能操作的弹窗
 *     version: 1.0
 * </pre>
 */
public class OperationBottomPopupView  extends BottomPopupView {

    TextView tvTitle;
    TextView tvSubTitle;
    ImageView actionHelp;
    ImageView actionClose;
    RecyclerView recyclerView;

    private OperationListAdapter adapter;
    private List<Operation> operationList = new ArrayList<>();
    private String title;
    private String subtitle;

    private Help help;


    public OperationBottomPopupView(@NonNull Context context) {
        super(context);
    }

    public OperationBottomPopupView(@NonNull Context context, List<Operation> operationList, String title, String subtitle, Help help) {
        super(context);
        this.operationList = operationList;
        this.title = title;
        this.help = help;
        this.subtitle = subtitle;
    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.componenet_opertaion_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        initView();

        initRecycler();

        initListener();

    }


    private void initView() {
        tvTitle = findViewById(R.id.title);
        tvSubTitle = findViewById(R.id.subtitle);

        actionHelp = findViewById(R.id.action_help);
        actionClose = findViewById(R.id.action_close);
        recyclerView = findViewById(R.id.recycler_view);

        tvTitle.setText(title);
        if (!StringUtil.trim(subtitle).trim().equals("")){
            tvSubTitle.setText(subtitle);
            tvSubTitle.setVisibility(View.VISIBLE);
        }else {
            tvSubTitle.setVisibility(View.GONE);
        }

        if (!help.isHelp()){
            actionHelp.setVisibility(View.GONE);
        }else {
            actionHelp.setVisibility(View.VISIBLE);
            //点击事件，这个地方功能扩展
        }

    }

    private void initRecycler() {
        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new OperationListAdapter(operationList);
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //执行对应操作
                operationList.get(position).run();
            }
        });
    }

    private void initListener(){



        actionClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();//关闭当前弹窗
            }
        });
    }


    @Override
    protected void onShow() {
        super.onShow();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }


    public List<Operation> getOperationList() {
        return operationList;
    }

    public void setOperationList(List<Operation> operationList) {
        this.operationList = operationList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
