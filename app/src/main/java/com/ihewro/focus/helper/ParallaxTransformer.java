package com.ihewro.focus.helper;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.ALog;
import com.ihewro.focus.adapter.PostDetailListPagerAdapter;

import java.util.HashMap;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/08/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ParallaxTransformer implements ViewPager.PageTransformer {

    float parallaxCoefficient;
    float distanceCoefficient;
    private PostDetailListPagerAdapter adapter;
    private HashMap<Integer,int[]> mLayoutViewIdsMap;
    public ParallaxTransformer(PostDetailListPagerAdapter adapter, HashMap<Integer,int[]>map, float parallaxCoefficient, float distanceCoefficient) {
        this.adapter = adapter;
        this.parallaxCoefficient = parallaxCoefficient;
        this.distanceCoefficient = distanceCoefficient;
        this.mLayoutViewIdsMap = map;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void transformPage(View page, float position) {
        float scrollXOffset = page.getWidth() * parallaxCoefficient;

        ViewGroup pageViewWrapper = (ViewGroup) page;
        @SuppressWarnings("SuspiciousMethodCalls")
        int[] layer = adapter.getCurrentFragment().getLayers();
        for (int id : layer) {
            View view = page.findViewById(id);
            if (view != null) {
                ALog.d("not null");
                view.setTranslationX(scrollXOffset * position);
            }else {
                ALog.d("null ???");
            }
            scrollXOffset *= distanceCoefficient;
        }
    }
}