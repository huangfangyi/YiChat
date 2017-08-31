package com.htmessage.fanxinht.acitivity.chat.call;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.manager.ContactsManager;
import com.htmessage.fanxinht.utils.CommonUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by huangfangyi on 2017/4/15.
 * qq 84543217
 */

public class VoiceOutgoingActivity extends MeetingActivity {
    private ImageButton btn_audio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_outgoing);
        intMediaPlayer(R.raw.video_request,true);
        initView();
        startRTC(true);
    }


    private void initView() {
        ImageView ivAvatar = (ImageView) this.findViewById(R.id.iv_avatar);//头像
        TextView tvNick = (TextView) this.findViewById(R.id.tv_nick);//昵称
        ImageView ivBackground = (ImageView) this.findViewById(R.id.iv_background);

        btn_audio = (ImageButton) this.findViewById(R.id.btn_audio);
        btn_audio.setEnabled(false);
        User user = ContactsManager.getInstance().getContactList().get(mUserId);
        if (user != null) {
            Glide.with(this).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(ivAvatar);
            tvNick.setText(user.getNick());
            Glide.with(this).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new BlurTransformation(this, 25), new CenterCrop(this))
                    .into(ivBackground);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int action = intent.getIntExtra("action", 0);
        if (action == 3003) {
            releaseHandler.removeCallbacks(runnable);
            btn_audio.setEnabled(true);
        }
        TextView tv_note= (TextView) this.findViewById(R.id.tv_note);
        tv_note.setVisibility(View.INVISIBLE);


    }

    @Override
    public void OnRtcJoinMeetOK(String strAnyrtcId) {
        super.OnRtcJoinMeetOK(strAnyrtcId);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendCallMesssage(3000, new HTChatManager.HTMessageCallBack() {
                    @Override
                    public void onProgress() {

                    }

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.showToastShort(VoiceOutgoingActivity.this,R.string.call_failed);
                                finish();
                            }
                        });
                    }
                });
            }
        });


    }

    @Override
    public void sendOverMessage() {
        super.sendOverMessage();
        if(isChating){
            sendCallMesssage(3005,null);
        }else{
            sendCallMesssage(3001,null);
        }

    }

    @Override
    public void timeOver() {
        super.timeOver();
        sendCallMesssage(3002,null);
    }
}
