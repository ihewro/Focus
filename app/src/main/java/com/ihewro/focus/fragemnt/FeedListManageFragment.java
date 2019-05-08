package com.ihewro.focus.fragemnt;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ihewro.focus.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedListManageFragment extends Fragment {

    private static final String FEED_FOLDER_ID = "FEED_FOLDER_ID";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;

    /**
     * 新建一个新的碎片
     *
     * @return 返回实例
     */
    public static FeedListManageFragment newInstance(int feedFolderId) {
        FeedListManageFragment fragment = new FeedListManageFragment();
        Bundle args = new Bundle();
        args.putInt(FeedListManageFragment.FEED_FOLDER_ID, feedFolderId);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_list_manage, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRecyclerView();
    }


    private void setRecyclerView(){

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
