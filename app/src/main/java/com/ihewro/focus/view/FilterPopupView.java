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

import skin.support.utils.SkinPreference;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/10
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FilterPopupView extends DrawerPopupView {

    public static final int ORDER_BY_NEW = 960;
    public static final int ORDER_BY_OLD = 467;

    public static final int SHOW_ALL = 871;
    public static final int SHOW_UNREAD = 705;
    public static final int SHOW_STAR = 750;

    TextView newestTv;
    CardView newestCard;
    TextView oldTv;
    CardView oldCard;
    TextView allTv;
    CardView allCard;
    TextView readTv;
    CardView readCard;
    TextView starTv;
    CardView starCard;

    private CardView newestC;
    private boolean isNeedUpdate = false;

    private int orderChoice = ORDER_BY_NEW;//当前排序选择
    private int filterChoice = SHOW_ALL;//当前筛选器的选择



    List<Integer> orderOperation = Arrays.asList(ORDER_BY_NEW,ORDER_BY_OLD);
    List<Integer> orderCardViews = Arrays.asList(R.id.newest_card, R.id.old_card);
    List<Integer> orderTextViews = Arrays.asList(R.id.newest_tv, R.id.old_tv);


    List<Integer> filterOperation = Arrays.asList(SHOW_ALL,SHOW_UNREAD,SHOW_STAR);
    List<Integer> filterCardViews = Arrays.asList(R.id.all_card, R.id.read_card, R.id.star_card);
    List<Integer> filterTextViews = Arrays.asList(R.id.all_tv, R.id.read_tv, R.id.star_tv);

    int normalTextColor;
    int normalBGColor;
    int highlightTextColor;
    int highlightBGColor;


    public FilterPopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.component_filter_drawer;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        Log.e("tag", "CustomDrawerPopupView onCreate");


        initView();

        //初始化颜色

        if (SkinPreference.getInstance().getSkinName().equals("night")){
            normalTextColor = getResources().getColor(R.color.material_drawer_dark_selected_text);
            normalBGColor = getResources().getColor(R.color.material_drawer_dark_selected);

            highlightTextColor = getResources().getColor(R.color.material_drawer_dark_primary_text);
            highlightBGColor = getResources().getColor(R.color.material_drawer_dark_background);

        }else {
            normalTextColor = getResources().getColor(R.color.colorAccent);
            normalBGColor = getResources().getColor(R.color.material_drawer_selected);

            highlightTextColor = getResources().getColor(R.color.material_drawer_primary_text);
            highlightBGColor = getResources().getColor(R.color.material_drawer_background);
        }

        //初始化选项
        clickOrderList(0);
        clickFilterList(0);


        initListener();
    }

    private void initView(){
        newestTv = findViewById(R.id.newest_tv);
        newestCard = findViewById(R.id.newest_card);
        oldTv = findViewById(R.id.old_tv);
        oldCard = findViewById(R.id.old_card);
        allTv = findViewById(R.id.all_tv);
        allCard = findViewById(R.id.all_card);
        readTv = findViewById(R.id.read_tv);
        readCard = findViewById(R.id.read_card);
        starTv = findViewById(R.id.star_tv);
        starCard = findViewById(R.id.star_card);

    }

    private void initListener() {

        for (int i = 0; i < orderOperation.size();i++){
            final int finalI = i;
            findViewById(orderCardViews.get(i)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    isNeedUpdate = true;
                    clickOrderList(finalI);
                }
            });
        }

        for (int i = 0; i < filterOperation.size();i++){
            final int finalI = i;
            findViewById(filterCardViews.get(i)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    isNeedUpdate = true;
                    clickFilterList(finalI);
                }
            });
        }
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






    /**
     * 点击了排序的列表
     * @param position
     */
    private void clickOrderList(int position){
        orderChoice = orderOperation.get(position);

        //修改当前项为高亮
        ((TextView)findViewById(orderTextViews.get(position))).setTextColor(normalTextColor);
        ((CardView)findViewById(orderCardViews.get(position))).setCardBackgroundColor(normalBGColor);

        //修改其他项为普通颜色
        for (int i = 0; i < orderOperation.size(); i++){
            if (i != position){
                ((TextView)findViewById(orderTextViews.get(i))).setTextColor(highlightTextColor);
                ((CardView)findViewById(orderCardViews.get(i))).setCardBackgroundColor(highlightBGColor);

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
        ((TextView)findViewById(filterTextViews.get(position))).setTextColor(normalTextColor);
        ((CardView)findViewById(filterCardViews.get(position))).setCardBackgroundColor(normalBGColor);

        //修改其他项为普通颜色
        for (int i = 0; i < filterOperation.size();i++){
            if (i != position){
                ((TextView)findViewById(filterTextViews.get(i))).setTextColor(highlightTextColor);
                ((CardView)findViewById(filterCardViews.get(i))).setCardBackgroundColor(highlightBGColor);
            }
        }
    }

    public int getOrderChoice() {
        return orderChoice;
    }

    public int getFilterChoice() {
        return filterChoice;
    }

    public boolean isNeedUpdate() {
        return isNeedUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }
}