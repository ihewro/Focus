package com.ihewro.focus.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.common.base.Strings;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.MyApplication;
import com.ihewro.focus.callback.FileOperationCallback;
import com.mikepenz.materialize.util.UIUtils;

import static java.lang.Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS;
import static java.lang.Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS;
import static java.lang.Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT;
import static java.lang.Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
import static java.lang.Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A;
import static java.lang.Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B;

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

    public static boolean isMainThread(){
        if(Thread.currentThread() == Looper.getMainLooper().getThread()){
            return true;
        }else {
            return false;
        }

    }

    /**
     * make true current connect service is wifi
     */
    public static boolean isWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) UIUtil.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }


    public static void runOnUiThread(Activity activity,Runnable runnable){
        if (activity!=null){
            activity.runOnUiThread(runnable);
        }
    }


    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }



    public static boolean checkStringContainChinese(String checkStr){
        if(!Strings.isNullOrEmpty(checkStr)){
            char[] checkChars = checkStr.toCharArray();
            for(int i = 0; i < checkChars.length; i++){
                char checkChar = checkChars[i];
                if(checkCharContainChinese(checkChar)){
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkCharContainChinese(char checkChar){
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(checkChar);
        if(CJK_UNIFIED_IDEOGRAPHS == ub || CJK_COMPATIBILITY_IDEOGRAPHS == ub || CJK_COMPATIBILITY_FORMS == ub ||
                CJK_RADICALS_SUPPLEMENT == ub || CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A == ub || CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B == ub){
            return true;
        }
        return false;
    }



}
