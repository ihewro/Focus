package com.ihewro.focus.fragemnt;


import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.ihewro.focus.R;
import com.ihewro.focus.activity.MainActivity;
import com.ihewro.focus.adapter.UserFeedPostsVerticalAdapter;
import com.ihewro.focus.bean.EventMessage;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.callback.RequestFeedItemListCallback;
import com.ihewro.focus.decoration.DividerItemDecoration;
import com.ihewro.focus.decoration.SuspensionDecoration;
import com.ihewro.focus.helper.MyLinearLayoutManager;
import com.ihewro.focus.task.RequestFeedListDataService;
import com.ihewro.focus.task.RequestFeedListDataTask;
import com.ihewro.focus.util.UIUtil;
import com.ihewro.focus.view.FilterPopupView;
import com.mikepenz.materialize.util.UIUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * 用户的最新订阅信息文章列表的碎片
 */
public class UserFeedUpdateContentFragment extends Fragment {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private SuspensionDecoration mDecoration;
    List<FeedItem> eList = new ArrayList<FeedItem>();
    List<FeedItem> tempList = new ArrayList<FeedItem>();
    public static final String FEED_LIST_ID = "FEED_LIST_ID";

    UserFeedPostsVerticalAdapter adapter;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;

    private View view;//toolbar的view,用来显示列表依存的view
    private View subView;//toolbar下面的文字view,用来显示当前未读数目
    private boolean isFirstOpen = true;//首次打开
    private ArrayList<String> feedIdList = new ArrayList<>();

    private boolean isconnet = false;//
    private int feedItemNum;
    private int feedItemNumTemp;

    private int notReadNum = 0;
    private Snackbar snackbar;

    @SuppressLint("ValidFragment")
    public UserFeedUpdateContentFragment(View view,View subView) {
        this.view = view;
        this.subView = subView;
    }

    public UserFeedUpdateContentFragment() {
    }

    /**
     * 新建一个新的碎片
     *
     * @return 返回实例
     */
    public static UserFeedUpdateContentFragment newInstance(ArrayList<String> feedIdList,View view,View subView) {
        UserFeedUpdateContentFragment fragment = new UserFeedUpdateContentFragment(view,subView);
        Bundle args = new Bundle();
        args.putStringArrayList(UserFeedUpdateContentFragment.FEED_LIST_ID,feedIdList);
        fragment.setArguments(args);
        fragment.isFirstOpen = true;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            feedIdList = getArguments().getStringArrayList(UserFeedUpdateContentFragment.FEED_LIST_ID);
        }
        EventBus.getDefault().register(this);
        updateNotReadNum();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_feed_update_content, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEmptyView();

        bindListener();
        refreshLayout.autoRefresh();
        refreshLayout.setEnableLoadMore(false);//禁止加载更多
        //使上拉加载具有弹性效果
        refreshLayout.setEnableAutoLoadMore(false);
        //禁止越界拖动（1.0.4以上版本）
        refreshLayout.setEnableOverScrollDrag(false);
        //关闭越界回弹功能
        refreshLayout.setEnableOverScrollBounce(false);
        // 这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的：
        refreshLayout.setEnableAutoLoadMore(false);

    }


    public void initEmptyView() {
        //初始化列表
        MyLinearLayoutManager linearLayoutManager = new MyLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new UserFeedPostsVerticalAdapter(eList, getActivity());
        adapter.bindToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(mDecoration = new SuspensionDecoration(getActivity(), eList));


        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    /**
     * 获取用户的所有订阅的文章
     */
    public void requestAllData(){
        Intent intent = new Intent(getActivity(), RequestFeedListDataService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(intent);
        } else {
            getActivity().startService(intent);
        }
        getActivity().bindService(intent,connection,BIND_AUTO_CREATE);

    }


    private RequestFeedListDataService.MyBinder myBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, final IBinder iBinder) {
            //TODO:主线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //子线程
                    whileServiceConnect(iBinder);
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //停止服务
            myBinder.stopService();
            isconnet = false;
            ALog.d("活动与服务解除了绑定");
        }
    };


    //子线程
    private void whileServiceConnect(final IBinder iBinder){
        //子线程
        isconnet = true;
        myBinder = (RequestFeedListDataService.MyBinder) iBinder;

        List<Feed> feedList = new ArrayList<>();//
        if (feedIdList.size() >0){
            for (int i = 0; i < feedIdList.size(); i++) {
                feedList.add(LitePal.find(Feed.class,Integer.parseInt(feedIdList.get(i))));
            }
        }else {//为空表示显示所有的feedId
            feedList = LitePal.findAll(Feed.class);
            for (Feed feed:feedList){
                feedIdList.add(feed.getId()+"");
            }
        }

        final List<Feed> finalFeedList = feedList;

        UIUtil.runOnUiThread(getActivity(),new Runnable() {
            @Override
            public void run() {
                //主线程
                myBinder.initParameter((TextView)subView,getActivity(), view, isFirstOpen, finalFeedList, new RequestFeedItemListCallback() {
                    @Override
                    public void onBegin() {
                        //主线程

                    }
                    @Override
                    public void onUpdate(final List<FeedItem> feedItems, final int newNum) {
                        //主线程
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (newNum > 0){//有新文章时候才会去更新界面
                                    //根据是否显示新文章来判断
                                    UIUtil.runOnUiThread(getActivity(),new Runnable() {
                                        @Override
                                        public void run() {
                                            ALog.d("请求过程中！");

                                            snackbar= Snackbar.make(recyclerView, newNum + "篇新文章，点击显示", Snackbar.LENGTH_INDEFINITE)
                                                    .setActionTextColor(Color.WHITE)
                                                    .setAction("显示", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            //点击的时候执行刷新界面
                                                            ALog.d("请求过程中刷新界面！");

                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    eList.clear();
                                                                    eList.addAll(feedItems);

                                                                    UIUtil.runOnUiThread(getActivity(), new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (eList.size()==0){
                                                                                adapter.setNewData(null);
                                                                                adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);
                                                                            }else {
                                                                                //一定是网络请求才会调用这里，所以用diff比较
                                                                                adapter.setNewDataByDiff(feedItems,notReadNum);
                                                                            }

                                                                            //TODO:滚动到添加数据的地方
//                                                                            recyclerView.smoothScrollToPosition();
                                                                        }
                                                                    });
                                                                }
                                                            }).start();
                                                            //最后再去更新就行
                                                        }

                                                    });

                                            snackbar.show();
                                        }
                                    });

                                }
                            }
                        }).start();
                    }


                    @Override
                    public void onFinish(final List<FeedItem> feedList, final int newNum) {

                         ALog.d("请求结束了！");
                        //主线程
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //子线程
                                eList.clear();
                                eList.addAll(feedList);

//                                ALog.d("列表数目" + eList.size());
                               if (newNum > 0){//获取到新的数据才自动备份
                                   //备份数据库
                                   UIUtil.autoBackUpWhenItIsNecessary();
                               }

                                UIUtil.runOnUiThread(getActivity(),new Runnable() {
                                    @Override
                                    public void run() {
                                        //主线程
                                        if (eList.size()==0){
                                            adapter.setNewData(null);
                                            adapter.setEmptyView(R.layout.simple_empty_view,recyclerView);
                                        }else {
                                            if (newNum > 0){//网络请求再使用diff比较
                                                adapter.setNewDataByDiff(feedList,notReadNum);
                                            }else {
                                                adapter.setNewData(eList);
                                            }
                                        }

                                        //显示通知
                                        if (!isFirstOpen){
                                            if (newNum > 0 ){
                                                if (snackbar!=null && snackbar.isShown()){
                                                    snackbar.dismiss();
                                                }
                                                Toasty.success(getActivity(),"共有"+newNum+"篇新文章").show();
                                            }else {
                                                Toasty.success(getActivity(),"暂无新内容").show();
                                            }
                                        }

                                        //下次刷新请求数据
                                        isFirstOpen = false;

                                        //取消刷新
                                        refreshLayout.finishRefresh(true);

                                        updateNotReadNum();
                                        if (newNum>0){
                                            //TODO: 如果不是网络请求，不用发消息
                                            //更新侧边栏和其他接收这个通知的组件
                                            EventBus.getDefault().post(new EventMessage(EventMessage.MAIN_READ_NUM_EDIT));
                                        }

                                        //解除绑定
                                        if (isconnet){
                                            isconnet = false;
                                            Objects.requireNonNull(getActivity()).unbindService(connection);
                                        }
                                    }
                                });
                            }
                        }).start();

                    }
                });
                myBinder.startTask();
            }
        });
    }

    public void bindListener(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestAllData();
            }
        });


    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity()!=null && isconnet){//当活动被回收的时候，服务也必须停止
            getActivity().unbindService(connection);
        }
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }


    public ArrayList<String> getFeedIdList() {
        return feedIdList;
    }

    public void updateData(ArrayList<String> feedIdList) {
        this.feedIdList = feedIdList;
        this.isFirstOpen = true;
        refreshLayout.autoRefresh();


    }

    private void updateNotReadNum(){
        //修改顶部的未读数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                notReadNum  = 0;
                if (feedIdList.size() >0){
                    for (int i = 0; i < feedIdList.size(); i++) {
                        notReadNum += LitePal.where("read = ? and feedid = ?","0",feedIdList.get(i)).count(FeedItem.class);
                    }
                }else {//为空表示显示所有的feedId
                    notReadNum = LitePal.where("read = ?","0").count(FeedItem.class);
                }
                UIUtil.runOnUiThread(getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        if (notReadNum == 0){
                            ((TextView)subView).setText("无未读文章");
                        }else {
                            ((TextView)subView).setText("共有"+notReadNum+"篇未读");
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity()!=null && isconnet){//当活动被回收的时候，服务也必须停止
            try {
                getActivity().unbindService(connection);
            }catch (IllegalArgumentException e){//获取没有注册
                ALog.d(e);
            }
            isconnet = false;
        }
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshUI(final EventMessage eventBusMessage) {

        //更新未读数目
        if (EventMessage.feedItemReadStatusOperation.contains(eventBusMessage.getType())){
            updateNotReadNum();
        }
        if (Objects.equals(eventBusMessage.getType(), EventMessage.MAKE_READ_STATUS_BY_INDEX_LIST)) {//已读状态修改
            //更新已读标志
            List<Integer> list = eventBusMessage.getIntIds();
            for (Integer indexInList: list){
                if (indexInList!=-1){
                    eList.get(indexInList).setRead(true);
                    adapter.notifyItemChanged(indexInList);
                }
            }

        }
        else if (Objects.equals(eventBusMessage.getType(), EventMessage.MAKE_READ_STATUS_BY_ID_LIST)){//已读状态修改
            //寻找到id的位置
            List<Integer> list = eventBusMessage.getIntIds();

            for (Integer idInList: list){
                int pos = -1;
                for (int i = 0;i<eList.size();i++){
                    if (eList.get(i).getId() == idInList){
                        pos = i;
                        break;
                    }
                }
                eList.get(pos).setRead(true);
                adapter.notifyItemChanged(pos);
            }

        }
        else if (Objects.equals(eventBusMessage.getType(), EventMessage.MAKE_STAR_STATUS_BY_INDEX)){//收藏状态修改
            //更新收藏状态
            int indexInList = eventBusMessage.getInteger();
            boolean flag = eventBusMessage.isFlag();
            eList.get(indexInList).setFavorite(flag);
            adapter.notifyItemChanged(indexInList);
        }else if (Objects.equals(eventBusMessage.getType(), EventMessage.MAKE_STAR_STATUS_BY_ID)){//收藏状态修改
            //寻找到id的位置
            int pos = -1;
            for (int i = 0;i<eList.size();i++){
                if (eList.get(i).getId() == eventBusMessage.getInteger()){
                    pos = i;
                    break;
                }
            }
            boolean flag = eventBusMessage.isFlag();
            eList.get(pos).setFavorite(flag);
            adapter.notifyItemChanged(pos);
        }
        else if (Objects.equals(eventBusMessage.getType(), EventMessage.MARK_FEED_READ) || Objects.equals(eventBusMessage.getType(), EventMessage.MARK_FEED_FOLDER_READ)){//有文章的已读状态修改
            //检查feedid是否在本碎片的内容内
            //如果在，则让这部分
            List<Integer> feedIdList = new ArrayList<>();
            if (Objects.equals(eventBusMessage.getType(), EventMessage.MARK_FEED_READ)){
                feedIdList.add(eventBusMessage.getInteger());
            }else{//整个文件夹都标记为已读
                List<Feed>feedList = LitePal.where("feedfolderid = ?", String.valueOf(eventBusMessage.getInteger())).find(Feed.class);
                for(Feed feed:feedList){
                    feedIdList.add(feed.getId());
                }
            }
            //找到当前内容是否有在 标记已读的文件夹
            for (int i = 0; i< eList.size();i++){
                if (feedIdList.contains(eList.get(i).getFeedId())){
                    eList.get(i).setRead(true);//设置为已读
                    adapter.notifyItemChanged(i);//修改UI
                }
            }

        }else if (Objects.equals(eventBusMessage.getType(),EventMessage.DELETE_FEED) || Objects.equals(eventBusMessage.getType(),EventMessage.DELETE_FEED_FOLDER)){//删除了订阅或者文件夹，直接显示全部文章

            updateData(new ArrayList<String>());
            ((TextView)view.findViewById(R.id.toolbar_title)).setText("全部文章");
        }else if (Objects.equals(eventBusMessage.getType(),EventMessage.IMPORT_OPML_FEED) || Objects.equals(eventBusMessage.getType(),EventMessage.DATABASE_RECOVER)){//恢复数据，通过OPML或者数据库的方式
            //显示所有文章
            updateData(new ArrayList<String>());
            ((TextView)view.findViewById(R.id.toolbar_title)).setText("全部文章");
        }else if (Objects.equals(eventBusMessage.getType(),EventMessage.GO_TO_LIST_TOP)){
            //列表回顶部

            if (feedIdList.size()>0){
                recyclerView.smoothScrollToPosition(0);//这个方法只保证指定的item被滑动到屏幕中，意味着自下往上滑的话，可以将指定item置顶，但是如果已经在屏幕中的话，那他就不会起作用，并且如果是自上往下滑的话，则置顶的item就会被滑到底部
            }
        }
    }

    public int getNotReadNum(){
        return this.notReadNum;
    }

    public int getFeedItemNum(){
        this.feedItemNum = 0;
        if (feedIdList.size() >0){
            for (int i = 0; i < feedIdList.size(); i++) {
                this.feedItemNum+= LitePal.where("feedid = ?",feedIdList.get(i)).count(FeedItem.class);
            }
        }else {//为空表示显示所有的feedId
            this.feedItemNum = LitePal.count(FeedItem.class);
        }
        return this.feedItemNum;
    }
}
