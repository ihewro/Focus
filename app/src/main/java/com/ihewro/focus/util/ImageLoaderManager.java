package com.ihewro.focus.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.ihewro.focus.R;
import com.ihewro.focus.callback.ImageLoaderCallback;
import com.ihewro.focus.view.ImageManagePopupView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.ImageViewerPopupView;
import com.lxj.xpopup.interfaces.XPopupImageLoader;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

import skin.support.utils.SkinPreference;

/**
 * @description:
 * @author: Match
 * @date: 1/27/17
 */

public class ImageLoaderManager {

    public static void init(Context context) {

        ColorDrawable defaultDrawable = new ColorDrawable(context.getResources().getColor(R.color.main_grey_light));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultDrawable)
                .showImageForEmptyUri(defaultDrawable)
                .showImageOnFail(defaultDrawable)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(options)
                .memoryCache(new LruMemoryCache(8 * 1024 * 1024))
                .diskCacheSize(100 * 1024 * 1024)
                .diskCacheFileCount(100)
                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }


    public static DisplayImageOptions getSubsciptionIconOptions(Context context) {

        Drawable defaultDrawable;

        Drawable errorDrawable = context.getResources().getDrawable(R.drawable.loading_error);
        defaultDrawable = context.getResources().getDrawable(R.drawable.ic_loading);

/*        if (SkinPreference.getInstance().getSkinName().equals("night")) {
            defaultDrawable = context.getResources().getDrawable(R.drawable.ic_night_loading);
        } else {
            defaultDrawable = context.getResources().getDrawable(R.drawable.ic_day_loading);

        }*/

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultDrawable)
                .showImageForEmptyUri(defaultDrawable)
                .showImageOnFail(errorDrawable)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        return options;
    }







    public static void loadImageUrlToImageView(String imageUrl, final ImageView imageView, final ImageLoaderCallback imageLoaderCallback){

        //TODO: 根据夜间模式，加载中的图片不同
        DisplayImageOptions simpleOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.bj_weixianshi)//加载中的等待图片
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.loadImage(imageUrl, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                imageLoaderCallback.onStart(imageView);

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                imageLoaderCallback.onFailed(imageView,failReason);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageLoaderCallback.onSuccess(imageView,loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }



    public static void showSingleImageDialog(final Context context, final String imageUrl, View srcView){
        // 单张图片场景
        ImageViewerPopupView imageViewerPopupView = new XPopup.Builder(context)
                .asImageViewer((ImageView) srcView, imageUrl, new MyImageLoader(context));
        imageViewerPopupView.show();
    }


    static class MyImageLoader implements XPopupImageLoader {

        private Context context;
        MyImageLoader(Context context) {
            this.context = context;
        }

        @Override
        public void loadImage(int position, @NonNull Object url, @NonNull ImageView imageView) {
            ImageLoader.getInstance().displayImage(StringUtil.trim(String.valueOf(url)), imageView,ImageLoaderManager.getSubsciptionIconOptions(context));
        }

        @Override
        public File getImageFile(@NonNull Context context, @NonNull Object uri) {
            try {
                return ImageLoader.getInstance().getDiskCache().get(String.valueOf(uri));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
