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
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.view.MyScrollView;
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
//            ALog.d("接收到网站的标题");
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
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            ((TextView)webLayout.getLayout().findViewById(R.id.header)).setText("网页由 " + url + " 提供");

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
//                .get();
                .go(url);


        WebSettings webSettings = mAgentWeb.getAgentWebSettings().getWebSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//自适应屏幕        ☆☆
        webSettings.setDisplayZoomControls(true);
        webSettings.setUseWideViewPort(true);


        webSettings.setUserAgentString("Mozilla/5.0 (Windows Phone 10.0; Android 9.1; Microsoft; Lumia 950 XL Dual SIM; KaiOS; Java) Gecko/68 Firefox/68 SearchCraft/2.8.2 baiduboxapp/4.3.0.10");

//        mAgentWeb.getUrlLoader().loadUrl(url);

//        mAgentWeb.getUrlLoader().loadDataWithBaseURL(url,"<script src=\"https://focus.com/nolimit.js\"></script>",null,"utf-8",null);


        /*webView.requestDisallowInterceptTouchEvent(false);
        webView.setWebViewClient(new WebViewClient());
        */
/*        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);*/
//        webView.loadUrl(url);
//        webView.loadData();




        //双击顶栏回顶部事件
        final GestureDetector gestureDetector1 = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mAgentWeb.getWebCreator().getWebView().scrollTo(0,0);
                return super.onDoubleTap(e);
            }
        });


        toolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector1.onTouchEvent(motionEvent);
            }
        });
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
