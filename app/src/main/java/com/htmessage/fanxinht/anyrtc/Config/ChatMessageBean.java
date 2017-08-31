package com.htmessage.fanxinht.anyrtc.Config;

/**
 * Created by Skyline on 2016/8/3.
 */
public class ChatMessageBean {
    private String mCustomID;
    private String mCustomName;
    private String mCustomHeader;
    private String mMsgContent;

    public ChatMessageBean(String mCustomID, String mCustomName, String mCustomHeader, String mMsgContent) {
        this.mCustomID = mCustomID;
        this.mCustomName = mCustomName;
        this.mCustomHeader = mCustomHeader;
        this.mMsgContent = mMsgContent;
    }

    public String getmCustomID() {
        return mCustomID;
    }

    public void setmCustomID(String mCustomID) {
        this.mCustomID = mCustomID;
    }

    public String getmCustomName() {
        return mCustomName;
    }

    public void setmCustomName(String mCustomName) {
        this.mCustomName = mCustomName;
    }

    public String getmCustomHeader() {
        return mCustomHeader;
    }

    public void setmCustomHeader(String mCustomHeader) {
        this.mCustomHeader = mCustomHeader;
    }

    public String getmMsgContent() {
        return mMsgContent;
    }

    public void setmMsgContent(String mMsgContent) {
        this.mMsgContent = mMsgContent;
    }
}
