package com.ihewro.focus.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/06/23
 *     desc   : 收藏文件夹表
 *     version: 1.0
 * </pre>
 */
public class CollectionFolder extends LitePalSupport {

    @Column(unique = true, defaultValue = "")
    private String name;//分类的名称


    @Column(defaultValue = "1.0")
    private double orderValue;//顺序权限，用来排序的

    private String password;//密码

    @Column(ignore = true)
    private boolean isSelect;//当前收藏分类是否被选择了

    public CollectionFolder(String name) {
        this.name = name;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
