package com.htmessage.fanxinht.acitivity.main.contacts;

import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.acitivity.BasePresenter;

import java.util.List;

/**
 * Created by huangfangyi on 2017/6/28.
 * qq 84543217
 */

public interface BaseContactsPresenter extends BasePresenter{
    List<User> getContactsListInDb();
    void deleteContacts(String userId);
    void moveUserToBlack(String userId);
    List<User> sortList(  List<User> users);
    void refreshContactsInServer();
    int getInvitionCount();
    int getContactsCount();
    void clearInvitionCount();
    void refreshContactsInLocal();
}
