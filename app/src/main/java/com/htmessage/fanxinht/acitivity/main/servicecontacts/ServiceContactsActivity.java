package com.htmessage.fanxinht.acitivity.main.servicecontacts;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

/**
 * 项目名称：FanXinHT0831
 * 类描述：ServiceContactsActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/9/25 16:09
 * 邮箱:814326663@qq.com
 */
public class ServiceContactsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.service_online);
        ServiceServiceContactsFragment fragment = (ServiceServiceContactsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = new ServiceServiceContactsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, fragment);
            transaction.commit();
        }
//        ServiceContactsPresenter presenter = new ServiceContactsPresenter(fragment);
    }
}
