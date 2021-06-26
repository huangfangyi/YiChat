package com.htmessage.yichat.acitivity.password;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

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
            int isBind=getIntent().getIntExtra("isBind",0);
            if(isBind==1){
                setTitle("绑定手机号");
                return;
            }else if(isBind==2){
                setTitle("更换手机号");
                return;
            }

            setTitle(R.string.find_pwd);

        }
    }
}