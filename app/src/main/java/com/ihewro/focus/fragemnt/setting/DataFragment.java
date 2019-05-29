package com.ihewro.focus.fragemnt.setting;

import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.text.InputType;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedFolder;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.callback.FileOperationCallback;
import com.ihewro.focus.task.RecoverDataTask;
import com.ihewro.focus.util.DateUtil;
import com.ihewro.focus.util.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.List;
import java.util.regex.Pattern;

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
    private Preference clean_data;
    private Preference database_version;


    @Override
    public void initView() {
        addPreferencesFromResource(R.xml.pref_data_setting);
    }

    @Override
    public void initPreferenceComponent() {
        feed_info = findPreference(getString(R.string.pref_key_feed_num));

        back_up = findPreference(getString(R.string.pref_key_backup));
        recover_data = findPreference(getString(R.string.pref_key_recover));
        clean_data = findPreference(getString(R.string.pref_key_clean_database));
        database_version = findPreference(getString(R.string.pref_key_database_version));
    }

    @Override
    public void initPreferencesData() {
        feed_info.setSummary(LitePal.count(FeedFolder.class) + "个分类 " +LitePal.count(Feed.class)+"个订阅 " + LitePal.count(FeedItem.class) + "篇文章");
        database_version.setSummary(LitePal.getDatabase().getVersion() + "");
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

        clean_data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //显示输入弹窗
                new MaterialDialog.Builder(getContext())
                        .title("输入需要清理的数目")
                        .content("该数目的文章会被删除，有且仅有在存在备份的情况下才可以恢复。优先清理历史悠久的文章")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                String num = dialog.getInputEditText().getText().toString().trim();
                                Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
                                if (pattern.matcher(num).matches()) {
                                    //清理数据库
                                    List<FeedItem> feedItems = LitePal.where("favorite = ?","0").limit(Integer.parseInt(num)).order("date").find(FeedItem.class);
                                    for (FeedItem feedItem:feedItems){
                                        feedItem.delete();
                                    }

                                    Toasty.info(getActivity(),"清理成功！").show();
                                    EventBus.getDefault().post(new EventMessage(EventMessage.DATABASE_RECOVER));

                                }else {
                                    //输入错误
                                    Toasty.info(getActivity(),"老实说你输入的是不是数字").show();
                                }
                            }
                        }).show();

                return false;
            }
        });

    }
}
