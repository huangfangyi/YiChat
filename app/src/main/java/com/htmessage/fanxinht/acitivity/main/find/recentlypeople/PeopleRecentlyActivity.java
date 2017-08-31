package com.htmessage.fanxinht.acitivity.main.find.recentlypeople;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

/**
 * 项目名称：yichat0504
 * 类描述：PeopleRecentlyActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/5 16:53
 * 邮箱:814326663@qq.com
 */
public class PeopleRecentlyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.people_time);
        PeopleRecentlyFragment peopleRecentlyFragment =
                (PeopleRecentlyFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (peopleRecentlyFragment == null) {
            // Create the fragment
            peopleRecentlyFragment = new PeopleRecentlyFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, peopleRecentlyFragment);
            transaction.commit();
        }
        PeopleRecentlyPrestener prestener = new PeopleRecentlyPrestener(peopleRecentlyFragment);
    }
}
