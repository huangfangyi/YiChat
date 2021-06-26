package com.htmessage.yichat.acitivity.main.contacts;

import com.htmessage.yichat.domain.User;
import com.htmessage.yichat.acitivity.BasePresenter;

import java.util.List;

/**
 * Created by huangfangyi on 2017/6/28.
 * qq 84543217
 */

public interface BaseContactsPresenter extends BasePresenter{
    List<User> getContactsListLocal();
    void deleteContacts(User user);
     void refreshContactsInServer();
     int getContactsCount();
    void clearInvitionCount();
    void refreshContactsInLocal();
    void deleteContactsFromCMD(String userId);
    void groupSend(String msg);
    void updateUser(String userId);
    void getApplyUnread();
}
