package com.ihewro.focus.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class RecyclerViewPageChangeListenerHelper extends RecyclerView.OnScrollListener {
    private SnapHelper snapHelper;
    private OnPageChangeListener onPageChangeListener;
    private int oldPosition = -1;//防止同一Position多次触发
    private boolean firstPosition = true;

    public RecyclerViewPageChangeListenerHelper(SnapHelper snapHelper, OnPageChangeListener onPageChangeListener) {
        this.snapHelper = snapHelper;
        this.onPageChangeListener = onPageChangeListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (onPageChangeListener != null) {
            if (firstPosition){
                onPageChangeListener.onFirstScroll();
                firstPosition = false;
            }
            onPageChangeListener.onScrolled(recyclerView, dx, dy);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        int position = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        //获取当前选中的itemView
        View view = snapHelper.findSnapView(layoutManager);
        if (view != null) {
            //获取itemView的position
            position = layoutManager.getPosition(view);
        }

        if (onPageChangeListener != null) {
            onPageChangeListener.onScrollStateChanged(recyclerView, newState);
            //newState == RecyclerView.SCROLL_STATE_IDLE 当滚动停止时触发防止在滚动过程中不停触发
            if (newState == RecyclerView.SCROLL_STATE_IDLE && oldPosition != position) {
                oldPosition = position;
                onPageChangeListener.onPageSelected(position);
            }
        }
    }

    public interface OnPageChangeListener {

        void onFirstScroll();

        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        void onPageSelected(int position);
    }

}