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
package com.fanxin.huangfangyi.main.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.utils.OKHttpUtils;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.huangfangyi.main.widget.pull.SwipyRefreshLayout;
import com.fanxin.huangfangyi.ui.BaseActivity;
import java.util.ArrayList;
import java.util.List;

public class NearPeopleActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener,SwipyRefreshLayout.OnRefreshListener{

	private final static String TAG = NearPeopleActivity.class.getSimpleName();
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	static BDLocation lastLocation = null;
	public static NearPeopleActivity instance = null;
	private BaiduSDKReceiver mBaiduReceiver;

	private ListView listView;//展示列表
	private SwipyRefreshLayout srl_fresh;//下拉刷新
	private int currentpage = 1;//默认为1
	private int pagesize = 20;//默认显示20行
	private final int TOP_REFRESH = 1;//下拉刷新
	private final int BOTTOM_REFRESH = 2;//上拉加载更多

	private ImageView iv_back,iv_camera;
	private TextView tv_title;
	private RelativeLayout titleBar;



	public class BaiduSDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			String st1 = getResources().getString(com.hyphenate.easeui.R.string.Network_error);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				
				String st2 = getResources().getString(com.hyphenate.easeui.R.string.please_check);
				Toast.makeText(instance, st2, Toast.LENGTH_SHORT).show();
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(instance, st1, Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		//initialize SDK with context, should call this before setContentView
        SDKInitializer.initialize(DemoApplication.getInstance());
		setContentView(R.layout.activity_near_people);
		initView();
		initData();
		setOnClick();
	}

	private void setOnClick() {
		iv_back.setOnClickListener(this);
		srl_fresh.setOnRefreshListener(this);
		listView.setOnItemClickListener(this);
	}

	private void initView() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_camera = (ImageView) findViewById(R.id.iv_camera);
		tv_title = (TextView) findViewById(R.id.tv_title);
		titleBar = (RelativeLayout) findViewById(R.id.title);
		listView = (ListView) findViewById(R.id.lv_near_people);
		srl_fresh = (SwipyRefreshLayout) findViewById(R.id.srl_refresh);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.iv_back:
				back(v);
				break;
		}
	}
	public void initData() {
		iv_camera.setVisibility(View.GONE);
		this.tv_title.setText("附近的人");
		showMapWithLocationClient();
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mBaiduReceiver = new BaiduSDKReceiver();
		registerReceiver(mBaiduReceiver, iFilter);
	}
	private void showMapWithLocationClient() {
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// open gps
		// option.setCoorType("bd09ll"); 
		// Johnson change to use gcj02 coordination. chinese national standard
		// so need to conver to bd09 everytime when draw on baidu map
		option.setCoorType("gcj02");
		option.setScanSpan(30000);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
	}

	@Override
	protected void onPause() {
		if (mLocClient != null) {
			mLocClient.stop();
		}
		super.onPause();
		lastLocation = null;
	}

	@Override
	protected void onResume() {
		if (mLocClient != null) {
			mLocClient.start();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null)
			mLocClient.stop();
		unregisterReceiver(mBaiduReceiver);
		super.onDestroy();
	}

	/**
	 * format new location to string and show on screen
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.d(TAG, "On location change received:" + location);
			if (location == null) {
				Toast.makeText(NearPeopleActivity.this, "无法获取到您的位置", Toast.LENGTH_SHORT).show();
				return;
			}
			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
					Log.d(TAG, "same location, skip refresh");
					// mMapView.refresh(); //need this refresh?
					return;
				}
			}
				lastLocation = location;
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
			//附近的人及雷达加好友界面及基本功能实现,  网络请求数据未实现 ,其中雷达加好友使用模拟数据展示,附近的人未展示
			Toast.makeText(NearPeopleActivity.this, "地址:"+location.getAddrStr()+"\n"+"经度:"+latitude+"\n"+"纬度:"+longitude, Toast.LENGTH_SHORT).show();
				Log.d(TAG, "addr:" + location.getAddrStr());
				//TODO 获取到经纬度后 请求服务器  查询附近的人 由于没有借口 网络未实现 数据未实现
				Log.d(TAG,"latitude:"+latitude+"-----longitude:"+longitude);
				getNearPeople(latitude,longitude);
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}
	private void getNearPeople(double latitude,double longitude ){
		List<Param> params = new ArrayList<>();
		params.add(new Param("currentpage", String.valueOf(currentpage )));
		params.add(new Param("pagesize", String.valueOf(pagesize)));
		params.add(new Param("latitude",String.valueOf(latitude)));
		params.add(new Param("longitude",String.valueOf(longitude)));
		new OKHttpUtils(NearPeopleActivity.this).post(params, FXConstant.HOST, new OKHttpUtils.HttpCallBack() {
			@Override
			public void onResponse(JSONObject jsonObject) {
//				Toast.makeText(NearPeopleActivity.this, "获取附近好友失败", Toast.LENGTH_SHORT).show();
				listView.setBackgroundResource(R.drawable.empty);
			}

			@Override
			public void onFailure(String errorMsg) {
//				Toast.makeText(NearPeopleActivity.this, "获取附近好友失败", Toast.LENGTH_SHORT).show();
				listView.setBackgroundResource(R.drawable.empty);
			}
		});
		srl_fresh.setRefreshing(false);
	}
	@Override
	public void onRefresh(int index) {
		dataOption(TOP_REFRESH);
	}

	@Override
	public void onLoad(int index) {
		dataOption(BOTTOM_REFRESH);
	}
	private void dataOption(int option){
		switch (option) {
			case TOP_REFRESH:
				//下拉刷新
				currentpage =1;
				break;
			case BOTTOM_REFRESH:
				//上拉加载更多
				currentpage++;
				break;
		}
		//重新请求数据
		getNearPeople(lastLocation.getLatitude(),lastLocation.getLongitude());
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}
}
