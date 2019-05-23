package com.ihewro.focus.bean;

import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/23
 *     desc   : 网络请求信息表
 *     version: 1.0
 * </pre>
 */
public class FeedRequest extends LitePalSupport {
    private int id;
    private int feedId;//订阅id
    private boolean success;//是否请求成功
    private int num;//请求的数目




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
        this.feedId = feedId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
