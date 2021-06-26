package com.htmessage.yichat.acitivity.main.profile.info.update;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

/**
 * Created by huangfangyi on 2016/7/3.\
 * QQ:84543217
 */
public class ProfileUpdateActivity extends BaseActivity {
    public static final int TYPE_NICK = 0;
    public static final int TYPE_APPID = 1;
     private UpdateProfilePrestener prestener;
    private String title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        int type = getIntent().getIntExtra("type", 0);
        switch (type){
            case 0:
                title = getString(R.string.change_nick);
                break;
            case 1:
                title = getString(R.string.change_appId);
                break;

        }
        setTitle(title);
        ProfileUpdateFragment fragment = (ProfileUpdateFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new ProfileUpdateFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame,fragment);
            transaction.commit();
        }
        prestener = new UpdateProfilePrestener(fragment);
        showRightTextView(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prestener.update();
            }
        });
    }
}
