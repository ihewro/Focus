package com.ihewro.focus.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.lxj.xpopup.impl.PartShadowPopupView;
import com.lxj.xpopup.util.XPopupUtils;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CustomPartShadowPopupView extends PartShadowPopupView {
    public CustomPartShadowPopupView(@NonNull Context context) {
        super(context);
    }
    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_part_shadow_popup;
    }

    TextView text;
    @Override
    protected void onCreate() {
        super.onCreate();
        text = findViewById(R.id.text);
        Log.e("tag","CustomPartShadowPopupView onCreate");
        findViewById(R.id.btnClose).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.ch).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText(text.getText()+"\n 啦啦啦啦啦啦");

            }
        });
    }

    @Override
    protected void onShow() {
        super.onShow();
        Log.e("tag","CustomPartShadowPopupView onShow");
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        Log.e("tag","CustomPartShadowPopupView onDismiss");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ALog.d("????");
        // 如果自己接触到了点击，并且不在PopupContentView范围内点击，则进行判断是否是点击事件
        // 如果是，则dismiss
        Rect rect = new Rect();
        getPopupContentView().getGlobalVisibleRect(rect);
        if (!XPopupUtils.isInRect(event.getX(), event.getY(), rect)) {//不在点击范围
            ALog.d("不在范围内");
        }else {
            ALog.d("在范围内");
            performClick();
        }
        return true;
    }
}
