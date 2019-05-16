package com.ihewro.focus.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedRequire {

    public static final int SET_URL = 444;//该类型直接设置url
    public static final int JOIN_URL = 787;//该类型的需要拼接url这两个操作只可能有一个出现，参数列表中不可能出现两种


    public static final int SET_NAME = 356;//设置订阅的名称
    
    private String iid;
    private String feedId;//订阅的id，用于和所需的参数关联
    private String name;//参数英文名称
    private String chinaName;//参数中文名称
    private String desc;//参数描述
    private boolean optional;//true 表示可选，false表示必选
    private int type;//参数的作用是什么，比如url，表示该参数需要拼接成url
    private String defaultValue;//默认值，使用在输入框中显示默认填好的值


    public FeedRequire() {
    }

    public FeedRequire(String chinaName, String desc, int type) {
        this.chinaName = chinaName;
        this.desc = desc;
        this.type = type;
    }

    public FeedRequire(String chinaName, String desc, int type, String defaultValue) {
        this.chinaName = chinaName;
        this.desc = desc;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChinaName() {
        return chinaName;
    }

    public void setChinaName(String chinaName) {
        this.chinaName = chinaName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
