package com.ihewro.focus.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blankj.ALog;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.Message;
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
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
public class RequestFeedListDataTask extends AsyncTask<Feed, Integer, Message> {


    private RequestDataCallback callback;


    RequestFeedListDataTask(RequestDataCallback callback) {
        this.callback = callback;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Message doInBackground(Feed... feeds) {
        Feed feed = feeds[0];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.e("多线程任务！！","任务开始"+feed.getUrl()+dateFormat.format(new Date(System.currentTimeMillis())));
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
                    EventBus.getDefault().post(new EventMessage(EventMessage.EDIT_FEED_FOLDER_NAME));
                    return new Message(true,feed2.getFeedItemList());
                }else {
                    //当前解析的内容为空
                    //TODO: 应该是另外一种标识表示数据为空，请求是成功的
                    return new Message(false);
                }
            }else {
                feed.setErrorGet(true);
                feed.save();
                try {
                    ALog.d("请求失败" + response.code() + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new Message(false);
            }
        }  catch (IOException e) {
            feed.setErrorGet(true);
            feed.save();
            e.printStackTrace();
        }

        return new Message(false);
    }






    @Override
    protected void onPostExecute(Message message) {
        super.onPostExecute(message);
        //这个地方不需要返回了，都已经保存到本地数据库了
        callback.onSuccess(message.getFeedItemList());
    }
}
