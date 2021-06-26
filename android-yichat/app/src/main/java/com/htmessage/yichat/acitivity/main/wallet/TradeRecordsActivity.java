package com.htmessage.yichat.acitivity.main.wallet;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.main.NoAnimViewPager;

/**
 * Created by huangfangyi on 2019/5/27.
 * qq 84543217
 */
public class TradeRecordsActivity  extends BaseActivity {
   private TabLayout tabLayout;
   private NoAnimViewPager viewPager;
   private MyAdapter adapter;
   private String[] titles=new String[]{"全部","支出","收入"};
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_records);
        setTitle("交易详情");
        tabLayout=this.findViewById(R.id.tabLayout);
        viewPager=this.findViewById(R.id.viewPager);
        initTabLayout(tabLayout);

    }

    private  Fragment[] fragments;

    private void   initTabLayout(TabLayout tabLayout){


        fragments= new Fragment[]{new RecordsFragment(), new RecordsFragment(), new RecordsFragment()};


        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                return fragments[position];

            }

            @Override
            public int getCount() {
                return fragments.length;
            }


        });

        for(int i=0;i<fragments.length;i++){
            Bundle bundle=new Bundle();
            bundle.putInt("type",i);
            fragments[i].setArguments(bundle);

        }



        tabLayout.setupWithViewPager(viewPager);
        //标题需要实在设置完viewpager之后方可生效
        for (int i = 0; i < titles.length; i++) {
            //tabLayout.addTab(  tabLayout.newTab().setText(titles[i]));
tabLayout.getTabAt(i).setText(titles[i]);
//
//            tabs[i] = tabLayout.getTabAt(i);
//            tabs[i].setText(titles[i]);
        }



    }


    class  MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
