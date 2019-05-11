package com.ihewro.focus.util;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.ImageViewerPopupView;
import com.lxj.xpopup.interfaces.OnSrcViewUpdateListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class MJavascriptInterface {
    private Context context;
    private String[] imageUrls;

    public MJavascriptInterface(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @android.webkit.JavascriptInterface
    public void openImage(String img) {
        //打开当前图片的弹窗

        /*// 多图片场景
        new XPopup.Builder(context).asImageViewer(null, position, list, new OnSrcViewUpdateListener() {
            @Override
            public void onSrcViewUpdate(ImageViewerPopupView popupView, int position) {
                // 作用是当Pager切换了图片，需要更新源View
                popupView.updateSrcView((ImageView) recyclerView.getChildAt(position));
            }
        }, new ImageLoader())
                .show();*/

        Toasty.success(context,"点击了图片").show();
    }
}