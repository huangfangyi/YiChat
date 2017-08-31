package com.htmessage.fanxinht.acitivity.chat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.acitivity.chat.group.ChatSettingGroupActivity;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.manager.ContactsManager;
import com.htmessage.fanxinht.acitivity.main.MainActivity;
import com.htmessage.fanxinht.manager.NotifierManager;

import java.util.List;

/**
 *
 */
public class ChatActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    private ChatFragment chatFragment;
    public String toChatUsername;
    public int chatType;
    private ChatPresenter chatPresenter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        activityInstance = this;
        toChatUsername = getIntent().getExtras().getString("userId");
        chatType = getIntent().getExtras().getInt("chatType", MessageUtils.CHAT_SINGLE);
        if (chatType == MessageUtils.CHAT_SINGLE) {
            User user = ContactsManager.getInstance().getContactList().get(toChatUsername);
            setTitle(user.getNick());
            showRightView(R.drawable.icon_setting_single, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ChatActivity.this, ChatSettingSingleActivity.class).putExtra("userId", toChatUsername));
                }
            });

        } else if (chatType == MessageUtils.CHAT_GROUP) {
            HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(toChatUsername);
            setTitle(htGroup.getGroupName());
            showRightView(R.drawable.icon_setting_group, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ChatActivity.this, ChatSettingGroupActivity.class).putExtra("groupId", toChatUsername));
                }
            });
        }

         chatFragment= (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(chatFragment==null){
            chatFragment = new ChatFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, chatFragment).commit();
        }
        chatFragment.setArguments(getIntent().getExtras());
        chatPresenter =new ChatPresenter(chatFragment,toChatUsername,chatType);
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityInstance = null;
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
        chatFragment.onBackPressed();
        toMainActivity();
    }

    @Override
    public void back(View view) {
        toMainActivity();
        super.back(view);

    }

    private void toMainActivity(){
        if (isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }



    public boolean isSingleActivity(Context context) {
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
}
