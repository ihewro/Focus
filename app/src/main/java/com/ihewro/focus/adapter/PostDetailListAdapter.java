package com.ihewro.focus.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.PostDetailActivity;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.PostSetting;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.helper.CustomTabActivityHelper;
import com.ihewro.focus.helper.WebviewFallback;
import com.ihewro.focus.util.DateUtil;
import com.ihewro.focus.util.PostUtil;
import com.ihewro.focus.view.PostFooter;
import com.ihewro.focus.view.PostHeader;
import com.ihewro.focus.view.pageturn.factory.PicturesPageFactory;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

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


    @BindView(R.id.post_title)
    TextView postTitle;
    @BindView(R.id.post_time)
    TextView postTime;
    @BindView(R.id.post_content)
    WebView postContent;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.feed_name)
    TextView feedName;

    private Context context;
    private int indexInList;
    private PostSetting postSetting;

    public PostDetailListAdapter(Context context, int indexInList, PostSetting postSetting, @Nullable List<FeedItem> data) {
        super(R.layout.item_post_detail,data);
        this.context = context;
        this.indexInList = indexInList;
        this.postSetting = postSetting;
    }

    @Override
    protected void convert(BaseViewHolder helper, final FeedItem item) {


        //设置文章内容
        PostUtil.setContent(context, item, postContent);
        helper.setText(R.id.post_title,item.getTitle());
        helper.setText(R.id.post_time,DateUtil.getTimeStringByInt(item.getDate()));
        helper.setText(R.id.feed_name,item.getFeedName());


        //将该文章标记为已读，并且通知首页修改布局
        item.setRead(true);
        item.save();
        if (indexInList == -1) {//TODO:如果是-1表示根据id来修改UI的已读信息
            EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_READ_STATUS_BY_ID, item.getId()));
        } else {
            EventBus.getDefault().post(new EventMessage(EventMessage.MAKE_READ_STATUS_BY_INDEX, helper.getAdapterPosition()));
        }

        //根据设置，设置文字的UI

    }

}
