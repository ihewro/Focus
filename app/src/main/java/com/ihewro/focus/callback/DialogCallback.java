package com.ihewro.focus.callback;

import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface DialogCallback {

    void onFinish(MaterialDialog dialog, View view, int which, CharSequence text,int targetFeedFolderId);
}
