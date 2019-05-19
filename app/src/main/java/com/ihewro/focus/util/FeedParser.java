package com.ihewro.focus.util;

import android.util.Xml;

import com.blankj.ALog;
import com.google.common.base.Strings;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.UserPreference;

import org.jsoup.Jsoup;
import org.litepal.LitePal;
import org.litepal.exceptions.LitePalSupportException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.ihewro.focus.util.AtomParser.FEED;
import static com.ihewro.focus.util.AtomParser.readFeedForFeed;

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

    //feed 与 item公用部分
    private static final String LINK = "link";
    private static final String DESC = "description";
    private static final String TITLE = "title";


    //feed信息
    private static final String RSS = "rss";
    private static final String CHANNEL = "channel";
    private static final String LAST_BUILD_DATE = "lastBuildDate";

    //item信息
    private static final String ITEM = "item";
    private static final String CONTENT = "content:encoded";
    private static final String PUB_DATE = "pubDate";

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

            while (parser.next() != XmlPullParser.END_TAG){
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals(RSS)){//RSS格式
                    return readRssForFeed(parser);

                }else if (name.equals(FEED)){
                    return readFeedForFeed(parser,url);
                }
            }
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
        String netFeedName = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            switch (name) {
                case TITLE:
                    //使用用户设置的title
                    netFeedName = readTitle(parser);
                    break;
                case LINK:
                    feed.setLink(readLink(parser));
                    break;
                case LAST_BUILD_DATE:
                    feed.setTime(readLastBuildDate(parser));
                    break;
                case DESC:
                    feed.setDesc(readDesc(parser));
                    break;
                case ITEM:
                    //获取当前feed最新的文章列表
                    FeedItem feedItem = readItemForFeedItem(parser);
                    if (feedItem.isNotHaveExtractTime()){
                        feedItem.setDate(feedItem.getDate() - feedItems.size() *1000);//越往后的时间越小，保证前面的时间大，后面的时间小
                    }
                    feedItems.add(feedItem);//加到开头
                    break;
                default:
                    skip(parser);
                    break;
            }

            ALog.d();
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
    private static FeedItem readItemForFeedItem(XmlPullParser parser) throws IOException, XmlPullParserException {
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
        FeedItem feedItem = new FeedItem(title, DateUtil.date2TimeStamp(pubDate),description,content,link, false, false);

        if (pubDate == null){
            feedItem.setNotHaveExtractTime(true);
        }
        return  feedItem;
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
        return Jsoup.parse(readTagByTagName(parser,TITLE)).text();
    }

    private static String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readTagByTagName(parser,LINK);
    }

    private static String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        String pubData = readTagByTagName(parser,PUB_DATE);
        //TODO:对没有时间的feedItem 进行特殊处理
        if (pubData==null){
            pubData =DateUtil.getNowDateRFCStr();
        }
        ALog.d("没有时间！");
        ALog.d(pubData);
        return pubData;
    }

    private static String readDesc(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readTagByTagName(parser,DESC);
    }


    private static String readContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readTagByTagName(parser,CONTENT);
    }

    private static Long readLastBuildDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        String dateStr = readTagByTagName(parser,LAST_BUILD_DATE);
        return DateUtil.date2TimeStamp(dateStr);
    }


    /**
     * 根据tag名称获取tag内部的数据
     * @param parser
     * @param tagName
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static String readTagByTagName(XmlPullParser parser,String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, tagName);
        String dateStr = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, tagName);
        return dateStr;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }



    /**
     * 获取到的数据与本地数据库的数据进行比对，重复的则恢复状态信息，不重复则入库
     * @param feed
     * @return
     */
    public static Feed HandleFeed(Feed feed){
        if (feed !=null){
            //给feed下面所有feedItem设置feedName和feedId;
            //获取当前feed的iid
            List<Feed> tempFeeds = LitePal.where("url = ?",feed.getUrl()).find(Feed.class);
            int feedId = 0;
            if (tempFeeds.size() <= 0){
                ALog.d("出现未订阅错误"+feed.getUrl());//我们获取feedItem内容是从找数据库的feed，所以不可能feedItem中的feed url 不在数据库中欧冠
            }else {
                feedId = tempFeeds.get(0).getId();
            }
            feed.setId(feedId);
            if(UserPreference.queryValueByKey(UserPreference.AUTO_SET_FEED_NAME,"0").equals("0")){//没有选择，自动设置会手动设置name
                feed.setName(tempFeeds.get(0).getName());//因为在线请求的时候没有拉取Titile这个字段
            }

            //        tempFeeds.get(0).setLink(feed.getLink());
//        tempFeeds.get(0).setDesc(feed.getDesc());
//        tempFeeds.get(0).save();
            feed.update(feedId);
//        feed.save();//更新数据！

            //给feed下所有feedItem绑定feed信息
            for (int i =0;i<feed.getFeedItemList().size();i++){
                feed.getFeedItemList().get(i).setFeedName(feed.getName());
                feed.getFeedItemList().get(i).setFeedId(feedId);
                try{
                    feed.getFeedItemList().get(i).saveThrows();//当前feed存储数据库
                }catch (LitePalSupportException exception){
                    ALog.d("数据重复不会插入");//当前feedItem 已经存在数据库中了
                    //此时要对feedItem进行状态字段的恢复，读取数据的状态
                    FeedItem temp = LitePal.where("url = ?",feed.getFeedItemList().get(i).getUrl()).limit(1).find(FeedItem.class).get(0);
                    feed.getFeedItemList().get(i).setId(temp.getId());
                    feed.getFeedItemList().get(i).setRead(temp.isRead());
                    feed.getFeedItemList().get(i).setFavorite(temp.isFavorite());
                    feed.getFeedItemList().get(i).setDate(temp.getDate());//有的feedItem 源地址中 没有时间，所以要恢复第一次加入数据库中的时间
                }
            }
        }

        return feed;

    }
}
