package com.htmessage.yichat.acitivity.main.profile.info.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

public class ProfileActivity extends BaseActivity {
    private ProfilePrester prester;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTitle(R.string.title_user_profile);
        ProfileFragment fragment =  (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new ProfileFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, fragment);
            transaction.commit();
        }
        prester = new ProfilePrester(fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        prester.result(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
