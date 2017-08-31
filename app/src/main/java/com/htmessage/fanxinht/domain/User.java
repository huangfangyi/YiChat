/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htmessage.fanxinht.domain;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.utils.CommonUtils;


@SuppressLint("ParcelCreator")
public class User implements Parcelable {
    
    /**
     * initial letter for nickname
     */
	protected String initialLetter;

	public String getUsername() {
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

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public User(String username){
	    this.username = username;
	}

	public String getInitialLetter() {
	    if(initialLetter == null){
            CommonUtils.setUserInitialLetter(this);
        }
		return initialLetter;
	}

	public void setInitialLetter(String initialLetter) {
		this.initialLetter = initialLetter;
	}


	public String getAvatar() {
		if(!TextUtils.isEmpty(avatar)){
			if (!avatar.contains("http")){
				avatar = HTConstant.URL_AVATAR+avatar;
			}
		}
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
	public String getUserInfo(){

		return userInfo;

	}
	public void setUserInfo(String userInfo){
		this.userInfo=userInfo;
	}

    @Override
	public int hashCode() {
		return 17 * getUsername().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof User)) {
			return false;
		}
		return getUsername().equals(((User) o).getUsername());
	}

	@Override
	public String toString() {
		return nick == null ? username : nick;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {

	}
}
