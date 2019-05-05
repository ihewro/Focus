package com.ihewro.focus.util;

import android.util.Log;
import android.util.Xml;

import com.blankj.ALog;
import com.google.common.base.Strings;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;

import org.jsoup.Jsoup;
import org.litepal.LitePal;
import org.litepal.exceptions.LitePalSupportException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/07
 *     desc   : 解析xml工具
 *     version: 1.0
 * </pre>
 */
public class FeedParser {
    private static final String RSS = "rss";
    private static final String ITEM = "item";
    private static final String CHANNEL = "channel";
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String PUB_DATE = "pubDate";
    private static final String DESC = "description";
    private static final String CONTENT = "content:encoded";
    private static final String LAST_BUILD_DATE = "lastBuildDate";

    private static String feedUrl;


    /**
     * 从字符串解析出feed
     * @param xmlStr
     * @return[
     */
    public static Feed parseStr2Feed(String xmlStr,String url) {
        if (Strings.isNullOrEmpty(xmlStr)) {
            return null;
        }

        feedUrl = url;

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(xmlStr));
            parser.nextTag();

            return readRssForFeed(parser);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static Feed readRssForFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, RSS);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals(CHANNEL)) {
                return readChannelForFeed(parser);
            } else {
                skip(parser);
            }
        }
        return null;
    }


    /**
     * 从channel标志开始构建feed类。
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static Feed readChannelForFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        Feed feed = new Feed();
        feed.setUrl(feedUrl);
        List<FeedItem> feedItems = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, CHANNEL);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals(TITLE)) {
                feed.setName(readTitle(parser));
            } else if (name.equals(LINK)) {
                feed.setLink(readLink(parser));
            } else if (name.equals(LAST_BUILD_DATE)) {
                feed.setTime(readLastBuildDate(parser));
            } else if (name.equals(DESC)) {
                feed.setDesc(readDesc(parser));
            }else if (name.equals(ITEM)) {
                //获取当前feed最新的文章列表
                FeedItem feedItem = readItemForArticle(parser);
                feedItems.add(feedItem);
            }
            else {
                skip(parser);
            }
        }

//写在请求结束的那个地方合并旧数据
//        feedItems.addAll(feed.getFeedItemList());//把新的数据和旧的数据合并
//        feed.setFeedItemList(new ArrayList<>(new LinkedHashSet<>(feedItems)));
        feed.setFeedItemList(feedItems);
        feed.setWebsiteCategoryName("");
//        feed.setTotalCount(0L);
//        feed.setUnreadCount(0L);

        //给feed下面所有feedItem设置feedName和feedId;
        //获取当前feed的iid

        List<Feed> tempFeeds = LitePal.where("url = ?",feed.getUrl()).find(Feed.class);
        String feedIid = "";
        if (tempFeeds.size() <= 0){
            ALog.d("出现未订阅错误"+feed.getUrl());
        }else {
            feedIid = tempFeeds.get(0).getIid();
        }
        for (int i =0;i<feed.getFeedItemList().size();i++){
            feed.getFeedItemList().get(i).setFeedName(feed.getName());
            feed.getFeedItemList().get(i).setFeedId(feedIid);
            try{
                feed.getFeedItemList().get(i).saveThrows();//当前feed存储数据库
            }catch (LitePalSupportException exception){
                ALog.d("数据重复不会插入");
                //此时要对feedItem进行状态字段的恢复，读取数据的状态
                FeedItem temp = LitePal.where("iid = ?",feed.getFeedItemList().get(i).getIid()).limit(1).find(FeedItem.class).get(0);
                feed.getFeedItemList().get(i).setRead(temp.isRead());
                feed.getFeedItemList().get(i).setFavorite(temp.isFavorite());
//                feed.getFeedItemList().get(i).setDate(temp.getDate());//有的feedItem 源地址中 没有时间，所以要恢复第一次加入数据库中的时间
            }
        }

        return feed;
    }


    /**
     * 从XmlPullParser 解析出feedItem
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private static FeedItem readItemForArticle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, ITEM);
        String title = null;
        String link = null;
        String pubDate = null;
        String description = null;
        String content = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case TITLE:
                    title = readTitle(parser);
                    break;
                case LINK:
                    link = readLink(parser);
                    break;
                case PUB_DATE:
                    pubDate = readPubDate(parser);
                    break;
                case DESC:
                    description = readDesc(parser);
                    break;
                case CONTENT:
                    content = readContent(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        ALog.d("item名称：" + title + "时间为" + pubDate);
        return new FeedItem(title, DateUtil.date2TimeStamp(pubDate),description,content,link, false, false);
    }


    public static void skip(XmlPullParser parser) throws IOException, XmlPullParserException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TITLE);
        String title = Jsoup.parse(readText(parser)).text();
        parser.require(XmlPullParser.END_TAG, null, TITLE);

        return title;
    }

    private static String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, LINK);
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, LINK);
        return link;
    }

    private static String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, PUB_DATE);
        String pubData = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, PUB_DATE);
        if (pubData==null){
            ALog.d("？？？null");
            pubData ="Tue, 02 Apr 2019 07:43:34 +0000";
        }
        ALog.d(pubData);
        return pubData;
    }

    private static String readDesc(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, DESC);
        String desc = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, DESC);
        return desc;
    }

    private static String readContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, CONTENT);
        String content = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, CONTENT);
        return content;
    }

    private static Long readLastBuildDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, LAST_BUILD_DATE);
        String dateStr = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, LAST_BUILD_DATE);

        return DateUtil.date2TimeStamp(dateStr);
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
