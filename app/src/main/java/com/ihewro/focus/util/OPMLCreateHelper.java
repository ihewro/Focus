package com.ihewro.focus.util;

import android.Manifest;
import android.app.Activity;
import android.os.Environment;

import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.ihewro.focus.activity.FeedManageActivity;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/11
 *     desc   : 导出OPML文件
 *     version: 1.0
 * </pre>
 */
public class OPMLCreateHelper {

    public static final int REQUEST_STORAGE_WRITE = 210;

    private Activity activity;
    public OPMLCreateHelper() {
    }

    public OPMLCreateHelper(Activity activity) {
        this.activity = activity;
    }

    public void run(){
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(activity, perms)) {
            //有权限
           add();


        } else {
            //没有权限 1. 申请权限
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(activity, REQUEST_STORAGE_WRITE, perms)
                            .setRationale("必须允许写存储器才能导出OPML文件")
                            .setPositiveButtonText("确定")
                            .setNegativeButtonText("取消")
                            .build());
        }

    }

    private void add() {
        //
    }


}
