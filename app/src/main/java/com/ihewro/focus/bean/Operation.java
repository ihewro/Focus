package com.ihewro.focus.bean;

import android.graphics.drawable.Drawable;

import com.ihewro.focus.callback.OperationCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/12
 *     desc   : 功能操作类，配合{@link com.ihewro.focus.adapter.OperationListAdapter}
 *     version: 1.0
 * </pre>
 */
public class Operation {

    private String name;//操作名称
    private Drawable drawable;//图标
    private OperationCallback callback;//该操作的内容
    private Object object;
    private String info;



    public Operation(String name,String info, Drawable drawable, Object o, OperationCallback callback) {
        this.name = name;
        this.info = info;
        this.drawable = drawable;
        this.callback = callback;
        this.object = o;
    }


    public void run(){
        callback.run(object);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public OperationCallback getCallback() {
        return callback;
    }

    public void setCallback(OperationCallback callback) {
        this.callback = callback;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
