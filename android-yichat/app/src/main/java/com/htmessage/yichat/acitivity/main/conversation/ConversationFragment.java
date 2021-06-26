package com.htmessage.yichat.acitivity.main.conversation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTClientHelper;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.ChatActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.DensityUtil;
import com.htmessage.sdk.manager.MmvkManger;
import com.htmessage.yichat.widget.HTAlertDialog;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.utils.MessageUtils;

import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.htmessage.yichat.IMAction.ACTION_CONNECTION_CHANAGED;
import static com.htmessage.yichat.IMAction.ACTION_DELETE_GROUP;
import static com.htmessage.yichat.IMAction.ACTION_MESSAGE_WITHDROW;
import static com.htmessage.yichat.IMAction.ACTION_NEW_MESSAGE;
import static com.htmessage.yichat.IMAction.ACTION_REMOVED_FROM_GROUP;
import static com.htmessage.yichat.IMAction.ACTION_UPDATE_CHAT_TITLE;
import static com.htmessage.yichat.IMAction.CMD_DELETE_FRIEND;
import static com.htmessage.yichat.IMAction.DELETE_FRIEND_LOCAL;

public class ConversationFragment extends Fragment implements ConversationView {

    private ListView listView;
    private ConversationAdapter adapter;

    public RelativeLayout errorItem;
    public TextView errorText;
    public NewMeesageListener mListener;
    private ConversationPresenter conPresenter;
    private int chatNumalType = MessageUtils.CHAT_SINGLE;
    private GridView gridview;
    private SmallProgramGridViewAdapter gridViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void setPresenter(ConversationPresenter presenter) {
        conPresenter = presenter;
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
        HTAlertDialog dialog = new HTAlertDialog(getActivity(), null, new String[]{getString(R.string.delete), topTitle});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                if (position == 0) {
                    conPresenter.deleteConversation(htConversation.getUserId());
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

//    public void refreshALL() {
//
//
//        Observable.create(new ObservableOnSubscribe<List<HTConversation>>() { // 第一步：初始化Observable
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<List<HTConversation>> e) throws Exception {
//                List<HTConversation> htConversations = HTClient.get().conversationManager().getAllConversations();
//                List<HTGroup> htGroups = HTClient.get().groupManager().getAllGroups();
//                for (int i = 0; i < htConversations.size(); i++) {
//                    HTConversation htConversation = htConversations.get(i);
//                    if (htConversation.getChatType() == ChatType.groupChat) {
//                        boolean hasGroup = false;
//                        for (HTGroup htGroup : htGroups) {
//                            if (htGroup.getGroupId().equals(htConversation.getMyUserId())) {
//                                hasGroup = true;
//                                break;
//                            }
//                        }
//                        if (!hasGroup) {
//                            htConversations.remove(i);
//                            HTClient.get().conversationManager().deleteConversation(htConversation.getMyUserId());
//                        }
//                    }
//                }
//                e.onNext(htConversations);
//            }
//        }).doOnNext(new Consumer<List<HTConversation>>() {
//            @Override
//            public void accept(List<HTConversation> htConversations) throws Exception {
//            }
//        })
//
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<List<HTConversation>>() {
//                    @Override
//                    public void accept(List<HTConversation> htConversations) throws Exception {
//                        List<HTConversation> conversationList = conPresenter.getAllConversations();
//                        conversationList.clear();
//                        conversationList.addAll(htConversations);
//                        adapter.notifyDataSetChanged();
//                        onUnreadMsgChange();
//
//                    } // 第三步：订阅
//
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//
//                    }
//                });
//
//
//    }

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
        }
    }

    private void onUnreadMsgChange() {
        mListener.onUnReadMsgs(conPresenter.getUnreadMsgCount());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initView();
        initData();
        setListener();
        conPresenter.start();

    }

    private void getData() {
        registerConnectionBroadCast();
        //   conPresenter.requestSmallProgram(1);
    }

    public void showSmall(List<JSONObject> smallList) {
        if (smallList == null || smallList.size() == 0) {
            changeGridView(1);
            return;
        } else {
            changeGridView(smallList.size());
        }
        gridViewAdapter = new SmallProgramGridViewAdapter(getActivity(), smallList);
        gridview.setAdapter(gridViewAdapter);
    }

    @Override
    public void adapterRefresh() {

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        onUnreadMsgChange();
    }


    /**
     * 将GridView改成单行横向布局
     */
    private void changeGridView(int size) {
        // item宽度
        int itemWidth = DensityUtil.dip2px(getActivity(), 90);
        // item之间的间隔
        int itemPaddingH = DensityUtil.dip2px(getActivity(), 1);
        // 计算GridView宽度
        int gridviewWidth = size * (itemWidth + itemPaddingH);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridview.setLayoutParams(params);
        gridview.setColumnWidth(itemWidth);
        gridview.setHorizontalSpacing(itemPaddingH);
        gridview.setStretchMode(GridView.NO_STRETCH);
        gridview.setNumColumns(size);
    }

    private void initView() {
        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
        listView = (ListView) getView().findViewById(R.id.list);
        gridview = (GridView) getView().findViewById(R.id.gridview);

    }

    private void initData() {
        adapter = new ConversationAdapter(getActivity(), conPresenter.getAllConversations());
        // 设置adapter
        listView.setAdapter(adapter);
    }

    private void setListener() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HTConversation htConversation = adapter.getItem(position);
                if (htConversation == null) {
                    return;
                }
                String userId = htConversation.getUserId();
                conPresenter.markAllMessageRead(htConversation);
                String nick = userId;
                ChatType chatType = htConversation.getChatType();
                if (chatType == ChatType.singleChat) {
                    chatNumalType = MessageUtils.CHAT_SINGLE;


                } else {
                    chatNumalType = MessageUtils.CHAT_GROUP;

                }
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("chatType", chatNumalType);
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
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject item = gridViewAdapter.getItem(position);
                String smallUrl = item.getString("url");
                String title = item.getString("title");
                CommonUtils.openUrl(getActivity(), smallUrl, title);
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_CONNECTION_CHANAGED.equals(intent.getAction())) {
                //   boolean isConnected = intent.getBooleanExtra("state", true);
//                if (isConnected) {
//                  //  errorItem.setVisibility(View.GONE);
//                } else {
////                    errorItem.setVisibility(View.VISIBLE);
////                    if (CommonUtils.isNetWorkConnected(getActivity())) {
////                        errorText.setText(R.string.can_not_connect_chat_server_connection);
////                    } else {
////                        errorText.setText(R.string.the_current_network);
////                    }
//                }
                if (!CommonUtils.isNetWorkConnected(getActivity())) {
                    errorItem.setVisibility(View.VISIBLE);
                    errorText.setText(R.string.the_current_network);
                } else {
                    errorItem.setVisibility(View.GONE);
                }

            } else if (ACTION_NEW_MESSAGE.equals(intent.getAction())) {
                //   收到新消息,收到撤回消息,收到群相关消息-被提出群聊
                HTMessage htMessage = intent.getParcelableExtra("message");
                if (htMessage != null) {
                    conPresenter.onNewMsgReceived(htMessage);
                }
            } else if (ACTION_REMOVED_FROM_GROUP.equals(intent.getAction()) ||
                    ACTION_DELETE_GROUP.equals(intent.getAction())) {
                String userId = intent.getStringExtra("userId");
                conPresenter.deleteConversation(userId);
                //   收到新消息,收到撤回消息,收到群相关消息-被提出群聊
                // 群退出时要删除会话
                //   refreshALL();
            } else if (CMD_DELETE_FRIEND.equals(intent.getAction()) || DELETE_FRIEND_LOCAL.equals(intent.getAction())) {
                String userId = intent.getStringExtra("userId");
//                HTConversation conversation = HTClient.get().conversationManager().getConversation(userId);
//                if (conversation != null) {
                conPresenter.deleteConversation(userId);
                //  }
            } else if (ACTION_MESSAGE_WITHDROW.equals(intent.getAction())) {
                HTMessage htMessage = intent.getParcelableExtra("message");
                if (htMessage != null) {
                    conPresenter.onNewMsgReceived(htMessage);
                }

            }

        }
    };


    private void registerConnectionBroadCast() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CONNECTION_CHANAGED);
        intentFilter.addAction(ACTION_NEW_MESSAGE);
        intentFilter.addAction(ACTION_MESSAGE_WITHDROW);
        intentFilter.addAction(CMD_DELETE_FRIEND);
        intentFilter.addAction(DELETE_FRIEND_LOCAL);
        intentFilter.addAction(ACTION_REMOVED_FROM_GROUP);
        intentFilter.addAction(ACTION_DELETE_GROUP);
        intentFilter.addAction(ACTION_UPDATE_CHAT_TITLE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                conPresenter.checkFriendsAndGroups();
            }
        },500);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = conPresenter.getUnreadMsgCount();
                if (count < 0) {
                    count = 0;
                }
                ShortcutBadger.applyCount(getBaseContext(), count); //for 1.1.4+
                MmvkManger.getIntance().putLong("BadgerCount", count);
                HTClientHelper.BadgerCount = count;
            }
        }).start();
    }

}
