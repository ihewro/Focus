package com.ihewro.focus.task;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ProgressBar;

import com.ihewro.focus.R;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.callback.RequestDataCallback;
import com.ihewro.focus.callback.RequestFeedItemListCallback;
import com.ihewro.focus.view.FilterPopupView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class RequestFeedListDataService extends Service {

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builderProgress;
    private Notification notification;

    private boolean flag;//isForce为true的时候表明不是一开始打开页面，所以此时的刷新请求数据必须请求
    private Activity activity;
    private boolean is_use_internet;//是否使用网络请求
    private ProgressBar pbProgress;
    private int orderChoice = FilterPopupView.ORDER_BY_NEW;
    private int filterChoice = FilterPopupView.SHOW_ALL;
    private View view;
    private List<Feed> feedList = new ArrayList<>();
    private LinkedHashSet<FeedItem> eList = new LinkedHashSet<>();//使用set保证不重复
    private RequestFeedItemListCallback callback;


    private MyBinder mBinder = new MyBinder();


    //default construct
    public RequestFeedListDataService() {

    }



    public class MyBinder extends Binder{

        public void initParameter(int orderChoice2, int filterChoice2, Activity activity2, View view2, boolean flag, List<Feed> feedList2, RequestFeedItemListCallback callback2){
            activity = activity2;
            feedList = feedList2;
            callback = callback2;
            RequestFeedListDataService.this.flag = flag;
            view = view2;
            orderChoice = orderChoice2;
            filterChoice = filterChoice2;

            createNotice("初始化数据获取服务……",0);
        }

        public void startTask(){
            RequestFeedListDataTask task = new RequestFeedListDataTask(orderChoice,filterChoice,activity,view,flag,feedList, new RequestDataCallback() {

                @Override
                public void onBegin() {
                    callback.onBegin();
                    //发送通知
                    startForeground(1,createNotice("开始获取数据中……",0));
                }

                @Override
                public void onProgress(int progress) {
                    //更新通知
                    mNotificationManager.notify(1, createNotice("获取中……",progress));
                }

                @Override
                public void onSuccess(List<FeedItem> list) {
                    //通知显示已完成
                    //结束
                    stopForeground(true);

                    mNotificationManager.notify(1, createNotice("数据获取完毕！",100));

                    //通知activity修改数据
                    callback.onFinish(list);
                    //结束自己
                    stopSelf();
                }

                @Override
                public void onFailed() {
                    //这个地方暂时不需要的，等待扩展
                }
            });

            task.execute();
        }


    }



    private Notification createNotice(String title, int progress){
        //消息管理
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        initChannels(getApplicationContext());
        builderProgress = new NotificationCompat.Builder(getApplicationContext(), "focus_pull_data");
        builderProgress.setContentTitle(title);
        builderProgress.setSmallIcon(R.mipmap.ic_focus_launcher_round);
//        builderProgress.setTicker("进度条通知");

        if (progress > 0){
            builderProgress.setContentText(progress + "%");
            builderProgress.setProgress(100, progress, false);
        }

        notification = builderProgress.build();

        return notification;
    }

    /**
     * android 8.0 新增的notification channel这里需要做一个判断
     *
     * @param context
     */
    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("focus_pull_data",
                "Channel focus",
                NotificationManager.IMPORTANCE_MIN);
        channel.setDescription("更新订阅的数据");
        mNotificationManager.createNotificationChannel(channel);
    }


    public void updateNotice(int percent){
        //更新进度条
        builderProgress.setProgress(100, percent, false);
        //再次通知
        mNotificationManager.notify(1, builderProgress.build());
    }


    private NotificationManager getNotificationManager(){
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
