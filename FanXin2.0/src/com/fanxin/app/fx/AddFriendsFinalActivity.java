package com.fanxin.app.fx;

import com.easemob.EMCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMMessage;
import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.activity.FXAlertDialog;
import com.fanxin.app.fx.others.LocalUserInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddFriendsFinalActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends_final);
        
        
        final String hxid =this.getIntent().getStringExtra("hxid");
        TextView  tv_send= (TextView) this.findViewById(R.id.tv_send);
        final EditText et_reason= (EditText) this.findViewById(R.id.et_reason);
        
        tv_send.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                addContact(hxid,et_reason.getText().toString().trim());
            }
            
        });
    }
    
    /**
     * 添加contact
     * 
     * @param view
     */
    @SuppressLint("ShowToast")
    public void addContact(final String glufine_id,final String myreason) {
        if (glufine_id == null || glufine_id.equals("")) {
            return;
        }

        if (MYApplication.getInstance().getUserName().equals(glufine_id)) {
            startActivity(new Intent(this, FXAlertDialog.class).putExtra("msg",
                    "不能添加自己"));
            return;
        }

        if (MYApplication.getInstance().getContactList()
                .containsKey(glufine_id)) {
            startActivity(new Intent(this, FXAlertDialog.class).putExtra("msg",
                    "此用户已是你的好友"));
            return;
        }
        
        
        
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发送请求...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        
        
        
        //支持单聊和群聊，默认单聊，如果是群聊添加下面这行
      //  cmdMsg.setChatType(ChatType.GroupChat);
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String action =Constant.CMD_ADD_FRIEND;//action可以自定义，在广播接收时可以收到
        CmdMessageBody cmdBody=new CmdMessageBody(action);
        
        String name = LocalUserInfo.getInstance(
                AddFriendsFinalActivity.this).getUserInfo("nick");
        String avatar = LocalUserInfo.getInstance(
                AddFriendsFinalActivity.this).getUserInfo("avatar");
        long time = System.currentTimeMillis();
        String myreason_temp=myreason;
        if(myreason==null||myreason.equals("")){
            myreason_temp = "请求加你为好友";
        }
        String reason = name + "66split88" + avatar + "66split88"
                + String.valueOf(time)+"66split88"+myreason_temp;

        
        
        cmdMsg.setReceipt(glufine_id);
        cmdMsg.setAttribute("reason", reason);//支持自定义扩展
        cmdMsg.addBody(cmdBody); 
        EMChatManager.getInstance().sendMessage(cmdMsg, new EMCallBack(){

          @Override
          public void onError(int arg0, final String arg1) {
              runOnUiThread(new Runnable() {
                  public void run() {
                      progressDialog.dismiss();
                      Toast.makeText(getApplicationContext(),
                              "请求添加好友失败:" + arg1, Toast.LENGTH_SHORT ).show();
                  }
              });              
          }

          @Override
          public void onProgress(int arg0, String arg1) {
               
          }

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
            
        });
     
          
        
//        
//        
//        new Thread(new Runnable() {
//            public void run() {
//
//                try {
//                    // 在reason封装请求者的昵称/头像/时间等信息，在通知中显示
//
//                    String name = LocalUserInfo.getInstance(
//                            AddFriendsFinalActivity.this).getUserInfo("nick");
//                    String avatar = LocalUserInfo.getInstance(
//                            AddFriendsFinalActivity.this).getUserInfo("avatar");
//                    long time = System.currentTimeMillis();
//                    String myreason_temp=myreason;
//                    if(myreason==null||myreason.equals("")){
//                        myreason_temp = "请求加你为好友";
//                    }
//                    String reason = name + "66split88" + avatar + "66split88"
//                            + String.valueOf(time)+"66split88"+myreason_temp;
//                    EMContactManager.getInstance().addContact(glufine_id,
//                            reason);
//                    runOnUiThread(new Runnable() {
//                        @SuppressLint("ShowToast")
//                        public void run() {
//                            progressDialog.dismiss();
//                            Toast.makeText(getApplicationContext(),
//                                    "发送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();
//                            
//                            finish();
//                        }
//                    });
//
//                } catch (final Exception e) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            progressDialog.dismiss();
//                            Toast.makeText(getApplicationContext(),
//                                    "请求添加好友失败:" + e.getMessage(), Toast.LENGTH_SHORT ).show();
//                        }
//                    });
//                }
//            }
//        }).start();
    }
    
    public void back(View view ){
        
        finish();
    }
}
