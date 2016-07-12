package com.fanxin.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.alibaba.fastjson.JSONObject;
import com.easemob.redpacketsdk.RedPacket;
import com.facebook.drawee.backends.pipeline.Fresco;
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
	private String time="";
	@Override
	public void onCreate() {
		MultiDex.install(this);
		super.onCreate();
        applicationContext = this;
        instance = this;
		OkHttpManager.init(instance);
		LocalUserUtil.init(instance);
        DemoHelper.getInstance().init(applicationContext);
		RedPacket.getInstance().initContext(applicationContext);
		Fresco.initialize(this);
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
	public String getTime(){
		return time;

	}
	public void setTime(String time){
		this.time=time;
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
