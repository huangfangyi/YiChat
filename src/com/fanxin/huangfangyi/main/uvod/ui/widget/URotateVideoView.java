package com.fanxin.huangfangyi.main.uvod.ui.widget;


import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.ucloud.player.api.UVideoInfo;
import com.ucloud.player.widget.v2.UVideoView;

import java.util.List;

/**
 * Created by leewen on 2015/10/10.
 */
public class URotateVideoView extends URotateLayout {
    public static final String TAG = "URotateVideoView";

    private UVideoView mVideoView;

    public URotateVideoView(Context context) {
        super(context);
        init(context);
    }

    public URotateVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public URotateVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mLayoutParams.gravity = Gravity.CENTER;
        mVideoView = new UVideoView(context);
        mVideoView.setLayoutParams(mLayoutParams);
        addView(mVideoView);
    }



    /**video player method**/

    /**
     * Sets video path.
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
        if(mVideoView != null) mVideoView.setVideoPath(path);
    }

    public void release(boolean cleartargetstate) {
        if (mVideoView != null) mVideoView.release(cleartargetstate);
    }

    public void start() {
        if (mVideoView != null) mVideoView.start();
    }

    public void pause() {
        if (mVideoView != null) mVideoView.pause();
    }

    public void suspend() {
        if (mVideoView != null) mVideoView.suspend();
    }

    public void resume() {
        if (mVideoView != null) mVideoView.resume();
    }

    public int getDuration() {
        if (mVideoView != null) return mVideoView.getDuration();
        return 0;
    }

    public int getCurrentPosition() {
        if (mVideoView != null) return mVideoView.getCurrentPosition();
        return 0;
    }

    public void seekTo(int msec) {
        if (mVideoView != null) mVideoView.seekTo(msec);
    }

    public boolean isPlaying() {
        if (mVideoView != null) return mVideoView.isPlaying();
        return false;
    }

    public int getBufferPercentage() {
        if (mVideoView != null) return mVideoView.getBufferPercentage();
        return 0;
    }

    public boolean isInPlaybackState() {
        if (mVideoView != null) return mVideoView.isInPlaybackState();
        return false;
    }

    public boolean canPause() {
        if (mVideoView != null) return mVideoView.canPause();
        return false;
    }

    public boolean canSeekBackward() {
        if (mVideoView != null) return mVideoView.canSeekBackward();
        return false;
    }

    public boolean canSeekForward() {
        if (mVideoView != null) return mVideoView.canSeekForward();
        return false;
    }

    public int getAudioSessionId() {
        if (mVideoView != null) return mVideoView.getAudioSessionId();
        return -1;
    }

    public void releaseWithoutStop() {
        if (mVideoView != null) mVideoView.releaseWithoutStop();
    }

    public void setRatio(int ratio) {
        if (mVideoView !=null) mVideoView.setRatio(ratio);
    }

    public void setHistoryOffset(int historyOffset) {
        if (mVideoView !=null) mVideoView.setHistoryOffset(historyOffset);
    }

    public void registerCallabck(UVideoView.Callback mCallback) {
        if (mVideoView !=null) mVideoView.registerCallback(mCallback);
    }

    public int getRatio() {
        if (mVideoView != null) return mVideoView.getRatio();
        return 0;
    }

    public void stopPlayback(boolean flag) {
        if (mVideoView != null) mVideoView.stopPlayback(flag);
    }

    public void setDecoder(int decoder) {
        mVideoView.setDecoder(decoder);
    }

    public int getDecoder() {
        return mVideoView.getDecoder();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * must call after start
     * @return
     */
    public List<UVideoView.DefinitionType> getDefinitions() {
        return mVideoView.getDefinitions();
    }

    public void toggleDefinition(UVideoView.DefinitionType targetDefinition) {
        mVideoView.toggleDefinition(targetDefinition);
    }


    public void toggleDecoder(int decoder) {
        mVideoView.toggleDecoder(decoder);
    }

    public UVideoView.DefinitionType getDefaultDefinition() {
        return mVideoView.getDefaultDefinition();
    }

    public void setVideoInfo(UVideoInfo videoInfo) {
       mVideoView.setVideoInfo(videoInfo);
    }
}
