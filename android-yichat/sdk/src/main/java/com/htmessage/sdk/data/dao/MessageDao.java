package com.htmessage.sdk.data.dao;

import android.content.ContentValues;
import android.content.Context;


import com.htmessage.sdk.manager.HTDatabaseManager;
import com.htmessage.sdk.model.HTMessage;

import java.util.Collections;
import java.util.List;

/**
 * Created by huangfangyi on 2016/9/21.
 * qq 84543217
 */

public class MessageDao {

    public  static final String TABLE_NAME = "message";
    public  static final String COLUMN_NAME_MSG_ID = "msgId";
    public static final String COLUMN_NAME_FROM = "msgFrom";
    public static final String COLUMN_NAME_TO = "msgTo";
    public static final String COLUMN_NAME_BODY = "body";
    public static final String COLUMN_NAME_CHAT_TYPE = "chatType";
    public static final String COLUMN_NAME_MSG_TIME = "msgTime";
    public static final String COLUMN_NAME_TYPE = "type";
    public static final String COLUMN_NAME_lOCAL_TIME = "localTime";
    public static final String COLUMN_NAME_STATUS = "status";
    public static final String COLUMN_NAME_DIRECT = "direct";
    public static final String COLUMN_NAME_ATTRIBUTE="attribute";

    public MessageDao(Context context) {
        
    }

    /**
     * save message
     *
     * @param message
     * @return return cursor of the message
     */
    public void saveMessage(HTMessage message) {
         HTDatabaseManager.getInstance().saveHTMessage(message);
    }

    /**
     * update message
     *
     * @param msgId
     * @param values
     */
    public void updateHTMessage(String msgId, ContentValues values) {
        HTDatabaseManager.getInstance().updateHTMessageStatue(msgId, values);
    }

    public void deleteHTMessage(String msgId){
        HTDatabaseManager.getInstance().deleteHTMessage(msgId);

    }
    public void deleteUserHTMessage(String chatTo){
        HTDatabaseManager.getInstance().deleteUserHTMessage(chatTo);

    }


    public List<HTMessage>  getAllMessages(String userId) {
        List<HTMessage> htMessages= HTDatabaseManager.getInstance().getAllMessages(userId,20);
        Collections.reverse(htMessages);
         return htMessages;
    }


    public List<HTMessage>  loadMoreMsgFromDB(String userId,long timestamp,int pageSize) {
        List<HTMessage> htMessages=  HTDatabaseManager.getInstance().loadMoreMsgFromDB(userId,timestamp,pageSize);
        Collections.reverse(htMessages);
        return htMessages;

    }


    public List<HTMessage>  searchMsgFromDB(String userId,String content) {
        List<HTMessage> htMessages=  HTDatabaseManager.getInstance().searchMsgFromDB(userId,content);
         return htMessages;

    }




    public HTMessage getMessage(String msgId){

       return HTDatabaseManager.getInstance().getMessage(msgId);
    }

    public HTMessage getLastMessage(String chatTo){
        List<HTMessage> messages=HTDatabaseManager.getInstance().getAllMessages(chatTo,1);
       if(messages!=null&&messages.size()>0){
           return  messages.get(0);
       }

       return null;

    }

    public HTMessage getLastMessageOffsize(String chatTo,int offSize){
        List<HTMessage> messages=HTDatabaseManager.getInstance().getAllMessages(chatTo,1+offSize);
        if(messages!=null&&messages.size()>1){
            return  messages.get(1);
        }

        return null;

    }

    public void deleteHTMessageFromTimestamp(String chatTo,long timeStamp){
        HTDatabaseManager.getInstance().deleteHTMessageFromTimestamp(chatTo,timeStamp);

    }




}
