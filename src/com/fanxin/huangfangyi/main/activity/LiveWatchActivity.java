package com.fanxin.huangfangyi.main.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.adapter.LiveMessageAdapter;
import com.fanxin.huangfangyi.main.uvod.preference.Settings;
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
import com.ucloud.player.widget.v2.UVideoView;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class LiveWatchActivity extends BaseActivity implements UVideoView.Callback {
    public static final String TAG = LiveWatchActivity.class.getSimpleName();

    @Bind(R.id.videoview)
    UVideoView mVideoView;


    private String mUri;

    Settings mSettings;

   // protected EaseChatInputMenu inputMenu;



    private ListView listView;
    private int chatType = EaseConstant.CHATTYPE_CHATROOM;
    private String toChatUsername;
    private Button btn_send;
    private EditText et_content;
    private List<EMMessage> msgList;
    LiveMessageAdapter adapter;
    private EMConversation conversation;
    protected int pagesize = 20;
    private  ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle bundles) {
        super.onCreate(bundles);
        setContentView(R.layout.activity_video_demo2);
        ButterKnife.bind(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("正在进入直播间...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mSettings = new Settings(this);

        mUri = getIntent().getStringExtra("videoPath");

        mVideoView.setPlayType(UVideoView.PlayType.LIVE);  //UVideoView.PlayType.NORMAL 点播  UVideoView.PlayType.LIVE 直播

        mVideoView.setDecoder(UVideoView.DECODER_VOD_SW);
        mVideoView.setRatio(UVideoView.VIDEO_RATIO_MATCH_PARENT);
        mVideoView.setPlayMode(UVideoView.PlayMode.REPEAT);

        mVideoView.setVideoPath(mUri);
        mVideoView.registerCallback(this);

        initView();

//        listView.setAdapter(new SimpleAdapter());
//        inputMenu = (EaseChatInputMenu) findViewById(R.id.input_menu);
//        inputMenu.init(null);
//        inputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {
//
//            @Override
//            public void onSendMessage(String content) {
//                //sendTextMessage(content);
//            }
//
//            @Override
//            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
////				return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderView.EaseVoiceRecorderCallback() {
////
////					@Override
////					public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
////						sendVoiceMessage(voiceFilePath, voiceTimeLength);
////					}
////				});
//                return false;
//            }
//
//            @Override
//            public void onBigExpressionClicked(EaseEmojicon emojicon) {
//                //sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
//            }
//        });

        //	messageList.init("10000", 1, null);
    }

    private void initView(){

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

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            int currentPosition = mVideoView.getCurrentPosition();
            Log.d(TAG, "save currentPosition:" + currentPosition);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            if (mVideoView.isInPlaybackState()) {
                mVideoView.stopPlayback();
            }
            mVideoView.release(true);
        }
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }


    @Override
    public void onEvent(int i, String s) {

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
        adapter = new LiveMessageAdapter(msgList, LiveWatchActivity.this);
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
