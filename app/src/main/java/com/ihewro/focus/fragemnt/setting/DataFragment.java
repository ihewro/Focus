package com.ihewro.focus.fragemnt.setting;

import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.callback.FileOperationCallback;
import com.ihewro.focus.task.RecoverDataTask;
import com.ihewro.focus.util.DateUtil;
import com.ihewro.focus.util.FileUtil;

import org.litepal.LitePal;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DataFragment extends SettingFragment{

    private Preference back_up;
    private Preference recover_data;
    private Preference feed_info;


    @Override
    public void initView() {
        addPreferencesFromResource(R.xml.pref_data_setting);
    }

    @Override
    public void initPreferenceComponent() {
        feed_info = findPreference(getString(R.string.pref_key_feed_num));

        back_up = findPreference(getString(R.string.pref_key_backup));
        recover_data = findPreference(getString(R.string.pref_key_recover));

    }

    @Override
    public void initPreferencesData() {
        feed_info.setSummary(LitePal.count(FeedFolder.class) + "个分类 " +LitePal.count(Feed.class)+"个订阅 " + LitePal.count(FeedItem.class) + "篇文章");

    }

    @Override
    public void initListener() {

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

    }
}
