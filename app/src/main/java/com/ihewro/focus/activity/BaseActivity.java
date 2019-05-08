package com.ihewro.focus.activity;

import android.annotation.SuppressLint;
import android.view.MenuItem;

import com.saber.chentianslideback.SlideBackActivity;

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
public class BaseActivity extends SlideBackActivity {


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setSlideBackDirection(SlideBackActivity.LEFT);

        //setStatusBar();
//        defaultListener();
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
        finish();//或者其他
    }

}
