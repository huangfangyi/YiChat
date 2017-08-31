package com.htmessage.fanxinht.acitivity.auth;

import android.content.Intent;

import com.htmessage.fanxinht.acitivity.BasePresenter;


/**
 * 项目名称：yichat0504
 * 类描述：AuthBasePresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/17 17:07
 * 邮箱:814326663@qq.com
 */
public interface AuthBasePresenter extends BasePresenter {
    void OnActivityResult(int requestCode, int resultCode, Intent data);
    void authResultError();
    void authResultSuccess();
    void onDestory();

}
