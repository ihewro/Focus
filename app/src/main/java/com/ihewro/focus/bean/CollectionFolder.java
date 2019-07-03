package com.ihewro.focus.bean;

import android.content.Context;
import android.text.InputType;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.focus.callback.OperationCallback;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;
import org.litepal.exceptions.LitePalSupportException;

import es.dmoral.toasty.Toasty;

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

    private int id;

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

    public static void addNewFolder(final Context context, final OperationCallback callback){
        new MaterialDialog.Builder(context)
                .title("输入新增的收藏分类名称：")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog2, CharSequence input) {
                        //TODO:不能重命名
                        String name = dialog2.getInputEditText().getText().toString().trim();
                        CollectionFolder collectionFolder = new CollectionFolder(name);
                        try {
                            collectionFolder.saveThrows();
                            Toasty.success(context,"新建成功！").show();
                            callback.run(collectionFolder);

                        }catch (LitePalSupportException e){
                            //名称重复了
                            Toasty.info(context,"已经有该收藏分类了！").show();
                        }

                    }
                }).show();

    }
}
