package com.htmessage.fanxinht.acitivity.chat.call;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import org.anyrtc.meet_kit.RTMeetKit;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by huangfangyi on 2017/4/15.
 * qq 84543217
 */

public class VoiceIncomingActivity extends MeetingActivity {
    private ImageButton btn_audio;
    private ImageButton btn_speaker;
    private Button btn_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_coming);
        intMediaPlayer(R.raw.video_incoming, false);
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int action = intent.getIntExtra("action", 0);

    }

    private void initView() {

        ImageView ivAvatar = (ImageView) this.findViewById(R.id.iv_avatar);//头像
        TextView tvNick = (TextView) this.findViewById(R.id.tv_nick);//昵称
        ImageView ivBackground = (ImageView) this.findViewById(R.id.iv_background);
        User user = ContactsManager.getInstance().getContactList().get(mUserId);
        if (user != null) {
            Glide.with(this).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(ivAvatar);
            tvNick.setText(user.getNick());
            Glide.with(this).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new BlurTransformation(this, 25), new CenterCrop(this))
                    .into(ivBackground);
        } else {
            Glide.with(this).load(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(ivAvatar);
            tvNick.setText("");
            Glide.with(this).load(R.color.black).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new BlurTransformation(this, 25), new CenterCrop(this))
                    .into(ivBackground);
        }
        btn_audio = (ImageButton) this.findViewById(R.id.btn_audio);
        btn_speaker = (ImageButton) this.findViewById(R.id.btn_speaker);
        btn_answer = (Button) this.findViewById(R.id.btn_answer);
        btn_audio.setVisibility(View.GONE);
        btn_answer.setVisibility(View.VISIBLE);
        btn_speaker.setVisibility(View.INVISIBLE);
        btn_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChating = true;
                if (isGroup) {
                    Intent intent = getIntent();
                    intent.setClass(VoiceIncomingActivity.this, GroupRtcActivity.class);
                    intent.putExtra("mode", RTMeetKit.RTCVideoLayout.RTC_V_3X3_auto.ordinal());
                    intent.putExtra("isOutgoing", false);
                    startActivity(intent);
                    finish();
                } else {
                    startRTC(true);
                    btn_answer.setVisibility(View.GONE);
                    btn_audio.setVisibility(View.VISIBLE);
                    btn_speaker.setVisibility(View.VISIBLE);
                    sendCallMesssage(3003, new HTChatManager.HTMessageCallBack() {
                        @Override
                        public void onProgress() {

                        }

                        @Override
                        public void onSuccess() {
                            soundPool.release();
//                            releaseHandler.removeCallbacks(runnable);
                        }

                        @Override
                        public void onFailure() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.showToastShort(VoiceIncomingActivity.this, R.string.hung_in_failed);
                                    finish();
                                }
                            });
                        }
                    });
                }

            }
        });


    }

    @Override
    public void sendOverMessage() {
        super.sendOverMessage();
        if (!isGroup) {
            if (isChating) {
                sendCallMesssage(3005, null);
            } else {
                sendCallMesssage(3002, null);
            }
        }


    }

    @Override
    public void OnRtcJoinMeetOK(String strAnyrtcId) {
        super.OnRtcJoinMeetOK(strAnyrtcId);
        if (!isGroup) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv_note = (TextView) findViewById(R.id.tv_note);
                    tv_note.setVisibility(View.INVISIBLE);

                }
            });

        }


    }

    @Override
    public void timeOver() {
        super.timeOver();
        if (!isGroup) {
            sendCallMesssage(3002, null);
        }

    }
}
