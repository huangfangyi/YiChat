package com.htmessage.update.uitls;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.acitivity.main.pay.aliutils.Base64;
import com.htmessage.yichat.utils.LoggerUtils;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageFileBody;
import com.htmessage.sdk.model.HTMessageImageBody;
import com.htmessage.sdk.model.HTMessageLocationBody;
import com.htmessage.sdk.model.HTMessageTextBody;
import com.htmessage.sdk.model.HTMessageVideoBody;
import com.htmessage.sdk.model.HTMessageVoiceBody;
import com.htmessage.update.data.UserManager;

import java.net.URLDecoder;
import java.util.UUID;

/**
 * Created by huangfangyi on 2019/8/3.
 * qq 84543217
 */
public class MsgUtils {
    private static MsgUtils msgUtils;

    public static MsgUtils getInstance() {
        if (msgUtils == null) {
            msgUtils = new MsgUtils();
        }
        return msgUtils;
    }

    public HTMessage stringToMessage(String msgString, long time) {
        try {
            String msg =  URLDecoder.decode(msgString,"utf-8");
            JSONObject jsonObject = JSONObject.parseObject(msg);
            if (jsonObject.getIntValue("type") == 2000) {
                HTMessage htMessage = new HTMessage();
                JSONObject data = jsonObject.getJSONObject("data");
                if (data == null) {
                    return null;
                }
                if (HTApp.getInstance().getUsername().equals(data.getString("from"))) {
                    htMessage.setDirect(HTMessage.Direct.SEND);
                    htMessage.setStatus(HTMessage.Status.SUCCESS);
                } else {
                    htMessage.setDirect(HTMessage.Direct.RECEIVE);
                    htMessage.setStatus(HTMessage.Status.INPROGRESS);
                }
                if ("1".equals(data.getString("chatType"))) {
                    htMessage.setChatType(ChatType.singleChat);
                } else {
                    htMessage.setChatType(ChatType.groupChat);
                }
                String bodyStr = data.getString("body");
                try {
                    JSONObject bodyJSON = JSONObject.parseObject(bodyStr);
                    if(bodyJSON.containsKey("localPath")){
                       bodyJSON.remove("localPath");
                    }
                    if ("2001".equals(data.getString("msgType"))) {
                        htMessage.setType(HTMessage.Type.TEXT);
                        HTMessageTextBody body = new HTMessageTextBody(bodyJSON);
                        htMessage.setBody(body);
                    } else if ("2002".equals(data.getString("msgType"))) {
                        htMessage.setType(HTMessage.Type.IMAGE);
                        HTMessageImageBody body = new HTMessageImageBody(bodyJSON);
                        htMessage.setBody(body);
                    } else if ("2003".equals(data.getString("msgType"))) {
                        htMessage.setType(HTMessage.Type.VOICE);
                        HTMessageVoiceBody body = new HTMessageVoiceBody(bodyJSON);
                        htMessage.setBody(body);
                    } else if ("2004".equals(data.getString("msgType"))) {
                        htMessage.setType(HTMessage.Type.VIDEO);
                        HTMessageVideoBody body = new HTMessageVideoBody(bodyJSON);
                        htMessage.setBody(body);
                    } else if ("2005".equals(data.getString("msgType"))) {
                        htMessage.setType(HTMessage.Type.FILE);
                        HTMessageFileBody body = new HTMessageFileBody(bodyJSON);
                        htMessage.setBody(body);
                    } else if ("2006".equals(data.getString("msgType"))) {
                        htMessage.setType(HTMessage.Type.LOCATION);
                        HTMessageLocationBody body = new HTMessageLocationBody(bodyJSON);
                        htMessage.setBody(body);
                    }
                } catch (JSONException e) {
                }
                htMessage.setMsgId(data.getString("msgId"));
                htMessage.setFrom(data.getString("from"));
                htMessage.setTo(data.getString("to"));
                try {
                    JSONObject jsonObject1 = JSONObject.parseObject(data.getString("ext"));
                    htMessage.setAttributes(jsonObject1);
                } catch (JSONException e) {
                }
                htMessage.setTime(time);
                htMessage.setLocalTime(System.currentTimeMillis());
                return htMessage;

            } else if (jsonObject.getInteger("type") == 1000) {
                //CMD消息
//                JSONObject data = jsonObject.getJSONObject("data");
//                if (HTApp.getInstance().getUserId().equals(data.getString("from"))) {
//                    return;
//                }
//                CmdMessage cmdMessage = new CmdMessage(data, Long.parseLong(time));
//                Intent intent = new Intent(HTAction.ACTION_MESSAGE_CMD);
//                intent.putExtra("data", cmdMessage);
//                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);

            }
        } catch (Exception e) {
            LoggerUtils.e("msgString---->" + msgString);
            e.printStackTrace();
        }

        return null;
    }

    public HTMessage stringToMessage2(String msgString1, long time) {
        try {
            String msgString=new String(Base64.decode(msgString1)) ;
             String msg =  URLDecoder.decode(msgString,"utf-8");
            JSONObject jsonObject = JSONObject.parseObject(msg);
            if (jsonObject.getIntValue("type") == 2000) {
                HTMessage htMessage = new HTMessage();
                JSONObject data = jsonObject.getJSONObject("data");
                if (data == null) {
                    return null;
                }
                if (HTApp.getInstance().getUsername().equals(data.getString("from"))) {
                    htMessage.setDirect(HTMessage.Direct.SEND);
                    htMessage.setStatus(HTMessage.Status.SUCCESS);
                } else {
                    htMessage.setDirect(HTMessage.Direct.RECEIVE);
                    htMessage.setStatus(HTMessage.Status.INPROGRESS);
                }
                if ("1".equals(data.getString("chatType"))) {
                    htMessage.setChatType(ChatType.singleChat);
                } else {
                    htMessage.setChatType(ChatType.groupChat);
                }
                String bodyStr = data.getString("body");
                try {
                    JSONObject bodyJSON = JSONObject.parseObject(bodyStr);
                    if ("2001".equals(data.getString("msgType"))) {
                        htMessage.setType(HTMessage.Type.TEXT);
                        HTMessageTextBody body = new HTMessageTextBody(bodyJSON);
                        htMessage.setBody(body);
                    } else if ("2002".equals(data.getString("msgType"))) {
                        htMessage.setType(HTMessage.Type.IMAGE);
                        HTMessageImageBody body = new HTMessageImageBody(bodyJSON);
                        htMessage.setBody(body);
                    } else if ("2003".equals(data.getString("msgType"))) {
                        htMessage.setType(HTMessage.Type.VOICE);
                        HTMessageVoiceBody body = new HTMessageVoiceBody(bodyJSON);
                        htMessage.setBody(body);
                    } else if ("2004".equals(data.getString("msgType"))) {
                        htMessage.setType(HTMessage.Type.VIDEO);
                        HTMessageVideoBody body = new HTMessageVideoBody(bodyJSON);
                        htMessage.setBody(body);
                    } else if ("2005".equals(data.getString("msgType"))) {
                        htMessage.setType(HTMessage.Type.FILE);
                        HTMessageFileBody body = new HTMessageFileBody(bodyJSON);
                        htMessage.setBody(body);
                    } else if ("2006".equals(data.getString("msgType"))) {
                        htMessage.setType(HTMessage.Type.LOCATION);
                        HTMessageLocationBody body = new HTMessageLocationBody(bodyJSON);
                        htMessage.setBody(body);
                    }
                } catch (JSONException e) {
                }
                htMessage.setMsgId(data.getString("msgId"));
                htMessage.setFrom(data.getString("from"));
                htMessage.setTo(data.getString("to"));
                try {
                    JSONObject jsonObject1 = JSONObject.parseObject(data.getString("ext"));
                    htMessage.setAttributes(jsonObject1);
                } catch (JSONException e) {
                }
                htMessage.setTime(time);
                htMessage.setLocalTime(System.currentTimeMillis());
                return htMessage;

            } else if (jsonObject.getInteger("type") == 1000) {
                //CMD消息
//                JSONObject data = jsonObject.getJSONObject("data");
//                if (HTApp.getInstance().getUserId().equals(data.getString("from"))) {
//                    return;
//                }
//                CmdMessage cmdMessage = new CmdMessage(data, Long.parseLong(time));
//                Intent intent = new Intent(HTAction.ACTION_MESSAGE_CMD);
//                intent.putExtra("data", cmdMessage);
//                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);

            }
        } catch (Exception e) {
            LoggerUtils.e("msgString---->" + msgString1);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 发送禁言或者解除禁言的透传
     *
     * @param code    要传的action
     * @param groupId 发送的groupId
     */
    public void sendNoTalkOrCancleCmdMessage(int code, String groupId, String groupName) {
        JSONObject data = new JSONObject();
        data.put("adminId", HTApp.getInstance().getUsername());
        data.put("adminNick", HTApp.getInstance().getUserNick());
        data.put("groupId", groupId);
        data.put("groupName", groupName);
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("action", code);
        bodyJson.put("data", data);
        CmdMessage message = new CmdMessage();
        message.setChatType(ChatType.groupChat);
        message.setTo(groupId);
        message.setFrom(HTApp.getInstance().getUsername());
        message.setTime(System.currentTimeMillis());
        message.setMsgId(UUID.randomUUID().toString());
        message.setBody(bodyJson.toJSONString());
        HTClient.getInstance().chatManager().sendCmdMessage(message, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(long timeStamp) {
                LoggerUtils.d("----禁言或者不禁言透传发送成功!");
            }

            @Override
            public void onFailure() {
                LoggerUtils.d("----禁言或者不禁言透传发送失败!");
            }
        });
    }


    /**
     * 发送取消或者设置了管理员的透传
     *
     * @param userId
     * @param groupId
     */
    public  void sendCancleOrSetManagerCmdMsg(String userId, String groupId, int action) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        JSONObject object = new JSONObject();
        object.put("groupId", groupId);
        object.put("userId", userId);
        object.put("nick", UserManager.get().getMyNick());
        object.put("avatar", UserManager.get().getMyAvatar());
        jsonObject.put("data", object.toJSONString());
        CmdMessage customMessage = new CmdMessage();
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setFrom(HTApp.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(groupId);
        customMessage.setBody(jsonObject.toJSONString());
        customMessage.setChatType(ChatType.groupChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(long timeStamp) {

            }

            @Override
            public void onFailure() {

            }
        });
    }


    public void sendDeleteCMD(String userId){

        CmdMessage customMessage = new CmdMessage();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", 1003);
        JSONObject data = new JSONObject();
        data.put("userId",UserManager.get().getMyUserId());
        data.put("nick", UserManager.get().getMyNick());
        data.put("avatar",UserManager.get().getMyAvatar());
         jsonObject.put("data", data);
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setFrom(HTApp.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(userId);
        customMessage.setBody(jsonObject.toJSONString());
        customMessage.setChatType(ChatType.singleChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(long timeStamp) {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void downloadMsgFile(){}


}
