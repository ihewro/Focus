package com.ihewro.focus.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.blankj.ALog;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.activity.ErrorActivity;
import com.ihewro.focus.activity.MainActivity;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.callback.FileOperationCallback;
import com.ihewro.focus.helper.DatabaseHelper;
import com.ihewro.focus.util.DataCleanManager;
import com.ihewro.focus.util.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;
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
                                            FileUtil.copyFileToTarget(backupFilesPath.get(which),activity.getDatabasePath("focus.db").getAbsolutePath(), new FileOperationCallback() {
                                                @Override
                                                public void onFinish() {
                                                    Toasty.success(activity,"恢复数据成功，为您重新启动……", Toast.LENGTH_SHORT).show();

                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            //重新启动
                                                            //启动页
                                                            Intent intent = new Intent(activity, MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            activity.startActivity(intent);
                                                            android.os.Process.killProcess(android.os.Process.myPid());
                                                        }
                                                    },1000); // 延时1秒
                                                                           }
                                            });
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
                                    .show((MainActivity)activity); // an AppCompatActivity which implements FileCallback

                        }
                    })
                    .show();
        }else {
            Toasty.info(activity,"暂无任何备份文件，请先备份数据",Toast.LENGTH_SHORT).show();
        }
    }
}