package com.ihewro.focus.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedRequest;
import com.ihewro.focus.util.DateUtil;
import com.ihewro.focus.util.StringUtil;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/06/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FeedRequestAdapter extends BaseQuickAdapter<FeedRequest, BaseViewHolder> {
    private Activity activity;
    public FeedRequestAdapter(Activity activity, @Nullable List<FeedRequest> data) {
        super(R.layout.item_request,data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, final FeedRequest item) {
        helper.setText(R.id.date, DateUtil.getRFCStringByInt(item.getTime()));
        helper.setText(R.id.code, item.getCode()+"");
        if (StringUtil.trim(item.getReason()).equals("")){
            helper.setText(R.id.reason, "成功");
        }else {
            helper.setText(R.id.reason, "点击查看详情");
            helper.getView(R.id.reason).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(activity)
                            .title("失败原因")
                            .content(item.getReason())
                            .show();
                }
            });
        }
        helper.setText(R.id.num, item.getNum()+"");
    }
}
