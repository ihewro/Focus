package com.ihewro.focus.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.PostDetailActivity;
import com.ihewro.focus.activity.StarActivity;
import com.ihewro.focus.bean.PostSetting;
import com.ihewro.focus.helper.CustomTabActivityHelper;
import com.ihewro.focus.helper.MyWebViewClient;
import com.ihewro.focus.helper.WebviewFallback;
import com.ihewro.focus.view.WebContentLayout;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

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


    public static void LoadHtmlIntoWebView(WebView webView, String html, Activity context, String url){
        LoadHtmlIntoWebView(webView, html, context, url,null);
    }
    /**
     *
     * @param webView
     * @param html
     * @param context
     * @param url 用于修改相对地址的
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static void LoadHtmlIntoWebView(WebView webView, String html, Activity context, String url, ViewGroup parent){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//自适应屏幕        ☆☆
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);


        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        String[] imageUrls = {};
        webView.addJavascriptInterface(new MJavascriptInterface(context,imageUrls,webView), "imagelistener");
        webView.setWebViewClient(new MyWebViewClient(context,url));


        webView.setLayerType(View.LAYER_TYPE_NONE,null);//关闭硬件加速
//        webView.getSettings().setBlockNetworkImage(true);//一开始禁止图片加载


        //加载HTML
        String meta = "<meta name=\"viewport\" content=\"initial-scale=1,user-scalable=no,maximum-scale=1,width=device-width\">";
        String css = "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://focus.com/content.css\">";
        String js = "<script src=\"https://focus.com/content.js\"></script>";
        String mclass = "";
        if(SkinPreference.getInstance().getSkinName().equals("night")){
            mclass = "entry-dark";
        }

        //根据配置加载字体、间距的css
        String fontSize = "font-size:"+ PostSetting.getFontSize() + "px;";
        String lineSpace = "line-height:" + PostSetting.getLineSpace() + ";";
        String fontSpace = "letter-spacing:" + PostSetting.getFontSpace() + "px;";


        String backgroundCss = "";

        if (!SkinPreference.getInstance().getSkinName().equals("night")) {//夜间模式的背景颜色设置无效
            backgroundCss = "background:" + PostSetting.getBackground(context);
        }

//        backgroundCss = "";

        String settingCss = "<style>.entry{"+fontSize+lineSpace+fontSpace+backgroundCss+"}</style>\n";


        String body = "<html><header>"  + meta +css + js + settingCss+ "</header><body class=\"entry "+mclass+"\">" + html
                + "</body></html>";

        webView.setBackgroundColor(Color.parseColor("#00000000"));
//        webView.loadData( body, "text/html; charset=UTF-8", null);
        webView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);


       /* WebContentLayout webContentLayout = new WebContentLayout(context);
        AgentWeb mAgentWeb = AgentWeb.with(context)
                .setAgentWebParent(parent, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
//                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(new MyWebViewClient(context,url))
                .setWebLayout(webContentLayout)
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
                .interceptUnkownUrl() //拦截找不到相关页面的Scheme
                .createAgentWeb()
                .ready()
                .get();

        mAgentWeb.getUrlLoader().loadData( body, "text/html; charset=UTF-8", null);*/

    }


    public static void openLink(String url, Activity activity){
        ALog.d("打开的地址" + url);
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        CustomTabActivityHelper.openCustomTab(
                activity, customTabsIntent, Uri.parse(url), new WebviewFallback());

    }
}
