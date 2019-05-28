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

    private int id;//主键
    private int feedId;//订阅id
    private boolean success;//是否请求成功
    private int num;//请求成功获取的文章数目
    private String reason;//请求失败的原因
    private int code;//请求返回码
    private int time;//请求的时间



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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
