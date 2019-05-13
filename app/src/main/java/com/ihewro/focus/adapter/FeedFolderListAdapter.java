package com.ihewro.focus.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.FeedListActivity;
import com.ihewro.focus.activity.MainActivity;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.util.UIUtil;
import com.ihewro.focus.view.FeedFolderOperationPopupView;
import com.lxj.xpopup.XPopup;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FeedFolderListAdapter extends BaseItemDraggableAdapter<FeedFolder, BaseViewHolder> {

    private Activity activity;

    public FeedFolderListAdapter(@Nullable List<FeedFolder> data,Activity activity) {
        super(R.layout.item_feed_folder,data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedFolder item) {
        helper.setText(R.id.title,item.getName());

        initListener(helper,item);
    }

    private void initListener(final BaseViewHolder helper, final FeedFolder item){

        //点击切换fragment
        helper.getView(R.id.item_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMessage(EventMessage.SHOW_FEED_LIST_MANAGE,item.getId()+""));
            }
        });
        //长按显示功能菜单
        helper.getView(R.id.item_view).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new XPopup.Builder(activity)
                        .asCustom(new FeedFolderOperationPopupView(activity, item.getId(),item.getName(),"",new Help(false)))
                        .show();
                return true;
            }
        });

    }

}
