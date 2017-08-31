package com.htmessage.fanxinht.acitivity.keeplive;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

/**
 * Created by huangfangyi on 2017/8/1.
 * qq 84543217
 */

public class KeepAliveActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle("保活设置");
       KeepAliveFragment fragment = (KeepAliveFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new KeepAliveFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, fragment);
            transaction.commit();
        }
    }
}
