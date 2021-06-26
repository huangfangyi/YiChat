package com.htmessage.yichat.acitivity.main.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.SwitchButton;
import com.htmessage.update.data.SettingsManager;


public class NewMsgNoticeSettingActivity extends BaseActivity implements View.OnClickListener {
    private SwitchButton notifiSwitch;
    private SwitchButton soundSwitch;
    private SwitchButton vibrateSwitch;
    private SwitchButton  voiceSwitch;

    private TextView tv_title;
     /**
     * new message notification
     */
    private RelativeLayout rl_switch_notification;
    /**
     * sound
     */
    private RelativeLayout rl_switch_sound;
    /**
     * vibration
     */
    private RelativeLayout rl_switch_vibrate;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_new_msg_notice_setting);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        rl_switch_notification.setOnClickListener(this);
        rl_switch_sound.setOnClickListener(this);
        rl_switch_vibrate.setOnClickListener(this);
        findViewById(R.id.rl_switch_voice).setOnClickListener(this);
    }
    private SettingsManager settingsModel;
    private void initData() {
        tv_title.setText(R.string.notify_set);
        settingsModel = SettingsManager.getInstance();
        // the vibrate and sound notification are allowed or not?
        if (settingsModel.getSettingMsgNotification()) {
            notifiSwitch.openSwitch();
            rl_switch_sound.setVisibility(View.VISIBLE);
            rl_switch_vibrate.setVisibility(View.VISIBLE);
        } else {
            notifiSwitch.closeSwitch();
            rl_switch_sound.setVisibility(View.GONE);
            rl_switch_vibrate.setVisibility(View.GONE);
        }

        // sound notification is switched on or not?
        if (settingsModel.getSettingMsgSound()) {
            soundSwitch.openSwitch();
        } else {
            soundSwitch.closeSwitch();
        }

        // vibrate notification is switched on or not?
        if (settingsModel.getSettingMsgVibrate()) {
            vibrateSwitch.openSwitch();
        } else {
            vibrateSwitch.closeSwitch();
        }

        // vibrate notification is switched on or not?
        if (settingsModel.getSettingMsgSpeaker()) {
            voiceSwitch.openSwitch();
        } else {
            voiceSwitch.closeSwitch();
        }

    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
        rl_switch_sound = (RelativeLayout) findViewById(R.id.rl_switch_sound);
        rl_switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
        notifiSwitch = (SwitchButton) findViewById(R.id.switch_notification);
        soundSwitch = (SwitchButton) findViewById(R.id.switch_sound);
        vibrateSwitch = (SwitchButton) findViewById(R.id.switch_vibrate);
        voiceSwitch=findViewById(R.id.switch_voice );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_switch_notification:
                if (notifiSwitch.isSwitchOpen()) {
                    notifiSwitch.closeSwitch();
                    rl_switch_sound.setVisibility(View.GONE);
                    rl_switch_vibrate.setVisibility(View.GONE);
                    settingsModel.setSettingMsgNotification(false);
                } else {
                    notifiSwitch.openSwitch();
                    rl_switch_sound.setVisibility(View.VISIBLE);
                    rl_switch_vibrate.setVisibility(View.VISIBLE);
                    settingsModel.setSettingMsgNotification(true);
                }
                break;
            case R.id.rl_switch_sound:
                if (soundSwitch.isSwitchOpen()) {
                    soundSwitch.closeSwitch();
                    settingsModel.setSettingMsgSound(false);
                } else {
                    soundSwitch.openSwitch();
                    settingsModel.setSettingMsgSound(true);
                }
                break;
            case R.id.rl_switch_vibrate:
                if (vibrateSwitch.isSwitchOpen()) {
                    vibrateSwitch.closeSwitch();
                    settingsModel.setSettingMsgVibrate(false);
                } else {
                    vibrateSwitch.openSwitch();
                    settingsModel.setSettingMsgVibrate(true);
                }
            case R.id.rl_switch_voice:
                if (voiceSwitch.isSwitchOpen()) {
                    voiceSwitch.closeSwitch();
                    settingsModel.setSettingMsgSpeaker(false);
                } else {
                    voiceSwitch.openSwitch();
                    settingsModel.setSettingMsgSpeaker(true);
                }

                break;
        }
    }
}
