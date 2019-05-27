package com.ihewro.focus.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.PostDetailActivity;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.StarItem;
import com.ihewro.focus.callback.ImageLoaderCallback;
import com.ihewro.focus.util.DataUtil;
import com.ihewro.focus.util.DateUtil;
import com.ihewro.focus.util.ImageLoaderManager;
import com.ihewro.focus.util.StringUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import skin.support.utils.SkinPreference;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/27
 *     desc   : {@link UserFeedPostsVerticalAdapter} 和这个差不多，是一个浓缩版，为什么单独再写一个呢，
 *     version: 1.0
 * </pre>
 */
public class StarItemListAdapter extends BaseQuickAdapter<StarItem, BaseViewHolder> {

    private Activity activity;
    private String feedName;
    private List<StarItem> feedItemList;

    public StarItemListAdapter(@Nullable List<StarItem> data, Activity activity) {
        super(R.layout.item_post, data);
        this.activity = activity;
        this.feedItemList = data;
    }

    @Override
    protected void convert(final BaseViewHolder helper, StarItem item) {
        //绑定事件
        bindListener(helper,item);

        ALog.d(item.getTitle() + "日期：" + item.getDate());
        helper.setText(R.id.post_title,item.getTitle());
        helper.setText(R.id.rss_name,item.getFeedName());
        helper.setText(R.id.post_summay, DataUtil.getOptimizedDesc(item.getSummary()));
        helper.setText(R.id.post_time, DateUtil.getTTimeStringByInt(item.getDate()));

        helper.setGone(R.id.markRead,true);

        String imageUrl = DataUtil.getFeedItemImageUrl(item);
        if (!StringUtil.trim(imageUrl).equals("")){
            if (!imageUrl.startsWith("http://")&& !imageUrl.startsWith("https://")){
                //说明是相对地址
                if (!imageUrl.substring(0,1).equals("/")){
                    imageUrl = "/" + imageUrl;//前面如果没有/，补足一个
                }
                imageUrl =  StringUtil.getUrlPrefix(item.getUrl()) + imageUrl;
            }
            ALog.d("图片地址是" + imageUrl);
            helper.getView(R.id.post_pic).setVisibility(View.VISIBLE);
            ImageLoaderManager.loadImageUrlToImageView(StringUtil.trim(imageUrl), (ImageView) helper.getView(R.id.post_pic), new ImageLoaderCallback() {
                @Override
                public void onFailed(ImageView imageView, FailReason failReason) {
                    imageView.setVisibility(View.GONE);
                    ALog.d("图片加载失败！！");
                }

                @Override
                public void onSuccess(ImageView imageView, Bitmap bitmap) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                }

                @Override
                public void onStart(ImageView imageView) {
                    if (SkinPreference.getInstance().getSkinName().equals("night")) {
                        imageView.setImageResource(R.drawable.ic_night_loading);
                    } else {
                        imageView.setImageResource(R.drawable.ic_day_loading);
                    }

                }
            });
        }else {
            helper.getView(R.id.post_pic).setVisibility(View.GONE);
        }

        if (item.isFavorite()){
            helper.getView(R.id.favorite).setVisibility(View.VISIBLE);
            helper.setText(R.id.star,"取消收藏");
        }else {
            helper.getView(R.id.favorite).setVisibility(View.GONE);
            helper.setText(R.id.star,"收藏");
        }




    }


    private void bindListener(final BaseViewHolder helper, final StarItem item){

        helper.getView(R.id.star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                item.setFavorite(!item.isFavorite());
                //保存到数据库
                item.save();
                notifyDataSetChanged();
                //修改对应的feedItem

                ContentValues values = new ContentValues();
                values.put("favorite", "0");
                LitePal.update(FeedItem.class,values,item.getFeedItemId());


                //通知
                if (item.isFavorite()){
                    Toasty.success(activity,"收藏成功").show();
                }else {
                    Toasty.success(activity,"取消收藏成功").show();
                }


            }
        });

        //跳转页面
        helper.getView(R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Integer> list = new ArrayList<>();
                for (StarItem starItem: feedItemList){
                    list.add(starItem.getId());
                }
                PostDetailActivity.activityStart(activity,helper.getAdapterPosition(),list,false,true);
            }
        });

    }

}
