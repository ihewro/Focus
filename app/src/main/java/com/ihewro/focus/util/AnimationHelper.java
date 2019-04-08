package com.ihewro.focus.util;

import android.app.Activity;
import android.os.Build;

import com.ihewro.focus.R;

/**
 * @description:
 * @author: Match
 * @date: 1/30/17
 */

public class AnimationHelper {

    public static void overridePendingTransition(Activity activity) {
        if (activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }

    public static void setFadeTransition(Activity activity) {
        if (activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}
