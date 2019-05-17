package com.ihewro.focus.callback;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/16
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface ImageLoaderCallback {
    void onFailed(ImageView imageView, FailReason failReason);
    void onSuccess(ImageView imageView, Bitmap bitmap);
}
