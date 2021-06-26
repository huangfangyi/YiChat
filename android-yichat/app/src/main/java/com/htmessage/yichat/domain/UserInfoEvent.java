package com.htmessage.yichat.domain;

/**
 * Created by huangfangyi on 2019/7/28.
 * qq 84543217
 */
public class UserInfoEvent {

    private String userId;
    private String nick;
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }


}
