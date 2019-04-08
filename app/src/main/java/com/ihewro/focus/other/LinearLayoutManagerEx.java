package com.ihewro.focus.other;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * @description:
 * @author: Match
 * @date: 12/18/16
 */

public class LinearLayoutManagerEx extends LinearLayoutManager {

    public LinearLayoutManagerEx(Context context) {
        super(context);
    }

    /**
     * Trick to avoid crash
     * http://stackoverflow.com/questions/31759171/recyclerview-and-java-lang-indexoutofboundsexception-inconsistency-detected-in
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
