package com.ihewro.focus.bean;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/06
 *     desc   : 用户个人设置表
 *     version: 1.0
 * </pre>
 */
public class UserPreference {
    private int id;//主键
    private int is_use_internet_while_open;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIs_use_internet_while_open() {
        return is_use_internet_while_open;
    }

    public void setIs_use_internet_while_open(int is_use_internet_while_open) {
        this.is_use_internet_while_open = is_use_internet_while_open;
    }
}
