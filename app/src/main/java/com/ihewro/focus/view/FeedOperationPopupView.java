package com.ihewro.focus.view;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.bean.Operation;
import com.ihewro.focus.callback.DialogCallback;
import com.ihewro.focus.callback.OperationCallback;
import com.ihewro.focus.task.ShowFeedFolderListDialogTask;
import com.ihewro.focus.util.UIUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/13
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FeedOperationPopupView extends OperationBottomPopupView{


    public FeedOperationPopupView(@NonNull Context context, long id, String title, String subtitle, Help help) {
        super(context, null, title, subtitle, help);
        this.setOperationList(getFeedOperationList(id));
    }

    private List<Operation> getFeedOperationList(final long id){
        List<Operation> operations = new ArrayList<>();
        Feed feed = LitePal.find(Feed.class,id);
        operations.add(new Operation("重命名","",getResources().getDrawable(R.drawable.ic_edit_black_24dp),feed, new OperationCallback() {
            @Override
            public void run(Object o) {
                final Feed item = (Feed) o;
                new MaterialDialog.Builder(UIUtil.getContext())
                        .title("修改订阅名称")
                        .content("输入新的名称：")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(item.getName(), "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                String name = dialog.getInputEditText().getText().toString().trim();
                                if (name.equals("")){
                                    Toasty.info(UIUtil.getContext(),"请勿填写空名字哦😯").show();
                                }else {
                                    item.setName(name);
                                    item.save();
                                    EventBus.getDefault().post(new EventMessage(EventMessage.EDIT_FEED_NAME));
                                }
                            }
                        }).show();
            }
        }));


        operations.add(new Operation("退订","",getResources().getDrawable(R.drawable.ic_exit_to_app_black_24dp),feed, new OperationCallback() {
            @Override
            public void run(Object o) {
                final Feed item = (Feed)o;
                new MaterialDialog.Builder(UIUtil.getContext())
                        .title("操作通知")
                        .content("确定去掉订阅文件夹吗，确定则会取消该文件夹下所有订阅！")
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                int id = item.getId();
                                //先删除对应的feedITEM
                                LitePal.deleteAll(FeedItem.class,"feedid = ?", String.valueOf(item.getId()));
                                //再删除feed
                                LitePal.delete(Feed.class,id);

                                EventBus.getDefault().post(new EventMessage(EventMessage.DELETE_FEED));
                            }
                        })
                        .show();
            }
        }));


        operations.add(new Operation("标记全部已读","",getResources().getDrawable(R.drawable.ic_radio_button_checked_black_24dp),feed, new OperationCallback() {
            @Override
            public void run(Object o) {
                ContentValues values = new ContentValues();
                values.put("read", "1");
                LitePal.updateAll(FeedItem.class,values,"feedid = ?", String.valueOf(id));
            }
        }));


        operations.add(new Operation("移动到其他文件夹","",getResources().getDrawable(R.drawable.ic_touch_app_black_24dp),feed, new OperationCallback() {
            @Override
            public void run(Object o) {
                final Feed item = (Feed)o;
                new ShowFeedFolderListDialogTask(new DialogCallback() {
                    @Override
                    public void onFinish(MaterialDialog dialog, View view, int which, CharSequence text, int targetId) {
                        //移动到指定的目录下
                        item.setFeedFolderId(targetId);
                        item.save();
                        EventBus.getDefault().post(new EventMessage(EventMessage.MOVE_FEED));
                    }
                },UIUtil.getContext(),"移动到其他文件夹","点击文件夹名称执行移动操作").execute();
            }
        }));


        return  operations;
    }




}
