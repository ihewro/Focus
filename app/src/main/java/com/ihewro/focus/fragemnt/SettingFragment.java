package com.ihewro.focus.fragemnt;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.util.RSSUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment {


    private SwitchPreference use_internet_while_open;
    private Preference choose_rsshub;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_setting);

        initPreferenceComponent(getPreferenceManager());

        initPreferencesData();

        initListener();
    }


    private void initPreferenceComponent(PreferenceManager preferenceManager) {
        use_internet_while_open = (SwitchPreference) preferenceManager.findPreference(getString(R.string.pref_key_use_internet_while_open));
        choose_rsshub = preferenceManager.findPreference(getString(R.string.pref_key_rsshub_choice));

    }

    private void initPreferencesData() {
        //查询数据库

    }


    private void initListener() {
        choose_rsshub.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //显示弹窗
                int select = RSSUtil.urlIsContainsRSSHub(UserPreference.getRssHubUrl());
                List<String> list = GlobalConfig.rssHub;
                new MaterialDialog.Builder(getActivity())
                        .title("源管理")
                        .items(list)
                        .itemsCallbackSingleChoice(select, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                UserPreference.updateValueByKey(UserPreference.RSS_HUB,GlobalConfig.rssHub.get(which));
                                return true;
                            }
                        })
                        .positiveText("选择")
                        .show();

                return false;
            }
        });

        use_internet_while_open.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (use_internet_while_open.isChecked()){
                    UserPreference.updateValueByKey(UserPreference.USE_INTERNET_WHILE_OPEN,"1");
                }else {
                    UserPreference.updateValueByKey(UserPreference.USE_INTERNET_WHILE_OPEN,"0");
                }
                return false;
            }
        });
    }



}
