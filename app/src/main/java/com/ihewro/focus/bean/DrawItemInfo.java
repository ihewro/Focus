package com.ihewro.focus.bean;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/13
 *     desc   : 侧边栏item所要包含的信息
 *     version: 1.0
 * </pre>
 */
public class DrawItemInfo {
    private int identify;//标识符
    private int id;//所指示的item在数据库中的位置

    public DrawItemInfo(int identify, int id) {
        this.identify = identify;
        this.id = id;
    }

    public int getIdentify() {
        return identify;
    }

    public void setIdentify(int identify) {
        this.identify = identify;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
