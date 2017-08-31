package com.htmessage.fanxinht.acitivity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

/**
 * 项目名称：yichat0504
 * 类描述：AuthActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/17 13:53
 * 邮箱:814326663@qq.com
 */
public class AuthActivity extends BaseActivity {
    private AuthPresenter presenter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.yichat_login);
        AuthFragment fragment = (AuthFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = new AuthFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, fragment);
            transaction.commit();
        }
        presenter = new AuthPresenter(fragment, getIntent().getExtras());
        changeBackView(R.drawable.close32, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.authResultError();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.OnActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
