package com.fanxin.huangfangyi.main.uvod.ui.base;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class UVolumeHelper extends UBaseHelper {
	public static int DEFAULT_VOLUME_LEVEL = 1;
	private AudioManager mAudioManager;
	public static final String TAG = "UVolumeHelper";
	public UVolumeHelper(Context context) {
		super(context);
	}

	@Override
	public void init(Context context) {
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		setMaxLevel(maxVolume);
		mCurrentLevel = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentLevel, 0);
		setLevel(DEFAULT_VOLUME_LEVEL);
	}

	@Override
	public void setValue(int level, boolean isTouch) {
		Log.i(TAG, "CurrentLevel: " + mCurrentLevel + ", Operation level:" + level + ", Max level:" + mMaxLevel);
		if (isZero() && isTouch) {
			level = mHistoryLevel;
		}
		if (level < 0) {
			level = 0;
		} else if (level > mMaxLevel) {
			level = mMaxLevel;
		}
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, 0);
		updateValue();
		if (!isZero()) {
			mHistoryLevel = mCurrentLevel;
		}
		if (mListener != null ) {
			mListener.onUpdateUI();
		}
		
	}

	@Override
	public int getSystemValueLevel() {
		int level;
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (mLevel == 0) {
			setLevel(DEFAULT_VOLUME_LEVEL);
		}
        level = (int) (currentVolume / mLevel);
        if (currentVolume % mLevel > 0) {
            level++;
        }
        return level;
	}

}
