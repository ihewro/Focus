package com.ihewro.focus.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ihewro.focus.decoration.ISuspensionInterface;
import com.ihewro.focus.util.DateUtil;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/06/23
 *     desc   : 收藏表
 *     version: 1.0
 * </pre>
 */
public class Collection extends LitePalSupport implements MultiItemEntity, ISuspensionInterface {


    public static final int FEED_ITEM = 794;//文章
    public static final int WEBSITE = 580;//网页

    @Column(unique = true)
    private int id;

    private String title;//收藏标题
    private Long date;//收藏文章的发布日期

    private String summary;//文章简介
    private String content;//文章内容
    private String feedName;//订阅名称


    @Column(unique = true)
    private String url;//文章指向的链接，作为文章的唯一标识符

    private int itemType;//收藏类型


    private Long time;//收藏时间



    public Collection() {
    }

    //收藏文章的构造器
    public Collection(String title, String feedName, Long date, String summary, String content, String url, int itemType, Long time) {
        this.title = title;
        this.feedName = feedName;
        this.date = date;
        this.summary = summary;
        this.content = content;
        this.url = url;
        this.itemType = itemType;
        this.time = time;
    }


    //收藏网站的构造器
    public Collection(String title, String url, int itemType, Long time) {
        this.title = title;
        this.url = url;
        this.itemType = itemType;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }



    @Override
    public int getItemType() {
        return itemType;
    }

    public String getFeedName() {
        return feedName;
    }

    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    @Override
    public boolean isShowSuspension() {
        return true;
    }

    @Override
    public String getSuspensionTag() {
        return DateUtil.getTimeStringByInt(time);//返回年/月/日格式的日期
    }

    @Override
    public boolean save() {
        return super.save();
    }
}
