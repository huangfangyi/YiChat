package com.htmessage.fanxinht.acitivity.main.password;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

/**
 * Created by huangfangyi on 2016/10/7.
 * qq 84543217
 */

public class PasswordResetActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        getData();
        initView();
    }

    private void initView() {
        PasswordResetFragment fragment = (PasswordResetFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new PasswordResetFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, fragment);
            transaction.commit();
        }
        PasswordPrester prester = new PasswordPrester(fragment);
    }

    private void getData() {
        boolean isReset = getIntent().getBooleanExtra("isReset", false);
        if (isReset){
            setTitle(R.string.resetPassword);
        }else{
            setTitle(R.string.find_pwd);
        }
    }
}