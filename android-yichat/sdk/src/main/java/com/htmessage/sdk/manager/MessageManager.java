package com.htmessage.sdk.manager;

import android.content.Context;
import android.util.Pair;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.data.dao.MessageDao;
import com.htmessage.sdk.model.HTMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by huangfangyi on 2016/12/1.
 * qq 84543217
 */

public class MessageManager {
     private Context context;
    private MessageDao messageDao;


    public MessageManager(Context context) {
        this.context = context;
        messageDao = new MessageDao(context);

    }


    public void updateSuccess(HTMessage message) {

        message.setStatus(HTMessage.Status.SUCCESS);
        saveMessage(message, false);


    }

    public void deleteMessage(String chatTo, String msgId) {

        messageDao.deleteHTMessage(msgId);

    }



    public void deleteMessageFromTimestamp( String chatTo, long timeStamp) {

        messageDao.deleteHTMessageFromTimestamp(chatTo,timeStamp);

    }


    public void deleteUserMessage(String chatTo, boolean isDeleteConversation) {

        messageDao.deleteUserHTMessage(chatTo);
        if (isDeleteConversation) {
            HTClient.getInstance().conversationManager().deleteConversation(chatTo);
        }
     }



    public List<HTMessage> getMessageList(final String userId) {




        return  messageDao.getAllMessages(userId) ;


}


    //获取单条消息
    public HTMessage getMssage(String userId, String msgId) {

        MessageDao messageDao = new MessageDao(context);
        return messageDao.getMessage(msgId);
    }

    //获取单条消息
    public HTMessage getMssage(String msgId) {

        MessageDao messageDao = new MessageDao(context);
        return messageDao.getMessage(msgId);
    }

    //获取某个对话的最近的消息,以数据库为准
    public HTMessage getLastMessage(String chatTo) {

        MessageDao messageDao = new MessageDao(context);
        return messageDao.getLastMessage(chatTo);
    }

    //获取某个对话的最近第2条的消息,以数据库为准
    public HTMessage getLastSecondMessage(String chatTo) {

        MessageDao messageDao = new MessageDao(context);
        return messageDao.getLastMessageOffsize(chatTo,1);
    }

    public void refreshMessageList(String userId, HTMessage htMessage) {
    }

    public List<HTMessage> loadMoreMsgFromDB(String chatTo, long timestamp, int pageSize) {
        return messageDao.loadMoreMsgFromDB(chatTo, timestamp, pageSize);
    }

    public List<HTMessage> searchMsgFromDB(String chatTo, String  content) {
        return messageDao.searchMsgFromDB(chatTo, content);
    }

    public synchronized void saveMessage(HTMessage htMessage, boolean isAddUnCount) {
        String chatTo = htMessage.getUsername();

         MessageDao messageDao = new MessageDao(context);
         messageDao.saveMessage(htMessage);
         HTClient.getInstance().conversationManager().updateConversation(htMessage, isAddUnCount);
    }

    public synchronized void updateMessageInDB(HTMessage htMessage) {

        MessageDao messageDao = new MessageDao(context);
        messageDao.saveMessage(htMessage);
     }

    private List<HTMessage> loadMessageList(List<HTMessage> htMessages) {
        List<Pair<Long, HTMessage>> sortList = new ArrayList<Pair<Long, HTMessage>>();
        synchronized (htMessages) {
            for (HTMessage htMessage : htMessages) {
                try {
                    sortList.add(new Pair<Long, HTMessage>(htMessage.getTime(), htMessage));
                } catch (NullPointerException e) {


                }
            }
            try {
                // Internal is TimSort algorithm, has bug
                sortMessageByLastChatTime(sortList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<HTMessage> list = new ArrayList<HTMessage>();
            for (Pair<Long, HTMessage> sortItem : sortList) {
                list.add(sortItem.second);
            }
            return list;
        }

    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param
     */

    private void sortMessageByLastChatTime(List<Pair<Long, HTMessage>> messages) {
        Collections.sort(messages, new Comparator<Pair<Long, HTMessage>>() {
            @Override
            public int compare(final Pair<Long, HTMessage> con1, final Pair<Long, HTMessage> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return -1;
                } else {
                    return 1;
                }
            }

        });
    }


}
