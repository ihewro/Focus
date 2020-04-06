package com.ihewro.focus.util;

import com.blankj.ALog;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.Message;
import com.ihewro.focus.bean.UserPreference;

import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        String netFeedName = "";

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            switch (name) {
                case TITLE:
                    netFeedName = readTitle(parser);
                    break;
                case LINK:
                    Message message = readFeedLink(parser);
                    if (message.isFlag()){//是真正的link信息
                        feed.setLink(message.getData());
                    }else {
                        skip(parser);
                    }
                    break;
                case UPDATED:
                    feed.setTime(readLastBuildDate(parser));
                    break;
                case SUBTITLE:
                    feed.setDesc(readSubtitle(parser));
                    break;
                case ENTRY:
                    //获取当前feed最新的文章列表
                    FeedItem feedItem = readEntryForFeedItem(parser);
                    if (feedItem.isNotHaveExtractTime()){
                        feedItem.setDate(feedItem.getDate() - feedItems.size() *1000);//越往后的时间越小，保证前面的时间大，后面的时间小
                    }
                    feedItems.add(feedItem);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        if(UserPreference.queryValueByKey(UserPreference.AUTO_SET_FEED_NAME,"0").equals("1")){//自动通过网络设置名字
            feed.setName(netFeedName);//因为在线请求的时候没有拉取Titile这个字段
        }
        feed.setFeedItemList(feedItems);
        feed.setWebsiteCategoryName("");
        return feed;
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
        String updateDate = null;
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
                    link = readItemLink(parser);
                    break;
                case PUBLISHED:
                    pubDate = readPubDate(parser);
                    break;
                case UPDATED:
                    updateDate = readUpdateDate(parser);
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
        ALog.d("item名称：" + title + "时间为" + pubDate + "地址为" + link);
        pubDate = pubDate == null ? updateDate : pubDate;
        FeedItem feedItem = new FeedItem(title, DateUtil.date2TimeStamp(pubDate), description, content, link, false, false);
        if (pubDate == null) {
            feedItem.setNotHaveExtractTime(true);
        }
        return feedItem;
    }


    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        return Jsoup.parse(readTagByTagName(parser,TITLE)).text();
    }


    private static Message readFeedLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String url = "";
        parser.require(XmlPullParser.START_TAG, null, LINK);
        boolean is_false = false;
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            switch (parser.getAttributeName(i)) {
                case "href":
                    url = parser.getAttributeType(i);
                    break;
                case "rel":
                    //直接返回，这个地址是不需要的
                    is_false = true;
                    break;
            }
            if (is_false){
                break;
            }
        }
        if (is_false){
            return new Message(false);
        }else {
            parser.next();
            return new Message(true,url);
        }
    }


    private static String readItemLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String url = "";
        parser.require(XmlPullParser.START_TAG, null, LINK);
        boolean isHaveTargetValue = false;
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            switch (parser.getAttributeName(i)) {
                case "href":
                    url = parser.getAttributeValue(i);
                    isHaveTargetValue = true;
                    break;
            }
            if (isHaveTargetValue){
                break; //退出
            }
        }
        parser.next();
        return url;
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

    private static String readUpdateDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        String dateStr = readTagByTagName(parser, UPDATED);
        return dateStr;
    }

    private static String readSubtitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readTagByTagName(parser,SUBTITLE);
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
