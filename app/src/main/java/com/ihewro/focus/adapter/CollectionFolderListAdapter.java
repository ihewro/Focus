package com.ihewro.focus.adapter;

import android.support.annotation.Nullable;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.CollectionFolder;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/06/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CollectionFolderListAdapter extends BaseQuickAdapter<CollectionFolder, BaseViewHolder> {
    public CollectionFolderListAdapter(@Nullable List<CollectionFolder> data) {
        super(R.layout.item_collection,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollectionFolder item) {
        helper.setText(R.id.name,item.getName());
//        helper.setText(R.id.info,)
        helper.setGone(R.id.info,true);
        ((CheckBox)(helper.getView(R.id.select))).setChecked(item.isSelect());

        initListener(helper,item);
    }

    private void initListener(BaseViewHolder helper, CollectionFolder item){

    }
}
