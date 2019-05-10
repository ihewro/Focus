package com.ihewro.focus.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedFolder;
import com.ihewro.focus.callback.DialogCallback;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ShowFeedFolderListDialogTask extends AsyncTask<Void,Integer,List<String>> {

    private DialogCallback listener;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private String title;
    private String content;

    private List<FeedFolder> feedFolders;
    private boolean hasContent;

    public ShowFeedFolderListDialogTask(DialogCallback listener, Activity activity, String title, String content) {
        this.listener = listener;
        this.activity = activity;
        this.title = title;
        if (content.trim().equals("")){
            hasContent = false;
        }else {
            hasContent = true;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<String> doInBackground(Void... voids) {

        feedFolders = LitePal.findAll(FeedFolder.class);
        List<String> list = new ArrayList<>();
        for (int i = 0;i < feedFolders.size(); i++){
            list.add(feedFolders.get(i).getName());
        }


        return list;
    }

    @Override
    protected void onPostExecute(List<String> list) {
        showDialog(list);
    }

    private void showDialog(final List<String> list){
        final String[] temp = list.toArray(new String[0]);
        new MaterialDialog.Builder(activity)
                .title(title)
                .items(temp)
                .neutralText("新增")
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        //移动到指定的目录下
                        listener.onFinish(dialog,view,which,text,feedFolders.get(which).getId());
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        //新增文件夹
                        new MaterialDialog.Builder(activity)
                                .title("输入新增加的文件夹名称：")
                                .inputType(InputType.TYPE_CLASS_TEXT)
                                .input("", "", new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog dialog2, CharSequence input) {
                                        //TODO:不能重命名
                                        String name = dialog2.getInputEditText().getText().toString().trim();
                                        FeedFolder feedFolder = new FeedFolder(name);
                                        feedFolder.save();
                                        list.add(name);
                                        feedFolders.add(feedFolder);//这个列表也必须添加上
                                        EventBus.getDefault().post(new EventMessage(EventMessage.ADD_FEED_FOLDER));
                                        showDialog(list);
                                    }
                                }).show();

                    }
                })
                .show();
    }
}
