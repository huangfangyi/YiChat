package com.htmessage.yichat.acitivity.main.profile.info.update;


import com.htmessage.yichat.acitivity.BaseView;

/**
 * 项目名称：HTOpen
 * 类描述：UpdateProfileView 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/7 13:41
 * 邮箱:814326663@qq.com
 */
public interface UpdateProfileView extends BaseView<UpdateProfilePrestener> {
    String getDefultString();
    int getType();
    String getInputString();
    void onUpdateSuccess(String key,String value);
    void onUpdateFailed(String msg);
    void showTips(int msg);
    void hintTips();
    void show(int resId);
    void showDialog();
    void cancelDialog();
    void finish();

}
