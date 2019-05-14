package com.ihewro.focus.activity;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ihewro.focus.R;
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
            setTheme(R.style.AppTheme_Dark);
        }else {
            setTheme(R.style.AppTheme);
        }

        super.setContentView(layoutResID);
    }




}
