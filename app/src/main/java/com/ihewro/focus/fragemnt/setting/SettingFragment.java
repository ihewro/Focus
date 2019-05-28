package com.ihewro.focus.fragemnt.setting;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.ihewro.focus.fragemnt.BasePreferenceFragmentCompat;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class SettingFragment extends BasePreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        initView();
        initPreferenceComponent();
        initPreferencesData();
        initListener();
    }

    public abstract void initView();
    public abstract void initPreferenceComponent();
    public abstract void initPreferencesData();
    public abstract void initListener();
}