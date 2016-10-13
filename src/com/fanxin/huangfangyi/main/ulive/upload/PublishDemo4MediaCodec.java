package com.fanxin.huangfangyi.main.ulive.upload;

import android.os.Bundle;

import com.fanxin.huangfangyi.main.ulive.preference.Log2FileUtil;
import com.ucloud.live.UEasyStreaming;
import com.ucloud.live.UStreamingProfile;



public class PublishDemo4MediaCodec extends BasePublishDemo {

    private static final String TAG = "MediaCodecPublishDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initEnv() {
        if (mSettings.isOpenLogRecoder()) {
            Log2FileUtil.getInstance().setLogCacheDir(mSettings.getLogCacheDir());
            Log2FileUtil.getInstance().startLog();
        }
        UStreamingProfile.Stream stream = new UStreamingProfile.Stream(rtmpPushStreamDomain, "ucloud/" + mSettings.getPusblishStreamId());

        mStreamingProfile = new UStreamingProfile.Builder()
                .setVideoCaptureWidth(mSettings.getVideoCaptureWidth())
                .setVideoCaptureHeight(mSettings.getVideoCaptureHeight())
                .setVideoEncodingBitrate(mSettings.getVideoEncodingBitRate()) //UStreamingProfile.VIDEO_BITRATE_NORMAL
                .setVideoEncodingFrameRate(mSettings.getVideoFrameRate())
                .setAudioEncodingBitrate(UStreamingProfile.AUDIO_BITRATE_NORMAL)
                .setVideoCaptureOrientation(mSettings.getVideoCaptureOrientation())//UStreamingProfile.ORIENTATION_LANDSCAPE or UStreamingProfile.ORIENTATION_PORTRAIT
                .setStream(stream).build();

        mEasyStreaming = new UEasyStreaming(this, UEasyStreaming.UEncodingType.MEDIA_CODEC);

        mEasyStreaming.setAspectWithStreamingProfile(mPreviewContainer, mStreamingProfile);

        mEasyStreaming.setStreamingStateListener(this);
    }
}
