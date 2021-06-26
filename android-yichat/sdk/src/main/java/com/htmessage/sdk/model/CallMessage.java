package com.htmessage.sdk.model;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.SDKConstant;

/**
 * Created by huangfangyi on 2017/4/9.
 * qq 84543217
 */

public class CallMessage extends CmdMessage {

    @Override
    public String toXmppMessage() {
        JSONObject dataJson = new JSONObject();

        dataJson.put("from", getFrom());
        dataJson.put("to", getTo());
        dataJson.put("msgId", getMsgId());
        int chatTypeInt = 1;
        if (getChatType() == ChatType.groupChat) {
            chatTypeInt = 2;
        }
        dataJson.put("chatType", chatTypeInt);
        dataJson.put("body", getBody());
        JSONObject xmppJson = new JSONObject();
        xmppJson.put(SDKConstant.FX_MSG_KEY_TYPE, SDKConstant.TYPT_CALL);
        xmppJson.put(SDKConstant.FX_MSG_KEY_DATA, dataJson);

        return xmppJson.toJSONString();
    }
    public CallMessage(JSONObject bodyJson, long time) {
      super(bodyJson,time);
    }

    public CallMessage(){
        super();
    }


}
