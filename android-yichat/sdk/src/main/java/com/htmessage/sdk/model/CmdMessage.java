package com.htmessage.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.SDKConstant;
import com.htmessage.sdk.manager.HTPreferenceManager;

import java.util.UUID;


/**
 * Created by huangfangyi on 2016/12/14.
 * qq 84543217
 */

public class CmdMessage implements Parcelable {
    private String from;
    private String to;
    private long time;
    private String body;
    private ChatType chatType;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    private String msgId;

    public CmdMessage(Parcel in) {
        msgId = in.readString();
        from = in.readString();
        to = in.readString();
        time = in.readLong();
        body = in.readString();
        int chatTypeInt = in.readInt();
        if (chatTypeInt == ChatType.groupChat.ordinal()) {
            chatType = ChatType.groupChat;
        } else {
            chatType = ChatType.singleChat;
        }


    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;

    }

    public ChatType getChatType() {

        return chatType;
    }


    public static final Creator<CmdMessage> CREATOR = new Creator<CmdMessage>() {
        @Override
        public CmdMessage createFromParcel(Parcel in) {
            return new CmdMessage(in);
        }

        @Override
        public CmdMessage[] newArray(int size) {
            return new CmdMessage[size];
        }
    };

    private CmdMessage creatCmdMessage(String chatTo) {
        CmdMessage cmdMessage = new CmdMessage();
        cmdMessage.setFrom(HTPreferenceManager.getInstance().getUser().getUsername());
        cmdMessage.setTo(chatTo);
        cmdMessage.setChatType(ChatType.singleChat);
        cmdMessage.setMsgId(UUID.randomUUID().toString());
        cmdMessage.setTime(System.currentTimeMillis());
        return cmdMessage;

    }

    public CmdMessage creatCmdMessage(String chatTo, String body) {
        CmdMessage cmdMessage = creatCmdMessage(chatTo);
        cmdMessage.setBody(body);
        return cmdMessage;

    }

    public CmdMessage() {
        this.chatType=ChatType.singleChat;
        this.time= System.currentTimeMillis();
        this.from= HTPreferenceManager.getInstance().getUser().getUsername();
        this.msgId=UUID.randomUUID().toString();
    }



    public void setTime(long time) {
        this.time = time;
    }


    public long getTime() {

        return time;
    }


    public String getFrom() {

        return from;

    }

    public void setFrom(String from) {
        this.from = from;
    }


    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    public void setBody(String body) {
        this.body = body;

    }

    public String getBody() {
        return body;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msgId);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeLong(time);
        dest.writeString(body);
        //默认单聊
        int chatTypeInt = 1;
        if (chatType == ChatType.groupChat) {
            chatTypeInt = 2;
        }


        dest.writeInt(chatTypeInt);
    }

    public String toXmppMessage() {

        JSONObject dataJson = new JSONObject();

        dataJson.put("from", from);
        dataJson.put("to", to);
        dataJson.put("msgId", msgId);
        int chatTypeInt = 1;
        if (chatType == ChatType.groupChat) {
            chatTypeInt = 2;
        }
        dataJson.put("chatType", chatTypeInt);
        dataJson.put("body", body);
        JSONObject xmppJson = new JSONObject();
        xmppJson.put(SDKConstant.FX_MSG_KEY_TYPE, SDKConstant.TYPE_MESSGAE_CMD);
        xmppJson.put(SDKConstant.FX_MSG_KEY_DATA, dataJson);

        return xmppJson.toJSONString();
    }

    public CmdMessage(JSONObject bodyJson, long time) {
        from = bodyJson.getString("from");
        to = bodyJson.getString("to");
        int chatTypeInt = bodyJson.getInteger("chatType");
        if (chatTypeInt == 1) {
            chatType = ChatType.singleChat;
        } else if (chatTypeInt == 2) {
            chatType = ChatType.groupChat;
        }
        body = bodyJson.getString("body");
        this.msgId = bodyJson.getString("msgId");
        this.time = time;
    }


}
