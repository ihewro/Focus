package com.ihewro.focus.task;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.MainActivity;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.callback.RequestDataCallback;
import com.ihewro.focus.callback.RequestFeedItemListCallback;
import com.ihewro.focus.util.UIUtil;
import com.ihewro.focus.view.FilterPopupView;

import org.litepal.LitePal;
import org.litepal.crud.callback.CountCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import es.dmoral.toasty.Toasty;

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

    private boolean isForce;//isForce为true的时候表明不是一开始打开页面，所以此时的刷新请求数据必须请求
    private boolean flag;//用户设置中是否快速启动
    private Activity activity;
    private boolean is_use_internet;//是否使用网络请求
    private ProgressBar pbProgress;
    private String orderChoice = FilterPopupView.ORDER_BY_NEW;
    private String filterChoice = FilterPopupView.SHOW_ALL;
    private View view;
    private TextView subTitle;
    private List<Feed> feedList = new ArrayList<>();
    private LinkedHashSet<FeedItem> eList = new LinkedHashSet<>();//使用set保证不重复，是之前的数据+当前请求的数据
    private RequestFeedItemListCallback callback;
    private boolean isFinish = false;


    private AtomicInteger num = new AtomicInteger();//总共需要请求的数目
    private volatile AtomicInteger okNum = new AtomicInteger() ;//已经请求的数目

    private int feedItemNum;
    private int feedItemNumTemp;



    private MyBinder mBinder = new MyBinder();


    //default construct
    public RequestFeedListDataService() {

    }




    public class MyBinder extends Binder{

        public void initParameter(TextView subTitle2,Activity activity2, View view2, boolean flag, List<Feed> feedList2, RequestFeedItemListCallback callback2){
            //主线程
            activity = activity2;
            feedList = feedList2;
            callback = callback2;
            RequestFeedListDataService.this.isForce = !flag;
            view = view2;
            subTitle = subTitle2;
            createNotice("初始化数据获取服务……",0);

            //初始化文章总数目

        }

        public void startTask(){
            //主线程

            num.set(feedList.size());;
            eList.clear();
            String value = UserPreference.queryValueByKey(UserPreference.USE_INTERNET_WHILE_OPEN, "0");
            if (value != null && value.equals("1")){//强制刷新数据
                flag = true;
            }else {
                flag = false;
            }
            okNum.set(0);;
            callback.onBegin();

            //必须要开启前台通知
            startForeground(1,createNotice("初始化数据获取服务…",0));


            if (num.get()>0){//请求总数大于1才会进行请求
                if (!flag && !isForce){
                    //加载本地数据就可以了，没有网络请求
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handleData(new RequestDataCallback() {
                                @Override
                                public void onSuccess(List<FeedItem> feedItemList) {
                                    //主线程
                                    ALog.d("无网络请求结束");
                                    callback.onFinish(feedItemList,0);
                                    stopForeground(true);
                                    mNotificationManager.cancel(1);//无网络情况下状态栏不需要留下通知
                                }
                            });
                        }
                    }).start();
                }else {//网络请求
                    int num = LitePal.count(FeedItem.class);
                    RequestFeedListDataService.this.feedItemNum = num;
                    RequestFeedListDataService.this.feedItemNumTemp = num;
                    Toasty.success(activity,"开始请求数据").show();
                    mNotificationManager.notify(1, createNotice("开始获取数据中……",0));
                    ExecutorService mExecutor = Executors.newCachedThreadPool();
                    RequestFeedListDataService.this.feedItemNum = num;
                    RequestFeedListDataService.this.feedItemNumTemp = num;
                    for (int i = 0;i < feedList.size();i++){

                        if (!feedList.get(i).isOffline()){
                            //改为线程池调用
                            RequestFeedListDataTask task = new RequestFeedListDataTask(new RequestDataCallback() {
                                @Override
                                public void onSuccess(List<FeedItem> feedItemList) {
                                    //主线程
                                    updateUI();
                                }
                            });
                            task.executeOnExecutor(mExecutor,feedList.get(i));
                        }else {
                            updateUI();

                        }

                    }
                }
            }else {//也必须返回一个空数组
                callback.onFinish(new ArrayList<FeedItem>(),0);
            }
        }

        //主线程
        private void updateUI(){
            //子线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (this){
                        okNum.incrementAndGet();

                        UIUtil.runOnUiThread(activity, new Runnable() {
                            @Override
                            public void run() {
                                //计算出当前的process
                                int process = (int) (okNum.get() *1.0 / num.get() * 100);
                                mNotificationManager.notify(1, createNotice("获取中，您可以切换别的应用稍作等待一会……",process));
                            }
                        });

                        //没请求完也需要
                        handleData(new RequestDataCallback() {
                            @Override
                            public void onSuccess(final List<FeedItem> feedItemList) {
                                //主线程
                                //使用了网络请求
                                if (okNum.get() >= num.get() && !isFinish){//数据全部请求完毕
                                    isFinish = true;
                                    ALog.d("请求数据完毕");
                                    int num = LitePal.count(FeedItem.class);
                                    final int sub = num - RequestFeedListDataService.this.feedItemNum;
                                    stopForeground(true);
                                    if (sub>0){
                                        mNotificationManager.notify(1, createNotice("共有"+sub+"篇新文章",100));
                                    }else {
                                        mNotificationManager.notify(1, createNotice("暂无新数据",100));
                                    }
                                    //通知activity修改数据
                                    callback.onFinish(feedItemList,sub);
                                    //结束当前服务
                                    stopSelf();
                                }else {//任务没有结束
                                    if (!isFinish){//如果已经结束了，就无需再update了！
                                        LitePal.countAsync(FeedItem.class).listen(new CountCallback() {
                                            @Override
                                            public void onFinish(int count) {
                                                final int sub = count - RequestFeedListDataService.this.feedItemNumTemp;
                                                RequestFeedListDataService.this.feedItemNumTemp = count;
                                                callback.onUpdate(feedItemList,sub);
                                            }
                                        });
                                    }
                                }
                            }
                        });


                    }
                }
            }).start();


//            ALog.d("完成数目"+okNum+"总数目"+num);

        }

        private void handleData (final RequestDataCallback callback){
            //子线程
            if (UIUtil.isMainThread()){
                ALog.d("主线程");
            }else {
//                ALog.d("子线程" + okNum);
            }
            //子线程
            //进行数据处理
            //合并旧数据没必要合并数据，请求数据的时候都已经保存到本地数据库了。
            for (int i = 0; i < num.get();i++){
                Feed temp = feedList.get(i);
                String url = temp.getUrl();
                getFeedItems(url);
            }
//                    ALog.d("开始对数据排序");
            final List<FeedItem> list = new ArrayList<>(eList);

            orderChoice = UserPreference.queryValueByKey(UserPreference.ODER_CHOICE,FilterPopupView.ORDER_BY_NEW);
            filterChoice = UserPreference.queryValueByKey(UserPreference.FILTER_CHOICE,FilterPopupView.SHOW_ALL);

            //对数据进行过滤
            if (filterChoice.equals(FilterPopupView.SHOW_STAR)){
                Iterator<FeedItem> sListIterator = list.iterator();
                while (sListIterator.hasNext()) {
                    FeedItem feedItem = sListIterator.next();
                    if (!feedItem.isFavorite()) {//非收藏数据删除
                        sListIterator.remove();
                    }
                }
            }else if (filterChoice.equals(FilterPopupView.SHOW_UNREAD)){
                Iterator<FeedItem> sListIterator = list.iterator();
                while (sListIterator.hasNext()) {
                    FeedItem feedItem = sListIterator.next();
                    if (feedItem.isRead()) {//已经阅读过的数据删除
                        sListIterator.remove();
                    }
                }
            }
            //对数据排序
            //选择排序方式
            if (orderChoice.equals(FilterPopupView.ORDER_BY_NEW)){
                Collections.sort(list, new Comparator<FeedItem>() {
                    @Override
                    public int compare(FeedItem t0, FeedItem t1) {//新的在前
                        if (t0.getDate() < t1.getDate()){
                            return 1;
                        }else if (t0.getDate() > t1.getDate()){
                            return -1;
                        }else {
                            return 0;
                        }
                    }
                });
            }else {//旧的在前
                Collections.sort(list, new Comparator<FeedItem>() {
                    @Override
                    public int compare(FeedItem t0, FeedItem t1) {//新的在前
                        if (t0.getDate() > t1.getDate()){
                            return 1;
                        }else if (t0.getDate() < t1.getDate()){
                            return -1;
                        }else {
                            return 0;
                        }
                    }
                });
            }
            UIUtil.runOnUiThread(activity,new Runnable() {
                @Override
                public void run() {
                    //主线程
                    //数据整理完毕
                    callback.onSuccess(list);
                }
            });
        }

        private void getFeedItems(String url){
            //将本地数据库的内容合并到列表中
            //找到当前feed url 本地数据库的内容
            List<Feed> tempList = LitePal.where("url = ?" ,url).find(Feed.class);
            if (tempList.size()>0){
                Feed temp = tempList.get(0);
//                ALog.d(temp);
                List<FeedItem> tempFeedItemList = LitePal.where("feedid = ?", String.valueOf(temp.getId())).find(FeedItem.class);
//                ALog.d("本地数据库信息url" + url + "订阅名称为"+ temp.getName() + "文章数目" + tempFeedItemList.size());

                //设置badguy
                for (int i = 0;i<tempFeedItemList.size();i++){
                    tempFeedItemList.get(i).setBadGuy(temp.isBadGuy());
                }
                eList.addAll(tempFeedItemList);
            }
        }

        public void stopService(){
            stopSelf();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        ALog.d("服务被结束了");

    }

    private Notification createNotice(String title, int progress){
        //消息管理
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        initChannels(getApplicationContext());
        builderProgress = new NotificationCompat.Builder(getApplicationContext(), "focus_pull_data");
        builderProgress.setContentTitle(title);
        builderProgress.setSmallIcon(R.mipmap.ic_focus_launcher_round);
//        builderProgress.setTicker("进度条通知");
        subTitle.setText(title);

        if (progress > 0){//全部获取完的时候不需要显示进度条了
            builderProgress.setContentText(progress + "%");
            subTitle.setText("请求数据进度："+progress + "%");
            builderProgress.setProgress(100, progress, false);
        }
        if (progress == 100){
            subTitle.setText("请求完毕，整理数据中……");
            builderProgress.setContentText(title);
        }
        //绑定点击事件
        Intent intent = new Intent(activity,MainActivity.class);
        PendingIntent pending_intent_go = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builderProgress.setAutoCancel(true);
        builderProgress.setContentIntent(pending_intent_go);

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




    private NotificationManager getNotificationManager(){
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
