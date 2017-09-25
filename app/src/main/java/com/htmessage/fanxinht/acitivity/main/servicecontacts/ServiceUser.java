package com.htmessage.fanxinht.acitivity.main.servicecontacts;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.htmessage.fanxinht.utils.CommonUtils;


/**
 * 项目名称：PersonalTailor
 * 类描述：ServiceUser 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/2 14:16
 * 邮箱:814326663@qq.com
 */
@SuppressLint("ParcelCreator")
public class ServiceUser implements Parcelable {
    /**
     * initial letter for nickname
     */
    private String initialLetter;
    private String avatar;
    private String nick;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInitialLetter() {
        if (initialLetter == null) {
            CommonUtils.setServiceInitialLetter(this);
        }
        return initialLetter;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    private boolean isShow = false;

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
