package com.ihewro.focus.activity;

import android.annotation.SuppressLint;
import android.view.MenuItem;

import com.ihewro.focus.R;
import com.jaeger.library.StatusBarUtil;
import com.saber.chentianslideback.SlideBackActivity;

import skin.support.utils.SkinPreference;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/06
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@SuppressLint("Registered")
public class BackActivity extends SlideBackActivity {


    @Override
    public void setContentView(int layoutResID) {
        if(SkinPreference.getInstance().getSkinName().equals("night")){
            setTheme(R.style.AppTheme_Dark);
        }else {
            setTheme(R.style.AppTheme);
        }
        super.setContentView(layoutResID);
        setSlideBackDirection(SlideBackActivity.LEFT);

        if(!SkinPreference.getInstance().getSkinName().equals("night")){
            StatusBarUtil.setLightMode(this);
            StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        }
    }



    /**
     * 点击toolbar上的按钮事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void slideBackSuccess() {
        onBackPressed();//或者其他
    }

}
