package com.ihewro.focus.fragemnt;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ihewro.focus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedListManageFragment extends Fragment {


    public FeedListManageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed_list_manage, container, false);
    }

}
