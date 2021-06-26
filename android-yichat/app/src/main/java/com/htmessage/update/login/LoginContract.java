package com.htmessage.update.login;


import com.htmessage.yichat.acitivity.BasePresenter;
import com.htmessage.yichat.acitivity.BaseView;

/**
 * Created by huangfangyi on 2017/6/21.
 * qq 84543217
 */

public interface LoginContract {

    interface View extends BaseView<Presenter> {
         void cancelDialog();
        void setButtonEnable();
        void setButtonDisabel();
        void showToast(int stringResId);
        void onLoginSuccessed();
        void showDialog(String msg);



    }

    interface Presenter extends BasePresenter {
        void requestServer(String username, String password);
        void getWxToken(String code);
        void loginByQQ(String token,String openId,String nickname,String avatar);
     }




}
