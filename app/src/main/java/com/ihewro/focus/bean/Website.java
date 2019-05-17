package com.ihewro.focus.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/07
 *     desc   : 某个网站名称，比如微博
 *     version: 1.0
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Website extends LitePalSupport {

    @Column(unique = true)
    private String iid;
    @Column(unique = true)
    private String name;
    private String desc;
    private String cate_name;//分类名称
    private String icon;//图标地址

    @Column(ignore = true)
    private List<Feed> feedList = new ArrayList<>();


    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCate_name() {
        return cate_name;
    }

    public void setCate_name(String cate_name) {
        this.cate_name = cate_name;
    }

    public List<Feed> getFeedList() {
        return feedList;
    }

    public void setFeedList(List<Feed> feedList) {
        this.feedList = feedList;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
