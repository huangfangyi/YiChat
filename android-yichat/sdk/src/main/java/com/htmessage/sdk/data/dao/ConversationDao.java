package com.htmessage.sdk.data.dao;

import android.content.Context;


import com.htmessage.sdk.manager.HTDatabaseManager;
import com.htmessage.sdk.model.HTConversation;

import java.util.Map;

/**
 * Created by huangfangyi on 2016/11/25.
 * qq 84543217
 */

public class ConversationDao {
    public  static final String TABLE_NAME = "conversation";
    public static final String COLUMN_NAME_MSG_ID = "msgId";
    public  static final String COLUMN_NAME_USER_ID = "userId";
    public static final String COLUMN_NAME_UNREADCOUNT = "unReadCount";
    public static final String COLUMN_NAME_CHAT_TYPE= "chatType";
    public static final String COLUMN_NAME_TIME="time";
    public static final String COLUMN_NAME_TIME_TOP="time_top";
    public ConversationDao(Context context) {
    }
    public void saveConversation(HTConversation conversation) {
         HTDatabaseManager.getInstance().saveConversation(conversation);
    }
    public void updateLastMessage(String  userId, String msgId) {
        HTDatabaseManager.getInstance().updateLastMessage(userId,msgId);
    }
    public int getUnreadMessagesCount() {
        return HTDatabaseManager.getInstance().getUnreadMessageCount();
    }
    public void saveUnreadMessageCount(int count) {
        HTDatabaseManager.getInstance().setUnreadMessageCount(count);
    }

    public Map<String ,HTConversation> getConversationList() {
      return   HTDatabaseManager.getInstance().getConversationList();
    }

    public void deleteConversation(String chatTo) {
            HTDatabaseManager.getInstance().deleteConversation(chatTo);
    }


}
