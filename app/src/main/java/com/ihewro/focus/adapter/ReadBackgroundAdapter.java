package com.ihewro.focus.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.Background;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.UserPreference;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/07/03
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ReadBackgroundAdapter extends BaseQuickAdapter<Background, BaseViewHolder> {

    public ReadBackgroundAdapter(@Nullable List<Background> data) {
        super(R.layout.item_read_background,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Background item) {
        GradientDrawable myGrad = (GradientDrawable)helper.getView(R.id.round_rect).getBackground();
        myGrad.setColor(item.getColor());

        //如果当前背景颜色是用户选择的，则显示对号图标
        if (UserPreference.queryValueByKey(UserPreference.READ_BACKGROUND, String.valueOf(R.color.white)).equals(item.getColor()+"")){
            helper.setGone(R.id.check,false);
        }else {
            helper.setGone(R.id.check,true);
        }
    }
}
