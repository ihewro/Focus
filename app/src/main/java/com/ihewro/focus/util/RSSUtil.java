package com.ihewro.focus.util;

import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedRequest;
import com.ihewro.focus.bean.UserPreference;

import org.litepal.LitePal;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/06
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class RSSUtil {

    /**
     * url是否包含rsshub的头，来判断当前url是否需要加
     * @param url
     * @return
     */
    public static int urlIsContainsRSSHub(String url){
        List<String> list = GlobalConfig.rssHub;
        for(int i = 0; i<list.size();i++){
            if (url.contains(list.get(i))){
                return i;
            }
        }
        return  -1;
    }


    /**
     * TODO:根据订阅的历史记录进行动态化计算超时时间
     * @return
     */
    public static int calTimeout(int feedid){
        List<FeedRequest> list = LitePal.where("feedid = ?", String.valueOf(feedid)).find(FeedRequest.class);

        float timeout;

        //成功率>95% 则超时时间减少10s

        //成功率>90% 超时时间

       return 35;
    }

    /**
     *
     * @param imgUrl 图片地址
     * @param url 博客页面地址
     * @param isBadGuy 是否需要反盗链
     * @return
     */
    public static String handleImageUrl(String imgUrl,String url,boolean isBadGuy){
        //相对地址判断 没有根域名/upload/5798_1.jpg upload/1.jpg  没有写协议头//www.ihewro.com/1.jpg  http://www.ihewro.com/1.jpg https://www.ihewro.com/1.jpg
        //判断imageurl 是不是相对地址

        if (StringUtil.trim(imgUrl).equals("")){
            return "";
        }else {
            String temp = imgUrl.substring(0,Math.min(8,imgUrl.length()));
            if (temp.contains("//")){
                if (temp.contains("http")){//无需修改的

                }else {//缺少协议头
                    imgUrl = "http:" + imgUrl;
                }
            }else {//没有根域名的
                if (imgUrl.substring(0, 1).equals("/")){//拼接根域名 /upload/5798_1.jpg upload/1.jpg
                    //从url中找到根域名 http://www.ihewro.com/archive/123

                    if (!url.substring(url.length() - 1).equals("/")){
                        url = url + "/";
                    }
                    int pos0 = url.indexOf("/",8);
                    String root = url.substring(0,pos0);
                    imgUrl = root + imgUrl;
                }else {//直接拼接   upload/5798_1.jpg upload/1.jpg
                    imgUrl = url + "/" +imgUrl;
                }
            }

            if (isBadGuy){
                imgUrl = GlobalConfig.BAD_GUY+"?url="+imgUrl+"&refer="+url;
            }

            return imgUrl;
        }
    }
}
