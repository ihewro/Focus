package com.ihewro.focus.bean;

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
public class Collection extends LitePalSupport {

    private static final String FEED_ITEM = "FEED_ITEM";//文章
    private static final String WEBSITE = "WEBSITE";//网页

    @Column(unique = true)
    private int id;

    private String title;//收藏标题
    private Long date;//收藏文章的发布日期

    private String summary;//文章简介
    private String content;//文章内容


    @Column(unique = true)
    private String url;//文章指向的链接，作为文章的唯一标识符
    private String type;//收藏类型

    private Long time;//收藏时间


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
