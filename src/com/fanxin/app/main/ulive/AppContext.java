package com.fanxin.app.main.ulive;

import android.app.Application;
import android.os.Build;
import android.util.DisplayMetrics;

import com.ucloud.live.UEasyStreaming;

import java.util.Random;


/**
 * 
 * @author leewen
 *
 */
public class AppContext extends Application {
	protected static AppContext  mInstance;
	private DisplayMetrics displayMetrics = null;

	public AppContext(){
		mInstance = this;
	}
	public static boolean sRunningOnIceCreamSandwich;

	static {
		sRunningOnIceCreamSandwich = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static AppContext getApp() {
		if (mInstance != null && mInstance instanceof AppContext) {
			return (AppContext) mInstance;
		} else {
			mInstance = new AppContext();
			mInstance.onCreate();
			return (AppContext) mInstance;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		UEasyStreaming.initStreaming("publish3-key");
		UEasyStreaming.syncMobileConfig(this, 3600 * 24);
	}

	public static int getRandomStreamId() {
		Random random = new Random();
		int randint =(int)Math.floor((random.nextDouble()*10000.0 + 10000.0));
		return randint;
	}

	public float getScreenDensity() {
		if (this.displayMetrics == null) {
			setDisplayMetrics(getResources().getDisplayMetrics());
		}
		return this.displayMetrics.density;
	}

	public int getScreenHeight() {
		if (this.displayMetrics == null) {
			setDisplayMetrics(getResources().getDisplayMetrics());
		}
		return this.displayMetrics.heightPixels;
	}

	public int getScreenWidth() {
		if (this.displayMetrics == null) {
			setDisplayMetrics(getResources().getDisplayMetrics());
		}
		return this.displayMetrics.widthPixels;
	}

	public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
		this.displayMetrics = DisplayMetrics;
	}

	public int dp2px(float f)
	{
		return (int)(0.5F + f * getScreenDensity());
	}

	public int px2dp(float pxValue) {
		return (int) (pxValue / getScreenDensity() + 0.5f);
	}

	//获取应用的data/data/....File目录
	public String getFilesDirPath() {
		return getFilesDir().getAbsolutePath();
	}

	//获取应用的data/data/....Cache目录
	public String getCacheDirPath() {
		return getCacheDir().getAbsolutePath();
	}
}