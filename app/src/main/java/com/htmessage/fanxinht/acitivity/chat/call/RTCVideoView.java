package com.htmessage.fanxinht.acitivity.chat.call;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.anyrtc.meet_kit.RTCViewHelper;
import org.anyrtc.meet_kit.RTMeetKit;
import org.anyrtc.utils.ScreenUtils;
import org.webrtc.EglBase;
import org.webrtc.PercentFrameLayout;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Eric on 2016/7/26.
 * <p>
 * Update by Ming on 2017/03/20
 */
public class RTCVideoView implements RTCViewHelper, View.OnTouchListener {
    private static final String TAG = "RTCVideoView";
    private static Context mContext;
    private static int SUB_X = 72;
    private static int SUB_Y = 2;
    private static int SUB_WIDTH = 24;
    private static int SUB_HEIGHT = 20;
    private static int mScreenWidth;
    private static int mScreenHeight;

    private HashMap<String, Boolean> mAudioSetting = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> mVideoSetting = new HashMap<String, Boolean>();

    private ViewClickEvent mVideoClickEvent;
    private boolean isHost;

    public interface ViewClickEvent {
        void CloseVideoRender(View view, String strPeerId);

        void OnSwitchCamera(View view);

        void onVideoTouch(String strPeerId);
    }

    /**
     * 设置按钮的点击事件
     *
     * @param viewClickEvent
     */
    public void setBtnClickEvent(ViewClickEvent viewClickEvent) {
        this.mVideoClickEvent = viewClickEvent;
    }

    protected static class VideoView {
        public String strPeerId;
        public int index;
        public int x;
        public int y;
        public int w;
        public int h;
        public PercentFrameLayout mLayout = null;
        public SurfaceViewRenderer mView = null;
        public VideoRenderer mRenderer = null;
        public ImageView btnClose = null;
        private RelativeLayout layoutCamera = null;
        private RTMeetKit.RTCVideoLayout mRTCVideoLayout;

        private boolean mAudioShowFlag = false;
        private boolean mVideoShowFlag = false;
        public ImageView mAudioImageView;
        public ImageView mVideoImageView;
        public ImageView mLocalCamera;
        private int width = mScreenWidth * SUB_WIDTH / (100 * 3);
        private int height = mScreenHeight * SUB_HEIGHT / (100 * 3);

        public VideoView(String strPeerId, Context ctx, EglBase eglBase, int index, int x, int y, int w, int h, RTMeetKit.RTCVideoLayout videoLayout) {
            this.strPeerId = strPeerId;
            this.index = index;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.mRTCVideoLayout = videoLayout;

            mLayout = new PercentFrameLayout(ctx);
            mLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            View view = View.inflate(ctx, org.anyrtc.rtmpc_hybrid.R.layout.layout_meeting, null);

            mView = (SurfaceViewRenderer) view.findViewById(org.anyrtc.rtmpc_hybrid.R.id.suface_view);
            btnClose = (ImageView) view.findViewById(org.anyrtc.rtmpc_hybrid.R.id.img_close_render);
            mLocalCamera = (ImageView) view.findViewById(org.anyrtc.rtmpc_hybrid.R.id.camera_off);
            mAudioImageView = (ImageView) view.findViewById(org.anyrtc.rtmpc_hybrid.R.id.img_audio_close);
            mVideoImageView = (ImageView) view.findViewById(org.anyrtc.rtmpc_hybrid.R.id.img_video_close);
            layoutCamera = (RelativeLayout) view.findViewById(org.anyrtc.rtmpc_hybrid.R.id.layout_camera);
            mView.init(eglBase.getEglBaseContext(), null);
            mView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mLayout.addView(view);
        }

        public Boolean Fullscreen() {
            return w == 100 || h == 100;
        }

        public Boolean Hited(int px, int py) {
            if (!Fullscreen()) {
                int left = x * mScreenWidth / 100;
                int top = y * mScreenHeight / 100;
                int right = (x + w) * mScreenWidth / 100;
                int bottom = (y + h) * mScreenHeight / 100;
                if ((px >= left && px <= right) && (py >= top && py <= bottom)) {
                    return true;
                }
            }
            return false;
        }

        public void close() {
            mLayout.removeView(mView);
            mView.release();
            mView = null;
            mRenderer = null;
        }

        private void updateView() {
            if (mAudioShowFlag) {
                if (mRTCVideoLayout == RTMeetKit.RTCVideoLayout.RTC_V_3X3_auto) {
                    mAudioImageView.setVisibility(View.VISIBLE);
                }
            } else {
                mAudioImageView.setVisibility(View.GONE);
            }

            if (mVideoShowFlag) {
                if (mRTCVideoLayout == RTMeetKit.RTCVideoLayout.RTC_V_3X3_auto) {
                    mVideoImageView.setVisibility(View.VISIBLE);
                }
            } else {
                mVideoImageView.setVisibility(View.GONE);
            }
        }
    }

    private boolean mAutoLayout;
    private EglBase mRootEglBase;
    private static RelativeLayout mVideoView;
    private VideoView mLocalRender;
    private HashMap<String, VideoView> mRemoteRenders;
    private RTMeetKit.RTCVideoLayout mRTCVideoLayout;

    public RTCVideoView(RelativeLayout videoView, Context ctx, EglBase eglBase, boolean isHost, RTMeetKit.RTCVideoLayout rtcVideoLayout) {
        mAutoLayout = false;
        mContext = ctx;
        mVideoView = videoView;
        mRootEglBase = eglBase;
        mLocalRender = null;
        mRemoteRenders = new HashMap<>();
        this.isHost = isHost;
        mRTCVideoLayout = rtcVideoLayout;
        if (mRTCVideoLayout == RTMeetKit.RTCVideoLayout.RTC_V_3X3_auto) {
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mScreenWidth = ScreenUtils.getScreenWidth(mContext);
        mScreenHeight = ScreenUtils.getScreenHeight(mContext) - ScreenUtils.getStatusHeight(mContext);
    }

    /**
     * 设置1x3模式下点击小图像切换至全屏
     *
     * @param enable
     */
    public void setVideoSwitchEnable(boolean enable) {
        if (mRTCVideoLayout == RTMeetKit.RTCVideoLayout.RTC_V_1X3) {
            mVideoView.setOnTouchListener(this);
        }
    }

    /**
     * 获取视频窗口的个数
     *
     * @return
     */
    public int GetVideoRenderSize() {
        int size = mRemoteRenders.size();
        if (mLocalRender != null) {
            size += 1;
        }
        return size;
    }

    /**
     * 切换本地图像和远程图像
     *
     * @param peerid 远程图像的peerid
     */
    public void SwitchLocalViewToOtherView(String peerid) {
        VideoView fullscrnView = mLocalRender;
        VideoView view1 = mRemoteRenders.get(peerid);
        int index, x, y, w, h;

        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;

        view1.index = fullscrnView.index;
        view1.x = fullscrnView.x;
        view1.y = fullscrnView.y;
        view1.w = fullscrnView.w;
        view1.h = fullscrnView.h;

        fullscrnView.index = index;
        fullscrnView.x = x;
        fullscrnView.y = y;
        fullscrnView.w = w;
        fullscrnView.h = h;

        fullscrnView.mLayout.setPosition(fullscrnView.x, fullscrnView.y, fullscrnView.w, fullscrnView.h);
        view1.mLayout.setPosition(view1.x, view1.y, view1.w, view1.h);
        updateVideoLayout(view1, fullscrnView);
    }

    /**
     * 交换两个图像的位置
     *
     * @param peerid1 图像1的peerid
     * @param peerid2 图像2的peerid
     */
    public void SwitchViewByPeerId(String peerid1, String peerid2) {
        VideoView view1 = mRemoteRenders.get(peerid1);
        VideoView view2 = mRemoteRenders.get(peerid2);
        int index, x, y, w, h;

        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;

        view1.index = view2.index;
        view1.x = view2.x;
        view1.y = view2.y;
        view1.w = view2.w;
        view1.h = view2.h;

        view2.index = index;
        view2.x = x;
        view2.y = y;
        view2.w = w;
        view2.h = h;

        view2.mLayout.setPosition(view2.x, view2.y, view2.w, view2.h);
        view1.mLayout.setPosition(view1.x, view1.y, view1.w, view1.h);
        updateVideoLayout(view1, view2);
    }

    private void SwitchViewToFullscreen(VideoView view1, VideoView fullscrnView) {
        int index, x, y, w, h;

        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;

        view1.index = fullscrnView.index;
        view1.x = fullscrnView.x;
        view1.y = fullscrnView.y;
        view1.w = fullscrnView.w;
        view1.h = fullscrnView.h;

        fullscrnView.index = index;
        fullscrnView.x = x;
        fullscrnView.y = y;
        fullscrnView.w = w;
        fullscrnView.h = h;

        fullscrnView.mLayout.setPosition(fullscrnView.x, fullscrnView.y, fullscrnView.w, fullscrnView.h);
        view1.mLayout.setPosition(view1.x, view1.y, view1.w, view1.h);

        updateVideoLayout(view1, fullscrnView);
    }

    private void SwitchView1ToFullscreen(VideoView view1, VideoView fullscrnView) {
        int index, x, y, w, h;

        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;

        view1.index = fullscrnView.index;
        view1.x = fullscrnView.x;
        view1.y = fullscrnView.y;
        view1.w = fullscrnView.w;
        view1.h = fullscrnView.h;

        fullscrnView.index = index;
        fullscrnView.x = x;
        fullscrnView.y = y;
        fullscrnView.w = w;
        fullscrnView.h = h;

        fullscrnView.mLayout.setPosition(fullscrnView.x, fullscrnView.y, fullscrnView.w, fullscrnView.h);
        view1.mLayout.setPosition(view1.x, view1.y, view1.w, view1.h);

        view1.mLayout.requestLayout();
        fullscrnView.mLayout.requestLayout();
        mVideoView.removeView(view1.mLayout);
        mVideoView.removeView(fullscrnView.mLayout);
        mVideoView.addView(view1.mLayout, 0);
        mVideoView.addView(fullscrnView.mLayout, 0);
    }

    private void SwitchViewPosition(VideoView view1, VideoView view2) {
        int index, x, y, w, h;
        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;

        view1.index = view2.index;
        view1.x = view2.x;
        view1.y = view2.y;
        view1.w = view2.w;
        view1.h = view2.h;

        view2.index = index;
        view2.x = x;
        view2.y = y;
        view2.w = w;
        view2.h = h;

        view1.mLayout.setPosition(view1.x, view1.y, view1.w, view1.h);
        view2.mLayout.setPosition(view2.x, view2.y, view2.w, view2.h);
        updateVideoLayout(view1, view2);
    }

    /**
     * 视频切换后更新视频的布局
     *
     * @param view1
     * @param view2
     */
    private void updateVideoLayout(VideoView view1, VideoView view2) {
        if (view1.Fullscreen()) {
            view1.mView.setZOrderMediaOverlay(false);
            view2.mView.setZOrderMediaOverlay(true);
            view1.mLayout.requestLayout();
            view2.mLayout.requestLayout();
            mVideoView.removeView(view1.mLayout);
            mVideoView.removeView(view2.mLayout);
            mVideoView.addView(view1.mLayout, -1);
            mVideoView.addView(view2.mLayout, 0);
        } else if (view2.Fullscreen()) {
            view1.mView.setZOrderMediaOverlay(true);
            view2.mView.setZOrderMediaOverlay(false);
            view2.mLayout.requestLayout();
            view1.mLayout.requestLayout();
            mVideoView.removeView(view1.mLayout);
            mVideoView.removeView(view2.mLayout);
            mVideoView.addView(view1.mLayout, 0);
            mVideoView.addView(view2.mLayout, -1);
        } else {
            view1.mLayout.requestLayout();
            view2.mLayout.requestLayout();
            mVideoView.removeView(view1.mLayout);
            mVideoView.removeView(view2.mLayout);
            mVideoView.addView(view1.mLayout, 0);
            mVideoView.addView(view2.mLayout, 0);
        }
    }

    /**
     * 切换第一个视频为全屏
     *
     * @param fullscrnView
     */
    private void SwitchIndex1ToFullscreen(VideoView fullscrnView) {
        VideoView view1 = null;
        if (mLocalRender != null && mLocalRender.index == 1) {
            view1 = mLocalRender;
        } else {
            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, VideoView> entry = iter.next();
                VideoView render = entry.getValue();
                if (render.index == 1) {
                    view1 = render;
                    break;
                }
            }
        }
        if (view1 != null) {
            SwitchView1ToFullscreen(view1, fullscrnView);
        }
    }

    public void BubbleSortSubView(VideoView view) {
        if (mLocalRender != null && view.index + 1 == mLocalRender.index) {
            SwitchViewPosition(mLocalRender, view);
        } else {
            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, VideoView> entry = iter.next();
                VideoView render = entry.getValue();
                if (view.index + 1 == render.index) {
                    SwitchViewPosition(render, view);
                    break;
                }
            }
        }
        if (view.index < mRemoteRenders.size()) {
            BubbleSortSubView(view);
        }
    }

    /**
     * 屏幕发生变化时变换图像的大小
     */
    private void screenChange() {
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            SUB_Y = 2;
            SUB_WIDTH = 20;
            SUB_HEIGHT = 24;
        } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            SUB_Y = 2;
            SUB_WIDTH = 24;
            SUB_HEIGHT = 20;
        }
    }

    /**
     * 根据模板更新视频界面的布局
     */
    private void updateVideoView() {
        if (mRTCVideoLayout == RTMeetKit.RTCVideoLayout.RTC_V_1X3) {
            screenChange();
//            int startPosition = (100 - SUB_WIDTH * mRemoteRenders.size()) / 2;
            int startPosition = (100 - SUB_WIDTH * mRemoteRenders.size() - 2);
            int remotePosition;
            int index;
            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, VideoView> entry = iter.next();

                VideoView render = entry.getValue();
                if (render.Fullscreen()) {
                    render = mLocalRender;
                    index = mLocalRender.index;
                } else {
                    index = render.index;
                }

//                render.y = (100 - 2 * (SUB_HEIGHT + SUB_Y));
                render.y = 2 * SUB_Y;

                remotePosition = startPosition + (index - 1) * SUB_WIDTH;
                render.x = remotePosition;

                if (!render.Fullscreen()) {
                    render.x = remotePosition;
                } else {
                    mLocalRender.x = remotePosition;
                }

                render.mLayout.setPosition(remotePosition, render.y, SUB_WIDTH, SUB_HEIGHT);
                render.mView.requestLayout();
            }
        } else if (mRTCVideoLayout == RTMeetKit.RTCVideoLayout.RTC_V_3X3_auto) {
            int size = mRemoteRenders.size();
            if (size == 0) {
                mLocalRender.mLayout.setPosition(0, 0, 100, 100);
                mLocalRender.mView.requestLayout();
            } else if (size == 1) {
                int X = 50;
                int Y = 30;
                int WIDTH = 50;
                int HEIGHT = 30;
                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, VideoView> entry = iter.next();

                    VideoView render = entry.getValue();
                    mLocalRender.mLayout.setPosition(0, Y, WIDTH, HEIGHT);
                    mLocalRender.mView.requestLayout();
                    if (render.index == 1) {
                        render.mLayout.setPosition(X, Y, WIDTH, HEIGHT);
                        render.mView.requestLayout();
                    }
                }
            } else if (size == 2) {
                int X = 50;
                int Y = 0;
                int WIDTH = 50;
                int HEIGHT = 30;
                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, VideoView> entry = iter.next();

                    VideoView render = entry.getValue();
                    mLocalRender.mLayout.setPosition(0, Y, WIDTH, HEIGHT);
                    mLocalRender.mView.requestLayout();
                    if (render.index == 1) {
                        render.mLayout.setPosition(X, Y, WIDTH, HEIGHT);
                        render.mView.requestLayout();
                    } else if (render.index == 2) {
                        render.mLayout.setPosition(X / 2, Y + HEIGHT, WIDTH, HEIGHT);
                        render.mView.requestLayout();
                    }
                }
            } else if (size == 3) {
                int X = 50;
                int Y = 0;
                int WIDTH = 50;
                int HEIGHT = 30;
                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, VideoView> entry = iter.next();

                    VideoView render = entry.getValue();
                    mLocalRender.mLayout.setPosition(0, Y, WIDTH, HEIGHT);
                    mLocalRender.mView.requestLayout();
                    if (render.index == 1) {
                        render.mLayout.setPosition(X, Y, WIDTH, HEIGHT);
                        render.mView.requestLayout();
                    } else if (render.index == 2) {
                        render.mLayout.setPosition(0, Y + HEIGHT, WIDTH, HEIGHT);
                        render.mView.requestLayout();
                    } else if (render.index == 3) {
                        render.mLayout.setPosition(X, Y + HEIGHT, WIDTH, HEIGHT);
                        render.mView.requestLayout();
                    }
                }
            } else if (size >= 4) {
                int X = 100 / 3;
                int Y = 0;
                int WIDTH = 100 / 3;
                int HEIGHT = 20;
                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, VideoView> entry = iter.next();

                    VideoView render = entry.getValue();
                    mLocalRender.mLayout.setPosition(0, Y, WIDTH, HEIGHT);
                    mLocalRender.mView.requestLayout();
                    if (render.index % 3 == 2) {
                        render.mLayout.setPosition(X * (render.index % 3), Y + (HEIGHT * (render.index / 3)), WIDTH + 1, HEIGHT);
                        render.mView.requestLayout();
                    } else {
                        render.mLayout.setPosition(X * (render.index % 3), Y + (HEIGHT * (render.index / 3)), WIDTH, HEIGHT);
                        render.mView.requestLayout();
                    }
                }
            }
        }
    }

    /**
     * 更新远端音频图像的状态
     *
     * @param peerId
     * @param flag
     */
    private void updateRemoteAudioImage(String peerId, boolean flag) {
        if (mRemoteRenders.containsKey(peerId)) {
            VideoView videoView = mRemoteRenders.get(peerId);
            videoView.mAudioShowFlag = !flag;
            videoView.updateView();
        }
    }

    /**
     * 更新本地音频状态
     *
     * @param flag
     */
    public void updateLocalAudioImage(boolean flag) {
        mLocalRender.mAudioShowFlag = flag;
        mLocalRender.updateView();
    }

    /**
     * 更新远端视频的状态
     *
     * @param peerId
     * @param flag
     */
    private void updateRemoteVideoImage(String peerId, boolean flag) {
        if (mRemoteRenders.containsKey(peerId)) {
            VideoView videoView = mRemoteRenders.get(peerId);
            videoView.mVideoShowFlag = !flag;
            videoView.updateView();
        }
    }

    /**
     * 更新本地视频的状态
     *
     * @param flag
     */
    public void updateLocalVideoImage(boolean flag) {
        mLocalRender.mVideoShowFlag = flag;
        mLocalRender.updateView();
    }

    /**
     * 关闭你摄像头
     */
    public void disableCamera() {
        if (mLocalRender == null  || mLocalRender.mLocalCamera ==null){
            return;
        }
        if (mRTCVideoLayout == RTMeetKit.RTCVideoLayout.RTC_V_1X3) {
            mLocalRender.mLocalCamera.setBackgroundColor(Color.BLACK);
        } else {
            mLocalRender.mLocalCamera.setBackgroundColor(Color.BLACK);
        }
    }

    /**
     * 打开摄像头
     */
    public void enableCamera() {
        if (mLocalRender == null  || mLocalRender.mLocalCamera ==null){
            return;
        }
        mLocalRender.mLocalCamera.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * 根据peerid记录每个视频的音视频状态
     *
     * @param peerId      每个视频图像的标识
     * @param audioEnable 音频状态
     * @param videoEnable 视频状态
     */
    public void OnRTCAVStatus(String peerId, boolean audioEnable, boolean videoEnable) {
        mAudioSetting.put(peerId, audioEnable);
        mVideoSetting.put(peerId, videoEnable);
        updateRemoteAudioImage(peerId, audioEnable);
        updateRemoteVideoImage(peerId, videoEnable);
    }

    /**
     * 更新图像中视频和音频的标识
     */
    private void updateImageFlag() {
        Iterator<Map.Entry<String, Boolean>> iterator = mVideoSetting.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Boolean> entry = iterator.next();
            String publishId = entry.getKey();
            Boolean videoFlag = entry.getValue();
            updateRemoteVideoImage(publishId, videoFlag);
        }

        iterator = mAudioSetting.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Boolean> entry = iterator.next();
            String publishId = entry.getKey();
            Boolean audioFlag = entry.getValue();
            updateRemoteAudioImage(publishId, audioFlag);
        }
    }

    /**
     * 获取全屏的界面
     *
     * @return
     */
    private VideoView GetFullScreen() {
        if (mLocalRender.Fullscreen()) {
            return mLocalRender;
        }
        Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, VideoView> entry = iter.next();
            String peerId = entry.getKey();
            VideoView render = entry.getValue();
            if (render.Fullscreen())
                return render;
        }
        return null;
    }

    /**
     * 横竖屏切换
     */
    public void onScreenChanged() {
        mScreenWidth = ScreenUtils.getScreenWidth(mContext);
        mScreenHeight = ScreenUtils.getScreenHeight(mContext) - ScreenUtils.getStatusHeight(mContext);

        if (mScreenHeight > mScreenWidth) {
            SUB_Y = 2;
            SUB_WIDTH = 24;
            SUB_HEIGHT = 20;
        } else {
            SUB_Y = 2;
            SUB_WIDTH = 20;
            SUB_HEIGHT = 24;
        }
        updateVideoView();
    }

    /**
     * Implements for AnyRTCViewEvents.
     */
    @Override
    public VideoRenderer OnRtcOpenLocalRender() {
        int size = GetVideoRenderSize();
        screenChange();
        if (size == 0) {
            if (mRTCVideoLayout == RTMeetKit.RTCVideoLayout.RTC_V_1X3) {
                mLocalRender = new VideoView("localRender", mVideoView.getContext(), mRootEglBase, 0, 0, 0, 100, 100, mRTCVideoLayout);
            } else {
                mLocalRender = new VideoView("localRender", mVideoView.getContext(), mRootEglBase, 0, 0, 0, 100, 100, mRTCVideoLayout);
            }
        } else {
            mLocalRender = new VideoView("localRender", mVideoView.getContext(), mRootEglBase, size, SUB_X, (100 - size * (SUB_HEIGHT + SUB_Y)), SUB_WIDTH, SUB_HEIGHT, mRTCVideoLayout);
        }
        if (mRTCVideoLayout == RTMeetKit.RTCVideoLayout.RTC_V_1X3) {
            mVideoView.addView(mLocalRender.mLayout, -1);
        } else {
            mVideoView.addView(mLocalRender.mLayout, 0);
        }


        mLocalRender.mLayout.setPosition(
                mLocalRender.x, mLocalRender.y, mLocalRender.w, mLocalRender.h);
        mLocalRender.mView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        mLocalRender.mRenderer = new VideoRenderer(mLocalRender.mView);
        return mLocalRender.mRenderer;
    }

    @Override
    public void OnRtcRemoveLocalRender() {
        if (mLocalRender != null) {
            mLocalRender.close();
            mLocalRender.mRenderer = null;
            mVideoView.removeView(mLocalRender.mLayout);
            mLocalRender = null;
        }
    }

    @Override
    public VideoRenderer OnRtcOpenRemoteRender(final String strRtcPeerId) {
        VideoView remoteRender = mRemoteRenders.get(strRtcPeerId);
        if (remoteRender == null) {
            int size = GetVideoRenderSize();
            if (size == 0) {
                remoteRender = new VideoView(strRtcPeerId, mVideoView.getContext(), mRootEglBase, 0, 0, 0, 100, 100, mRTCVideoLayout);
            } else {
                remoteRender = new VideoView(strRtcPeerId, mVideoView.getContext(), mRootEglBase, size, SUB_X, (100 - size * (SUB_HEIGHT + SUB_Y)), SUB_WIDTH, SUB_HEIGHT, mRTCVideoLayout);
                remoteRender.mView.setZOrderMediaOverlay(true);
            }

            mVideoView.addView(remoteRender.mLayout, 0);

            remoteRender.mLayout.setPosition(
                    remoteRender.x, remoteRender.y, remoteRender.w, remoteRender.h);
            remoteRender.mView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
            remoteRender.mRenderer = new VideoRenderer(remoteRender.mView);
            mRemoteRenders.put(strRtcPeerId, remoteRender);
            updateVideoView();
            updateImageFlag();
            if (isHost || (!isHost && strRtcPeerId.equals("LocalCameraRender"))) {
//                remoteRender.btnClose.setVisibility(View.VISIBLE);
                if (!isHost) {
                    remoteRender.layoutCamera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != mVideoClickEvent) {
                                mVideoClickEvent.OnSwitchCamera(v);
                            }
                        }
                    });
                }
                remoteRender.btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mVideoClickEvent) {
                            mVideoClickEvent.CloseVideoRender(v, strRtcPeerId);
                        }
                    }
                });
            }
            if (mRemoteRenders.size() == 1 && mLocalRender != null) {
                if (mRTCVideoLayout == RTMeetKit.RTCVideoLayout.RTC_V_1X3) {
                    SwitchViewToFullscreen(remoteRender, mLocalRender);
                }
            }
        }
        return remoteRender.mRenderer;
    }

    @Override
    public void OnRtcRemoveRemoteRender(String peerId) {
        VideoView remoteRender = mRemoteRenders.get(peerId);
        if (remoteRender != null) {
            if (remoteRender.Fullscreen()) {
                SwitchIndex1ToFullscreen(remoteRender);
            }

            if (mRemoteRenders.size() > 1 && remoteRender.index <= mRemoteRenders.size()) {
                BubbleSortSubView(remoteRender);
            }
            remoteRender.close();
            mVideoView.removeView(remoteRender.mLayout);
            mRemoteRenders.remove(peerId);
            updateVideoView();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int startX = (int) event.getX();
            int startY = (int) event.getY();
            if (mLocalRender.Hited(startX, startY)) {
                return true;
            } else {
                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, VideoView> entry = iter.next();
                    String peerId = entry.getKey();
                    VideoView render = entry.getValue();
                    if (render.Hited(startX, startY)) {
                        return true;
                    }
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            int startX = (int) event.getX();
            int startY = (int) event.getY();
            if (mLocalRender.Hited(startX, startY)) {
                mVideoClickEvent.onVideoTouch("localRender");
                SwitchViewToFullscreen(mLocalRender, GetFullScreen());
                return true;
            } else {
                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, VideoView> entry = iter.next();
                    String peerId = entry.getKey();
                    VideoView render = entry.getValue();
                    if (render.Hited(startX, startY)) {
                        mVideoClickEvent.onVideoTouch(peerId);
                        SwitchViewToFullscreen(render, GetFullScreen());
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
