package com.htmessage.yichat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.SDKConstant;
import com.htmessage.sdk.client.HTAction;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.client.HTOptions;
import com.htmessage.sdk.listener.HTConnectionListener;
import com.htmessage.sdk.model.CallMessage;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.update.Constant;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.data.SettingsManager;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.uitls.MsgUtils;
import com.htmessage.yichat.acitivity.chat.ChatActivity;
import com.htmessage.yichat.acitivity.chat.VideoIncomingActivity;
import com.htmessage.yichat.manager.NotifierManager;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.HTMessageUtils;
import com.htmessage.yichat.utils.LoggerUtils;
import com.htmessage.sdk.manager.MmvkManger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

 import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by huangfangyi on 2017/3/3.
 * qq 84543217
 */

public class HTClientHelper {
    private static Context applicationContext;

    private static HTClientHelper htClientHelper;

    public static final String baseOssUrl = "http://" + SDKConstant.bucket + "." + SDKConstant.endpoint + "/";

    public static void init(Context context) {
        htClientHelper = new HTClientHelper(context);
    }

    private static long timeStamp = 0;
    private boolean isDelay = false;
    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1002:
                    checkToken();

                    break;
                case 1003:
                    HTApp.getInstance().logoutApp(1);

                    break;
                case 1005:

                    getGroupOfflineMsg();
                    break;
                case 1001:
                    if (msg.obj != null) {
                        long timeTemP = (long) msg.obj;
                        timeStamp = Math.max(timeStamp, timeTemP);
                    }
                    if (!isDelay) {
                        isDelay = true;
                        Observable.create(new ObservableOnSubscribe<Long>() { // 第一步：初始化Observable
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<Long> e) throws Exception {
                                e.onNext(Long.parseLong("100"));
                            }
                        }).delay(30000, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Long>() { // 第三步：订阅
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {
                                    }

                                    @Override
                                    public void onNext(@NonNull Long integer) {
                                        if (timeStamp == 0) {
                                            return;
                                        }
                                        JSONObject data = new JSONObject();
                                        data.put("timestamp", timeStamp);
                                        ApiUtis.getInstance().postJSON(data, Constant.URL_UPDATE_TIMESTAMP, new ApiUtis.HttpCallBack() {
                                            @Override
                                            public void onResponse(JSONObject jsonObject) {

                                            }

                                            @Override
                                            public void onFailure(int errorCode) {

                                            }
                                        });

                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        isDelay = false;
                                    }

                                    @Override
                                    public void onComplete() {
                                    }
                                });
                    }
                    break;

                case 1004:
                    String groupId= (String) msg.obj;
                    GroupInfoManager.getInstance().getGroupAllMembersFromServer(groupId,null);

                    break;
            }

        }
    };


    public HTClientHelper(Context context) {
        this.applicationContext = context;
        HTOptions htOptions = new HTOptions();
        htOptions.setDualProcess(false);
        htOptions.setDebug(true);
        HTClient.init(applicationContext, htOptions);
        HTClient.getInstance().setMessageLisenter(messageLisenter);
        HTClient.getInstance().addConnectionListener(htConnectionListener);
        HTClient.getInstance().setGroupLisenter(groupLisenter);
        BadgerCount = (int) MmvkManger.getIntance().getAsLong("BadgerCount");
    }

    /**
     * 获取VersionCode
     *
     * @return 当前应用的VersionCode
     */
    public int getVersionCode() {
        try {
            PackageManager manager = applicationContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(applicationContext.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void getGroupOfflineMsg() {

        JSONObject data = new JSONObject();
        data.put("currentVersion", getVersionCode());
        data.put("type", "0");
        ApiUtis.getInstance().postJSON(data, Constant.URL_CONFIG, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if(data!=null){
                        int createGroupAuthStatus=data.getIntValue("createGroupAuthStatus");
                        if(createGroupAuthStatus==1){
                            SettingsManager.getInstance().setCreateGroupAuthStatus(true);
                        }else {

                            SettingsManager.getInstance().setCreateGroupAuthStatus(false);
                        }

                        JSONArray unreadCountList = data.getJSONArray("unreadCountList");
                        for (int i = 0; i < unreadCountList.size(); i++) {
                            JSONObject item = unreadCountList.getJSONObject(i);
                            int unreadCount = item.getInteger("unreadCount");
                            if (unreadCount > 0) {
                                JSONObject lastmessage = item.getJSONObject("lastmessage");
                                String msgString = lastmessage.getString("content");
                                long timeStamp = lastmessage.getLong("time");
                                HTMessage htMessage = MsgUtils.getInstance().stringToMessage(msgString, timeStamp);
                                if (htMessage != null) {
                                    HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                                    Intent intent = new Intent(HTAction.ACTION_MESSAGE);
                                    intent.putExtra("data", htMessage);
                                    HTConversation htConversation = HTClient.getInstance().conversationManager().getConversation(htMessage.getUsername());
                                    int currentCount = htConversation.getUnReadCount();
                                    htConversation.setUnReadCount(currentCount + unreadCount - 1);
                                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
                                }
                            }

                        }

                        JSONObject versionJSON = data.getJSONObject("androidVersion");
                        if (versionJSON != null) {
                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.VERSION_UPDATE).putExtra("versionJSON",versionJSON));
                        }
                        JSONObject shareJSON = data.getJSONObject("share");
                        if (shareJSON != null) {
                            SettingsManager.getInstance().setShareJSON(shareJSON);
                         }
                    }

                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });


    }


    public static HTClientHelper getInstance() {

        if (htClientHelper == null) {
            throw new RuntimeException("please init first!");
        }
        return htClientHelper;
    }


    private HTClient.GroupLisenter groupLisenter = new HTClient.GroupLisenter() {
        @SuppressWarnings("deprecation")
        @Override
        public void onGroupListLoaded() {

//            //绑定极光推送tags
//            List<HTGroup> htGroups=  HTClient.getInstance().groupManager().getAllGroups();
//            Set<String> groupIds=new HashSet<>();
//             for(HTGroup htGroup:htGroups){
//                 groupIds.add(htGroup.getGroupId());
//            }
//           // JPushInterface.setTags(applicationContext,0,groupIds);
//            JPushInterface.setAliasAndTags(applicationContext, HTApp.getInstance().getUsername(), groupIds, new TagAliasCallback() {
//                @Override
//                public void gotResult(int i, String s, Set<String> set) {
//                 //    Log.d("i----",i+"----"+s+"---"+set.toArray())
//                }
//            });

        }

        @Override
        public void onGroupDestroyed(String groupId) {
          //群被删除
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_DELETE_GROUP).putExtra("userId", groupId));

        }

        @Override
        public void onGroupMemberLeaved(String groupId, String userId, String userNick) {
            Message message = handler.obtainMessage();
            message.what = 1004;
            message.obj=groupId;
            message.sendToTarget();
        }
    };

    private HTConnectionListener htConnectionListener = new HTConnectionListener() {
        @Override
        public void onConnected(int type) {
            //type=0是登录成功，type 1是重连成功
            GroupInfoManager.getInstance().initClear();
            notifyConnection(true);
            LoggerUtils.e("htConnectionListener----onConnected---type"+type);
            if(type==0){
                Message message = handler.obtainMessage();
                message.what = 1005;
                message.sendToTarget();
            }
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.XMPP_LOGIN_OR_RELOGIN));
        }

        @Override
        public void onDisconnected() {
            notifyConnection(false);
            LoggerUtils.e("htConnectionListener----onDisconnected");
        }

        @Override
        public void onConflict() {
            notifyConflict(applicationContext);
            LoggerUtils.e("htConnectionListener----onConflict");

        }
    };
    public static int BadgerCount = 0;
    private HTClient.MessageLisenter messageLisenter = new HTClient.MessageLisenter() {

        @Override
        public void onHTMessage(final HTMessage htMessage) {

            BadgerCount++;
            ShortcutBadger.applyCount(applicationContext, BadgerCount); //for 1.1.4+
            MmvkManger.getIntance().putLong("BadgerCount", BadgerCount);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LoggerUtils.e("htMessage----" + htMessage.toXmppMessageBody());

                    if (htMessage.getChatType() == ChatType.groupChat) {
                        String username = htMessage.getUsername();
                        int action = htMessage.getIntAttribute("action", 0);
                        if (action < 2006 && action > 1999) {
                            //todo  监听群成员，做成员处理
                            String groupName = htMessage.getStringAttribute("groupName");
                            String groupDescription = htMessage.getStringAttribute("groupDescription");
                            String groupAvatar = htMessage.getStringAttribute("groupAvatar");
                            HTGroup group = HTClient.getInstance().groupManager().getGroup(username);
                            if (group != null) {
                                if (!TextUtils.isEmpty(groupName)) {
                                    group.setGroupName(groupName);
                                }
                                if (!TextUtils.isEmpty(groupAvatar)) {
                                    group.setImgUrl(groupAvatar);
                                }
                                if (!TextUtils.isEmpty(groupDescription)) {
                                    group.setGroupDesc(groupDescription);
                                }
                                HTClient.getInstance().groupManager().saveGroup(group);
                            }
//                            Message message = handler.obtainMessage();
//                             message.what = 1004;
//                             message.obj=username;
//                            message.sendToTarget();
                        }else if(action==10004){
                            Log.d("10004---",htMessage.toXmppMessageBody());
                            //领取红包消息
                            if(!UserManager.get().getMyUserId().equals(htMessage.getStringAttribute("msgFrom"))){
                                return;

                            }

                        }
                        Message message = handler.obtainMessage();
                        message.obj = htMessage.getTime();
                        message.what = 1001;
                        message.sendToTarget();

                        //检查是否有@自己的消息，要给对应的会话做标记
                        String atUser=htMessage.getStringAttribute("atUser");
                        if(!TextUtils.isEmpty(atUser)&&(atUser.contains(UserManager.get().getMyUserId())||atUser.equals("all"))){
                            String groupId=htMessage.getTo();
                            GroupInfoManager.getInstance().setAtTag(groupId,true);
                        }

                    }else{
//                        if( !UserManager.get().getFriends().contains(htMessage.getUsername())) {
//                            return;
//                        }
                        //新加好友发过来消息，而好友关系拉取需要时间，会有时差
                        if(htMessage.getIntAttribute("action",0)==50001){
                            //保存资料及添加进通讯录
                            String userId=htMessage.getFrom();
                            String nick=htMessage.getStringAttribute("nick");
                            String avatar=htMessage.getStringAttribute("avatar");
                            UserManager.get().saveUserNickAvatar(userId, nick, avatar);
                            UserManager.get().addMyFriends(userId);
                        }

                    }
                    //防止某些消息不需要存库，在逻辑处理完之后再处理存库
                    HTClient.getInstance().  messageManager().saveMessage(htMessage, true);

                    handleHTMessage(htMessage);

                }
            }).start();


        }

        @Override
        public void onCMDMessgae(CmdMessage cmdMessage) {
            handleCmdMessage(cmdMessage);
        }

        @Override
        public void onCallMessgae(CallMessage callMessage) {

        }
    };

    private void handleHTMessage(HTMessage htMessage) {

        Intent intent = new Intent(IMAction.ACTION_NEW_MESSAGE);
        intent.putExtra("message", htMessage);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
        if (ChatActivity.activityInstance != null && htMessage.getUsername().equals(ChatActivity.activityInstance.getToChatUsername())) {
        } else {
            NotifierManager.getInstance().onNewMessage(htMessage);
        }
    }


    private void handleCmdMessage(final CmdMessage cmdMessage) {
        LoggerUtils.e("cmdMessage----" + cmdMessage.toXmppMessage());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = cmdMessage.getBody();
                if (data != null) {
                    JSONObject dataJSON = JSONObject.parseObject(data);
                    if (dataJSON != null && dataJSON.containsKey("action")) {

                        int action = dataJSON.getInteger("action");
                        if (action == 1000 || action == 1001) {
                            //1000收到好友申请 1001收到好友同意
//                            if (action == 1001) {
//
//                                //本地将该用户存在好友列表中
//                                JSONObject jsonObject = dataJSON.getJSONObject("data");
//                                if (jsonObject != null) {
//                                    String userId = jsonObject.getString("userId");
//                                    String nick = jsonObject.getString("nick");
//                                    String avatar = jsonObject.getString("avatar");
//                                    UserManager.get().saveUserNickAvatar(userId, nick, avatar);
//                                    UserManager.get().addMyFriends(userId);
//                                }
//
//                            }

                            //收到好友申请的请求
                            //响铃震动+UI提示
                            NotifierManager.getInstance().vibrateAndPlayTone();
                            SettingsManager.getInstance().setContactChangeUnread(true);
                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_INVITE_MESSAGE));

                        }
//                        else if (action == 1001) {
//                            //收到好友同意的透传消息
//                            SettingsManager.getInstance().setContactChangeUnread(true);
//                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcastSync(new Intent(IMAction.ACTION_CONTACT_CHANAGED));
//                        }
//                        else if (action == 1002) {
//                            //收到好友拒绝的透传消息
//
//                        }
                        else if (action == 1003) {
                            //收到删除好友的透传消息
                            //发送广播
                            //本地将该用户存在好友列表中
                            JSONObject jsonObject = dataJSON.getJSONObject("data");
                            if (jsonObject != null) {
                                String userId = jsonObject.getString("userId");
                                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.CMD_DELETE_FRIEND).putExtra("userId", userId));
                                //关闭聊天页，删除会话页，通讯录移除

                            }


                        }
//                        else if (action == 1004) {
//                            //进群申请审核
//                            //收到好友同意的透传消息
//
//
//                        }
                        else if (action == 2004) {
                            //收到你被踢出某群的消息
                            String groupId = dataJSON.getString("data");
                            HTClient.getInstance().groupManager().deleteGroupLocalOnly(groupId);
                            // notify ui
                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_REMOVED_FROM_GROUP).putExtra("userId", groupId));
                        }
                        else if(action == 2008) {
                            //收到你被踢出某群的消息
                            String groupId = dataJSON.getString("data");
                            Message message = handler.obtainMessage();
                             message.what = 1004;
                             message.obj=groupId;
                            message.sendToTarget();
                         }

                        else if (action == 6000) {//收到撤回消息的透传
                              String msgId = dataJSON.getString("msgId");
                              String  opId = dataJSON.getString("opId");
                            String  opNick = dataJSON.getString("opNick");
                            String chatTo = cmdMessage.getTo();
                            if (cmdMessage.getChatType() == ChatType.singleChat) {
                                chatTo = cmdMessage.getFrom();
                            }
                            //完成数据库的处理，然后通知UI进行处理（内存数据）

                             HTMessage htMessage = HTClient.getInstance().messageManager().getMssage(chatTo, msgId);
                            if (htMessage != null) {
                                HTMessageUtils.makeToWithDrowMsg(htMessage,opId,opNick);
                                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_WITHDROW)
                                        .putExtra("chatTo", chatTo).putExtra("message",htMessage)
                                );
                            }
                        } else if (action == 30000) {//被禁言
                            String to = cmdMessage.getTo();
                            JSONObject jsonObject = dataJSON.getJSONObject("data");
//                            String adminId = dataJSON.getString("adminId");
//                            String adminNick = dataJSON.getString("adminNick");
//                            String groupName = dataJSON.getString("groupName");
                            GroupInfoManager.getInstance().setGroupSilent(to, true,false);

                            String content = applicationContext.getString(R.string.has_no_talk);
                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_HAS_NO_TALK).putExtra("userId", to).putExtra("content", content));
                            // CommonUtils.sendNoTalkBrocast(applicationContext, to, jsonObject);
                        } else if (action == 30001) {//解除禁言
                            String to = cmdMessage.getTo();
                            JSONObject jsonObject = dataJSON.getJSONObject("data");
                            GroupInfoManager.getInstance().setGroupSilent(to,false,false);

                            CommonUtils.sendCancleNoTalkBrocast(applicationContext, to, jsonObject);
                        } else if (action == 30002 || action == 30003) {//30002设置了管理员 30003取消了
                            JSONObject jsonObject = dataJSON.getJSONObject("data");
                            String userId = jsonObject.getString("userId");
                            String groupId = jsonObject.getString("groupId");
                            String nick = jsonObject.getString("nick");
                            String avatar = jsonObject.getString("avatar");
                            //防止本地没有此管路员的资料
                            UserManager.get().saveUserNickAvatar(userId, nick, avatar);
                            if (action == 30002) {
                                GroupInfoManager.getInstance().addManager(groupId, userId);

                            } else {
                                GroupInfoManager.getInstance().removManager(groupId, userId);
                            }
                            LocalBroadcastManager.getInstance(applicationContext).
                                    sendBroadcast(new Intent(IMAction.ACTION_SET_OR_CANCLE_GROUP_MANAGER)
                                            .putExtra("groupId", groupId)
                                            .putExtra("userId", userId)
                                            .putExtra("action", action));                          //  CommonUtils.sendCancleOrSetBrocast(applicationContext, to, userId, action);
                        }else if (action == 30004) {//被单个禁言
                            Log.d("30004-----", dataJSON.toJSONString());
                             //JSONObject jsonObject = dataJSON.getJSONObject("data");
                            String groupId = dataJSON.getString("data");
                            String content = "您已被管理员禁言";
                            GroupInfoManager.getInstance().setGroupSilent(groupId, true, true);

                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.NO_TALK_USER).putExtra("userId", groupId).putExtra("content", content));
                            // CommonUtils.sendCancleOrSetBrocast(applicationContext, to, userId, action);
                        } else if (action == 30005) {
                             //被解除单个禁言
                            String groupId = dataJSON.getString("data");
                            String content = "您已被管理员解除禁言";
                            GroupInfoManager.getInstance().setGroupSilent(groupId, false, true);

                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.NO_TALK_USER_CANCEL).putExtra("userId", groupId).putExtra("content", content));

                        }

                        else if (action == 40002) {
                            Intent intent = new Intent();
                            JSONObject jsonObject = dataJSON.getJSONObject("data");
                            String groupId = jsonObject.getString("groupId");
                            String title = jsonObject.getString("title");
                            String content = jsonObject.getString("content");
                            String id = jsonObject.getString("id");

                            intent.putExtra("groupId", groupId);
                            intent.putExtra("title", title);
                            intent.putExtra("content", content);
                            intent.putExtra("id", id);
                            intent.setAction(IMAction.NEW_GROUP_NOTICE);
                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
                        }else if (action == 40001) {
                            String chatTo=cmdMessage.getTo();

                            if(ChatActivity.activityInstance!=null&&ChatActivity.activityInstance.getToChatUsername().equals(chatTo)){
                                return;
                            }

                            Intent intent1 = new Intent();
                            intent1.setClass(applicationContext, VideoIncomingActivity.class);
                            intent1.putExtra("data", dataJSON.getJSONObject("data").toJSONString());
                            intent1.putExtra("action", action);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_SINGLE_TOP  );
                            applicationContext.startActivity(intent1);
                        }
//                        else if (action == 1008611) {//群成员变化
//                            String groupId = cmdMessage.getTo();
//                            HTGroup group = HTClient.getInstance().groupManager().getGroup(groupId);
//                            if (group == null) {
//                                String groupId1 = dataJSON.getString("groupId");
//                                String groupOwner = dataJSON.getString("groupOwner");
//                                String groupName = dataJSON.getString("groupName");
//                                HTGroup group1 = new HTGroup();
//                                group1.setGroupId(groupId);
//                                group1.setGroupName(groupName);
//                                group1.setOwner(groupOwner);
//                                HTClient.getInstance().groupManager().saveGroup(group1);
//                            }
//                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_CHANGE_GROUP_MEMBER).putExtra(HTConstant.JSON_KEY_USERID, groupId));
//                        }
                    }
                }
            }
        }).start();

    }


//    /**
//     * user has logged into another device
//     */
//    public void notifyConflict(Context context, boolean statues) {
//        Intent intent = new Intent(applicationContext, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(IMAction.ACTION_CONFLICT, statues);
//        context.startActivity(intent);
//    }

    /**
     * user has logged into another device
     */
    public void notifyConflict(Context context) {
        Message message = handler.obtainMessage();
        message.what = 1002;
        message.sendToTarget();

//        Intent intent = new Intent(applicationContext, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(IMAction.ACTION_CONFLICT, true);
//        context.startActivity(intent);
    }

    /**
     * user has logged into another device
     */
    protected void notifyConnection(boolean isConnected) {

        Intent intent = new Intent(IMAction.ACTION_CONNECTION_CHANAGED);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("state", isConnected);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }


    private void checkToken() {
        String token = UserManager.get().getToken();
        if (TextUtils.isEmpty(token)) {
            Message message = handler.obtainMessage();
            message.what = 1003;
            message.sendToTarget();
            return;
        }
        JSONObject data = new JSONObject();
        data.put("token", token);
        ApiUtis.getInstance().postJSON(data, Constant.URL_CHECK_TOKEN, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    int data = jsonObject.getInteger("data");
                    if (data == 1) {
                        Message message = handler.obtainMessage();
                        message.what = 1003;
                        message.sendToTarget();
                    }
                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });


    }
}
