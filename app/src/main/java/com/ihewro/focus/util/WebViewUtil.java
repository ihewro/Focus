package com.ihewro.focus.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ihewro.focus.bean.PostSetting;
import com.ihewro.focus.helper.MyWebViewClient;

import skin.support.utils.SkinPreference;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/16
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class WebViewUtil {

    @SuppressLint("SetJavaScriptEnabled")
    public static void LoadHtmlIntoWebView(WebView webView, String html, Context context){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//自适应屏幕        ☆☆
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        String[] imageUrls = {};
        webView.addJavascriptInterface(new MJavascriptInterface(context,imageUrls), "imagelistener");
        webView.setWebViewClient(new MyWebViewClient(context));

        //加载HTML
        String css = "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://focus.com/content.css\">";
        String mclass = "";
        if(SkinPreference.getInstance().getSkinName().equals("night")){
            mclass = "entry-dark";
        }

        //根据配置加载字体、间距的css
        String fontSize = "font-size:"+ PostSetting.getFontSize() + "px;";
        String lineSpace = "line-height:" + PostSetting.getLineSpace() + ";";
        String fontSpace = "letter-spacing:" + PostSetting.getFontSpace() + "px;";

        String settingCss = "<style>.entry{"+fontSize+lineSpace+fontSpace+"}</style>\n";


        String meta = "";
        String body = "<html><header>"  + meta +css + settingCss+ "</header><body class=\"entry "+mclass+"\">" + html
                + "</body></html>";

        webView.setBackgroundColor(Color.parseColor("#00000000"));
        webView.loadData( body, "text/html; charset=UTF-8", null);

    }
}
