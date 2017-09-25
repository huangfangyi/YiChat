package com.htmessage.fanxinht.acitivity.main.servicecontacts;


import com.htmessage.fanxinht.acitivity.BaseView;

/**
 * 项目名称：PersonalTailor
 * 类描述：ServiceContactsNewView 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/2 13:24
 * 邮箱:814326663@qq.com
 */
public interface ServiceContactsNewView extends BaseView<ServiceContactsPresenter> {
    void showToast(String msg);
    void showSiderBar();
    void refresh();
}
