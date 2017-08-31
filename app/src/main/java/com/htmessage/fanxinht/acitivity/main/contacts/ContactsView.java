package com.htmessage.fanxinht.acitivity.main.contacts;

import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.acitivity.BaseView;

/**
 * Created by huangfangyi on 2017/6/28.
 * qq 84543217
 */

public interface ContactsView extends BaseView<ContactsPresenter> {
    void showItemDialog(User user);
    void showSiderBar();
    void showInvitionCount(int count);
    void showContactsCount(int count);
    void refresh();



}
