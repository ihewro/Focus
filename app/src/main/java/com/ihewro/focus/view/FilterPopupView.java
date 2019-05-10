package com.ihewro.focus.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ihewro.focus.R;
import com.lxj.xpopup.core.DrawerPopupView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/10
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FilterPopupView extends DrawerPopupView implements View.OnClickListener {

    public static final int ORDER_BY_NEW = 960;
    public static final int ORDER_BY_OLD = 467;

    public static final int SHOW_ALL = 871;
    public static final int SHOW_UNREAD = 705;
    public static final int SHOW_STAR = 750;

    @BindView(R.id.newest_tv)
    TextView newestTv;
    @BindView(R.id.newest_card)
    CardView newestCard;
    @BindView(R.id.old_tv)
    TextView oldTv;
    @BindView(R.id.old_card)
    CardView oldCard;
    @BindView(R.id.all_tv)
    TextView allTv;
    @BindView(R.id.all_card)
    CardView allCard;
    @BindView(R.id.read_tv)
    TextView readTv;
    @BindView(R.id.read_card)
    CardView readCard;
    @BindView(R.id.star_tv)
    TextView starTv;
    @BindView(R.id.star_card)
    CardView starCard;

    private CardView newestC;

    private int orderChoice = ORDER_BY_NEW;//当前排序选择
    private int filterChoice = SHOW_ALL;//当前筛选器的选择



    List<Integer> oderOperation = Arrays.asList(ORDER_BY_NEW,ORDER_BY_OLD);
    List<Integer> oderCardViews = Arrays.asList(R.id.newest_card, R.id.old_card);
    List<Integer> oderTextViews = Arrays.asList(R.id.newest_tv, R.id.old_tv);


    List<Integer> filterOperation = Arrays.asList(SHOW_ALL,SHOW_UNREAD,SHOW_STAR);
    List<Integer> filterCardViews = Arrays.asList(R.id.all_card, R.id.read_card, R.id.star_card);
    List<Integer> filterTextViews = Arrays.asList(R.id.all_tv, R.id.read_tv, R.id.star_tv);

    public FilterPopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.component_filder_drawer;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        Log.e("tag", "CustomDrawerPopupView onCreate");


        //初始化选项

        clickOrderList(0);
        clickOrderList(0);


        //默认选择显示全部
        allTv.setTextColor(getResources().getColor(R.color.text_unread));
        allCard.setCardBackgroundColor(getResources().getColor(R.color.material_drawer_selected));

        initListener();
    }

    private void initListener() {

    }

    @Override
    protected void onShow() {
        super.onShow();
        Log.e("tag", "CustomDrawerPopupView onShow");
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        Log.e("tag", "CustomDrawerPopupView onDismiss");
    }

    @Override
    public void onClick(View view) {

        int position =  filterCardViews.indexOf(view.getId());
        if (position == -1){//点击是排序的列表
            position = oderCardViews.indexOf(view.getId());//获取位置
            clickOrderList(position);

        }else {//点击的是筛选的列报表
            clickFilterList(position);
        }


        dismiss();//关闭右侧边栏，并刷新数据

    }


    /**
     * 点击了排序的列表
     * @param position
     */
    private void clickOrderList(int position){
        orderChoice = oderOperation.get(position);

        //修改当前项为高亮
        ((TextView)findViewById(oderTextViews.get(position))).setTextColor(getResources().getColor(R.color.text_unread));
        ((CardView)findViewById(oderCardViews.get(position))).setCardBackgroundColor(getResources().getColor(R.color.material_drawer_selected));

        //修改其他项为普通颜色
        for (int i = 0; i < oderOperation.size();i++){
            if (i != position){
                ((TextView)findViewById(oderTextViews.get(i))).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                ((CardView)findViewById(oderCardViews.get(i))).setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

            }
        }

    }

    /**
     * 点击了筛选的列表
     * @param position
     */
    private void clickFilterList(int position){
        filterChoice = filterOperation.get(position);

        //修改当前项为高亮
        ((TextView)findViewById(filterTextViews.get(position))).setTextColor(getResources().getColor(R.color.text_unread));
        ((CardView)findViewById(filterCardViews.get(position))).setCardBackgroundColor(getResources().getColor(R.color.material_drawer_selected));

        //修改其他项为普通颜色
        for (int i = 0; i < filterOperation.size();i++){
            if (i != position){
                ((TextView)findViewById(filterTextViews.get(i))).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                ((CardView)findViewById(filterCardViews.get(i))).setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    public int getOrderChoice() {
        return orderChoice;
    }

    public int getFilterChoice() {
        return filterChoice;
    }

}