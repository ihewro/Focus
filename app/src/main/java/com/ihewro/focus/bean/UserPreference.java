package com.ihewro.focus.bean;

import com.ihewro.focus.GlobalConfig;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;

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

    public static final String  USE_INTERNET_WHILE_OPEN =  "pref_key_use_internet_while_open";
    public static final String  AUTO_SET_FEED_NAME =  "AUTO_SET_FEED_NAME";

    public static final String  RSS_HUB =  "rsshub";
    public static final String FIRST_USE_LOCAL_SEARCH_AND_FILTER = "FIRST_USE_LOCAL_SEARCH_AND_FILTER";//是否首次打开APP
    public static final String FIRST_INTRO_MAIN_FEED_ITEM = "FIRST_INTRO_MAIN_FEED_ITEM";//首次介绍首页的feedItem功能，包括侧滑，等等
    public static final String FIRST_INTRO_DISCOVER = "FIRST_INTRO_DISCOVER";//首次介绍发现市场的功能，包括手动订阅，添加订阅


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


    public UserPreference(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String queryValueByKey(String key,String defaultValue){
        List<UserPreference> userPreferences = LitePal.where("key = ?", key).find(UserPreference.class);
        if (userPreferences.size()>0){
            UserPreference temp = userPreferences.get(0);
            return temp.getValue();
        }else {
            return defaultValue;
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
        }else {
            setValueByKey(key,value);
        }
    }


    public static String getRssHubUrl(){
        return queryValueByKey(RSS_HUB, GlobalConfig.OfficialRSSHUB);
    }




}
