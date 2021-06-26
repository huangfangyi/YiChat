package com.htmessage.yichat.acitivity.chat.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htmessage.sdk.model.HTMessage;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.data.UserManager;

/**
 * Created by huangfangyi on 2017/3/5.
 * qq 84543217
 */

public class UpdateGroupActivity extends BaseActivity {

    public static final int TYPE_GROUP_NAME = 1;
    public static final int TYPE_GROUP_DESC = 2;

    private String groupId;
    private HTGroup htGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
        int type = getIntent().getIntExtra("type", 1);
        groupId = getIntent().getStringExtra("groupId");
        RelativeLayout title = (RelativeLayout) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        TextView titleTV = (TextView) findViewById(R.id.tv_title);
        TextView saveTV = (TextView) findViewById(R.id.tv_save);
        EditText infoET = (EditText) findViewById(R.id.et_info);
        htGroup = HTClient.getInstance().groupManager().getGroup(groupId);
        if (TextUtils.isEmpty(groupId) || htGroup == null) {
            finish();
            return;
        }
        initView(type, titleTV, saveTV, infoET);
    }

    private void initView(final int type, TextView titleTV, TextView saveTV, final EditText infoET) {
        String title = "";
        String key = "";
        switch (type) {
            case TYPE_GROUP_NAME:
                title = getString(R.string.change_groupName);
                infoET.setHint(htGroup.getGroupName());
                break;
            case TYPE_GROUP_DESC:
                title = getString(R.string.change_group_desc);
                infoET.setHint(htGroup.getGroupDesc());

                break;
        }
        titleTV.setText(title);
        if (GroupInfoManager.getInstance().isManager(groupId)) {
            infoET.setEnabled(true);
            saveTV.setVisibility(View.VISIBLE);
        } else {
            saveTV.setVisibility(View.GONE);
            infoET.setEnabled(false);
        }
        saveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GroupInfoManager.getInstance().isManager(groupId)) {
                         CommonUtils.showToastShort(getBaseContext(), R.string.change_group_info_just_owner);
                        return;

                }
                updateInServer(type, infoET.getText().toString().trim());

            }
        });

    }

    private void updateInServer(int type, final String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        if (type == TYPE_GROUP_NAME) {
            if (!value.equals(htGroup.getGroupName())) {
                if (value.length() > 18) {
                    CommonUtils.showToastShort(getApplicationContext(), R.string.group_groupname_dot_18);
                    return;
                }
                CommonUtils.showDialogNumal(this, getString(R.string.are_uploading));
                HTClient.getInstance().groupManager().updateGroupName(htGroup.getGroupId(), value, UserManager.get().getMyNick(), new GroupManager.CallBack() {
                    @Override
                    public void onSuccess(String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(getApplicationContext(), R.string.Modify_the_group_name_successful);
                                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(IMAction.ACTION_UPDATE_CHAT_TITLE).putExtra(HTConstant.JSON_KEY_USERID, htGroup.getGroupId()).putExtra(HTConstant.JSON_KEY_NICK, value));
                                setResult(RESULT_OK, new Intent().putExtra("value", value));
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(getApplicationContext(), R.string.change_groupName_failed);
                            }
                        });
                    }

                    @Override
                    public void onHTMessageSend(HTMessage htMessage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.upLoadMessage(htMessage);
                                LocalBroadcastManager.getInstance(HTApp.getContext()).sendBroadcast(new Intent(IMAction.ACTION_NEW_MESSAGE).putExtra("message",htMessage));

                            }
                        });

                    }
                });
            }
            return;
        }
        if (type == TYPE_GROUP_DESC) {
            if (value.length() > 100) {
                CommonUtils.showToastShort(getApplicationContext(), R.string.group_groupdesc_dot_100);
                return;
            }
            if (!value.equals(htGroup.getGroupDesc())) {
                CommonUtils.showDialogNumal(this, getString(R.string.are_uploading));
                HTClient.getInstance().groupManager().updateGroupDesc(htGroup.getGroupId(), value, UserManager.get().getMyNick(), new GroupManager.CallBack() {
                    @Override
                    public void onSuccess(String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(getApplicationContext(), R.string.update_groups_success);
                                setResult(RESULT_OK, new Intent().putExtra("value", value));
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(getApplicationContext(), R.string.update_groups_failed);
                            }
                        });
                    }

                    @Override
                    public void onHTMessageSend(HTMessage htMessage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.upLoadMessage(htMessage);
                                LocalBroadcastManager.getInstance(HTApp.getContext()).sendBroadcast(new Intent(IMAction.ACTION_NEW_MESSAGE).putExtra("message",htMessage));

                            }
                        });

                    }
                });
            }
            return;
        }
    }
}
