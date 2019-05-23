package com.ihewro.focus.callback;

import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.Message;

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

    void onSuccess(List<FeedItem> feedItemList);//有网络请求的结束

}
