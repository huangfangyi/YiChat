package com.htmessage.yichat.acitivity.chat.group;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;

import java.util.UUID;

/**
 * Created by huangfangyi on 2019/7/17.
 * qq 84543217
 */
public class GroupNoticePublishActivity extends BaseActivity {

    private EditText et_title;
    private EditText et_content;
    private Button btnSummit;
    private String groupId;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    CommonUtils.cencelDialog();
                    JSONArray jsonArray= (JSONArray) msg.obj;
                    if(jsonArray!=null&&jsonArray.size()>0){
                        JSONObject data=jsonArray.getJSONObject(jsonArray.size()-1);
                        String noticeId=data.getString("noticeId");
                        CommonUtils.showToastShort(GroupNoticePublishActivity.this,"发布成功");
                        final String title=data.getString("title");
                        final String content=data.getString("content");
                        sendCMDMessage(title,content,groupId,noticeId);
                    }

                    finish();

                    break;
                case 1001:
                    CommonUtils.cencelDialog();

                    int resId=msg.arg1;
                    Toast.makeText(GroupNoticePublishActivity.this,resId,Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_group_notice_pub);
        setTitle("群公告");
        groupId=this.getIntent().getStringExtra("groupId");

        et_title=this.findViewById(R.id.et_title);
        et_content=this.findViewById(R.id.et_content);
        btnSummit=this.findViewById(R.id.btn_summit);
        btnSummit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                summitGroupNotice();
            }
        });
    }

    private void summitGroupNotice(){
        final String title=et_title.getText().toString();
        final String content=et_content.getText().toString();
        if(TextUtils.isEmpty(title)||TextUtils.isEmpty(content)){
            CommonUtils.showToastShort(GroupNoticePublishActivity.this,"标题或内容不可为空");
            return;
        }
        CommonUtils.showDialogNumal(GroupNoticePublishActivity.this,"正在提交");


        JSONObject body = new JSONObject();
        body.put("title", title);
        body.put("content", content);
        body.put("groupId", groupId);
        ApiUtis.getInstance().postJSON(body, Constant.URL_GROUP_NOTICE_publish, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null){
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                     Message message = handler.obtainMessage();
                    message.obj=data;
                    message.what = 1000;
                    message.sendToTarget();
                }  else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                if(handler==null){
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });











//        List<Param> params=new ArrayList<>();
//        params.add(new Param("title",title));
//        params.add(new Param("content",content));
//        params.add(new Param("groupid",groupId));
//         params.add(new Param("userid", HTApp.getInstance().getUsername()));
//        new OkHttpUtils(GroupNoticePublishActivity.this).post(params, HTConstant.URL_GROUP_NOTICE_PUB, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                CommonUtils.cencelDialog();
//                   int code=jsonObject.getInteger("code");
//                   if(code==1){
//                       String id=jsonObject.getString("id");
//                       CommonUtils.showToastShort(GroupNoticePublishActivity.this,"发布成功");
//                       sendCMDMessage(title,content,groupId,id);
//                       finish();
//
//                   }else {
//                       CommonUtils.showToastShort(GroupNoticePublishActivity.this,"发布失败");
//
//                   }
//
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//                CommonUtils.showToastShort(GroupNoticePublishActivity.this,"发布失败");
//
//            }
//        });


    }
    
    private void sendCMDMessage(String title,String content,String groupId,String id){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("action",40002);
        JSONObject data=new JSONObject();
        data.put("title",title);
        data.put("content",content);
        data.put("groupId",groupId);
        data.put("id",id);
        jsonObject.put("data",data);
        CmdMessage customMessage = new CmdMessage();
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setFrom(HTApp.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(groupId);
        customMessage.setBody(jsonObject.toJSONString());
        customMessage.setChatType(ChatType.groupChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(long timeStamp) {

            }

            @Override
            public void onFailure() {

            }
        });
        
        
        
    }
}
