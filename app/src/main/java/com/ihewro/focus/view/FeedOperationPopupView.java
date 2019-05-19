package com.ihewro.focus.view;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.MainActivity;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.bean.Operation;
import com.ihewro.focus.callback.DialogCallback;
import com.ihewro.focus.callback.OperationCallback;
import com.ihewro.focus.task.ShowFeedFolderListDialogTask;

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
        operations.add(new Operation("重命名","",getResources().getDrawable(R.drawable.ic_rate_review_black_24dp),feed, new OperationCallback() {
            @Override
            public void run(Object o) {
                final Feed item = (Feed) o;
                new MaterialDialog.Builder(getContext())
                        .title("修改订阅名称")
                        .content("输入新的名称：")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(item.getName(), item.getName(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                String name = dialog.getInputEditText().getText().toString().trim();
                                if (name.equals("")){
                                    Toasty.info(getContext(),"请勿填写空名字哦😯").show();
                                }else {
                                    item.setName(name);
                                    item.save();
                                    Toasty.success(getContext(),"修改成功").show();
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
                new MaterialDialog.Builder(getContext())
                        .title("操作通知")
                        .content("确定退订该订阅吗")
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
                                Toasty.success(getContext(),"退订成功").show();
                                EventBus.getDefault().post(new EventMessage(EventMessage.DELETE_FEED,id));
                            }
                        })
                        .show();
            }
        }));


        operations.add(new Operation("标记全部已读","",getResources().getDrawable(R.drawable.ic_radio_button_checked_black_24dp),feed, new OperationCallback() {
            @Override
            public void run(Object o) {

                //显示弹窗
                new MaterialDialog.Builder(getContext())
                        .title("操作通知")
                        .content("确定将该订阅下所有文章标记已读吗？")
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                ContentValues values = new ContentValues();
                                values.put("read", "1");
                                LitePal.updateAll(FeedItem.class,values,"feedid = ?", String.valueOf(id));
                                Toasty.success(getContext(),"操作成功").show();
                                EventBus.getDefault().post(new EventMessage(EventMessage.MARK_FEED_READ, (int) id));
                            }
                        })
                        .show();
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
                        Toasty.success(getContext(),"移动成功").show();
                        EventBus.getDefault().post(new EventMessage(EventMessage.MOVE_FEED));
                    }
                },getContext(),"移动到其他文件夹","点击文件夹名称执行移动操作").execute();
            }
        }));


        operations.add(new Operation("复制RSS地址","",getResources().getDrawable(R.drawable.ic_content_copy_black_24dp),feed, new OperationCallback() {
            @Override
            public void run(Object o) {
                final Feed item = (Feed)o;
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, item.getUrl()));
                Toasty.success(getContext(),"复制成功").show();
                dismiss();

            }
        }));

        operations.add(new Operation("修改RSS地址","",getResources().getDrawable(R.drawable.ic_touch_app_black_24dp),feed, new OperationCallback() {
            @Override
            public void run(Object o) {
                final Feed item = (Feed)o;
                new MaterialDialog.Builder(getContext())
                        .title("修改RSS地址")
                        .content("输入修改后的RSS地址：")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(item.getUrl(), item.getUrl(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                String url = dialog.getInputEditText().getText().toString().trim();
                                if (url.equals("")){
                                    Toasty.info(getContext(),"请勿为空😯").show();
                                }else {
                                    item.setUrl(url);
                                    item.save();
                                    Toasty.success(getContext(),"修改成功").show();

                                    EventBus.getDefault().post(new EventMessage(EventMessage.EDIT_FEED_NAME));
                                }
                            }
                        }).show();
            }
        }));


        operations.add(new Operation("设置超时时间","",getResources().getDrawable(R.drawable.ic_touch_app_black_24dp),feed, new OperationCallback() {
            @Override
            public void run(Object o) {
                final Feed item = (Feed)o;
                new MaterialDialog.Builder(getContext())
                        .title("设置超时时间")
                        .content("单位是秒，默认15s，时间太短可能会导致部分源无法获取最新数据：")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(item.getTimeout()+"", item.getTimeout()+"", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                String timeout = dialog.getInputEditText().getText().toString().trim();
                                if (timeout.equals("")){
                                    Toasty.info(getContext(),"请勿为空😯").show();
                                }else {
                                    item.setTimeout(Integer.parseInt(timeout));
                                    item.save();
                                    Toasty.success(getContext(),"设置成功").show();
                                    EventBus.getDefault().post(new EventMessage(EventMessage.EDIT_FEED_NAME));
                                }
                            }
                        }).show();
            }
        }));


        return  operations;
    }




}
