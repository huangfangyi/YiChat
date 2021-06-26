package com.htmessage.yichat.acitivity.friends.addfriend;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.update.data.UserManager;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.DialogUtils;
import com.htmessage.yichat.acitivity.main.MainActivity;

import java.util.UUID;


public class AddFriendsFinalActivity extends BaseActivity {
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    String userId = (String) msg.obj;
                    sendCmdNotice(userId);
                    break;
                case 1001:
                    dialog.dismiss();
                    int resId = msg.arg1;
                    showToast(resId);
                    finish();
                    break;
                case 1002:
                    //发送透传消息成功
                    dialog.dismiss();
                    showToast(R.string.apply_success);
                    startActivity(new Intent(AddFriendsFinalActivity.this, MainActivity.class));

                    finish();

                    break;
            }
        }
    };

    private void showToast(int resId) {
        Toast.makeText(AddFriendsFinalActivity.this, resId, Toast.LENGTH_LONG).show();
    }

    private Dialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends_final);
        String userId = getIntent().getStringExtra("userId");

        initView(userId);

    }


    private void initView(final String userId) {
        dialog = DialogUtils.creatDialog(AddFriendsFinalActivity.this, R.string.handing);
        final EditText etReason = (EditText) this.findViewById(R.id.et_reason);
        etReason.setText(getString(R.string.i_am) + UserManager.get().getMyNick());
        if (etReason.getText() instanceof Spannable) {
            Spannable spanText = (Spannable) etReason.getText();
            Selection.setSelection(spanText, etReason.getText().length());
        }
        findViewById(R.id.tv_send).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addContact(userId, etReason.getText().toString().trim());
            }

        });
    }

    /**
     * 添加contact
     *
     * @param
     */
    public void addContact(final String userId, String reason) {
        dialog.show();
        JSONObject data = new JSONObject();
        data.put("friendId", userId);
        data.put("reason", reason);
        ApiUtis.getInstance().postJSON(data, Constant.URL_FRIEND_APPLY, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if (handler == null) {
                    return;
                }
                Message message = handler.obtainMessage();

                if ("0".equals(code)) {

                    message.what = 1000;
                    message.obj = userId;

                } else if ("142".equals(code)) {
                    message.what = 1001;
                    message.arg1 = R.string.has_applied;

                } else if ("143".equals(code)) {

                    message.what = 1001;
                    message.arg1 = R.string.cannot_add_myself;

                } else {
                    message.what = 1001;
                    message.arg1 = R.string.apply_fail;

                }
                message.sendToTarget();
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });


//        CommonUtils.showDialogNumal(this, getString(R.string.Is_sending_a_request));

    }

    private void sendCmdNotice(String userId) {
        Log.d("userId",userId);
        JSONObject data = new JSONObject();
        data.put("ADD_REASON", "");
        data.put("userId", UserManager.get().getMyUserId());
        data.put("nick", UserManager.get().getMyNick());
        data.put("avatar", UserManager.get().getMyAvatar());
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("action", 1000);
        bodyJson.put("data", data);
        CmdMessage customMessage = new CmdMessage();
        customMessage.setBody(bodyJson.toJSONString());
        customMessage.setFrom(HTApp.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(userId);
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setChatType(ChatType.singleChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(long timeStamp) {
                if (handler == null) {
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1002;
                message.sendToTarget();
            }

            @Override
            public void onFailure() {
                if (handler == null) {
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = R.string.apply_fail_2;
                message.sendToTarget();
            }
        });
    }

    @Override
    protected void onDestroy() {

        handler = null;
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onDestroy();

    }
}
