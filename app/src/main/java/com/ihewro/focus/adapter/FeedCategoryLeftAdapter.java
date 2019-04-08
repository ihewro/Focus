package com.ihewro.focus.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.WebsiteCategory;

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
public class FeedCategoryLeftAdapter extends BaseQuickAdapter<WebsiteCategory, BaseViewHolder> {

    public int currentPosition  = 0;

    public FeedCategoryLeftAdapter(@Nullable List<WebsiteCategory> data) {
        super(R.layout.item_adpater_left_category,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WebsiteCategory item) {
        if (currentPosition == helper.getAdapterPosition()){
            helper.getView(R.id.iv_select).setVisibility(View.VISIBLE);
        }else {
            helper.getView(R.id.iv_select).setVisibility(View.GONE);
        }

        helper.setText(R.id.tv_item,item.getName());
    }

    public void setCurrentPosition(int position){
        this.currentPosition = position;
    }
}
