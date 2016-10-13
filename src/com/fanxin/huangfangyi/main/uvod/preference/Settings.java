package com.fanxin.huangfangyi.main.uvod.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fanxin.huangfangyi.R;
import com.ucloud.player.widget.v2.UVideoView;



public class Settings {
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    public Settings(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public int getDecoderType() {
        String key = "pref.decoder_type";
        return Integer.parseInt(mSharedPreferences.getString(key, UVideoView.DECODER_VOD_SW + ""));
    }

    public void setDecoderType(String type) {
        String key = "pref.decoder_type";
        mSharedPreferences.edit().putString(key, type).commit();
    }

    public int getScreenRatioType() {
        String key = "pref.screen_ratio";
        return Integer.parseInt(mSharedPreferences.getString(key, UVideoView.VIDEO_RATIO_FIT_PARENT + ""));
    }

    public void setScreenRatioType(String type) {
        String key = "pref.screen_ratio";
        mSharedPreferences.edit().putString(key, type).commit();
    }

    public boolean isOpenLogRecoder() {
        String key = "pref.open_log";
        return  !mSharedPreferences.getBoolean(key, true);
    }

    public String getLogCacheDir() {
        String key = "pref.log_dir";
        return mSharedPreferences.getString(key, mContext.getResources().getString(R.string.pref_default_log_cache_dir));
    }

    public boolean isFullscreen() {
        String key = "pref.fullscreen";
        return mSharedPreferences.getBoolean(key, false);
    }

    public void setScreenState(boolean isFullscreen) {
        String key = "pref.fullscreen";
        mSharedPreferences.edit().putBoolean(key, isFullscreen).commit();
    }
}
