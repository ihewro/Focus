package com.ihewro.focus.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.FeedRequire;
import com.ihewro.focus.bean.Operation;
import com.ihewro.focus.util.StringUtil;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/12
 *     desc   : 功能操作的listView
 *     version: 1.0
 * </pre>
 */
public class OperationListAdapter extends BaseQuickAdapter<Operation, BaseViewHolder> {


    public OperationListAdapter(@Nullable List<Operation> data) {
        super(R.layout.item_operation, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Operation item) {
        helper.setText(R.id.name,item.getName());
        if (StringUtil.trim(item.getInfo()).equals("")){
            helper.setText(R.id.info,item.getInfo());
        }else {
            helper.setGone(R.id.text_info,true);
        }
        helper.setImageDrawable(R.id.icon, item.getDrawable());
    }
}
