package com.fanxin.huangfangyi.main.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.ui.BaseActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;

public class AddFriendsFinalActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fx_activity_addfriends_final);
        String userInfo=getIntent().getStringExtra(FXConstant.KEY_USER_INFO);
        JSONObject jsonObject=null;
        try {
            jsonObject=JSONObject.parseObject(userInfo);
        }catch(JSONException e){
        }
        if(jsonObject==null){
            finish();
            return;
        }
        initView(jsonObject);

    }

    private void initView(final JSONObject jsonObject){
        final EditText etReason= (EditText) this.findViewById(R.id.et_reason);
        findViewById(R.id.tv_send).setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                addContact(jsonObject.getString(FXConstant.JSON_KEY_HXID),etReason.getText().toString().trim());
            }

        });
    }
    
    /**
     * 添加contact
     * 
     * @param
     */
     public void addContact( String  hxid, String reason) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发送请求...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        //支持单聊和群聊，默认单聊，
         cmdMsg.setChatType(EMMessage.ChatType.Chat);
          //action可以自定义
         EMCmdMessageBody cmdBody = new EMCmdMessageBody(FXConstant.CMD_ADD_FRIEND);
         cmdMsg.setReceipt(hxid);
         cmdMsg.addBody(cmdBody);
         //传递申请的理由
         JSONObject jsonObject=DemoApplication.getInstance().getUserJson();
         jsonObject.put(FXConstant.CMD_ADD_REASON,reason);
         //传递申请者的资料+申请理由
         cmdMsg.setAttribute(FXConstant.KEY_USER_INFO, jsonObject.toJSONString());
         cmdMsg.setMessageStatusCallback(new EMCallBack() {
             @Override
             public void onSuccess() {
                 runOnUiThread(new Runnable() {
                     @SuppressLint("ShowToast")
                     public void run() {
                         progressDialog.dismiss();
                         Toast.makeText(getApplicationContext(),
                                 "发送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();

                         finish();
                     }
                 });
             }
             @Override
             public void onError(int i, final String s) {
                 runOnUiThread(new Runnable() {
                     public void run() {
                         progressDialog.dismiss();
                         Toast.makeText(getApplicationContext(),
                                 "请求添加好友失败:" + s, Toast.LENGTH_SHORT).show();
                     }
                 });
             }

             @Override
             public void onProgress(int i, String s) {

             }
         });
         EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }
          
 }
