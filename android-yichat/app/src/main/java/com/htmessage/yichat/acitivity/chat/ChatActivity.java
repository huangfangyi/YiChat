package com.htmessage.yichat.acitivity.chat;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.htmessage.sdk.model.HTConversation;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.BaseFragmentActivity;
import com.htmessage.yichat.acitivity.chat.group.ChatSettingGroupActivity;
import com.htmessage.yichat.acitivity.main.MainActivity;
import com.htmessage.yichat.acitivity.main.profile.info.profile.ProfileActivity;
import com.htmessage.yichat.acitivity.main.wallet.PayPasswordActivity;
import com.htmessage.yichat.manager.NotifierManager;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.update.data.UserManager;

import java.util.List;

/**
 *
 */
public class ChatActivity extends BaseFragmentActivity {
    public static ChatActivity activityInstance;
    private ChatFragment chatFragment;
    public String toChatUsername;
     public int chatType;
    private ChatPresenter chatPresenter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat);
        activityInstance = this;
        toChatUsername = getIntent().getExtras().getString("userId");
        chatType = getIntent().getExtras().getInt("chatType", MessageUtils.CHAT_SINGLE);
         setTitle();
        chatFragment = new ChatFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, chatFragment).commit();
        chatFragment.setArguments(getIntent().getExtras());
        chatPresenter = new ChatPresenter(chatFragment);
//        showRightTextView("手动刷新", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder exceptionBuilder = new AlertDialog.Builder(ChatActivity.this);
//
//                exceptionBuilder.setTitle("是否启动手动刷新");
//                exceptionBuilder.setMessage("将触发主动从服务器拉取最新准确的聊天记录，将刷新本地的最近20条消息！");
//                exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//                         chatPresenter.refreshHistory();
//
//                    }
//                });
//                exceptionBuilder.setCancelable(false);
//                exceptionBuilder.show();
//            }
//        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityInstance = null;
    }

    private void setTitle() {

        if (chatType == MessageUtils.CHAT_SINGLE) {

//            if (!UserManager.get().getFriends().contains(toChatUsername)) {
//                Toast.makeText(activityInstance, R.string.is_not_friend, Toast.LENGTH_SHORT).show();
//                HTClient.getInstance().conversationManager().deleteConversation(toChatUsername);
//                finish();
//            } else {
                setTitle(UserManager.get().getUserNick(toChatUsername));
                showRightView(R.drawable.icon_setting_single, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ChatActivity.this, ChatSettingSingleActivity.class).putExtra("userId", toChatUsername));
                    }
                });
          //  }


        } else if (chatType == MessageUtils.CHAT_GROUP) {
            HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(toChatUsername);
            GroupInfoManager.getInstance().setAtTag(toChatUsername,false);

            if (htGroup != null) {
                setTitle(htGroup.getGroupName());
            }
            showRightView(R.drawable.icon_setting_group, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ChatActivity.this, ChatSettingGroupActivity.class).putExtra("groupId", toChatUsername));
                }
            });
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        toMainActivity();
        chatFragment.onBackPressed();
    }

    @Override
    public void back(View view) {
        onBackPressed();
        super.back(view);
    }


    private void toMainActivity() {
        if (isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }


    public boolean isSingleActivity(Context context) {
        if (context == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningTasks(1);
        return ((ActivityManager.RunningTaskInfo) list.get(0)).numRunning == 1;
    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityInstance = this;
        NotifierManager.getInstance().cancel(Integer.parseInt(toChatUsername));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (chatPresenter != null) {
            chatPresenter.onResult(requestCode, resultCode, data,this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
