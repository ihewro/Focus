package com.ihewro.focus.view;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.bean.Operation;
import com.ihewro.focus.callback.OperationCallback;

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
public class FeedFolderOperationPopupView extends OperationBottomPopupView {

    private FeedFolder feedFolder;

    public FeedFolderOperationPopupView(@NonNull Context context, long id, String title, String subtitle, Help help) {
        super(context, null, title, subtitle, help);
        this.setOperationList(getFeedFolderOperationList(id));
    }

    private List<Operation> getFeedFolderOperationList(final long id){
        feedFolder = LitePal.find(FeedFolder.class,id);
        List<Operation> operations = new ArrayList<>();
        operations.add(new Operation("重命名文件夹","", getResources().getDrawable(R.drawable.ic_rate_review_black_24dp),feedFolder, new OperationCallback() {
            @Override
            public void run(Object o) {
                //对文件夹进行重命名
                final FeedFolder finalO = (FeedFolder) o;
                new MaterialDialog.Builder(getContext())
                        .title("修改文件夹名称")
                        .content("输入新的名称：")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                String name = dialog.getInputEditText().getText().toString().trim();
                                if (name.equals("")){
                                    Toasty.info(getContext(),"请勿填写空名字哦😯").show();
                                }else {
                                    finalO.setName(name);
                                    finalO.save();
                                }
                                Toasty.success(getContext(),"修改成功").show();

                                EventBus.getDefault().post(new EventMessage(EventMessage.EDIT_FEED_FOLDER_NAME));
                            }
                        }).show();

            }
        }));

        operations.add(new Operation("退订文件夹","", getResources().getDrawable(R.drawable.ic_exit_to_app_black_24dp),feedFolder, new OperationCallback() {
            @Override
            public void run(final Object o) {
                new MaterialDialog.Builder(getContext())
                        .title("操作通知")
                        .content("确定退订该文件夹吗？确定会退订文件夹下所有订阅")
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                FeedFolder feedFolder = ((FeedFolder)o);
                                //退订文件夹的内容

                                //1.删除该文件夹下的所有feedITEN
                                List<Feed> temp = LitePal.where("feedfolderid = ?", String.valueOf(id)).find(Feed.class);
                                for (int i = 0;i<temp.size();i++){
                                    LitePal.deleteAll(FeedItem.class,"feedid = ?", String.valueOf(temp.get(i).getId()));
                                    //2.删除文件夹下的所有feed
                                    temp.get(i).delete();
                                }

                                //3.删除文件夹
                                LitePal.delete(FeedFolder.class,id);
                                Toasty.success(getContext(),"退订成功").show();

                                EventBus.getDefault().post(new EventMessage(EventMessage.DELETE_FEED_FOLDER, (int) id));
                            }
                        })
                        .show();

            }
        }));

        operations.add(new Operation("标记全部已读", "",getResources().getDrawable(R.drawable.ic_radio_button_checked_black_24dp),feedFolder, new OperationCallback() {
            @Override
            public void run(final Object o) {
                //显示弹窗
                new MaterialDialog.Builder(getContext())
                        .title("操作通知")
                        .content("确定将该订阅下所有文章标记已读吗？")
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                FeedFolder feedFolder = (FeedFolder)o;
                                //标记全部已读
                                List<Feed> feedList = LitePal.where("feedfolderid = ?", String.valueOf(feedFolder.getId())).find(Feed.class);
                                for (Feed feed: feedList){
                                    ContentValues values = new ContentValues();
                                    values.put("read", "1");
                                    LitePal.updateAll(FeedItem.class,values,"feedid = ?", String.valueOf(feed.getId()));

                                }
                                Toasty.success(getContext(),"标记成功").show();
                                EventBus.getDefault().post(new EventMessage(EventMessage.MARK_FEED_FOLDER_READ, (int) id));
                            }
                        })
                        .show();
            }
        }));

        return  operations;
    }



}
