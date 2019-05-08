package com.ihewro.focus.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.FeedFolder;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FeedFolderListAdapter extends BaseQuickAdapter<FeedFolder, BaseViewHolder> {

    public FeedFolderListAdapter(@Nullable List<FeedFolder> data) {
        super(R.layout.item_feed_folder,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedFolder item) {

    }
}
