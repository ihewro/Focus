package com.ihewro.focus.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.ihewro.focus.R;
import com.lxj.xpopup.core.DrawerPopupView;

import java.util.Random;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/10
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FilterPopupView extends DrawerPopupView {
    TextView text;
    public FilterPopupView(@NonNull Context context) {
        super(context);
    }
    @Override
    protected int getImplLayoutId() {
        return R.layout.component_filder_drawer;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        Log.e("tag", "CustomDrawerPopupView onCreate");
        text = findViewById(R.id.text);

        text.setText(new Random().nextInt()+"");

        //通过设置topMargin，可以让Drawer弹窗进行局部阴影展示
//        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) getPopupContentView().getLayoutParams();
//        params.topMargin = 450;
    }

    @Override
    protected void onShow() {
        super.onShow();
        Log.e("tag", "CustomDrawerPopupView onShow");
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        Log.e("tag", "CustomDrawerPopupView onDismiss");
    }
}