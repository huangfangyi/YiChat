package com.htmessage.fanxinht.acitivity.main;

import com.htmessage.fanxinht.acitivity.BaseView;
import com.htmessage.fanxinht.acitivity.main.contacts.FragmentContacts;
import com.htmessage.fanxinht.acitivity.main.conversation.ConversationFragment;
import com.htmessage.fanxinht.acitivity.main.find.FragmentFind;

/**
 * Created by huangfangyi on 2017/6/25.
 * qq 84543217
 */

public interface MainView extends BaseView<MainPrestener>,ConversationFragment.NewMeesageListener,FragmentContacts.ContactsListener,FragmentFind.OnMomentsMessageLisenter {

    void showConflicDialog();

     void showUpdateDialog( String message,String url,String isForce);

}
