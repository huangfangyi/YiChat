/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htmessage.yichat.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.promeg.pinyinhelper.Pinyin;
import com.htmessage.update.data.UserManager;


public class User implements Parcelable {

    /**
     * initial letter for nickname
     */
    protected String initialLetter;

    protected User(Parcel in) {
        initialLetter = in.readString();
        avatar = in.readString();
        username = in.readString();
        userInfo = in.readString();
        nick = in.readString();
        remark=in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * avatar of the user
     */
    protected String avatar;
    private String username;
    protected String userInfo;
    private String nick;
    private String remark;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;
    public String getNick() {
        return TextUtils.isEmpty(remark) ? nick : remark;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }


    public User(JSONObject jsonObject) {
        this.userInfo = jsonObject.toJSONString();
        this.username=jsonObject.getString("userId");
        this.nick = jsonObject.getString("nick");
        this.avatar = jsonObject.getString("avatar");
        this.remark = UserManager.get().getUserRemark(username);
        if(!TextUtils.isEmpty(this.remark)){
            this.initialLetter=getInitialLetter(this.remark);
        }else {
            this.initialLetter=getInitialLetter(this.nick);
        }

    }

    public User(){

    }



    public String getInitialLetter() {

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

    public String getUserInfo() {

        return userInfo;

    }


    @Override
    public int hashCode() {
        return 17 * getUserId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof User)) {
            return false;
        }
        return getUserId().equals(((User) o).getUserId());
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(initialLetter);
        parcel.writeString(avatar);
        parcel.writeString(username);
        parcel.writeString(userInfo);
        parcel.writeString(nick);
        parcel.writeString(remark);
    }


    public static String getInitialLetter(String nick) {

        String letter = "#";
        if (!TextUtils.isEmpty(nick)) {
            letter = Pinyin.toPinyin(nick.toCharArray()[0]);
            letter = letter.toUpperCase().substring(0, 1);
            if ("0123456789".contains(letter) || !check(letter)) {
                letter = "#";
            }
        }
        return letter;
    }

    private static boolean check(String fstrData) {
        char c = fstrData.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }
}
