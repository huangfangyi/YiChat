package com.htmessage.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.SDKConstant;
import com.htmessage.sdk.manager.HTPreferenceManager;

import java.util.UUID;

/**
 * Created by huangfangyi on 2016/9/13.
 * qq 84543217
 */
public class HTMessage implements Parcelable ,Comparable<HTMessage> {

    private String from;
    private String to;
    private long time;
    private Status status;
    private long localTime;
    private String msgId;
    private ChatType chatType;
    private Direct direct;
    private Type type;
    private JSONObject attributes;
    private HTMessageBody body;
    //private String ext;

    public HTMessage(Parcel in) {


        from = in.readString();
        to = in.readString();
        time = in.readLong();
        int statusInt = in.readInt();

        localTime = in.readLong();
        msgId = in.readString();
        int chatTypeInt = in.readInt();

        int directInt = in.readInt();
        int typeInt = in.readInt();
        String bodyString = in.readString();
        body = new HTMessageBody(bodyString);

        String attributesTemp = in.readString();
        attributes= JSONObject.parseObject(attributesTemp);

        if (statusInt == Status.CREATE.ordinal()) {
            this.status = Status.CREATE;

        } else if (statusInt == Status.SUCCESS.ordinal()) {
            this.status = Status.SUCCESS;

        } else if (statusInt == Status.INPROGRESS.ordinal()) {
            this.status = Status.INPROGRESS;

        } else if (statusInt == Status.FAIL.ordinal()) {
            this.status = Status.FAIL;

        }else if (statusInt == Status.READ.ordinal()) {
            this.status = Status.READ;

        }else if (statusInt == Status.ACKED.ordinal()) {
            this.status = Status.ACKED;

        }
        if (chatTypeInt == ChatType.singleChat.ordinal()) {
            chatType = ChatType.singleChat;
        } else if (chatTypeInt == ChatType.groupChat.ordinal()) {
            chatType = ChatType.groupChat;
        }

        if (directInt == Direct.SEND.ordinal()) {
            direct = Direct.SEND;
        } else if (directInt == Direct.RECEIVE.ordinal()) {
            direct = Direct.RECEIVE;
        }


        if (typeInt == Type.TEXT.ordinal()) {

            type = Type.TEXT;
        } else if (typeInt == Type.IMAGE.ordinal()) {

            type = Type.IMAGE;
        } else if (typeInt == Type.VOICE.ordinal()) {

            type = Type.VOICE;
        } else if (typeInt == Type.VIDEO.ordinal()) {

            type = Type.VIDEO;
        } else if (typeInt == Type.FILE.ordinal()) {

            type = Type.FILE;
        } else if (typeInt == Type.LOCATION.ordinal()) {

            type = Type.LOCATION;
        }

        // Log.d("attributes1---->",attributes.toJSONString());
    }

    public static final Creator<HTMessage> CREATOR = new Creator<HTMessage>() {
        @Override
        public HTMessage createFromParcel(Parcel in) {
            return new HTMessage(in);
        }

        @Override
        public HTMessage[] newArray(int size) {
            return new HTMessage[size];
        }
    };

    public HTMessage() {

    }


    public static HTMessage createTextSendMessage(String chatTo, String content) {
        HTMessage htMessage = commonSet(chatTo);

        htMessage.setType(Type.TEXT);
        HTMessageTextBody htMessageTextBody = new HTMessageTextBody();
        htMessageTextBody.setContent(content);
        htMessage.setBody(htMessageTextBody);
        return htMessage;
    }

    public static HTMessage createImageSendMessage(String chatTo, String filePath, String size) {
        HTMessage htMessage = commonSet(chatTo);
        htMessage.setType(Type.IMAGE);
        HTMessageImageBody htMessageImageBody = new HTMessageImageBody();
        htMessageImageBody.setLocalPath(filePath);
        htMessageImageBody.setSize(size);
        htMessageImageBody.setFileName(filePath.substring(filePath.lastIndexOf("/") + 1));
        htMessage.setBody(htMessageImageBody);
        return htMessage;
    }

    public static HTMessage createVoiceSendMessage(String chatTo, String filePath, int length) {
        HTMessage htMessage = commonSet(chatTo);
        htMessage.setType(Type.VOICE);
        HTMessageVoiceBody htMessageVoiceBody = new HTMessageVoiceBody();
        htMessageVoiceBody.setLocalPath(filePath);
        htMessageVoiceBody.setAudioDuration(length);
        htMessageVoiceBody.setFileName(filePath.substring(filePath.lastIndexOf("/") + 1));
        htMessage.setBody(htMessageVoiceBody);
        return htMessage;
    }

    public static HTMessage createVideoSendMessage(String chatTo, String videoPath, String thumbPath, int duration) {
        HTMessage htMessage = commonSet(chatTo);
        htMessage.setType(Type.VIDEO);
        HTMessageVideoBody htMessageVideoBody = new HTMessageVideoBody();
        htMessageVideoBody.setFileName(videoPath.substring(videoPath.lastIndexOf("/") + 1));
        htMessageVideoBody.setLocalPath(videoPath);
        htMessageVideoBody.setLocalPathThumbnail(thumbPath);
        htMessageVideoBody.setVideoDuration(duration);
        htMessage.setBody(htMessageVideoBody);
        return htMessage;
    }

    public static HTMessage createVideoSendMessage(String chatTo, String videoPath, String thumbPath, int duration,String size) {
        HTMessage htMessage = commonSet(chatTo);
        htMessage.setType(Type.VIDEO);
        HTMessageVideoBody htMessageVideoBody = new HTMessageVideoBody();
        htMessageVideoBody.setFileName(videoPath.substring(videoPath.lastIndexOf("/") + 1));
        htMessageVideoBody.setLocalPath(videoPath);
        htMessageVideoBody.setLocalPathThumbnail(thumbPath);
        htMessageVideoBody.setVideoDuration(duration);
        htMessageVideoBody.setSize(size);
        htMessage.setBody(htMessageVideoBody);
        return htMessage;
    }

    public static HTMessage createLocationSendMessage(String chatTo, double latitude, double longitude, String locationAddress, String thumbailPath) {
        HTMessage htMessage = commonSet(chatTo);
        htMessage.setType(Type.LOCATION);
        HTMessageLocationBody htMessageLocationBody = new HTMessageLocationBody();
        htMessageLocationBody.setLocalPath(thumbailPath);
        htMessageLocationBody.setAddress(locationAddress);
        htMessageLocationBody.setFileName(thumbailPath.substring(thumbailPath.lastIndexOf("/") + 1));
        htMessageLocationBody.setLatitude(latitude);
        htMessageLocationBody.setLongitude(longitude);

        htMessage.setBody(htMessageLocationBody);
        return htMessage;
    }
    public static HTMessage createFileSendMessage(String chatTo, String localPath, long  fileSize) {
        HTMessage htMessage = commonSet(chatTo);
        htMessage.setType(Type.FILE);
        HTMessageFileBody htMessageFileBody=new HTMessageFileBody();
        htMessageFileBody.setLocalPath(localPath);
        htMessageFileBody.setFileName(localPath.substring(localPath.lastIndexOf("/") + 1));
        htMessageFileBody.setSize(fileSize);
        htMessage.setBody(htMessageFileBody);
        return htMessage;
    }


    private static HTMessage commonSet(String chatTo) {
        HTMessage htMessage = new HTMessage();
        htMessage.setTo(chatTo);
        long timeTemp = System.currentTimeMillis();
        htMessage.setTime(timeTemp);
        htMessage.setLocalTime(timeTemp);
        htMessage.setFrom(HTPreferenceManager.getInstance().getUser().getUsername());
        htMessage.setMsgId(UUID.randomUUID().toString());
        htMessage.setDirect(Direct.SEND);
        htMessage.setChatType(ChatType.singleChat);
        htMessage.setStatus(Status.CREATE);
        return htMessage;

    }


//    public HTMessage(String from,String to,long time,Status status,long localTime,String msgId,ChatType chatType,Direct direct,Type type,String body) {
//        this.from=from;
//        this.to=to;
//        this.time=time;
//        this.status=status;
//        this.localTime=localTime;
//        this.msgId=msgId;
//        this.chatType=chatType;
//        this.direct=direct;
//        this.type=type;
//        this.body=body;
//       // this.attribute=attribute;
//    }

    public void setAttribute(String key, String value) {

        getAttributes().put(key, value);

    }

    public void setAttributes(JSONObject attributes) {

        this.attributes = attributes;
    }


    public String getStringAttribute(String key) {
        String value = null;
        if (getAttributes().containsKey(key) && getAttributes().get(key) instanceof String) {
            value = getAttributes().getString(key);
        }
        return value;
    }

    public JSONObject getJSONObjectAtrribute(String key) {

        JSONObject jsonObject = null;
        if (getAttributes().containsKey(key) && getAttributes().get(key) instanceof JSONObject) {

            jsonObject = getAttributes().getJSONObject(key);
        }
        return jsonObject;

    }

    public JSONArray getJSONArrayAtrribute(String key) {

        JSONArray jsonArray = null;
        if (getAttributes().containsKey(key) && getAttributes().get(key) instanceof JSONArray) {

            jsonArray = getAttributes().getJSONArray(key);
        }
        return jsonArray;

    }

    public void setJSONObjectAtrribute(String key, JSONObject value) {
        getAttributes().put(key, value);
    }

    public void setJSONArrayAttribute(String key, JSONArray value) {
        getAttributes().put(key, value);
    }


    public boolean getBooleanAttribute(String key, boolean defaultValue) {
        if (getAttributes().containsKey(key) && getAttributes().get(key) instanceof Boolean) {
            defaultValue = getAttributes().getBooleanValue(key);
        }
        return defaultValue;
    }

    public JSONObject getAttributes()  {
        if (attributes  == null) {
            attributes=new JSONObject();
        }
        return attributes;
    }

    public int getIntAttribute(String key, int defaultInt) {
        if (getAttributes().containsKey(key) && getAttributes().get(key) instanceof Integer) {
            defaultInt = getAttributes().getIntValue(key);
        }else if(getAttributes().containsKey(key) && getAttributes().get(key) instanceof String){
            try {
                defaultInt=Integer.parseInt( (String) getAttributes().get(key));
            }catch (NumberFormatException e){
                e.printStackTrace();
            }

        }
        return defaultInt;
    }


    public String getMsgId() {

        return msgId;
    }


    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setLocalTime(long localTime) {

        this.localTime = localTime;
    }

    public long getLocalTime() {

        return localTime;
    }

    public long getTime() {

        return time;
    }


    public String getFrom() {

        return from;

    }

    public String getUsername() {

        if (direct == Direct.RECEIVE) {
            if (chatType == ChatType.groupChat) {
                return to;

            } else {
                return from;
            }

        } else {
            return to;
        }
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

    public void setType(Type type) {

        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setBody(HTMessageBody body) {
        this.body = body;
    }


    public HTMessage(JSONObject dataJson, Direct direct, Status status, long localTime, long time) {
        from = dataJson.getString("from");
        to = dataJson.getString("to");
        body = new HTMessageBody(dataJson.getJSONObject("body"));
        int chatTypeInt = dataJson.getIntValue("chatType");
        switch (chatTypeInt) {
            case 1:
                chatType = ChatType.singleChat;
                break;
            case 2:
                chatType = ChatType.groupChat;
                break;
            default:
                chatType = ChatType.singleChat;
                break;
        }
        int typeInt = dataJson.getIntValue("msgType");
        switch (typeInt) {
            case 2001:
                type = Type.TEXT;
                break;
            case 2002:
                type = Type.IMAGE;
                break;
            case 2003:
                type = Type.VOICE;
                break;
            case 2004:
                type = Type.VIDEO;
                break;
            case 2005:
                type = Type.FILE;
                break;
            case 2006:
                type = Type.LOCATION;
                break;
            default:
                type = Type.TEXT;
                break;
        }
        if (dataJson.containsKey("ext")) {
            attributes = dataJson.getJSONObject("ext");
        }
        msgId = dataJson.getString("msgId");
        this.time = time;
        this.localTime = localTime;
        this.status = status;
        this.direct = direct;
    }

    public HTMessageBody getBody() {
        if (type == Type.TEXT) {
         //   Log.d("body.()---->", body.getLocalBody());
            return new HTMessageTextBody(body.getLocalBody());
        } else if (type == Type.IMAGE) {
            return new HTMessageImageBody(body.getLocalBody());
        } else if (type == Type.VOICE) {
            return new HTMessageVoiceBody(body.getLocalBody());
        } else if (type == Type.VIDEO) {
            return new HTMessageVideoBody(body.getLocalBody());
        } else if (type == Type.LOCATION) {
            return new HTMessageLocationBody(body.getLocalBody());
        } else if (type == Type.FILE) {
            return new HTMessageFileBody(body.getLocalBody());
        }


        return body;
    }

//    public JSONObject getBodyJSON() {
//        //  Log.d("body--->",body);
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = JSONObject.parseObject(body);
//        } catch (NullPointerException | JSONException e) {
//
//            throw new RuntimeException("msg　body error");
//        }
//        return jsonObject;
//    }

//    public JSONObject getAttributeJson() {
//        //  Log.d("body--->",body);
//        JSONObject jsonObject = null;
//        if(attribute!=null){
//            try {
//                jsonObject = JSONObject.parseObject(attribute);
//            } catch (NullPointerException | JSONException e) {
//
//                throw new RuntimeException("msg　body error");
//            }
//        }
//
//        return jsonObject;
//    }


    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(from);
        dest.writeString(to);
        dest.writeLong(time);
        dest.writeInt(status.ordinal());
        dest.writeLong(localTime);
        dest.writeString(msgId);
        dest.writeInt(chatType.ordinal());
        dest.writeInt(direct.ordinal());
        dest.writeInt(type.ordinal());
        dest.writeString(body.getLocalBody());
        if (getAttributes() != null && getAttributes().size() > 0) {
            dest.writeString(getAttributes().toJSONString());
        }
     //   Log.d("attributes2---->", attributes.toJSONString());

    }

    @Override
    public int compareTo(@NonNull HTMessage o) {
        if(this.getTime()>o.getTime()){
            return 1;
        }

        return -1;
    }


    public enum Type {
        IMAGE, TEXT, VOICE, VIDEO, FILE, LOCATION;
    }


    public void setChatType(ChatType chatType) {
        this.chatType = chatType;

    }

    public ChatType getChatType() {
        return chatType;
    }


    public void setStatus(Status status) {

        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public Direct getDirect() {

        return direct;
    }

    public void setDirect(Direct direct) {
        this.direct = direct;
    }

    public enum Status {
        SUCCESS,
        FAIL,
        INPROGRESS,
        CREATE,
        ACKED,
        READ;

    }

    public enum Direct {
        RECEIVE,
        SEND;
    }

    @Override
    public String toString() {
        return msgId;
    }


    public String toXmppMessageBody() {
        int chatTypeInt = 1;
        if (chatType == ChatType.singleChat) {

            chatTypeInt = 1;
        } else if (chatType == ChatType.groupChat) {

            chatTypeInt = 2;
        }
        int typeInt = 2001;
        if (type == Type.TEXT) {

            typeInt = 2001;
        } else if (type == Type.IMAGE) {
            typeInt = 2002;
        } else if (type == Type.VOICE) {
            typeInt = 2003;
        } else if (type == Type.VIDEO) {
            typeInt = 2004;
        } else if (type == Type.FILE) {
            typeInt = 2005;
        } else if (type == Type.LOCATION) {
            typeInt = 2006;
        }
        JSONObject data = new JSONObject();
        data.put("from", from);
        data.put("to", to);
        //TODO 各种消息类型的body本地存储和发送时是有差别的,比如文件消息等,不应发送本地的文件地址
        data.put("body", JSONObject.parse(body.getXmppBody()));
        data.put("chatType", chatTypeInt);
        data.put("msgType", typeInt);
        if (attributes != null) {
            data.put("ext", attributes );
        }
        data.put("msgId", msgId);
        JSONObject bodyJson = new JSONObject();
        //通讯消息的type为2000,透传消息为1000
        bodyJson.put("type", SDKConstant.TYPE_MESSGAE_HT);
        bodyJson.put("data", data);
        return bodyJson.toJSONString();
    }
}
