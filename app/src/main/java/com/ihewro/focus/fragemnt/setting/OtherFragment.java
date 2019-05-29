package com.ihewro.focus.fragemnt.setting;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.canking.minipay.Config;
import com.canking.minipay.MiniPayUtils;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.AboutActivity;
import com.ihewro.focus.activity.MainActivity;
import com.ihewro.focus.util.UIUtil;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherFragment extends SettingFragment {

    private Preference donate;
    private Preference about;
    private Preference fiveStar;

    @Override
    public void initView() {
        addPreferencesFromResource(R.xml.pref_other_setting);
    }

    @Override
    public void initPreferenceComponent() {
        donate = findPreference(getString(R.string.pref_key_donate));
        about = findPreference(getString(R.string.pref_key_about));
        fiveStar = findPreference(getString(R.string.pref_key_five_star));
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
        fiveStar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Uri uri = Uri.parse("market://details?id=" + UIUtil.getContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    Toasty.error(getContext(), "无法启动应用市场，请重试", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
}
