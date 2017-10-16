package com.htmessage.fanxinht.acitivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.acitivity.keeplive.KeepAliveActivity;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.manager.SettingsManager;
import com.htmessage.fanxinht.acitivity.login.LoginActivity;
import com.htmessage.fanxinht.acitivity.main.password.PasswordResetActivity;
import com.htmessage.fanxinht.utils.SwitchButton;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.fanxinht.widget.HTAlertDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by huangfangyi on 2016/7/4.\
 * QQ:84543217
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener {
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
    /**
     * speaker
     */
    private RelativeLayout rl_switch_speaker;


    /**
     * line between sound and vibration
     */
    private TextView textview1, textview2;

    private LinearLayout blacklistContainer;

    private LinearLayout userProfileContainer;

    /**
     * logout
     */
    private RelativeLayout rl_logout;

    private RelativeLayout rl_switch_chatroom_leave;

    private RelativeLayout rl_switch_delete_msg_when_exit_group;
    private RelativeLayout rl_switch_auto_accept_group_invitation;
    private RelativeLayout rl_switch_adaptive_video_encode, rl_keep_live, re_resetpassword;

    private RelativeLayout rl_update, rl_about_us;//检查更新

    /**
     * Diagnose
     */
    private LinearLayout llDiagnose, llChange, ll_numal_set;
    private View view_devider;
    /**
     * display name for APNs
     */
    private LinearLayout pushNick, ll_new_msg;

    private SwitchButton notifiSwitch;
    private SwitchButton soundSwitch;
    private SwitchButton vibrateSwitch;
    private SwitchButton speakerSwitch;
    private SwitchButton ownerLeaveSwitch;
    private SwitchButton switch_delete_msg_when_exit_group;
    private SwitchButton switch_auto_accept_group_invitation;
    private SwitchButton switch_adaptive_video_encode;
    private SettingsManager settingsModel;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fragment_conversation_settings);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        blacklistContainer.setOnClickListener(this);
        userProfileContainer.setOnClickListener(this);
        rl_switch_notification.setOnClickListener(this);
        rl_switch_sound.setOnClickListener(this);
        rl_switch_vibrate.setOnClickListener(this);
        rl_switch_speaker.setOnClickListener(this);
        rl_logout.setOnClickListener(this);
        llDiagnose.setOnClickListener(this);
        pushNick.setOnClickListener(this);
        rl_switch_chatroom_leave.setOnClickListener(this);
        rl_switch_delete_msg_when_exit_group.setOnClickListener(this);
        rl_switch_auto_accept_group_invitation.setOnClickListener(this);
        rl_switch_adaptive_video_encode.setOnClickListener(this);
        rl_update.setOnClickListener(this);
        rl_keep_live.setOnClickListener(this);
        re_resetpassword.setOnClickListener(this);
        rl_about_us.setOnClickListener(this);
        ll_new_msg.setOnClickListener(this);
        llChange.setOnClickListener(this);
    }

    private void initData() {
        settingsModel = SettingsManager.getInstance();

        // the vibrate and sound notification are allowed or not?
        if (settingsModel.getSettingMsgNotification()) {
            notifiSwitch.openSwitch();
            rl_switch_sound.setVisibility(View.VISIBLE);
            rl_switch_vibrate.setVisibility(View.VISIBLE);
            textview1.setVisibility(View.VISIBLE);
            textview2.setVisibility(View.VISIBLE);
        } else {
            notifiSwitch.closeSwitch();
            rl_switch_sound.setVisibility(View.GONE);
            rl_switch_vibrate.setVisibility(View.GONE);
            textview1.setVisibility(View.GONE);
            textview2.setVisibility(View.GONE);
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

        // the speaker is switched on or not?
        if (settingsModel.getSettingMsgSpeaker()) {
            speakerSwitch.openSwitch();
        } else {
            speakerSwitch.closeSwitch();
        }

        // if allow owner leave
        if (settingsModel.isChatroomOwnerLeaveAllowed()) {
            ownerLeaveSwitch.openSwitch();
        } else {
            ownerLeaveSwitch.closeSwitch();
        }

        // delete messages when exit group?
        if (settingsModel.isDeleteMessagesAsExitGroup()) {
            switch_delete_msg_when_exit_group.openSwitch();
        } else {
            switch_delete_msg_when_exit_group.closeSwitch();
        }

        if (settingsModel.isAutoAcceptGroupInvitation()) {
            switch_auto_accept_group_invitation.openSwitch();
        } else {
            switch_auto_accept_group_invitation.closeSwitch();
        }

    }

    private void initView() {
        rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
        rl_switch_sound = (RelativeLayout) findViewById(R.id.rl_switch_sound);
        rl_switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
        rl_switch_speaker = (RelativeLayout) findViewById(R.id.rl_switch_speaker);
        rl_switch_chatroom_leave = (RelativeLayout) findViewById(R.id.rl_switch_chatroom_owner_leave);
        rl_switch_delete_msg_when_exit_group = (RelativeLayout) findViewById(R.id.rl_switch_delete_msg_when_exit_group);
        rl_switch_auto_accept_group_invitation = (RelativeLayout) findViewById(R.id.rl_switch_auto_accept_group_invitation);
        rl_switch_adaptive_video_encode = (RelativeLayout) findViewById(R.id.rl_switch_adaptive_video_encode);
        rl_update = (RelativeLayout) findViewById(R.id.rl_update);//rl_keep_live,re_resetpassword
        rl_keep_live = (RelativeLayout) findViewById(R.id.rl_keep_live);
        re_resetpassword = (RelativeLayout) findViewById(R.id.re_resetpassword);
        rl_about_us = (RelativeLayout) findViewById(R.id.rl_about_us);

        notifiSwitch = (SwitchButton) findViewById(R.id.switch_notification);
        soundSwitch = (SwitchButton) findViewById(R.id.switch_sound);
        vibrateSwitch = (SwitchButton) findViewById(R.id.switch_vibrate);
        speakerSwitch = (SwitchButton) findViewById(R.id.switch_speaker);
        ownerLeaveSwitch = (SwitchButton) findViewById(R.id.switch_owner_leave);
        switch_delete_msg_when_exit_group = (SwitchButton) findViewById(R.id.switch_delete_msg_when_exit_group);
        switch_auto_accept_group_invitation = (SwitchButton) findViewById(R.id.switch_auto_accept_group_invitation);
        switch_adaptive_video_encode = (SwitchButton) findViewById(R.id.switch_adaptive_video_encode);
        llChange = (LinearLayout) findViewById(R.id.ll_change);
        ll_new_msg = (LinearLayout) findViewById(R.id.ll_new_msg);
        ll_numal_set = (LinearLayout) findViewById(R.id.ll_numal_set);
        rl_logout = (RelativeLayout) findViewById(R.id.rl_logout);

        textview1 = (TextView) findViewById(R.id.textview1);
        textview2 = (TextView) findViewById(R.id.textview2);

        blacklistContainer = (LinearLayout) findViewById(R.id.ll_black_list);
        userProfileContainer = (LinearLayout) findViewById(R.id.ll_user_profile);
        llDiagnose = (LinearLayout) findViewById(R.id.ll_diagnose);
        pushNick = (LinearLayout) findViewById(R.id.ll_set_push_nick);
        view_devider = findViewById(R.id.view_devider);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_change: //新消息设置
                if (ll_new_msg.getVisibility() == View.GONE) {
                    ll_new_msg.setVisibility(View.VISIBLE);
                    view_devider.setVisibility(View.GONE);
                } else {
                    ll_new_msg.setVisibility(View.GONE);
                    view_devider.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rl_switch_notification:
                if (notifiSwitch.isSwitchOpen()) {
                    notifiSwitch.closeSwitch();
                    rl_switch_sound.setVisibility(View.GONE);
                    rl_switch_vibrate.setVisibility(View.GONE);
                    textview1.setVisibility(View.GONE);
                    textview2.setVisibility(View.GONE);
                    settingsModel.setSettingMsgNotification(false);
                } else {
                    notifiSwitch.openSwitch();
                    rl_switch_sound.setVisibility(View.VISIBLE);
                    rl_switch_vibrate.setVisibility(View.VISIBLE);
                    textview1.setVisibility(View.VISIBLE);
                    textview2.setVisibility(View.VISIBLE);
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
                break;
            case R.id.rl_switch_speaker:
                if (speakerSwitch.isSwitchOpen()) {
                    speakerSwitch.closeSwitch();
                    settingsModel.setSettingMsgSpeaker(false);
                } else {
                    speakerSwitch.openSwitch();
                    settingsModel.setSettingMsgSpeaker(true);
                }
                break;
            case R.id.rl_switch_chatroom_owner_leave:
                if (ownerLeaveSwitch.isSwitchOpen()) {
                    ownerLeaveSwitch.closeSwitch();
                    settingsModel.allowChatroomOwnerLeave(false);

                } else {
                    ownerLeaveSwitch.openSwitch();
                    settingsModel.allowChatroomOwnerLeave(true);
                }
                break;
            case R.id.rl_switch_delete_msg_when_exit_group:
                if (switch_delete_msg_when_exit_group.isSwitchOpen()) {
                    switch_delete_msg_when_exit_group.closeSwitch();
                    settingsModel.setDeleteMessagesAsExitGroup(false);
                } else {
                    switch_delete_msg_when_exit_group.openSwitch();
                    settingsModel.setDeleteMessagesAsExitGroup(true);
                }
                break;
            case R.id.rl_switch_auto_accept_group_invitation:
                if (switch_auto_accept_group_invitation.isSwitchOpen()) {
                    switch_auto_accept_group_invitation.closeSwitch();
                    settingsModel.setAutoAcceptGroupInvitation(false);
                } else {
                    switch_auto_accept_group_invitation.openSwitch();
                    settingsModel.setAutoAcceptGroupInvitation(true);
                }
                break;
            case R.id.rl_switch_adaptive_video_encode:
                if (switch_adaptive_video_encode.isSwitchOpen()) {
                    switch_adaptive_video_encode.closeSwitch();
                    settingsModel.setAdaptiveVideoEncode(false);
                } else {
                    switch_adaptive_video_encode.openSwitch();
                    settingsModel.setAdaptiveVideoEncode(true);
                }
                break;
            case R.id.rl_logout:
//                logout();
                logOutDialog();
                break;
            case R.id.ll_black_list:
                // startActivity(new Intent(SettingsActivity.this, BlacklistActivity.class));
                break;
            case R.id.ll_diagnose:
                //    startActivity(new Intent(SettingsActivity.this, DiagnoseActivity.class));
                break;
            case R.id.ll_set_push_nick:
                //   startActivity(new Intent(SettingsActivity.this, OfflinePushNickActivity.class));
                break;
            case R.id.ll_user_profile:
//                startActivity(new Intent(SettingsActivity.this, UserProfileActivity.class).putExtra("setting", true)
//                        .putExtra("username", EMClient.getInstance().getCurrentUser()));
                break;
            case R.id.rl_update: //检查更新
                getAppUpdate();
                break;
            //rl_keep_live,re_resetpassword
            case R.id.re_resetpassword: //重置密码
                startActivity(new Intent(SettingsActivity.this, PasswordResetActivity.class).putExtra("isReset", true));
                break;
            case R.id.rl_keep_live: //后台保活
               startActivity(new Intent(SettingsActivity.this,KeepAliveActivity.class));
                break;
            case R.id.rl_about_us:
                break;
            default:
                break;
        }

    }

    private void logOutDialog() {
        HTAlertDialog dialog = new HTAlertDialog(this, null, new String[]{getString(R.string.exit_this_user), getString(R.string.close_app)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        logout();
                        break;
                    case 1:
                        HTApp.getInstance().finishActivities();
                        //杀死该应用进程
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                        break;
                }
            }
        });
    }


    void logout() {
        final ProgressDialog pd = new ProgressDialog(SettingsActivity.this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        HTClient.getInstance().logout(new HTClient.HTCallBack() {
            @Override
            public void onSuccess() {
                pd.dismiss();
                HTApp.getInstance().setUserJson(null);
                HTApp.getInstance().finishActivities();
                  startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                finish();

            }

            @Override
            public void onError() {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), R.string.logout_failed, Toast.LENGTH_SHORT).show();
            }
        });


//        HTClientManager.getInstance().logout(new HTClientManager.LogoutCallBack() {
//            @Override
//            public void onSuccess() {
//
//                HTApp.getInstance().finishActivities();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        pd.dismiss();
//                        startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
//                        finish();
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//
//            }
//        });
//
//
    }

    public void back(View view) {
        finish();
    }

    /**
     * 获取VersionCode
     *
     * @return 当前应用的VersionCode
     */
    public String getVersionCode() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String version = String.valueOf(info.versionCode);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getAppUpdate() {
        final ProgressDialog dialog = new ProgressDialog(SettingsActivity.this);
        dialog.setMessage(getString(R.string.are_checking_update));
        dialog.show();
        final String version = getVersionCode();
        List<Param> params = new ArrayList<>();
        params.add(new Param("system", "0"));
        params.add(new Param("vid", version));
        new OkHttpUtils(SettingsActivity.this).post(params, HTConstant.URL_CHECK_UPDATE, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    String serviceVersion = jsonObject.getString("newVersion");
                    String url = jsonObject.getString("url");
                    String info = jsonObject.getString("info");
                    String statue = jsonObject.getString("statue");
                    if (!version.equals(serviceVersion) && (Integer.valueOf(version) < Integer.valueOf(serviceVersion))) {
                        showUpdateDialog(SettingsActivity.this, getString(R.string.has_update), info, url);
                    } else {
                        Toast.makeText(SettingsActivity.this, R.string.just_new_version, Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(String errorMsg) {
                dialog.dismiss();
            }
        });
    }

    private void showUpdateDialog(final Context context, String title, String message, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.layout_alert_dialog_delete, null);
        TextView tv_delete_people = (TextView) dialogView.findViewById(R.id.tv_delete_people);
        TextView tv_delete_title = (TextView) dialogView.findViewById(R.id.tv_delete_title);
        TextView tv_cancle = (TextView) dialogView.findViewById(R.id.tv_cancle);
        TextView tv_ok = (TextView) dialogView.findViewById(R.id.tv_ok);
        tv_delete_title.setText(title);
        tv_delete_people.setText(message);
        tv_cancle.setText(R.string.update_later);
        tv_ok.setText(R.string.update_now);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
//        dialog.setCancelable(false);//点击屏幕外不取消  返回键也没用
//        dialog.setCanceledOnTouchOutside(false); //点击屏幕外取消,返回键有用
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url);
                intent.setData(uri);
                SettingsActivity.this.startActivity(intent);
            }
        });
    }
}
