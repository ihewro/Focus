package com.ihewro.focus.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.PostDetailActivity;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.util.DateUtil;
import com.ihewro.focus.util.UIUtil;

import org.litepal.LitePal;

import java.util.List;

import es.dmoral.toasty.Toasty;

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

    private Activity activity;
    private String feedName;

    public UserFeedPostsVerticalAdapter(@Nullable List<FeedItem> data, Activity activity) {
        super(R.layout.item_post, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedItem item) {
        ALog.d(item.getTitle() + "日期：" + item.getDate());
        helper.setText(R.id.post_title,item.getTitle());
        helper.setText(R.id.rss_name,item.getFeedName());
        helper.setText(R.id.post_summay,item.getSummary());
        helper.setText(R.id.post_time, DateUtil.getTTimeStringByInt(item.getDate()));
        bindListener(helper,item);


        if (item.isRead()){
            ALog.d("是否已读" + item.isRead());

            helper.setTextColor(R.id.rss_name,activity.getResources().getColor(R.color.text_read));
            helper.setTextColor(R.id.post_title,activity.getResources().getColor(R.color.text_read));
            helper.setTextColor(R.id.post_summay,activity.getResources().getColor(R.color.text_read));
            helper.setTextColor(R.id.post_time,activity.getResources().getColor(R.color.text_read));
            helper.setText(R.id.markRead,"标记未读");
            bindListenerIfRead(helper,item);
        }else {
            helper.setTextColor(R.id.post_title,activity.getResources().getColor(R.color.text_unread));
            helper.setTextColor(R.id.rss_name,activity.getResources().getColor(R.color.text_unread));
            helper.setTextColor(R.id.post_summay,activity.getResources().getColor(R.color.text_unread));
            helper.setTextColor(R.id.post_time,activity.getResources().getColor(R.color.text_unread));
            helper.setText(R.id.markRead,"标记已读");
            bindListenerIfUnRead(helper,item);
        }
        if (item.isFavorite()){
            helper.getView(R.id.favorite).setVisibility(View.VISIBLE);
            helper.setText(R.id.star,"取消收藏");
            bindListenerIfStar(helper,item);
        }else {
            helper.getView(R.id.favorite).setVisibility(View.GONE);
            helper.setText(R.id.star,"收藏");
            bindListenerIfUnStar(helper,item);
        }

    }

    private void bindListener(final BaseViewHolder helper, final FeedItem item){
        helper.getView(R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostDetailActivity.activityStart(activity,item.getIid(),helper.getAdapterPosition());
            }
        });
    }


    private void bindListenerIfRead(final BaseViewHolder helper, final FeedItem item){

        helper.getView(R.id.markRead).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.success(UIUtil.getContext(),"标记未读").show();
                item.setRead(false);
                updateItem(item,false,"read");
                notifyItemChanged(helper.getAdapterPosition());
            }
        });
    }



    private void bindListenerIfUnRead(final BaseViewHolder helper, final FeedItem item){
        helper.getView(R.id.markRead).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.success(activity,"标记已读").show();
                item.setRead(true);
                updateItem(item,true,"read");
                notifyItemChanged(helper.getAdapterPosition());
            }
        });
    }


    private void bindListenerIfStar(final BaseViewHolder helper, final FeedItem item){
        helper.getView(R.id.star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.success(UIUtil.getContext(),"取消收藏成功").show();
                item.setFavorite(false);
                updateItem(item,false,"star");
                notifyItemChanged(helper.getAdapterPosition());
            }
        });
    }



    private void bindListenerIfUnStar(final BaseViewHolder helper, final FeedItem item){
        helper.getView(R.id.star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.success(activity,"收藏成功").show();
                item.setFavorite(true);
                updateItem(item,true,"star");
                notifyItemChanged(helper.getAdapterPosition());
            }
        });
    }


    public void updateItem(FeedItem item,boolean status,String type){
        FeedItem temp = LitePal.where("iid = ?",item.getIid()).limit(1).find(FeedItem.class).get(0);
        if (type.equals("star")){
            temp.setFavorite(status);
        }else {
            temp.setRead(status);
        }
        temp.save();
    }





}
