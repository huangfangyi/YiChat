package com.htmessage.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.SDKConstant;
import com.htmessage.sdk.client.HTAction;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.CallMessage;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by huangfangyi on 2016/11/26.
 * qq 84543217
 */

public class MessageUtils {

    public static final String FROM = "from";
    public static final String TO = "to";
    //    public static final String TIMESTAMP="timestamp";
    public static final String CHAT_TYPE = "chatType";
    public static final String BODY = "body";
    public static final String MSG_ID = "msgId";
    public static final String MSG_TYPE = "msgType";
    public static final int TYPE_TEXT = 2001;
    public static final int TYPE_IMAGE = 2002;
    public static final int TYPE_VOICE = 2003;
    public static final int TYPE_VEDIO = 2004;
    public static final int TYPE_FILE = 2005;
    public static final int TYPE_LOCATION = 2006;
    public static final String TEXT_CONTENT = "content";
    public static final String EXT = "ext";
    public static final int CHAT_SINGLE = 1;
    public static final int CHAT_GROUP = 2;

    public static void handleReceiveMessage(Message message, Context context, boolean isOffline) {

        String messageBody = message.getBody();
        if (messageBody == null) {
            return;
        }
        try {

            //  messageBody=  Base64.decode(messageBody.getBytes()).toString();
            messageBody= URLDecoder.decode(messageBody,"utf-8");

            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            int type = jsonObject.getIntValue(SDKConstant.FX_MSG_KEY_TYPE);
            JSONObject dataJson = jsonObject.getJSONObject(SDKConstant.FX_MSG_KEY_DATA);
            //自定义消息
            if (type == SDKConstant.TYPE_MESSGAE_CMD) {
                ExtensionElement time = message.getExtension("delay", "urn:xmpp:delay");
                long timeStamp=System.currentTimeMillis();
                if(time!=null){
                    timeStamp= getTimeStamp(time.toXML().toString());
                }
                CmdMessage cmdMessage = new CmdMessage(dataJson,timeStamp );
                Intent intent = new Intent(HTAction.ACTION_MESSAGE_CMD);
                intent.putExtra("data", cmdMessage);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


            } else if (type == SDKConstant.TYPE_MESSGAE_HT) {

                ExtensionElement time = message.getExtension("delay", "urn:xmpp:delay");
                long timeStamp=System.currentTimeMillis();
                if(time!=null){
                    timeStamp= getTimeStamp(time.toXML().toString());
                }

                HTMessage htMessage = new HTMessage(dataJson, HTMessage.Direct.RECEIVE, HTMessage.Status.INPROGRESS, System.currentTimeMillis(), timeStamp);
                int action = htMessage.getIntAttribute("action", 0);
                Log.d("creatNewGroup--->",action+"");

                if (action == 2000) {
                    //新群建立成功
                    String groupName = htMessage.getStringAttribute("groupName");
                    String groupDesc = htMessage.getStringAttribute("groupDescription");
                    String groupAvatar = htMessage.getStringAttribute("groupAvatar");
                    creatNewGroup(htMessage.getFrom(), htMessage.getTo(), groupName, groupDesc, groupAvatar);
                } else if (action == 2004) {
               /*  有群成员被踢的消息
                String uid=extJson.getString("uid");
                String nickName=extJson.getString("nickName");
                        */

                }else if(action==2003){
                    //如果自己是被加入的,则需要创建一个群
//                    extJson.put("groupName",htGroup.getGroupName());
//                    extJson.put("groupDescription",htGroup.getGroupDesc());
//                    extJson.put("groupAvatar",htGroup.getImgUrl());
//                    extJson.put("owner",htGroup.getOwner());
                    creatNewGroup(htMessage.getStringAttribute("owner"),htMessage.getUsername(),htMessage.getStringAttribute("groupName"),

                            htMessage.getStringAttribute("groupDescription"),htMessage.getStringAttribute("groupAvatar")
                    );

                }

                if (isOffline) {
                    HTClient.getInstance(). messageManager().saveMessage(  htMessage, true);
                }else{
                    Intent intent = new Intent(HTAction.ACTION_MESSAGE);
                    intent.putExtra("data", htMessage);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }



            } else if (type == SDKConstant.TYPE_MESSGAE_GROUP_DESTROY) {
           /*      群被删除,通知群成员;
               <messagetype="groupchat"to="10033@app.im/app"from="10041@muc.app.im">
                <body>{"type":"4000","data":{"gid":"10041"}}</body>
               </message>*/

                String groupId = dataJson.getString("gid");
                if (!TextUtils.isEmpty(groupId)) {
                    HTClient.getInstance().groupManager().deleteGroupLocalOnly(groupId);
                    Intent intent=new Intent(HTAction.ACTION_GROUP_DELETED);
                    intent.putExtra("groupId",groupId);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

            } else if (type == SDKConstant.TYPE_MESSGAE_GROUP_LEAVE) {
                String groupId=message.getFrom();
                if(groupId.contains("@")){
                    groupId=groupId.substring(0,groupId.indexOf("@"));
                }
                String userId= dataJson.getString("uid");
                String userNick= dataJson.getString("nickname");
                if(!TextUtils.isEmpty(userId)){

                    Intent intent=new Intent(HTAction.ACTION_GROUP_LEAVED);
                    intent.putExtra("userId",userId);
                    intent.putExtra("userNick",userNick);
                    intent.putExtra("groupId",groupId);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                //某群成员自己退群了.
//                <messagetype="groupchat"to="10033@app.im/app"from="10041@muc.app.im">
//                <body>{"type":"3000","data":{"uid":"10031","nickname":"user10031"}}</body>
//                </message>
                //自定义一个消息体.
//                ExtensionElement time = message.getExtension("delay", "urn:xmpp:delay");
//                HTMessage htMessage=new HTMessage(dataJson, HTMessage.Direct.RECEIVE, HTMessage.Status.SUCCESS,System.currentTimeMillis(),getTimeStamp(time.toXML().toString()));
//                MessageManager.getInstance().saveMessage(htMessage,true);
//                sendNewMessageBroadcast(htMessage,context);

            }else if(type == SDKConstant.TYPT_CALL){
                ExtensionElement time = message.getExtension("delay", "urn:xmpp:delay");
                CallMessage callMessage = new CallMessage(dataJson, getTimeStamp(time.toXML().toString()));
                Intent intent = new Intent(HTAction.ACTION_MESSAGE_CALL);
                intent.putExtra("data", callMessage);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }


        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();

        }
    }


    private static void sendGroupChangeBroadCast(String groupId) {


//        Intent intent = new Intent();
//        intent.setAction("ACTION_HTCLIENT");
//        intent.putExtra("TYPE", ReceiverConstant.TYPE_NEW_MESSAGE_HT);
//        //  intent.setPackage("com.fanxin.tigase");
//        intent.putExtra("data", htMessage);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//// 3.1以后的版本直接设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES的value：32
//        if (android.os.Build.VERSION.SDK_INT >= 12) {
//            intent.setFlags(32);
//        }
//        context.sendBroadcast(intent);

    }


    private static void creatNewGroup(String owner, String groupId, String groupName, String groupDesc, String imgUrl) {

        HTGroup htGroup = new HTGroup();
        htGroup.setOwner(owner);
        htGroup.setGroupName(groupName);
        htGroup.setGroupId(groupId);
        htGroup.setImgUrl(imgUrl);
        htGroup.setGroupDesc(groupDesc);
        htGroup.setTime(System.currentTimeMillis());
        HTClient.getInstance().groupManager().saveGroup(htGroup);
    }


    public static long getTimeStamp(String xmlString) {
        if (xmlString.contains("stamp='")) {
            String[] strings = xmlString.split("stamp='");
            if (strings.length > 1) {
                String str1 = strings[1];

                String str2 = str1.substring(0, str1.indexOf("'"));

                try {
                    return dateToStamp(str2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return System.currentTimeMillis();
    }


    public static long dateToStamp(String s) throws ParseException {
        s = s.replace("T", " ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String finalTime = s.substring(0, 25);
        Date date = simpleDateFormat.parse(finalTime);
        long ts = date.getTime();
        return ts;
    }


    private static List<HTMessage> loadMessageList(List<HTMessage> messageList) {
        List<Pair<Long, HTMessage>> sortList = new ArrayList<Pair<Long, HTMessage>>();
        synchronized (messageList) {
            for (HTMessage htMessage : messageList) {
                try {
                    sortList.add(new Pair<Long, HTMessage>(htMessage.getTime(), htMessage));
                } catch (NullPointerException e) {

                }
            }
            try {
                sortMessageByLastChatTime(sortList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<HTMessage> list = new ArrayList<HTMessage>();
            for (Pair<Long, HTMessage> sortItem : sortList) {
                list.add(sortItem.second);
            }
            return list;
        }

    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param
     */

    private static void sortMessageByLastChatTime(List<Pair<Long, HTMessage>> messages) {
        Collections.sort(messages, new Comparator<Pair<Long, HTMessage>>() {
            @Override
            public int compare(final Pair<Long, HTMessage> con1, final Pair<Long, HTMessage> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return -1;
                } else {
                    return 1;
                }
            }

        });
    }

}
