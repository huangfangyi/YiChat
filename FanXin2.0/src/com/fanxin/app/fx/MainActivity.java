package com.fanxin.app.fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;

import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.db.InviteMessgeDao;
import com.fanxin.app.db.UserDao;
import com.fanxin.app.domain.InviteMessage;
import com.fanxin.app.domain.InviteMessage.InviteMesageStatus;
import com.fanxin.app.domain.User;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class MainActivity extends BaseActivity {
    // 未读消息textview
    private TextView unreadLabel;
    // 未读通讯录textview
    TextView unreadAddressLable;
    protected static final String TAG = "MainActivity";

    private Fragment[] fragments;
    public FragmentCoversation homefragment;
    private FragmentFriends contactlistfragment;
    private FragmentFind findfragment;
    private FragmentProfile profilefragment;
    private ImageView[] imagebuttons;
    private TextView[] textviews;
    private int index;
    // 当前fragment的index
    private int currentTabIndex;
    private NewMessageBroadcastReceiver msgReceiver;
    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private boolean isConflictDialogShow;
    private boolean isAccountRemovedDialogShow;
    // 账号在别处登录
    public boolean isConflict = false;
    // 账号被移除
    private boolean isCurrentAccountRemoved = false;

    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    private ImageView iv_add;
    private ImageView iv_search;

    /**
     * 检查当前用户是否被删除
     */
    public boolean getCurrentAccountRemoved() {
        return isCurrentAccountRemoved;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED,
                        false)) {
            // 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            MYApplication.getInstance().logout(null);
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        } else if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        setContentView(R.layout.activity_mian_temp);
        initView();

        if (getIntent().getBooleanExtra("conflict", false)
                && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false)
                && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }
        iv_add = (ImageView) this.findViewById(R.id.iv_add);
        iv_search = (ImageView) this.findViewById(R.id.iv_search);
        iv_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AddPopWindow addPopWindow = new AddPopWindow(MainActivity.this);
                addPopWindow.showPopupWindow(iv_add);
            }

        });
        iv_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }

        });

        TextView tv_online = (TextView) this.findViewById(R.id.tv_online);
        tv_online.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        LasterLoginUserActivity.class));
            }

        });

    }

    private void initView() {
        unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
        unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);

        homefragment = new FragmentCoversation();
        contactlistfragment = new FragmentFriends();
        findfragment = new FragmentFind();
        profilefragment = new FragmentProfile();
        fragments = new Fragment[] { homefragment, contactlistfragment,
                findfragment, profilefragment };
        imagebuttons = new ImageView[4];
        imagebuttons[0] = (ImageView) findViewById(R.id.ib_weixin);
        imagebuttons[1] = (ImageView) findViewById(R.id.ib_contact_list);
        imagebuttons[2] = (ImageView) findViewById(R.id.ib_find);
        imagebuttons[3] = (ImageView) findViewById(R.id.ib_profile);

        imagebuttons[0].setSelected(true);
        textviews = new TextView[4];
        textviews[0] = (TextView) findViewById(R.id.tv_weixin);
        textviews[1] = (TextView) findViewById(R.id.tv_contact_list);
        textviews[2] = (TextView) findViewById(R.id.tv_find);
        textviews[3] = (TextView) findViewById(R.id.tv_profile);
        textviews[0].setTextColor(0xFF45C01A);
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homefragment)
                .add(R.id.fragment_container, contactlistfragment)
                .add(R.id.fragment_container, profilefragment)
                .add(R.id.fragment_container, findfragment)
                .hide(contactlistfragment).hide(profilefragment)
                .hide(findfragment).show(homefragment).commit();
        inviteMessgeDao = new InviteMessgeDao(this);
        userDao = new UserDao(this);

        // 注册一个接收消息的BroadcastReceiver
        msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager
                .getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);

        // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager
                .getInstance().getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(3);
        registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

        // 注册一个透传消息的BroadcastReceiver
        IntentFilter cmdMessageIntentFilter = new IntentFilter(EMChatManager
                .getInstance().getCmdMessageBroadcastAction());
        cmdMessageIntentFilter.setPriority(3);
        registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);
        // 注册一个离线消息的BroadcastReceiver
        // IntentFilter offlineMessageIntentFilter = new
        // IntentFilter(EMChatManager.getInstance()
        // .getOfflineMessageBroadcastAction());
        // registerReceiver(offlineMessageReceiver, offlineMessageIntentFilter);

        // setContactListener监听联系人的变化等
        EMContactManager.getInstance().setContactListener(
                new MyContactListener());
        // 注册一个监听连接状态的listener
        EMChatManager.getInstance().addConnectionListener(
                new MyConnectionListener());
        // 注册群聊相关的listener
        EMGroupManager.getInstance().addGroupChangeListener(
                new MyGroupChangeListener());
        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();

    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
        case R.id.re_weixin:
            index = 0;
            break;
        case R.id.re_contact_list:
            index = 1;
            break;
        case R.id.re_find:
            index = 2;
            break;
        case R.id.re_profile:
            index = 3;
            break;

        }

        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        imagebuttons[index].setSelected(true);
        textviews[currentTabIndex].setTextColor(0xFF999999);
        textviews[index].setTextColor(0xFF45C01A);
        currentTabIndex = index;
    }

    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        MYApplication.getInstance().logout(null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(
                            MainActivity.this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                                conflictBuilder = null;
                                finish();
                                startActivity(new Intent(MainActivity.this,
                                        LoginActivity.class));
                            }
                        });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                EMLog.e(TAG,
                        "---------color conflictBuilder error" + e.getMessage());
            }

        }

    }

    /**
     * 帐号被移除的dialog
     */
    private void showAccountRemovedDialog() {
        isAccountRemovedDialogShow = true;
        MYApplication.getInstance().logout(null);
        String st5 = getResources().getString(R.string.Remove_the_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (accountRemovedBuilder == null)
                    accountRemovedBuilder = new android.app.AlertDialog.Builder(
                            MainActivity.this);
                accountRemovedBuilder.setTitle(st5);
                accountRemovedBuilder.setMessage(R.string.em_user_remove);
                accountRemovedBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                                accountRemovedBuilder = null;
                                finish();
                                startActivity(new Intent(MainActivity.this,
                                        LoginActivity.class));
                            }
                        });
                accountRemovedBuilder.setCancelable(false);
                accountRemovedBuilder.create().show();
                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                EMLog.e(TAG,
                        "---------color userRemovedBuilder error"
                                + e.getMessage());
            }

        }

    }

    /**
     * 新消息广播接收者
     * 
     * 
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

            String from = intent.getStringExtra("from");
            // 消息id
            String msgId = intent.getStringExtra("msgid");
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            // 2014-10-22 修复在某些机器上，在聊天页面对方发消息过来时不立即显示内容的bug
            if (ChatActivity.activityInstance != null) {
                if (message.getChatType() == ChatType.GroupChat) {
                    if (message.getTo().equals(
                            ChatActivity.activityInstance.getToChatUsername()))
                        return;
                } else {
                    if (from.equals(ChatActivity.activityInstance
                            .getToChatUsername()))
                        return;
                }
            }

            // 注销广播接收者，否则在ChatActivity中会收到这个广播
            abortBroadcast();

            notifyNewMessage(message);

            // 刷新bottom bar消息未读数
            updateUnreadLabel();
            if (currentTabIndex == 0) {
                // 当前页面如果为聊天历史页面，刷新此页面
                if (homefragment != null) {
                    homefragment.refresh();
                }
            }

        }
    }

    /**
     * 消息回执BroadcastReceiver
     */
    private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");

            EMConversation conversation = EMChatManager.getInstance()
                    .getConversation(from);
            if (conversation != null) {
                // 把message设为已读
                EMMessage msg = conversation.getMessage(msgid);

                if (msg != null) {

                    // 2014-11-5 修复在某些机器上，在聊天页面对方发送已读回执时不立即显示已读的bug
                    if (ChatActivity.activityInstance != null) {
                        if (msg.getChatType() == ChatType.Chat) {
                            if (from.equals(ChatActivity.activityInstance
                                    .getToChatUsername()))
                                return;
                        }
                    }

                    msg.isAcked = true;
                }
            }

        }
    };

    /**
     * 透传消息BroadcastReceiver
     */
    private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            EMLog.d(TAG, "收到透传消息");
            // 获取cmd message对象
            String msgId = intent.getStringExtra("msgid");
            EMMessage message = intent.getParcelableExtra("message");

            // 获取消息body
            CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
            String action = cmdMsgBody.action;// 获取自定义action
            // 收到加好友的透传消息，取reason存到申请好友的列表数据库，这个reason的设置会在帖子里仔细说明下
            final String username = message.getFrom();

            if (action.equals(Constant.CMD_ADD_FRIEND)) {
                try {
                    String reason = message.getStringAttribute("reason");
                    List<InviteMessage> msgs = inviteMessgeDao
                            .getMessagesList();

                    for (InviteMessage inviteMessage : msgs) {
                        if (inviteMessage.getGroupId() == null
                                && inviteMessage.getFrom().equals(username)) {
                            inviteMessgeDao.deleteMessage(username);
                        }
                    }
                    // 自己封装的javabean
                    InviteMessage msg = new InviteMessage();
                    msg.setFrom(username);
                    msg.setTime(System.currentTimeMillis());
                    msg.setReason(reason);
                    Log.d(TAG, username + "请求加你为好友,reason: " + reason);
                    // 设置相应status
                    msg.setStatus(InviteMesageStatus.BEINVITEED);
                    notifyNewIviteMessage(msg);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            } else if (action.equals(Constant.CMD_ADD_FRIEND)) {
                List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
                for (InviteMessage inviteMessage : msgs) {
                    if (inviteMessage.getFrom().equals(username)) {
                        return;
                    }
                }
                runOnUiThread(new Runnable() {
                    public void run() {

                        addFriendToList(username);
                    }
                });

            }

            // 获取扩展属性 此处省略
            // message.getStringAttribute("");
            // EMLog.d(TAG, String.format("透传消息：action:%s,message:%s",
            // action,message.toString()));
            // String st9 =
            // getResources().getString(R.string.receive_the_passthrough);
            // Toast.makeText(MainActivity.this, st9+action,
            // Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收者
        try {
            unregisterReceiver(msgReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(ackMessageReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(cmdMessageReceiver);
        } catch (Exception e) {
        }

        // try {
        // unregisterReceiver(offlineMessageReceiver);
        // } catch (Exception e) {
        // }

        if (conflictBuilder != null) {
            conflictBuilder.create().dismiss();
            conflictBuilder = null;
        }

    }

    /**
     * 离线消息BroadcastReceiver sdk 登录后，服务器会推送离线消息到client，这个receiver，是通知UI
     * 有哪些人发来了离线消息 UI 可以做相应的操作，比如下载用户信息
     */
    // private BroadcastReceiver offlineMessageReceiver = new
    // BroadcastReceiver() {
    //
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // String[] users = intent.getStringArrayExtra("fromuser");
    // String[] groups = intent.getStringArrayExtra("fromgroup");
    // if (users != null) {
    // for (String user : users) {
    // System.out.println("收到user离线消息：" + user);
    // }
    // }
    // if (groups != null) {
    // for (String group : groups) {
    // System.out.println("收到group离线消息：" + group);
    // }
    // }
    // }
    // };

    /***
     * 好友变化listener
     * 
     */
    private class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {

            refreshFriendsList();
            // 刷新ui
            if (currentTabIndex == 1)
                contactlistfragment.refresh();

        }

        @Override
        public void onContactDeleted(final List<String> usernameList) {
            // 被删除
            Map<String, User> localUsers = MYApplication.getInstance()
                    .getContactList();
            for (String username : usernameList) {
                localUsers.remove(username);
                userDao.deleteContact(username);
                inviteMessgeDao.deleteMessage(username);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    // 如果正在与此用户的聊天页面
                    String st10 = getResources().getString(
                            R.string.have_you_removed);
                    if (ChatActivity.activityInstance != null
                            && usernameList
                                    .contains(ChatActivity.activityInstance
                                            .getToChatUsername())) {
                        Toast.makeText(
                                MainActivity.this,
                                ChatActivity.activityInstance
                                        .getToChatUsername() + st10,
                                Toast.LENGTH_SHORT).show();
                        ChatActivity.activityInstance.finish();
                    }
                    updateUnreadLabel();
                    // 刷新ui
                    if (currentTabIndex == 1)
                        contactlistfragment.refresh();
                    else if (currentTabIndex == 0)
                        homefragment.refresh();
                }
            });

        }

        @Override
        public void onContactInvited(String username, String reason) {
            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null
                        && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            Log.d(TAG, username + "请求加你为好友,reason: " + reason);
            // 设置相应status
            msg.setStatus(InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactAgreed(final String username) {

            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            runOnUiThread(new Runnable() {
                public void run() {

                    addFriendToList(username);
                }
            });

        }

        @Override
        public void onContactRefused(String username) {
            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }

    }

    /**
     * 保存提示新消息
     * 
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // 提示有新消息
        EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

        // 刷新bottom bar消息未读数
        updateUnreadAddressLable();
        // 刷新好友页面ui
        if (currentTabIndex == 1)
            contactlistfragment.refresh();
    }

    /**
     * 保存邀请等msg
     * 
     * @param msg
     */
    private void saveInviteMsg(InviteMessage msg) {
        // 保存msg
        inviteMessgeDao.saveMessage(msg);
        // 未读数加1
        User user = MYApplication.getInstance().getContactList()
                .get(Constant.NEW_FRIENDS_USERNAME);
        if (user.getUnreadMsgCount() == 0)
            user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
    }

    /**
     * set head
     * 
     * @param username
     * @return
     */
    @SuppressLint("DefaultLocale")
    User setUserHead(String username) {
        User user = new User();
        user.setUsername(username);
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance()
                    .get(headerName.substring(0, 1)).get(0).target.substring(0,
                    1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
        return user;
    }

    /**
     * 连接监听listener
     * 
     */
    private class MyConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    homefragment.errorItem.setVisibility(View.GONE);
                }

            });
        }

        @Override
        public void onDisconnected(final int error) {
            final String st1 = getResources().getString(
                    R.string.Less_than_chat_server_connection);
            final String st2 = getResources().getString(
                    R.string.the_current_network);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        showAccountRemovedDialog();
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆dialog
                        showConflictDialog();
                    } else {
                        homefragment.errorItem.setVisibility(View.VISIBLE);
                        if (NetUtils.hasNetwork(MainActivity.this))
                            homefragment.errorText.setText(st1);
                        else
                            homefragment.errorText.setText(st2);

                    }
                }

            });
        }
    }

    /**
     * MyGroupChangeListener
     */
    private class MyGroupChangeListener implements GroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName,
                String inviter, String reason) {

            // 被邀请
            String st3 = getResources().getString(
                    R.string.Invite_you_to_join_a_group_chat);
            User user = MYApplication.getInstance().getContactList()
                    .get(inviter);
            if (user != null) {
                EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
                msg.setChatType(ChatType.GroupChat);
                msg.setFrom(inviter);
                msg.setTo(groupId);
                msg.setMsgId(UUID.randomUUID().toString());
                msg.addBody(new TextMessageBody(user.getNick() + st3));
                msg.setAttribute("useravatar", user.getAvatar());
                msg.setAttribute("usernick", user.getNick());
                // 保存邀请消息
                EMChatManager.getInstance().saveMessage(msg);
                // 提醒新消息
                EMNotifier.getInstance(getApplicationContext())
                        .notifyOnNewMsg();
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // 刷新ui
                    if (currentTabIndex == 0)
                        homefragment.refresh();
                    // if (CommonUtils.getTopActivity(MainActivity.this).equals(
                    // GroupsActivity.class.getName())) {
                    // GroupsActivity.instance.onResume();
                    // }
                }
            });

        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter,
                String reason) {

        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee,
                String reason) {

        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            // 提示用户被T了，demo省略此步骤
            // 刷新ui
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        updateUnreadLabel();
                        if (currentTabIndex == 0)
                            homefragment.refresh();
                        // if (CommonUtils.getTopActivity(MainActivity.this)
                        // .equals(GroupsActivity.class.getName())) {
                        // GroupsActivity.instance.onResume();
                        // }
                    } catch (Exception e) {
                        EMLog.e(TAG, "refresh exception " + e.getMessage());
                    }
                }
            });
        }

        @Override
        public void onGroupDestroy(String groupId, String groupName) {
            // 群被解散
            // 提示用户群被解散,demo省略
            // 刷新ui
            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    if (currentTabIndex == 0)
                        homefragment.refresh();
                    // if (CommonUtils.getTopActivity(MainActivity.this).equals(
                    // GroupsActivity.class.getName())) {
                    // GroupsActivity.instance.onResume();
                    // }
                }
            });

        }

        @Override
        public void onApplicationReceived(String groupId, String groupName,
                String applyer, String reason) {
            // 用户申请加入群聊
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
            msg.setStatus(InviteMesageStatus.BEAPPLYED);
            notifyNewIviteMessage(msg);
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName,
                String accepter) {
            String st4 = getResources().getString(
                    R.string.Agreed_to_your_group_chat_application);
            // 加群申请被同意
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(accepter + st4));
            // 保存同意消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // 刷新ui
                    if (currentTabIndex == 0)
                        homefragment.refresh();
                    // if (CommonUtils.getTopActivity(MainActivity.this).equals(
                    // GroupsActivity.class.getName())) {
                    // GroupsActivity.instance.onResume();
                    // }
                }
            });
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName,
                String decliner, String reason) {
            // 加群申请被拒绝，demo未实现
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConflict || !isCurrentAccountRemoved) {
            // initView();
            updateUnreadLabel();
            updateUnreadAddressLable();
            EMChatManager.getInstance().activityResumed();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getIntent().getBooleanExtra("conflict", false)
                && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false)
                && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            unreadLabel.setText(String.valueOf(count));
            unreadLabel.setVisibility(View.VISIBLE);
        } else {
            unreadLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取未读申请与通知消息
     * 
     * @return
     */
    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        if (MYApplication.getInstance().getContactList()
                .get(Constant.NEW_FRIENDS_USERNAME) != null)
            unreadAddressCountTotal = MYApplication.getInstance()
                    .getContactList().get(Constant.NEW_FRIENDS_USERNAME)
                    .getUnreadMsgCount();
        return unreadAddressCountTotal;
    }

    /**
     * 刷新申请与通知消息数
     */
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = getUnreadAddressCountTotal();
                if (count > 0) {
                    unreadAddressLable.setText(String.valueOf(count));
                    unreadAddressLable.setVisibility(View.VISIBLE);
                } else {
                    unreadAddressLable.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    /**
     * 获取未读消息数
     * 
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        return unreadMsgCountTotal;
    }

    public void refreshFriendsList() {
        // List<String> usernames = new ArrayList<String>();
        // try {
        // usernames = EMContactManager.getInstance().getContactUserNames();
        // } catch (EaseMobException e1) {
        // e1.printStackTrace();
        // }
        // if (usernames != null && usernames.size() > 0) {
        // String totaluser = usernames.get(0);
        // for (int i = 1; i < usernames.size(); i++) {
        // final String split = "66split88";
        // totaluser += split + usernames.get(i);
        // }
        // totaluser = totaluser.replace(Constant.NEW_FRIENDS_USERNAME, "");
        // totaluser = totaluser.replace(Constant.GROUP_USERNAME, "");

        Map<String, String> map = new HashMap<String, String>();

        // map.put("uids", totaluser);
        map.put("hxid", MYApplication.getInstance().getUserName());
        LoadDataFromServer task = new LoadDataFromServer(MainActivity.this,
                Constant.URL_FriendList, map);

        task.getData(new DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    int code = data.getInteger("code");
                    if (code == 1000) {
                        JSONArray josnArray = data.getJSONArray("friends");

                        saveFriends(josnArray);

                    }

                } catch (JSONException e) {
                    Log.e("MainActivity", "update friendsLiST ERROR");
                    e.printStackTrace();
                }
            }
        });
        // }

    }

    private void saveFriends(JSONArray josnArray) {

        Map<String, User> map = new HashMap<String, User>();

        if (josnArray != null) {
            for (int i = 0; i < josnArray.size(); i++) {
                JSONObject json = (JSONObject) josnArray.getJSONObject(i);
                try {
                    String hxid = json.getString("hxid");
                    String fxid = json.getString("fxid");
                    String nick = json.getString("nick");
                    String avatar = json.getString("avatar");
                    String sex = json.getString("sex");
                    String region = json.getString("region");
                    String sign = json.getString("sign");
                    String tel = json.getString("tel");

                    User user = new User();
                    user.setFxid(fxid);
                    user.setUsername(hxid);
                    user.setBeizhu("");
                    user.setNick(nick);
                    user.setRegion(region);
                    user.setSex(sex);
                    user.setTel(tel);
                    user.setSign(sign);
                    user.setAvatar(avatar);
                    setUserHearder(hxid, user);
                    map.put(hxid, user);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);
        newFriends.setBeizhu("");
        newFriends.setFxid("");
        newFriends.setHeader("");
        newFriends.setRegion("");
        newFriends.setSex("");
        newFriends.setTel("");
        newFriends.setSign("");
        newFriends.setAvatar("");
        map.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        groupUser.setNick(strChat);
        groupUser.setBeizhu("");
        groupUser.setFxid("");
        groupUser.setHeader("");
        groupUser.setRegion("");
        groupUser.setSex("");
        groupUser.setTel("");
        groupUser.setSign("");
        groupUser.setAvatar("");
        map.put(Constant.GROUP_USERNAME, groupUser);

        // 存入内存
        MYApplication.getInstance().setContactList(map);
        // 存入db
        UserDao dao = new UserDao(MainActivity.this);
        List<User> users = new ArrayList<User>(map.values());
        dao.saveContactList(users);

    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     * 
     * @param username
     * @param user
     */
    @SuppressLint("DefaultLocale")
    protected void setUserHearder(String username, User user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        headerName = headerName.trim();
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance()
                    .get(headerName.substring(0, 1)).get(0).target.substring(0,
                    1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }

    private void addFriendToList(final String hxid) {
        Map<String, String> map_uf = new HashMap<String, String>();
        map_uf.put("hxid", hxid);
        LoadDataFromServer task = new LoadDataFromServer(null,
                Constant.URL_Get_UserInfo, map_uf);
        task.getData(new DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                try {

                    int code = data.getInteger("code");
                    if (code == 1) {

                        JSONObject json = data.getJSONObject("user");
                        if (json != null && json.size() != 0) {

                        }
                        String nick = json.getString("nick");
                        String avatar = json.getString("avatar");

                        String hxid = json.getString("hxid");
                        String fxid = json.getString("fxid");
                        String region = json.getString("region");
                        String sex = json.getString("sex");
                        String sign = json.getString("sign");
                        String tel = json.getString("tel");
                        User user = new User();

                        user.setUsername(hxid);
                        user.setNick(nick);
                        user.setAvatar(avatar);
                        user.setFxid(fxid);
                        user.setRegion(region);
                        user.setSex(sex);
                        user.setSign(sign);
                        user.setTel(tel);
                        setUserHearder(hxid, user);
                        Map<String, User> userlist = MYApplication
                                .getInstance().getContactList();
                        Map<String, User> map_temp = new HashMap<String, User>();
                        map_temp.put(hxid, user);
                        userlist.putAll(map_temp);
                        // 存入内存
                        MYApplication.getInstance().setContactList(userlist);
                        // 存入db
                        UserDao dao = new UserDao(MainActivity.this);

                        dao.saveContact(user);

                        // 自己封装的javabean
                        InviteMessage msg = new InviteMessage();
                        msg.setFrom(hxid);
                        msg.setTime(System.currentTimeMillis());

                        String reason_temp = nick + "66split88" + avatar
                                + "66split88"
                                + String.valueOf(System.currentTimeMillis())
                                + "66split88" + "已经同意请求";
                        msg.setReason(reason_temp);

                        msg.setStatus(InviteMesageStatus.BEAGREED);
                        User userTemp = MYApplication.getInstance()
                                .getContactList()
                                .get(Constant.NEW_FRIENDS_USERNAME);
                        if (userTemp != null
                                && userTemp.getUnreadMsgCount() == 0) {
                            userTemp.setUnreadMsgCount(userTemp
                                    .getUnreadMsgCount() + 1);
                        }
                        notifyNewIviteMessage(msg);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }

        });

    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                moveTaskToBack(false);
                finish();

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
