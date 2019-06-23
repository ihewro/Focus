package com.ihewro.focus.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

    private int lastY = 0;
    private int touchEventId = -9983761;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            View scroller = (View)msg.obj;
            if(msg.what==touchEventId) {
                if(lastY ==scroller.getScrollY()) {
                    handleStop(scroller);
                }else {
                    handler.sendMessageDelayed(handler.obtainMessage(touchEventId,scroller), 50);
                    lastY = scroller.getScrollY();
                }
            }
        }
    };



    private int mLastX = 0;
    private int mLastY = 0;
    private boolean is_current_scroll = false;//当前是否是上下滚动的
    private boolean is_vertical_scroll = false;


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                ALog.d("ACTION_DOWN");
                // 让父容器不拦截ACTION_DOWN事件
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                ALog.d("ACTION_MOVE");
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {//水平滑动
                    ALog.d("水平滑动");
                    if (!is_current_scroll){
                        // 如果水平方向滑动的距离多一点，那就表示让父容器水平滑动，子控件不滑动，让父容器拦截、消费事件
                        getParent().getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }else {//上下滑动
                    is_current_scroll = true;
                    ALog.d("上下滑动");
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                //水平滑动的时候，没有这个事件，因为被上级recyclerview消费掉了
                ALog.d("ACTION_UP");
                is_current_scroll = false;

                handler.sendMessageDelayed(handler.obtainMessage(touchEventId,MyScrollView.this), 5);

                break;
            }
            default:
                break;
        }

        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(event);
    }



    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        is_current_scroll = true;

       /* if ((t-oldt) > 1){
            is_current_scroll = true;
        }else {
            is_current_scroll = false;
        }*/
        ALog.d("上下滑动距离"+(t-oldt));

    }


    //这里写真正的事件
    private void handleStop(Object view) {
        ALog.d("停止滑动了");
        is_current_scroll = false;
        /*ScrollView scroller = (ScrollView) view;
        System.out.println(scroller.getScrollY());
        System.out.println(scroller.getHeight());*/
        //Do Something
    }


}
