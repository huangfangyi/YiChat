package com.htmessage.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.MessageManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.service.MessageService;

/**
 * Created by huangfangyi on 2017/1/17.
 * qq 84543217
 */

public class HTMessageHelper {


    public static void sendHTMessage(final HTMessage htMessage, final Context context) {

                HTClient.getInstance().messageManager().saveMessage(htMessage,false);
                Intent intent = new Intent(context, MessageService.class);
                intent.putExtra("TYPE", MessageService.TYPE_CHAT);
                intent.putExtra("chatTo", htMessage.getTo());
                intent.putExtra("body", htMessage.toXmppMessageBody());
                 int chatTypeInt=1;
                if(htMessage.getChatType()==ChatType.groupChat){
                    chatTypeInt=2;
                }
                intent.putExtra("chatType", chatTypeInt);
                intent.putExtra("msgId", htMessage.getMsgId());
               // context.startService(intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }



    }

    public static void sendCustomMessage(CmdMessage customMessage, Context context){

        Intent intent = new Intent(context, MessageService.class);
        intent.putExtra("TYPE", MessageService.TYPE_CHAT_CMD);
        intent.putExtra("chatTo", customMessage.getTo());
        intent.putExtra("body", customMessage.toXmppMessage());
        int chatTypeInt=1;
        if(customMessage.getChatType()==ChatType.groupChat){

            chatTypeInt=2;
        }
        intent.putExtra("chatType", chatTypeInt);
        intent.putExtra("msgId", customMessage.getMsgId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

    }



}
