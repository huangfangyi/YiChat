package com.htmessage.fanxinht.anyrtc.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.anyrtc.Config.ChatMessageBean;
import com.htmessage.fanxinht.anyrtc.Utils.RTMPCHttpSDK;
import com.htmessage.fanxinht.anyrtc.Utils.SoftKeyboardUtil;
import com.htmessage.fanxinht.anyrtc.Utils.ThreadUtil;
import com.htmessage.fanxinht.anyrtc.adapter.LiveChatAdapter;
import com.htmessage.fanxinht.anyrtc.weight.ScrollRecycerView;
import com.htmessage.fanxinht.HTConstant;
import com.opendanmaku.DanmakuItem;
import com.opendanmaku.DanmakuView;
import com.opendanmaku.IDanmakuItem;

import org.anyrtc.rtmpc_hybrid.RTMPCAbstractHoster;
import org.anyrtc.rtmpc_hybrid.RTMPCHosterKit;
import org.anyrtc.rtmpc_hybrid.RTMPCHybird;
import org.anyrtc.rtmpc_hybrid.RTMPCVideoView;
import org.anyrtc.utils.RTMPAudioManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：AnyRTCTest
 * 类描述：AnyHosterActivity 描述: 直播界面
 * 创建人：songlijie
 * 创建时间：2016/11/9 12:33
 * 邮箱:814326663@qq.com
 */
public class AnyHosterActivity extends BaseActivity implements View.OnClickListener, ScrollRecycerView.ScrollPosation {
    private String TAG = AnyHosterActivity.class.getSimpleName();
    private String mNickname;
    private String mRtmpPushUrl;
    private String mAnyrtcId;
    private String mHlsUrl;
    private String mGuestId;
    private String mUserData;
    private String mTopic;
    private String mHosterId;
    private String mVodSvrId;
    private String mVodResTag;
    private String header;
    private RTMPCHosterKit mHosterKit;
    private RTMPCVideoView mVideoView;
    private boolean mStartRtmp = false;
    private SoftKeyboardUtil softKeyboardUtil;
    private int duration = 100;//软键盘延迟打开时间
    private CheckBox mCheckBarrage;
    private DanmakuView mDanmakuView;
    private EditText editMessage;
    private ViewAnimator vaBottomBar;
    private LinearLayout llInputSoft;
    private FrameLayout flChatList;
    private ScrollRecycerView rcLiveChat;
    private ImageView btnChat;
    private ImageView iv_back, iv_camera;
    private TextView tv_title;
    private RelativeLayout titleBar;
    private List<ChatMessageBean> mChatMessageList;
    private LiveChatAdapter mChatLiveAdapter;
    private com.alibaba.fastjson.JSONObject userJson;
    private int maxMessageList = 150; //列表中最大 消息数目
    private RelativeLayout rl_rtmpc_videos;
    private Dialog dialog;
    private RTMPAudioManager mRtmpAudioManager = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_anyhoster);
        getDate();
        initView();
        iniData();
        setOnClick();
    }

    private void iniData() {
        iv_camera.setBackgroundResource(R.drawable.camera_switch_selector);
        tv_title.setText(mTopic);
        titleBar.setBackgroundColor(getResources().getColor(R.color.transparent));
        mChatLiveAdapter = new LiveChatAdapter(mChatMessageList, this);
        rcLiveChat.setLayoutManager(new LinearLayoutManager(this));
        rcLiveChat.setAdapter(mChatLiveAdapter);
        rcLiveChat.addScrollPosation(this);
        setEditTouchListener();
        vaBottomBar.setAnimateFirstView(true);
        //设置流
        setStream();
    }

    private void setStream() {
        //设置横屏模式 当主播端设置后， 观众端也必须设置为横屏模式，也可在sdk初始化时进行设置
        //RTMPCHybird.Inst().SetScreenToLandscape();
        mVideoView = new RTMPCVideoView(rl_rtmpc_videos, RTMPCHybird.Inst().Egl(), true,false);
        mVideoView.setBtnCloseEvent(mBtnVideoCloseEvent);
        mHosterKit = new RTMPCHosterKit(this, mHosterListener);
        {
            VideoRenderer render = mVideoView.OnRtcOpenLocalRender(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            mHosterKit.SetVideoCapturer(render.GetRenderPointer(), true);
        }
        {
            // Create and audio manager that will take care of audio routing,
            // audio modes, audio device enumeration etc.
            mRtmpAudioManager = RTMPAudioManager.create(this, new Runnable() {
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
            mRtmpAudioManager.init();
        }
        mStartRtmp = true;
        /**
         * 设置自适应码流
         */
        mHosterKit.SetNetAdjustMode(RTMPCHosterKit.RTMPNetAdjustMode.RTMP_NA_Fast);
        /**
         * 开始推流
         */
        mHosterKit.StartPushRtmpStream(mRtmpPushUrl);
        /**
         * 建立RTC连线连接
         */
        mHosterKit.OpenRTCLine(mAnyrtcId, mHosterId, mUserData);
    }

    private void getDate() {
        userJson = HTApp.getInstance().getUserJson();
        mChatMessageList = new ArrayList<ChatMessageBean>();
        mNickname = getIntent().getExtras().getString("nickname");
        mHosterId = getIntent().getExtras().getString("hosterId");
        mRtmpPushUrl = getIntent().getExtras().getString("rtmp_url");
        mAnyrtcId = getIntent().getExtras().getString("andyrtcId");
        mUserData = getIntent().getExtras().getString("userData");
        mHlsUrl = getIntent().getExtras().getString("hls_url");
        mTopic = getIntent().getExtras().getString("topic");
        header =getIntent().getExtras().getString("headUrl");//主播头像地址
    }

    private void setOnClick() {
        iv_back.setOnClickListener(this);
        iv_camera.setOnClickListener(this);
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        tv_title = (TextView) findViewById(R.id.tv_title);
        titleBar = (RelativeLayout) findViewById(R.id.title);

        mDanmakuView = (DanmakuView) findViewById(R.id.danmakuView);
        mCheckBarrage = (CheckBox) findViewById(R.id.check_barrage);
        editMessage = (EditText) findViewById(R.id.edit_message);
        vaBottomBar = (ViewAnimator) findViewById(R.id.va_bottom_bar);
        llInputSoft = (LinearLayout) findViewById(R.id.ll_input_soft);
        flChatList = (FrameLayout) findViewById(R.id.fl_chat_list);
        btnChat = (ImageView) findViewById(R.id.iv_host_text);
        rcLiveChat = (ScrollRecycerView) findViewById(R.id.rc_live_chat);

        rl_rtmpc_videos = (RelativeLayout) findViewById(R.id.rl_rtmpc_videos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDanmakuView.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDanmakuView.hide();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ShowExitDialog();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDanmakuView.clear();
        softKeyboardUtil.removeGlobalOnLayoutListener(this);

        if (mVodSvrId != null && mVodSvrId.length() > 0 && mVodResTag.length() > 0) {
            //关闭录像
            RTMPCHttpSDK.CloseRecRtmpStream(getApplicationContext(), RTMPCHybird.Inst().GetHttpAddr(), HTConstant.DEVELOPERID, HTConstant.APPID,
                    HTConstant.APPTOKEN, mVodSvrId, mVodResTag);
        }

        // Close RTMPAudioManager
        if (mRtmpAudioManager != null) {
            mRtmpAudioManager.close();
            mRtmpAudioManager = null;

        }

        if (mHosterKit != null) {
            mVideoView.OnRtcRemoveLocalRender();
            mHosterKit.Clear();
            mHosterKit = null;
        }
    }

    private void onAudioManagerChangedState() {
        // TODO(henrika): disable video if
        // AppRTCAudioManager.AudioDevice.EARPIECE
        // is active.
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    public void OnBtnClicked(View btn) {
        if (btn.getId() == R.id.btn_send_message) {
            String message = editMessage.getText().toString();
            editMessage.setText("");
            if (TextUtils.isEmpty(message)) {
                return;
            }
            if (mCheckBarrage.isChecked()) {
                mHosterKit.SendBarrage(mNickname, header, message);
                IDanmakuItem item = new DanmakuItem(AnyHosterActivity.this, new SpannableString(mNickname + ":" + message), mDanmakuView.getWidth(), 0, R.color.icon_press, 18, 1);
                mDanmakuView.addItemToHead(item);
            } else {
                mHosterKit.SendUserMsg(mNickname, header, message);
                addChatMessageList(new ChatMessageBean(mNickname, mNickname, header, message));//TODO 此处弹幕开关未开启时,消息在消息列表显示
            }
//         addChatMessageList(new ChatMessageBean(mNickname, mNickname,header, message));//TODO 此处是把发送的消息都添加到消息列表中 不论是弹幕还是消息
        } else if (btn.getId() == R.id.iv_host_text) {
            btnChat.clearFocus();
            vaBottomBar.setDisplayedChild(1);
            editMessage.requestFocus();
            softKeyboardUtil.showKeyboard(AnyHosterActivity.this, editMessage);
        }
    }

    /**
     * 连线时小图标的关闭连接按钮及切换摄像头按钮
     */
    private RTMPCVideoView.BtnVideoCloseEvent mBtnVideoCloseEvent = new RTMPCVideoView.BtnVideoCloseEvent() {

        @Override
        public void CloseVideoRender(View view, String strPeerId) {
            /**
             * 挂断连线
             */
            mHosterKit.HangupRTCLine(strPeerId);
        }

        @Override
        public void OnSwitchCamera(View view) {
            /**
             * 切换摄像头
             */
            mHosterKit.SwitchCamera();
        }
    };

    /**
     * 更细列表
     *
     * @param chatMessageBean
     */
    private void addChatMessageList(ChatMessageBean chatMessageBean) {
        // 150 条 修改；

        if (mChatMessageList == null) {
            return;
        }

        if (mChatMessageList.size() < maxMessageList) {
            mChatMessageList.add(chatMessageBean);
        } else {
            mChatMessageList.remove(0);
            mChatMessageList.add(chatMessageBean);
        }
        mChatLiveAdapter.notifyDataSetChanged();
        rcLiveChat.smoothScrollToPosition(mChatMessageList.size() - 1);
    }

    /**
     * 设置 键盘的监听事件
     */
    private void setEditTouchListener() {
        softKeyboardUtil = new SoftKeyboardUtil();

        softKeyboardUtil.observeSoftKeyboard(AnyHosterActivity.this, new SoftKeyboardUtil.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyBoardChange(int softKeybardHeight, boolean isShow) {
                if (isShow) {
                    ThreadUtil.runInUIThread(new Runnable() {
                        @Override
                        public void run() {
                            llInputSoft.animate().translationYBy(-editMessage.getHeight() / 2).setDuration(100).start();
                            flChatList.animate().translationYBy(-editMessage.getHeight() / 2).setDuration(100).start();
                        }
                    }, duration);
                } else {
                    btnChat.requestFocus();
                    vaBottomBar.setDisplayedChild(0);
                    llInputSoft.animate().translationYBy(editMessage.getHeight() / 2).setDuration(100).start();
                    flChatList.animate().translationYBy(editMessage.getHeight() / 2).setDuration(100).start();
                }
            }
        });
    }

    /**
     * 连线弹窗
     *
     * @param context
     * @param strLivePeerID
     * @param strCustomID
     */
    private Dialog ShowDialog(Context context, final String strLivePeerID, final String strCustomID, final String strUserData) {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.layout_dialog_rtc, null);
        TextView tv_delete_title = (TextView) view.findViewById(R.id.tv_delete_title);
        TextView tv_rtc_nick = (TextView) view.findViewById(R.id.tv_rtc_nick);
        ImageView iv_trc_avatar = (ImageView) view.findViewById(R.id.iv_trc_avatar);
        tv_delete_title.setText(getString(R.string.str_connect_hoster));
        tv_rtc_nick.setText(String.format(getString(R.string.str_apply_connect_line), strCustomID));
        TextView tv_cancle = (TextView) view.findViewById(R.id.tv_cancle);
        tv_cancle.setText(R.string.str_refused);
        TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        tv_ok.setText(R.string.str_agree);
        Log.d("slj","连麦头像:"+strUserData +"---strLivePeerID:"+ strLivePeerID+"----strCustomID:"+strCustomID );
        Glide.with(context).load(strUserData).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(iv_trc_avatar);
        build.setView(view);
        build.setCancelable(false);
        final AlertDialog dialog = build.create();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                // TODO Auto-generated method stub
                /**
                 * 主播接受连线请求
                 */
                mHosterKit.AcceptRTCLine(strLivePeerID);
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                // TODO Auto-generated method stub
                /**
                 * 主播拒绝连线请求
                 */
                mHosterKit.RejectRTCLine(strLivePeerID, true);
            }
        });
        return dialog;
    }

    private void ShowExitDialog() {
        AlertDialog.Builder build = new AlertDialog.Builder(AnyHosterActivity.this);
        View view = View.inflate(AnyHosterActivity.this, R.layout.layout_dialog_rtc, null);
        TextView tv_delete_title = (TextView) view.findViewById(R.id.tv_delete_title);
        TextView tv_rtc_nick = (TextView) view.findViewById(R.id.tv_rtc_nick);
        TextView tv_cancle = (TextView) view.findViewById(R.id.tv_cancle);
        TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        ImageView iv_trc_avatar = (ImageView) view.findViewById(R.id.iv_trc_avatar);
        iv_trc_avatar.setVisibility(View.GONE);
        tv_delete_title.setText(getString(R.string.str_exit));
        tv_rtc_nick.setText(R.string.str_live_stop);
        build.setView(view);
        final AlertDialog dialog = build.show();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                // TODO Auto-generated method stub
                mStartRtmp = false;
                mHosterKit.StopRtmpStream();
                finish();
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    @Override
    public void ScrollButtom() {

    }

    @Override
    public void ScrollNotButtom() {

    }

    /**
     * 主播回调信息接口
     */
    private RTMPCAbstractHoster mHosterListener = new RTMPCAbstractHoster() {
        /**
         * rtmp连接成功
         */
        @Override
        public void OnRtmpStreamOKCallback() {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    //开始录像
//                    RTMPCHttpSDK.RecordRtmpStream(AnyHosterActivity.this, RTMPCHybird.Inst().GetHttpAddr(), HTConstant.DEVELOPERID, HTConstant.APPID,
//                            HTConstant.APPTOKEN, mAnyrtcId, mRtmpPushUrl, mAnyrtcId, new RTMPCHttpSDK.RTMPCHttpCallback() {
//                                @Override
//                                public void OnRTMPCHttpOK(String strContent) {
//                                    try {
//                                        JSONObject recJson = new JSONObject(strContent);
//                                        mVodSvrId = recJson.getString("VodSvrId");
//                                        mVodResTag = recJson.getString("VodResTag");
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void OnRTMPCHttpFailed(int code) {
//
//                                }
//                            });
                }
            });
        }

        /**
         * rtmp 重连次数
         * @param times 重连次数
         */
        @Override
        public void OnRtmpStreamReconnectingCallback(final int times) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        /**
         * rtmp 推流状态
         * @param delayMs 推流延时
         * @param netBand 推流码流
         */
        @Override
        public void OnRtmpStreamStatusCallback(final int delayMs, final int netBand) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        /**
         * rtmp推流失败回调
         * @param code
         */
        @Override
        public void OnRtmpStreamFailedCallback(int code) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        /**
         * rtmp 推流关闭回调
         */
        @Override
        public void OnRtmpStreamClosedCallback() {
            finish();
        }

        @Override
        public void OnRtmpAudioLevelCallback(String s, int i) {

        }

        /**
         * RTC 连接回调
         * @param code 0： 连接成功
         * @param strErr 原因
         */
        @Override
        public void OnRTCOpenLineResultCallback(final int code, String strErr) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        /**
         * 游客有申请连线回调
         *
         * @param strLivePeerID
         * @param strCustomID
         * @param strUserData
         */
        @Override
        public void OnRTCApplyToLineCallback(final String strLivePeerID, final String strCustomID, final String strUserData) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = new JSONObject(strUserData);
                        String headUrl = object.getString("headUrl");
                    if (dialog ==null){
                        dialog = ShowDialog(AnyHosterActivity.this, strLivePeerID, strCustomID, headUrl);
                        dialog.show();
                    }else{
                        if (dialog.isShowing()){
                            dialog.dismiss();
                            dialog.show();
                        }else{
                            dialog.show();
                        }
                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * 视频连线超过4人时回调
         * @param strLivePeerID
         * @param strCustomID
         * @param strUserData
         */
        @Override
        public void OnRTCLineFullCallback(final String strLivePeerID, String strCustomID, String strUserData) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AnyHosterActivity.this, getString(R.string.str_connect_full), Toast.LENGTH_LONG).show();
                    mHosterKit.RejectRTCLine(strLivePeerID, true);
                }
            });
        }

        /**
         * 游客挂断连线回调
         * @param strLivePeerID
         */
        @Override
        public void OnRTCCancelLineCallback(String strLivePeerID) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dialog !=null){
                        dialog.dismiss();
                    }
                    Toast.makeText(AnyHosterActivity.this, getString(R.string.str_line_disconnect), Toast.LENGTH_LONG).show();
                }
            });
        }

        /**
         * RTC 连接关闭回调
         * @param code 207：请去AnyRTC官网申请账号,如有疑问请联系客服!
         * @param strReason
         */
        @Override
        public void OnRTCLineClosedCallback(final int code, String strReason) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 207) {
                        Toast.makeText(AnyHosterActivity.this, getString(R.string.str_apply_anyrtc_account), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
        }
        /**
         * 连线接通时的视频图像回调；
         * @param strLivePeerID
         */
        @Override
        public void OnRTCOpenVideoRenderCallback(final String strLivePeerID, String s1) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final VideoRenderer render = mVideoView.OnRtcOpenRemoteRender(strLivePeerID, RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                    if (null != render) {
                        mHosterKit.SetRTCVideoRender(strLivePeerID, render.GetRenderPointer());
                    }
                }
            });
        }
        /**
         * 连线关闭时的视频图像回调；
         * @param strLivePeerID
         */
        @Override
        public void OnRTCCloseVideoRenderCallback(final String strLivePeerID, String s1) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mHosterKit.SetRTCVideoRender(strLivePeerID, 0);
                    mVideoView.OnRtcRemoveRemoteRender(strLivePeerID);
                }
            });
        }

        @Override
        public void OnRTCOpenAudioLineCallback(String s, String s1) {

        }

        @Override
        public void OnRTCCloseAudioLineCallback(String s, String s1) {

        }

        /**
         * 消息回调
         * @param strCustomID 消息的发送者id
         * @param strCustomName 消息的发送者昵称
         * @param strCustomHeader 消息的发送者头像url
         * @param strMessage 消息内容
         */
        @Override
        public void OnRTCUserMessageCallback(final String strCustomID, final String strCustomName, final String strCustomHeader, final String strMessage) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addChatMessageList(new ChatMessageBean(strCustomID, strCustomName, strCustomHeader, strMessage));
                }
            });
        }

        /**
         * 弹幕回调
         * @param strCustomID 弹幕的发送者id
         * @param strCustomName 弹幕的发送者昵称
         * @param strCustomHeader 弹幕的发送者头像url
         * @param strBarrage 弹幕的内容
         */
        @Override
        public void OnRTCUserBarrageCallback(final String strCustomID, final String strCustomName, final String strCustomHeader, final String strBarrage) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    addChatMessageList(new ChatMessageBean(strCustomID, strCustomName, strCustomHeader, strBarrage)); //TODO 把获取到的弹幕消息添加到消息列表
                    IDanmakuItem item = new DanmakuItem(AnyHosterActivity.this, new SpannableString(strCustomName + ":" + strBarrage), mDanmakuView.getWidth(), 0, R.color.icon_press, 18, 1);
                    mDanmakuView.addItemToHead(item);
                }
            });
        }

        /**
         * 直播观看总人数回调
         * @param totalMembers 观看总人数
         */
        @Override
        public void OnRTCMemberListWillUpdateCallback(final int totalMembers) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.txt_watcher_number)).setText(String.format(getString(R.string.str_live_watcher_number), totalMembers));
                }
            });
        }

        /**
         * 人员上下线回调
         * @param strCustomID
         * @param strUserData
         */
        @Override
        public void OnRTCMemberCallback(final String strCustomID, final String strUserData) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject userData = new JSONObject(strUserData);
                        addChatMessageList(new ChatMessageBean(userData.getString("nickName"), "", userData.getString("headUrl"), ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * 直播观看总人数回调结束
         */
        @Override
        public void OnRTCMemberListUpdateDoneCallback() {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                ShowExitDialog();
                break;
            case R.id.iv_camera:
                mHosterKit.SwitchCamera();
                break;
        }
    }
}
