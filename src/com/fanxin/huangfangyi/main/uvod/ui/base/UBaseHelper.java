package com.fanxin.huangfangyi.main.uvod.ui.base;

import android.content.Context;

/**
 * 
 * @author leewen
 *
 */
public abstract class UBaseHelper {
	public interface ChangeListener {
		void onUpdateUI();
	}
	//当前进度
	protected int mCurrentLevel;
	//最大进度
	protected int mMaxLevel;
	//历史进度
	protected int mHistoryLevel;
	//每次增加的粒度
	protected int mLevel;
	protected ChangeListener mListener;
	protected Context mContext;
	
	public abstract void init(Context context);
	public abstract void setValue(int level, boolean isTouch);
	public abstract int getSystemValueLevel();
	
	public UBaseHelper(Context context) {
		mContext = context;
		init(context);
	}
	public int getCurrentLevel() {
		return mCurrentLevel;
	}
	
	public void setCurrentLevel(int currentLevel) {
		mCurrentLevel = currentLevel;
	}
	
	public int getMaxLevel() {
		return mMaxLevel;
	}
	
	public void setMaxLevel(int maxLevel) {
		mMaxLevel = maxLevel;
	}
	
	public int getHistoryLevel() {
		return mHistoryLevel;
	}
	
	public void setHistoryLevel(int historyLevel) {
		mHistoryLevel = historyLevel;
	}
	
	public int getLevel() {
		return mLevel;
	}
	
	public void setLevel(int level) {
		mLevel = level;
	}
	
	public ChangeListener getChanageListener() {
		return mListener;
	}
	
	public void setOnChangeListener(ChangeListener l) {
		mListener = l;
	}
	
	public void increaseValue() {
		setValue(mCurrentLevel + mLevel, false);
	}
	
	public void decreaseValue() {
		setValue(mCurrentLevel - mLevel, false);
	}
	
	public boolean isZero() {
		return mCurrentLevel == 0;
	}
	
	public void setToZero() {
		setValue(0, false);
	}
	
	public void updateValue() {
		mCurrentLevel = getSystemValueLevel();
	}
	
	public void setVauleTouch(int level) {
        setValue(level, true);
    }
}
