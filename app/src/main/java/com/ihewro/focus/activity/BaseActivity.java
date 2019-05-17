package com.ihewro.focus.activity;

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
        super.setContentView(layoutResID);

        if(!SkinPreference.getInstance().getSkinName().equals("night")){
//            StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
//            StatusBarUtil.setLightMode(this);
            StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary),0);
        }
    }

    @Override
    public void setTheme(int resid) {
        ALog.d("主题名称",SkinPreference.getInstance().getSkinName());
        if(SkinPreference.getInstance().getSkinName().equals("night")){
            super.setTheme(R.style.AppTheme_Dark);
        }else {
            super.setTheme(R.style.AppTheme);
        }
    }
}
