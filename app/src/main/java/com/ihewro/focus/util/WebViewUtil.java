package com.ihewro.focus.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.blankj.ALog;
import com.ihewro.focus.GlobalConfig;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.HttpUrl;
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
        LoadHtmlIntoWebView(webView, html, context, url,null,false,false);
    }
    /**
     *
     * @param webView
     * @param html
     * @param context
     * @param url 用于修改相对地址的
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static void LoadHtmlIntoWebView(WebView webView, String html, Context context, String url, ViewGroup parent,boolean isBadGuy,boolean isChina){

        //对文章中的图片进一步处理
        //使用loading一开始进行占位
        Document doc = Jsoup.parse(html);
        //清洗所有文字的背景颜色，样式
        Elements ps = doc.select("p");
        for (Element element:ps){
            element.attr("style","");
        }


        Elements spans = doc.select("span");
        for (Element element:spans){
            element.attr("style","");
        }

        Elements pngs = doc.select("img[src]");
        for (Element element : pngs) {
            String imgUrl = RSSUtil.handleImageUrl(element.attr("src"),url,isBadGuy,isChina);

            element.attr("src","data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIGlkPSJsb2FkZXItMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiIHdpZHRoPSI0MHB4IiBoZWlnaHQ9IjQwcHgiIHZpZXdCb3g9IjAgMCA1MCA1MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNTAgNTA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4KPHBhdGggZmlsbD0iIzAwMCIgZD0iTTI1LjI1MSw2LjQ2MWMtMTAuMzE4LDAtMTguNjgzLDguMzY1LTE4LjY4MywxOC42ODNoNC4wNjhjMC04LjA3MSw2LjU0My0xNC42MTUsMTQuNjE1LTE0LjYxNVY2LjQ2MXoiIHRyYW5zZm9ybT0icm90YXRlKDU5LjM5MjggMjUgMjUpIj4KPGFuaW1hdGVUcmFuc2Zvcm0gYXR0cmlidXRlVHlwZT0ieG1sIiBhdHRyaWJ1dGVOYW1lPSJ0cmFuc2Zvcm0iIHR5cGU9InJvdGF0ZSIgZnJvbT0iMCAyNSAyNSIgdG89IjM2MCAyNSAyNSIgZHVyPSIwLjZzIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSIvPgo8L3BhdGg+Cjwvc3ZnPg==");
            element.attr("data",imgUrl);

        }
        html = doc.toString();

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
        String meta = "<meta name=\"viewport\" content=\"initial-scale=1,user-scalable=no,maximum-scale=1,width=device-width\"><meta name=\"referrer\" content=\"no-referrer\" />";
        String css = "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://focus.com/content.css\">";
        String js = "<script src=\"https://focus.com/content.js\"></script>";
//        String meta = '<meta name="referrer" content="no-referrer" />';
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

        String settingCss = "<style>.entry{"+fontSize+lineSpace+fontSpace+backgroundCss+"}</style>\n";


        String body = "<html><header>"  + meta +css + js + settingCss+ "</header><body class=\"entry "+mclass+"\">" + html
                + "</body></html>";

        webView.setBackgroundColor(Color.parseColor("#00000000"));
        webView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                ALog.d(consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });


    }


    public static void openLink(String url, Activity activity){
        ALog.d("打开的地址" + url);
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        CustomTabActivityHelper.openCustomTab(
                activity, customTabsIntent, Uri.parse(url), new WebviewFallback());

    }
}
