package com.ihewro.focus.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.ihewro.focus.R;
import com.ihewro.focus.callback.ImageLoaderCallback;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

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


}
