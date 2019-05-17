package com.ihewro.focus.bean;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/05
 *     desc   : 一个简单的信息类，作用是作为返回参数，返回多个信息
 *     version: 1.0
 * </pre>
 */
public class Message {
    boolean flag;
    String data;

    public Message(boolean flag, String data) {
        this.flag = flag;
        this.data = data;
    }

    public Message(boolean b) {
        this.flag = b;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "flag = " + flag + "\n" +
                "data" + data;
    }
}
