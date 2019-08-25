package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ihewro.focus.R;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.stephentuso.welcome.WelcomeHelper;

public class NewComerActivity extends WelcomeActivity {


    public static void activityStart(Activity activity) {
        Intent intent = new Intent(activity, NewComerActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.colorAccent)
                .page(new BasicPage(R.drawable.ic_history_white_24dp,
                        "过去","“从不缺少信息”。其实也并不是特别需要查看那些刷新也刷不完的“推荐内容”")
                        .background(R.color.color6)
                )
                .page(new BasicPage(R.drawable.ic_center_focus_weak_white_24dp,
                        "现在",
                        "有新的选择。聚合关注的内容。追求减法，碎片阅读也要“适可而止”")
                        .background(R.color.color7)
                )
                .page(new BasicPage(R.drawable.ic_thumb_up_white_24dp,
                        "开始使用",
                        "使用OPML轻松导入过去数据，或者尝试手动添加订阅吧，发现市场发现更多")
                        .background(R.color.color8)
                )
                .swipeToDismiss(true)
                .build();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public static String welcomeKey() {
        return "WelcomeScreen";
    }

}
