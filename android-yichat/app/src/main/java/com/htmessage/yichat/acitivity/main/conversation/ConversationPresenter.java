package com.htmessage.yichat.acitivity.main.conversation;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by huangfangyi on 2017/6/27.
 * qq 84543217
 */

public class ConversationPresenter implements BaseConversationPresenter {
    private ConversationView conversationView;
    private List<HTConversation> allConversations = new ArrayList<>();
    private List<JSONObject> objectList = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1000:
                    //本地会话数据库查询结束

                    List<HTConversation> htConversations = (List<HTConversation>) msg.obj;
                    allConversations.clear();
                    allConversations.addAll(htConversations);
                    conversationView.adapterRefresh();
                    break;

            }
        }
    };

    public ConversationPresenter(ConversationView view) {
        conversationView = view;
        conversationView.setPresenter(this);
        // loadAllConversation();
    }


    @Override
    public List<HTConversation> getAllConversations() {

        return allConversations;
    }

    @Override
    public void deleteConversation(final String userId) {
        for (HTConversation htConversation : allConversations) {
            if (htConversation.getUserId().equals(userId)) {
                allConversations.remove(htConversation);

                conversationView.adapterRefresh();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HTClient.getInstance().messageManager().deleteUserMessage(userId, true);

                    }
                }).start();

                break;
            }
        }


    }

    @Override
    public void setTopConversation(final HTConversation htConversation) {
        htConversation.setTopTimestamp(System.currentTimeMillis());
        int originPosition = allConversations.indexOf(htConversation);
        if (originPosition != -1) {
            allConversations.remove(originPosition);
        }
        allConversations.add(0, htConversation);
        conversationView.adapterRefresh();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HTClient.getInstance().conversationManager().setConversationTop(htConversation, System.currentTimeMillis());
            }
        }).start();

        // conversationView.refreshALL();

    }

    @Override
    public void cancelTopConversation(HTConversation htConversation) {
        htConversation.setTopTimestamp(0);
        HTClient.getInstance().conversationManager().setConversationTop(htConversation, 0);
        conversationView.adapterRefresh();
        //  conversationView.refreshALL();
    }

    @Override
    public void onNewMsgReceived(HTMessage htMessage) {
        if (htMessage != null) {
            HTConversation htConversation = HTClient.getInstance().conversationManager().getConversation(htMessage.getUsername());
            if (htConversation != null) {

                if (allConversations.contains(htConversation)) {
                    //先删除原位置
                    int originPositon = allConversations.indexOf(htConversation);
                    if (originPositon != -1) {
                        allConversations.remove(originPositon);

                    }

                }
                if (htConversation.getTopTimestamp() > 0) {
                    //如果是已经置顶的消息
                    allConversations.add(0, htConversation);
                    conversationView.adapterRefresh();
                } else {
                    //如果是已经置顶的消息
                    allConversations.add(getTopCount(), htConversation);
                    conversationView.adapterRefresh();
                }

            }
        }
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
        conversation.setUnReadCount(0);
        conversationView.adapterRefresh();
    }


    @Override
    public void refreshContactsInServer() {


    }

    private void loadAllConversation() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!HTClient.getInstance().isLogined()) {
                    return;
                }

                List<HTConversation> htConversations = HTClient.getInstance().conversationManager().getAllConversations();
                if (htConversations == null || htConversations.size() == 0) {
                    return;
                }

                ListIterator<HTConversation> iterator = htConversations.listIterator();
                while (iterator.hasNext()) {
                    HTConversation htConversation = iterator.next();
                    if (htConversation.getChatType() == ChatType.groupChat) {
                        HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(htConversation.getUserId());
                        if (htGroup == null) {
                            iterator.remove();
                        }
                    }
                }

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(htConversations, new Comparator<HTConversation>() {
                    @Override
                    public int compare(HTConversation o1, HTConversation o2) {
//                            if(o1==null){
//                                return 1;
//                            }
//                            if(o2==null){
//                                return -1;
//                            }
//                            if(o1==null&&o2==null){
//                                return 0;
//                            }


                        long topTime1 = o1.getTopTimestamp();
                        long topTime2 = o2.getTopTimestamp();
                        if (topTime1 > topTime2) {
                            return -1;
                        } else if (topTime1 == topTime2) {
                            long time1 = o1.getTime();
                            long time2 = o2.getTime();
                            if (time1 > time2) {
                                return -1;
                            } else if (time1 == time2) {
                                return 0;
                            } else {
                                return 1;
                            }
                        } else {

                            return 1;
                        }

//                            if(topTime1>0||topTime2>0){
//                                if (topTime1 > topTime2) {
//                                    return -1;
//                                } else {
//                                    return 1;
//                                }
//                            }else {
//                                long time1 = o1.getTime();
//                                long time2 = o2.getTime();
//                                if (time1 > time2) {
//                                    return -1;
//                                } else {
//                                    return 1;
//                                }
//                            }


//                            if (topTime1 > topTime2) {
//                                return -1;
//                            } else {
//                                if (topTime1 == topTime2) {
//                                    long time1 = o1.getTime();
//                                    long time2 = o2.getTime();
//                                    if (time1 > time2) {
//                                        return -1;
//                                    } else {
//                                        return 1;
//                                    }
//
//                                } else {
//                                    return 1;
//                                }
//
//
//                            }


                    }
                });
                // }
//                else{
//                    Collections.sort(htConversations);
//                 }

                Message message = handler.obtainMessage();
                message.obj = htConversations;
                message.what = 1000;
                message.sendToTarget();


            }
        });
        thread.start();

    }


    @Override
    public void requestSmallProgram(int page) {
//        Activity activity = conversationView.getBaseActivity();
//        List<Param> params = new ArrayList<>();
//        new OkHttpUtils(activity).post(params, HTConstant.URL_GET_COLUMN, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONArray data = jsonObject.getJSONArray("data");
//                        List<JSONObject> objects = new ArrayList<JSONObject>();
//                        if (data != null) {
//                            objects.clear();
//                            for (int i = 0; i < data.size(); i++) {
//                                JSONObject object = data.getJSONObject(i);
//                                if (!objects.contains(object)) {
//                                    objects.add(object);
//                                }
//                            }
//                             objectList.clear();
//                            objectList.addAll(objects);
//                            if (conversationView != null && conversationView.getBaseActivity() != null) {
//
//                                conversationView.showSmall(objectList);
//                            }
//
//                        }
//                        break;
//                    default:
//                        objectList.clear();
//                        if (conversationView != null && conversationView.getBaseActivity() != null) {
//                            conversationView.showSmall(objectList);
//                        }
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                objectList.clear();
//                conversationView.showSmall(objectList);
//            }
//        });
    }

    @Override
    public void onMsgWithDraw(HTMessage htMessage) {

    }

    @Override
    public void checkFriendsAndGroups() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!HTClient.getInstance().isLogined()) {
                    return;
                }

                List<HTConversation> htConversations = HTClient.getInstance().conversationManager().getAllConversations();
                if (htConversations == null || htConversations.size() == 0) {
                    return;
                }

                ListIterator<HTConversation> iterator = htConversations.listIterator();
                while (iterator.hasNext()) {
                    HTConversation htConversation = iterator.next();
                    if (htConversation.getChatType() == ChatType.groupChat) {
                        HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(htConversation.getUserId());
                        if (htGroup == null) {
                            iterator.remove();
                        }
                    }
                }

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(htConversations, new Comparator<HTConversation>() {
                    @Override
                    public int compare(HTConversation o1, HTConversation o2) {
//                            if(o1==null){
//                                return 1;
//                            }
//                            if(o2==null){
//                                return -1;
//                            }
//                            if(o1==null&&o2==null){
//                                return 0;
//                            }


                        long topTime1 = o1.getTopTimestamp();
                        long topTime2 = o2.getTopTimestamp();
                        if (topTime1 > topTime2) {
                            return -1;
                        } else if (topTime1 == topTime2) {
                            long time1 = o1.getTime();
                            long time2 = o2.getTime();
                            if (time1 > time2) {
                                return -1;
                            } else if (time1 == time2) {
                                return 0;
                            } else {
                                return 1;
                            }
                        } else {

                            return 1;
                        }

//                            if(topTime1>0||topTime2>0){
//                                if (topTime1 > topTime2) {
//                                    return -1;
//                                } else {
//                                    return 1;
//                                }
//                            }else {
//                                long time1 = o1.getTime();
//                                long time2 = o2.getTime();
//                                if (time1 > time2) {
//                                    return -1;
//                                } else {
//                                    return 1;
//                                }
//                            }


//                            if (topTime1 > topTime2) {
//                                return -1;
//                            } else {
//                                if (topTime1 == topTime2) {
//                                    long time1 = o1.getTime();
//                                    long time2 = o2.getTime();
//                                    if (time1 > time2) {
//                                        return -1;
//                                    } else {
//                                        return 1;
//                                    }
//
//                                } else {
//                                    return 1;
//                                }
//
//
//                            }


                    }
                });
                // }
//                else{
//                    Collections.sort(htConversations);
//                 }

                Message message = handler.obtainMessage();
                message.obj = htConversations;
                message.what = 1000;
                message.sendToTarget();
            }
        });
        thread.start();


    }

    @Override
    public void start() {
        loadAllConversation();
    }

    private int getTopCount() {
        int count = 0;
        for (HTConversation htConversation : allConversations) {
            if (htConversation.getTopTimestamp() > 0) {
                count++;
            } else {
                break;
            }
        }


        return count;

    }
}
