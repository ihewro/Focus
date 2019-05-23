package com.ihewro.focus.bean;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/05
 *     desc   : 一个简单的信息类，作用是作为返回参数，返回多个信息
 *     version: 1.0
 * </pre>
 */
public class Message {
    boolean flag;
    String data;
    List<FeedItem> feedItemList;


    public Message(boolean flag, List<FeedItem> feedItemList) {
        this.flag = flag;
        this.feedItemList = feedItemList;
    }


    public List<FeedItem> getFeedItemList() {
        return feedItemList;
    }

    public void setFeedItemList(List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
    }

    public Message(boolean flag, String data) {
        this.flag = flag;
        this.data = data;
    }

    public Message(boolean b) {
        this.flag = b;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "flag = " + flag + "\n" +
                "data" + data;
    }
}
