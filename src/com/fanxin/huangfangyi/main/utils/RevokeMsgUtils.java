package com.fanxin.huangfangyi.main.utils;

import android.content.Context;
import android.util.Log;

import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.easeui.EaseConstant;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.UUID;

/**
 * Created by huangfangyi on 2016/9/29.
 * qq 84543217
 */

public class RevokeMsgUtils {

    public static void setRevokeMsg(final String hxid, final String msgId, final boolean isLocalSend, int chatType, final CallBack callBack, final Context context) {

        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        //支持单聊和群聊，默认单聊，
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            cmdMsg.setChatType(EMMessage.ChatType.GroupChat);
            cmdMsg.setAttribute(FXConstant.KEY_USER_INFO, DemoApplication.getInstance().getUserJson().toJSONString());
        } else {

            cmdMsg.setChatType(EMMessage.ChatType.Chat);
        }

        //action可以自定义
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(FXConstant.FX_REVOKE_MESSAGE);
        cmdMsg.setReceipt(hxid);
        cmdMsg.addBody(cmdBody);
        cmdMsg.setAttribute(FXConstant.REVOKE_MESSAGE_ID, msgId);
        cmdMsg.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                try {
                    String content = "你撤回了一条消息";
                    Log.d("content--->", content);
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(hxid);
                    conversation.removeMessage(msgId);
                    EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
                    msg.setChatType(EMMessage.ChatType.Chat);
                    msg.setFrom(DemoHelper.getInstance().getCurrentUsernName());
                    msg.setTo(hxid);
                    msg.setMsgId(UUID.randomUUID().toString());
                    try {
                        msg.addBody(new EMTextMessageBody(content));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    msg.setStatus(EMMessage.Status.SUCCESS);
                    msg.setAttribute(FXConstant.IS_MESSAGE_REVOKE, true);
                    msg.setAttribute(FXConstant.IS_MESSAGE_REVOKE_SEND, isLocalSend);
                    // save invitation as messages
                    EMClient.getInstance().chatManager().saveMessage(msg);
                    // notify invitation message
                    callBack.onSuccess();


                } catch (NullPointerException e) {

                    e.printStackTrace();
                }


            }

            @Override
            public void onError(int i, final String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }

    public interface CallBack {

        void onSuccess();
    }
}


