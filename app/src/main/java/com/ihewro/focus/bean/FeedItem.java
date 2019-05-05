package com.ihewro.focus.bean;

import android.support.annotation.NonNull;

import com.ihewro.focus.decoration.ISuspensionInterface;
import com.ihewro.focus.util.DateUtil;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/03/13
 *     desc   : 具体的某篇文章的数据结构
 *     version: 1.0
 * </pre>
 */
public class FeedItem extends LitePalSupport implements ISuspensionInterface {
    @Column(unique = true)
    private String iid;//唯一id，标识

    private String title;//文章标题
    private Long date;//文章发布日期
    private String summary;//文章简介
    private String content;//文章内容
    private String feedIid;
    private String feedName;
    private String url;//文章指向的链接

    private boolean read;//是否已经阅读,true 表示已阅读
    private boolean favorite;//是否收藏，true 表示已收藏


    @NonNull
    @Override
    public String toString() {
        return "title" + title + "\n"
                + "date" + DateUtil.getTimeStringByInt(date) + "\n"
                + "summary" + summary + "\n";
    }

    public FeedItem(Long date) {
        this.date = date;
    }

    public FeedItem() {
    }

    public FeedItem(String title, Long date, String summary, String content,String url, boolean read, boolean favorite) {
        this.title = title;
        this.date = date;
        this.summary = summary;
        this.content = content;
        this.url = url;
        this.read = read;
        this.favorite = favorite;
        this.iid = title.hashCode() + url.hashCode() +"";//生成唯一标识符
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFeedIid() {
        return feedIid;
    }

    public void setFeedId(String feedId) {
        this.feedIid = feedId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
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
        return DateUtil.getTimeStringByInt(date);//返回年/月/日格式的日期
    }





    @Override
    public boolean equals(@android.support.annotation.Nullable Object obj) {
        FeedItem feedItem = ((FeedItem)obj);
        assert feedItem != null;
        if (this.url.equals(feedItem.getUrl()) && this.title.equals(feedItem.getTitle())){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getUrl().hashCode() + this.getTitle().hashCode();
    }
}
