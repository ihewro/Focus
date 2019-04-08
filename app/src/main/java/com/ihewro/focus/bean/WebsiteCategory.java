package com.ihewro.focus.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/07
 *     desc   : 网站类型，比如新闻媒体
 *     version: 1.0
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebsiteCategory extends LitePalSupport {

    @Column(unique = true)
    private String id;
    @Column(unique = true)
    private String name;
    private String desc;

    public WebsiteCategory() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
