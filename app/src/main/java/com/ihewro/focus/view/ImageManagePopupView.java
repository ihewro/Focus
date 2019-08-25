package com.ihewro.focus.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ihewro.focus.bean.Help;
import com.ihewro.focus.bean.Operation;
import com.ihewro.focus.callback.OperationCallback;
import com.ihewro.focus.util.ImageLoaderManager;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/08/24
 *     desc   : 长按图片弹窗
 *     version: 1.0
 * </pre>
 */
public class ImageManagePopupView extends OperationBottomPopupView {



    public ImageManagePopupView(@NonNull Context context,String imageurl) {
        super(context, null, "图片操作", "", new Help(false));

        //操作列表
        this.setOperationList(getFeedFolderOperationList(imageurl));

    }

    private List<Operation> getFeedFolderOperationList(String imageUrl){
        List<Operation> list = new ArrayList<>();
        list.add(new Operation(imageUrl, "",null, imageUrl, new OperationCallback() {
            @Override
            public void run(Object o) {

            }
        }));

        list.add(new Operation("查看图片", "",null, imageUrl, new OperationCallback() {
            @Override
            public void run(Object o) {
                //打开图片弹窗
                ImageLoaderManager.showSingleImageDialog(getContext(), String.valueOf(o));
            }
        }));

        list.add(new Operation("拷贝图片地址", "",null, imageUrl, new OperationCallback() {
            @Override
            public void run(Object o) {
                //复制
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, String.valueOf(o)));
                Toasty.success(getContext(),"复制成功").show();
            }
        }));
        return list;
    }

}
