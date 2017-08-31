package com.htmessage.fanxinht.anyrtc.Config;

/**
 * Created by Skyline on 2016/7/28.
 */
public class LiveItemBean {
    private String mHosterId;
    private String mRtmpPushUrl;
    private String mRtmpPullUrl;
    private String mHlsUrl;
    private String mLiveTopic;
    private String mAnyrtcId;
    private int mMemNumber;
    private String headerUrl;
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public String getmAnyrtcId() {
        return mAnyrtcId;
    }

    public void setmAnyrtcId(String mAnyrtcId) {
        this.mAnyrtcId = mAnyrtcId;
    }

    public String getmHlsUrl() {
        return mHlsUrl;
    }

    public void setmHlsUrl(String mHlsUrl) {
        this.mHlsUrl = mHlsUrl;
    }

    public String getmHosterId() {
        return mHosterId;
    }

    public void setmHosterId(String mHosterId) {
        this.mHosterId = mHosterId;
    }

    public String getmLiveTopic() {
        return mLiveTopic;
    }

    public void setmLiveTopic(String mLiveTopic) {
        this.mLiveTopic = mLiveTopic;
    }

    public String getmRtmpPushUrl() {
        return mRtmpPushUrl;
    }

    public void setmRtmpPushUrl(String mRtmpPushUrl) {
        this.mRtmpPushUrl = mRtmpPushUrl;
    }

    public String getmRtmpPullUrl() {
        return mRtmpPullUrl;
    }

    public void setmRtmpPullUrl(String mRtmpPullUrl) {
        this.mRtmpPullUrl = mRtmpPullUrl;
    }

    public int getmMemNumber() {
        return mMemNumber;
    }

    public void setmMemNumber(int mMemNumber) {
        this.mMemNumber = mMemNumber;
    }
}
