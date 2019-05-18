package com.ihewro.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.focus.GlobalConfig;
import com.ihewro.focus.R;
import com.ihewro.focus.adapter.FeedCategoryLeftAdapter;
import com.ihewro.focus.adapter.FeedCategoryRightAdapter;
import com.ihewro.focus.adapter.FeedListAdapter;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedRequire;
import com.ihewro.focus.bean.Help;
import com.ihewro.focus.bean.Website;
import com.ihewro.focus.bean.WebsiteCategory;
import com.ihewro.focus.http.HttpInterface;
import com.ihewro.focus.http.HttpUtil;
import com.ihewro.focus.view.RequireListPopupView;
import com.lxj.xpopup.XPopup;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import skin.support.utils.SkinPreference;

import static com.ihewro.focus.GlobalConfig.serverUrl;

public class FeedCategoryActivity extends BackActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_left)
    RecyclerView recyclerLeft;
    @BindView(R.id.recycler_right)
    RecyclerView recyclerRight;
    FeedCategoryLeftAdapter leftAdapter;
    FeedCategoryRightAdapter rightAdapter;

    List<WebsiteCategory> websiteCategoryList = new ArrayList<>();
    List<Website> websiteList = new ArrayList<>();
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FeedListAdapter adapter;
    private List<Feed> feedList = new ArrayList<>();


    public static void activityStart(Activity activity) {
        Intent intent = new Intent(activity, FeedCategoryActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_category);
        ButterKnife.bind(this);

        initEmptyView();

        bindListener();

        requestLeftData();


    }

    public void initEmptyView() {

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerLeft.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        recyclerRight.setLayoutManager(linearLayoutManager2);


        leftAdapter = new FeedCategoryLeftAdapter(websiteCategoryList);
        rightAdapter = new FeedCategoryRightAdapter(websiteList);


        leftAdapter.bindToRecyclerView(recyclerLeft);
        rightAdapter.bindToRecyclerView(recyclerRight);

        leftAdapter.setEmptyView(R.layout.simple_empty_view);
        rightAdapter.setEmptyView(R.layout.simple_empty_view);

        initSearchAdapter();
    }


    public void bindListener() {


        //搜索在线源🔍
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO:提交才开始搜索


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //输入过程中不做任何操作
                return false;
            }
        });



        leftAdapter.setOnItemClickListener(new FeedCategoryLeftAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ((FeedCategoryLeftAdapter) adapter).setCurrentPosition(position);
                leftAdapter.notifyDataSetChanged();
                requestRightData(websiteCategoryList.get(position).getName());
            }
        });

        rightAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FeedListActivity.activityStart(FeedCategoryActivity.this, websiteList.get(position).getName());
            }
        });
    }

    public void requestLeftData() {
        Retrofit retrofit = HttpUtil.getRetrofit("bean", serverUrl, 10, 10, 10);
        Call<List<WebsiteCategory>> request = retrofit.create(HttpInterface.class).getCategoryList();
        request.enqueue(new Callback<List<WebsiteCategory>>() {
            @Override
            public void onResponse(Call<List<WebsiteCategory>> call, Response<List<WebsiteCategory>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    websiteCategoryList.addAll(response.body());
                    leftAdapter.setNewData(websiteCategoryList);
                    requestRightData(websiteCategoryList.get(0).getName());
                } else {
                    ALog.d("请求失败" + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<WebsiteCategory>> call, Throwable t) {
                ALog.d("请求失败2" + t.getMessage());
            }
        });
    }

    public void requestRightData(String categoryName) {
        Retrofit retrofit = HttpUtil.getRetrofit("bean", GlobalConfig.serverUrl, 10, 10, 10);
        Call<List<Website>> request = retrofit.create(HttpInterface.class).getWebsiteListByCategory(categoryName);

        request.enqueue(new Callback<List<Website>>() {
            @Override
            public void onResponse(Call<List<Website>> call, Response<List<Website>> response) {
                if (response.isSuccessful()) {
                    websiteList.clear();
                    websiteList.addAll(response.body());
                    rightAdapter.setNewData(websiteList);
                } else {
                    ALog.d("请求失败" + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Website>> call, Throwable t) {
                ALog.d("请求失败2" + t.getMessage());
            }
        });
    }


    private void initSearchAdapter(){
        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FeedListAdapter(feedList);
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEmptyView(R.layout.simple_empty_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(SkinPreference.getInstance().getSkinName().equals("night")){
            getMenuInflater().inflate(R.menu.feed_night, menu);
        }else {
            getMenuInflater().inflate(R.menu.feed_night, menu);
        }
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_add_by_url:
                //弹窗
                List<FeedRequire> list = new ArrayList<>();
                list.add(new FeedRequire("订阅地址","举例：https://www.ihewro.com/feed",FeedRequire.SET_URL));
                list.add(new FeedRequire("订阅名称","随意给订阅取一个名字",FeedRequire.SET_NAME));
                new XPopup.Builder(FeedCategoryActivity.this)
//                        .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                        .asCustom(new RequireListPopupView(FeedCategoryActivity.this,list,"手动订阅","适用于高级玩家",new Help(false),new Feed(),getSupportFragmentManager()))
                        .show();

                break;
        }

        return true;
    }
}
