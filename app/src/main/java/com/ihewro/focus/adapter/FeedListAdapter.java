package com.ihewro.focus.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.Feed;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/04/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FeedListAdapter  extends BaseQuickAdapter<Feed, BaseViewHolder> {

    public FeedListAdapter(@Nullable List<Feed> data) {
        super(R.layout.item_feed,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Feed item) {
        helper.setText(R.id.name,item.getName());
        helper.setText(R.id.desc,item.getDesc());
    }
}
