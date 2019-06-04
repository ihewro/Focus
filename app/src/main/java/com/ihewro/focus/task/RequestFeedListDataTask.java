package com.ihewro.focus.task;

import android.os.AsyncTask;
import android.util.Log;

import com.blankj.ALog;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.FeedRequest;
import com.ihewro.focus.bean.FeedRequire;
import com.ihewro.focus.bean.Message;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.callback.RequestDataCallback;
import com.ihewro.focus.http.HttpInterface;
import com.ihewro.focus.http.HttpUtil;
import com.ihewro.focus.util.DateUtil;
import com.ihewro.focus.util.FeedParser;
import com.ihewro.focus.util.UIUtil;

import org.litepal.LitePal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
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

        boolean flag = true;
        if (!UIUtil.isWifi() && UserPreference.queryValueByKey(UserPreference.notWifi,"0").equals("1")){
            flag = false;//当前不是wifi并且开启了仅在WiFi下请求数据
        }

        if (flag){
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
                    Feed feed2 = FeedParser.HandleFeed(feed.getId(),response,FeedParser.parseStr2Feed(response.body(),originUrl));

                    //feed更新到当前的时间流中。
                    if (feed2!=null){
                        return new Message(true,feed2.getFeedItemList());
                    }else {
                        //当前解析的内容为空
                        return new Message(false);
                    }
                }else {

                    String reason;
                    if (response.errorBody()!=null){
                        reason = response.errorBody().string();
                        //编码转换
                        //获取xml文件的编码
                        String encode = "UTF-8";//默认编码
                        reason = new String(reason.getBytes("ISO-8859-1"),encode);
                    }else {
                        reason = "无错误报告";
                        ALog.d("出问题了！");
                    }

                    FeedRequest feedRequire = new FeedRequest(feed.getId(),false,0,reason,response.code(), DateUtil.getNowDateRFCInt());
                    feedRequire.save();
                    feed.setErrorGet(true);
                    feed.save();


                    try {
                        ALog.d("请求失败" + response.code() + response.errorBody().string());
                    } catch (IOException e) {
                        ALog.d("请求失败");
                        e.printStackTrace();
                    }
                    return new Message(false);
                }
            }  catch (IOException e) {
                ALog.d("请求失败");
                ALog.d(e.getMessage());
                FeedRequest feedRequire = new FeedRequest(feed.getId(),false,0,e.getMessage(),-1, DateUtil.getNowDateRFCInt());
                feedRequire.save();

                feed.setErrorGet(true);
                feed.save();
                e.printStackTrace();
            }
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
