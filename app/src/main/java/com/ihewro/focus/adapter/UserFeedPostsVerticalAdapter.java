package com.ihewro.focus.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.PostDetailActivity;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.callback.ImageLoaderCallback;
import com.ihewro.focus.util.DataUtil;
import com.ihewro.focus.util.DateUtil;
import com.ihewro.focus.util.ImageLoaderManager;
import com.ihewro.focus.util.StringUtil;
import com.ihewro.focus.util.UIUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import skin.support.utils.SkinPreference;

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
    private List<FeedItem> feedItemList;

    public UserFeedPostsVerticalAdapter(@Nullable List<FeedItem> data, Activity activity) {
        super(R.layout.item_post, data);
        this.activity = activity;
        this.feedItemList = data;
    }

    @Override
    protected void convert(final BaseViewHolder helper, FeedItem item) {
        //绑定事件
        bindListener(helper,item);

        ALog.d(item.getTitle() + "日期：" + item.getDate());
        helper.setText(R.id.post_title,item.getTitle());
        helper.setText(R.id.rss_name,item.getFeedName());
        helper.setText(R.id.post_summay, DataUtil.getOptimizedDesc(item.getSummary()));
        helper.setText(R.id.post_time, DateUtil.getTTimeStringByInt(item.getDate()));


        int not_read_color;
        int read_color;
        int not_read_content_color;
        int read_content_color;

        if(SkinPreference.getInstance().getSkinName().equals("night")){
            read_color = R.color.text_read_night;
            read_content_color = R.color.text_read_content_night;

            not_read_color = R.color.text_unread_night;
            not_read_content_color = R.color.text_unread_content_night;

        }else {
            read_color = R.color.text_read;
            read_content_color = R.color.text_read_content;

            not_read_color = R.color.text_unread;
            not_read_content_color = R.color.text_unread_content;
        }

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
        if (item.isRead()){
            ALog.d("是否已读" + item.isRead());
            helper.setTextColor(R.id.post_title,activity.getResources().getColor(read_color));
            helper.setTextColor(R.id.rss_name,activity.getResources().getColor(read_content_color));
            helper.setTextColor(R.id.post_summay,activity.getResources().getColor(read_content_color));
            helper.setTextColor(R.id.post_time,activity.getResources().getColor(read_content_color));
            helper.setText(R.id.markRead,"标记未读");
        }else {
            helper.setTextColor(R.id.post_title,activity.getResources().getColor(not_read_color));
            helper.setTextColor(R.id.rss_name,activity.getResources().getColor(not_read_content_color));
            helper.setTextColor(R.id.post_summay,activity.getResources().getColor(not_read_content_color));
            helper.setTextColor(R.id.post_time,activity.getResources().getColor(not_read_content_color));
            helper.setText(R.id.markRead,"标记已读");
        }
        if (item.isFavorite()){
            helper.getView(R.id.favorite).setVisibility(View.VISIBLE);
            helper.setText(R.id.star,"取消收藏");
        }else {
            helper.getView(R.id.favorite).setVisibility(View.GONE);
            helper.setText(R.id.star,"收藏");
        }

    }


    private void bindListener(final BaseViewHolder helper, final FeedItem item){

        helper.getView(R.id.markRead).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setRead(!item.isRead());
                notifyDataSetChanged();
                //保存到数据库
                FeedItem temp = LitePal.find(FeedItem.class,item.getId());
                temp.setRead(item.isRead());
                temp.save();

                //通知
                if (item.isRead()){
                    Toasty.success(activity,"标记已读成功").show();
                }else {
                    Toasty.success(activity,"标记未读成功").show();
                }
                EventBus.getDefault().post(new EventMessage(EventMessage.EDIT_ITEM_READ));
            }
        });

        helper.getView(R.id.star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setFavorite(!item.isFavorite());
                notifyDataSetChanged();

                //保存到数据库
                item.setFavorite(item.isFavorite());
                item.save();

                //通知
                if (item.isFavorite()){
                    Toasty.success(activity,"收藏成功").show();
                }else {
                    Toasty.success(activity,"取消收藏成功").show();
                }
            }
        });

        helper.getView(R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserPreference.queryValueByKey(UserPreference.FIRST_INTRO_MAIN_FEED_ITEM, "0").equals("0")){
                    initTapView(helper.getView(R.id.operations));
                }else {
                    ArrayList<Integer> list = new ArrayList<>();
                    for (FeedItem feedItem: feedItemList){
                        list.add(feedItem.getId());
                    }
                    PostDetailActivity.activityStart(activity,helper.getAdapterPosition(),list,true);
                }
            }
        });

        helper.getView(R.id.operations).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new XPopup.Builder(activity)
                        .atView(helper.getView(R.id.operations))  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                        .asAttachList(new String[]{"将以上部分标记为已读", "将以下部分标记为已读"},
                                new int[]{},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        if (position == 0){
                                            markReadOfTop(helper,item);
                                        }else if (position == 1){
                                            markReadOfBottom(helper,item);
                                        }
                                    }
                                })
                        .show();
            }
        });
    }

    private void markReadOfTop(final BaseViewHolder helper, final FeedItem item){
        for (int i = 0;i<helper.getAdapterPosition();i++){
            feedItemList.get(i).setRead(true);
            feedItemList.get(i).save();
            notifyItemChanged(i);

        }
    }

    private void markReadOfBottom(final BaseViewHolder helper, final FeedItem item){
        for (int i = helper.getAdapterPosition();i<feedItemList.size();i++){
            feedItemList.get(i).setRead(true);
            feedItemList.get(i).save();
            notifyItemChanged(i);
        }
    }


    private void initTapView(View view){
        TapTargetView.showFor(activity,                 // `this` is an Activity
                TapTarget.forView(view, "触发新手教程！", "先别急的进入！点击该图标也可以快速标记已读哦，文章左滑也可以快速标记收藏和已读哦！\n")
                        .cancelable(false)
                        .drawShadow(true)
                        .titleTextColor(R.color.colorAccent)
                        .descriptionTextColor(R.color.text_secondary_dark)
                        .tintTarget(true)
                        .targetCircleColor(android.R.color.black),
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        UserPreference.updateOrSaveValueByKey(UserPreference.FIRST_INTRO_MAIN_FEED_ITEM,"1");
                    }
                });

    }

}
