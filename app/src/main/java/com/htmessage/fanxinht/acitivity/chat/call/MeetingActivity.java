package com.htmessage.fanxinht.acitivity.chat.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;

import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CallMessage;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.utils.CommonUtils;

import org.anyrtc.meet_kit.RTMeetHelper;
import org.anyrtc.meet_kit.RTMeetKit;
import org.anyrtc.utils.RTMPAudioManager;
import org.webrtc.VideoRenderer;


public class MeetingActivity extends AppCompatActivity implements RTMeetHelper {

    public RTMeetKit mMeetKit;
    public RTCVideoView mVideoView;
    public RTMPAudioManager mRtcAudioManager = null;
    public RTMeetKit.RTCVideoLayout mLayoutMode;
    private boolean isCameraOpen = true, isAudioOpen = true;
    public final static int TIME = 50000;//响铃时间
    //
    public JSONObject data;
    public SoundPool soundPool;
    public int streamID = 0;
    public AudioManager audioManager;
    public String groupId;
    public String callId_add = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // setContentView(R.layout.ht_activity_meet);
        HTApp.isCalling = true;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        getIntentData();
        //  holderVoice();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("3005");
        intentFilter.addAction("4004");
        intentFilter.addAction("3004");
        intentFilter.addAction("5002");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    public String callId;
    public String mUserId;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("3005")) {
                CommonUtils.showToastShort(getApplicationContext(), "通话已挂断");
                finish();
            } else if (intent.getAction().equals("4004")) {
                videoTovoice();
            } else if (intent.getAction().equals("3004")) {
                CommonUtils.showToastShort(getApplicationContext(), "对方正在通话中");
                finish();
            } else if (intent.getAction().equals("5002")) {
                CommonUtils.showToastShort(getApplicationContext(), "群视频通话呼叫被取消");
                finish();
            }
        }
    };

    public void videoTovoice() {

    }


    private void getIntentData() {
        int action = getIntent().getIntExtra("action", 0);
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        if (action == 0) {
            mUserId = getIntent().getStringExtra("userId");
            callId = getIntent().getStringExtra("callId");
            groupId = getIntent().getStringExtra("groupId");
            if (callId == null) {
                callId = HTApp.getInstance().getUsername() + "_" + mUserId;
            }

        } else if (action == 3000 || action == 4000 || action == 5000) {
            String dataStri = getIntent().getStringExtra("data");
            if (dataStri == null) {
                return;
            }
            JSONObject dataTemp = JSONObject.parseObject(dataStri);
            mUserId = dataTemp.getString("userId");
            groupId = dataTemp.getString("groupId");
            callId = dataTemp.getString("callId");
            if (action == 5000) {
                isGroup = true;
            }

        }
        data = new JSONObject();
        JSONObject myInfoJson = HTApp.getInstance().getUserJson();
        data.put("userId", myInfoJson.getString("userId"));
        data.put("nick", myInfoJson.getString("nick"));
        data.put("avatar", myInfoJson.getString("avatar"));
        data.put("callId", callId);
        data.put("groupId", groupId);
    }


    public void startRTC(boolean isVoice) {
        int mode = getIntent().getIntExtra("mode", RTMeetKit.RTCVideoLayout.RTC_V_1X3.ordinal());
        if (mode == RTMeetKit.RTCVideoLayout.RTC_V_1X3.ordinal()) {
            //1x3模板
            mLayoutMode = RTMeetKit.RTCVideoLayout.RTC_V_1X3;
        } else if (mode == RTMeetKit.RTCVideoLayout.RTC_V_3X3_auto.ordinal()) {
            //3x3模板（微信模板）仅支持竖屏模式
            mLayoutMode = RTMeetKit.RTCVideoLayout.RTC_V_3X3_auto;
        }

        mMeetKit = new RTMeetKit(this, this);
        /**
         * 设置视频会议的模板
         */
        RTMeetKit.SetVideoLayout(mLayoutMode);
        mMeetKit.InitEngineWithAnyrtcInfo(HTConstant.DEVELOPERID, HTConstant.APPID, HTConstant.APPKEY, HTConstant.APPTOKEN);
        mVideoView = new RTCVideoView((RelativeLayout) findViewById(R.id.rl_rtc_videos), MeetingActivity.this, mMeetKit.Egl(), true, mLayoutMode);

        /**
         * 设置控件的点击事件
         */
        mVideoView.setBtnClickEvent(mBtnVideoCloseEvent);
        /**
         * 1x3模式下， 设置点击小图像切换至大屏
         */
        mVideoView.setVideoSwitchEnable(true);

        VideoRenderer render = mVideoView.OnRtcOpenLocalRender();
        mMeetKit.SetVideoCapturer(render.GetRenderPointer(), true);
        if (isVoice) {
            mVideoView.disableCamera();
            findViewById(R.id.rl_rtc_videos).setVisibility(View.INVISIBLE);
        }

        //mMeetKit.SetVideoEnable(false);
        //进入会议室
        mMeetKit.Join(callId);

        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        mRtcAudioManager = RTMPAudioManager.create(this, new Runnable() {
            // This method will be called each time the audio state (number
            // and
            // type of devices) has been changed.
            @Override
            public void run() {
                onAudioManagerChangedState();
            }
        });
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        mRtcAudioManager.init();

    }


    public boolean isGroup = false;

    public void sendCallMesssage(int action, HTChatManager.HTMessageCallBack callBack) {
        CallMessage callMessage = new CallMessage();

        JSONObject body = new JSONObject();
        //单聊语音
        body.put("action", action);
        data.put("callId_add", callId_add);
        if (isGroup) {
            callMessage.setChatType(ChatType.groupChat);
            callMessage.setTo(groupId);
        } else {
            callMessage.setTo(mUserId);
        }
        body.put("data", data);
        callMessage.setBody(body.toString());
        if (callBack != null) {
            HTClient.getInstance().chatManager().sendCallMessage(callMessage, callBack);
        } else {
            HTClient.getInstance().chatManager().sendCallMessage(callMessage, new HTChatManager.HTMessageCallBack() {
                @Override
                public void onProgress() {

                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure() {
                }
            });
        }


    }


    public boolean isChating = false;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int action = intent.getIntExtra("action", 0);
        if (action == 3003 || action == 4003) {
            CommonUtils.showToastShort(getApplicationContext(), "通话已接通");
            isChating = true;
            soundPool.release();
        } else if (action == 3001 || action == 3002 || action == 4001 || action == 4002) {
            String toastStr = "对方取消了呼叫";
            switch (action) {
                case 3002:
                    toastStr = "对方拒绝了通话申请";
                    break;

                case 4002:
                    toastStr = "对方拒绝了视频申请";
                    break;


            }
            CommonUtils.showToastShort(getApplicationContext(), toastStr);
            finish();

//            JSONObject dataTemp = (JSONObject) intent.getSerializableExtra("data");
//            if (dataTemp != null) {
//                mUserId = dataTemp.getString("userId");
//                callId = dataTemp.getString("callId");
//            }
        }

       /* if(){


        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMeetKit != null) {
            mMeetKit.Clear();
            mMeetKit = null;
        }
        // Close RTMPAudioManager
        if (mRtcAudioManager != null) {
            mRtcAudioManager.close();
            mRtcAudioManager = null;

        }
        if (mVideoView != null) {
            mVideoView.OnRtcRemoveLocalRender();
        }
        HTApp.isCalling = false;
        if (releaseHandler != null) {

            releaseHandler.removeCallbacks(runnable);
        }
        if (soundPool != null) {
            soundPool.release();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null) {
            mVideoView.onScreenChanged();
        }
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 点击事件
     *
     * @param btn
     */
    public void OnBtnClicked(View btn) {
        if (btn.getId() == R.id.btn_hangup) {
            if (isChating) {
                mMeetKit.Leave();
            }
            sendOverMessage();
            finish();

        } else if (btn.getId() == R.id.btn_audio) {
            if (null != mVideoView) {
                mVideoView.updateLocalAudioImage(isAudioOpen);
            }
            mMeetKit.SetAudioEnable(!isAudioOpen);
            isAudioOpen = !isAudioOpen;
            ((ImageButton) findViewById(R.id.btn_audio)).setImageResource(isAudioOpen ? R.drawable.mute : R.drawable.mute_selected);
        } else if (btn.getId() == R.id.btn_camera) {
            if (null != mVideoView) {
                /**
                 * 3x3模式下设置屏蔽本地图像
                 */
                if (null != mVideoView) {
                    mVideoView.updateLocalVideoImage(isCameraOpen);
                }

                if (isCameraOpen) {
                    mVideoView.disableCamera();
                } else {
                    mVideoView.enableCamera();
                }
            }
            mMeetKit.SetVideoEnable(!isCameraOpen);
            isCameraOpen = !isCameraOpen;
            ((ImageButton) findViewById(R.id.btn_camera)).setBackgroundResource(isCameraOpen ? R.drawable.open_cmaera_numal : R.drawable.open_cmaera_press);
        } else if (btn.getId() == R.id.btn_swtich_camera) {
            mMeetKit.SwitchCamera();
        } else if (btn.getId() == R.id.btn_speaker) {
            if (audioManager.isSpeakerphoneOn()) {
                setSpeakerphoneOn(false);
                ((ImageButton) findViewById(R.id.btn_speaker)).setImageResource(R.drawable.speaker_out_selected);
            } else {
                setSpeakerphoneOn(true);
                ((ImageButton) findViewById(R.id.btn_speaker)).setImageResource(R.drawable.speaker_out);
            }
        } else if (btn.getId() == R.id.tv_check_people) {
            startCheckAgain();
        }
    }

    public void sendOverMessage() {
    }

    private void onAudioManagerChangedState() {
        // TODO(henrika): disable video if
        // AppRTCAudioManager.AudioDevice.EARPIECE
        // is active.
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    /**
     * 关闭连接按钮、切换摄像头按钮、点击视频图像的回调
     */
    private RTCVideoView.ViewClickEvent mBtnVideoCloseEvent = new RTCVideoView.ViewClickEvent() {

        @Override
        public void CloseVideoRender(View view, String strPeerId) {
            //暂时无用
        }

        @Override
        public void OnSwitchCamera(View view) {
            //暂时无用
        }

        @Override
        public void onVideoTouch(String strPeerId) {
            //设置点击小图像切换大图像的情况下， 回调图像唯一的标识strPeerId，peerid可与每个用户一对一绑定起来，用于标识具体是哪个用户的图像
        }
    };

    /**
     * Implements for RTMeetHelper
     */
    @Override
    public void OnRtcJoinMeetOK(String strAnyrtcId) {

    }


    @Override
    public void OnRtcJoinMeetFailed(String strAnyrtcId, int code, String strReason) {

    }

    @Override
    public void OnRtcLeaveMeet(int code) {

    }

    @Override
    public void OnRTCOpenVideoRender(final String strLivePeerID) {
        MeetingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //根据peerid添加图像，每一个图像的唯一标识是peerid
                final VideoRenderer render = mVideoView.OnRtcOpenRemoteRender(strLivePeerID);
                if (null != render) {
                    mMeetKit.SetRTCVideoRender(strLivePeerID, render.GetRenderPointer());
                }
            }
        });
    }

    @Override
    public void OnRTCCloseVideoRender(final String strLivePeerID) {
        MeetingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mMeetKit) {
                    mMeetKit.SetRTCVideoRender(strLivePeerID, 0);
                    /**
                     * 根据peerid删除图像
                     */
                    mVideoView.OnRtcRemoveRemoteRender(strLivePeerID);
                }
            }
        });
    }

    @Override
    public void OnRTCAVStatus(final String sstrLivePeerID, final boolean audio, final boolean video) {

        MeetingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mVideoView != null) {
                    /**
                     * 更新每个图像的音视频状态
                     */
                    mVideoView.OnRTCAVStatus(sstrLivePeerID, audio, video);
                }
            }
        });
    }

    @Override
    public void OnRTCAudioActive(final String sstrLivePeerID, final int showTime) {
        MeetingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //每个图像的音频实时监测
            }
        });
    }


    //方法：SwitchViewByPeerId(peerid1,peerid2)，根据peerid切换图像位置;
    //方法：GetVideoRenderSize()，获取当前视频窗口的个数


    public void setSpeakerphoneOn(boolean on) {
        if (on) {
            audioManager.setSpeakerphoneOn(true);
        } else {
            audioManager.setSpeakerphoneOn(false);//关闭扬声器
            audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            //把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
    }

    public void intMediaPlayer(int raw, final boolean isOutiong1) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        }
        streamID = soundPool.load(this, raw, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {

                soundPool.play(1, 1, 1, 0, -1, 1);
                if (isOutiong1) {
                    releaseHandler.postDelayed(runnable, TIME);
                }

            }
        });


    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            releaseHandler.sendEmptyMessage(1000);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        holdSoundPool();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (soundPool != null && streamID != 0) {
            soundPool.resume(streamID);
        }

    }

    private void holdSoundPool() {
        Log.d("streamID--->", streamID + "");
        if (soundPool != null) {
            soundPool.pause(streamID);

        }
    }

    public Handler releaseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                if (soundPool != null) {
                    soundPool.release();
                }
                timeOver();
                CommonUtils.showToastShort(getApplicationContext(), "对方无应答");
                finish();
            }

        }
    };

    public void timeOver() {

    }

    public void startCheckAgain() {
    }
}
