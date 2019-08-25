package com.ihewro.focus.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.blankj.ALog;
import com.ihewro.focus.R;
import com.ihewro.focus.util.StatusBarUtil;
import com.saber.chentianslideback.SlideBackActivity;

import skin.support.utils.SkinPreference;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    public void setContentView(int layoutResID) {
        if(SkinPreference.getInstance().getSkinName().equals("night")){
            super.setTheme(R.style.AppTheme_Dark);
        }else {
            super.setTheme(R.style.AppTheme);
            StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary),0);

        }
        super.setContentView(layoutResID);
    }

    /**
     * 设置 app 字体不随系统字体设置改变
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res != null) {
            Configuration config = res.getConfiguration();
            if (config != null && config.fontScale != 1.0f) {
                config.fontScale = 1.0f;
                res.updateConfiguration(config, res.getDisplayMetrics());
            }
        }
        return res;
    }
}
