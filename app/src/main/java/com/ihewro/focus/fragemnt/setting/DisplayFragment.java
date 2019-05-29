package com.ihewro.focus.fragemnt.setting;

import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.ihewro.focus.R;
import com.ihewro.focus.bean.UserPreference;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DisplayFragment extends SettingFragment {

    private SwitchPreferenceCompat not_show_image_in_list;

    @Override
    public void initView() {
        addPreferencesFromResource(R.xml.pref_display_setting);
    }

    @Override
    public void initPreferenceComponent() {
        not_show_image_in_list = (SwitchPreferenceCompat) findPreference(getString(R.string.pref_key_not_image_in_list));
    }

    @Override
    public void initPreferencesData() {
        if (UserPreference.queryValueByKey(UserPreference.not_show_image_in_list,"0").equals("1")){//开启禁用
            not_show_image_in_list.setChecked(true);
        }

    }

    @Override
    public void initListener() {

        //保存数据
        not_show_image_in_list.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (not_show_image_in_list.isChecked()){
                    UserPreference.updateOrSaveValueByKey(UserPreference.not_show_image_in_list,"1");
                }else {
                    UserPreference.updateOrSaveValueByKey(UserPreference.not_show_image_in_list,"0");
                }
                return false;
            }
        });

    }
}
