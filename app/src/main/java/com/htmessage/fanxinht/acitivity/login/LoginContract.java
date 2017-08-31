package com.htmessage.fanxinht.acitivity.login;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.htmessage.fanxinht.acitivity.BasePresenter;
import com.htmessage.fanxinht.acitivity.BaseView;

/**
 * Created by huangfangyi on 2017/6/21.
 * qq 84543217
 */

public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void showDialog();

        void cancelDialog();

        String getUsername();

        String getPassword();

        void setButtonEnable();

        void setButtonDisabel();

        void showToast(int toastMsg);

        String getCountryName();

        String getCountryCode();

        Activity getBaseActivity();

        Context getBaseContext();


    }

    interface Presenter extends BasePresenter {

        void requestServer(String username, String password, boolean isAuth);

        void chooseCuntry(Context context, TextView tvCountryName, TextView tvCountryCode);

    }
}
