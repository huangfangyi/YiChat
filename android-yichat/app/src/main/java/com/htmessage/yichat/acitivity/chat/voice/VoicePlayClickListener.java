package com.htmessage.yichat.acitivity.chat.voice;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageVoiceBody;
import com.htmessage.update.data.SettingsManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.ChatFileManager;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.HTPathUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by huangfangyi on 2016/12/5.
 * qq 84543217
 */

public class VoicePlayClickListener implements View.OnClickListener {
    private static final String TAG = "VoicePlayClickListener";
    private HTMessage message;
    private ImageView voiceIconView;

    private AnimationDrawable voiceAnimation = null;
    private MediaPlayer mediaPlayer = null;
    private ImageView iv_read_status;
    private Activity activity;
    private ChatType chatType;
    private RecyclerView.Adapter adapter;

    public static boolean isPlaying = false;
    public static VoicePlayClickListener currentPlayListener = null;
    public static String playMsgId;
    public String chatTo;
    private ArrayList<HTMessage> messageArrayList;
    private int position = 0;
    private Handler handler=new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case 1000:
                    String localPath= (String) msg.obj;
                    playVoice(localPath);

                    break;
            }
        }
    }  ;


    public VoicePlayClickListener(HTMessage message, String chatTo, ImageView v, ImageView iv_read_status, RecyclerView.Adapter adapter, Activity context) {
        this.message = message;
        this.iv_read_status = iv_read_status;
        this.adapter = adapter;
        voiceIconView = v;
        this.activity = context;
        this.chatType = message.getChatType();
        this.chatTo = chatTo;
    }

    public VoicePlayClickListener(int position, String chatTo, ImageView v, ImageView iv_read_status, RecyclerView.Adapter adapter, Activity context, ArrayList<HTMessage> messageArrayList, ChatType type) {
        this.position = position;
        this.iv_read_status = iv_read_status;
        this.adapter = adapter;
        voiceIconView = v;
        this.activity = context;
        this.chatType = type;
        this.chatTo = chatTo;
        this.messageArrayList = messageArrayList;
    }

    public void stopPlayVoice() {
        voiceAnimation.stop();
        if (message.getDirect() == HTMessage.Direct.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.ad1);
        } else {
            voiceIconView.setImageResource(R.drawable.adj);
        }
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        CommonUtils.muteAudioFocus(activity, false);
        isPlaying = false;
        playMsgId = null;
      //  adapter.notifyDataSetChanged();
    }

    public void playVoice(String filePath) {
        if (!(new File(filePath).exists())) {
            return;
        }
        playMsgId = message.getMsgId();
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

        mediaPlayer = new MediaPlayer();
        if (SettingsManager.getInstance().getSettingMsgSpeaker()) {
            audioManager.setMode(AudioManager.STREAM_ALARM);
            audioManager.setSpeakerphoneOn(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        } else {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
       }
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopPlayVoice(); // stop animation
                }

            });
            CommonUtils.muteAudioFocus(activity, true);
            isPlaying = true;
            currentPlayListener = this;
            mediaPlayer.start();
            showAnimation();

            // 如果是接收的消息
            if (message.getDirect() == HTMessage.Direct.RECEIVE) {
                if (message.getStatus() != HTMessage.Status.SUCCESS && iv_read_status != null && iv_read_status.getVisibility() == View.VISIBLE) {
                    // 隐藏自己未播放这条语音消息的标志
                    iv_read_status.setVisibility(View.INVISIBLE);
                    HTClient.getInstance().messageManager().updateSuccess(message);
                }
            }
        } catch (Exception e) {
            System.out.println();
        }
    }

    /**
     * 下一首
     *
     * @param messageArrayList
     */
    private void nextSound(ArrayList<HTMessage> messageArrayList) {
        if (position < messageArrayList.size() - 1) {
            position = position + 1;
            playVoice();
        } else {
//            messageArrayList.clear();
            position = 0;
        }
    }

    public void playVoice() {
        message = messageArrayList.get(position);
        HTMessageVoiceBody body = (HTMessageVoiceBody) message.getBody();
        playMsgId = message.getMsgId();
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
//        if (SettingsManager.get().getSettingMsgSpeaker()) {//这个是设置里面 设置 扬声器播放就是下面的
//            audioManager.setMode(AudioManager.STREAM_ALARM);
//            audioManager.setSpeakerphoneOn(true);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
//        } else {//这个是听筒模式
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
       // }
        try {
            mediaPlayer.setDataSource(body.getRemotePath());
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    // 隐藏自己未播放这条语音消息的标志
                    if (iv_read_status != null) {
                        iv_read_status.setVisibility(View.INVISIBLE);
                    }
                    HTClient.getInstance().messageManager().updateSuccess(message);
                    stopPlayVoice(); // stop animation
                    nextSound(messageArrayList);
                }
            });
            CommonUtils.muteAudioFocus(activity, true);
            isPlaying = true;
            currentPlayListener = this;
            mediaPlayer.start();
            showAnimation();
            // 如果是接收的消息
            if (message.getDirect() == HTMessage.Direct.RECEIVE) {
                if (message.getStatus() != HTMessage.Status.SUCCESS && iv_read_status != null && iv_read_status.getVisibility() == View.VISIBLE) {
                    // 隐藏自己未播放这条语音消息的标志
                    iv_read_status.setVisibility(View.INVISIBLE);
                    HTClient.getInstance().messageManager().updateSuccess(message);
                }
            }
        } catch (Exception e) {
            System.out.println();
        }
    }

    // show the voice playing animation
    private void showAnimation() {
        // play voice, and start animation
        if (message.getDirect() == HTMessage.Direct.RECEIVE) {
            voiceIconView.setImageResource(+R.anim.voice_from_icon);
        } else {
            voiceIconView.setImageResource(+R.anim.voice_to_icon);
        }
        voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
        voiceAnimation.start();
    }

    @Override
    public void onClick(View v) {
        if (isPlaying) {
            if (playMsgId != null && playMsgId.equals(message.getMsgId())) {
                currentPlayListener.stopPlayVoice();
                return;
            }
            currentPlayListener.stopPlayVoice();
        }
        String filePath= ChatFileManager.get().getLocalPath(message.getMsgId(),message.getType());
        if(filePath==null){
            HTMessageVoiceBody htMessageVoiceBody = (HTMessageVoiceBody) message.getBody();
            String remotePath = htMessageVoiceBody.getRemotePath();
            downLoadVoiceFileFromServer(remotePath,htMessageVoiceBody.getFileName());

        }else {
            File file = new File(filePath);
            if (file.exists() && file.isFile())
                playVoice(filePath);
            else
                Log.e(TAG, "file not exist");

        }



//
////        if (message.getDirect() == HTMessage.Direct.SEND) {
////            playVoice(loaclPath);
////        } else {
//
//            if (!TextUtils.isEmpty(loaclPath)) {
//                if (message.getStatus() == HTMessage.Status.SUCCESS || message.getStatus() == HTMessage.Status.ACKED) {
//                    File file = new File(loaclPath);
//                    if (file.exists() && file.isFile())
//                        playVoice(loaclPath);
//                    else
//                        Log.e(TAG, "file not exist");
//                }
//            } else {
//                downLoadVoiceFileFromServer(loaclPath);
//            }
      //  }
    }


    private void downLoadVoiceFileFromServer(String  remotePath,String fileName ) {
        HTPathUtils pathUtils = new HTPathUtils(chatTo, activity);
        final String localPath=pathUtils.getVoicePath() + "/" + fileName;
        ApiUtis.getInstance().loadFile(remotePath, localPath, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                ChatFileManager.get().setLocalPath(message.getMsgId(),localPath,message.getType());
                Message message=handler.obtainMessage();
                message.what=1000;
                message.obj=localPath;
                message.sendToTarget();
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });


//        new OkHttpUtils(activity).loadFile(remotePath, localPath, new OkHttpUtils.DownloadCallBack() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onFailure(String message) {
//
//            }
//        });

//        HTMessageUtils.loadMessageFile(htMessage, false, chatTo, activity, new HTMessageUtils.CallBack() {
//            @Override
//            public void error() {
//
//            }
//
//            @Override
//            public void completed(String localPath) {
//                playVoice(localPath);
//                HTMessageVoiceBody htMessageVoiceBody = (HTMessageVoiceBody) htMessage.getBody();
//                htMessageVoiceBody.setLocalPath(localPath);
//                htMessage.setBody(htMessageVoiceBody);
//                HTClient.getInstance().messageManager().updateSuccess(htMessage);
//            }
//        });
    }
}
