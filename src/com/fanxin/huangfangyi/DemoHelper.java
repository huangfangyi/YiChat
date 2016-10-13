package com.fanxin.huangfangyi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.easemob.redpacketui.RedPacketConstant;
import com.easemob.redpacketui.utils.RedPacketUtil;
import com.fanxin.huangfangyi.db.DemoDBManager;
import com.fanxin.huangfangyi.db.InviteMessgeDao;
import com.fanxin.huangfangyi.db.UserDao;
import com.fanxin.huangfangyi.domain.EmojiconExampleGroupData;
import com.fanxin.huangfangyi.domain.InviteMessage;
import com.fanxin.huangfangyi.domain.RobotUser;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.activity.ChatActivity;
import com.fanxin.huangfangyi.main.fragment.MainActivity;
import com.fanxin.huangfangyi.main.service.GroupService;
import com.fanxin.huangfangyi.main.utils.JSONUtil;
import com.fanxin.huangfangyi.receiver.CallReceiver;
import com.fanxin.huangfangyi.ui.VideoCallActivity;
import com.fanxin.huangfangyi.ui.VoiceCallActivity;
import com.fanxin.huangfangyi.utils.PreferenceManager;
import com.fanxin.easeui.EaseConstant;
import com.fanxin.easeui.controller.EaseUI;
import com.fanxin.easeui.controller.EaseUI.EaseEmojiconInfoProvider;
import com.fanxin.easeui.controller.EaseUI.EaseSettingsProvider;
import com.fanxin.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.fanxin.easeui.domain.EaseEmojicon;
import com.fanxin.easeui.domain.EaseEmojiconGroupEntity;
import com.fanxin.easeui.domain.EaseUser;
import com.fanxin.easeui.model.EaseAtMessageHelper;
import com.fanxin.easeui.model.EaseNotifier;
import com.fanxin.easeui.model.EaseNotifier.EaseNotificationInfoProvider;
import com.fanxin.easeui.utils.EaseCommonUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMMessage.Type;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DemoHelper {
    /**
     * data sync listener
     */
    static public interface DataSyncListener {
        /**
         * sync complete
         *
         * @param success true：data sync successful，false: failed to sync data
         */
        public void onSyncComplete(boolean success);
    }

    protected static final String TAG = "DemoHelper";

    private EaseUI easeUI;

    /**
     * EMEventListener
     */
    protected EMMessageListener messageListener = null;

    private Map<String, EaseUser> contactList;

    private Map<String, RobotUser> robotList;


    private static DemoHelper instance = null;

    private DemoModel demoModel = null;

    /**
     * sync groups status listener
     */
    private List<DataSyncListener> syncGroupsListeners;


    private boolean isSyncingGroupsWithServer = false;
    private boolean isGroupsSyncedWithServer = false;

    public boolean isVoiceCalling;
    public boolean isVideoCalling;

    private String username;

    private Context appContext;

    private CallReceiver callReceiver;

    private EMConnectionListener connectionListener;

    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    private LocalBroadcastManager broadcastManager;

    private boolean isGroupAndContactListenerRegisted;

    private DemoHelper() {
    }

    public synchronized static DemoHelper getInstance() {
        if (instance == null) {
            instance = new DemoHelper();
        }
        return instance;
    }

    /**
     * init helper
     *
     * @param context application context
     */
    public void init(Context context) {
        demoModel = new DemoModel(context);
        EMOptions options = initChatOptions();
        //use default options if options is null
        if (EaseUI.getInstance().init(context, options)) {
            appContext = context;

            //debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(true);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //to set user's profile and avatar
            setEaseUIProviders();
            //initialize preference manager
            PreferenceManager.init(context);

            EMClient.getInstance().callManager().getVideoCallHelper().setAdaptiveVideoFlag(getModel().isAdaptiveVideoEncode());

            setGlobalListeners();
            broadcastManager = LocalBroadcastManager.getInstance(appContext);
            initDbDao();
        }
    }


    private EMOptions initChatOptions() {
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);

        //you need apply & set your own id if you want to use google cloud messaging.
        options.setGCMNumber("324169311137");
        //you need apply & set your own id if you want to use Mi push notification
        options.setMipushConfig("2882303761517426801", "5381742660801");
        //you need apply & set your own id if you want to use Huawei push notification
//        options.setHuaweiPushAppId("10492024");

        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
        options.setDeleteMessagesAsExitGroup(getModel().isDeleteMessagesAsExitGroup());
        options.setAutoAcceptGroupInvitation(getModel().isAutoAcceptGroupInvitation());

        return options;
    }

    protected void setEaseUIProviders() {
        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });

        //set options 
        easeUI.setSettingsProvider(new EaseSettingsProvider() {

            @Override
            public boolean isSpeakerOpened() {
                return demoModel.getSettingMsgSpeaker();
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return demoModel.getSettingMsgVibrate();
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return demoModel.getSettingMsgSound();
            }

            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                if (message == null) {
                    return demoModel.getSettingMsgNotification();
                }
                if (!demoModel.getSettingMsgNotification()) {
                    return false;
                } else {
                    String chatUsename = null;
                    List<String> notNotifyIds = null;
                    // get user or group id which was blocked to show message notifications
                    if (message.getChatType() == ChatType.Chat) {
                        chatUsename = message.getFrom();
                        notNotifyIds = demoModel.getDisabledIds();
                    } else {
                        chatUsename = message.getTo();
                        notNotifyIds = demoModel.getDisabledGroups();
                    }

                    if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        //set emoji icon provider
        easeUI.setEmojiconInfoProvider(new EaseEmojiconInfoProvider() {

            @Override
            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                for (EaseEmojicon emojicon : data.getEmojiconList()) {
                    if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                return null;
            }
        });

        //set notification options, will use default if you don't set it
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //you can update title here
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //you can update icon here
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // be used on notification bar, different text according the message type.
                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = getUserInfo(message.getFrom());
                if (user != null) {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                        return String.format(appContext.getString(R.string.at_your_in_group), user.getNick());
                    }
                    return user.getNick() + ": " + ticker;
                } else {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                        return String.format(appContext.getString(R.string.at_your_in_group), message.getFrom());
                    }
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                // here you can customize the text.
                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
                return null;
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                // you can set what activity you want display when user click the notification
                Intent intent = new Intent(appContext, ChatActivity.class);
                // open calling activity if there is call
                if (isVideoCalling) {
                    intent = new Intent(appContext, VideoCallActivity.class);
                } else if (isVoiceCalling) {
                    intent = new Intent(appContext, VoiceCallActivity.class);
                } else {
                    ChatType chatType = message.getChatType();
                    if (chatType == ChatType.Chat) { // single chat message
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                    } else { // group chat message
                        // message.getTo() is the group id
                        intent.putExtra("userId", message.getTo());
                        if (chatType == ChatType.GroupChat) {
                            intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                        } else {
                            intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
                        }

                    }
                }
                return intent;
            }
        });
    }

    /**
     * set global listener
     */
    protected void setGlobalListeners() {
        syncGroupsListeners = new ArrayList<DataSyncListener>();
        isGroupsSyncedWithServer = demoModel.isGroupsSynced();
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
                    onCurrentAccountRemoved();
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    onConnectionConflict();
                }
            }

            @Override
            public void onConnected() {
                // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
                if (isGroupsSyncedWithServer) {
                    EMLog.d(TAG, "group and contact already synced with servre");
                } else {
                    if (!isGroupsSyncedWithServer) {
                        asyncFetchGroupsFromServer(null);
                    }

                }
            }
        };

        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }

        //register incoming call receiver
        appContext.registerReceiver(callReceiver, callFilter);
        //register connection listener
        EMClient.getInstance().addConnectionListener(connectionListener);
        //register group and contact event listener
        registerGroupAndContactListener();
        //register message event listener
        registerMessageListener();

    }

    private void initDbDao() {
        inviteMessgeDao = new InviteMessgeDao(appContext);
        userDao = new UserDao(appContext);
    }

    /**
     * register group and contact listener, you need register when login
     */
    public void registerGroupAndContactListener() {
        if (!isGroupAndContactListenerRegisted) {
            EMClient.getInstance().groupManager().addGroupChangeListener(new MyGroupChangeListener());
            EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
            isGroupAndContactListenerRegisted = true;
        }

    }

    /**
     * group change listener
     */
    class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            appContext.startService(new Intent(appContext, GroupService.class).putExtra("groupId", groupId).putExtra("groupName",groupName));
            //            new InviteMessgeDao(appContext).deleteMessage(groupId);
//
//            // user invite you to join group
//            InviteMessage msg = new InviteMessage();
//            msg.setFrom(groupId);
//            msg.setTime(System.currentTimeMillis());
//            msg.setGroupId(groupId);
//            msg.setGroupName(groupName);
//            msg.setReason(reason);
//            msg.setGroupInviter(inviter);
//            Log.d(TAG, "receive invitation to join the group：" + groupName);
//            msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION);
//            notifyNewInviteMessage(msg);
//            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onInvitationAccpted(String groupId, String invitee, String reason) {
 //            new InviteMessgeDao(appContext).deleteMessage(groupId);
//
//            //user accept your invitation
//            boolean hasGroup = false;
//            EMGroup _group = null;
//            for (EMGroup group : EMClient.getInstance().groupManager().getAllGroups()) {
//                if (group.getGroupId().equals(groupId)) {
//                    hasGroup = true;
//                    _group = group;
//                    break;
//                }
//            }
//            if (!hasGroup)
//                return;
//
//            InviteMessage msg = new InviteMessage();
//            msg.setFrom(groupId);
//            msg.setTime(System.currentTimeMillis());
//            msg.setGroupId(groupId);
//            msg.setGroupName(_group == null ? groupId : _group.getGroupName());
//            msg.setReason(reason);
//            msg.setGroupInviter(invitee);
//            Log.d(TAG, invitee + "Accept to join the group：" + _group == null ? groupId : _group.getGroupName());
//            msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION_ACCEPTED);
//            notifyNewInviteMessage(msg);
//            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
 //
//            //user declined your invitation
//            boolean hasGroup = false;
//            EMGroup group = null;
//            for (EMGroup _group : EMClient.getInstance().groupManager().getAllGroups()) {
//                if (_group.getGroupId().equals(groupId)) {
//                    group = _group;
//                    hasGroup = true;
//                    break;
//                }
//            }
//            if (!hasGroup)
//                return;
//
//            InviteMessage msg = new InviteMessage();
//            msg.setFrom(groupId);
//            msg.setTime(System.currentTimeMillis());
//            msg.setGroupId(groupId);
//            msg.setGroupName(group == null ? groupId : group.getGroupName());
//            msg.setReason(reason);
//            msg.setGroupInviter(invitee);
//            Log.d(TAG, invitee + "Declined to join the group：" + group == null ? groupId : group.getGroupName());
//            msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION_DECLINED);
//            notifyNewInviteMessage(msg);
//            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            appContext.startService(new Intent(appContext, GroupService.class).putExtra("groupId", groupId).putExtra("groupName",groupName));            //user is removed from group
//            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onGroupDestroy(String groupId, String groupName) {
            appContext.startService(new Intent(appContext, GroupService.class).putExtra("groupId", groupId).putExtra("groupName",groupName));//            // group is dismissed,
//            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
            appContext.startService(new Intent(appContext, GroupService.class).putExtra("groupId", groupId).putExtra("groupName",groupName));//
//            // user apply to join group
//            InviteMessage msg = new InviteMessage();
//            msg.setFrom(applyer);
//            msg.setTime(System.currentTimeMillis());
//            msg.setGroupId(groupId);
//            msg.setGroupName(groupName);
//            msg.setReason(reason);
//            Log.d(TAG, applyer + " Apply to join group：" + groupName);
//            msg.setStatus(InviteMessage.InviteMesageStatus.BEAPPLYED);
//            notifyNewInviteMessage(msg);
//            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {
            appContext.startService(new Intent(appContext, GroupService.class).putExtra("groupId", groupId).putExtra("groupName",groupName));

//            String st4 = appContext.getString(R.string.Agreed_to_your_group_chat_application);
//            // your application was accepted
//            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
//            msg.setChatType(ChatType.GroupChat);
//            msg.setFrom(accepter);
//            msg.setTo(groupId);
//            msg.setMsgId(UUID.randomUUID().toString());
//            msg.addBody(new EMTextMessageBody(accepter + " " + st4));
//            msg.setStatus(Status.SUCCESS);
//            // save accept message
//            EMClient.getInstance().chatManager().saveMessage(msg);
//            // notify the accept message
//            getNotifier().vibrateAndPlayTone(msg);
//
//            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            appContext.startService(new Intent(appContext, GroupService.class).putExtra("groupId", groupId).putExtra("groupName",groupName));            // your application was declined, we do nothing here in demo
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
 //            String st3 = appContext.getString(R.string.Invite_you_to_join_a_group_chat);
//            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
//            msg.setChatType(ChatType.GroupChat);
//            msg.setFrom(inviter);
//            msg.setTo(groupId);
//            msg.setMsgId(UUID.randomUUID().toString());
//            msg.addBody(new EMTextMessageBody(inviter + " " + st3));
//            msg.setStatus(EMMessage.Status.SUCCESS);
//            // save invitation as messages
//            EMClient.getInstance().chatManager().saveMessage(msg);
//            // notify invitation message
//            getNotifier().vibrateAndPlayTone(msg);
//            EMLog.d(TAG, "onAutoAcceptInvitationFromGroup groupId:" + groupId);
//            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }
    }

    /***
     * 好友变化listener
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(String username) {
            // save contact
            Map<String, EaseUser> localUsers = getContactList();
            Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
            EaseUser user = new EaseUser(username);

            if (!localUsers.containsKey(username)) {
                userDao.saveContact(user);
            }
            toAddUsers.put(username, user);
            localUsers.putAll(toAddUsers);

            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactDeleted(String username) {
            Map<String, EaseUser> localUsers = DemoHelper.getInstance().getContactList();
            localUsers.remove(username);
            userDao.deleteContact(username);
            inviteMessgeDao.deleteMessage(username);

            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactInvited(String username, String reason) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // save invitation as message
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            Log.d(TAG, username + "apply to be your friend,reason: " + reason);
            // set invitation status
            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactAgreed(String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // save invitation as message
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "accept your request");
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactRefused(String username) {
            // your request was refused
            Log.d(username, username + " refused to your request");
        }
    }

    /**
     * save and notify invitation message
     *
     * @param msg
     */
    private void notifyNewInviteMessage(InviteMessage msg) {
        if (inviteMessgeDao == null) {
            inviteMessgeDao = new InviteMessgeDao(appContext);
        }
        inviteMessgeDao.saveMessage(msg);
        //increase the unread message count
        inviteMessgeDao.saveUnreadMessageCount(1);
        // notify there is new message
        getNotifier().vibrateAndPlayTone(null);
    }

    /**
     * user has logged into another device
     */
    protected void onConnectionConflict() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_CONFLICT, true);
        appContext.startActivity(intent);
    }

    /**
     * account is removed
     */
    protected void onCurrentAccountRemoved() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }

    private EaseUser getUserInfo(String username) {
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        EaseUser user = null;
        if (username.equals(EMClient.getInstance().getCurrentUser()))
            return JSONUtil.Json2User(DemoApplication.getInstance().getUserJson());
        user = getContactList().get(username);
        if (user == null && getRobotList() != null) {
            user = getRobotList().get(username);
        }

        // if user is not in your contacts, set inital letter for him/her
        if (user == null) {
            user = new EaseUser(username);
            EaseCommonUtils.setUserInitialLetter(user);
        }
        return user;
    }

    /**
     * Global listener
     * If this event already handled by an activity, you don't need handle it again
     * activityList.size() <= 0 means all activities already in background or not in Activity Stack
     */
    protected void registerMessageListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                    // in background, do not refresh UI, notify it in notification bar
                    if (!easeUI.hasForegroundActivies() && message.getChatType() != ChatType.ChatRoom) {
                        if (message.getChatType() == ChatType.Chat && !DemoHelper.getInstance().getContactList().containsKey(message.getUserName())) {
                            return;
                        }
                        getNotifier().onNewMsg(message);
                    }


                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "receive command message");
                    //get message body
                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action();//get your predefined action
                    if (!easeUI.hasForegroundActivies()) {
                        if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
                            RedPacketUtil.receiveRedPacketAckMessage(message);
                            broadcastManager.sendBroadcast(new Intent(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION));
                            Log.d("ack_redpacket-->", "11111");
                        }
                    }
                    if (action.equals(FXConstant.CMD_ADD_FRIEND)) {


                        try {
                            String userInfo = message.getStringAttribute(FXConstant.KEY_USER_INFO);
                            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

                            for (InviteMessage inviteMessage : msgs) {
                                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(message.getFrom())) {
                                    inviteMessgeDao.deleteMessage(message.getFrom());
                                }
                            }
                            // save invitation as message
                            InviteMessage msg = new InviteMessage();
                            msg.setFrom(message.getFrom());
                            msg.setTime(System.currentTimeMillis());
                            msg.setReason(userInfo);
                            Log.d(TAG, message.getFrom() + "apply to be your friend,reason: " + userInfo);
                            // set invitation status
                            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
                            notifyNewInviteMessage(msg);
                            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));

                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }

                    } else if (action.equals(FXConstant.CMD_AGREE_FRIEND)) {
                        try {
                            String userInfo = message.getStringAttribute(FXConstant.KEY_USER_INFO);
                            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
                            for (InviteMessage inviteMessage : msgs) {
                                if (inviteMessage.getFrom().equals(message.getFrom())) {
                                    inviteMessgeDao.deleteMessage(message.getFrom());

                                }
                            }
                            // save invitation as message
                            InviteMessage msg = new InviteMessage();
                            msg.setFrom(message.getFrom());
                            msg.setReason(userInfo);
                            msg.setTime(System.currentTimeMillis());
                            Log.d(TAG, message.getFrom() + "accept your request");
                            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
                            notifyNewInviteMessage(msg);
                            EaseUser user = JSONUtil.Json2User(JSONObject.parseObject(userInfo));
                            getContactList().put(user.getUsername(), user);
                            userDao.saveContact(user);
                            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    } else if (action.equals(FXConstant.FX_REVOKE_MESSAGE)) {
                        if (message.getChatType() == ChatType.GroupChat) {


                            String groupId = message.getTo();

                            try {
                                String msgId = message.getStringAttribute(FXConstant.REVOKE_MESSAGE_ID);
                                EMConversation emConversation = EMClient.getInstance().chatManager().getConversation(groupId);
                                emConversation.removeMessage(msgId);
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }

                            try {
                                String content = appContext.getString(R.string.revoke_content_ed);
                                try {
                                    String revokeNick = message.getStringAttribute(FXConstant.KEY_USER_INFO);
                                    JSONObject jsonObject = JSONObject.parseObject(revokeNick);
                                    String nick = jsonObject.getString("nick");
                                    if (!DemoHelper.getInstance().getCurrentUsernName().equals(message.getFrom())) {

                                        content = String.format(appContext.getString(R.string.revoke_content_someone), nick);
                                    }
                                } catch (HyphenateException e) {
                                    e.printStackTrace();
                                }

                                Log.d("content--->", content);

                                EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                                msg.setChatType(ChatType.GroupChat);
                                msg.setFrom(DemoHelper.getInstance().getCurrentUsernName());
                                msg.setTo(groupId);
                                msg.setMsgId(UUID.randomUUID().toString());
                                try {
                                    msg.addBody(new EMTextMessageBody( content));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                msg.setStatus(EMMessage.Status.SUCCESS);
                                msg.setAttribute(FXConstant.IS_MESSAGE_REVOKE, true);
                                msg.setAttribute(FXConstant.IS_MESSAGE_REVOKE_SEND, false);
                                // save invitation as messages
                                EMClient.getInstance().chatManager().saveMessage(msg);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        } else {
                            String userName = message.getFrom();
                            try {
                                String msgId = message.getStringAttribute(FXConstant.REVOKE_MESSAGE_ID);
                                EMConversation emConversation = EMClient.getInstance().chatManager().getConversation(userName);
                                emConversation.removeMessage(msgId);
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }

                            try {
                                String content = appContext.getString(R.string.revoke_content_ed);
                                Log.d("content--->", content);
                                EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                                msg.setChatType(EMMessage.ChatType.Chat);
                                msg.setFrom(userName);
                                msg.setTo(DemoHelper.getInstance().getCurrentUsernName());
                                msg.setMsgId(UUID.randomUUID().toString());
                                try {
                                    msg.addBody(new EMTextMessageBody( content));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                msg.setStatus(EMMessage.Status.SUCCESS);
                                msg.setAttribute(FXConstant.IS_MESSAGE_REVOKE, true);
                                msg.setAttribute(FXConstant.IS_MESSAGE_REVOKE_SEND, false);
                                // save invitation as messages
                                EMClient.getInstance().chatManager().saveMessage(msg);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        }
                        appContext.sendBroadcast(new Intent().setAction(EaseConstant.ACTION_DELETE_MSG));

                    } else if (action.equals(FXConstant.CMD_DELETE_FRIEND)) {

                        Map<String, EaseUser> localUsers = DemoHelper.getInstance().getContactList();
                        localUsers.remove(message.getFrom());
                        userDao.deleteContact(message.getFrom());
                        inviteMessgeDao.deleteMessage(message.getFrom());
                        broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
                    }else if(action.equals(FXConstant.CMD_REFUSE_FRIEND)){
                        try {
                            String userInfo = message.getStringAttribute(FXConstant.KEY_USER_INFO);
                            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
                            for (InviteMessage inviteMessage : msgs) {
                                if (inviteMessage.getFrom().equals(message.getFrom())) {
                                    inviteMessgeDao.deleteMessage(message.getFrom());

                                }
                            }
                            // save invitation as message
                            InviteMessage msg = new InviteMessage();
                            msg.setFrom(message.getFrom());
                            msg.setReason(userInfo);
                            msg.setTime(System.currentTimeMillis());
                            Log.d(TAG, message.getFrom() + "refused your request");
                            msg.setStatus(InviteMessage.InviteMesageStatus.BEREFUSED);
                            notifyNewInviteMessage(msg);
                            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }

                    }

                    //maybe you need get extension of your message
                    //message.getStringAttribute("");
                    EMLog.d(TAG, String.format("Command：action:%s,message:%s", action, message.toString()));
                }
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {

            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * logout
     *
     * @param unbindDeviceToken whether you need unbind your device token
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        endCall();
        Log.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    /**
     * get instance of EaseNotifier
     *
     * @return
     */
    public EaseNotifier getNotifier() {
        return easeUI.getNotifier();
    }

    public DemoModel getModel() {
        return (DemoModel) demoModel;
    }

    /**
     * update contact list
     *
     * @param
     */
    public void setContactList(Map<String, EaseUser> aContactList) {
        if (aContactList == null) {
            if (contactList != null) {
                contactList.clear();
            }
            return;
        }

        contactList = aContactList;
    }

    /**
     * save single contact
     */
    public void saveContact(EaseUser user) {
        contactList.put(user.getUsername(), user);
        demoModel.saveContact(user);
    }

    /**
     * get contact list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        if (isLoggedIn() && contactList == null) {
            contactList = demoModel.getContactList();
        }

        // return a empty non-null object to avoid app crash
        if (contactList == null) {
            return new Hashtable<String, EaseUser>();
        }

        return contactList;
    }

    /**
     * set current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        this.username = username;
        demoModel.setCurrentUserName(username);
    }

    /**
     * get current user's id
     */
    public String getCurrentUsernName() {
        if (username == null) {
            username = demoModel.getCurrentUsernName();
        }
        return username;
    }

    public void setRobotList(Map<String, RobotUser> robotList) {
        this.robotList = robotList;
    }

    public Map<String, RobotUser> getRobotList() {
        if (isLoggedIn() && robotList == null) {
            robotList = demoModel.getRobotList();
        }
        return robotList;
    }

    /**
     * update user list to cache and database
     *
     * @param
     */
    public void updateContactList(List<EaseUser> contactInfoList) {
        for (EaseUser u : contactInfoList) {
            contactList.put(u.getUsername(), u);
        }
        ArrayList<EaseUser> mList = new ArrayList<EaseUser>();
        mList.addAll(contactList.values());
        demoModel.saveContactList(mList);
    }


    void endCall() {
        try {
            EMClient.getInstance().callManager().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSyncGroupListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncGroupsListeners.contains(listener)) {
            syncGroupsListeners.add(listener);
        }
    }

    public void removeSyncGroupListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncGroupsListeners.contains(listener)) {
            syncGroupsListeners.remove(listener);
        }
    }

    /**
     * Get group list from server
     * This method will save the sync state
     *
     * @throws HyphenateException
     */
    public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback) {
        if (isSyncingGroupsWithServer) {
            return;
        }

        isSyncingGroupsWithServer = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                    // in case that logout already before server returns, we should return immediately
                    if (!isLoggedIn()) {
                        isGroupsSyncedWithServer = false;
                        isSyncingGroupsWithServer = false;
                        noitifyGroupSyncListeners(false);
                        return;
                    }

                    demoModel.setGroupsSynced(true);

                    isGroupsSyncedWithServer = true;
                    isSyncingGroupsWithServer = false;

                    //notify sync group list success
                    noitifyGroupSyncListeners(true);

                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (HyphenateException e) {
                    demoModel.setGroupsSynced(false);
                    isGroupsSyncedWithServer = false;
                    isSyncingGroupsWithServer = false;
                    noitifyGroupSyncListeners(false);
                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }

    public void noitifyGroupSyncListeners(boolean success) {
        for (DataSyncListener listener : syncGroupsListeners) {
            listener.onSyncComplete(success);
        }
    }


    public boolean isSyncingGroupsWithServer() {
        return isSyncingGroupsWithServer;
    }

    public boolean isGroupsSyncedWithServer() {
        return isGroupsSyncedWithServer;
    }


    synchronized void reset() {
        isSyncingGroupsWithServer = false;
        demoModel.setGroupsSynced(false);
        isGroupsSyncedWithServer = false;

        isGroupAndContactListenerRegisted = false;

        setContactList(null);
        setRobotList(null);
        DemoApplication.getInstance().setUserJson(null);
        DemoDBManager.getInstance().closeDB();
    }

    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }


}