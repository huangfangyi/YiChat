package com.fanxin.huangfangyi.main.uvod.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.uvod.ui.base.UBrightnessHelper;
import com.fanxin.huangfangyi.main.uvod.ui.base.UMenuItem;
import com.fanxin.huangfangyi.main.uvod.ui.base.UMenuItemHelper;
import com.fanxin.huangfangyi.main.uvod.ui.base.UVolumeHelper;
import com.fanxin.huangfangyi.main.uvod.ui.widget.URotateVideoView;
import com.fanxin.huangfangyi.main.uvod.ui.widget.UVerticalProgressView;
import com.ucloud.common.util.SystemUtil;
import com.ucloud.player.api.UVideoInfo;
import com.ucloud.player.widget.v2.UVideoView;


import butterknife.Bind;
import butterknife.ButterKnife;

public class UVideoMainView extends FrameLayout implements UPlayer, UTopView.Callback, UBottomView.Callback, USettingMenuView.Callback, UVideoView.Callback {
    public static final String TAG = "UVideoMainView";
    private Activity mContext;
    private static final int MSG_SHOW_TOP_AND_BOTTOM_VIEW = 1;
    private static final int MSG_HIDE_TOP_AND_BOTTOM_VIEW = 2;

    private static final int MSG_SHOW_SETTING_MENU_VIEW = 7;
    private static final int MSG_HIDE_SETTING_MENU_VIEW = 8;

    private static final int MSG_SHOW_LOADING_VIEW = 13;
    private static final int MSG_HIDE_LOADING_VIEW = 14;
    private static final int MSG_UPDATE_PROGRSS = 15;

    private static final int UPDATE_PROGRESS_INTERVAL = 1000;

    private int mMenuViewShowOrHideAnimationDuration = 100;

    @Bind(R.id.bottomview)
    UBottomView mBottomView;

    @Bind(R.id.topview)
    UTopView mTopView;

    @Bind(R.id.videoview)
    URotateVideoView mRotateVideoView;

    @Bind(R.id.setting_menu_view_ll)
    USettingMenuView mSettingMenuView;

    @Bind(R.id.volume_view)
    UVerticalProgressView mVolumeView;

    @Bind(R.id.brightness_view)
    UVerticalProgressView mBrightnessView;

    @Bind(R.id.loading)
    View mLoadingView;

    @Bind(R.id.loading_container)
    View mLoadingContainer;

    @Bind(R.id.circle_play_status)
    View mPlayStatusView;

    private int mRatio = UVideoView.VIDEO_RATIO_FIT_PARENT;
    private int mDecoder = UVideoView.DECODER_VOD_SW;

    private GestureDetector mGestureDetector;
    private InnerGestureDetector mInnerGestrueDetectoer;
    private int mScreenWidth;
    private int mScreenHeight;
    private USettingMenuView.Callback mSettingMenuItemSelectedListener;

    private UVideoView.Callback mCallback;

    private boolean isFastSeekMode;

    protected String mUri;

    private boolean isSuccess = true;

    private boolean isInitSettingMenu = false;

    private boolean isFullscreen;

    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_TOP_AND_BOTTOM_VIEW:
                 //   doShowNavigationBar();
                    break;
                case MSG_HIDE_TOP_AND_BOTTOM_VIEW:
                 //   doHideNavigationBar();
                    break;
                case MSG_SHOW_SETTING_MENU_VIEW:
                  doShowSettingMenuView();
                    break;
                case MSG_HIDE_SETTING_MENU_VIEW:
                    doHideSettingMenuView();
                    break;
                case MSG_SHOW_LOADING_VIEW:
                    doShowLoadingView();
                    break;
                case MSG_HIDE_LOADING_VIEW:
                    doHideLoadingView();
                    break;
                case MSG_UPDATE_PROGRSS:
                    doUpdateProgress();
                    break;
                default:
                    break;
            }
        }
    };

    public UVideoMainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public UVideoMainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UVideoMainView(Context context) {
        this(context, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        mInnerGestrueDetectoer = new InnerGestureDetector();
        mGestureDetector = new GestureDetector(getContext(), mInnerGestrueDetectoer);
        setOnTouchListener(mGestureTouchListener);
        updateScreenWidthAndHeight(context);
    }

    private void updateScreenWidthAndHeight(Context context) {
        Pair<Integer, Integer> resolution = SystemUtil.getResolution(context);
        mScreenWidth = resolution.first;
        mScreenHeight = resolution.second;
        isFullscreen = mScreenWidth >= mScreenHeight;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        loadViews();
        initVolumeHelper();
        initBrightnessHelper();
        initListeners();
    }

    private void initVolumeHelper() {
        UVolumeHelper volumeHelper = new UVolumeHelper(getContext());
        if (mVolumeView != null) {
            mVolumeView.setIconNormalResId(R.drawable.player_icon_bottomview_volume_button_normal);
            mVolumeView.setHelper(volumeHelper);
        }
    }

    private void initBrightnessHelper() {
        UBrightnessHelper brightnessHelper = new UBrightnessHelper(getContext());
        if (mBrightnessView != null) {
            mBrightnessView.setIconNormalResId(R.drawable.player_icon_bottomview_brightness_button_normal);
            mBrightnessView.setHelper(brightnessHelper);
        }
    }

    private void loadViews() {
        ButterKnife.bind(this);
    }

    private void initListeners() {
        if (mTopView != null) {
            mTopView.registerCallback(this);
        }
        if (mBottomView != null) {
            mBottomView.registerCallback(this);
            mBottomView.setPlayerController(this);
        }
        if (mPlayStatusView != null) {
            mPlayStatusView.setOnClickListener(mPlayStatusViewClickListener);
        }
    }

    OnClickListener mPlayStatusViewClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mRotateVideoView != null && !mRotateVideoView.isPlaying()) {
                togglePlayerToPlay();
            }
        }
    };

    @Override
    public void setVideoPath(String uri) {
        if (mRotateVideoView != null && !TextUtils.isEmpty(uri)) {
            notifyShowLoadingView(0);
            setVideoPath(uri, mDecoder, mRatio, 0);
        }
    }

    public void setVideoPath(String uri, int decoder, int ratio, int position) {
        if (mRotateVideoView != null && !TextUtils.isEmpty(uri)) {
            mUri = uri;
            mRotateVideoView.setDecoder(decoder);
            mRotateVideoView.setRatio(ratio);
            mRotateVideoView.setHistoryOffset(position);
            mRotateVideoView.setVideoPath(mUri);
            mRotateVideoView.registerCallabck(this);
        } else {
            Log.i(TAG, "video layout is null.....");
        }
    }

    @Override
    public void start() {
        if (mRotateVideoView != null) {
            mRotateVideoView.start();
        }
    }

    public boolean isNavigationBarShown() {
        return mBottomView != null && mBottomView.getVisibility() == View.VISIBLE;
    }

    public void notifyShowNavigationBar(int delay) {
        uiHandler.removeMessages(MSG_SHOW_TOP_AND_BOTTOM_VIEW);
        Message msg = Message.obtain();
        msg.what = MSG_SHOW_TOP_AND_BOTTOM_VIEW;
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public void notifyHideNavigationBar(int delay) {
        uiHandler.removeMessages(MSG_HIDE_TOP_AND_BOTTOM_VIEW);
        Message msg = Message.obtain();
        msg.what = MSG_HIDE_TOP_AND_BOTTOM_VIEW;
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public void notifyShowSettingMenuView(int delay) {
        uiHandler.removeMessages(MSG_SHOW_SETTING_MENU_VIEW);
        Message msg = Message.obtain();
        msg.what = MSG_SHOW_SETTING_MENU_VIEW;
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public void notifyHideSettingMenuView(int delay) {
        uiHandler.removeMessages(MSG_HIDE_SETTING_MENU_VIEW);
        Message msg = Message.obtain();
        msg.what = MSG_HIDE_SETTING_MENU_VIEW;
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public boolean isSettingMenuViewShown() {
        return mSettingMenuView != null && mSettingMenuView.getVisibility() == View.VISIBLE;
    }

    private void doShowSettingMenuView() {
        if (mSettingMenuView != null && mSettingMenuView.getVisibility() != View.VISIBLE && isSuccess) {
            mSettingMenuView.setVisibility(View.VISIBLE);
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(mMenuViewShowOrHideAnimationDuration);
            mSettingMenuView.startAnimation(ta);
        }
    }

    private void doHideSettingMenuView() {
        if (mSettingMenuView != null && isSuccess) {
            mSettingMenuView.setVisibility(View.GONE);
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(mMenuViewShowOrHideAnimationDuration);
            mSettingMenuView.startAnimation(ta);
        }
    }

    private void doShowNavigationBar() {
        if (mBottomView != null && mBottomView.getVisibility() != View.VISIBLE) {
            mBottomView.setVisibility(View.VISIBLE);
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(100);
            mBottomView.startAnimation(ta);
        }
        if (mTopView != null && mTopView.getVisibility() != View.VISIBLE) {
            mTopView.setVisibility(View.VISIBLE);
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(100);
            mTopView.startAnimation(ta);
        }
    }

    private void doHideNavigationBar() {
        if (mBottomView != null && mBottomView.getVisibility() == View.VISIBLE) {
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
            ta.setDuration(100);
            mBottomView.startAnimation(ta);
            mBottomView.setVisibility(View.GONE);
        }
        if (mTopView != null && mTopView.getVisibility() == View.VISIBLE) {
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
            ta.setDuration(100);
            mTopView.startAnimation(ta);
            mTopView.setVisibility(View.GONE);
        }
    }

    private void singleTapConfirmed() {
        if (mBottomView != null && mBottomView.isShown()) {
            notifyHideNavigationBar(0);
        } else {
            if (!isNavigationBarShown()) {
                notifyShowNavigationBar(0);
            }
        }
    }

    private void hideVolumeView() {
        if (mVolumeView != null && mVolumeView.getVisibility() == View.VISIBLE) {
            mVolumeView.setVisibility(View.GONE);
        }
        if (mBrightnessView != null && mBrightnessView.getVisibility() == View.VISIBLE) {
            mBrightnessView.setVisibility(View.GONE);
        }
    }

    OnTouchListener mGestureTouchListener = new OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean result = mGestureDetector.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (mRotateVideoView != null && isFastSeekMode && mBottomView != null && mBottomView.getLastFastSeekPosition() != -1) {
                        notifyShowLoadingView(0);
                        mRotateVideoView.seekTo(mBottomView.getLastFastSeekPosition());
                        isFastSeekMode = false;
                        mBottomView.notifyHideFaskSeekIndexBar(1000);
                        mBottomView.notifyUpdateVideoProgressBar(mBottomView.getLastFastSeekPosition());
                        mBottomView.setLastFastSeekPosition(-1);
                    }
                    if (mInnerGestrueDetectoer != null) {
                        mInnerGestrueDetectoer.init();
                    }
                    break;
                default:
                    break;
            }
            return result;
        }
    };

    @Override
    public void onEvent(int what, String message) {
        Log.d(TAG, message);
        switch (what){
            case UVideoView.Callback.EVENT_PLAY_START: //prepared
                dealOnPrepared();
                break;
            case UVideoView.Callback.EVENT_PLAY_PAUSE:
                break;
            case UVideoView.Callback.EVENT_PLAY_STOP:
                break;
            case UVideoView.Callback.EVENT_PLAY_COMPLETION:
                dealCompletion();
                break;
            case UVideoView.Callback.EVENT_PLAY_DESTORY:
                break;
            case UVideoView.Callback.EVENT_PLAY_ERROR:
                break;
            case UVideoView.Callback.EVENT_PLAY_RESUME:
                break;
            case UVideoView.Callback.EVENT_PLAY_SEEK_COMPLETED:
                notifyHideLoadingView(1000);
                break;
            case UVideoView.Callback.EVENT_PLAY_INFO_BUFFERING_START:
                notifyShowLoadingView(0);
                break;
            case UVideoView.Callback.EVENT_PLAY_INFO_BUFFERING_END:
                notifyHideLoadingView(0);
                break;
        }
        if (mCallback != null) {
            mCallback.onEvent(what, message);
        }
    }

    class InnerGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private float x1 = -1;
        private float y1 = -1;
        private int MIN_SLIDE_DISTANCE = 40;
        private int mMinVerticalSlideDistance = MIN_SLIDE_DISTANCE;
        private int mMinHorizontalSlideDistance = MIN_SLIDE_DISTANCE;
        public boolean isSeekEnable = false;
        public InnerGestureDetector() {
            init();
        }

        public void init() {
            x1 = -1;
            y1 = -1;
            ViewConfiguration mViewConfiguration = ViewConfiguration.get(getContext());
            MIN_SLIDE_DISTANCE = mViewConfiguration.getScaledTouchSlop();
            mMinHorizontalSlideDistance = mMinVerticalSlideDistance = MIN_SLIDE_DISTANCE;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            x1 = -1;
            y1 = -1;
            if (mBottomView != null) {
                mBottomView.setLastSeekPosition(-1);
            }
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float velocityX, float velocityY) {
            try {
                if (x1 == -1 || y1 == -1) {
                    x1 = e1.getX();
                    y1 = e1.getY();
                }
                int slideX = (int) (e2.getX() - x1);
                int slideY = (int) (e2.getY() - y1);
                boolean slideVertical = Math.abs(slideY) >= Math.abs(slideX);
                if (slideVertical && Math.abs(slideY) > mMinVerticalSlideDistance
                        && x1 > mScreenWidth / 2) {
                    mVolumeView.change(slideY < 0, false);
                    mBrightnessView.setVisibility(View.GONE);
                    mVolumeView.setVisibility(View.VISIBLE);
                    x1 = e2.getX();
                    y1 = e2.getY();
                    mMinHorizontalSlideDistance = mScreenWidth;
                    return true;
                }
                if (slideVertical && Math.abs(slideY) > mMinVerticalSlideDistance
                        && x1 < mScreenWidth / 2) {
                    mBrightnessView.change(slideY < 0, false);
                    mVolumeView.setVisibility(View.GONE);
                    mBrightnessView.setVisibility(View.VISIBLE);
                    x1 = e2.getX();
                    y1 = e2.getY();
                    mMinHorizontalSlideDistance = mScreenWidth;
                    return true;
                }
                if (isSuccess && mRotateVideoView.isInPlaybackState() && isSeekEnable) {
                    if (!slideVertical && Math.abs(slideX) > mMinHorizontalSlideDistance) {
                        isFastSeekMode = true;
                        if (!isNavigationBarShown()) {
                            notifyShowNavigationBar(0);
                        }
                        mBottomView.notifyShowFaskSeekIndexBar(0);
                        mBottomView.fastSeek(slideX > 0);
                        x1 = e2.getX();
                        y1 = e2.getY();
                        mMinVerticalSlideDistance = mScreenHeight;
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            try {
                hideVolumeView();
                float x2 = e.getX();
                float range;
                if (!isFullscreen()) {
                    range = mScreenWidth;
                } else {
                    range = 5 * mScreenWidth / 6;
                }
                if (mRotateVideoView != null && !mRotateVideoView.isInPlaybackState()) {
                    return super.onSingleTapConfirmed(e);
                }

                if (!isSettingMenuViewShown() && x2 >= range) {
                    notifyHideNavigationBar(0);
                    if (isFullscreen()) {
                        notifyShowSettingMenuView(0);
                    }
                } else {
                    if (isSettingMenuViewShown()) {
                        notifyHideSettingMenuView(0);
                    }
                    else {
                        singleTapConfirmed();
                    }
                }
            } catch (Exception error) {
                error.printStackTrace();
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    @Override
    public boolean onLeftButtonClick(View view) {
        if(isFullscreen()) toggleScreenStyle();
        else mContext.finish();
        return false;
    }

    @Override
    public boolean onRightButtonClick(View view) {
        toggleScreenStyle();
        return false;
    }

    @Override
    public void init(Activity context) {
        mContext = context;
    }

    @Override
    public void toggleScreenStyle() {
        if (mRotateVideoView != null) {
            mRotateVideoView.toggleOrientation();
        }
    }

    public void setScreenOriention(int oriention) {
        if (mRotateVideoView != null) {
            mRotateVideoView.setOrientation(oriention);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (!isFullscreen()) {
            if(mSettingMenuView != null) {
                mSettingMenuView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean isFullscreen() {
        updateScreenWidthAndHeight(getContext());
        return isFullscreen;
    }

    @Override
    public boolean onPlayButtonClick(View view) {
        if (mRotateVideoView != null) {
            if (mRotateVideoView.isPlaying()) {
                togglePlayerToPause();
            } else {
                togglePlayerToPlay();
            }
        }
        return false;
    }

    private void togglePlayerToPause() {
        mRotateVideoView.pause();
        mPlayStatusView.setVisibility(View.VISIBLE);
        mBottomView.togglePlayButtonIcon(R.drawable.player_icon_bottomview_play_button_normal);
    }

    private void togglePlayerToPlay() {
        mPlayStatusView.setVisibility(View.GONE);
        mRotateVideoView.start();
        mBottomView.togglePlayButtonIcon(R.drawable.player_icon_bottomview_pause_button_normal);
    }

    public void dealOnPrepared() {
        notifyHideLoadingView(1000);
        mPlayStatusView.setVisibility(View.GONE);
        mBottomView.togglePlayButtonIcon(R.drawable.player_icon_bottomview_pause_button_normal);
        mBottomView.release();
        notifyUpdateProgress();

        if (!isInitSettingMenu) {
            UMenuItemHelper menuItemHelper = UMenuItemHelper.getInstance(getContext());
            menuItemHelper.release();
            menuItemHelper.register(UMenuItemHelper.getInstance(getContext()).buildVideoRatioMenuItem(mRatio));
            menuItemHelper.register(UMenuItemHelper.getInstance(getContext()).buildVideoDecoderMenuItem(mDecoder));
            UMenuItem uMenuItem = menuItemHelper.buildVideoDefinitationMenuItem(mRotateVideoView.getDefinitions(), mRotateVideoView.getDefaultDefinition().index());
            menuItemHelper.register(uMenuItem, 0);

            mSettingMenuView.init();
            mSettingMenuView.setOnMenuItemSelectedListener(this);
            isInitSettingMenu = true;
        }

        boolean isCanSeek = mRotateVideoView.canSeekForward();
        mInnerGestrueDetectoer.isSeekEnable = isCanSeek;
        mBottomView.setSeekEnable(isCanSeek);
    }

    public void dealCompletion() {
        if (mBottomView != null && mRotateVideoView != null && mPlayStatusView != null) {
            togglePlayerToPause();
        }
    }

    @Override
    public boolean isInPlaybackState() {
        return mRotateVideoView != null && mRotateVideoView.isInPlaybackState();
    }

    @Override
    public int getDuration() {
        if (mRotateVideoView != null) {
            return mRotateVideoView.getDuration();
        }
        return 0;
    }

    @Override
    public void seekTo(int position) {
        if (mRotateVideoView != null) {
            mRotateVideoView.seekTo(position);
        }
    }

    @Override
    public void showNavigationBar(int delay) {
        notifyShowNavigationBar(delay);
    }

    @Override
    public int getCurrentPosition() {
        if (mRotateVideoView != null) {
            return mRotateVideoView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void stop(boolean cleardefinition) {
        if (mRotateVideoView != null) {
            mRotateVideoView.stopPlayback(cleardefinition);
        }
        if (cleardefinition) {
            isInitSettingMenu = false;
        }
    }

    @Override
    public void pause() {
        if (mRotateVideoView != null) {
            mRotateVideoView.pause();
        }
    }

    @Override
    public int getRatio() {
        if (mRotateVideoView != null) {
            return mRotateVideoView.getRatio();
        }
        return UPlayer.VIDEO_RATIO_AUTO;
    }

    @Override
    public void setRatio(int ratio) {
        mRatio = ratio;
        if (mRotateVideoView.isInPlaybackState()) {
            mRotateVideoView.setRatio(mRatio);
        }
    }

    @Override
    public void setDecoder(int decoder) {
        mDecoder = decoder;
    }

    @Override
    public int getDecoder() {
        if (mRotateVideoView != null) {
            mRotateVideoView.getDecoder();
        }
        return DECODER_SW;
    }

    @Override
    public boolean onBrightnessButtonClick(View view) {
        if (mBrightnessView != null && mBrightnessView.isShown()) {
            mBrightnessView.setVisibility(View.GONE);
        } else {
            assert mBrightnessView != null;
            mBrightnessView.setVisibility(View.VISIBLE);
        }
//        mRotateVideoView.setRotation(mRotateVideoView.getRotation() + 90 % 360);
        return false;
    }

    @Override
    public boolean onVolumeButtonClick(View view) {
        if (mVolumeView != null && mVolumeView.getVisibility() == View.VISIBLE) {
            mVolumeView.setVisibility(View.GONE);
        } else {
            assert mVolumeView != null;
            mVolumeView.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public void release() {
        UMenuItemHelper.getInstance(getContext()).release();
    }

    private void notifyShowLoadingView(int duration) {
        Message msg = Message.obtain();
        msg.what = MSG_SHOW_LOADING_VIEW;
        uiHandler.removeMessages(MSG_SHOW_LOADING_VIEW);
        uiHandler.sendMessageDelayed(msg, duration);
    }

    private void notifyHideLoadingView(int duration) {
        Message msg = Message.obtain();
        msg.what = MSG_HIDE_LOADING_VIEW;
        uiHandler.removeMessages(MSG_HIDE_LOADING_VIEW);
        uiHandler.sendMessageDelayed(msg, duration);
    }

    private void doShowLoadingView() {
        if (mLoadingContainer != null && mLoadingContainer.getVisibility() == View.GONE && mLoadingView != null) {
            mLoadingContainer.setVisibility(View.VISIBLE);
            RotateAnimation rotateAnimation = new RotateAnimation(0f, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(2000);
            rotateAnimation.setRepeatMode(RotateAnimation.RESTART);
            rotateAnimation.setRepeatCount(-1);
            mLoadingView.startAnimation(rotateAnimation);
        }
    }

    private void doHideLoadingView(){
        if (mLoadingContainer != null && mLoadingView != null) {
            mLoadingView.clearAnimation();
            mLoadingContainer.setVisibility(View.GONE);
        }
    }

    private void doUpdateProgress() {
        if (mRotateVideoView != null && mRotateVideoView.isInPlaybackState()) {
            int currnetPosition = mRotateVideoView.getCurrentPosition();
            int duration = mRotateVideoView.getDuration();
            if (mBottomView != null) {
                mBottomView.onPositionChanaged(currnetPosition, duration);
            }
            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyUpdateProgress();
                }
            },UPDATE_PROGRESS_INTERVAL);
        }
    }

    private void notifyUpdateProgress() {
        Message msg = Message.obtain();
        msg.what = MSG_UPDATE_PROGRSS;
        uiHandler.removeMessages(msg.what);
        uiHandler.sendMessage(msg);
    }

    @Override
    public void setVideoInfo(UVideoInfo videoData) {
       mRotateVideoView.setVideoInfo(videoData);
    }

    @Override
    public void setOnSettingMenuItemSelectedListener(USettingMenuView.Callback l) {
        mSettingMenuItemSelectedListener = l;
    }

    @Override
    public void registerCallback(UVideoView.Callback callback) {
        mCallback = callback;
    }

    @Override
    public boolean onSettingMenuSelected(UMenuItem item) {
        boolean flag = false;
        if (mSettingMenuItemSelectedListener != null) {
            flag = mSettingMenuItemSelectedListener.onSettingMenuSelected(item);
        }
        if (!flag) try {
            if (item.parent != null) {
                if (item.parent.title.equals(mContext.getResources().getString(R.string.menu_item_title_definition))) {
                    notifyShowLoadingView(0);
                    mRotateVideoView.toggleDefinition(UVideoView.DefinitionType.find(item.type));
                } else if (item.parent != null && item.parent.title.equals(mContext.getResources().getString(R.string.menu_item_title_ratio))) {
                    mRotateVideoView.setRatio(Integer.parseInt(item.type));
                } else if (item.parent != null && item.parent.title.equals(mContext.getResources().getString(R.string.menu_item_title_decoder))) {
                    notifyShowLoadingView(0);
                    mRotateVideoView.toggleDecoder(Integer.parseInt(item.type));
                }
                notifyHideSettingMenuView(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
