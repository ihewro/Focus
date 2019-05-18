package com.ihewro.focus.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blankj.ALog;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.callback.RequestDataCallback;
import com.ihewro.focus.callback.RequestFeedItemListCallback;
import com.ihewro.focus.http.HttpInterface;
import com.ihewro.focus.http.HttpUtil;
import com.ihewro.focus.util.FeedParser;
import com.ihewro.focus.util.UIUtil;
import com.ihewro.focus.view.FeedsLoadingPopupView;
import com.ihewro.focus.view.FilterPopupView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.XPopupCallback;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class RequestFeedListDataTask extends AsyncTask<String, Integer, Integer> {


    private List<Feed> feedList = new ArrayList<>();
    private LinkedHashSet<FeedItem> eList = new LinkedHashSet<>();//使用set保证不重复
    private RequestDataCallback callback;
    private List<String> errorFeedIdList = new ArrayList<>();

    private FeedsLoadingPopupView popupView;
    private View view;



    private int num;//总共需要请求的数目
    private int okNum = 0;//已经请求的数目
    private boolean isForce;//isForce为true的时候表明不是一开始打开页面，所以此时的刷新请求数据必须请求
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private int orderChoice = FilterPopupView.ORDER_BY_NEW;
    private int filterChoice = FilterPopupView.SHOW_ALL;
    //查询用户设置，如果开启了快速启动，则不请求数据，直接显示本地数据
    private boolean flag;

    RequestFeedListDataTask(int oderChoice, int filterChoice, Activity activity, View view, boolean flag, List<Feed> feedList, RequestDataCallback callback) {
        this.activity = activity;
        this.feedList = feedList;
        this.callback = callback;
        this.isForce = !flag;
        this.view = view;
        this.orderChoice = oderChoice;
        this.filterChoice = filterChoice;

    }


    public void run(){
        callback.onBegin();

        errorFeedIdList.clear();
        this.num = feedList.size();

        String value = UserPreference.queryValueByKey(UserPreference.USE_INTERNET_WHILE_OPEN, "0");
        if (value != null && value.equals("1")){//强制刷新数据
            flag = true;
        }else {
            flag = false;
        }
        this.okNum = 0;
        if (num>0){//请求总数大于1才会进行请求
            if (!flag && !isForce){
                this.okNum = num;
                setUI();
            }else {
                callback.onStart();
                //显示进度通知
                ShowProgress();
                for (int i = 0; i <feedList.size() ; i++) {
                    Feed temp = feedList.get(i);
                    String url = temp.getUrl();
                    ALog.d("超时时间" + temp.getTimeout());
                    requestData(temp,i);
                }
            }
        }else {//也必须返回一个空数组
            callback.onFinish(new ArrayList<FeedItem>());
        }
    }
    /**
     *
     * @param index 已经完成了的任务个数
     */
    private void updateTextInAlter(int index){
        if (isForce || flag){//当有网络请求的时候，才有进度通知条
            if (index >= num){
                if (popupView!=null && popupView.isShow()){

                    popupView.getProgressBar().setProgress(100);
                    popupView.getProgressInfo().setText("任务完成");
                    popupView.getCircle().setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            popupView.dismiss();
                        }
                    },1000); // 延时1秒
                }
            }else {//这个地方结束了第index个请求（从1计数），开始第index+1个请求
                ALog.d("怎么回事吗？？");
                if (popupView!=null && popupView.isShow()){
                    ALog.d("怎么回事吗？？2333");
                    int progress = (int) ((index*1.0)/num *100);
                    popupView.getProgressBar().setProgress(progress);
                    popupView.getProgressInfo().setText(returnProgressText(index));
                }
            }
        }
    }

    /**
     * 该代码函数为子线程，禁止操作UI内容
     * @param feed
     * @param pos
     */
    private void requestData(final Feed feed,final int pos) {
        String url = feed.getUrl();
        int timeout = feed.getTimeout();

        if (timeout == 0){
            timeout = Feed.DEFAULT_TIMEOUT;//默认值
        }
        final String originUrl = url;

        //判断RSSHUB的源地址，替换成现在的地址
        for (int i = 0; i< GlobalConfig.rssHub.size(); i++){
            if (url.contains(GlobalConfig.rssHub.get(i))){
                url = url.replace(GlobalConfig.rssHub.get(i),UserPreference.getRssHubUrl());
                break;
            }
        }

        if (url.charAt(url.length() -1) == '/'){//去掉末尾的/
            url = url.substring(0,url.length()-1);
        }
        Call<String> call;
        //比如https://www.dreamwings.cn/feed，with值就是feed,url最后就是根域名https://www.dreamwings.cn
        int pos1 = url.lastIndexOf("/");
        if (pos1 == 7 || pos1 == 6){//说明这/是协议头的,比如https://www.ihewro.com
            url = url + "/";
            Retrofit retrofit = HttpUtil.getRetrofit("String", url, timeout, timeout, timeout);
            HttpInterface request = retrofit.create(HttpInterface.class);
            call = request.getRSSData();
        }else {
            String with = url.substring(pos1+1);
            url = url.substring(0,pos1) + "/";
            ALog.d("根域名" + url + "参数" + with);
            Retrofit retrofit = HttpUtil.getRetrofit("String", url, timeout, timeout, timeout);
            HttpInterface request = retrofit.create(HttpInterface.class);
            call = request.getRSSDataWith(with);
        }

        try {
            Response<String> response = call.execute();
            if (response != null && response.isSuccessful()){
                feed.setErrorGet(false);
                feed.save();
                Feed feed2 = FeedParser.HandleFeed(FeedParser.parseStr2Feed(response.body(),originUrl));
//                    ALog.dTag("feed233", feed);
                //feed更新到当前的时间流中。
                if (feed2!=null){
                    eList.addAll(feed2.getFeedItemList());
                }else {
                    //当前解析的内容为空你
                    ALog.d("当前解析的内容为空");
                }
                EventBus.getDefault().post(new EventMessage(EventMessage.EDIT_FEED_FOLDER_NAME));
            }else {
                feed.setErrorGet(true);
                feed.save();
                try {
                    ALog.d("请求失败" + response.code() + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setUI();
    }

    private void mergeOldItems(String url){
        //将本地数据库的内容合并到列表中
        //找到当前feed url 本地数据库的内容
        List<Feed> tempList = LitePal.where("url = ?" ,url).find(Feed.class);
        if (tempList.size()>0){
            Feed temp = tempList.get(0);
            List<FeedItem> tempFeedItemList = LitePal.where("feedid = ?", String.valueOf(temp.getId())).find(FeedItem.class);
            ALog.d("本地数据库信息url" + url + "订阅名称为"+ temp.getName() + "文章数目" + tempFeedItemList.size());
            eList.addAll(tempFeedItemList);
        }
    }


    /**
     * 更新UI界面
     */
    private void setUI(){
        okNum++;

        if (isForce || flag){//有网络请求才会修改进度
            publishProgress(okNum);
        }

        //要把这个list传到MainActivity中去，因为这个保存是异步的，那边更新来不及更新
        EventBus.getDefault().post(new EventMessage(EventMessage.FEED_PULL_DATA_ERROR));

        if (this.okNum >= num){//数据全部请求完毕
            //合并旧数据
            for (int i = 0; i < num;i++){
                Feed temp = feedList.get(i);
                String url = temp.getUrl();
                mergeOldItems(url);
            }
            ALog.d("开始对数据排序");
            List<FeedItem> list = new ArrayList<>(eList);

            //对数据进行过滤
            if (filterChoice == FilterPopupView.SHOW_STAR){
                Iterator<FeedItem> sListIterator = list.iterator();
                while (sListIterator.hasNext()) {
                    FeedItem feedItem = sListIterator.next();
                    if (!feedItem.isFavorite()) {//非收藏数据删除
                        sListIterator.remove();
                    }
                }
            }else if (filterChoice == FilterPopupView.SHOW_UNREAD){
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
            if (orderChoice == FilterPopupView.ORDER_BY_NEW){
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

            ALog.d("结束数据按时间排序");
//            updateTextInAlter(9999);
            if (flag || isForce){
                callback.onSuccess(list);
            }else {
                callback.onFinish(list);
            }
        }
    }

    private void ShowProgress(){
        if (popupView == null) {
            popupView = (FeedsLoadingPopupView) new XPopup.Builder(activity)
                    .atView(view)
                    .setPopupCallback(new XPopupCallback() {
                        @Override
                        public void onShow() {
                        }

                        @Override
                        public void onDismiss() {
                        }
                    })
                    .dismissOnBackPressed(false) // 按返回键是否关闭弹窗，默认为true
                    .autoDismiss(false) // 操作完毕后是否自动关闭弹窗，默认为true；比如点击ConfirmPopup的确认按钮，默认自动关闭；如果为false，则不会关闭
                    .dismissOnTouchOutside(false) // 点击外部是否关闭弹窗，默认为true
                    .asCustom(new FeedsLoadingPopupView(activity));
        }
        popupView.show();

        //初始化
        popupView.getProgressBar().setProgress(0);
        popupView.getProgressInfo().setText(returnProgressText(0));
    }

    private String returnProgressText(int index){
        int order = index + 1;
        return  "正在请求第" + order +"/" + num+"个订阅数据："+feedList.get(index).getName();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... strings) {
        run();
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        //更新进度条
        updateTextInAlter(values[0]);

        //调用service的回调函数
        callback.onProgress((int) ((values[0]*1.0/num)*100));
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }
}
