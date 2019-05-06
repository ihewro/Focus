package com.ihewro.focus.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/06
 *     desc   : 订阅的文件夹
 *     version: 1.0
 * </pre>
 */
public class FeedFolder extends LitePalSupport {

    private int id;//使用int型主键

    @Column(unique = true, defaultValue = "")
    private String name;//文件夹名称,且是唯一的
    @Column(defaultValue = "1.0")
    private double orderValue;//顺序权限，用来排序的

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(double orderValue) {
        this.orderValue = orderValue;
    }
}
