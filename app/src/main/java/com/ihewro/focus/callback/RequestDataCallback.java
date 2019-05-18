package com.ihewro.focus.callback;

import com.ihewro.focus.bean.FeedItem;

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
public interface RequestDataCallback {

    void onBegin();//代码执行的开始

    void onStart();//开始工作，请求数据

    void onProgress(int progress);

    void onSuccess(List<FeedItem> list);//有网络请求的结束

    void onFinish(List<FeedItem> list);//没有网络请求的结束
}
