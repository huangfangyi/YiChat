package com.htmessage.fanxinht.acitivity.auth;


import com.htmessage.fanxinht.acitivity.BaseView;

/**
 * 项目名称：yichat0504
 * 类描述：AuthView 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/17 17:09
 * 邮箱:814326663@qq.com
 */
public interface AuthView extends BaseView<AuthPresenter> {

    void showAppName(String appName);

    void showAppIcon(String appicon);

    void authWebSuccess(String msg);

    void authWebFailed(String error);
}
