package com.ihewro.focus.helper;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/08/30
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MyViewHolder extends BaseViewHolder implements
        ItemTouchHelperViewHolder, View.OnClickListener{

    public MyViewHolder(View view) {
        super(view);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(Context context) {

    }

    @Override
    public void onItemClear(Context context) {

    }
}