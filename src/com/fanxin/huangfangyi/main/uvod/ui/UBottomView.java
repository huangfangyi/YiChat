package com.fanxin.huangfangyi.main.uvod.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.fanxin.huangfangyi.R;
import com.ucloud.common.api.base.BaseInterface;
import com.ucloud.common.util.StringUtil;


import butterknife.Bind;
import butterknife.ButterKnife;

public class UBottomView extends RelativeLayout{

	public static final String TAG = "UBottomView";
	private static final int SEEKBAR_MAX = 1000;
	private static int DEFAULT_SEEK_PROGRESS = 40 * 1000;

	public static final int MSG_INIT_SEEK_BAR = 1;
	public static final int MSG_UPDATE_SEEK_BAR = 2;
	public static final int MSG_SHOW_FAST_SEEK_BAR_VIEW = 3;
	public static final int MSG_HIDE_FAST_SEEK_BAR_VIEW = 4;

	private Callback mCallabck;

	@Bind(R.id.img_bt_pause_play)
	ImageButton mPlayPauseButton;

	@Bind(R.id.seekbar)
	SeekBar mSeekBar;

	@Bind(R.id.fast_seekbar)
	SeekBar mFastSeekBar;

	@Bind(R.id.txtv_current_position)
	TextView mCurrentPositionTxtv;

	@Bind(R.id.txtv_duration)
	TextView mDurationTxtv;


	@Bind(R.id.fast_seek_index_txtv)
	TextView mSeekingIndexTxtv;

	@Bind(R.id.fast_seek_index_rl)
	ViewGroup mSeekIndexView;

	@Bind(R.id.img_btn_brightness)
	ImageButton mBrightnessImgBtn;

	@Bind(R.id.img_btn_volume)
	ImageButton mVolumeImgBtn;

	private long mDuration;
	private boolean isInitSeekBar;
	private UPlayer mPlayerContrller;


	private int lastSeekPosition = -1;
	private int fastSeekToTemp = -1;

	public interface Callback extends BaseInterface {
		boolean onPlayButtonClick(View view);
		boolean onBrightnessButtonClick(View view);
		boolean onVolumeButtonClick(View view);
	}

	private class UiHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_INIT_SEEK_BAR:
					initVideoProgressSeekBar(msg.arg1, msg.arg2);
					break;
				case MSG_UPDATE_SEEK_BAR:
					setVideoSeekbarCurrent(msg.arg1);
					break;
				case MSG_SHOW_FAST_SEEK_BAR_VIEW:
					doShowFastSeekIndexBar();
					break;
				case MSG_HIDE_FAST_SEEK_BAR_VIEW:
					doHideFastSeekIndexBar();
					break;
				default:
					break;
			}
		}
	}

	private Handler uiHandler = new UiHandler();

	public UBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public UBottomView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public UBottomView(Context context) {
		this(context, null);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.bind(this);
		mPlayPauseButton.setOnClickListener(mPlayPauseButtonClickListener);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChanageListener);
		mBrightnessImgBtn.setOnClickListener(mBrightnessButtonClickListener);
		mVolumeImgBtn.setOnClickListener(mVolumeButtonClickListener);
	}

	OnClickListener mPlayPauseButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCallabck != null) {
				mCallabck.onPlayButtonClick(v);
			}
			mPlayerContrller.showNavigationBar(0);
		}
	};

	OnClickListener mBrightnessButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCallabck != null) {
				mCallabck.onBrightnessButtonClick(v);
			}
			mPlayerContrller.showNavigationBar(0);
		}
	};

	OnClickListener mVolumeButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCallabck != null) {
				mCallabck.onVolumeButtonClick(v);
			}
			mPlayerContrller.showNavigationBar(0);
		}
	};

	public void togglePlayButtonIcon(int resid) {
		if (mPlayPauseButton != null) {
			mPlayPauseButton.setBackgroundResource(resid);
		}
	}

	public void registerCallback(Callback callback) {
		mCallabck = callback;
	}

	private int getProgressByPosition(int position) {
		if (mDuration == 0) {
			return 0;
		}
		int pos = 0;
		int max = mSeekBar.getMax();
		position = (int) (position * 1000L / mDuration);
		if (max > 0 && max >= position && pos <= mDuration) {
			pos = position;
		} else {
			pos = max;
		}
		return pos;
	}

	private void setVideoSeekbarCurrent(int currposition) {
		int pos = 0;
		if (mDuration > 0) {
			pos = getProgressByPosition(currposition);
		}
		mSeekBar.setProgress(pos);
		String content;
		if (currposition > 0) {
			content = StringUtil.getTimeFormatString(currposition / 1000);
		} else {
			if (mDuration > 0) {
				content = StringUtil.getTimeFormatString(0);
			} else {
				content = "";
			}
		}
		mCurrentPositionTxtv.setText(content);
	}

	private void initVideoProgressSeekBar(int position, long duration) {
		if (position > duration || position < 0) {
			position = 0;
		}
		if (duration > 0) {
			initVideoDuration(duration);
			if (mSeekBar != null) {
				mSeekBar.setMax(SEEKBAR_MAX);
				mFastSeekBar.setMax(SEEKBAR_MAX);
			}
			setVideoSeekbarCurrent(position);
			isInitSeekBar = true;
		} else {
			isInitSeekBar = false;
		}
	}

	private void initVideoDuration(long duration) {
		mDuration = duration;
		String content = StringUtil.getTimeFormatString((int) duration / 1000);
		DEFAULT_SEEK_PROGRESS = (int) (15 * mDuration / 1000);
		if (mDurationTxtv != null) {
			mDurationTxtv.setText(content);
		}
	}

	public void notifyInitVideoProgressBar(int position, int duration) {
		uiHandler.removeMessages(MSG_INIT_SEEK_BAR);
		Message msg = Message.obtain();
		msg.arg1 = position;
		msg.arg2 = duration;
		msg.what = MSG_INIT_SEEK_BAR;
		uiHandler.sendMessage(msg);
	}

	public void notifyUpdateVideoProgressBar(int position) {
		uiHandler.removeMessages(MSG_UPDATE_SEEK_BAR);
		Message msg = Message.obtain();
		msg.arg1 = position;
		msg.what = MSG_UPDATE_SEEK_BAR;
		uiHandler.sendMessage(msg);
	}

	public void notifyShowFaskSeekIndexBar(int delay) {
		uiHandler.removeMessages(MSG_SHOW_FAST_SEEK_BAR_VIEW);
		Message msg = Message.obtain();
		msg.what = MSG_SHOW_FAST_SEEK_BAR_VIEW;
		uiHandler.sendMessageDelayed(msg, delay);
	}

	public void doShowFastSeekIndexBar() {
		if (mSeekIndexView != null) {
			mSeekIndexView.setVisibility(View.VISIBLE);
		}
	}

	public void onPositionChanaged(int position, int duration) {
		if (!isInitSeekBar) {
			notifyInitVideoProgressBar(position, duration);
		} else {
			notifyUpdateVideoProgressBar(position);
		}
	}

	OnSeekBarChangeListener mSeekBarChanageListener = new OnSeekBarChangeListener() {
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int position = mPlayerContrller.getDuration() / 1000 * seekBar.getProgress();
			if (mPlayerContrller.isInPlaybackState()) {
				mPlayerContrller.seekTo(position);
				mFastSeekBar.setProgress(seekBar.getProgress());
			}
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (mFastSeekBar != null) {
				mFastSeekBar.setProgress(progress);
			}
			if (fromUser) {
				mPlayerContrller.showNavigationBar(0);
			}
		}
	};

	public void setPlayerController(UPlayer controller) {
		mPlayerContrller = controller;
	}

	private void seekIncrease() {
		int p;
		if (lastSeekPosition == -1) {
			p = mPlayerContrller.getCurrentPosition();
		} else {
			p = lastSeekPosition;
		}
		int seekTo = p + DEFAULT_SEEK_PROGRESS;
		if (seekTo > mPlayerContrller.getDuration()) {
			seekTo = mPlayerContrller.getDuration();
		}
		doFastSeek(seekTo);
	}

	public void release() {
		lastSeekPosition = -1;
		fastSeekToTemp = -1;
		isInitSeekBar = false;
	}

	private void seekDecrease() {
		int p;
		if (lastSeekPosition == -1) {
			p = mPlayerContrller.getCurrentPosition();
		} else {
			p = lastSeekPosition;
		}
		int seekTo = p - DEFAULT_SEEK_PROGRESS;
		if (seekTo < 0) {
			seekTo = 0;
		}
		doFastSeek(seekTo);
	}

	private void doFastSeek(int seekTo) {
		if (mSeekBar != null && mFastSeekBar != null) {
			mSeekingIndexTxtv.setText(StringUtil.getTimeFormatString((seekTo) / 1000));
			int progress = getProgressByPosition(seekTo);
			mFastSeekBar.setProgress(progress);
			fastSeekToTemp = seekTo;
			float pivotX = (mFastSeekBar.getWidth() * mFastSeekBar.getProgress() / 1000 - mSeekingIndexTxtv.getWidth() / 2);
			LayoutParams lp = (LayoutParams) mSeekIndexView.getLayoutParams();
			lp.leftMargin = (int) pivotX;
			mSeekIndexView.setLayoutParams(lp);
			lastSeekPosition = seekTo;
		}
	}

	public int getLastFastSeekPosition() {
		return fastSeekToTemp;
	}

	public void setLastFastSeekPosition(int value) {
		fastSeekToTemp = value;
	}

	public void setLastSeekPosition(int value) {
		lastSeekPosition = value;
	}

	public void fastSeek(boolean flag) {
		if (flag) {
			seekIncrease();
		} else {
			seekDecrease();
		}
	}

	public void doHideFastSeekIndexBar() {
		if (mSeekIndexView != null) {
			mSeekIndexView.setVisibility(View.GONE);
		}
	}

	public void notifyHideFaskSeekIndexBar(int delay) {
		uiHandler.removeMessages(MSG_HIDE_FAST_SEEK_BAR_VIEW);
		Message msg = Message.obtain();
		msg.what = MSG_HIDE_FAST_SEEK_BAR_VIEW;
		uiHandler.sendMessageDelayed(msg, delay);
	}

	public void setSeekEnable(boolean isSeekEnable) {
		if (mFastSeekBar != null) {
			mFastSeekBar.setEnabled(isSeekEnable);
		}
		if (mSeekBar != null) {
			mSeekBar.setEnabled(isSeekEnable);
		}
	}
}
