package com.htmessage.fanxinht.acitivity.addfriends.invitefriend;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

/**
 * 项目名称：zhigongxing
 * 类描述：ContactsInviteActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/9 14:12
 * 邮箱:814326663@qq.com
 */
public class ContactsInviteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.contacts_friends);
      ContactsInviteFragment fragment = (ContactsInviteFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new ContactsInviteFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame,fragment);
            transaction.commit();
        }
    }
}
