package com.ihewro.focus.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/07/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Post extends LitePalSupport {

    private int id;

    private String title;//文章标题

    private Long date;//文章发布日期

    private String summary;//文章简介

    private String content;//文章内容

    @Column(ignore = true)
    private int feedId;

    private String feedName;

    private String url;//文章指向的链接，作为文章的唯一标识符

    @Column(ignore = true)
    private boolean read;//是否已经阅读,true 表示已阅读
    @Column(ignore = true)
    private boolean favorite;//是否收藏，true 表示已收藏

    @Column(ignore = true)
    private boolean notHaveExtractTime;//feedItem 获取的时候，没有时间这个字段


    public Post() {
    }

    public Post(int id, String title, Long date, String summary, String content, int feedId, String feedName, String url, boolean read, boolean favorite, boolean notHaveExtractTime) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.summary = summary;
        this.content = content;
        this.feedId = feedId;
        this.feedName = feedName;
        this.url = url;
        this.read = read;
        this.favorite = favorite;
        this.notHaveExtractTime = notHaveExtractTime;
    }
}
