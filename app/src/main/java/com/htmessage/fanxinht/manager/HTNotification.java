package com.htmessage.fanxinht.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.acitivity.chat.ChatActivity;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageTextBody;

/**
 * Created by huangfangyi on 2016/12/19.
 * qq 84543217
 */

public class HTNotification {
    private NotificationManager manager = null;
    private Context mContext;
     private NotificationCompat.Builder builder;
    public HTNotification(Context context) {
        this.mContext = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //为了版本兼容  选择V4包下的NotificationCompat进行构造
        builder = new NotificationCompat.Builder(mContext);
     }


    public void onNewMessage(HTMessage htMessage) {
        String userId = htMessage.getUsername();
        String userNick = userId;
        Intent intent = new Intent();
        intent.setClass(mContext, ChatActivity.class);
        intent.putExtra("userId", userId);
        if (htMessage.getChatType() == ChatType.singleChat) {
            User user = ContactsManager.getInstance().getContactList().get(userId);
            if (user != null) {
                userNick = user.getNick();
            }
        } else if (htMessage.getChatType() == ChatType.groupChat) {
            HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(userId);
            if (htGroup != null) {
                userNick = htGroup.getGroupName();
            }
            intent.putExtra("chatType", MessageUtils.CHAT_GROUP);
        }
//        if (!HTApp.getInstance().isForegroud()) {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, Integer.valueOf(userId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            builder
//                    .setContentIntent(pendingIntent)
//                    .setContentTitle(userNick)
//                    //  .setTicker("发来一个新消息")
//                    .setContentText(getContent(htMessage))
//                    .setWhen(System.currentTimeMillis())
//                    .setPriority(Notification.PRIORITY_DEFAULT)
////                .setOngoing(false)
////                .setAutoCancel(true)
//                    .setAutoCancel(true)
//                    .setFullScreenIntent(pendingIntent, true)
//                    .setDefaults(Notification.DEFAULT_SOUND)
//                    .setSmallIcon(mContext.getApplicationInfo().icon);
//            // do stuff
//            Log.d("isMyProces --->", "isMyProcessInTheForeground");
//            NotificationCompatBase.Action action;
//
//            //如果描述的PendingIntent已经存在，则在产生新的Intent之前会先取消掉当前的
////            PendingIntent hangPendingIntent = PendingIntent.getActivity(mContext, 0, hangIntent, PendingIntent.FLAG_CANCEL_CURRENT);
////            builder.setFullScreenIntent(hangPendingIntent, true);
////          //  notificationManager.notify(2, builder.build());
//        } else {
        // 如果当前Activity启动在前台，则不开启新的Activity。
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, Integer.valueOf(userId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent)
                .setContentTitle(userNick)
                //  .setTicker("发来一个新消息")
                .setContentText(getContent(htMessage))
                .setWhen(System.currentTimeMillis())
//                    .setPriority(Notification.PRIORITY_DEFAULT)
//                .setOngoing(false)
//                .setAutoCancel(true)
//                    .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(mContext.getApplicationInfo().icon);
//            Log.d("isMyProces2 --->", "isMyProcessInTheForeground");
//        }

        Notification notification = builder.build();
        /**
         * 判断是否打开了某些设置
         * */
        if (SettingsManager.getInstance().getSettingMsgNotification()) {
            if (SettingsManager.getInstance().getSettingMsgSound() && !SettingsManager.getInstance().getSettingMsgVibrate()) {
                notification.flags = Notification.DEFAULT_SOUND;
            } else if (SettingsManager.getInstance().getSettingMsgVibrate() && !SettingsManager.getInstance().getSettingMsgSound()) {
                notification.flags = Notification.DEFAULT_VIBRATE;
            } else if (SettingsManager.getInstance().getSettingMsgVibrate() && SettingsManager.getInstance().getSettingMsgSound()) {
                notification.flags = Notification.DEFAULT_ALL;
            } else {
                notification.flags = Notification.DEFAULT_LIGHTS;
            }
        }
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(Integer.valueOf(userId), notification);//发送通知
    }

    public void cancel(int id) {
        manager.cancel(id);
    }

    protected final static int[] msgs = {R.string.sent_a_message, R.string.sent_a_picture,R.string.sent_a_voice, R.string.sent_a_location,R.string.sent_a_video, R.string.sent_a_file,
            R.string.contacts_messages};

    private String getContent(HTMessage message) {
        HTConversation htConversation = HTClient.getInstance().conversationManager().getConversation(message.getFrom());
        String notifyText = "";
        if (htConversation != null) {
            if (htConversation.getUnReadCount() > 0) {
                notifyText = mContext.getString(R.string.zhongkuohao) + htConversation.getUnReadCount() + mContext.getString(R.string.zhongkuohao_msg);

            }

        }

        switch (message.getType()) {
            case TEXT:
                HTMessageTextBody htMessageTextBody = (HTMessageTextBody) message.getBody();
                String content = htMessageTextBody.getContent();
                if (content != null) {
                    notifyText += content;
                } else {
                    notifyText += getString(msgs[0]);
                }
                break;
            case IMAGE:
                notifyText += getString(msgs[1]);
                break;
            case VOICE:
                notifyText +=getString(msgs[2]);
                break;
            case LOCATION:
                notifyText += getString(msgs[3]);
                break;
            case VIDEO:
                notifyText += getString(msgs[4]);
                break;
            case FILE:
                notifyText += getString(msgs[4]);
                break;
        }
        return notifyText;
    }

    private String getString(int res){
        if(mContext!=null){
            return mContext.getString(res);
        }
        return "";
    }
}
