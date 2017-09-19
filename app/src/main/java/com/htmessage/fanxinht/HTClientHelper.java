package com.htmessage.fanxinht;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.client.HTOptions;
import com.htmessage.fanxinht.acitivity.main.MainActivity;
import com.htmessage.fanxinht.acitivity.chat.ChatActivity;
import com.htmessage.fanxinht.acitivity.chat.call.VideoIncomingActivity;
import com.htmessage.fanxinht.acitivity.chat.call.VideoOutgoingActivity;
import com.htmessage.fanxinht.acitivity.chat.call.VoiceIncomingActivity;
import com.htmessage.fanxinht.acitivity.chat.call.VoiceOutgoingActivity;
import com.htmessage.fanxinht.domain.InviteMessage;
import com.htmessage.fanxinht.domain.InviteMessgeDao;
import com.htmessage.fanxinht.domain.MomentsMessage;
import com.htmessage.fanxinht.domain.MomentsMessageDao;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.manager.ContactsManager;
import com.htmessage.fanxinht.manager.NotifierManager;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.utils.HTMessageUtils;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.listener.HTConnectionListener;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CallMessage;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTMessage;

import java.util.List;

/**
 * Created by huangfangyi on 2017/3/3.
 * qq 84543217
 */

public class HTClientHelper {
    private static Context applicationContext;

    private static HTClientHelper htClientHelper;

    public static void init(Context context) {
        htClientHelper = new HTClientHelper(context);
    }

    public HTClientHelper(Context context) {
        this.applicationContext = context;
//        HTOptions options=new HTOptions();
//        options.setHost(HTConstant.HOST_IM);
//        options.setOssInfo(HTConstant.endpoint,HTConstant.bucket,HTConstant.accessKeyId,HTConstant.accessKeySecret);
//        options.setSinglePointUrl(HTConstant.DEVICE_URL_UPDATE,HTConstant.DEVICE_URL_GET);
//        options.setDebug(false);
//        options.setKeepAlive(false);
        HTOptions htOptions = new HTOptions();
        htOptions.setDualProcess(true);
        htOptions.setDebug(true);
        HTClient.init(applicationContext, htOptions);
        HTClient.getInstance().setMessageLisenter(messageLisenter);
        HTClient.getInstance().addConnectionListener(htConnectionListener);
    }

    public static HTClientHelper getInstance() {

        if (htClientHelper == null) {
            throw new RuntimeException("please init first!");
        }
        return htClientHelper;
    }

    private HTConnectionListener htConnectionListener = new HTConnectionListener() {
        @Override
        public void onConnected() {

            //   Toast.makeText(applicationContext,"连上啦",Toast.LENGTH_SHORT).show();
            notifyConnection(true);
        }

        @Override
        public void onDisconnected() {
            notifyConnection(false);
            // Toast.makeText(applicationContext,"断连啦",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConflict() {
            // Toast.makeText(applicationContext,"被踢啦",Toast.LENGTH_SHORT).show();
            notifyConflict(applicationContext);
        }
    };
    private HTClient.MessageLisenter messageLisenter = new HTClient.MessageLisenter() {
        @Override
        public void onHTMessage(HTMessage htMessage) {
            Log.d("htMessage---->", htMessage.toXmppMessageBody());
            handleHTMessage(htMessage);

        }

        @Override
        public void onCMDMessgae(CmdMessage cmdMessage) {

            handleCmdMessage(cmdMessage);

        }

        @Override
        public void onCallMessgae(CallMessage callMessage) {
            handleCallMessage(callMessage);
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


    private void handleCmdMessage(CmdMessage cmdMessage) {
        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(applicationContext);
        String data = cmdMessage.getBody();
        if (data != null) {
            JSONObject dataJSON = JSONObject.parseObject(data);
            if (dataJSON != null && dataJSON.containsKey("action")) {

                int action = dataJSON.getInteger("action");
                if (action == 1000) {
                    //收到好友申请的请求
                    List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
                    for (InviteMessage inviteMessage : msgs) {
                        if (inviteMessage.getFrom().equals(cmdMessage.getFrom())) {
                            inviteMessgeDao.deleteMessage(cmdMessage.getFrom());
                        }
                    }
                    InviteMessage msg = new InviteMessage();
                    msg.setFrom(cmdMessage.getFrom());
                    msg.setTime(System.currentTimeMillis());
                    msg.setReason(dataJSON.getJSONObject("data").toJSONString());
                    //    Log.d(TAG, message.getFrom() + "apply to be your friend,reason: " + userInfo);
                    // set invitation status
                    msg.setStatus(InviteMessage.Status.BEINVITEED);
                    notifyNewInviteMessage(msg, null);
                } else if (action == 1001) {
                    //收到好友同意的透传消息
                    List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
                    for (InviteMessage inviteMessage : msgs) {
                        if (inviteMessage.getFrom().equals(cmdMessage.getFrom())) {
                            inviteMessgeDao.deleteMessage(cmdMessage.getFrom());

                        }
                    }
                    // save invitation as message
                    InviteMessage msg = new InviteMessage();
                    msg.setFrom(cmdMessage.getFrom());
                    msg.setReason(dataJSON.getJSONObject("data").toJSONString());
                    msg.setTime(System.currentTimeMillis());
                    //   Log.d(TAG, message.getFrom() + "accept your request");
                    msg.setStatus(InviteMessage.Status.BEAGREED);
                    notifyNewInviteMessage(msg, dataJSON.getJSONObject("data"));
                } else if (action == 1002) {
                    //收到好友拒绝的透传消息
                    List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
                    for (InviteMessage inviteMessage : msgs) {
                        if (inviteMessage.getFrom().equals(cmdMessage.getFrom())) {
                            inviteMessgeDao.deleteMessage(cmdMessage.getFrom());
                        }
                    }
                    // save invitation as message
                    InviteMessage msg = new InviteMessage();
                    msg.setFrom(cmdMessage.getFrom());
                    msg.setReason(dataJSON.getJSONObject("data").toJSONString());
                    msg.setTime(System.currentTimeMillis());
                    msg.setStatus(InviteMessage.Status.BEREFUSED);
                    notifyNewInviteMessage(msg, null);
                } else if (action == 1003) {
                    //收到删除好友的透传消息
                    //发送广播
                    if (HTApp.getInstance().getUsername().equals(cmdMessage.getTo()) || HTApp.getInstance().getUsername().equals(cmdMessage.getFrom())) {
                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.CMD_DELETE_FRIEND).putExtra(HTConstant.JSON_KEY_HXID, cmdMessage.getFrom()));
                    }
                } else if (action == 2004) {
                    //收到你被踢出某群的消息
                    String groupId = dataJSON.getString("data");
                    HTClient.getInstance().groupManager().deleteGroupLocalOnly(groupId);
                    // notify ui
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_REMOVED_FROM_GROUP).putExtra("groupId", groupId));
                } else if (action == 6000) {//收到撤回消息的透传
                    String msgId = dataJSON.getString("msgId");
                    String chatTo = cmdMessage.getTo();
                    if (cmdMessage.getChatType() == ChatType.singleChat) {
                        chatTo = cmdMessage.getFrom();
                    }
//                    HTClient.getInstance().messageManager().deleteMessage(chatTo, msgId);
                    HTMessage htMessage = HTClient.getInstance().messageManager().getMssage(chatTo, msgId);
                    if (htMessage != null) {
                        HTMessageUtils.creatWithDrowMsg(htMessage);
                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_WITHDROW).putExtra("msgId", msgId));
                    }
                } else if (action == 7000) {//收到朋友圈点赞或者评论的消息
                    JSONObject momentsData = dataJSON.getJSONObject("data");
                    int typeInt = momentsData.getInteger("type");

                    MomentsMessage.Type type = MomentsMessage.Type.GOOD;
                    if (typeInt == 1) {
                        type = MomentsMessage.Type.COMMENT;
                    } else if (typeInt == 2) {
                        type = MomentsMessage.Type.REPLY_COMMENT;
                    }
                    MomentsMessage momentsMessage = new MomentsMessage();
                    momentsMessage.setTime(System.currentTimeMillis());
                    momentsMessage.setUserNick(momentsData.getString("nickname"));
                    momentsMessage.setUserId(momentsData.getString("userId"));
                    momentsMessage.setMid(momentsData.getString("mid"));
                    momentsMessage.setStatus(MomentsMessage.Status.UNREAD);
                    momentsMessage.setImageUrl(momentsData.getString("imageUrl"));
                    momentsMessage.setUserAvatar(momentsData.getString("avatar"));
                    momentsMessage.setContent(momentsData.getString("content"));
                    momentsMessage.setType(type);
                    MomentsMessageDao momentsMessageDao = new MomentsMessageDao(applicationContext);
                    momentsMessageDao.savaMomentsMessage(momentsMessage);
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_MOMENTS));
                }
            }
        }
    }

    private void handleCallMessage(CallMessage callMessage) {
        String data = callMessage.getBody();
        if (data != null) {
            JSONObject dataJSON = JSONObject.parseObject(data);
            if (dataJSON != null && dataJSON.containsKey("action")) {

                int action = dataJSON.getInteger("action");
                if (action == 3000 || action == 4000 || action == 5000) {

                    //收到语音电话呼叫
                    if (!HTApp.isCalling) {
                        //如果没在通话,进入接听界面
                        if (action == 5000) {
                            JSONObject object = dataJSON.getJSONObject("data");
                            String callId = object.getString("callId");
                            if (object.containsKey("callId_add")) {
                                String callId_add = object.getString("callId_add");
                                if (!callId_add.contains(HTApp.getInstance().getUsername())) {
                                    return;
                                }
                            } else {
                                if (!callId.contains(HTApp.getInstance().getUsername())) {
                                    return;
                                }
                            }
                        }
                        Intent intent = new Intent();
                        if (action == 4000) {
                            intent.setClass(applicationContext, VideoIncomingActivity.class);
                        } else {
                            intent.setClass(applicationContext, VoiceIncomingActivity.class);
                        }
                        intent.putExtra("data", dataJSON.getJSONObject("data").toJSONString());
                        intent.putExtra("action", action);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        applicationContext.startActivity(intent);
                    } else {
                        if (callMessage.getChatType() == ChatType.singleChat) {
                            //发送繁忙的通知
                            JSONObject reJSON = new JSONObject();
                            JSONObject infoJSON = new JSONObject();
                            infoJSON.put("userId", HTApp.getInstance().getUserJson().getString("userId"));
                            infoJSON.put("nick", HTApp.getInstance().getUserJson().getString("nick"));
                            infoJSON.put("avatar", HTApp.getInstance().getUserJson().getString("avatar"));
                            infoJSON.put("callId", dataJSON.getJSONObject("data").getString("callId"));
                            reJSON.put("action", 3004);
                            reJSON.put("data", infoJSON);
                            CallMessage mCallMessage = new CallMessage();
                            mCallMessage.setBody(reJSON.toJSONString());
                            mCallMessage.setTo(callMessage.getFrom());
                            HTClient.getInstance().chatManager().sendCallMessage(mCallMessage, new HTChatManager.HTMessageCallBack() {
                                @Override
                                public void onProgress() {
                                    Log.d("action3004", "onProgress");
                                }

                                @Override
                                public void onSuccess() {
                                    Log.d("action3004", "onSuccess");
                                }

                                @Override
                                public void onFailure() {
                                    Log.d("action3004", "onFailure");
                                }
                            });
                        }
                    }
                } else if (action == 3001 || action == 3002 || action == 4002 || action == 4001) {
                    //3001对方取消了呼叫
                    //3002对方拒绝了你的语音电话
                    //3005对方挂断了电话
                    // 4002对方拒绝的语音申请
                    //4001对方取消了呼叫
                    if (HTApp.isCalling) {
                        //如果没在通话,进入接听界面
                        Intent intent = new Intent();
                        if (action == 3001) {
                            intent.setClass(applicationContext, VoiceIncomingActivity.class);
                        } else if (action == 3002) {
                            intent.setClass(applicationContext, VoiceOutgoingActivity.class);

                        } else if (action == 4002) {
                            intent.setClass(applicationContext, VideoOutgoingActivity.class);
                        } else if (action == 4001) {
                            intent.setClass(applicationContext, VideoIncomingActivity.class);
                        }
                        intent.putExtra("data", dataJSON.getJSONObject("data").toJSONString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.putExtra("action", action);
                        applicationContext.startActivity(intent);
                    }
                } else if (action == 3003 || action == 4003) {
                    //对方接听了你的语音电话;
                    //对方接听了你的视频通话;
                    if (HTApp.isCalling) {
                        //如果没在通话,进入接听界面
                        Intent intent = new Intent();
                        if (action == 4003) {
                            intent.setClass(applicationContext, VideoOutgoingActivity.class);
                        } else {
                            intent.setClass(applicationContext, VoiceOutgoingActivity.class);
                        }
                        intent.putExtra("data", dataJSON.getJSONObject("data").toJSONString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.putExtra("action", action);
                        applicationContext.startActivity(intent);
                    }
                } else if (action == 3005 || action == 4004 || action == 3004 || action == 5002) {
                    //3005-对方双方有一方挂断电话
                    //4004-视频聊天双方有一方切换成了语音通话
                    //3004-对方正在语音或者视频通话,繁忙中
                    //5002-群视频通话时,未接通状态下,发起者关闭呼叫
                    Intent intent = new Intent();

                    intent.putExtra("data", dataJSON.getJSONObject("data").toJSONString());
                    intent.setAction(action + "");
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
                }
            }
        }
    }


    /**
     * user has logged into another device
     */
    public void notifyConflict(Context context) {
        Intent intent = new Intent(applicationContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IMAction.ACTION_CONFLICT, true);
        context.startActivity(intent);
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

    /**
     * save and notify invitation message
     *
     * @param msg
     */
    private void notifyNewInviteMessage(final InviteMessage msg, final JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(applicationContext);
                inviteMessgeDao.saveMessage(msg);
                inviteMessgeDao.saveUnreadMessageCount(1);
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_INVITE_MESSAGE));
                NotifierManager.getInstance().vibrateAndPlayTone();
                if (jsonObject != null) {
                    User user = CommonUtils.Json2User(jsonObject);
                    ContactsManager.getInstance().saveContact(user);
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(IMAction.ACTION_CONTACT_CHANAGED));
                }
            }
        }).start();
    }
}
