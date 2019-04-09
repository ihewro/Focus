package com.ihewro.focus.util;

import android.content.Context;

import com.google.common.base.Strings;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.htmltextview.HtmlTextView;
import com.ihewro.focus.other.HtmlImageGetterEx;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/08
 *     desc   : 显示文章
 *     version: 1.0
 * </pre>
 */
public class ArticleUtil {
    public static void setContent(Context context, FeedItem article, HtmlTextView textView) {
        if (article == null || textView == null) {
            return;
        }

        try {
            textView.setHtml(getContent(article), new HtmlImageGetterEx(textView, null, true));
        } catch (IndexOutOfBoundsException e) {
            Toasty.error(context,
                    "subscription=" + article.getFeedName() + ", desc=" + article.getSummary()).show();
        } catch (RuntimeException e) {
            Toasty.error(context,
                    "subscription=" + article.getFeedName() + ", desc=" + article.getSummary()
                            + ", message=" + e.getMessage()).show();
        }
    }

    private static String getContent(FeedItem article) {
        if (!Strings.isNullOrEmpty(article.getContent())) {
            return article.getContent();
        }
        return article.getSummary();
    }
}
