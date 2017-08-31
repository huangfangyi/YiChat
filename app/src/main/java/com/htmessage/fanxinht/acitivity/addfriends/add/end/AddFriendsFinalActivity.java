package com.htmessage.fanxinht.acitivity.addfriends.add.end;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

import java.util.UUID;

public class AddFriendsFinalActivity extends BaseActivity {
     private   ProgressDialog progressDialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends_final);
        String userInfo = getIntent().getStringExtra(HTConstant.KEY_USER_INFO);
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(userInfo);
        } catch (JSONException e) {
        }
        if (jsonObject == null) {
            finish();
            return;
        }
        initView(jsonObject);

    }


    private void initView(final JSONObject jsonObject) {
        final EditText etReason = (EditText) this.findViewById(R.id.et_reason);
        etReason.setText(getString(R.string.i_am)+ HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_NICK));
        if (etReason.getText() instanceof Spannable) {
            Spannable spanText = (Spannable)etReason.getText();
            Selection.setSelection(spanText, etReason.getText().length());
        }
        findViewById(R.id.tv_send).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addContact(jsonObject.getString(HTConstant.JSON_KEY_HXID), etReason.getText().toString().trim());
            }

        });
    }

    /**
     * 添加contact
     *
     * @param
     */
    public void addContact(final String hxid, String reason) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.Is_sending_a_request));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        JSONObject userJson = HTApp.getInstance().getUserJson();
        JSONObject data = new JSONObject();
        data.put("ADD_REASON", reason);
        data.put("userId", userJson.getString("userId"));
        data.put("nick",userJson.getString("nick"));
        data.put("avatar", userJson.getString("avatar"));
        data.put("role",userJson.getString(HTConstant.JSON_KEY_ROLE));
        data.put("teamId",userJson.getString("teamId"));
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("action", 1000);
        bodyJson.put("data", data);

        CmdMessage customMessage = new CmdMessage();
        customMessage.setBody(bodyJson.toJSONString());
        customMessage.setFrom(HTApp.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(hxid);
         customMessage.setMsgId( UUID.randomUUID().toString());
        customMessage.setChatType(ChatType.singleChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(AddFriendsFinalActivity.this,
                                R.string.send_successful, Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(AddFriendsFinalActivity.this,
                                getResources().getString( R.string.Request_add_buddy_failure), Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    }
                });
            }
        });
    }
}
