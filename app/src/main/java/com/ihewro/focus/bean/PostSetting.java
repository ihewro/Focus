package com.ihewro.focus.bean;

import android.content.Context;

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.util.UIUtil;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/13
 *     desc   : 文章页面设置
 *     version: 1.0
 * </pre>
 */
public class PostSetting  {
    public static final String FONT_SIZE = "FONT_SIZE";//字大小
    public static final String FONT_SIZE_DEFAULT = "16";//字大小

    public static final String FONT_SPACING = "FONT_SPACING";//字间距
    public static final String FONT_SPACING_DEFAULT = "0";//字间距

    public static final String LINE_SPACING = "LINE_SPACING";//行间距
    public static final String LINE_SPACING_DEFAULT = "2";//行间距
    public static final String THEME_MODE = "THEME_MODE";//主题模式

    public static final String DAY_MODE = "";//白天
    public static final String NIGHT_MODE = "night";//夜间
    public static final String THEME_MODE_DEFAULT = DAY_MODE;//主题模式




    public static String getFontSize() {
        return UserPreference.queryValueByKey(FONT_SIZE,FONT_SIZE_DEFAULT);
    }

    public static String getLineSpace() {
        return UserPreference.queryValueByKey(LINE_SPACING,LINE_SPACING_DEFAULT);

    }

    public static String getFontSpace() {
        return UserPreference.queryValueByKey(FONT_SPACING,FONT_SPACING_DEFAULT);
    }


    public static String getThemeMode() {
        return UserPreference.queryValueByKey(THEME_MODE,THEME_MODE_DEFAULT);
    }


    public static String getBackground(Context context) {
        String color = UserPreference.queryValueByKey(UserPreference.READ_BACKGROUND,Background.getColorString(context,R.color.white));
        String colorString = "#" + Integer.toHexString(Integer.parseInt(color)).substring(2);
//        ALog.d("颜色！"+colorString);
        return colorString;
    }

    public static int getBackgroundInt(Context context){
        String color = UserPreference.queryValueByKey(UserPreference.READ_BACKGROUND,Background.getColorString(context,R.color.white));//10进制的int
//        ALog.d("颜色toolbar"+Integer.toHexString(Integer.parseInt(color)));
        return Integer.parseInt(color);
    }

}
