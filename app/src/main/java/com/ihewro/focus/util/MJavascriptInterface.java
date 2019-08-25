package com.ihewro.focus.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.MainActivity;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.view.FeedOperationPopupView;
import com.ihewro.focus.view.ImageManagePopupView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.ImageViewerPopupView;
import com.lxj.xpopup.interfaces.XPopupImageLoader;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import static com.ihewro.focus.util.UIUtil.getResources;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class MJavascriptInterface {
    private Activity activity;
    private String[] imageUrls;
    private WebView webView;

    public MJavascriptInterface(Activity context, String[] imageUrls, WebView webView) {
        this.activity = context;
        this.imageUrls = imageUrls;
        this.webView = webView;
    }

    @android.webkit.JavascriptInterface
    public void openImage(String img) {

        ALog.d("点击了图片" +img);
       ImageLoaderManager.showSingleImageDialog(activity,img,null);
    }





    @android.webkit.JavascriptInterface
    public void longClickImage(String img) {
        ALog.d("长按图片" +img);
        //显示下拉底部弹窗
        new XPopup.Builder(activity)
                .asCustom(new ImageManagePopupView(activity,img,null))
                .show();
    }

    @android.webkit.JavascriptInterface
    public void openUrl(String url) {
        WebViewUtil.openLink(url, activity);
    }

}