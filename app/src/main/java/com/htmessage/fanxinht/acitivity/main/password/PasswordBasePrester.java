package com.htmessage.fanxinht.acitivity.main.password;

import com.htmessage.fanxinht.acitivity.BasePresenter;

/**
 * 项目名称：HTOpen
 * 类描述：PasswordBasePrester 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/7 14:59
 * 邮箱:814326663@qq.com
 */
public interface PasswordBasePrester  extends BasePresenter {
    void sendSMSCode(String mobile, String countryName, String countryCode);
    void resetPassword(String cacheCode, String smsCode, String password, String confimPwd, String mobile);
}
