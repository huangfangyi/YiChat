package com.fanxin.app.main.fragment;

import android.content.Intent;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.redpacketui.RedPacketConstant;
import com.fanxin.app.Constant;
import com.fanxin.app.DemoHelper;
import com.fanxin.app.db.InviteMessgeDao;
import com.fanxin.app.main.adapter.ConversationAdapter;
import com.fanxin.app.ui.ChatActivity;
import com.fanxin.easeui.ui.EaseConversationListFragment;
import com.fanxin.easeui.widget.EaseConversationList;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMMessage;
import com.fanxin.app.R;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConversationListFragment extends EaseConversationListFragment {

    private TextView errorText;

    @Override
    protected void initView() {
        super.initView();
        View errorView = (LinearLayout) View.inflate(getActivity(),R.layout.em_chat_neterror_item, null);
        errorItemContainer.addView(errorView);
        errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
        titleBar.setVisibility(View.GONE);
        getView().findViewById(R.id.re_search).setVisibility(View.GONE);

    }
    
    @Override
    protected void setUpView() {
        setArrayAdapter(new ConversationAdapter(getContext(),0,loadConversationList()));
        super.setUpView();
        // register context menu
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                String username = conversation.getUserName();
                if (username.equals(EMClient.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                else {
                    // start chat acitivity
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    if(conversation.isGroup()){
                        if(conversation.getType() == EMConversationType.ChatRoom){
                            // it's group chat
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
                        }else{
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
                        }
                        
                    }
                    // it's single chat
                    intent.putExtra(Constant.EXTRA_USER_ID, username);
                    startActivity(intent);
                }
            }
        });
        conversationListView.setConversationListHelper(new EaseConversationList.EaseConversationListHelper() {
            @Override
            public String onSetItemSecondaryText(EMMessage lastMessage) {
                if (lastMessage.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {
                    try {
                        String sendNick = lastMessage.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME);
                        String receiveNick = lastMessage.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME);
                        String msg;
                        if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
                            msg = String.format(getResources().getString(R.string.money_msg_someone_take_money),receiveNick);
                        } else {
                            if (sendNick.equals(receiveNick)) {
                                msg = getResources().getString(R.string.money_msg_take_money);
                            } else {
                                msg = String.format(getResources().getString(R.string.money_msg_take_someone_money),sendNick);
                            }
                        }
                        return msg;
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });
        super.setUpView();
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (NetUtils.hasNetwork(getActivity())){
         errorText.setText(R.string.can_not_connect_chat_server_connection);
        } else {
          errorText.setText(R.string.the_current_network);
        }
    }
    
    @Override

    protected List<EMConversation> loadConversationList(){
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0&&conversation.getType()!= EMConversation.EMConversationType.ChatRoom||(conversation.getType()== EMConversation.EMConversationType.Chat&& DemoHelper.getInstance().getContactList().containsKey(conversation.getUserName()))) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu); 
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean deleteMessage = false;
        if (item.getItemId() == R.id.delete_message) {
            deleteMessage = true;
        } else if (item.getItemId() == R.id.delete_conversation) {
            deleteMessage = false;
        }
    	EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
    	if (tobeDeleteCons == null) {
    	    return true;
    	}
        try {
            // delete conversation
            EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.getUserName(), deleteMessage);
            InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
            inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh();

        // update unread count
        ((MainActivity) getActivity()).updateUnreadLabel();
        return true;
    }

}
