package com.ihewro.focus.view;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.ihewro.focus.R;
import com.just.agentweb.IWebLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/06/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class WebContentLayout implements IWebLayout {

    private Activity mActivity;
    private final SmartRefreshLayout mTwinklingRefreshLayout;
    private WebView mWebView = null;

    public WebContentLayout(Activity activity) {
        this.mActivity = activity;
        mTwinklingRefreshLayout = (SmartRefreshLayout) LayoutInflater.from(activity).inflate(R.layout.item_post_detail, null);
        mWebView = mTwinklingRefreshLayout.findViewById(R.id.post_content);
        mTwinklingRefreshLayout.setEnableLoadMore(false);
    }

    @NonNull
    @Override
    public ViewGroup getLayout() {
        return mTwinklingRefreshLayout;
    }

    @Nullable
    @Override
    public WebView getWebView() {
        return mWebView;
    }

}

