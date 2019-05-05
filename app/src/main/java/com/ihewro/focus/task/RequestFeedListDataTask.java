package com.ihewro.focus.task;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.blankj.ALog;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.callback.RequestFeedItemListCallback;
import com.ihewro.focus.http.HttpInterface;
import com.ihewro.focus.http.HttpUtil;
import com.ihewro.focus.util.FeedParser;
import com.ihewro.focus.util.UIUtil;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
public class RequestFeedListDataTask {


    private List<Feed> feedList = new ArrayList<>();
    LinkedHashSet<FeedItem> eList = new LinkedHashSet<>();//使用set保证不重复
    private RequestFeedItemListCallback callback;


    private int num;//总共需要请求的数目
    private int okNum = 0;//已经请求的数目


    public RequestFeedListDataTask(List<Feed> feedList, RequestFeedItemListCallback callback) {
        this.feedList = feedList;
        this.callback = callback;
    }



    public void run(){

        List<Feed> feedList = LitePal.findAll(Feed.class);
        this.num = feedList.size();

        for (int i = 0; i <feedList.size() ; i++) {
            Feed temp = feedList.get(i);
            String url = temp.getUrl();
            requestData(url);
        }
    }


    private void requestData(String url) {
        final String originUrl = url;
        if (url.charAt(url.length() -1) == '/'){//去掉末尾的/
            url = url.substring(0,url.length()-1);
        }



        Call<String> call;
        //比如https://www.dreamwings.cn/feed，with值就是feed,url最后就是根域名https://www.dreamwings.cn
        int pos1 = url.lastIndexOf("/");
        if (pos1 == 7 || pos1 == 6){//说明这/是协议头的,比如https://www.ihewro.com
            url = url + "/";
            Retrofit retrofit = HttpUtil.getRetrofit("String", url, 100, 100, 100);
            HttpInterface request = retrofit.create(HttpInterface.class);
            call = request.getRSSData();
        }else {
            String with = url.substring(pos1+1);
            url = url.substring(0,pos1) + "/";
            ALog.d("根域名" + url + "参数" + with);
            Retrofit retrofit = HttpUtil.getRetrofit("String", url, 100, 100, 100);
            HttpInterface request = retrofit.create(HttpInterface.class);
            call = request.getRSSDataWith(with);
        }

        call.enqueue(new Callback<String>() {
            @SuppressLint("CheckResult")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {

                    assert response.body() != null;
                    Feed feed = FeedParser.parseStr2Feed(response.body());
//                    ALog.dTag("feed233", feed);
                    //feed更新到当前的时间流中。
                    if (feed!=null){
                        eList.addAll(feed.getFeedItemList());
                        Toasty.success(UIUtil.getContext(),originUrl+"请求成功", Toast.LENGTH_SHORT).show();
                    }else {
                        Toasty.success(UIUtil.getContext(),originUrl+"解析失败", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    try {
                        ALog.d("请求失败" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toasty.info(UIUtil.getContext(),"请求失败" + response.errorBody(), Toast.LENGTH_SHORT).show();
                }
                mergeOldItems(originUrl);
                setUI(true);
            }

            @SuppressLint("CheckResult")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                ALog.d("请求失败2" + t.toString());
                Toasty.info(UIUtil.getContext(),"请求失败2" + t.toString(), Toast.LENGTH_SHORT).show();
                mergeOldItems(originUrl);
                setUI(false);
            }
        });
    }

    private void mergeOldItems(String url){
        //将本地数据库的内容合并到列表中
        //找到当前feed url 本地数据库的内容
        List<Feed> tempList = LitePal.where("url = ?" ,url).find(Feed.class);
        if (tempList.size()>0){
            Feed temp = tempList.get(0);
            List<FeedItem> tempFeedItemList = LitePal.where("feedname = ?",temp.getName()).find(FeedItem.class);
            ALog.d("本地数据库信息url" + url + "订阅名称为"+ temp.getName() + "文章数目" + tempFeedItemList.size());
            eList.addAll(tempFeedItemList);
        }
    }


    private void setUI(boolean flag){
        okNum++;
        if (this.okNum == num){//数据全部请求完毕，
            //对数据排序
            List<FeedItem> list = new ArrayList<>(eList);
            Collections.sort(list, new Comparator<FeedItem>() {
                @Override
                public int compare(FeedItem t0, FeedItem t1) {
                    if (t0.getDate() < t1.getDate()){
                        return 1;
                    }else if (t0.getDate() > t1.getDate()){
                        return -1;
                    }else {
                        return 0;
                    }
                }
            });
            callback.onFinish(list);
        }
    }
}
