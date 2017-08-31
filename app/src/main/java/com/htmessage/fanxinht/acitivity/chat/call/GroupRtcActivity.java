package com.htmessage.fanxinht.acitivity.chat.call;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.prevideocall.PreVideoCallActivity;
import com.htmessage.fanxinht.utils.CommonUtils;

/**
 * Created by huangfangyi on 2017/4/16.
 * qq 84543217
 */

public class GroupRtcActivity extends MeetingActivity {
    boolean isOutgoing = true;
    private TextView tv_check_people, tv_group_name;
    private ImageButton btn_swtich_camera;
    private RelativeLayout rl_rtc_videos;
    private LinearLayout llayout_tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_group);
        tv_check_people = (TextView) findViewById(R.id.tv_check_people);
        btn_swtich_camera = (ImageButton) findViewById(R.id.btn_swtich_camera);
        rl_rtc_videos = (RelativeLayout) findViewById(R.id.rl_rtc_videos);
        llayout_tools = (LinearLayout) findViewById(R.id.llayout_tools);
        HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(groupId);
        tv_group_name = (TextView) findViewById(R.id.tv_group_name);
        tv_group_name.setText(htGroup.getGroupName() + "会议");
        isGroup = true;
        isOutgoing = this.getIntent().getBooleanExtra("isOutgoing", true);
        if (isOutgoing) {
            intMediaPlayer(R.raw.video_request, true);
        } else {
            HTApp.isCalling = true;
        }

        // initView();
        startRTC(false);

        sendCallMesssage(5000, new HTChatManager.HTMessageCallBack() {
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
                        CommonUtils.showToastShort(GroupRtcActivity.this, R.string.call_failed);
                        finish();
                    }
                });
            }
        });
        rl_rtc_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGroup) {
                    if (tv_group_name.getVisibility() == View.GONE && llayout_tools.getVisibility() == View.GONE && tv_check_people.getVisibility() == View.GONE) {
                        tv_group_name.setVisibility(View.VISIBLE);
                        llayout_tools.setVisibility(View.VISIBLE);
                        tv_check_people.setVisibility(View.VISIBLE);
                    } else {
                        tv_group_name.setVisibility(View.GONE);
                        llayout_tools.setVisibility(View.GONE);
                        tv_check_people.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    public void OnRtcJoinMeetOK(String strAnyrtcId) {
        super.OnRtcJoinMeetOK(strAnyrtcId);


    }
    @Override
    public void OnRTCOpenVideoRender(final String strLivePeerID) {
        super.OnRTCOpenVideoRender(strLivePeerID);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mVideoView != null && mVideoView.GetVideoRenderSize() > 1) {
                    if (soundPool != null) {
                        soundPool.release();

                    }
                    releaseHandler.removeCallbacks(runnable);
                    HTApp.isCalling = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isGroup) {
                                tv_group_name.setVisibility(View.VISIBLE);
                                tv_check_people.setVisibility(View.VISIBLE);
                                btn_swtich_camera.setVisibility(View.VISIBLE);
                            } else {
                                tv_group_name.setVisibility(View.GONE);
                                tv_check_people.setVisibility(View.GONE);
                                btn_swtich_camera.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void sendOverMessage() {
        super.sendOverMessage();
        if (!isChating && isGroup && isOutgoing) {
            sendCallMesssage(5002, null);
        }

    }

    @Override
    public void startCheckAgain() {
        super.startCheckAgain();
        Intent intent = new Intent(this, PreVideoCallActivity.class);
        intent.putExtra("isAgain", true);
        intent.putExtra("groupId", groupId);
        startActivityForResult(intent, 0X26);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0X26:
                    String callId1 = data.getStringExtra("callId");
                    callId_add = callId1;
                    sendCallMesssage(5000, null);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
