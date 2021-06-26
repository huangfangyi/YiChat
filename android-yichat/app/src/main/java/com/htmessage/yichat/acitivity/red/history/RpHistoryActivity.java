package com.htmessage.yichat.acitivity.red.history;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.main.NoAnimViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：CityBz
 * 类描述：RpHistoryActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/9 16:45
 * 邮箱:814326663@qq.com
 */
public class RpHistoryActivity extends BaseActivity {
    private TextView tv_title;
    private TabLayout tabLayout;
    private NoAnimViewPager viewpager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private int index = 0;
    private RpReceivedHistoryFragment rpRecHistoryFragment;
    private RpSendHistoryFragment rpSendHistoryFragment;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_rp_history);
        getData();
        initView();
        initData();
    }

    private void setListener() {
        viewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
//                if (index == 0) {
//                    CommonUtils.showToastShort(getBaseContext(), titleList.get(index));
//                } else if (index == 1) {
//                    CommonUtils.showToastShort(getBaseContext(), titleList.get(index));
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        tv_title.setText(R.string.my_rp);
        setListener();
        tabLayout.setupWithViewPager(viewpager);
        TextView tvPromotion = new TextView(RpHistoryActivity.this);
        tvPromotion.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.accent_red));
        tvPromotion.setGravity(Gravity.CENTER);
        tvPromotion.setText(R.string.my_rp_receiver);
        tvPromotion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        TextView tvNews = new TextView(RpHistoryActivity.this);
        tvNews.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        tvNews.setGravity(Gravity.CENTER);
        tvNews.setText(R.string.my_rp_send);
        tvNews.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tabLayout.getTabAt(0).setCustomView(tvPromotion);
        tabLayout.getTabAt(1).setCustomView(tvNews);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.accent_red));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewpager = (NoAnimViewPager) findViewById(R.id.viewPager);
    }

    private void getData() {
        rpRecHistoryFragment = new RpReceivedHistoryFragment();
        rpSendHistoryFragment = new RpSendHistoryFragment();
        fragmentList.add(rpRecHistoryFragment);
        fragmentList.add(rpSendHistoryFragment);
    }
}
