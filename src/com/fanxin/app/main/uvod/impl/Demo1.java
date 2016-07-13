package com.fanxin.app.main.uvod.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.fanxin.app.R;
import com.fanxin.app.main.uvod.preference.Log2FileUtil;
import com.fanxin.app.main.uvod.preference.Settings;
import com.fanxin.app.main.uvod.ui.UPlayer;
import com.fanxin.app.main.uvod.ui.USettingMenuView;
import com.fanxin.app.main.uvod.ui.base.UMenuItem;
import com.ucloud.player.widget.v2.UVideoView;

public class Demo1 extends FragmentActivity implements USettingMenuView.Callback, UVideoView.Callback {
	UPlayer mPlayer;
	private String mUri;
	Settings mSettings;
	private static final int MSG_INIT_PLAY = 0;

	private class UiHandler extends  Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case MSG_INIT_PLAY:
					init();
					break;
			}
		}
	}
	private Handler uiHandler  = new UiHandler();

	private void init() {
		if (mPlayer == null || TextUtils.isEmpty(mUri))
			return;
		mPlayer.init(this);
		mPlayer.registerCallback(this);
		/*
		 * 设置屏幕比例处理类型
		 */
		mPlayer.setRatio(mSettings.getScreenRatioType());
		/*
		 * 设置软硬解
		 */
		mPlayer.setDecoder(mSettings.getDecoderType());
		mPlayer.setOnSettingMenuItemSelectedListener(this);
		mPlayer.setVideoPath(mUri);
		mPlayer.setScreenOriention(UPlayer.SCREEN_ORIENTATION_SENSOR);
	}

	@Override
	protected void onCreate(Bundle bundles) {
		super.onCreate(bundles);
		setContentView(R.layout.activity_video_demo1);
		mSettings = new Settings(this);

		if (mSettings.isOpenLogRecoder()) {
			Log2FileUtil.getInstance().setLogCacheDir(mSettings.getLogCacheDir());
			Log2FileUtil.getInstance().startLog();
		}

		mUri = getIntent().getStringExtra("videoPath");


		String intentAction = getIntent().getAction();
		if (!TextUtils.isEmpty(intentAction) && intentAction.equals(Intent.ACTION_VIEW)) {
			mUri = getIntent().getDataString();
		}
		mPlayer = (UPlayer)findViewById(R.id.video_main_view);

		mUri = Uri.decode(mUri);
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mNetworkStateListener, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mPlayer.isInPlaybackState()) {
			mSettings.setScreenState(mPlayer.isFullscreen());
			mPlayer.stop(true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mPlayer != null) {
			uiHandler.sendEmptyMessage(MSG_INIT_PLAY);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mSettings.isOpenLogRecoder()) {
			Log2FileUtil.getInstance().stopLog();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPlayer.release();
		unregisterReceiver(mNetworkStateListener);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mPlayer.isFullscreen()) {
				mPlayer.toggleScreenStyle();
				return true;
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onSettingMenuSelected(UMenuItem item) {
		if (item != null && item.parent != null && item.parent.title.equals(getString(R.string.menu_item_title_decoder))) {
			mSettings.setDecoderType(item.type);
		} else if (item != null && item.parent != null && item.parent.title.equals(getString(R.string.menu_item_title_ratio))) {
			mSettings.setScreenRatioType(item.type);
		}
		return false;
	}

	@Override
	public void onEvent(int what, String message) {
		switch (what){
			case UVideoView.Callback.EVENT_PLAY_START:
				break;
			case UVideoView.Callback.EVENT_PLAY_PAUSE:
				break;
			case UVideoView.Callback.EVENT_PLAY_STOP:
				break;
			case UVideoView.Callback.EVENT_PLAY_COMPLETION:
				/*mPlayer.stop(true);
				mPlayer.setVideoPath("new uri");*/
				break;
			case UVideoView.Callback.EVENT_PLAY_DESTORY:
				break;
			case UVideoView.Callback.EVENT_PLAY_ERROR:
				break;
			case UVideoView.Callback.EVENT_PLAY_RESUME:
				break;
		}
	}

	private BroadcastReceiver mNetworkStateListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeInfo = manager.getActiveNetworkInfo();
				if (activeInfo == null) {
					Toast.makeText(context, getString(R.string.error_current_network_disconnected), Toast.LENGTH_LONG).show();
				}
			}
		}
	};
}
