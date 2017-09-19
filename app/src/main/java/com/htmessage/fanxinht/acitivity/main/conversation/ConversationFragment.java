package com.htmessage.fanxinht.acitivity.main.conversation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htmessage.fanxinht.HTConstant;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.acitivity.chat.ChatActivity;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.widget.HTAlertDialog;

public class ConversationFragment extends Fragment implements ConversationView {


    private ListView listView;
    private ConversationAdapter adapter;

    public RelativeLayout errorItem;
    public TextView errorText;
    public NewMeesageListener mListener;
    private ConversationPresenter conPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        errorItem = (RelativeLayout) root.findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
        listView = (ListView) root.findViewById(R.id.list);
        return root;
    }

    @Override
    public void setPresenter(ConversationPresenter presenter) {
        //  conPresenter = presenter;
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }

    @Override
    public void showItemDialog(final HTConversation htConversation) {
        String topTitle = getString(R.string.stick_conversation);
        if (htConversation.getTopTimestamp() != 0) {
            //已经置顶的会话,显示取消置顶
            topTitle = getString(R.string.cancle_stick_conversation);
        }
        HTAlertDialog HTAlertDialog = new HTAlertDialog(getActivity(), null, new String[]{getString(R.string.delete), topTitle});
        HTAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                if (position == 0) {
                    conPresenter.deleteConversation(htConversation);
                } else if (position == 1) {
                    if (htConversation.getTopTimestamp() != 0) {//如果是置顶过的 就取消置顶
                        conPresenter.cancelTopConversation(htConversation);
                    } else {  //如果是没有置顶的就置顶
                        conPresenter.setTopConversation(htConversation);
                    }
                }
            }
        });
    }

    @Override
    public void refresh() {
        conPresenter.refreshConversations();
        adapter.notifyDataSetChanged();
        onUnreadMsgChange();
    }

    public interface NewMeesageListener {
        //返回多少条未读消息
        void onUnReadMsgs(int count);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        conPresenter = new ConversationPresenter(this);
        if (context instanceof NewMeesageListener) {
            mListener = ((NewMeesageListener) context);
            onUnreadMsgChange();
        }
    }

    private void onUnreadMsgChange() {
        mListener.onUnReadMsgs(conPresenter.getUnreadMsgCount());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new ConversationAdapter(getActivity(), conPresenter.getAllConversations());
        // 设置adapter
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HTConversation htConversation = adapter.getItem(position);
                conPresenter.markAllMessageRead(htConversation);
                Intent intent = new Intent(getActivity(), ChatActivity.class).putExtra("userId", htConversation.getUserId());
                if (htConversation.getChatType() == ChatType.groupChat) {
                    intent.putExtra("chatType", MessageUtils.CHAT_GROUP);
                }
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                HTConversation htConversation = adapter.getItem(i);
                showItemDialog(htConversation);
                return true;
            }
        });
        registerConnectionBroadCast();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (IMAction.ACTION_CONNECTION_CHANAGED.equals(intent.getAction())) {
                boolean isConnected = intent.getBooleanExtra("state", true);
                if (isConnected) {
                    errorItem.setVisibility(View.GONE);
                } else {
                    errorItem.setVisibility(View.VISIBLE);
                    if (CommonUtils.isNetWorkConnected(getActivity())) {
                        errorText.setText(R.string.can_not_connect_chat_server_connection);
                    } else {
                        errorText.setText(R.string.the_current_network);
                    }
                }

            } else if (IMAction.ACTION_NEW_MESSAGE.equals(intent.getAction()) || IMAction.ACTION_MESSAGE_WITHDROW.equals(intent.getAction())
                    || IMAction.ACTION_REMOVED_FROM_GROUP.equals(intent.getAction())) {
                //   收到新消息,收到撤回消息,收到群相关消息-被提出群聊
                refresh();

            } else if (IMAction.CMD_DELETE_FRIEND.equals(intent.getAction())) {
                String userId = intent.getStringExtra(HTConstant.JSON_KEY_HXID);
                HTConversation conversation = HTClient.getInstance().conversationManager().getConversation(userId);
                if (conversation != null) {
                    conPresenter.deleteConversation(conversation);
                }
            }
        }
    };


    private void registerConnectionBroadCast() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMAction.ACTION_CONNECTION_CHANAGED);
        intentFilter.addAction(IMAction.ACTION_NEW_MESSAGE);
        intentFilter.addAction(IMAction.ACTION_MESSAGE_WITHDROW);
        intentFilter.addAction(IMAction.CMD_DELETE_FRIEND);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}
