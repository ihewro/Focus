package com.ihewro.focus.fragemnt.setting;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ihewro.focus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GestureFragment extends SettingFragment {



    @Override
    public void initView() {
        addPreferencesFromResource(R.xml.pref_gesture_setting);

    }

    @Override
    public void initPreferenceComponent() {

    }

    @Override
    public void initPreferencesData() {

    }

    @Override
    public void initListener() {

    }


}
