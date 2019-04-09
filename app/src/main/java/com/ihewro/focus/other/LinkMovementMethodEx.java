package com.ihewro.focus.other;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import com.ihewro.focus.activity.PicActivity;

import es.dmoral.toasty.Toasty;

/**
 * @description:
 * @author: Match
 * @date: 3/18/17
 */

public class LinkMovementMethodEx extends LinkMovementMethod {
    static LinkMovementMethodEx sInstance;

    public static LinkMovementMethodEx getInstance() {
        if (sInstance == null) {
            sInstance = new LinkMovementMethodEx();
        }

        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {

        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ImageSpan[] images = buffer.getSpans(off, off, ImageSpan.class);

            if (images.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    onClick(widget, images[0]);
                }
                return true;
            }
        }

        try {
            return super.onTouchEvent(widget, buffer, event);
        } catch (ActivityNotFoundException e) {
            Toasty.error(widget.getContext(),e.getMessage()).show();
        }

        return false;
    }

    private void onClick(TextView widget, ImageSpan imageSpan) {
        Activity activity = (Activity) widget.getContext();
        PicActivity.activityStart(activity,imageSpan.getSource());
    }
}
