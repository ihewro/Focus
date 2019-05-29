package com.ihewro.focus.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.blankj.ALog;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.MyApplication;
import com.ihewro.focus.callback.FileOperationCallback;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/04/06
 *     desc   : 全局操作的一些公共操作
 *     version: 1.0
 * </pre>
 */
public class UIUtil {

    final static int BUFFER_SIZE = 4096;


    /**
     * 获取全局Context，静态方法，你可以在任何位置调用该方法获取Context
     * @return
     */
    public static Context getContext() {
        return MyApplication.getContext();
    }

    /**
     * 获取资源对象
     *
     * @return
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 获取资源文件字符串
     *
     * @param resId
     * @return
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 获取资源文件字符串数组
     *
     * @param resId
     * @return
     */
    public static String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 获取资源文件颜色
     *
     * @param resId
     * @return
     */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 自动数据库
     */
    public static void autoBackUpWhenItIsNecessary(){
        //删除autobackup其他的所有文件
        FileUtil.delFolder(GlobalConfig.appDirPath + "database/autobackup/");
        FileUtil.copyFileToTarget(UIUtil.getContext().getDatabasePath("focus.db").getAbsolutePath(), GlobalConfig.appDirPath + "database/autobackup/" + "auto:" + DateUtil.getNowDateStr() + ".db", new FileOperationCallback() {
            @Override
            public void onFinish() {
                //nothing to do
            }
        });
    }

    public static void logCurrentThread(){
        if(Thread.currentThread() == Looper.getMainLooper().getThread()){
            ALog.d("主线程！！");
        }else {
            ALog.d("子线程！！");
        }

    }

}
