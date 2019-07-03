package com.ihewro.focus.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.ihewro.focus.bean.Collection;
import com.ihewro.focus.bean.CollectionAndFolderRelation;
import com.ihewro.focus.bean.CollectionFolder;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.util.DateUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;
import org.litepal.exceptions.LitePalSupportException;

import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/07/03
 *     desc   : 修复数据库功能
 *     功能：删除错误数据（收藏关系表中id为<=0的数据）、导入以前的feeditem里面的收藏数据、删除多余的collection内容（在关系表中不存在该collection）
 *     version: 1.0
 * </pre>
 */
public class FixDataTask extends AsyncTask<Void,Void,Boolean> {

    @SuppressLint("StaticFieldLeak")
    private Activity activity;

    private MaterialDialog loading;
    public FixDataTask() {
    }

    public FixDataTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        loading = new MaterialDialog.Builder(activity)
                .content("马上就好……")
                .progress(true, 0)
                .show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {


        CollectionFolder collectionFolder = new CollectionFolder("旧版本数据迁移");
        try {
            collectionFolder.saveThrows();
        }catch (LitePalSupportException e){
            ALog.d(e);
            collectionFolder.setId(LitePal.where("name = ?",collectionFolder.getName()).find(CollectionFolder.class).get(0).getId());
        }

        List<FeedItem> feedItems = LitePal.where("favorite = ?","1").find(FeedItem.class);//所有收藏的文章
        for (FeedItem feedItem: feedItems){
            Collection collection = new Collection(feedItem.getTitle(),feedItem.getFeedName(),feedItem.getDate(),feedItem.getSummary(),feedItem.getContent(),feedItem.getUrl(),Collection.FEED_ITEM,DateUtil.getNowDateRFCInt());

            try {
                collection.saveThrows();
            }catch (LitePalSupportException e){
                ALog.d(e);
                collection.setId(LitePal.where("url = ?",collection.getUrl()).find(Collection.class).get(0).getId());
            }


            //查询数据库是否有这样的关系，如果有就不需要存储的了
            List<CollectionAndFolderRelation> temp = LitePal.where("collectionid = ? and collectionfolderid = ?",
                    collection.getId() + "",collectionFolder.getId() + "").find(CollectionAndFolderRelation.class);

            //保存新的关系
            if (temp.size() == 0){
                CollectionAndFolderRelation collectionAndFolderRelation = new CollectionAndFolderRelation(collection.getId(),collectionFolder.getId());
                collectionAndFolderRelation.save();
            }

        }


        //删除错误数据
        LitePal.deleteAll(CollectionAndFolderRelation.class,"collectionid = ? or collectionfolderid = ?","0","0");

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {

        //显示成功
        if (loading.isShowing()){
            loading.dismiss();
            Toasty.success(activity,"修复成功").show();
            EventBus.getDefault().post(new EventMessage(EventMessage.DATABASE_RECOVER));
        }

    }
}
