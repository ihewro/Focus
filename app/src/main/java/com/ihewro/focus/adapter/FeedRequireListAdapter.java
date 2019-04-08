package com.ihewro.focus.adapter;

import android.support.annotation.Nullable;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.FeedRequire;

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
public class FeedRequireListAdapter extends BaseQuickAdapter<FeedRequire, BaseViewHolder> {

    public FeedRequireListAdapter(@Nullable List<FeedRequire> data) {
        super(R.layout.item_feed_require,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedRequire item) {
        helper.setText(R.id.chinaName,item.getChinaName());
        ((EditText)helper.getView(R.id.input)).setHint(item.getDesc());
    }
}
