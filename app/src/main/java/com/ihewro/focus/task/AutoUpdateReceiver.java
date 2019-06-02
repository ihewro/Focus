package com.ihewro.focus.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ihewro.focus.bean.UserPreference;

public class AutoUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //当前定时器已开启
        UserPreference.updateOrSaveValueByKey(UserPreference.tim_is_open,"1");

        String interval = UserPreference.queryValueByKey(UserPreference.tim_interval,"-1");
        if (!interval.equals("-1")){//这个地方判断是因为有可能在定时器运行期间，用户取消了定时器，这样
            Intent i = new Intent(context, TimingService.class);
            context.startService(i);
        }
    }
}
