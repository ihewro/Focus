package com.ihewro.focus.bean;

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

    private int color;//背景颜色值

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Background(int color) {
        this.color = color;
    }
}
