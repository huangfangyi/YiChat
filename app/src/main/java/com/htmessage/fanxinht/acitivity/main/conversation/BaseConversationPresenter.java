package com.htmessage.fanxinht.acitivity.main.conversation;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.fanxinht.acitivity.BasePresenter;

import java.util.List;

/**
 * Created by huangfangyi on 2017/6/27.
 * qq 84543217
 */

public interface BaseConversationPresenter extends BasePresenter {
    List<HTConversation> getAllConversations();

    void deleteConversation(HTConversation conversation);

    void setTopConversation(HTConversation conversation);

    void cancelTopConversation(HTConversation conversation);

    void refreshConversations();

    void onNewMsgReceived(HTMessage htmessage);

    int getUnreadMsgCount();

    void markAllMessageRead(HTConversation conversation);

    List<JSONObject> getShowNotice();

}
