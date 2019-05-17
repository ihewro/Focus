package com.ihewro.focus.util;

import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.UserPreference;

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
}
