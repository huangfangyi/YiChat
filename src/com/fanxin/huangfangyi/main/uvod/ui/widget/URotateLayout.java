package com.fanxin.huangfangyi.main.uvod.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class URotateLayout extends FrameLayout{
    public static final String TAG = "URotateLayout";
    public static final int ORIENTATION_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    public static final int ORIENTATION_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    public static final int ORIENTATION_SENSOR_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
    public static final int ORIENTATION_SENSOR_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
    public static final int ORIENTATION_SENSOR = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
    public static final int ORIENTATION_LOCKED = ActivityInfo.SCREEN_ORIENTATION_LOCKED;

    private int mOrientation;
    private int mLastOrientation;

    private int mDefaultVideoContainerWidth;
    private int mDefaultVideoContainerHeight;

    private int mScreenWidth;
    private int mScreenHeight;

    public URotateLayout(Context context) {
        super(context);
    }

    public URotateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public URotateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void updateScreenWidthAndHeight() {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    public boolean isLandscape() {
        updateScreenWidthAndHeight();
        return mScreenWidth > mScreenHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateScreenWidthAndHeight();
        if (isLandscape()) {
            mDefaultVideoContainerWidth = mScreenWidth;
            mDefaultVideoContainerHeight = mScreenHeight;
        } else {
            mDefaultVideoContainerWidth = mScreenWidth;
         //   mDefaultVideoContainerHeight = mScreenWidth * 9 / 16;
            mDefaultVideoContainerHeight=mScreenHeight;
        }
        setMeasuredDimension(mDefaultVideoContainerWidth, mDefaultVideoContainerHeight);
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(int orientation) {
        if (getContext() instanceof Activity) {
            Activity mActivity = (Activity) getContext();
            switch (orientation) {
                case ORIENTATION_PORTRAIT:
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case ORIENTATION_LANDSCAPE:
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case ORIENTATION_SENSOR_LANDSCAPE:
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    break;
                case ORIENTATION_SENSOR_PORTRAIT:
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    break;
                case ORIENTATION_SENSOR:
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    break;
                case ORIENTATION_LOCKED:
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    break;
            }
            mOrientation = orientation;
            invalidate();
        }
    }

    public void locked() {
        mLastOrientation = mOrientation;
        setOrientation(ORIENTATION_LOCKED);
    }

    public boolean isLocked() {
        return mOrientation == ORIENTATION_LOCKED ? true : false;
    }

    public void unlocked() {
        setOrientation(mLastOrientation);
    }

    public void toggleOrientation() {
        if (getContext() instanceof Activity && mOrientation != ORIENTATION_LOCKED) {
            Activity mActivity = (Activity) getContext();
            if (isLandscape()) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            } else {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            if (mOrientation == ORIENTATION_SENSOR) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setOrientation(mOrientation);
                    }
                }, 2000);
            }
        }
    }
}