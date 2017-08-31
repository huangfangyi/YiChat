package com.htmessage.fanxinht.acitivity.main.conversation;

import android.content.Context;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTMessage;

import java.util.List;

/**
 * Created by huangfangyi on 2017/6/27.
 * qq 84543217
 */

public class ConversationPresenter implements BaseConversationPresenter {
    private ConversationView conversationView;
    private Context context;
    private List<HTConversation> allConversations;

    public ConversationPresenter(ConversationView view) {
        conversationView = view;
        conversationView.setPresenter(this);
        context = conversationView.getBaseContext();
        allConversations = HTClient.getInstance().conversationManager().getAllConversations();
    }


    @Override
    public void start() {

    }

    @Override
    public List<HTConversation> getAllConversations() {

        return allConversations;
    }

    @Override
    public void deleteConversation(HTConversation htConversation) {
        allConversations.remove(htConversation);
        HTClient.getInstance().messageManager().deleteUserMessage(htConversation.getUserId(), true);
        conversationView.refresh();
    }

    @Override
    public void setTopConversation(HTConversation htConversation) {
        HTClient.getInstance().conversationManager().setConversationTop(htConversation,System.currentTimeMillis());
        conversationView.refresh();

    }

    @Override
    public void cancelTopConversation(HTConversation htConversation) {
        HTClient.getInstance().conversationManager().setConversationTop(htConversation,0);
        conversationView.refresh();
    }

    @Override
    public void refreshConversations() {
        allConversations.clear();
        allConversations.addAll(HTClient.getInstance().conversationManager().getAllConversations());
     }

    @Override
    public void onNewMsgReceived(HTMessage htMessage) {

    }

    @Override
    public int getUnreadMsgCount() {
        int unreadMsgCountTotal = 0;
        if (allConversations.size() != 0 && allConversations != null) {
            for (int i = 0; i < allConversations.size(); i++) {
                 unreadMsgCountTotal += allConversations.get(i).getUnReadCount();
            }
        }
        return unreadMsgCountTotal;
    }

    @Override
    public void markAllMessageRead(HTConversation conversation) {

        HTClient.getInstance().conversationManager().markAllMessageRead(conversation.getUserId());
        conversationView.refresh();
    }
}
