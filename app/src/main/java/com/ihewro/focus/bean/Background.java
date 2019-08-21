package com.ihewro.focus.bean;

import android.content.Context;
import android.support.v4.content.ContextCompat;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/06/02
 *     desc   : 阅读界面的背景
 *     version: 1.0
 * </pre>
 */
public class Background {

    private int color;//背景颜色的16进制的int
    private String colorName;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Background(int color) {
        this.color = color;
    }

    public static int getColorInt(Context context,int colorId){
        return ContextCompat.getColor(context,colorId);
    }

    public static String getColorString(Context context,int colorId){
        return String.valueOf(getColorInt(context,colorId));
    }
}
