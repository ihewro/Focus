package com.ihewro.focus.fragemnt;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ihewro.focus.R;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.bean.PostSetting;
import com.ihewro.focus.bean.UserPreference;
import com.ihewro.focus.util.DateUtil;
import com.ihewro.focus.util.PostUtil;
import com.ihewro.focus.util.WebViewUtil;
import com.ihewro.focus.view.MyScrollView;
import com.ihewro.focus.view.PostFooter;
import com.ihewro.focus.view.PostHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import skin.support.utils.SkinPreference;

/**
 * 文章详情页面的碎片
 */
public class PostDetailFragment extends Fragment {


    @BindView(R.id.post_time)
    TextView postTime;
    @BindView(R.id.post_title)
    TextView postTitle;
    @BindView(R.id.feed_name)
    TextView feedName;
    @BindView(R.id.post_content)
    WebView postContent;
    @BindView(R.id.post_turn)
    MyScrollView postTurn;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;

    public PostDetailFragment() {
        // Required empty public constructor
    }

    private FeedItem feedItem;

    @SuppressLint("ValidFragment")
    public PostDetailFragment(FeedItem feedItem) {
        this.feedItem = feedItem;
    }

    /**
     * 新建一个新的碎片
     *
     * @return 返回实例
     */
    public static PostDetailFragment newInstance(FeedItem feedItem) {
        return new PostDetailFragment(feedItem);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();


    }


    private void initView(){

        //根据偏好设置的背景颜色，设置标题栏位置的背景颜色
        if (!SkinPreference.getInstance().getSkinName().equals("night")){
//            helper.setBackgroundColor(R.id.container, PostSetting.getBackgroundInt(context));
            postTitle.setBackgroundColor(PostSetting.getBackgroundInt(getContext()));
            postTurn.setBackgroundColor(PostSetting.getBackgroundInt(getContext()));
        }

        //设置文章内容
        PostUtil.setContent(getContext(), feedItem, postContent,null);
        postTitle.setText(feedItem.getTitle());
        postTime.setText( DateUtil.getTimeStringByInt(feedItem.getDate()));
        feedName.setText( feedItem.getFeedName());

        if (!feedItem.isRead()) {
            //如果这个文章没有阅读过则滚动到顶部
            postTurn.fullScroll(ScrollView.FOCUS_UP);
        }


        initListener();
    }


    private void initListener(){

        refreshLayout.setRefreshHeader(new PostHeader(getContext(), feedItem));
        refreshLayout.setRefreshFooter(new PostFooter(getContext(), feedItem));
        //使上拉加载具有弹性效果
        refreshLayout.setEnableAutoLoadMore(false);
        //禁止越界拖动（1.0.4以上版本）
        refreshLayout.setEnableOverScrollDrag(false);
        //关闭越界回弹功能
        refreshLayout.setEnableOverScrollBounce(false);
        // 这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的：
        refreshLayout.setEnableAutoLoadMore(false);

        refreshLayout.setEnableRefresh(false);//禁止下拉动作

        if (UserPreference.queryValueByKey(UserPreference.notOpenClick,"0").equals("0")){
            refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    refreshLayout.finishLoadMore();
                    //打开外链
                    openLink();
                }
            });
        }else {
            refreshLayout.setEnableLoadMore(false);
        }
    }

    private void openLink() {
        String url = feedItem.getUrl();
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (pattern.matcher(url).matches()){
            Toasty.info(getContext(),"该文章没有外链哦").show();
        }else {
            /*if (url.startsWith("/")){//相对地址
                Feed feed = LitePal.find(Feed.class,feedItem.getFeedId());
                String origin = feed.getLink();
                if (!origin.endsWith("/")){
                    origin = origin + "/";
                }
                url = origin + url;
            }*/
            WebViewUtil.openLink(url, getActivity());
        }
    }
    public View findViewById(int id){
        return getView().findViewById(id);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void refreshUI() {
        initView();
    }
}
