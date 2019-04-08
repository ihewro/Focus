package com.ihewro.focus.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.PostDetailActivity;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.util.DateUtil;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/03/13
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class UserFeedPostsVerticalAdapter extends BaseQuickAdapter<FeedItem, BaseViewHolder> {

    Activity activity;
    private String feedName;

    public UserFeedPostsVerticalAdapter(@Nullable List<FeedItem> data, Activity activity) {
        super(R.layout.item_post, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedItem item) {
        helper.setText(R.id.post_title,item.getTitle());
        helper.setText(R.id.rss_name,item.getFeedName());
        helper.setText(R.id.post_summay,item.getSummary());
        helper.setText(R.id.post_time, DateUtil.getTTimeStringByInt(item.getDate()));
        bindListener(helper,item);
    }

    public void bindListener(final BaseViewHolder helper, final FeedItem item){
        helper.getView(R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostDetailActivity.activityStart(activity,item.getId());
            }
        });

    }
}
