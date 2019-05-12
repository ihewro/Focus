package com.ihewro.focus.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.common.base.Strings;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.view.MyWebViewClient;
import com.ihewro.focus.view.htmltextview.HtmlTextView;
import com.ihewro.focus.other.HtmlImageGetterEx;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/08
 *     desc   : 显示文章
 *     version: 1.0
 * </pre>
 */
public class ArticleUtil {
    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    public static void setContent(Context context, FeedItem article, WebView textView) {
        if (article == null || textView == null) {
            return;
        }


        WebSettings webSettings = textView.getSettings();

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//自适应屏幕        ☆☆
        webSettings.setDisplayZoomControls(false);

        webSettings.setJavaScriptEnabled(true);




        // disable scroll on touch
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE){
                    return false;
                }else {
                    return true;
                }
            }
        });


        //
        String[] imageUrls = {};
        textView.addJavascriptInterface(new MJavascriptInterface(context,imageUrls), "imagelistener");
        textView.setWebViewClient(new MyWebViewClient(context));

        //加载HTML

        String css = "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://focus.com/content.css\">";

        String linkCss = "<style>img{max-width:50%}video{height:auto!important;width:100%;display:block}</style>\n";


        String meta = "";
        String body = "<html><header>" + linkCss + meta +css + "</header>" + getContent(article)
                + "</body></html>";

        textView.setBackgroundColor(Color.parseColor("#00000000"));
        textView.loadData( body, "text/html; charset=UTF-8", null);


    }

    private static String getContent(FeedItem article) {
        if (!Strings.isNullOrEmpty(article.getContent())) {
            return article.getContent();
        }
        return article.getSummary();
    }
}
