package com.ihewro.focus.bean;

import com.blankj.ALog;
import com.ihewro.focus.GlobalConfig;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/06
 *     desc   : 用户个人设置表
 *     version: 1.0
 * </pre>
 */
public class UserPreference extends LitePalSupport {

    public static final String USE_INTERNET_WHILE_OPEN =  "pref_key_use_internet_while_open";
    public static final String AUTO_SET_FEED_NAME =  "AUTO_SET_FEED_NAME";
    public static final String OWN_RSSHUB =  "自定义源";
    public static final String ODER_CHOICE = "ODER_CHOICE";//排序规则
    public static final String FILTER_CHOICE = "FILTER_CHOICE";//过滤规则
    public static final String notOpenClick = "notOpenClick";//不要下拉打开连接
    public static final String notStar = "notStar";//不要双击收藏
    public static final String notToTop = "notToTop";//不要双击回顶部
    public static final String not_show_image_in_list = "not_show_image_in_list";//首页列表不要显示图片
    public static final String tim_interval = "tim_interval";//后台请求间隔
    public static final String tim_is_open = "tim_is_open";//定时器是否已经开启
    public static final String back_error = "back_error";//定时器是否已经开启
    public static final String notWifi = "notWifi";//仅仅wifi下请求
    public static final String notUseChrome = "notUseChrome";//不使用Chrome内核



    public static final String  RSS_HUB =  "rsshub";
    public static final String FIRST_USE_LOCAL_SEARCH_AND_FILTER = "FIRST_USE_LOCAL_SEARCH_AND_FILTER";//是否首次打开APP
    public static final String FIRST_INTRO_MAIN_FEED_ITEM = "FIRST_INTRO_MAIN_FEED_ITEM";//首次介绍首页的feedItem功能，包括侧滑，等等
    public static final String FIRST_INTRO_DISCOVER = "FIRST_INTRO_DISCOVER";//首次介绍发现市场的功能，包括手动订阅，添加订阅
    public static final String DEFAULT_RSSHUB = "跟随上一级设置";


    private int id;//主键
    private String key;
    private String value;
    private String defaultValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    public  static  Map<String,String> map = new HashMap<>();


    public static void initCacheMap(){
        map.clear();
        LitePal.findAllAsync(UserPreference.class).listen(new FindMultiCallback<UserPreference>() {
            @Override
            public void onFinish(List<UserPreference> list) {
                for (UserPreference userPreference:list){
                    map.put(userPreference.getKey(),userPreference.getValue());
                }
            }
        });
    }

    public UserPreference(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public UserPreference(String key, String value, String defaultValue) {
        this.key = key;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public static String queryValueByKey(String key, String defaultValue){
        if (map.containsKey(key)){
            ALog.d("缓存数据");
            return map.get(key);
        }else {
            ALog.d("非缓存数据");
            List<UserPreference> userPreferences = LitePal.where("key = ?", key).find(UserPreference.class);
            if (userPreferences.size()>0){
                UserPreference temp = userPreferences.get(0);
                return temp.getValue();
            }else {
                return defaultValue;
            }
        }
    }




    private static void setValueByKey(String key,String value){
        UserPreference userPreference = new UserPreference(key,value);
        userPreference.save();
    }


    public static void updateOrSaveValueByKey(String key, String value){
        List<UserPreference> userPreferences = LitePal.where("key = ?", key).find(UserPreference.class);
        if (userPreferences.size()>0){
            UserPreference temp = userPreferences.get(0);
            temp.setValue(value);
            temp.save();
            //修改缓存中键值对
            if (map.containsKey(key)){
                map.remove(key);
                map.put(key,value);
            }
        }else {
            setValueByKey(key,value);
        }
    }


    public static String getRssHubUrl(){

        String url =  queryValueByKey(RSS_HUB, GlobalConfig.OfficialRSSHUB);
        if (url.equals(OWN_RSSHUB)){//如果有自定义源则选择这个
            url =  queryValueByKey(OWN_RSSHUB, GlobalConfig.OfficialRSSHUB);
            //对自定义源的地址进行处理
            if (!url.startsWith("http://") && !url.startsWith("https://")){
                url = "http://" + url;
            }
            if (url.endsWith("/")){
                url = url.substring(0,url.length()-1);
            }
            return url;
        }else {
            return url;
        }
    }




}
