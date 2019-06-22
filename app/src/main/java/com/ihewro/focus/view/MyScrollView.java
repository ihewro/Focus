package com.ihewro.focus.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;
import android.widget.Toast;

import com.blankj.ALog;
import com.ihewro.focus.activity.PostDetailActivity;
import com.ihewro.focus.util.UIUtil;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MyScrollView extends NestedScrollView {

    public MyScrollView(Context context) {
        super(context);
    }



    public MyScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    private int mLastX = 0;
    private int mLastY = 0;
    private boolean is_current_scroll = false;//当前是否是上下滚动的


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // 让父容器不拦截ACTION_DOWN事件
                is_current_scroll = false;
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
//                    ALog.d("水平滑动");
                    if (!is_current_scroll){
                        // 如果水平方向滑动的距离多一点，那就表示让父容器水平滑动，子控件不滑动，让父容器拦截事件
                        getParent().getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }else {
                    is_current_scroll = true;
//                    ALog.d("上下滑动");
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                is_current_scroll = false;
                break;
            }
            default:
                break;
        }

        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(event);
    }

}
