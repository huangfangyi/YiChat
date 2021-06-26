package com.htmessage.sdk.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by huangfangyi on 2017/2/14.
 * qq 84543217
 */

public class HTMessageTextBody extends HTMessageBody {
    private String content;
    public HTMessageTextBody(JSONObject bodyJson) {
        super(bodyJson);
     }
    public HTMessageTextBody(String body) {
       super(body);
    }

    public HTMessageTextBody(){

    }

    public void setContent(String content){
        this.content=content;
        bodyJson.put("content",content);
    }

    public String getContent() {
        if(content==null){
            content=bodyJson.getString("content");
        }

        return content;
    }
}
