package com.ihewro.focus.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.blankj.ALog;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.helper.DatabaseHelper;
import com.ihewro.focus.util.DataCleanManager;
import com.ihewro.focus.util.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class RecoverDataTask extends AsyncTask<Void,Void,Boolean> {

    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private List<String> backupFilesPath = new ArrayList<>();
    private List<String> backupFilesName = new ArrayList<>();

    public RecoverDataTask(Activity activity) {
        this.activity = activity;
        DatabaseHelper helper=new DatabaseHelper(activity,"focus");
        SQLiteDatabase db = helper.getReadableDatabase();
        db.disableWriteAheadLogging(); //禁用WAL模式
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        File dir = new File(GlobalConfig.appDirPath + "database");
        File autoDir = new File(GlobalConfig.appDirPath + "database/autobackup");

        if (!dir.exists() || !dir.isDirectory()){
            dir.mkdir();
            return false;
        }else {
            File[] backupFiles = dir.listFiles();
            for (File backupFile : backupFiles) {
                if (backupFile.isFile()){
                    backupFilesPath.add(GlobalConfig.appDirPath + "database/" + backupFile.getName());
                    backupFilesName.add(backupFile.getName() + "(" + DataCleanManager.getFormatSize(backupFile.length()) + ")");
                }
            }

            if (!autoDir.exists() || !autoDir.isDirectory()){
                autoDir.mkdir();
            }else {
                backupFiles = autoDir.listFiles();
                for (File autoBackupFile : backupFiles) {
                    ALog.d(autoBackupFile.getName() + autoBackupFile.getAbsolutePath());
                    if (autoBackupFile.isFile()){
                        backupFilesPath.add(GlobalConfig.appDirPath + "database/autobackup/" + autoBackupFile.getName());
                        backupFilesName.add(autoBackupFile.getName() + "(" + DataCleanManager.getFormatSize(autoBackupFile.length()) + ")");
                    }
                }
            }
            return true;
        }
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        if (aVoid){
            new MaterialDialog.Builder(activity)
                    .title("备份列表")
                    .content("单击恢复数据，长按删除备份")
                    .items(backupFilesName)
                    .itemsLongCallback(new MaterialDialog.ListLongCallback() {
                        @Override
                        public boolean onLongSelection(final MaterialDialog dialog, View itemView, final int position, CharSequence text) {
                            new MaterialDialog.Builder(activity)
                                    .title("确认删除此备份吗？")
                                    .content("该操作无法撤销，删除前先确定你不需要该备份数据了")
                                    .positiveText("确定")
                                    .negativeText("取消")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which2) {
                                            File file = new File(backupFilesPath.get(position));
                                            if (file.exists()){
                                                file.delete();
                                            }
                                            Toasty.success(activity,"删除该备份成功",Toast.LENGTH_SHORT).show();
                                            dialog.getItems().remove(position);
                                            dialog.notifyItemsChanged();
                                        }
                                    })
                                    .show();
                            return false;
                        }
                    })
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, final int which, CharSequence text) {
                            new MaterialDialog.Builder(activity)
                                    .title("确认恢复此备份吗？")
                                    .content("一旦恢复数据后，无法撤销操作。但是你可以稍后继续选择恢复其他备份文件")
                                    .positiveText("确定")
                                    .negativeText("取消")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which2) {

                                            final MaterialDialog dialog1 = new MaterialDialog.Builder(activity)
                                                    .content("马上就好……")
                                                    .progress(true, 0)
                                                    .show();

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    importData(backupFilesPath.get(which));
                                                    activity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            dialog1.dismiss();
                                                            Toasty.success(activity,"恢复数据成功！", Toast.LENGTH_SHORT).show();
                                                            EventBus.getDefault().post(new EventMessage(EventMessage.DATABASE_RECOVER));
                                                        }
                                                    });
                                                }
                                            }).start();
                                        }
                                    })
                                    .show();
                        }
                    })
                    .neutralText("导入备份")
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            //导入备份

                            new FileChooserDialog.Builder(activity)
                                    .initialPath(Environment.getExternalStorageDirectory().getAbsolutePath())  // changes initial path, defaults to external storage directory
                                    .extensionsFilter(".db") // Optional extension filter, will override mimeType()
                                    .tag("optional-identifier")
                                    .goUpLabel("上一级") // custom go up label, default label is "..."
                                    .show((FragmentActivity)activity); // an AppCompatActivity which implements FileCallback

                        }
                    })
                    .show();
        }else {
            Toasty.info(activity,"暂无任何备份文件，请先备份数据",Toast.LENGTH_SHORT).show();
        }
    }



    private void importData(String path){
        //子线程
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(path, null);
        database.disableWriteAheadLogging();

        //删除数据库
        LitePal.deleteAll(FeedFolder.class);
        LitePal.deleteAll(Feed.class);
        LitePal.deleteAll(FeedItem.class);
        LitePal.deleteAll(UserPreference.class);

        Cursor feedFolderCur = database.rawQuery("SELECT * from feedfolder", null);
        if (feedFolderCur!=null && feedFolderCur.moveToFirst()){
            do{
                FeedFolder feedFolder = new FeedFolder();
                String name = feedFolderCur.getString(feedFolderCur.getColumnIndex("name"));
                int feedfolderId = feedFolderCur.getInt(feedFolderCur.getColumnIndex("id"));
                feedFolder.setName(name);
                feedFolder.save();

                Cursor feedCur = database.rawQuery("select * from feed where feedfolderid = ?", new String[]{""+feedfolderId});
                if (feedCur!=null && feedCur.moveToFirst()){
                    do {
                        String name2 = feedCur.getString(feedCur.getColumnIndex("name"));
                        String desc = feedCur.getString(feedCur.getColumnIndex("desc_lpcolumn"));
                        String url = feedCur.getString(feedCur.getColumnIndex("url"));
                        String link = feedCur.getString(feedCur.getColumnIndex("link"));
                        String websiteName = feedCur.getString(feedCur.getColumnIndex("websitename"));
                        String websiteCategoryName = feedCur.getString(feedCur.getColumnIndex("websitecategoryname"));
                        Long time = feedCur.getLong(feedCur.getColumnIndex("time"));
                        int feedFolderId = feedFolder.getId();
                        String type = feedCur.getString(feedCur.getColumnIndex("type"));
                        int timeout = feedCur.getInt(feedCur.getColumnIndex("timeout"));
                        String temp = feedCur.getString(feedCur.getColumnIndex("errorget"));
                        boolean errorGet;
                        if (StringUtil.trim(temp).equals("1")){
                            errorGet = true;
                        }else {
                            errorGet = false;
                        }
                        int feedId = feedCur.getInt(feedCur.getColumnIndex("id"));
                        Feed feed = new Feed(name2,desc,url,link,websiteName,websiteCategoryName,time,feedFolderId,type,timeout,errorGet);
                        feed.save();

                        //绑定该feed下面的所有文章
                        Cursor feedItemCur = database.rawQuery("SELECT * from feeditem where feedid = ?", new String[]{feedId + ""});
                        if (feedItemCur != null && feedItemCur.moveToFirst()) {
                            if (feedItemCur.moveToFirst()) {
                                do {
                                    String title = feedItemCur.getString(feedItemCur.getColumnIndex("title"));
                                    Long date = feedItemCur.getLong(feedItemCur.getColumnIndex("date"));
                                    String summary = feedItemCur.getString(feedItemCur.getColumnIndex("summary"));
                                    String content = feedItemCur.getString(feedItemCur.getColumnIndex("content"));
                                    int feedId2 = feed.getId();
                                    String feedName = feedItemCur.getString(feedItemCur.getColumnIndex("feedname"));
                                    String url2 = feedItemCur.getString(feedItemCur.getColumnIndex("url"));
                                    temp = StringUtil.trim(feedItemCur.getString(feedItemCur.getColumnIndex("read")));
                                    boolean read;
                                    read = temp.equals("1");
                                    boolean favorite;
                                    temp = StringUtil.trim(feedItemCur.getString(feedItemCur.getColumnIndex("favorite")));
                                    favorite = temp.equals("1");
                                    FeedItem feedItem = new FeedItem(title,date,summary,content,feedId2,feedName,url2,read,favorite);
                                    feedItem.save();
                                } while (feedItemCur.moveToNext());
                            }
                            feedItemCur.close();
                        }

                    }while (feedCur.moveToNext());
                    feedCur.close();
                }
            }while (feedFolderCur.moveToNext());
            feedFolderCur.close();


            //恢复用户设置表
            Cursor userCur = database.rawQuery("select * from userpreference",null);
            if (userCur!=null && userCur.moveToFirst()){
                do{
                    String key = userCur.getString(userCur.getColumnIndex("key"));
                    String value = userCur.getString(userCur.getColumnIndex("value"));
                    String defaultValue = "";
                    try {//早期表没有这个字段
                        defaultValue = userCur.getString(userCur.getColumnIndex("defalutvalue"));
                    }catch (IllegalStateException e){
                        ALog.d(e);
                    }
                    UserPreference userPreference = new UserPreference(key,value,defaultValue);
                    userPreference.save();
                }while (userCur.moveToNext());
                userCur.close();
            }

            database.close();
        }

    }
}