package com.htmessage.yichat.acitivity.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.chat.group.GroupAddMembersActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.SwitchButton;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.update.data.SettingsManager;
import com.htmessage.update.data.UserManager;


public class ChatSettingSingleActivity extends BaseActivity implements
        OnClickListener {
    // 、置顶
    private RelativeLayout rl_switch_chattotop;
     private RelativeLayout re_clear;

   private SwitchButton switch_block_msg;

    private String userId;
    public static ChatSettingSingleActivity instance;
    private ImageView ivAvatar;
    private TextView tvNick;
    private ImageView ivAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_setting_single);
        instance = this;
        // 获取传过来的userId
        userId = getIntent().getStringExtra("userId");
        if (TextUtils.isEmpty(userId)) {
            finish();
            return;
        }
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        rl_switch_chattotop.setOnClickListener(this);
          re_clear.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        switch_block_msg.setOnClickListener(this);
    }

    private void initView() {

        ivAdd = (ImageView) this.findViewById(R.id.iv_avatar2);
        ivAvatar = (ImageView) this.findViewById(R.id.iv_avatar);
        tvNick = (TextView) this.findViewById(R.id.tv_username);
        rl_switch_chattotop = (RelativeLayout) findViewById(R.id.rl_switch_chattotop);
         re_clear = (RelativeLayout) findViewById(R.id.re_clear);
        switch_block_msg=findViewById(R.id.switch_block_msg);

           // 初始化置顶和免打扰的状态
           if( SettingsManager.getInstance().getNotifyGroupOrUser(userId)){
               switch_block_msg.closeSwitch();
           }else {
               switch_block_msg.openSwitch();
           }

    }

    private void initData() {

            tvNick.setText(UserManager.get().getUserNick(userId));

            UserManager.get().loadUserAvatar(this, UserManager.get().getUserAvatar(userId), ivAvatar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_block_msg: // 设置免打扰
                if(switch_block_msg.isSwitchOpen()){
                    SettingsManager.getInstance().setNotifyGroupOrUser(userId,true);
                    switch_block_msg.closeSwitch();
                }else {
                    switch_block_msg.openSwitch();
                    SettingsManager.getInstance().setNotifyGroupOrUser(userId,false);

                }

                break;
            case R.id.re_clear: // 清空聊天记录
                CommonUtils.showDialogNumal(ChatSettingSingleActivity.this, getString(R.string.clear));
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        HTClient.getInstance().messageManager().deleteUserMessage(userId,false);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_EMPTY).putExtra("id", userId));
                        CommonUtils.cencelDialog();

                    }

                }, 2000);

                break;
            case R.id.iv_avatar: // 头像

                break;
            case R.id.iv_avatar2: // 添加
                startActivity(new Intent(ChatSettingSingleActivity.this,
                        GroupAddMembersActivity.class).putExtra(HTConstant.JSON_KEY_USERID, userId));
                break;
        }

    }



}
