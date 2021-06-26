package com.htmessage.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by huangfangyi on 2017/1/17.
 * qq 84543217
 */

public class HTMessageBody implements Parcelable{
    public JSONObject bodyJson=new JSONObject();

    public HTMessageBody(JSONObject bodyJson){
        this.bodyJson=bodyJson;
    }
    public HTMessageBody(String  body){
        if(!TextUtils.isEmpty(body)) {
         //  new HTMessageBody(JSONObject.parseObject(body));
            bodyJson= JSONObject.parseObject(body);
        }
    }

    public HTMessageBody(){

    }



    public static final Creator<HTMessageBody> CREATOR = new Creator<HTMessageBody>() {
        @Override
        public HTMessageBody createFromParcel(Parcel in) {
            return new HTMessageBody(in);
        }

        @Override
        public HTMessageBody[] newArray(int size) {
            return new HTMessageBody[size];
        }
    };

    //本地存储的完整数据
    public String getLocalBody(){
         return bodyJson.toJSONString();
    }

    //用于通讯时,文件消息的本地地址不应该传输
    public String getXmppBody(){
        // JSONObject json= (JSONObject) bodyJson.clone();
        JSONObject json=new JSONObject();
        json.putAll(bodyJson);
         if(json.containsKey("localPath")){
             json.remove("localPath");
         }
        if(json.containsKey("localPathThumbnail")){

            json.remove("localPathThumbnail");
        }

        return json.toJSONString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(bodyJson.toJSONString());



    }

    protected HTMessageBody(Parcel in) {
      String  bodyJsonStr=in.readString();
        bodyJson= JSONObject.parseObject(bodyJsonStr);

    }


}
