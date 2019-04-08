package com.ihewro.focus.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.Website;

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
    protected void convert(BaseViewHolder helper, Website item) {
        bindItemListener(helper,item);
        helper.setText(R.id.name,item.getName());
        helper.setText(R.id.desc,item.getDesc());

    }

    public void bindItemListener(BaseViewHolder helper, Website item){

    }

}
