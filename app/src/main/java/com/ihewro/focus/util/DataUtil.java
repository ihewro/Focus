package com.ihewro.focus.util;

import android.database.Cursor;

import com.google.common.base.Strings;
import com.ihewro.focus.bean.Collection;
import com.ihewro.focus.bean.FeedItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/08
 *     desc   : 操作数据库
 *     version: 1.0
 * </pre>
 */
public class DataUtil {
    public static String getOptimizedDesc(String originDesc) {
        // Shrink string to optimize render time
        String result = "";
        if (Strings.isNullOrEmpty(originDesc)) {
            return result;
        }
        String parsedStr = Jsoup.parse(originDesc).text();
        int showLength = parsedStr.length() < 50 ? parsedStr.length() : 50;
        if (showLength > 0) {
            result = parsedStr.substring(0, showLength - 1);
        }
        return result;
    }


    public static String getFeedItemImageUrl(FeedItem feedItem){
        String content = PostUtil.getContent(feedItem);
        if (content!=null && !content.equals("")){
            Document doc = Jsoup.parse(content);
            if (doc != null) {
                Elements images = doc.select("img");
                if (images.size() > 0) {
                    return images.get(0).attr("src");
                }
            }
        }
        return null;
    }


    public static String getCollectionItemImageUrl(Collection item){
        String content;
        if (!Strings.isNullOrEmpty(item.getContent())) {
            content =  item.getContent();
        }else {

            content =  item.getSummary();
        }
        if (content!=null && !content.equals("")){
            Document doc = Jsoup.parse(content);
            if (doc != null) {
                Elements images = doc.select("img");
                if (images.size() > 0) {
                    return images.get(0).attr("src");
                }
            }
        }
        return null;
    }



    public static String getColumnString(Cursor cursor, String column,String defaultValue){
        int index = cursor.getColumnIndex(column);
        if (index == -1){
            return defaultValue;
        }else {
            return cursor.getString(index);
        }
    }


    public static int getColumnInt(Cursor cursor, String column,int defaultValue){
        int index = cursor.getColumnIndex(column);
        if (index == -1){
            return defaultValue;
        }else {
            return cursor.getInt(index);
        }
    }
}
