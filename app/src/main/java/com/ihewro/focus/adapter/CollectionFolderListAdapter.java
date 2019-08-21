package com.ihewro.focus.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.CollectionFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/06/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CollectionFolderListAdapter extends BaseQuickAdapter<CollectionFolder, BaseViewHolder> {
    private List<CollectionFolder> data;
    private List<Integer> selectFolderIds = new ArrayList<>();
    public CollectionFolderListAdapter(@Nullable List<CollectionFolder> data,List<Integer> list) {
        super(R.layout.item_collection,data);
        this.data = data;
        this.selectFolderIds = list;

        initListener();

    }

    @Override
    protected void convert(final BaseViewHolder helper, CollectionFolder item) {
        helper.setText(R.id.name,item.getName());
//        helper.setText(R.id.info,)
        helper.setGone(R.id.info,false);

        //
        if (selectFolderIds.contains(item.getId())){
            ((CheckBox)(helper.getView(R.id.select))).setChecked(true);
        }else {
            ((CheckBox)(helper.getView(R.id.select))).setChecked(false);
        }

        helper.getView(R.id.select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ALog.d("什么鬼？？");
                click(v,helper.getAdapterPosition(),!((CheckBox)(v.findViewById(R.id.select))).isChecked());

            }
        });

    }

    private void initListener(){
        this.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                click(view,position,((CheckBox)(view.findViewById(R.id.select))).isChecked());
            }
        });
    }

    private void click(View view,int position,boolean status){

        CollectionFolder current = data.get(position);

        if (!status){//添加到收藏分类中
            selectFolderIds.add(current.getId());
            ((CheckBox)(view.findViewById(R.id.select))).setChecked(true);
        }else {
            //剔除掉该id的文件夹
            selectFolderIds.remove((Integer) current.getId());
            ((CheckBox)(view.findViewById(R.id.select))).setChecked(false);
        }
    }

    public List<Integer> getSelectFolderIds() {
        return selectFolderIds;
    }
}
