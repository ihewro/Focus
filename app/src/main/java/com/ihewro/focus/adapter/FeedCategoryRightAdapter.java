package com.ihewro.focus.adapter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.Website;
import com.ihewro.focus.callback.ImageLoaderCallback;
import com.ihewro.focus.util.ImageLoaderManager;
import com.ihewro.focus.util.StringUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FeedCategoryRightAdapter extends BaseQuickAdapter<Website, BaseViewHolder> {
    public FeedCategoryRightAdapter(@Nullable List<Website> data) {
        super(R.layout.item_adpater_right_category,data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, Website item) {
        bindItemListener(helper,item);
        helper.setText(R.id.name,item.getName());
        if (StringUtil.trim(item.getDesc()).equals("")){
            helper.setGone(R.id.desc,false);
        }else {
            helper.setGone(R.id.desc,true);
            helper.setText(R.id.desc,item.getDesc());
        }

        if (StringUtil.trim(item.getIcon()).equals("")){
            helper.setImageResource(R.id.icon,R.drawable.ic_rss_feed_grey_24dp);
        }else {
            ALog.d("ico图片地址"+item.getIcon());
            ImageLoaderManager.loadImageUrlToImageView(StringUtil.trim(item.getIcon()), (ImageView) helper.getView(R.id.icon), new ImageLoaderCallback() {
                @Override
                public void onFailed(ImageView imageView, FailReason failReason) {
                    imageView.setImageResource(R.drawable.ic_rss_feed_grey_24dp);
                }

                @Override
                public void onSuccess(ImageView imageView, Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }
            });
        }


    }

    public void bindItemListener(BaseViewHolder helper, Website item){

    }

}
