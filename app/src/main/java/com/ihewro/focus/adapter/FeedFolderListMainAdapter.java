package com.ihewro.focus.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.focus.R;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.FeedFolder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/10
 *     desc   : {@link FeedFolderListAdapter} 为了要再写一个呢？因为前面那个适配器里面写了listener，但是这里事件不一样。
 *     为什么不把事件写在外面呢，这样就可以复用了，我懒！
 *     version: 1.0
 * </pre>
 */
public class FeedFolderListMainAdapter extends BaseItemDraggableAdapter<FeedFolder, BaseViewHolder> {

    public FeedFolderListMainAdapter(List<FeedFolder> data) {
        super(R.layout.item_feed_folder,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedFolder item) {
        helper.setText(R.id.title,item.getName());
        initListener(helper,item);

    }

    private void initListener(BaseViewHolder helper, FeedFolder item) {
        //点击切换fragment
        helper.getView(R.id.long_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换首页内容

            }
        });
    }
}
