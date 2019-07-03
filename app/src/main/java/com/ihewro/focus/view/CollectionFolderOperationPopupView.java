package com.ihewro.focus.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.Collection;
import com.ihewro.focus.bean.CollectionAndFolderRelation;
import com.ihewro.focus.bean.CollectionFolder;
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
 *     time   : 2019/07/03
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CollectionFolderOperationPopupView extends OperationBottomPopupView{

    private CollectionFolder folder;

    public CollectionFolderOperationPopupView(@NonNull Context context, long id, String title, String subtitle, Help help) {
        super(context, null, title, subtitle, help);
        this.setOperationList(getFeedFolderOperationList(id));
    }


    private List<Operation> getFeedFolderOperationList(final long id) {
        folder = LitePal.find(CollectionFolder.class,id);
        List<Operation> operations = new ArrayList<>();

        Operation editName = new Operation("重命名", "", getResources().getDrawable(R.drawable.ic_rate_review_black_24dp), folder, new OperationCallback() {
            @Override
            public void run(Object o) {
                //修改名称的弹窗

                //对文件夹进行重命名
                final CollectionFolder finalO = (CollectionFolder) o;
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
                                EventBus.getDefault().post(new EventMessage(EventMessage.COLLECTION_FOLDER_OPERATION));
                                dismiss();
                            }
                        }).show();


            }
        });

        Operation delete  = new Operation("删除", "", getResources().getDrawable(R.drawable.ic_exit_to_app_black_24dp), folder, new OperationCallback() {
            @Override
            public void run(final Object o) {
                new MaterialDialog.Builder(getContext())
                        .title("操作通知")
                        .content("确实删除该收藏分类吗？确定会删除该分类下的所有收藏内容")
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                CollectionFolder collectionFolder = ((CollectionFolder)o);

                                //1.删除该文件夹下的所有feedITEN
                                LitePal.deleteAll(CollectionAndFolderRelation.class,"collectionfolderid = ?", String.valueOf(id));


                                //2.删除文件夹
                                LitePal.delete(CollectionFolder.class,id);
                                Toasty.success(getContext(),"删除成功").show();
                                EventBus.getDefault().post(new EventMessage(EventMessage.COLLECTION_FOLDER_OPERATION, (int) id));
                                dismiss();

                            }
                        })
                        .show();

            }
        });

        operations.add(editName);
        operations.add(delete);


        return operations;
    }

}
