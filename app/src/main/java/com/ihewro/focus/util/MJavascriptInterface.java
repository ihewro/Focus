package com.ihewro.focus.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
    private Context activity;
    private String[] imageUrls;
    private WebView webView;

    public MJavascriptInterface(Context context, String[] imageUrls, WebView webView) {
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
    public void openUrl(final String url) {
        new MaterialDialog.Builder(activity)
                .title("即将前往")
                .content("点击「确定」将会访问该链接地址")
                .positiveText("确定")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        WebViewUtil.openLink(url, (Activity) activity);

                    }
                })
        .show();
    }

}