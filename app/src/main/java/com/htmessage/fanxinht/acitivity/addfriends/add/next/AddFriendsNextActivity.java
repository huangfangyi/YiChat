package com.htmessage.fanxinht.acitivity.addfriends.add.next;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

public class AddFriendsNextActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_input);
        AddFriendNextFragment fragment = (AddFriendNextFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new AddFriendNextFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame,fragment);
            transaction.commit();
        }
        new AddFriendNextPrestener(fragment);
    }
}