package com.ihewro.focus.fragemnt.setting;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canking.minipay.Config;
import com.canking.minipay.MiniPayUtils;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.AboutActivity;
import com.ihewro.focus.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherFragment extends SettingFragment {

    private Preference donate;
    private Preference about;
    @Override
    public void initView() {
        addPreferencesFromResource(R.xml.pref_other_setting);
    }

    @Override
    public void initPreferenceComponent() {
        donate = findPreference(getString(R.string.pref_key_donate));
        about = findPreference(getString(R.string.pref_key_about));
    }

    @Override
    public void initPreferencesData() {

    }

    @Override
    public void initListener() {
        donate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MiniPayUtils.setupPay(getActivity(), new Config.Builder("FKX07840DBMQMUHP92W1DD", R.drawable.alipay, R.drawable.wechatpay).build());
                return false;
            }
        });

        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AboutActivity.activityStart(getActivity());
                return false;
            }
        });
    }
}
