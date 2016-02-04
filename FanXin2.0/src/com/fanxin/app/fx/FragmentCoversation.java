package com.fanxin.app.fx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.fx.others.ConversationAdapter;
import com.fanxin.app.fx.others.TopUser;

public class FragmentCoversation extends Fragment {

    private boolean hidden;
    private ListView listView;
    private ConversationAdapter adapter;

    private List<EMConversation> normal_list = new ArrayList<EMConversation>();
    private List<EMConversation> top_list = new ArrayList<EMConversation>();

    private Map<String, TopUser> topMap;
    public RelativeLayout errorItem;
    public TextView errorText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false))
            return;
        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);

        topMap = MYApplication.getInstance().getTopUserList();
        normal_list.addAll(loadConversationsWithRecentChat());
        listView = (ListView) getView().findViewById(R.id.list);
        adapter = new ConversationAdapter(getActivity(), normal_list, top_list,topMap);
        // 设置adapter
        listView.setAdapter(adapter);

      
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        normal_list.clear();
        normal_list.addAll(loadConversationsWithRecentChat());

        adapter = new ConversationAdapter(getActivity(), normal_list, top_list,topMap);
        listView.setAdapter(adapter);
    }

    /**
     * 获取所有会话
     * 
     * @param context
     * @return +
     */
    private List<EMConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager
                .getInstance().getAllConversations();

        List<EMConversation> list = new ArrayList<EMConversation>();
        List<EMConversation> topList1 = new ArrayList<EMConversation>();

        // 置顶列表再刷新一次

        // 过滤掉messages seize为0的conversation
        for (EMConversation conversation : conversations.values()) {

            if (conversation.getAllMessages().size() != 0) {
                // 不在置顶列表里面
                if (!topMap.containsKey(conversation.getUserName())) {
                    list.add(conversation);
                } else {
                    // 在置顶列表里面
                    topList1.add(conversation);
                }
                //
                // for(EMMessage msg:conversation.getAllMessages()){
                // if(msg.getFrom().equals("admin")){
                // try {
                // Log.e("type--->>",msg.getStringAttribute("type"));
                // Log.e("groupid--->>",msg.getStringAttribute("groupid"));
                // } catch (EaseMobException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }
                // }
                //
                // }

            }

        }
        top_list.clear();
        top_list.addAll(topList1);
        // 排序
        sortConversationByLastChatTime(list);
        sortConversationByLastChatTime(top_list);
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     * 
     * @param usernames
     */
    private void sortConversationByLastChatTime(
            List<EMConversation> conversationList) {
        Collections.sort(conversationList, new Comparator<EMConversation>() {
            @Override
            public int compare(final EMConversation con1,
                    final EMConversation con2) {

                EMMessage con2LastMessage = con2.getLastMessage();
                EMMessage con1LastMessage = con1.getLastMessage();
                if (con2LastMessage.getMsgTime() == con1LastMessage
                        .getMsgTime()) {
                    return 0;
                } else if (con2LastMessage.getMsgTime() > con1LastMessage
                        .getMsgTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden && !((MainActivity) getActivity()).isConflict) {
            refresh();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }

   

}
