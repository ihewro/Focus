package com.ihewro.focus.adapter;

import android.support.annotation.Nullable;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.Website;
import com.ihewro.focus.util.StringUtil;

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
        if (StringUtil.trim(item.getDesc()).equals("")){
            helper.setGone(R.id.desc,true);
            ALog.d("怎么回事？？"+ StringUtil.trim(item.getDesc()));
        }else {
            helper.setGone(R.id.desc,false);
            helper.setText(R.id.desc,item.getDesc());
        }

    }

    public void bindItemListener(BaseViewHolder helper, Website item){

    }

}
