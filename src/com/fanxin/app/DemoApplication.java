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
package com.fanxin.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.alibaba.fastjson.JSONObject;
import com.easemob.redpacketsdk.RedPacket;
import com.fanxin.app.main.db.TopUser;
import com.fanxin.app.main.db.TopUserDao;
import com.fanxin.app.main.utils.LocalUserUtil;
import com.fanxin.app.main.utils.OkHttpManager;

import java.util.ArrayList;
import java.util.Map;


public class DemoApplication extends Application {

	public static Context applicationContext;
	private static DemoApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";
	public static String currentUserNick = "";
	private JSONObject userJson;
	private  Map<String, TopUser> topUsers;
	@Override
	public void onCreate() {
		MultiDex.install(this);
		super.onCreate();
        applicationContext = this;
        instance = this;
		LocalUserUtil.init(instance);
        DemoHelper.getInstance().init(applicationContext);
		RedPacket.getInstance().initContext(applicationContext);
		OkHttpManager.init(instance);
	}

	public static DemoApplication getInstance() {
		return instance;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	public  void setUserJson( JSONObject userJson){

		this.userJson=userJson;
		LocalUserUtil.getInstance().setUserJson(userJson);

	}
	public  JSONObject getUserJson(){
		if(userJson==null){
			userJson=LocalUserUtil.getInstance().getUserJson();
		}
		return  userJson;
	}

	/**
	 * 获取置顶列表
	 */
	public Map<String, TopUser> getTopUserList() {
		if(topUsers==null){
			TopUserDao dao = new TopUserDao(instance);
			topUsers=dao.getTopUserList();
		}
		return topUsers;
	}
	/*
	* 设置置顶列表
	* */
	public void saveTopUserList( Map<String, TopUser> topUsers) {
		this.topUsers=topUsers;
 		TopUserDao dao = new TopUserDao(instance);
		dao.saveTopUserList(new ArrayList<TopUser>(topUsers.values()));
	}

}
