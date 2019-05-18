package com.ihewro.focus.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.Toast;

import com.ihewro.focus.activity.MainActivity;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/11
 *     desc   :
 *     link   : {@link:https://blog.csdn.net/MXiaoChao/article/details/52254455}
 *     version: 1.0
 * </pre>
 */

public class ScrollWebView extends WebView {
    private float startx;
    private float starty;
    private float offsetx;
    private float offsety;


    private float downX ;    //按下时 的X坐标
    private float downY ;    //按下时 的Y坐标


    public ScrollWebView(Context context) {
        super(context);
    }

    public ScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x= event.getX();
        float y = event.getY();

        String action = "";


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                /*startx = event.getX();
                starty = event.getY();*/

                downX = x;
                downY = y;

                Log.e("MotionEvent", "webview按下");
                break;
            case MotionEvent.ACTION_MOVE:
                /*Log.e("MotionEvent", "webview滑动");
                offsetx = Math.abs(event.getX() - startx);
                offsety = Math.abs(event.getY() - starty);

                if (offsetx > offsety) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    Log.e("MotionEvent", "屏蔽了父控件");
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    Log.e("MotionEvent", "事件传递给父控件");
                }*/

                //获取到距离差
                float dx= x-downX;
                float dy = y-downY;
                //防止是按下也判断
                if (Math.abs(dx)>8&&Math.abs(dy)>8) {
                    //通过距离差判断方向
                    int orientation = getOrientation(dx, dy);
                    switch (orientation) {
                        case 'r':
                            action = "右";
                            break;
                        case 'l':
                            action = "左";
                            break;
                        case 't':
                            action = "上";
                            break;
                        case 'b':
                            action = "下";
                            break;
                    }
                }

                if (action.equals("上") || action.equals("下")){
                    //如果是上下滑动则屏蔽父控件
                    getParent().requestDisallowInterceptTouchEvent(true);
                    Log.e("MotionEvent", "屏蔽了父控件");
                }else {
                    //如果是左右滑动则不屏蔽父控件
                    getParent().requestDisallowInterceptTouchEvent(false);
                    Log.e("MotionEvent", "事件传递给父控件");
                }

                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 根据距离差判断 滑动方向
     * @param dx X轴的距离差
     * @param dy Y轴的距离差
     * @return 滑动的方向
     */
    private int getOrientation(float dx, float dy) {
        Log.e("Tag","========X轴距离差："+dx);
        Log.e("Tag","========Y轴距离差："+dy);
        if (Math.abs(dx)>Math.abs(dy)){
            //X轴移动
            return dx>0?'r':'l';
        }else{
            //Y轴移动
            return dy>0?'b':'t';
        }
    }
}
