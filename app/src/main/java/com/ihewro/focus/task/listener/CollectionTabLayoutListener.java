package com.ihewro.focus.task.listener;

import android.support.v4.app.Fragment;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/07/03
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface CollectionTabLayoutListener {
    void onFinish(List<Fragment> fragmentList, List<String> pageTitleList);

}

