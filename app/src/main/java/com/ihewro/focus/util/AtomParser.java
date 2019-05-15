package com.ihewro.focus.util;

import com.blankj.ALog;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;

import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.ihewro.focus.util.FeedParser.HandleFeed;
import static com.ihewro.focus.util.FeedParser.readTagByTagName;
import static com.ihewro.focus.util.FeedParser.skip;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/15
 *     desc   : 解析ATOM格式
 *     version: 1.0
 * </pre>
 */
public class AtomParser {

    //公用部分
    private static final String LINK = "link";
    private static final String TITLE = "title";

    //feed信息
    public static final String FEED = "feed";
    private static final String SUBTITLE = "subtitle";
    private static final String UPDATED = "updated";

    //item信息
    private static final String ENTRY = "entry";
    private static final String SUMMARY = "summary";
    private static final String CONTENT = "content";
    private static final String PUBLISHED = "published";




    /**
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static Feed readFeedForFeed(XmlPullParser parser,String url) throws XmlPullParserException, IOException {
        Feed feed = new Feed();
        feed.setUrl(url);
        List<FeedItem> feedItems = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, FEED);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            switch (name) {
                case TITLE:
                    feed.setName(readTitle(parser));
                    break;
                case LINK:
                    feed.setLink(readLink(parser));
                    break;
                case UPDATED:
                    feed.setTime(readLastBuildDate(parser));
                    break;
                case SUBTITLE:
                    feed.setDesc(readDesc(parser));
                    break;
                case ENTRY:
                    //获取当前feed最新的文章列表
                    FeedItem feedItem = readEntryForFeedItem(parser);
                    feedItems.add(feedItem);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        feed.setFeedItemList(feedItems);
        feed.setWebsiteCategoryName("");
        return HandleFeed(feed);
    }

    /**
     * 从XmlPullParser 解析出feedItem
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private static FeedItem readEntryForFeedItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, ENTRY);
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
                case PUBLISHED:
                    pubDate = readPubDate(parser);
                    break;
                case SUMMARY:
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


    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        return Jsoup.parse(readTagByTagName(parser,TITLE)).text();
    }

    private static String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readTagByTagName(parser,LINK);
    }

    private static String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        String pubData = readTagByTagName(parser,PUBLISHED);
        if (pubData==null){
            ALog.d("？？？null");
            pubData ="Tue, 02 Apr 2019 07:43:34 +0000";
        }
        ALog.d(pubData);
        return pubData;
    }

    private static String readDesc(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readTagByTagName(parser,SUMMARY);
    }

    private static String readContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readTagByTagName(parser,CONTENT);
    }

    private static Long readLastBuildDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        String dateStr = readTagByTagName(parser,UPDATED);
        return DateUtil.date2TimeStamp(dateStr);
    }




}
