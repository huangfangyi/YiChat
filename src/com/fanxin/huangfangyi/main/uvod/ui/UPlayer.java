package com.fanxin.huangfangyi.main.uvod.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.ucloud.common.api.base.BaseInterface;
import com.ucloud.player.api.UVideoInfo;
import com.ucloud.player.widget.v2.UVideoView;


public interface UPlayer extends BaseInterface {

	int VIDEO_RATIO_AUTO = UVideoView.VIDEO_RATIO_FIT_PARENT;
	int VIDEO_RATIO_ORIGIN = UVideoView.VIDEO_RATIO_WRAP_CONTENT;
    int VIDEO_RATIO_FULL_SCREEN = UVideoView.VIDEO_RATIO_FILL_PARENT;

	int DECODER_HW = UVideoView.DECODER_VOD_HW;
	int DECODER_SW = UVideoView.DECODER_VOD_SW;
	int SCREEN_ORIENTATION_SENSOR = ActivityInfo.SCREEN_ORIENTATION_SENSOR;

	void init(Activity context);
	void setVideoPath(String uri);
	void start();
	void stop(boolean cleardefinition);
	void pause();
	void release();
	void toggleScreenStyle();
	boolean isFullscreen();
	boolean isInPlaybackState();
	int getDuration();
	void seekTo(int position);
	void showNavigationBar(int delay);
	int getCurrentPosition();
	int getRatio();
	void setRatio(int ratio);
	void setDecoder(int decoder);
	int getDecoder();
	void setVideoInfo(UVideoInfo mVideoData);
	void setOnSettingMenuItemSelectedListener(USettingMenuView.Callback l);
	void setScreenOriention(int oriention);
	void registerCallback(UVideoView.Callback callback);
}
