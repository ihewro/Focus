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

import java.util.ArrayList;
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
    List<FeedItem> eList = new ArrayList<FeedItem>();
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
            if (url.charAt(url.length() -1) != '/'){//保证末尾是/，否则会出错
                url += "/";
            }
            requestData(url);
        }
    }


    private void requestData(final String url) {
        Retrofit retrofit = HttpUtil.getRetrofit("String", url, 100, 100, 100);
        HttpInterface request = retrofit.create(HttpInterface.class);
        Call<String> call = request.getRSSData();
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
                        Toasty.success(UIUtil.getContext(),"请求成功", Toast.LENGTH_SHORT).show();
                    }else {
                        Toasty.success(UIUtil.getContext(),url+"解析失败", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    ALog.d("请求失败" + response.errorBody());
                    Toasty.info(UIUtil.getContext(),"请求失败" + response.errorBody(), Toast.LENGTH_SHORT).show();
                }
                setUI();
            }

            @SuppressLint("CheckResult")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                ALog.d("请求失败2" + t.toString());
                Toasty.info(UIUtil.getContext(),"请求失败2" + t.toString(), Toast.LENGTH_SHORT).show();
                setUI();
            }
        });
    }


    private void setUI(){
        okNum++;
        if (this.okNum == num){//数据全部请求完毕，
            callback.onFinish(eList);
        }
    }
}
