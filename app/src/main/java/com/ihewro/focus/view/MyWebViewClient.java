package com.ihewro.focus.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blankj.ALog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MyWebViewClient extends WebViewClient {

    private Context context;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onPageFinished(WebView view, String url) {
        view.getSettings().setJavaScriptEnabled(true);
        super.onPageFinished(view, url);
        addImageClickListener(view);//待网页加载完全后设置图片点击的监听方法
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        view.getSettings().setJavaScriptEnabled(true);
        super.onPageStarted(view, url, favicon);
    }


    public MyWebViewClient() {
    }

    public MyWebViewClient(Context context) {
        this.context = context;
    }

    private void addImageClickListener(WebView webView) {

     /*   //设置图片的点击事件
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistener.openImage(this.src);  " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
                "    }  " +
                "}" +
                "})()");
*/

        //图片自适应大小


        //内置视频播放器

    }

//    shouldInterceptRequest




    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        ALog.d("请求的url有" + url);

        //拦截https://focus.com/content.css 替换成本地的css
        AssetManager am = context.getAssets();
       if (url.equals("https://focus.com/content.css")){
           try {
               InputStream is = context.getAssets().open("css/" + "webview.css");
               ALog.i("shouldInterceptRequest", "use offline resource for: " + url);
               return new WebResourceResponse("text/css", "UTF-8", is);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }


        return super.shouldInterceptRequest(view, url);
    }
}