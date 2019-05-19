package com.ihewro.focus.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.webkit.WebView;

import com.google.common.base.Strings;
import com.ihewro.focus.bean.FeedItem;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/08
 *     desc   : 显示文章
 *     version: 1.0
 * </pre>
 */
public class PostUtil {
    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    public static void setContent(Activity context, FeedItem article, WebView textView) {
        if (article == null || textView == null) {
            return;
        }
        WebViewUtil.LoadHtmlIntoWebView(textView,getContent(article),context,article.getUrl());
    }

    public static String getContent(FeedItem article) {
        if (!Strings.isNullOrEmpty(article.getContent())) {
            return article.getContent();
        }
        return article.getSummary();
    }
}
