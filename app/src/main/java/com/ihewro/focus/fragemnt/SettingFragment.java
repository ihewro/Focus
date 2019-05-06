package com.ihewro.focus.fragemnt;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ihewro.focus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment {


    private SwitchPreference use_internet_while_open;

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
    }

    private void initPreferencesData() {
    }


    private void initListener() {
    }



}
