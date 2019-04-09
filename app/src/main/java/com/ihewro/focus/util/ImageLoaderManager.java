package com.ihewro.focus.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.ihewro.focus.R;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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

        // TODO: 1/28/17 dependent on device performance
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
        Drawable defaultDrawable = context.getResources().getDrawable(R.drawable.ic_rss_feed_grey_24dp);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultDrawable)
                .showImageForEmptyUri(defaultDrawable)
                .showImageOnFail(defaultDrawable)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        return options;
    }
}
