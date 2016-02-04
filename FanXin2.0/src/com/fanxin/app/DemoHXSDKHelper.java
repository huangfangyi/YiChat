package com.fanxin.app;

import java.util.Map;

import android.content.Intent;
import android.content.IntentFilter;

import com.fanxin.applib.controller.HXSDKHelper;
import com.fanxin.applib.model.HXSDKModel;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;
import com.fanxin.app.domain.User;
import com.fanxin.app.fx.ChatActivity;
import com.fanxin.app.fx.MainActivity;
import com.fanxin.app.fx.others.TopUser;
import com.fanxin.app.receiver.VoiceCallReceiver;
import com.fanxin.app.utils.CommonUtils;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 * 
 * @author easemob
 * 
 */
public class DemoHXSDKHelper extends HXSDKHelper {

    /**
     * contact list in cache
     */
    private Map<String, User> contactList;
    private Map<String, TopUser> topUserList;

    @Override
    protected void initHXOptions() {
        super.initHXOptions();
        // you can also get EMChatOptions to set related SDK options
        // EMChatOptions options = EMChatManager.getInstance().getChatOptions();
    }

    @Override
    protected OnMessageNotifyListener getMessageNotifyListener() {
        // 取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的
        return new OnMessageNotifyListener() {

            @Override
            public String onNewMessageNotify(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = CommonUtils.getMessageDigest(message,
                        appContext);
                if (message.getType() == Type.TXT)
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                String nick = message.getFrom();
                User user = MYApplication.getInstance().getContactList()
                        .get(nick);
                if (user != null && user.getNick() != null) {
                    nick = user.getNick();
                }

                return nick + ": " + ticker;
            }

            @Override
            public String onLatestMessageNotify(EMMessage message,
                    int fromUsersNum, int messageNum) {

                return fromUsersNum + "个好友，发来了" + messageNum + "条消息";
            }

            @Override
            public String onSetNotificationTitle(EMMessage message) {
                // 修改标题,这里使用默认
                return null;
            }

            @Override
            public int onSetSmallIcon(EMMessage message) {
                // 设置小图标
                return 0;
            }
        };
    }

    @Override
    protected OnNotificationClickListener getNotificationClickListener() {
        return new OnNotificationClickListener() {

            @Override
            public Intent onNotificationClick(EMMessage message) {
                Intent intent = new Intent(appContext, ChatActivity.class);
                ChatType chatType = message.getChatType();
                if (chatType == ChatType.Chat) { // 单聊信息
                    intent.putExtra("userId", message.getFrom());
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                } else { // 群聊信息
                         // message.getTo()为群聊id
                    intent.putExtra("groupId", message.getTo());
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                }
                return intent;
            }
        };
    }

    @Override
    protected void onConnectionConflict() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("conflict", true);
        appContext.startActivity(intent);
    }

    @Override
    protected void onCurrentAccountRemoved() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }

    @Override
    protected void initListener() {
        super.initListener();
        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance()
                .getIncomingVoiceCallBroadcastAction());
        appContext.registerReceiver(new VoiceCallReceiver(), callFilter);
    }

    @Override
    protected HXSDKModel createModel() {
        return new DemoHXSDKModel(appContext);
    }

    /**
     * get demo HX SDK Model
     */
    public DemoHXSDKModel getModel() {
        return (DemoHXSDKModel) hxModel;
    }

    /**
     * 获取内存中好友user list
     * 
     * @return
     */
    public Map<String, User> getContactList() {
        if (getHXId() != null && contactList == null) {
            contactList = ((DemoHXSDKModel) getModel()).getContactList();
        }

        return contactList;
    }

    /**
     * 获取内存中置顶好友 t
     * 
     * @return
     */
    public Map<String, TopUser> getTopUserList() {
        if (getHXId() != null && topUserList == null) {
            topUserList = ((DemoHXSDKModel) getModel()).getTopUserList();
        }

        return topUserList;
    }

    /**
     * 设置置顶好友到内存中
     * 
     * @param contactList
     */
    public void setTopUserList(Map<String, TopUser> topUserList) {
        this.topUserList = topUserList;
    }

    /**
     * 设置好友user list到内存中
     * 
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        this.contactList = contactList;
    }

    @Override
    public void logout(final EMCallBack callback) {
        super.logout(new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                setContactList(null);
                getModel().closeDB();
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

        });
    }

}
