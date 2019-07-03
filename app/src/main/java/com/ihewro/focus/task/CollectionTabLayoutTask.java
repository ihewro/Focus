package com.ihewro.focus.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.ihewro.focus.activity.StarActivity;
import com.ihewro.focus.bean.CollectionFolder;
import com.ihewro.focus.fragemnt.CollectionListFragment;
import com.ihewro.focus.task.listener.CollectionTabLayoutListener;

import org.litepal.LitePal;

import java.util.ArrayList;
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
public class CollectionTabLayoutTask extends AsyncTask<Void,Void,Void> {


    private CollectionTabLayoutListener layoutListener;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private List<Fragment> fragmentList = new ArrayList<>();
    final List<String> pageTitleList = new ArrayList<>();

    public CollectionTabLayoutTask(CollectionTabLayoutListener layoutListener, Activity activity) {
        this.layoutListener = layoutListener;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        List<CollectionFolder> list = LitePal.findAll(CollectionFolder.class);


        //标题列表


        for (CollectionFolder collectionFolder : list) {
            pageTitleList.add(collectionFolder.getName());
             CollectionListFragment fragment = CollectionListFragment.newInstance(collectionFolder.getId(), activity);
            fragmentList.add(fragment);
        }

        return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        layoutListener.onFinish(fragmentList,pageTitleList);

    }
}
