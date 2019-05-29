package com.ihewro.focus.fragemnt.setting;


import android.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.ihewro.focus.R;
import com.ihewro.focus.bean.UserPreference;

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
        if (UserPreference.queryValueByKey(UserPreference.notOpenClick,"0").equals("1")){//开启禁用
            notOpenClick.setChecked(true);
        }

        if (UserPreference.queryValueByKey(UserPreference.notStar,"0").equals("1")){//开启禁用
            notStar.setChecked(true);
        }
        if (UserPreference.queryValueByKey(UserPreference.notToTop,"0").equals("1")){//开启禁用
            notToTop.setChecked(true);
        }
    }

    @Override
    public void initListener() {

        //保存数据
        notStar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (notStar.isChecked()){
                    UserPreference.updateOrSaveValueByKey(UserPreference.notStar,"1");
                }else {
                    UserPreference.updateOrSaveValueByKey(UserPreference.notStar,"0");
                }
                return false;
            }
        });


        notOpenClick.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (notOpenClick.isChecked()){
                    UserPreference.updateOrSaveValueByKey(UserPreference.notOpenClick,"1");
                }else {
                    UserPreference.updateOrSaveValueByKey(UserPreference.notOpenClick,"0");
                }
                return false;
            }
        });

        notToTop.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (notToTop.isChecked()){
                    UserPreference.updateOrSaveValueByKey(UserPreference.notToTop,"1");
                }else {
                    UserPreference.updateOrSaveValueByKey(UserPreference.notToTop,"0");
                }
                return false;
            }
        });

    }


}
