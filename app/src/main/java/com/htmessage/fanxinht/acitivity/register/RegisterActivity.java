package com.htmessage.fanxinht.acitivity.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;




/**
 * 注册页
 */
public class RegisterActivity extends BaseActivity   {
    private RegisterPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTitle(R.string.register);
        RegisterFragment registerFragment =
                (RegisterFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (registerFragment == null) {
            // Create the fragment
            registerFragment =new  RegisterFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, registerFragment);
            transaction.commit();
        }

        presenter=new RegisterPresenter(registerFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.result(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

    }
}
