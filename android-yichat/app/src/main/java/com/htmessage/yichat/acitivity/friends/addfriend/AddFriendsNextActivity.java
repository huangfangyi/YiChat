package com.htmessage.yichat.acitivity.friends.addfriend;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

public class AddFriendsNextActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_input);
        AddFriendNextFragment fragment = new AddFriendNextFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentFrame, fragment);
        transaction.commit();

        AddFriendNextPrestener addFriendNextPrestener=  new AddFriendNextPrestener(fragment);
    }
}