package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.view.WebLayout;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * 自定义的webView
 */
public class WebViewActivity extends BackActivity {

    public static final String EXTRA_URL = "extra.url";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.container)
    LinearLayout container;

    private String url;

    WebLayout webLayout;
    protected AgentWeb mAgentWeb;

    public static void activityStart(Activity activity, Uri uri) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_URL, uri.toString());
        activity.startActivity(intent);
    }


    private com.just.agentweb.WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (toolbar != null) {
                toolbar.setTitle(title);
            }
        }
    };


    private com.just.agentweb.WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

            if (url.equals("https://focus.com/nolimit.js")){
                try {
                    InputStream is = WebViewActivity.this.getAssets().open("js/" + "nolimit.js");
                    ALog.i("shouldInterceptRequest", "use offline resource for: " + url);
                    return new WebResourceResponse("text/javascript", "UTF-8", is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            view.loadUrl("javascript:(function(){" +
                    "navigator.__defineGetter__(\"userAgent\",function(){return\"Dalvik/2.1.0 (Linux; U; Android 6.0; NEM-AL10 Build/HONORNEM-AL10) iPhone/8.0\"});window.onload=function(){for(var b=document.getElementsByClassName(\"MobileAppHeader-downloadLink\"),a=0;a<b.length;a++)b[a].style.display=\"none\";b=document.getElementsByTagName(\"img\");for(a=0;a<b.length;a++)\"\\u5e7f\\u544a\"==b[a].alt&&(b[a].style.display=\"none\")};" +
                    "})()");
            //        mAgentWeb.getUrlLoader().loadDataWithBaseURL(url,"<script src=\"https://focus.com/nolimit.js\"></script>",null,"utf-8",null);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        String url = getIntent().getStringExtra(EXTRA_URL);
        this.url = url;
        webLayout = new WebLayout(this);


        ((TextView)webLayout.getLayout().findViewById(R.id.header)).setText("网页由 " + url + " 提供");
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(container, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setWebLayout(webLayout)
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
                .interceptUnkownUrl() //拦截找不到相关页面的Scheme
                .createAgentWeb()
                .ready()
                .get();
//                .go(url);

        
        WebSettings webSettings = mAgentWeb.getAgentWebSettings().getWebSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//自适应屏幕        ☆☆
        webSettings.setDisplayZoomControls(true);
        webSettings.setUseWideViewPort(true);

        mAgentWeb.getUrlLoader().loadUrl(url);

//        mAgentWeb.getUrlLoader().loadDataWithBaseURL(url,"<script src=\"https://focus.com/nolimit.js\"></script>",null,"utf-8",null);


        /*webView.requestDisallowInterceptTouchEvent(false);
        webView.setWebViewClient(new WebViewClient());
        */
/*        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);*/
//        webView.loadUrl(url);
//        webView.loadData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.webview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.refresh:
                if (mAgentWeb != null) {
                    mAgentWeb.getUrlLoader().reload(); // 刷新
                }
                return true;

            case R.id.copy:
                if (mAgentWeb != null) {
                    ClipboardManager mClipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, this.url));
                    Toasty.success(this,"复制成功").show();
                }
                return true;
            case R.id.default_browser:
                if (mAgentWeb != null) {
                    openBrowser(mAgentWeb.getWebCreator().getWebView().getUrl());
                }
                return true;
            case R.id.default_clean:
                toCleanWebCache();
            default:
                return false;
        }
    }

    /**
     * 打开浏览器
     *
     * @param targetUrl 外部浏览器打开的地址
     */
    private void openBrowser(String targetUrl) {
        if (TextUtils.isEmpty(targetUrl) || targetUrl.startsWith("file://")) {
            Toasty.info(this, targetUrl + " 该链接无法使用浏览器打开。", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri mUri = Uri.parse(targetUrl);
        intent.setData(mUri);
        startActivity(intent);
    }


    /**
     * 清除 WebView 缓存
     */
    private void toCleanWebCache() {

        if (this.mAgentWeb != null) {
            //清理所有跟WebView相关的缓存 ，数据库， 历史记录 等。
            this.mAgentWeb.clearWebCache();
            Toast.makeText(this, "已清理缓存", Toast.LENGTH_SHORT).show();
            //清空所有 AgentWeb 硬盘缓存，包括 WebView 的缓存 , AgentWeb 下载的图片 ，视频 ，apk 等文件。
//            AgentWebConfig.clearDiskCache(this.getContext());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
