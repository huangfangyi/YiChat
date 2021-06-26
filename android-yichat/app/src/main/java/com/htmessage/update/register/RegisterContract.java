package com.htmessage.update.register;

import android.app.Activity;
import android.content.Intent;

import com.htmessage.yichat.acitivity.BasePresenter;
import com.htmessage.yichat.acitivity.BaseView;

/**
 * Created by huangfangyi on 2017/6/23.
 * qq 84543217
 */

public interface RegisterContract {
      interface View extends BaseView<Presenter>{
        void showAvatar(String imagePath);
        void showDialog();
        void cancelDialog();
        void showPassword();
        void hidePassword();
        void enableButton();
        void disableButton();
        void showToast(Object msgRes);
        Activity getBaseActivity();
        void  onRegisterSucc();


      }

      interface Presenter extends BasePresenter{

        void registerInServer(String nickName,String mobile,String password);
         void result(int requsetCode, int resultCode, Intent intent);
         void selectAvatar();
     }

}
