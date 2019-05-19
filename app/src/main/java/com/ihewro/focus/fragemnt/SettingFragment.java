package com.ihewro.focus.fragemnt;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.ErrorActivity;
import com.ihewro.focus.activity.MainActivity;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.callback.FileOperationCallback;
import com.ihewro.focus.task.RecoverDataTask;
import com.ihewro.focus.util.DateUtil;
import com.ihewro.focus.util.FileUtil;
import com.ihewro.focus.util.RSSUtil;

import org.litepal.LitePal;

import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment {


    private SwitchPreference use_internet_while_open;
    private Preference choose_rsshub;
    private Preference feed_info;
    private SwitchPreference auto_name;
    private Preference back_up;
    private Preference recover_data;
    private Preference aboot;


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
        feed_info = preferenceManager.findPreference(getString(R.string.pref_key_feed_num));
        auto_name = (SwitchPreference) preferenceManager.findPreference(getString(R.string.pref_key_auto_name));

        back_up =  preferenceManager.findPreference(getString(R.string.pref_key_backup));
        recover_data =  preferenceManager.findPreference(getString(R.string.pref_key_recover));
        aboot =  preferenceManager.findPreference(getString(R.string.pref_key_about));

    }

    private void initPreferencesData() {
        //查询数据库
        if (UserPreference.queryValueByKey(UserPreference.USE_INTERNET_WHILE_OPEN,"0").equals("0")){
            use_internet_while_open.setChecked(false);
        }else {
            use_internet_while_open.setChecked(true);
        }
        feed_info.setSummary(LitePal.count(Feed.class)+"个订阅");

        if (UserPreference.queryValueByKey(UserPreference.AUTO_SET_FEED_NAME,"0").equals("0")){
            auto_name.setChecked(false);
        }else {
            auto_name.setChecked(true);
        }


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
                                UserPreference.updateOrSaveValueByKey(UserPreference.RSS_HUB,GlobalConfig.rssHub.get(which));
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
                    UserPreference.updateOrSaveValueByKey(UserPreference.USE_INTERNET_WHILE_OPEN,"1");
                }else {
                    UserPreference.updateOrSaveValueByKey(UserPreference.USE_INTERNET_WHILE_OPEN,"0");
                }
                return false;
            }
        });

        auto_name.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (auto_name.isChecked()){
                    UserPreference.updateOrSaveValueByKey(UserPreference.AUTO_SET_FEED_NAME,"1");
                }else {
                    UserPreference.updateOrSaveValueByKey(UserPreference.AUTO_SET_FEED_NAME,"0");
                }
                return false;
            }
        });


        back_up.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getActivity())
                        .title("为什么需要备份？")
                        .content("本应用没有云端同步功能，本地备份功能可以尽可能保证您的数据库安全。\n数据库备份不同于OPML导出，不仅包含所有订阅信息，还包括您的已读、收藏信息。但仅限在Focus应用内部交换使用。\n应用会在您有任何操作数据库的操作时候自动备份最新的一次数据库。")
                        .positiveText("开始备份")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                FileUtil.copyFileToTarget(getActivity().getDatabasePath("focus.db").getAbsolutePath(), GlobalConfig.appDirPath + "database/" + DateUtil.getNowDateStr() + ".db", new FileOperationCallback() {
                                    @Override
                                    public void onFinish() {
                                        Toasty.success(getActivity(),"备份数据成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .show();
                return  false;
            }
        });

        recover_data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new RecoverDataTask(getActivity()).execute();

                return false;
            }
        });


        aboot.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });


    }



}
