package com.ihewro.focus.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/06
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FeedSearchAdapter extends BaseQuickAdapter<FeedItem, BaseViewHolder> {


    public FeedSearchAdapter(@Nullable List<FeedItem> data) {
        super(R.layout.item_search_result,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedItem item) {
        helper.setText(R.id.result_txt,item.getTitle());
    }
}
