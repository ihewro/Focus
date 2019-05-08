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
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;
import com.ihewro.focus.util.UIUtil;

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
        helper.getView(R.id.long_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMessage(EventMessage.SHOW_FEED_LIST_MANAGE,item.getId()+""));
            }
        });
        //长按修改名称
        helper.getView(R.id.long_click).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //弹窗
                new MaterialDialog.Builder(activity)
                        .title("修改文件夹名称")
                        .content("输入新的名称：")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                String name = dialog.getInputEditText().getText().toString().trim();
                                if (name.equals("")){
                                    Toasty.info(activity,"请勿填写空名字哦😯").show();
                                }else {
                                    item.setName(name);
                                    item.save();
                                }
                                EventBus.getDefault().post(new EventMessage(EventMessage.EDIT_FEED_NAME));
                            }
                        }).show();
                return true;
            }
        });
        //退订
        helper.getView(R.id.not_feed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹窗
                new MaterialDialog.Builder(activity)
                        .title("操作通知")
                        .content("确定去掉订阅文件夹吗，确定则会取消该文件夹下所有订阅！")
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                int id = item.getId();
                                LitePal.deleteAll(Feed.class,"feedfolderid = ?", String.valueOf(id));//删除文件夹下面的订阅
                                LitePal.delete(FeedFolder.class,id);//删除文件夹
                                //从列表中移除该项
                                remove(helper.getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        })
                        .show();
            }
        });
    }

}
