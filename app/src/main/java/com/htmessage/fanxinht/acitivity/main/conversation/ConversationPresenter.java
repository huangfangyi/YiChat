package com.htmessage.fanxinht.acitivity.main.conversation;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.utils.ACache;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2017/6/27.
 * qq 84543217
 */

public class ConversationPresenter implements BaseConversationPresenter {
    private ConversationView conversationView;
    private Context context;
    private List<HTConversation> allConversations;
    private ACache aCache;
    private String noticeKey = HTApp.getInstance().getUsername() + "notice";
    private List<JSONObject> noticeList = new ArrayList<>();


    public ConversationPresenter(ConversationView view) {
        conversationView = view;
        conversationView.setPresenter(this);
        context = conversationView.getBaseContext();
        allConversations = HTClient.getInstance().conversationManager().getAllConversations();
        aCache = ACache.get(conversationView.getBaseActivity());
        getNoticeList();
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
        HTClient.getInstance().conversationManager().setConversationTop(htConversation, System.currentTimeMillis());
        conversationView.refresh();

    }

    @Override
    public void cancelTopConversation(HTConversation htConversation) {
        HTClient.getInstance().conversationManager().setConversationTop(htConversation, 0);
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


    public void getNoticeList() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("currentPage", "1"));
        params.add(new Param("pageSize", "20"));
        new OkHttpUtils(conversationView.getBaseActivity()).post(params, HTConstant.URL_GET_SHOW_NOTICE_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (data != null && data.size() != 0) {
                            noticeList.clear();
                            for (int i = 0; i < data.size(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                if (!noticeList.contains(object)) {
                                    noticeList.add(object);
                                }
                            }
                        }
                        aCache.put(noticeKey, data);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                CommonUtils.showToastShort(conversationView.getBaseContext(), errorMsg);
            }
        });
    }

    private List<JSONObject> getCacheShowNotice() {
        List<JSONObject> groups = new ArrayList<>();
        JSONArray data = aCache.getAsJSONArray(noticeKey);
        if (data != null && data.size() != 0) {
            for (int i = 0; i < data.size(); i++) {
                JSONObject object = data.getJSONObject(i);
                if (!groups.contains(object)) {
                    groups.add(object);
                }
            }
        }
        return groups;
    }

    @Override
    public List<JSONObject> getShowNotice() {
        noticeList.clear();
        noticeList.addAll(getCacheShowNotice());
        return noticeList;
    }
}
