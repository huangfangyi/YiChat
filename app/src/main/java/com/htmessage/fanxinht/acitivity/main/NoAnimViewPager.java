package com.htmessage.fanxinht.acitivity.main;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by huangfangyi on 2017/7/19.
 * qq 84543217
 */

public class NoAnimViewPager extends ViewPager {


    public NoAnimViewPager(Context context) {
        super(context);
    }

    public NoAnimViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    //去除页面切换时的滑动翻页效果
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, false);
    }
}