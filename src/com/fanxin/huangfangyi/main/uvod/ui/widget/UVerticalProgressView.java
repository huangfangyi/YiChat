package com.fanxin.huangfangyi.main.uvod.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.uvod.ui.base.UBaseHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 
 * @author leewen
 *
 */
public class UVerticalProgressView extends RelativeLayout implements
        UBaseHelper.ChangeListener {
    private static final int MSG_HIDE = 1;
    private static final int DELAY_HIDE = 5000;

    @Bind(R.id.volume_icon)
    ImageView mVolumeIcon;

    @Bind(R.id.volume_progress)
    UVerticalProgressBar mVerticalProgressBar;

    private UBaseHelper mBaseHelper;
    private boolean mIsUseSystemVolume;
    private int mIconNormalResId;

	private class UiHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HIDE:
                    hide();
                    break;
            }
        }
    }

    private UiHandler mHandler = new UiHandler();

    public void setIconNormalResId(int resId) {
    	mIconNormalResId = resId;
    }

    public UVerticalProgressView(Context context, AttributeSet attrs, int i) {
        super(context, attrs, i);
    }

    public UVerticalProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UVerticalProgressView(Context context) {
        this(context, null, 0);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        mVerticalProgressBar.setOrientation(false);
        mIsUseSystemVolume = true;
    }

    public void setHelper(UBaseHelper baseHelper) {
    	mBaseHelper = baseHelper;
    	mBaseHelper.setOnChangeListener(this);
    	mVerticalProgressBar.setMax(mBaseHelper.getMaxLevel());
    	updateProgressBar();
    }

    @SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event) {
        if (trackTouch(event)) {
            show();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean trackTouch(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
            mVerticalProgressBar.setProgress(computeLevel(event.getY()));
            return true;
        case MotionEvent.ACTION_UP:
            int level = computeLevel(event.getY());
            mVerticalProgressBar.setProgress(level);
            mBaseHelper.setVauleTouch(level);
            return true;
        }

        return false;
    }
    
    private int computeLevel(float location) {
        int level = 0;
        if (location <= mVerticalProgressBar.getTop()) {
            level = mVerticalProgressBar.getMax();
        } else if (location >= mVerticalProgressBar.getBottom()) {
            level = 0;
        } else {
            level = (int) Math
                    .ceil((mVerticalProgressBar.getHeight() - location + mVerticalProgressBar
                            .getTop())
                            * mVerticalProgressBar.getMax()
                            / mVerticalProgressBar.getHeight());
        }

        return level;
    }

    public void onUpdateUI() {
        updateProgressBar();
    }

    public void updateProgressBar() {
    	if (mBaseHelper != null) {
    		mBaseHelper.updateValue();
		}
        if (mVerticalProgressBar != null) {
            mVerticalProgressBar.setProgress((int)mBaseHelper.getCurrentLevel());
        }
        if (mBaseHelper.isZero()) {
            mVolumeIcon.setImageResource(mIconNormalResId);
        } else {
            mVolumeIcon.setImageResource(mIconNormalResId);
        }
    }

    public void change(boolean isUp, boolean isZero) {
        if (isZero) {
            mBaseHelper.isZero();
        } else {
            if (isUp) {
            	mBaseHelper.increaseValue();
            } else {
            	mBaseHelper.decreaseValue();
            }
        }
        show();
    }

    public boolean isUseSystemValue() {
        return mIsUseSystemVolume;
    }

    public void show() {
        mHandler.removeMessages(MSG_HIDE);
        setVisibility(VISIBLE);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE, DELAY_HIDE);
    }

    public void hide() {
        mHandler.removeMessages(MSG_HIDE);
        setVisibility(GONE);
    }
}