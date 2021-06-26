package com.htmessage.yichat.acitivity.main;

import com.htmessage.yichat.acitivity.BaseView;
import com.htmessage.yichat.acitivity.main.contacts.ContactsFragment;
import com.htmessage.yichat.acitivity.main.conversation.ConversationFragment;

/**
 * Created by huangfangyi on 2017/6/25.
 * qq 84543217
 */

public interface MainView extends BaseView<MainPrestener>,ConversationFragment.NewMeesageListener, ContactsFragment.ContactsListener {

    void showConflicDialog();

     void showUpdateDialog( String message,String url,boolean isForce);

}
