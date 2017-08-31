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
package com.htmessage.fanxinht.acitivity.chat.location;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
 import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.htmessage.fanxinht.R;

import com.htmessage.fanxinht.utils.MapUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BaiduMapActivity extends Activity {

	private final static String TAG = "map";
	static MapView mMapView = null;
 	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();

	Button sendButton = null;
	private String fromLat, fromLng;
	private String fromAddress;
	private String type;
	EditText indexText = null;
	int index = 0;
	// LocationData locData = null;
	static BDLocation lastLocation = null;
	public static BaiduMapActivity instance = null;
	ProgressDialog progressDialog;
	private BaiduMap mBaiduMap;
	
	private LocationMode mCurrentMode;
	private InfoWindow window;

	
	public class BaiduSDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			String st1 = getResources().getString(R.string.Network_error);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				
				String st2 = getResources().getString(R.string.please_check);
				Toast.makeText(instance, st2, Toast.LENGTH_SHORT).show();
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(instance, st1, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private BaiduSDKReceiver mBaiduReceiver;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		//initialize SDK with context, should call this before setContentView
        SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_baidumap);
		mMapView = (MapView) findViewById(R.id.bmapView);
		sendButton = (Button) findViewById(R.id.btn_location_send);
		Intent intent = getIntent();
		double latitude = intent.getDoubleExtra("latitude", 0);
		mCurrentMode = LocationMode.NORMAL;
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		initMapView();
		if (latitude == 0) {
			mMapView = new MapView(this, new BaiduMapOptions());
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
							mCurrentMode, true, null));
			showMapWithLocationClient();
			sendButton.setVisibility(View.VISIBLE);
		} else {
			sendButton.setVisibility(View.GONE);
			double longtitude = intent.getDoubleExtra("longitude", 0);
			String address = intent.getStringExtra("address");
			type = intent.getStringExtra("type");
			LatLng p = new LatLng(latitude, longtitude);
			mMapView = new MapView(this,
					new BaiduMapOptions().mapStatus(new MapStatus.Builder()
							.target(p).build()));
			showMap(latitude, longtitude, address);
		}
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mBaiduReceiver = new BaiduSDKReceiver();
		registerReceiver(mBaiduReceiver, iFilter);
	}

	private void showMap(final double latitude,final double longtitude,final String address) {
		sendButton.setVisibility(View.GONE);
		LatLng llA = new LatLng(latitude, longtitude);
		CoordinateConverter converter= new CoordinateConverter();
		converter.coord(llA);
		converter.from(CoordinateConverter.CoordType.COMMON);
		LatLng convertLatLng = converter.convert();
		BitmapDescriptor resource = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(resource)
				.zIndex(4).draggable(true);
		mBaiduMap.addOverlay(ooA);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
		mBaiduMap.animateMapStatus(u);
		if (!TextUtils.isEmpty(type)){
			mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
 					ShowInfowindow(marker,mMapView,fromLat,fromLng,fromAddress,String.valueOf(latitude),String.valueOf(longtitude),address);
					return true;
				}
			});
		}
	}
	private  void ShowInfowindow(Marker marker, final MapView mMapView, final String fromLat, final String fromLng, final String fromAddress, final String latitude, final String longtitude, final String address){
		//将marker所在的经纬度的信息转化成屏幕上的坐标
		final LatLng ll = marker.getPosition();
		LinearLayout baidumap_infowindow = (LinearLayout) LayoutInflater.from (BaiduMapActivity.this).inflate (R.layout.diolag_infowindown, null);
		TextView tv_address = (TextView) baidumap_infowindow.findViewById(R.id.tv_address);
		tv_address.setText(address);
		window = new InfoWindow(BitmapDescriptorFactory.fromView(baidumap_infowindow), ll, -47, new InfoWindow.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick() {
				showCityPup(mMapView,fromLat,fromLng,fromAddress,String.valueOf(latitude),String.valueOf(longtitude),address);
				//隐藏InfoWindow
				mBaiduMap.hideInfoWindow();
			}
		});
		//显示InfoWindow
		mBaiduMap.showInfoWindow(window);
	}



	private void showMapWithLocationClient() {
		String str1 = getResources().getString(R.string.Making_sure_your_location);
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(str1);

		progressDialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface arg0) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Log.d("map", "cancel retrieve location");
				finish();
			}
		});

		progressDialog.show();

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
		mMapView.onPause();
		if (mLocClient != null) {
			mLocClient.stop();
		}
		super.onPause();
		lastLocation = null;
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		if (mLocClient != null) {
	   	mLocClient.start();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null)
			mLocClient.stop();
		mMapView.onDestroy();
		unregisterReceiver(mBaiduReceiver);
		super.onDestroy();
	}
	private void initMapView() {
		mMapView.setLongClickable(true);
	}

	/**
	 * format new location to string and show on screen
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			Log.d("map", "On location change received:" + location);
			Log.d("map", "addr:" + location.getAddrStr());
			sendButton.setEnabled(true);
			if (progressDialog != null) {
				progressDialog.dismiss();
			}

			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
					Log.d("map", "same location, skip refresh");
					// mMapView.refresh(); //need this refresh?
					return;
				}
			}
			lastLocation = location;
			mBaiduMap.clear();
			LatLng llA = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
			CoordinateConverter converter= new CoordinateConverter();
			converter.coord(llA);
			converter.from(CoordinateConverter.CoordType.COMMON);
			LatLng convertLatLng = converter.convert();
			BitmapDescriptor resource = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
			OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(resource)
					.zIndex(4).draggable(true);
			mBaiduMap.addOverlay(ooA);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
			mBaiduMap.animateMapStatus(u);
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}



	public void back(View v) {
		finish();
	}

	public void sendLocation(View view) {
		final ProgressDialog progressDialog=new ProgressDialog(BaiduMapActivity.this);
		progressDialog.setMessage(getString(R.string.are_doing));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		Display display=getWindowManager().getDefaultDisplay();
		Point point=new Point();
		display.getSize(point);
		int width=point.x;
		int hight=point.y;
		Rect rect=new Rect(0,(hight/2)-(width/4)-180,width,(hight/2)+(width/4)-180);
		mBaiduMap.snapshotScope(rect, new BaiduMap.SnapshotReadyCallback() {
			@Override
			public void onSnapshotReady(Bitmap bitmap) {

				File file1 = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp/");
				if(!file1.exists()){
					file1.mkdirs();
				}
				File file=new File(file1.getAbsolutePath().toString()+"/"+System.currentTimeMillis()+".png");
				FileOutputStream out;
				try {
					out = new FileOutputStream(file);
					if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
						out.flush();
						out.close();
					}
                    BaiduMapActivity.this.runOnUiThread(new Runnable() {
					   @Override
					   public void run() {
						   progressDialog.dismiss();
					   }
				   });
					Intent intent = BaiduMapActivity.this.getIntent();
					intent.putExtra("latitude", lastLocation.getLatitude());
					intent.putExtra("longitude", lastLocation.getLongitude());
					intent.putExtra("address", lastLocation.getAddrStr());
					intent.putExtra("thumbnailPath", file.getAbsolutePath());
				//	Log.d("getAbsolutePath--->",file.getAbsolutePath());
					BaiduMapActivity.this.setResult(RESULT_OK, intent);
					finish();
					overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);

//					Toast.makeText(MapControlDemo.this,
//							"屏幕截图成功，图片存在: " + file.toString(),
//							Toast.LENGTH_SHORT).show();
				} catch (FileNotFoundException e) {
					BaiduMapActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
						}
					});
					e.printStackTrace();
				} catch (IOException e) {
					BaiduMapActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
						}
					});
					e.printStackTrace();
				}
			}
		});

	}
	private void showCityPup(View view,final String fromLat,final String fromLng,final String fromAddress, final String activityLat ,final String activityLng ,final String address) {
		RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.pop_city_layout, null);
		Button btn_map_baidu = (Button) layout.findViewById(R.id.btn_map_baidu);
		Button btn_map_gaode = (Button) layout.findViewById(R.id.btn_map_gaode);
		Button btn_map_google = (Button) layout.findViewById(R.id.btn_map_google);
		Button btn_map_tencent = (Button) layout.findViewById(R.id.btn_map_tencent);
		Button btn_cancle = (Button) layout.findViewById(R.id.btn_cancle);
		//判断地图是否存在
//        if (!MapUtils.isAvilible(MainActivity.this, "com.baidu.BaiduMap")){
//            btn_map_baidu.setVisibility(View.GONE);
//        }
//        if (!MapUtils.isAvilible(MainActivity.this,"com.autonavi.minimap")){
//            btn_map_gaode.setVisibility(View.GONE);
//        }
//        if (!MapUtils.isAvilible(MainActivity.this,"com.google.android.apps.maps")){
//            btn_map_google.setVisibility(View.GONE);
//        }
//        if (!MapUtils.isAvilible(MainActivity.this,"com.tencent.map")){
//            btn_map_tencent.setVisibility(View.GONE);
//        }
		final PopupWindow popupWindow = new PopupWindow(layout,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		//点击空白处时，隐藏掉pop窗口
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		//设置键盘不遮盖
		popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		//添加弹出、弹入的动画
		popupWindow.setAnimationStyle(R.style.Popupwindow);
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM, 0, -location[1]);
		//添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				popupWindow.dismiss();
			}
		});
		btn_map_baidu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
				MapUtils.openBaiduMap(BaiduMapActivity.this, activityLat, activityLng, address);
			}
		});
		btn_map_gaode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
				MapUtils.openGDMap(BaiduMapActivity.this, activityLat, activityLng, address);
			}
		});
		btn_map_google.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
				MapUtils.openGoogleMap(BaiduMapActivity.this, fromLat, fromLng,activityLat, activityLng);
			}
		});
		btn_map_tencent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
				MapUtils.openTencentMap(BaiduMapActivity.this, fromAddress, fromLat, fromLng, activityLat, activityLng, address);
			}
		});
		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
			}
		});
		btn_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
			}
		});
	}


}
