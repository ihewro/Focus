package com.ihewro.focus;

import android.os.Environment;

import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.UserPreference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class GlobalConfig {
//    public static final String serverUrl = "https://cloud.bmob.cn/f3d283d6ac358cd2/focus/";
    public static final String serverUrl = "https://auth.ihewro.com/focus/";


    public static final String storageFolderName = "focus";//所有表情包都在此目录下建立子目录
    public static final String appDirPath = Environment.getExternalStorageDirectory() + "/" + GlobalConfig.storageFolderName + "/";
    public static final String appXMLPath = Environment.getExternalStorageDirectory() + "/" + GlobalConfig.storageFolderName + "/" + "xml/";

    public static final String OfficialRSSHUB = "https://rsshub.app";
    public static final String MoeratsRsshub = "https://rsshub.moerats.com";
    public static final List<String> rssHub = Arrays.asList(OfficialRSSHUB, MoeratsRsshub, UserPreference.OWN_RSSHUB);
    public static final List<String> feedRssHub = Arrays.asList(OfficialRSSHUB, MoeratsRsshub, UserPreference.OWN_RSSHUB,UserPreference.DEFAULT_RSSHUB);

    public static final String is_need_update_main = "is_need_update_main";
    //两个是对应的
    public static final List<String> refreshInterval = Arrays.asList("2分钟","15分钟", "30分钟", "45分钟","1个小时","2个小时","3个小时","4个小时","6个小时","10个小时","12个小时","永不");
    public static final List<Integer> refreshIntervalInt = Arrays.asList(2,15, 30, 45,60,120,180,240,360,600,720,-1);


    public static List<FeedItem> feedItemList = new ArrayList<>();

    public static final String BAD_GUY= "https://www.ihewro.com/badguy.php";
    public static final String CHINA_NORMAL= "https://images.weserv.nl/";
    public static final String CHINA_GIF= "https://www.ihewro.com/badguy.php";


}
