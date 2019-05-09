package com.ihewro.focus.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
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
public class FeedListManageAdapter extends BaseQuickAdapter<Feed, BaseViewHolder>{

    private Activity activity;


    public FeedListManageAdapter(@Nullable List<Feed> data,Activity activity) {
        super(R.layout.item_feed_folder,data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Feed item) {
        helper.setText(R.id.title,item.getName());
        //TODO:如果有自己ico图标，则显示ico图标
        helper.setImageResource(R.id.main_logo,R.drawable.ic_rss_feed_grey_24dp);


        initListener(helper,item);
    }

    private void initListener(final BaseViewHolder helper, final Feed item){
        //长按修改名称
        helper.getView(R.id.long_click).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //功能选择弹窗

                String[] operation = {"修改订阅名称","移动到其他目录"};
                new MaterialDialog.Builder(activity)
                        .title("功能列表")
//                        .content("加载表情目录中稍等")
                        .items(operation)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (which == 0){
                                    editName(item);
                                }else if (which == 1){
                                    moveToFolder(item);
                                }

                            }
                        })
                        .show();
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
                                LitePal.delete(Feed.class,id);
                                //从列表中移除该项
                                remove(helper.getAdapterPosition());
                                notifyDataSetChanged();
                                EventBus.getDefault().post(new EventMessage(EventMessage.EDIT_FEED_FOLDER_NAME));
                            }
                        })
                        .show();
            }
        });
    }


    private void editName(final Feed item){
        new MaterialDialog.Builder(activity)
                .title("修改订阅名称")
                .content("输入新的名称：")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(item.getName(), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        String name = dialog.getInputEditText().getText().toString().trim();
                        if (name.equals("")){
                            Toasty.info(activity,"请勿填写空名字哦😯").show();
                        }else {
                            item.setName(name);
                            item.save();
                            EventBus.getDefault().post(new EventMessage(EventMessage.EDIT_FEED_FOLDER_NAME));
                        }
                    }
                }).show();
    }

    private void moveToFolder(final Feed item){
        final List<FeedFolder> feedFolders = LitePal.findAll(FeedFolder.class);
        List<String> list = new ArrayList<>();
        for (int i = 0;i < feedFolders.size(); i++){
            list.add(feedFolders.get(i).getName());
        }

        String[] temp = list.toArray(new String[0]);
        new MaterialDialog.Builder(activity)
                .title("功能列表")
//                        .content("加载表情目录中稍等")
                .items(temp)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        //移动到指定的目录下
                        item.setFeedFolderId(feedFolders.get(which).getId());
                        item.save();
                        EventBus.getDefault().post(new EventMessage(EventMessage.EDIT_FEED_FOLDER_NAME));
                    }
                })
                .show();

    }
}
