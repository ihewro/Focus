package com.ihewro.focus.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/26
 *     desc   : 收藏表，专门单独的是为了退订的时候收藏文章能保留
 *     version: 1.0
 * </pre>
 */
public class StarItem extends FeedItem {


    @Column(unique = true)
    private int feedItemId;


    public StarItem() {
    }

    public StarItem(FeedItem feedItem) {
        super(feedItem.getTitle(),feedItem.getDate(), feedItem.getSummary(), feedItem.getContent(),feedItem.getFeedId(),feedItem.getFeedName(), feedItem.getUrl(), feedItem.isRead(), feedItem.isFavorite());
        this.feedItemId = feedItem.getId();
    }

    public int getFeedItemId() {
        return feedItemId;
    }

    public void setFeedItemId(int feedItemId) {
        this.feedItemId = feedItemId;
    }
}
