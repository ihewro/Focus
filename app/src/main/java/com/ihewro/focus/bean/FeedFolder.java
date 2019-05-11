package com.ihewro.focus.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/06
 *     desc   : 订阅的文件夹
 *     version: 1.0
 * </pre>
 */
public class FeedFolder extends LitePalSupport {

    private int id;//使用int型主键

    @Column(unique = true, defaultValue = "")
    private String name;//文件夹名称,且是唯一的，订阅可以重名，但是文件夹不能重名
    @Column(defaultValue = "1.0")
    private double orderValue;//顺序权限，用来排序的

    @Column(ignore = true)
    private int notReadNum;

    @Column(ignore = true)
    private List<Feed> feedList;

    public FeedFolder(String name) {
        this.name = name;
    }


    public FeedFolder() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(double orderValue) {
        this.orderValue = orderValue;
    }

    public int getNotReadNum() {
        return notReadNum;
    }

    public void setNotReadNum(int notReadNum) {
        this.notReadNum = notReadNum;
    }

    public List<Feed> getFeedList() {
        return feedList;
    }

    public void setFeedList(List<Feed> feedList) {
        this.feedList = feedList;
    }
}
