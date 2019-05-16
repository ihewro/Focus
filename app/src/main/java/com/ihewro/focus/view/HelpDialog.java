package com.ihewro.focus.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ihewro.focus.R;
import com.ihewro.focus.util.WebViewUtil;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/16
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class HelpDialog extends DialogFragment {

    private String content;
    public static HelpDialog create(boolean darkTheme, int accentColor,String content) {
        HelpDialog dialog = new HelpDialog();
        Bundle args = new Bundle();
        args.putBoolean("dark_theme", darkTheme);
        args.putInt("accent_color", accentColor);
        dialog.setArguments(args);
        dialog.setContent(content);
        return dialog;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View customView;
        try {
            customView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_help_view, null);
        } catch (InflateException e) {
            throw new IllegalStateException("This device does not support Web Views.");
        }
        MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .theme(getArguments().getBoolean("dark_theme") ? Theme.DARK : Theme.LIGHT)
                        .title("帮助信息")
                        .customView(customView, false)
                        .positiveText(android.R.string.ok)
                        .build();

        final WebView webView = customView.findViewById(R.id.webview);
//        webView.loadData(this.content, "text/html", "UTF-8");
        WebViewUtil.LoadHtmlIntoWebView(webView,this.content,getContext());
        return dialog;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
