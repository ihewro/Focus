package com.ihewro.focus.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.ihewro.focus.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CollectionFolderManageActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_folder_manage);
        ButterKnife.bind(this);
    }


    private void initRecyclerView(){

    }
}
