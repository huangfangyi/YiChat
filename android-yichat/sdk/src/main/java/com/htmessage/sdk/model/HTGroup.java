package com.htmessage.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by huangfangyi on 2016/12/21.
 * qq 84543217
 */

public class HTGroup implements Parcelable {
   protected   String groupId;
    protected String groupName;
    private String groupDesc;
    protected String owner;
    private long time;
    private String imgUrl;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    protected HTGroup(Parcel in) {
        groupId = in.readString();
        groupName = in.readString();
        groupDesc = in.readString();
        owner = in.readString();
        time = in.readLong();
        imgUrl = in.readString();
    }

    public static final Creator<HTGroup> CREATOR = new Creator<HTGroup>() {
        @Override
        public HTGroup createFromParcel(Parcel in) {
            return new HTGroup(in);
        }

        @Override
        public HTGroup[] newArray(int size) {
            return new HTGroup[size];
        }
    };

    public HTGroup() {

    }

    public static HTGroup getHTGroup(JSONObject jsonObject) {
        HTGroup htGroup = new HTGroup();
        htGroup.setGroupDesc(jsonObject.getString("desc"));
        htGroup.setGroupName(jsonObject.getString("name"));
        htGroup.setGroupId(jsonObject.getString("gid"));
        htGroup.setOwner(jsonObject.getString("creator"));
        htGroup.setTime(jsonObject.getLong("create_date"));
        htGroup.setImgUrl(jsonObject.getString("imgurlde"));
        return htGroup;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;

    }

    public String getImgUrl() {
        return imgUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(groupId);
        parcel.writeString(groupName);
        parcel.writeString(groupDesc);
        parcel.writeString(owner);
        parcel.writeLong(time);
        parcel.writeString(imgUrl);


    }

    @Override
    public String toString() {
        return groupId;
    }
}
