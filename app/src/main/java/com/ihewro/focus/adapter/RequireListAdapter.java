package com.ihewro.focus.adapter;

import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.FeedRequire;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/11
 *     desc   : {@link FeedRequireListAdapter} 这适配器重复写了，保留现在这个，删掉那个
 *     version: 1.0
 * </pre>
 */
public class RequireListAdapter extends BaseQuickAdapter<FeedRequire, BaseViewHolder> {


    public RequireListAdapter(@Nullable List<FeedRequire> data) {
        super(R.layout.item_require_info,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedRequire item) {
        helper.setText(R.id.info,item.getChinaName());
        ((EditText)helper.getView(R.id.input)).setHint(item.getDesc());
    }
}
