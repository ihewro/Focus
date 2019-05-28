package com.ihewro.focus.fragemnt.setting;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ihewro.focus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GestureFragment extends SettingFragment {

    private SwitchPreferenceCompat notOpenClick;
    private SwitchPreferenceCompat notStar;
    private SwitchPreferenceCompat notToTop;


    @Override
    public void initView() {
        addPreferencesFromResource(R.xml.pref_gesture_setting);

    }

    @Override
    public void initPreferenceComponent() {
        notOpenClick = (SwitchPreferenceCompat) findPreference(getString(R.string.pref_key_not_pull_to_open_link));
        notStar = (SwitchPreferenceCompat) findPreference(getString(R.string.pref_key_not_double_click_to_star));
        notToTop = (SwitchPreferenceCompat) findPreference(getString(R.string.pref_key_not_double_click_to_top));
    }

    @Override
    public void initPreferencesData() {
        //恢复数据


    }

    @Override
    public void initListener() {
        //保存数据

    }


}
