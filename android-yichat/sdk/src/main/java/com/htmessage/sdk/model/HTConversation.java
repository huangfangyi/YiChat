package com.htmessage.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2016/11/25.
 * qq 84543217
 */

public class HTConversation implements Parcelable {

    private String userId;
    private int unReadCount = 0;
  //  private String lastMsgId;
    private long time=0;
    private ChatType chatType;
    private long topTimestamp=0;

    protected HTConversation(Parcel in) {
        userId = in.readString();
        unReadCount = in.readInt();
      //  lastMsgId = in.readString();
        time = in.readLong();
        topTimestamp=in.readLong();

    }

    public HTConversation() {

    }

    public static final Creator<HTConversation> CREATOR = new Creator<HTConversation>() {
        @Override
        public HTConversation createFromParcel(Parcel in) {
            return new HTConversation(in);
        }

        @Override
        public HTConversation[] newArray(int size) {
            return new HTConversation[size];
        }
    };

    public void setUserId(String userId) {

        this.userId = userId;

    }

    public String getUserId() {

        return userId;
    }

//    public void setLastMsgId(String lastMsgId) {
//
//        this.lastMsgId = lastMsgId;
//    }
//
//    public String getLastMsgId() {
//
//        return lastMsgId;
//    }

    public void setUnReadCount(int unReadCount) {

        this.unReadCount = unReadCount;

    }

    public int getUnReadCount() {

        return unReadCount;
    }


    public void setTime(long time) {

        this.time = time;

    }

    public long getTime() {

        return time;

    }

    public void setTopTimestamp(long topTimestamp){
        this.topTimestamp=topTimestamp;
    }

    public  long getTopTimestamp(){

        return topTimestamp;
    }

    public HTMessage getLastMessage() {

         HTMessage  htMessage=HTClient.getInstance().messageManager().getLastMessage(userId);
            if(htMessage!=null){
              return htMessage;
            }

        return null;
    }
    public List<HTMessage> getAllMessages(){
        List<HTMessage> messages=new ArrayList<>();
        if(userId!=null){
            messages= HTClient.getInstance().messageManager().getMessageList(userId);
        }
        return messages;
    }
    public List<HTMessage> loadMoreMsgFromDB(long timestamp,int pagesize){
        List<HTMessage> messages=new ArrayList<>();
        if(userId!=null){
            messages= HTClient.getInstance().messageManager().loadMoreMsgFromDB(userId,timestamp,pagesize);
        }
        return messages;
    }


    public ChatType getChatType() {
        return chatType;
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeInt(unReadCount);
     //   dest.writeString(lastMsgId);
        dest.writeLong(time);
        dest.writeLong(topTimestamp);

    }

    @Override
    public String toString() {
        return userId;
    }
}


