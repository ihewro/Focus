package com.ihewro.focus.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ihewro.focus.R;
import com.lxj.xpopup.impl.PartShadowPopupView;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/10
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FeedsLoadingPopupView extends PartShadowPopupView {

    private ProgressBar progressBar;
    private TextView progressInfo;
    private ProgressBar circle;

    public FeedsLoadingPopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.component_feeds_loading_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        progressBar = findViewById(R.id.real_progressbar);
        progressInfo = findViewById(R.id.progress_info);

        progressInfo.setText("开始同步……");
        progressBar.setProgress(0);

    }

    @Override
    protected void onShow() {
        super.onShow();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        Log.e("tag","CustomPartShadowPopupView onDismiss");
    }

    public ProgressBar getProgressBar() {
        if (progressBar == null){
            return findViewById(R.id.real_progressbar);
        }
        return progressBar;
    }

    public TextView getProgressInfo(){
        if (progressInfo == null){
            return findViewById(R.id.progress_info);
        }
        return progressInfo;
    }

    public ProgressBar getCircle() {
        if (circle == null){
            return findViewById(R.id.circle);
        }
        return circle;
    }
}
