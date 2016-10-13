package com.fanxin.huangfangyi.main.ulive.upload;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.adapter.LiveMessageAdapter;
import com.fanxin.huangfangyi.main.ulive.preference.Log2FileUtil;
import com.fanxin.huangfangyi.main.ulive.preference.Settings;
import com.fanxin.huangfangyi.ui.BaseActivity;
import com.fanxin.easeui.EaseConstant;
import com.fanxin.easeui.controller.EaseUI;
import com.fanxin.easeui.utils.EaseCommonUtils;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.ucloud.common.util.DeviceUtils;
import com.ucloud.common.util.StringUtil;
import com.ucloud.live.UEasyStreaming;
import com.ucloud.live.UStreamingProfile;
import com.ucloud.live.widget.UAspectFrameLayout;

import java.util.List;


public abstract class BasePublishDemo extends BaseActivity implements UEasyStreaming.UStreamingStateListener {
    private static final String TAG = "BasePublishDemo";

    public static final int MSG_UPDATE_COUNTDOWN = 1;

    public static final int COUNTDOWN_DELAY = 1000;

    public static final  int COUNTDOWN_START_INDEX = 3;
    public static final  int COUNTDOWN_END_INDEX = 1;


    protected Settings mSettings;

    protected String rtmpPushStreamDomain = "publish3.cdn.ucloud.com.cn";

    //Views
    protected ImageView mCameraToggleIv;
    protected ImageView mLampToggleIv;
    protected ImageButton mCloseRecorderImgBtn;
    protected ImageButton mToggleFilterImgBtn;
    protected Button mBackImgBtn;
    protected View mFocusIndex;
    protected TextView mBitrateTxtv;
    protected TextView mCountDownTxtv;
    protected TextView mRecordedTimeTxtv;
    protected TextView mOutputStreamInfoTxtv;
    protected TextView mBufferOverfloInfoTxtv;
    protected ViewGroup mContainer;

    protected UAspectFrameLayout mPreviewContainer;

    protected boolean isShutDownCountdown = false;

    protected UEasyStreaming mEasyStreaming;

    protected UStreamingProfile mStreamingProfile;

    protected UiHandler uiHandler;

    public abstract void initEnv();

    private ListView listView;
    private int chatType = EaseConstant.CHATTYPE_CHATROOM;
    private String toChatUsername;
    private Button btn_send;
    private EditText et_content;
    private List<EMMessage> msgList;
    LiveMessageAdapter adapter;
    private EMConversation conversation;
    protected int pagesize = 20;
    private ProgressDialog progressDialog;
    @Override
    public void onStateChanged(int type, Object event) {
       switch (type) {
            case UEasyStreaming.State.START_PREVIEW:
                Log.i(TAG, event.toString());
                handleShowStreamingInfo();
                if (this instanceof PublishDemo4MediaCodec) {
                    mEasyStreaming.applyFilter(UEasyStreaming.FILTER_BEAUTIFY_HIGH_PERFORMANCE);
//                mEasyStreaming.applyFilter(UEasyStreaming.FILTER_BEAUTIFY);
                }
                break;
            case UEasyStreaming.State.START_RECORDING:
                Log.i(TAG, event.toString());
                break;
            case UEasyStreaming.State.BUFFER_OVERFLOW:
                mBufferOverfloInfoTxtv.setText("unstable network stats:" + mEasyStreaming.getNetworkUnstableStats());
                Log.w(TAG, "unstable network");
                Toast.makeText(this, "unstable network", Toast.LENGTH_SHORT).show();
                break;
            case UEasyStreaming.State.MEDIA_MUXER_PREPARED_ERROR:
                Log.e(TAG, "prepare error, the publish stream id may be reuse, server error or network disconnect, try change one.");
                Toast.makeText(this, "the publish stream id may be reuse, server error or network disconnect, try change one.", Toast.LENGTH_LONG).show();
                break;
            case UEasyStreaming.State.MEDIA_MUXER_PREPARED_SUCCESS:
                Log.i(TAG, event.toString());
                break;
            case UEasyStreaming.State.MEDIA_INFO_SIGNATRUE_FAILED:
                Toast.makeText(this, event.toString(), Toast.LENGTH_LONG).show();
                break;
            case UEasyStreaming.State.MEDIA_INFO_NETWORK_SPEED:
                if (mBitrateTxtv != null) {
                    mBitrateTxtv.setVisibility(View.VISIBLE);
                    long speed = Long.valueOf(event.toString());
                    if (speed > 1024) {
                        mBitrateTxtv.setText(speed / 1024 + "K/s");
                    }
                    else {
                        mBitrateTxtv.setText(speed + "B/s");
                    }
                }
                break;
            case UEasyStreaming.State.MEDIA_INFO_PUBLISH_STREAMING_TIME:
                if (mRecordedTimeTxtv != null) {
                    mRecordedTimeTxtv.setVisibility(View.VISIBLE);
                    long time = Long.valueOf(event.toString());
                    String retVal = StringUtil.getTimeFormatString(time);
                    mRecordedTimeTxtv.setText(retVal);
                }
                break;
           case UEasyStreaming.State.MEDIA_ERROR_CAMERA_PREVIEW_SIZE_UNSUPPORT:
               Log.e(TAG, "MEDIA_ERROR_CAMERA_PREVIEW:" + event.toString());
               break;
        }
    }

    private class UiHandler extends Handler {
        public UiHandler(Looper looper) {
            super(looper);
        }
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_COUNTDOWN:
                    handleUpdateCountdown(msg.arg1);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("正在进入直播间...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mSettings = new Settings(this);
        setContentView(R.layout.live_layout_live_room_view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mSettings.getVideoCaptureOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        init();
        new Thread(){
            public void run() {
                int i = COUNTDOWN_START_INDEX;
                do {
                    try {
                        Thread.sleep(COUNTDOWN_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.what = MSG_UPDATE_COUNTDOWN;
                    msg.arg1 = i;
                    uiHandler.sendMessage(msg);
                    i--;
                }while(i >= COUNTDOWN_END_INDEX);
            }
        }.start();

        initViewChat();
    }

    private void init() {
        uiHandler = new UiHandler(getMainLooper());
        initView();
        initEnv();
    }

    private void initViewChat(){

        toChatUsername = FXConstant.FXLIVE_CHATROOM_ID;
        listView = (ListView) findViewById(R.id.list);
        listView.getBackground().setAlpha(100);
        btn_send = (Button) this.findViewById(R.id.btn_send);
        et_content = (EditText) this.findViewById(R.id.et_content);

        EMClient.getInstance().chatroomManager().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {
            @Override
            public void onSuccess(EMChatRoom emChatRoom) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getAllMessage();
                    }
                });

            }

            @Override
            public void onError(int i, String s) {
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           if(progressDialog!=null&&progressDialog.isShowing()){
                               progressDialog.dismiss();
                               Toast.makeText(getApplicationContext(),"初始化互动模块失败...",Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
            }
        });





    }


    private void initView() {
        mCameraToggleIv = (ImageView) findViewById(R.id.img_bt_switch_camera);
        mLampToggleIv = (ImageView) findViewById(R.id.img_bt_lamp);
        mCloseRecorderImgBtn = (ImageButton) findViewById(R.id.img_bt_close_record);
        mFocusIndex = findViewById(R.id.focus_index);
        mBitrateTxtv = (TextView) findViewById(R.id.bitrate_txtv);
        mPreviewContainer = (UAspectFrameLayout)findViewById(R.id.container);
        mCountDownTxtv = (TextView) findViewById(R.id.countdown_txtv);
        mRecordedTimeTxtv = (TextView) findViewById(R.id.recorded_time_txtv);
        mOutputStreamInfoTxtv = (TextView) findViewById(R.id.output_url_txtv);
        mToggleFilterImgBtn = (ImageButton) findViewById(R.id.img_bt_filter);
        mBufferOverfloInfoTxtv = (TextView) findViewById(R.id.network_overflow_count);
        mBackImgBtn = (Button) findViewById(R.id.btn_finish);
        mContainer = (ViewGroup) findViewById(R.id.live_finish_container);
        mCameraToggleIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mEasyStreaming != null) {
                    mEasyStreaming.switchCamera();
                }
            }
        });
        mLampToggleIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mEasyStreaming != null) {
                    mEasyStreaming.toggleFlashMode();
                }
            }

        });
        mCloseRecorderImgBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isShutDownCountdown = true;
                mCloseRecorderImgBtn.setEnabled(false);
                if (mEasyStreaming != null) {
                    mEasyStreaming.stopRecording();
                }
                mContainer.setVisibility(View.VISIBLE);
            }
        });
        mToggleFilterImgBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (BasePublishDemo.this instanceof PublishDemo4MediaCodec) {
                    mEasyStreaming.toggleFilter();
                } else {
                    Toast.makeText(BasePublishDemo.this, "Sorry, just support for mediacodec.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBackImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEasyStreaming.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEasyStreaming.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSettings.isOpenLogRecoder()) {
            Log2FileUtil.getInstance().stopLog();
        }
        mEasyStreaming.onDestory();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    public String bitrateMode(int value) {
        switch (value) {
            case UStreamingProfile.VIDEO_BITRATE_LOW: return "VIDEO_BITRATE_LOW";
            case UStreamingProfile.VIDEO_BITRATE_NORMAL: return "VIDEO_BITRATE_NORMAL";
            case UStreamingProfile.VIDEO_BITRATE_MEDIUM: return "VIDEO_BITRATE_MEDIUM";
            case UStreamingProfile.VIDEO_BITRATE_HIGH: return "VIDEO_BITRATE_HIGH";
            default: return value +"";
        }
    }

    public void handleShowStreamingInfo() {
        if (mOutputStreamInfoTxtv != null) {
            mOutputStreamInfoTxtv.setVisibility(View.VISIBLE);
           String info = "video width:" + mSettings.getVideoCaptureWidth()+ "\n" +
                    "video height:" + mSettings.getVideoCaptureHeight() + "\n" +
                    "video bitrate:" + bitrateMode(mSettings.getVideoEncodingBitRate()) + "\n" +
                    "video fps:" + mSettings.getVideoFrameRate() + "\n" +
                    "url:" + "rtmp://" + mStreamingProfile.getStream().getPublishDomain() + "/" + mStreamingProfile.getStream().getStreamId() + "\n" +
                    "brand:" + DeviceUtils.getDeviceBrand() + "_" + DeviceUtils.getDeviceModel() + "\n" +
                    "sdk version:" + com.ucloud.live.Build.VERSION + "\n" +
                    "android sdk version:" + android.os.Build.VERSION.SDK_INT + "\n" +
                    "codec type:" + (BasePublishDemo.this instanceof  PublishDemo4MediaCodec ? "mediacodec" : "x264") + "\n";
            mOutputStreamInfoTxtv.setText(info);
            Log.e(TAG, "@@" + info);
        }
    }

    public void handleUpdateCountdown(final int count) {
        if (mCountDownTxtv != null) {
            mCountDownTxtv.setVisibility(View.VISIBLE);
            mCountDownTxtv.setText(String.format("%d", count));
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(COUNTDOWN_DELAY);
            scaleAnimation.setFillAfter(false);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCountDownTxtv.setVisibility(View.GONE);

                    if (count == COUNTDOWN_END_INDEX && mEasyStreaming != null && !isShutDownCountdown) {
                        mEasyStreaming.startRecording();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (!isShutDownCountdown) {
                mCountDownTxtv.startAnimation(scaleAnimation);
            } else {
                mCountDownTxtv.setVisibility(View.GONE);
            }
        }
    }


    protected void getAllMessage() {


        // 获取当前conversation对象

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,
                EaseCommonUtils.getConversationType(chatType), true);
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }


        msgList = conversation.getAllMessages();
        adapter = new LiveMessageAdapter(msgList, BasePublishDemo.this);
        listView.setAdapter(adapter);
        listView.setSelection(listView.getCount() - 1);
        btn_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = et_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {

                    return;
                }
                setMesaage(content);
            }

        });
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }

    }

    private void setMesaage(String content) {

        // 创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        // 如果是群聊，设置chattype，默认是单聊
        if (chatType == EaseConstant.CHATTYPE_CHATROOM)
            message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute(FXConstant.KEY_USER_INFO, DemoApplication.getInstance().getUserJson().toJSONString());
        // 发送消息
        EMClient.getInstance().chatManager().sendMessage(message);

        msgList.add(message);

        adapter.notifyDataSetChanged();
        if (msgList.size() > 0) {
            listView.setSelection(listView.getCount() - 1);
        }
        et_content.setText("");

    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(toChatUsername)) {
                    msgList.addAll(messages);
                    adapter.notifyDataSetChanged();
                    if (msgList.size() > 0) {
                        et_content.setSelection(listView.getCount() - 1);

                    }

                }else {
                    // 如果消息不是和当前聊天ID的消息
                    EaseUI.getInstance().getNotifier().onNewMsg(message);
                }
            }

            // 收到消息
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            // 收到透传消息
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            // 收到已读回执
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            // 收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            // 消息状态变动
        }
    };
}