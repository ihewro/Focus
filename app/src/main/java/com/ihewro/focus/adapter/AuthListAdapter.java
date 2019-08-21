package com.ihewro.focus.adapter;

import android.app.Activity;
import android.view.View;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.Auth;
import com.ihewro.focus.bean.CollectionFolder;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/08/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class AuthListAdapter extends BaseItemDraggableAdapter<Auth, BaseViewHolder> {

    private Activity activity;

    public AuthListAdapter(List<Auth> data,Activity activity) {
        super(R.layout.item_auth,data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Auth item) {
        helper.setText(R.id.name,item.getName());
        helper.setText(R.id.desc,item.getDesc());

        helper.getView(R.id.ll_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录弹窗，增加账户

            }
        });
    }
}
