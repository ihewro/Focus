package com.ihewro.focus.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.callback.OnDoubleClickListener;
import com.ihewro.focus.helper.DoubleClick;
import com.ihewro.focus.util.DateUtil;
import com.ihewro.focus.util.PostUtil;
import com.ihewro.focus.util.WebViewUtil;
import com.ihewro.focus.view.PostFooter;
import com.ihewro.focus.view.PostHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.List;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class PostDetailListAdapter extends BaseQuickAdapter<FeedItem, BaseViewHolder> {


    SmartRefreshLayout refreshLayout;

    private Activity context;
    private ScrollView scrollView;
    private Toolbar toolbar;

    public PostDetailListAdapter(Activity context, Toolbar toolbar, @Nullable List<FeedItem> data) {
        super(R.layout.item_post_detail,data);
        this.context = context;
        this.toolbar = toolbar;
    }

    @Override
    protected void convert(BaseViewHolder helper, final FeedItem item) {

        scrollView = helper.getView(R.id.post_turn);
        //设置文章内容
        PostUtil.setContent(context, item, ((WebView) helper.getView(R.id.post_content)));
        helper.setText(R.id.post_title, item.getTitle());
        helper.setText(R.id.post_time, DateUtil.getTimeStringByInt(item.getDate()));
        helper.setText(R.id.feed_name, item.getFeedName());

        if (!item.isRead()) {
            //如果这个文章没有阅读过则滚动到顶部
            scrollView.fullScroll(ScrollView.FOCUS_UP);
        }

        refreshLayout = helper.getView(R.id.refreshLayout);

        initListener(item);
    }


    private void initListener(final FeedItem currentFeedItem){

        refreshLayout.setRefreshHeader(new PostHeader(context, currentFeedItem));
        refreshLayout.setRefreshFooter(new PostFooter(context, currentFeedItem));
        //使上拉加载具有弹性效果
        refreshLayout.setEnableAutoLoadMore(false);
        //禁止越界拖动（1.0.4以上版本）
        refreshLayout.setEnableOverScrollDrag(false);
        //关闭越界回弹功能
        refreshLayout.setEnableOverScrollBounce(false);
        // 这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的：
        refreshLayout.setEnableAutoLoadMore(false);

        refreshLayout.setEnableRefresh(false);//禁止下拉动作

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();
                //打开外链
                openLink(currentFeedItem);
            }
        });
    }

    private void openLink(FeedItem feedItem) {
        String url = feedItem.getUrl();
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (pattern.matcher(url).matches()){
            Toasty.info(context,"该文章没有外链哦").show();
        }else {
            WebViewUtil.openLink(url, context);
        }
    }


}
