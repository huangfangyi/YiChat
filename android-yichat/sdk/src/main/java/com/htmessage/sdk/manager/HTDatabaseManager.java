package com.htmessage.sdk.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.data.SDKDbOpenHelper;
import com.htmessage.sdk.data.dao.ConversationDao;
import com.htmessage.sdk.data.dao.GroupDao;
import com.htmessage.sdk.data.dao.MessageDao;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class HTDatabaseManager {
    static private HTDatabaseManager dbMgr;
    private SDKDbOpenHelper dbHelper;

    private HTDatabaseManager(Context context) {
        dbHelper = SDKDbOpenHelper.getInstance(context);
    }

    public static synchronized HTDatabaseManager getInstance() {

        if (dbMgr == null) {

            throw new RuntimeException("please init first!");
        }
        return dbMgr;
    }

    public static synchronized void init(Context context) {
        Log.d("Cache---->", "MessageManager:" + HTPreferenceManager.getInstance().getUser().getUsername());

        dbMgr = new HTDatabaseManager(context);

    }

    synchronized public Map<String, HTConversation> getConversationList() {


        Map<String, HTConversation> htConversations = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + ConversationDao.TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String userId = cursor.getString(cursor.getColumnIndex(ConversationDao.COLUMN_NAME_USER_ID));
                String msgId = cursor.getString(cursor.getColumnIndex(ConversationDao.COLUMN_NAME_MSG_ID));
                int unReadCount = cursor.getInt(cursor.getColumnIndex(ConversationDao.COLUMN_NAME_UNREADCOUNT));
                long topTimestamp = cursor.getLong(cursor.getColumnIndex(ConversationDao.COLUMN_NAME_TIME_TOP));
                long time = cursor.getLong(cursor.getColumnIndex(ConversationDao.COLUMN_NAME_TIME));
                int chatType = cursor.getInt(cursor.getColumnIndex(ConversationDao.COLUMN_NAME_CHAT_TYPE));
                HTConversation htConversation = new HTConversation();
                htConversation.setUserId(userId);
               // htConversation.setLastMsgId(msgId);
                if (chatType == ChatType.singleChat.ordinal()) {

                    htConversation.setChatType(ChatType.singleChat);
                } else if (chatType == ChatType.groupChat.ordinal()) {

                    htConversation.setChatType(ChatType.groupChat);
                }
                htConversation.setUnReadCount(unReadCount);
                htConversation.setTopTimestamp(topTimestamp);
                htConversation.setTime(time);
                htConversations.put(userId, htConversation);

            }
            cursor.close();
        }
        return htConversations;

    }

    public void deleteConversation(String chatTo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            if (db.isOpen()) {
                db.delete(ConversationDao.TABLE_NAME, ConversationDao.COLUMN_NAME_USER_ID + " = ?", new String[]{chatTo});
            }
        }

    }


    /**
     * save a message
     *
     * @param message
     * @return return cursor of the message
     */
    public synchronized void saveHTMessage(HTMessage message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(MessageDao.COLUMN_NAME_FROM, message.getFrom());
            values.put(MessageDao.COLUMN_NAME_TO, message.getTo());
            values.put(MessageDao.COLUMN_NAME_TYPE, message.getType().ordinal());
            values.put(MessageDao.COLUMN_NAME_MSG_TIME, message.getTime());
            values.put(MessageDao.COLUMN_NAME_lOCAL_TIME, message.getLocalTime());
            values.put(MessageDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
            values.put(MessageDao.COLUMN_NAME_BODY, message.getBody().getLocalBody());
            values.put(MessageDao.COLUMN_NAME_MSG_ID, message.getMsgId());
            values.put(MessageDao.COLUMN_NAME_DIRECT, message.getDirect().ordinal());
            values.put(MessageDao.COLUMN_NAME_CHAT_TYPE, message.getChatType().ordinal());
            values.put(MessageDao.COLUMN_NAME_ATTRIBUTE, message.getAttributes().toJSONString());
            db.replace(MessageDao.TABLE_NAME, null, values);
        }

    }

    public synchronized void saveConversation(HTConversation conversation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(ConversationDao.COLUMN_NAME_USER_ID, conversation.getUserId());
           // values.put(ConversationDao.COLUMN_NAME_MSG_ID, conversation.getLastMsgId());
            values.put(ConversationDao.COLUMN_NAME_UNREADCOUNT, conversation.getUnReadCount());
            values.put(ConversationDao.COLUMN_NAME_CHAT_TYPE, conversation.getChatType().ordinal());
            values.put(ConversationDao.COLUMN_NAME_TIME, conversation.getTime());
            values.put(ConversationDao.COLUMN_NAME_TIME_TOP, conversation.getTopTimestamp());
            db.replace(ConversationDao.TABLE_NAME, null, values);

        }


    }

    public synchronized void saveGroup(HTGroup htGroup) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(GroupDao.COLUMN_NAME_ID, htGroup.getGroupId());
            values.put(GroupDao.COLUMN_NAME_NAME, htGroup.getGroupName());
            values.put(GroupDao.COLUMN_NAME_DESC, htGroup.getGroupDesc());
            values.put(GroupDao.COLUMN_NAME_OWNER, htGroup.getOwner());
            values.put(GroupDao.COLUMN_NAME_TIME, htGroup.getTime());
            db.replace(GroupDao.TABLE_NAME, null, values);

        }


    }


    public synchronized void saveGroupList(List<HTGroup> groups) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(GroupDao.TABLE_NAME, null, null);
            for (HTGroup group : groups) {
                //  Log.d("saveContactList4----->",user.getUsername());
                ContentValues values = new ContentValues();
                values.put(GroupDao.COLUMN_NAME_ID, group.getGroupId());
                if (group.getGroupName() != null)
                    values.put(GroupDao.COLUMN_NAME_NAME, group.getGroupName());
                if (group.getGroupDesc() != null)
                    values.put(GroupDao.COLUMN_NAME_DESC, group.getGroupDesc());
                if (group.getOwner() != null)
                    values.put(GroupDao.COLUMN_NAME_OWNER, group.getOwner());

                values.put(GroupDao.COLUMN_NAME_TIME, group.getTime());
                db.replace(GroupDao.TABLE_NAME, null, values);
            }
        }


    }


    public synchronized List<HTMessage> getFXMessageList(String fxid) {
        List<HTMessage> htMessage = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + MessageDao.TABLE_NAME + " desc where " + MessageDao.COLUMN_NAME_FROM + " = " + fxid + " or " + MessageDao.COLUMN_NAME_TO + " = " + fxid, null);
            while (cursor.moveToNext()) {
                HTMessage msg = new HTMessage();
                String msgId = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_ID));
                String from = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_FROM));
                String to = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TO));
                long localTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_lOCAL_TIME));
                long msgTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_TIME));
                String body = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_BODY));
                int status = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_STATUS));
                int type = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TYPE));
                int direct = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_DIRECT));
                int chatType = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_CHAT_TYPE));
                String attributes = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_ATTRIBUTE));
                if (attributes != null) {
                    try {
                        msg.setAttributes(JSONObject.parseObject(attributes));
                    }catch (JSONException e ){
                        msg.setAttributes(new JSONObject());


                    }
                }
                msg.setMsgId(msgId);
                msg.setBody(new HTMessageBody(body));
                msg.setFrom(from);
                msg.setTo(to);
                msg.setLocalTime(localTime);
                msg.setTime(msgTime);
                if (type == HTMessage.Type.TEXT.ordinal()) {
                    msg.setType(HTMessage.Type.TEXT);
                } else if (type == HTMessage.Type.IMAGE.ordinal()) {
                    msg.setType(HTMessage.Type.IMAGE);
                }
                if (status == HTMessage.Status.CREATE.ordinal()) {

                    msg.setStatus(HTMessage.Status.CREATE);

                } else if (status == HTMessage.Status.FAIL.ordinal()) {
                    msg.setStatus(HTMessage.Status.FAIL);

                } else if (status == HTMessage.Status.SUCCESS.ordinal()) {
                    msg.setStatus(HTMessage.Status.SUCCESS);

                } else if (status == HTMessage.Status.INPROGRESS.ordinal()) {

                    msg.setStatus(HTMessage.Status.INPROGRESS);
                } else if (status == HTMessage.Status.READ.ordinal()) {

                    msg.setStatus(HTMessage.Status.READ);
                } else if (status == HTMessage.Status.ACKED.ordinal()) {

                    msg.setStatus(HTMessage.Status.ACKED);
                }
                if (direct == HTMessage.Direct.RECEIVE.ordinal()) {

                    msg.setDirect(HTMessage.Direct.RECEIVE);
                } else {

                    msg.setDirect(HTMessage.Direct.SEND);
                }


                if (chatType == ChatType.singleChat.ordinal()) {
                    msg.setChatType(ChatType.singleChat);

                } else if (chatType == ChatType.groupChat.ordinal()) {
                    msg.setChatType(ChatType.groupChat);

                }


                htMessage.add(msg);
            }
            cursor.close();
        }


        return htMessage;

    }


    public synchronized List<HTMessage> getFXMessageList(String fxid, long timestamp, int pageSize) {
        List<HTMessage> htMessage = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + MessageDao.TABLE_NAME + "order by" + MessageDao.COLUMN_NAME_MSG_TIME + " desc where (" + MessageDao.COLUMN_NAME_FROM + " = " + fxid + " or " + MessageDao.COLUMN_NAME_TO + " = " + fxid + ") and " + MessageDao.COLUMN_NAME_MSG_TIME + " < " + timestamp + " limit " + pageSize, null);
            while (cursor.moveToNext()) {
                HTMessage msg = new HTMessage();
                String msgId = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_ID));
                String from = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_FROM));
                String to = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TO));
                long localTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_lOCAL_TIME));
                long msgTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_TIME));
                String body = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_BODY));
                int status = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_STATUS));
                int type = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TYPE));
                int direct = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_DIRECT));
                int chatType = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_CHAT_TYPE));
                String attributes = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_ATTRIBUTE));
                if (attributes != null) {
                    try {
                        msg.setAttributes(JSONObject.parseObject(attributes));
                    }catch (JSONException e ){
                        msg.setAttributes(new JSONObject());


                    }
                }
                msg.setMsgId(msgId);
                msg.setBody(new HTMessageBody(body));
                msg.setFrom(from);
                msg.setTo(to);
                msg.setLocalTime(localTime);
                msg.setTime(msgTime);
                if (type == HTMessage.Type.TEXT.ordinal()) {
                    msg.setType(HTMessage.Type.TEXT);
                } else if (type == HTMessage.Type.IMAGE.ordinal()) {
                    msg.setType(HTMessage.Type.IMAGE);
                }
                if (status == HTMessage.Status.CREATE.ordinal()) {

                    msg.setStatus(HTMessage.Status.CREATE);

                } else if (status == HTMessage.Status.FAIL.ordinal()) {
                    msg.setStatus(HTMessage.Status.FAIL);

                } else if (status == HTMessage.Status.SUCCESS.ordinal()) {
                    msg.setStatus(HTMessage.Status.SUCCESS);

                } else if (status == HTMessage.Status.INPROGRESS.ordinal()) {

                    msg.setStatus(HTMessage.Status.INPROGRESS);
                } else if (status == HTMessage.Status.READ.ordinal()) {

                    msg.setStatus(HTMessage.Status.READ);
                } else if (status == HTMessage.Status.ACKED.ordinal()) {

                    msg.setStatus(HTMessage.Status.ACKED);
                }
                if (direct == HTMessage.Direct.RECEIVE.ordinal()) {

                    msg.setDirect(HTMessage.Direct.RECEIVE);
                } else {

                    msg.setDirect(HTMessage.Direct.SEND);
                }


                if (chatType == ChatType.singleChat.ordinal()) {
                    msg.setChatType(ChatType.singleChat);

                } else if (chatType == ChatType.groupChat.ordinal()) {
                    msg.setChatType(ChatType.groupChat);

                }

                htMessage.add(msg);
            }
            cursor.close();
        }


        return htMessage;

    }

    /**
     * update fxmessage
     *
     * @param msgId
     * @param values
     */
    synchronized public void updateHTMessageStatue(String msgId, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(MessageDao.TABLE_NAME, values, MessageDao.COLUMN_NAME_MSG_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    synchronized public void deleteHTMessage(String msgId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            if (db.isOpen()) {
                db.delete(MessageDao.TABLE_NAME, MessageDao.COLUMN_NAME_MSG_ID + " = ?", new String[]{msgId});
            }
        }
    }


    synchronized public void deleteGroup(String groupId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            if (db.isOpen()) {
                db.delete(GroupDao.TABLE_NAME, GroupDao.COLUMN_NAME_ID + " = ?", new String[]{groupId});
            }
        }
    }


    synchronized public void deleteUserHTMessage(String chatTo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            if (db.isOpen()) {
                db.delete(MessageDao.TABLE_NAME, MessageDao.COLUMN_NAME_FROM + " = ? or " + MessageDao.COLUMN_NAME_TO + " = ?", new String[]{chatTo, chatTo});
            }
        }
    }


    /**
     * update fxmessage
     *
     * @param msgId
     * @param userId
     */
    synchronized public void updateLastMessage(String userId, String msgId) {
        ContentValues values = new ContentValues();
        values.put(ConversationDao.COLUMN_NAME_MSG_ID, msgId);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(ConversationDao.TABLE_NAME, values, ConversationDao.COLUMN_NAME_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        }
    }

    /**
     * delete invitation message
     *
     * @param from
     */
    synchronized public void deleteFXMessage(String from) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(MessageDao.TABLE_NAME, MessageDao.COLUMN_NAME_FROM + " = ?", new String[]{from});
        }
    }


    public synchronized int getUnreadMessageCount() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select " + ConversationDao.COLUMN_NAME_UNREADCOUNT + " from " + ConversationDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public synchronized void setUnreadMessageCount(int count) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(ConversationDao.COLUMN_NAME_UNREADCOUNT, count);
            db.update(ConversationDao.TABLE_NAME, values, null, null);
        }
    }


    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
        dbMgr = null;
    }


    //根据msgId获取单个消息体
    synchronized public HTMessage getMessage(String msgId) {
        HTMessage message = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + MessageDao.TABLE_NAME + " where " + MessageDao.COLUMN_NAME_MSG_ID + "=? " + " limit 1", new String[]{msgId});
            if (cursor.moveToFirst()) {
                String from = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_FROM));
                String to = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TO));
                long localTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_lOCAL_TIME));
                long msgTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_TIME));
                String body = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_BODY));
                int status = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_STATUS));
                int type = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TYPE));
                int direct = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_DIRECT));
                int chatType = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_CHAT_TYPE));
                message = new HTMessage();
                message.setMsgId(msgId);
                message.setBody(new HTMessageBody(body));
                message.setFrom(from);
                message.setTo(to);
                message.setLocalTime(localTime);
                message.setTime(msgTime);
                String attributes = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_ATTRIBUTE));
                if (attributes != null) {
                    try {
                        message.setAttributes(JSONObject.parseObject(attributes));
                    }catch (JSONException e ){
                        message.setAttributes(new JSONObject());


                    }
                }
                if (type == HTMessage.Type.TEXT.ordinal()) {
                    message.setType(HTMessage.Type.TEXT);
                } else if (type == HTMessage.Type.IMAGE.ordinal()) {
                    message.setType(HTMessage.Type.IMAGE);
                } else if (type == HTMessage.Type.VOICE.ordinal()) {

                    message.setType(HTMessage.Type.VOICE);
                } else if (type == HTMessage.Type.VIDEO.ordinal()) {

                    message.setType(HTMessage.Type.VIDEO);
                } else if (type == HTMessage.Type.LOCATION.ordinal()) {

                    message.setType(HTMessage.Type.LOCATION);
                } else if (type == HTMessage.Type.FILE.ordinal()) {

                    message.setType(HTMessage.Type.FILE);
                } else {
                    message.setType(HTMessage.Type.TEXT);
                }
                if (status == HTMessage.Status.CREATE.ordinal()) {

                    message.setStatus(HTMessage.Status.CREATE);

                } else if (status == HTMessage.Status.FAIL.ordinal()) {
                    message.setStatus(HTMessage.Status.FAIL);

                } else if (status == HTMessage.Status.SUCCESS.ordinal()) {
                    message.setStatus(HTMessage.Status.SUCCESS);

                } else if (status == HTMessage.Status.INPROGRESS.ordinal()) {
                    message.setStatus(HTMessage.Status.INPROGRESS);
                } else if (status == HTMessage.Status.READ.ordinal()) {

                    message.setStatus(HTMessage.Status.READ);
                } else if (status == HTMessage.Status.ACKED.ordinal()) {

                    message.setStatus(HTMessage.Status.ACKED);
                }
                if (direct == HTMessage.Direct.RECEIVE.ordinal()) {
                    message.setDirect(HTMessage.Direct.RECEIVE);
                } else {
                    message.setDirect(HTMessage.Direct.SEND);
                }
                if (chatType == ChatType.singleChat.ordinal()) {
                    message.setChatType(ChatType.singleChat);
                } else if (chatType == ChatType.groupChat.ordinal()) {
                    message.setChatType(ChatType.groupChat);
                }
            }
            cursor.close();
        }
        return message;
    }

    //根据msgId获取单个消息体
    synchronized public HTMessage getLastMessage(String chatTo) {
        HTMessage message = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + MessageDao.TABLE_NAME + " where " + MessageDao.COLUMN_NAME_FROM + "=? or " + MessageDao.COLUMN_NAME_TO + "=?" + " order by " + MessageDao.COLUMN_NAME_MSG_TIME + " desc limit 1", new String[]{chatTo, chatTo});
            if (cursor.moveToFirst()) {
                String msgId = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_ID));
                String from = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_FROM));
                String to = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TO));
                long localTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_lOCAL_TIME));
                long msgTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_TIME));
                String body = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_BODY));
                int status = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_STATUS));
                int type = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TYPE));
                int direct = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_DIRECT));
                int chatType = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_CHAT_TYPE));
                message = new HTMessage();
                message.setMsgId(msgId);
                message.setBody(new HTMessageBody(body));
                message.setFrom(from);
                message.setTo(to);
                message.setLocalTime(localTime);
                message.setTime(msgTime);
                String attributes = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_ATTRIBUTE));
                if (attributes != null) {
                    try {
                        message.setAttributes(JSONObject.parseObject(attributes));
                    }catch (JSONException e ){
                        message.setAttributes(new JSONObject());


                    }
                }
                if (type == HTMessage.Type.TEXT.ordinal()) {
                    message.setType(HTMessage.Type.TEXT);
                } else if (type == HTMessage.Type.IMAGE.ordinal()) {
                    message.setType(HTMessage.Type.IMAGE);
                } else if (type == HTMessage.Type.VOICE.ordinal()) {

                    message.setType(HTMessage.Type.VOICE);
                } else if (type == HTMessage.Type.VIDEO.ordinal()) {

                    message.setType(HTMessage.Type.VIDEO);
                } else if (type == HTMessage.Type.LOCATION.ordinal()) {

                    message.setType(HTMessage.Type.LOCATION);
                } else if (type == HTMessage.Type.FILE.ordinal()) {

                    message.setType(HTMessage.Type.FILE);
                } else {
                    message.setType(HTMessage.Type.TEXT);
                }
                if (status == HTMessage.Status.CREATE.ordinal()) {

                    message.setStatus(HTMessage.Status.CREATE);

                } else if (status == HTMessage.Status.FAIL.ordinal()) {
                    message.setStatus(HTMessage.Status.FAIL);

                } else if (status == HTMessage.Status.SUCCESS.ordinal()) {
                    message.setStatus(HTMessage.Status.SUCCESS);

                } else if (status == HTMessage.Status.INPROGRESS.ordinal()) {
                    message.setStatus(HTMessage.Status.INPROGRESS);
                } else if (status == HTMessage.Status.READ.ordinal()) {

                    message.setStatus(HTMessage.Status.READ);
                } else if (status == HTMessage.Status.ACKED.ordinal()) {

                    message.setStatus(HTMessage.Status.ACKED);
                }
                if (direct == HTMessage.Direct.RECEIVE.ordinal()) {
                    message.setDirect(HTMessage.Direct.RECEIVE);
                } else {
                    message.setDirect(HTMessage.Direct.SEND);
                }
                if (chatType == ChatType.singleChat.ordinal()) {
                    message.setChatType(ChatType.singleChat);
                } else if (chatType == ChatType.groupChat.ordinal()) {
                    message.setChatType(ChatType.groupChat);
                }
            }
            cursor.close();
        }
        return message;
    }


    synchronized public Map<String, HTGroup> getAllGroups() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, HTGroup> htGroupMap = new Hashtable<String, HTGroup>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + GroupDao.TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String groupId = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_ID));
                String groupName = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_NAME));
                String groupDesc = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_DESC));
                String owner = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_OWNER));
                long time = cursor.getLong(cursor.getColumnIndex(GroupDao.COLUMN_NAME_TIME));
                HTGroup htGroup = new HTGroup();
                htGroup.setOwner(owner);
                htGroup.setGroupId(groupId);
                htGroup.setGroupName(groupName);
                htGroup.setGroupDesc(groupDesc);
                htGroup.setTime(time);
                htGroupMap.put(groupId, htGroup);

            }
            cursor.close();
        }
        return htGroupMap;

    }


    //
//    synchronized public Map<String, Map<String, HTMessage>> getAllMessages() {
//        Map<String, Map<String, HTMessage>> allMessage = new HashMap<String, Map<String, HTMessage>>();
//
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        if (db.isOpen()) {
//            Cursor cursor = db.rawQuery("select * from " + MessageDao.TABLE_NAME , null);
//            while (cursor.moveToNext()) {
//                String msgId = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_ID));
//                String from = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_FROM));
//                String to = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TO));
//                long localTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_lOCAL_TIME));
//                long msgTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_TIME));
//                String body = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_BODY));
//                int status = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_STATUS));
//                int type = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TYPE));
//                int direct = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_DIRECT));
//                int chatType = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_CHAT_TYPE));
//                HTMessage message = new HTMessage();
//                message.setMsgId(msgId);
//                message.setBody(new HTMessageBody(body));
//                message.setFrom(from);
//                message.setTo(to);
//                message.setLocalTime(localTime);
//                message.setTime(msgTime);
//                String attributes=cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_ATTRIBUTE));
//                if(attributes!=null){
//                    message.setAttributes(attributes);
//                    Log.d("attributes--->","getAllMessages");
//                }
//                if (type == HTMessage.Type.TEXT.ordinal()) {
//                    message.setType(HTMessage.Type.TEXT);
//                } else if (type == HTMessage.Type.IMAGE.ordinal()) {
//                    message.setType(HTMessage.Type.IMAGE);
//                }
//                if (status == HTMessage.Status.CREATE.ordinal()) {
//
//                    message.setStatus(HTMessage.Status.CREATE);
//
//                } else if (status == HTMessage.Status.FAIL.ordinal()) {
//                    message.setStatus(HTMessage.Status.FAIL);
//
//                } else if (status == HTMessage.Status.SUCCESS.ordinal()) {
//                    message.setStatus(HTMessage.Status.SUCCESS);
//
//                } else if (status == HTMessage.Status.INPROGRESS.ordinal()) {
//                    message.setStatus(HTMessage.Status.INPROGRESS);
//                }
//
//                if (chatType == ChatType.singleChat.ordinal()) {
//                    message.setChatType(ChatType.singleChat);
//                    if (direct == HTMessage.Direct.RECEIVE.ordinal()) {
//                        message.setDirect(HTMessage.Direct.RECEIVE);
//                        if (!allMessage.containsKey(from)) {
//                            Map<String, HTMessage> map = new HashMap<>();
//                            map.put(msgId, message);
//                            allMessage.put(from, map);
//                        } else {
//
//                            allMessage.get(from).put(msgId, message);
//
//                        }
//
//
//                    } else {
//                        message.setDirect(HTMessage.Direct.SEND);
//                        if (!allMessage.containsKey(to)) {
//                            Map<String, HTMessage> map = new HashMap<>();
//                            map.put(msgId, message);
//                            allMessage.put(to, map);
//                        } else {
//                            allMessage.get(to).put(msgId, message);
//                        }
//
//
//                    }
//
//                } else if (chatType == ChatType.groupChat.ordinal()) {
//                    message.setChatType(ChatType.groupChat);
//
//                    if (direct == HTMessage.Direct.RECEIVE.ordinal()) {
//                        message.setDirect(HTMessage.Direct.RECEIVE);
//                        if (!allMessage.containsKey(to)) {
//                            Map<String, HTMessage> map = new HashMap<>();
//                            map.put(msgId, message);
//                            allMessage.put(to, map);
//                        } else {
//                            allMessage.get(to).put(msgId, message);
//                        }
//                    } else {
//                        message.setDirect(HTMessage.Direct.SEND);
//                        if (!allMessage.containsKey(to)) {
//                            Map<String, HTMessage> map = new HashMap<>();
//                            map.put(msgId, message);
//                            allMessage.put(to, map);
//                        } else {
//                            allMessage.get(to).put(msgId, message);
//                        }
//                    }
//
//                }
//
//
//            }
//            cursor.close();
//        }
//        return allMessage;
//
//
//    }
    synchronized public List<HTMessage> getAllMessages(String userId, int pageSize) {
        List<HTMessage> allMessage = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + MessageDao.TABLE_NAME + " WHERE " + MessageDao.COLUMN_NAME_TO + "=? or ( " + MessageDao.COLUMN_NAME_FROM + "=? and " + MessageDao.COLUMN_NAME_CHAT_TYPE + "=" + ChatType.singleChat.ordinal() + " ) order by " + MessageDao.COLUMN_NAME_MSG_TIME + " desc limit " + pageSize, new String[]{userId, userId});
            while (cursor.moveToNext()) {
                String msgId = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_ID));
                String from = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_FROM));
                String to = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TO));
                long localTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_lOCAL_TIME));
                long msgTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_TIME));
                String body = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_BODY));
                int status = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_STATUS));
                int type = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TYPE));

                int direct = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_DIRECT));
                int chatType = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_CHAT_TYPE));
                HTMessage message = new HTMessage();
                message.setMsgId(msgId);
                message.setBody(new HTMessageBody(body));
                message.setFrom(from);
                message.setTo(to);
                message.setLocalTime(localTime);
                message.setTime(msgTime);
                String attributes = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_ATTRIBUTE));
                if (attributes != null) {
                    try {
                        message.setAttributes(JSONObject.parseObject(attributes));
                    }catch (JSONException e ){
                        message.setAttributes(new JSONObject());


                    }
                }
                if (type == HTMessage.Type.TEXT.ordinal()) {
                    message.setType(HTMessage.Type.TEXT);
                } else if (type == HTMessage.Type.IMAGE.ordinal()) {
                    message.setType(HTMessage.Type.IMAGE);
                } else if (type == HTMessage.Type.VOICE.ordinal()) {

                    message.setType(HTMessage.Type.VOICE);
                } else if (type == HTMessage.Type.VIDEO.ordinal()) {

                    message.setType(HTMessage.Type.VIDEO);
                } else if (type == HTMessage.Type.LOCATION.ordinal()) {

                    message.setType(HTMessage.Type.LOCATION);
                } else if (type == HTMessage.Type.FILE.ordinal()) {

                    message.setType(HTMessage.Type.FILE);
                } else {
                    message.setType(HTMessage.Type.TEXT);
                }
                if (status == HTMessage.Status.CREATE.ordinal()) {

                    message.setStatus(HTMessage.Status.CREATE);

                } else if (status == HTMessage.Status.FAIL.ordinal()) {
                    message.setStatus(HTMessage.Status.FAIL);

                } else if (status == HTMessage.Status.SUCCESS.ordinal()) {
                    message.setStatus(HTMessage.Status.SUCCESS);

                } else if (status == HTMessage.Status.INPROGRESS.ordinal()) {
                    message.setStatus(HTMessage.Status.INPROGRESS);
                }else if (status == HTMessage.Status.READ.ordinal()) {

                    message.setStatus(HTMessage.Status.READ);
                }else if (status == HTMessage.Status.ACKED.ordinal()) {

                    message.setStatus(HTMessage.Status.ACKED);
                }


                if (direct == HTMessage.Direct.RECEIVE.ordinal()) {
                    message.setDirect(HTMessage.Direct.RECEIVE);
                } else {
                    message.setDirect(HTMessage.Direct.SEND);
                }
                if (chatType == ChatType.singleChat.ordinal()) {
                    message.setChatType(ChatType.singleChat);

                } else if (chatType == ChatType.groupChat.ordinal()) {
                    message.setChatType(ChatType.groupChat);

                }
                allMessage.add(message);
            }
            cursor.close();
        }
        return allMessage;


    }

    synchronized public List<HTMessage> loadMoreMsgFromDB(String userId, long timestamp, int pageSize) {
        List<HTMessage> allMessage = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + MessageDao.TABLE_NAME + " WHERE (" + MessageDao.COLUMN_NAME_TO + "=? or(" + MessageDao.COLUMN_NAME_FROM + "=? and " + MessageDao.COLUMN_NAME_CHAT_TYPE + "=" + ChatType.singleChat.ordinal() + ")) and " + MessageDao.COLUMN_NAME_MSG_TIME + "<" + timestamp + " order by " + MessageDao.COLUMN_NAME_MSG_TIME + " desc  limit " + pageSize, new String[]{userId, userId});
            while (cursor.moveToNext()) {
                String msgId = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_ID));
                String from = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_FROM));
                String to = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TO));
                long localTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_lOCAL_TIME));
                long msgTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_TIME));
                String body = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_BODY));
                int status = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_STATUS));
                int type = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TYPE));
                int direct = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_DIRECT));
                int chatType = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_CHAT_TYPE));
                HTMessage message = new HTMessage();
                message.setMsgId(msgId);
                message.setBody(new HTMessageBody(body));
                message.setFrom(from);
                message.setTo(to);
                message.setLocalTime(localTime);
                message.setTime(msgTime);
                String attributes = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_ATTRIBUTE));
                if (attributes != null) {
                    try {
                        message.setAttributes(JSONObject.parseObject(attributes));
                    }catch (JSONException e ){
                        message.setAttributes(new JSONObject());


                    }
                }
                if (type == HTMessage.Type.TEXT.ordinal()) {
                    message.setType(HTMessage.Type.TEXT);
                } else if (type == HTMessage.Type.IMAGE.ordinal()) {
                    message.setType(HTMessage.Type.IMAGE);
                } else if (type == HTMessage.Type.VOICE.ordinal()) {

                    message.setType(HTMessage.Type.VOICE);
                } else if (type == HTMessage.Type.VIDEO.ordinal()) {

                    message.setType(HTMessage.Type.VIDEO);
                } else if (type == HTMessage.Type.LOCATION.ordinal()) {

                    message.setType(HTMessage.Type.LOCATION);
                } else if (type == HTMessage.Type.FILE.ordinal()) {

                    message.setType(HTMessage.Type.FILE);
                } else {
                    message.setType(HTMessage.Type.TEXT);
                }
                if (status == HTMessage.Status.CREATE.ordinal()) {

                    message.setStatus(HTMessage.Status.CREATE);

                } else if (status == HTMessage.Status.FAIL.ordinal()) {
                    message.setStatus(HTMessage.Status.FAIL);

                } else if (status == HTMessage.Status.SUCCESS.ordinal()) {
                    message.setStatus(HTMessage.Status.SUCCESS);

                } else if (status == HTMessage.Status.INPROGRESS.ordinal()) {
                    message.setStatus(HTMessage.Status.INPROGRESS);
                }else if (status == HTMessage.Status.READ.ordinal()) {

                    message.setStatus(HTMessage.Status.READ);
                }else if (status == HTMessage.Status.ACKED.ordinal()) {

                    message.setStatus(HTMessage.Status.ACKED);
                }


                if (direct == HTMessage.Direct.RECEIVE.ordinal()) {
                    message.setDirect(HTMessage.Direct.RECEIVE);
                } else {
                    message.setDirect(HTMessage.Direct.SEND);
                }
                if (chatType == ChatType.singleChat.ordinal()) {
                    message.setChatType(ChatType.singleChat);

                } else if (chatType == ChatType.groupChat.ordinal()) {
                    message.setChatType(ChatType.groupChat);

                }
                allMessage.add(message);
            }
            cursor.close();
        }
        return allMessage;


    }


    synchronized public List<HTMessage> searchMsgFromDB(String userId, String content) {

        if(TextUtils.isEmpty(content)){
            return new ArrayList<>();
        }
        List<HTMessage> allMessage = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor =null;
            if(!TextUtils.isEmpty(userId)){
                cursor=  db.rawQuery("select * from " + MessageDao.TABLE_NAME + " WHERE (" + MessageDao.COLUMN_NAME_TO + "=? or(" + MessageDao.COLUMN_NAME_FROM + "=? and " + MessageDao.COLUMN_NAME_CHAT_TYPE + "=" + ChatType.singleChat.ordinal() + ")) and " + MessageDao.COLUMN_NAME_BODY + " LIKE  \"%" + content+"%\"" + " order by " + MessageDao.COLUMN_NAME_MSG_TIME + " desc  ", new String[]{userId, userId});

            }else {
                cursor=  db.rawQuery("select * from " + MessageDao.TABLE_NAME + " WHERE  "+ MessageDao.COLUMN_NAME_BODY + " LIKE  \"%" + content+"%\"" + " order by " + MessageDao.COLUMN_NAME_MSG_TIME + " desc  ", null);

            }

            while (cursor.moveToNext()) {
                String msgId = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_ID));
                String from = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_FROM));
                String to = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TO));
                long localTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_lOCAL_TIME));
                long msgTime = cursor.getLong(cursor.getColumnIndex(MessageDao.COLUMN_NAME_MSG_TIME));
                String body = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_BODY));
                int status = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_STATUS));
                int type = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_TYPE));
                int direct = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_DIRECT));
                int chatType = cursor.getInt(cursor.getColumnIndex(MessageDao.COLUMN_NAME_CHAT_TYPE));
                HTMessage message = new HTMessage();
                message.setMsgId(msgId);
                message.setBody(new HTMessageBody(body));
                message.setFrom(from);
                message.setTo(to);
                message.setLocalTime(localTime);
                message.setTime(msgTime);
                String attributes = cursor.getString(cursor.getColumnIndex(MessageDao.COLUMN_NAME_ATTRIBUTE));
                if (attributes != null) {
                    try {
                        message.setAttributes(JSONObject.parseObject(attributes));
                    }catch (JSONException e ){
                        message.setAttributes(new JSONObject());


                    }
                }
                if (type == HTMessage.Type.TEXT.ordinal()) {
                    message.setType(HTMessage.Type.TEXT);
                } else if (type == HTMessage.Type.IMAGE.ordinal()) {
                    message.setType(HTMessage.Type.IMAGE);
                } else if (type == HTMessage.Type.VOICE.ordinal()) {

                    message.setType(HTMessage.Type.VOICE);
                } else if (type == HTMessage.Type.VIDEO.ordinal()) {

                    message.setType(HTMessage.Type.VIDEO);
                } else if (type == HTMessage.Type.LOCATION.ordinal()) {

                    message.setType(HTMessage.Type.LOCATION);
                } else if (type == HTMessage.Type.FILE.ordinal()) {

                    message.setType(HTMessage.Type.FILE);
                } else {
                    message.setType(HTMessage.Type.TEXT);
                }
                if (status == HTMessage.Status.CREATE.ordinal()) {

                    message.setStatus(HTMessage.Status.CREATE);

                } else if (status == HTMessage.Status.FAIL.ordinal()) {
                    message.setStatus(HTMessage.Status.FAIL);

                } else if (status == HTMessage.Status.SUCCESS.ordinal()) {
                    message.setStatus(HTMessage.Status.SUCCESS);

                } else if (status == HTMessage.Status.INPROGRESS.ordinal()) {
                    message.setStatus(HTMessage.Status.INPROGRESS);
                }else if (status == HTMessage.Status.READ.ordinal()) {

                    message.setStatus(HTMessage.Status.READ);
                }else if (status == HTMessage.Status.ACKED.ordinal()) {

                    message.setStatus(HTMessage.Status.ACKED);
                }


                if (direct == HTMessage.Direct.RECEIVE.ordinal()) {
                    message.setDirect(HTMessage.Direct.RECEIVE);
                } else {
                    message.setDirect(HTMessage.Direct.SEND);
                }
                if (chatType == ChatType.singleChat.ordinal()) {
                    message.setChatType(ChatType.singleChat);

                } else if (chatType == ChatType.groupChat.ordinal()) {
                    message.setChatType(ChatType.groupChat);

                }
                allMessage.add(message);
            }
            cursor.close();
        }
        return allMessage;


    }




    synchronized public void deleteHTMessageFromTimestamp(String chatTo,long timeStamp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            if (db.isOpen()) {
                db.delete(MessageDao.TABLE_NAME, MessageDao.COLUMN_NAME_FROM + " = ? or " + MessageDao.COLUMN_NAME_TO + " = ? and "+ MessageDao.COLUMN_NAME_MSG_TIME+" < ? ", new String[]{chatTo, chatTo,String.valueOf(timeStamp)});
            }
        }
    }





}
