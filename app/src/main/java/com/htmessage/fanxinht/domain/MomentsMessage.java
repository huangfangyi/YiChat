package com.htmessage.fanxinht.domain;

/**
 * Created by huangfangyi on 2017/7/23.
 * qq 84543217
 */

public class MomentsMessage {
    int id; //数据库的标识id



    String userId;
    String userNick;
    String userAvatar;
    String content;
    String imageUrl;
    Type type;
    String mid;
    Status status;
    long time;

    public long getTime() {
        return time;
    }



    public void setTime(long time) {
        this.time = time;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public  enum  Type{
        GOOD,
        COMMENT,
        REPLY_COMMENT
    }

    public enum Status{
        UNREAD,
        READ

    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
