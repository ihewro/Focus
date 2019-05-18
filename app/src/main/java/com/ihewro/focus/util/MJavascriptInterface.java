package com.ihewro.focus.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.blankj.ALog;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.ImageViewerPopupView;
import com.lxj.xpopup.interfaces.XPopupImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

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
    private Activity activity;
    private String[] imageUrls;

    MJavascriptInterface(Activity context, String[] imageUrls) {
        this.activity = context;
        this.imageUrls = imageUrls;
    }

    @android.webkit.JavascriptInterface
    public void openImage(String img) {

        ALog.d("点击了图片" +img);
        // 单张图片场景
        ImageViewerPopupView imageViewerPopupView = new XPopup.Builder(activity)
                .asImageViewer(null, img, new MyImageLoader());
        imageViewerPopupView.show();
    }


    @android.webkit.JavascriptInterface
    public void openUrl(String url) {
        WebViewUtil.openLink(url, activity);

    }



    class MyImageLoader implements XPopupImageLoader {
        @Override
        public void loadImage(int position, @NonNull Object url, @NonNull ImageView imageView) {
            //必须指定Target.SIZE_ORIGINAL，否则无法拿到原图，就无法享用天衣无缝的动画
            ImageLoader.getInstance().displayImage(String.valueOf(url), imageView);
//            Glide.with(imageView).load(url).apply(new RequestOptions().placeholder(R.mipmap.ic_launcher_round).override(Target.SIZE_ORIGINAL)).into(imageView);
        }

        @Override
        public File getImageFile(@NonNull Context context, @NonNull Object uri) {
            try {
                return ImageLoader.getInstance().getDiskCache().get(String.valueOf(uri));
//                return              com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(String.valueOf(url), imageView);
//                return Glide.with(context).downloadOnly().load(uri).submit().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}