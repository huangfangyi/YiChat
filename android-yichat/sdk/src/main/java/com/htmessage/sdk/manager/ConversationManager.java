package com.htmessage.sdk.manager;

import android.content.Context;
import android.util.Pair;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.data.dao.ConversationDao;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangfangyi on 2016/11/30.
 * qq 84543217
 */

public class ConversationManager {
     //   private static ConversationManager converstionManager;
     private static ConversationDao htConversationDao;
    private static Map<String, HTConversation> allConversations = new HashMap<>();

//    synchronized public static void init(Context context) {
//        Log.d("Cache---->","ConversationManager:"+ HTPreferenceManager.getInstance().getUser().getUsername());
//        converstionManager = new ConversationManager(context);
//    }

    public ConversationManager(Context context) {
         htConversationDao = new ConversationDao(context);
         initAllConversations();
    }


//    public static ConversationManager getInstance() {
//        if (converstionManager == null) {
//            throw new RuntimeException("please init first!");
//        }
//        return converstionManager;
//    }


    private void initAllConversations() {

                    allConversations = htConversationDao.getConversationList();

    }

    public void markAllMessageRead(String userId) {
        HTConversation htConversation = allConversations.get(userId);
        if (htConversation != null) {

            htConversation.setUnReadCount(0);
            refreshConversationList(userId, htConversation);
        }

        //   MessageManager.getInstance().
    }

    public List<HTConversation> getAllConversations() {
        if (allConversations == null) {
            allConversations = new HashMap<>();
        }

        return loadConversationList(allConversations);
    }


    public HTConversation getConversation(String userId) {
        HTConversation htConversation = null;
        if (allConversations != null) {
            htConversation = allConversations.get(userId);
        }

        return htConversation;
    }

    public void updateConversation(HTMessage message, boolean isAddUnreadCount) {

        HTConversation htConversation=null;
        String userId = message.getUsername();

        if (allConversations.containsKey(userId)) {
            htConversation=allConversations.get(userId);
//            htConversation.setUserId(userId);
            htConversation.setChatType(message.getChatType());
            htConversation.setTime(message.getTime());
            int currentUnreadCount = allConversations.get(userId).getUnReadCount();
            //已有会话数据
            if (isAddUnreadCount) {
                //需要增加未读条数
                htConversation.setUnReadCount(currentUnreadCount + 1);
            } else {
                //不需要增加未读条数
                htConversation.setUnReadCount(currentUnreadCount);
            }
        } else {
            htConversation = new HTConversation();

           // htConversation.setLastMsgId(message.getMsgId());
            htConversation.setUserId(userId);
            htConversation.setChatType(message.getChatType());
            htConversation.setTime(message.getTime());
            //还未创建会话列表
            if (isAddUnreadCount) {
                //需要增加未读条数
                htConversation.setUnReadCount(1);
            } else {
                //不需要增加未读条数
                htConversation.setUnReadCount(0);
            }
        }
        refreshConversationList(userId, htConversation);
    }

    public void refreshConversationList(final String userId, final HTConversation htConversation) {
        allConversations.put(userId, htConversation);
        htConversationDao.saveConversation(htConversation);
    }

    public List<HTConversation> loadConversationList(Map<String, HTConversation> htConversationMap) {

            if(htConversationMap==null||htConversationMap.size()==0){
                return new ArrayList<>();
            }
            return new ArrayList<>(htConversationMap.values()) ;


    }

    public void deleteConversationAndMessage(String chatTo) {

        HTClient.getInstance().messageManager().deleteUserMessage(chatTo, true);
    }


    public void deleteConversation(String chatTo) {
        htConversationDao.deleteConversation(chatTo);
        if (allConversations.containsKey(chatTo)) {
            allConversations.remove(chatTo);

        }
    }


    public void sortConversationByLastChatTime(List<Pair<Long, HTConversation>> messages) {
        Collections.sort(messages, new Comparator<Pair<Long, HTConversation>>() {
            @Override
            public int compare(final Pair<Long, HTConversation> con1, final Pair<Long, HTConversation> con2) {
                if(con2.second.getTopTimestamp()!=0&&con1.second.getTopTimestamp()!=0){
                    //都是置顶消息
                    if (con1.first == con2.first) {
                        return 0;
                    } else if (con2.first > con1.first) {
                        return 1;
                    } else {
                        return -1;
                    }
                }else if(con2.second.getTopTimestamp()!=0&&con1.second.getTopTimestamp()==0){
                    return 1;
                }else if(con2.second.getTopTimestamp()==0&&con1.second.getTopTimestamp()!=0) {
                    return -1;
                }else if(con2.second.getTopTimestamp()==0&&con1.second.getTopTimestamp()==0){
                    //都是置顶消息
                    if (con1.first == con2.first) {
                        return 0;
                    } else if (con2.first > con1.first) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
               return 0;
            }

        });
    }

    public void setConversationTop(HTConversation  htConversation,long timestamp){
        htConversation.setTopTimestamp(timestamp);
        saveConversation(htConversation);

    }

    private void saveConversation(HTConversation htConversation){
        allConversations.put(htConversation.getUserId(),htConversation);
        htConversationDao.saveConversation(htConversation);

    }


}
