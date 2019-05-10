package com.ihewro.focus.bean;

import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/07
 *     desc   : eventBus 使用的消息类
 *     version: 1.0
 * </pre>
 */
public class EventMessage {

    //对文章Item的操作
    public static final String MAKE_READ_STATUS_BY_INDEX = "MAKE_READ_STATUS_BY_INDEX";
    public static final String MAKE_READ_STATUS_BY_ID = "MAKE_READ_STATUS_BY_ID";
    public static final String MAKE_STAR_STATUS_BY_INDEX = "MAKE_STAR_STATUS";
    public static final String MAKE_STAR_STATUS_BY_ID = "MAKE_STAR_STATUS_BY_ID";
    public static final String EDIT_ITEM_READ = "EDIT_ITEM_READ";//修改了文章的已读状态

    //点击事件
    public static final String SHOW_FEED_FOLDER_MANAGE = "SHOW_FEED_FOLDER_MANAGE";
    public static final String SHOW_FEED_LIST_MANAGE = "SHOW_FEED_LIST_MANAGE";
    public static final String MAIN_SWITCH_FRAGMENT_USE_FEED_ID = "MAIN_SWITCH_FRAGMENT_USE_FOLDER_ID";


    //feed、feedFolder操作
    public static final String ADD_FEED = "ADD_FEED";//添加新的订阅
    public static final String MOVE_FEED = "MOVE_FEED";//移动feed到另一个文件夹
    public static final String EDIT_FEED_NAME = "EDIT_FEED_NAME";//修改订阅名称
    public static final String DELETE_FEED = "DELETE_FEED";//删除feed

    public static final String ADD_FEED_FOLDER = "ADD_FEED_FOLDER";//添加新的订阅文件夹
    public static final String EDIT_FEED_FOLDER_NAME = "EDIT_FEED_FOLDER";//修改feed文件夹名称
    public static final String DELETE_FEED_FOLDER = "DELETE_FEED_FOLDER";//删除feed文件夹

    //首页的左侧边栏遇到这些通知都会更新
    public static final List<String> feedAndFeedFolderAndItemOperation = Arrays.asList(ADD_FEED, MOVE_FEED, EDIT_FEED_NAME, DELETE_FEED, ADD_FEED_FOLDER, EDIT_FEED_FOLDER_NAME, DELETE_FEED_FOLDER,EDIT_ITEM_READ);
    public static final List<String> feedAndFeedFolderOperation = Arrays.asList(ADD_FEED, MOVE_FEED, EDIT_FEED_NAME, DELETE_FEED, ADD_FEED_FOLDER, EDIT_FEED_FOLDER_NAME, DELETE_FEED_FOLDER);

    //FeedList 收到这样的通知需要更新列表
    public static final List<String>  feedOperation = Arrays.asList(ADD_FEED, MOVE_FEED, EDIT_FEED_NAME, DELETE_FEED);

    //FeedFolderList收到这样的通知需要更新列表
    public static final List<String>  feedFolderOperation = Arrays.asList(ADD_FEED_FOLDER, EDIT_FEED_FOLDER_NAME, DELETE_FEED_FOLDER,MOVE_FEED);



    private String type;
    private String message;
    private int index;
    private String message2;
    private String message3;
    private boolean flag;


    public EventMessage(String type) {
        this.type = type;
    }

    public EventMessage(String type, int index) {
        this.type = type;
        this.index = index;
    }

    public EventMessage(String type, int index, boolean flag) {
        this.type = type;
        this.index = index;
        this.flag = flag;
    }

    public EventMessage(String type, String message) {
        this.type = type;
        this.message = message;
    }


    public EventMessage(String type, String message, String message2) {
        this.type = type;
        this.message = message;
        this.message2 = message2;
    }


    public EventMessage(String type, String message, String message2, String message3) {
        this.type = type;
        this.message = message;
        this.message2 = message2;
        this.message3 = message3;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public String getMessage3() {
        return message3;
    }

    public void setMessage3(String message3) {
        this.message3 = message3;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "type" + type + "\n" +
                "message" + message + "\n" +
                "message2" + message2 + "\n" +
                "message3" + message3;

    }
}
