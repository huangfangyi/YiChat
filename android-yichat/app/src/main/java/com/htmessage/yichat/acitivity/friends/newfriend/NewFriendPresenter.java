package com.htmessage.yichat.acitivity.friends.newfriend;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.R;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.utils.CommonUtils;

import java.util.UUID;

/**
 * Created by huangfangyi on 2019/7/29.
 * qq 84543217
 */
public class NewFriendPresenter implements NewFriendBasePresenter {


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (newFriendView == null) {
                return;
            }
            switch (msg.what){
                case 1000:
                      jsonArray= (JSONArray) msg.obj;
                    newFriendView.initRecyclerView(jsonArray);
                    break;

                case 1001:
                    CommonUtils.cencelDialog();
                    int resId=msg.arg1;
                    newFriendView.showToast(resId);
                    break;
                case 1002:
                    Bundle bundle=msg.getData();

                    String fid= bundle.getString("fid");
                    String userId=bundle.getString("userId");
                    sendAgreeCmd(userId);
                    newFriendView.changeAgreeButton(fid);
                    break;
                case 1003:
                    CommonUtils.cencelDialog();
                    int positon=msg.arg1;
                    jsonArray.remove(positon);
                    newFriendView.refreshRecyclerView( );
                    break;
                case 1004:
                    CommonUtils.cencelDialog();
                    jsonArray.clear();
                    newFriendView.refreshRecyclerView();
                    break;
            }
        }
    };

    private NewFriendView newFriendView;

    public NewFriendPresenter(NewFriendView newFriendView) {
        this.newFriendView = newFriendView;
        this.newFriendView.setPresenter(this);
    }

   private   JSONArray jsonArray=new JSONArray();
    @Override
    public void getData() {
        JSONObject data = new JSONObject();
        data.put("pageNo", 1);
        data.put("pageSize", 100);
        ApiUtis.getInstance().postJSON(data, Constant.URL_FRIEND_APPLY_LIST, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                  String code=jsonObject.getString("code");
                  if("0".equals(code)){
                      jsonArray= jsonObject.getJSONArray("data");
                      Message message=handler.obtainMessage();
                      message.what=1000;
                      message.obj=jsonArray;
                      message.sendToTarget();
                  }else{
                      Message message=handler.obtainMessage();
                      message.what=1001;
                      message.arg1= R.string.api_error_5;
                      message.sendToTarget();
                  }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();
                message.what=1001;
                message.arg1=errorCode;
                message.sendToTarget();
            }
        });
    }

    @Override
    public void deleteItem(int position) {
             JSONObject data=jsonArray.getJSONObject(position);
             String fid=data.getString("fid");
             deleteMsg(fid,position);

    }


    private void deleteMsg(String fid,int position){
        Log.d("fid---->",fid+"----"+position);
        CommonUtils.showDialogNumal(newFriendView.getBaseContext(),"正在处理");
        JSONObject body=new JSONObject();
        if(fid!=null){
            body.put("fid",fid);
        }
        ApiUtis.getInstance().postJSON(body, Constant.URL_friend_apply_delete, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    Message message=handler.obtainMessage();
                    if(fid!=null){
                        message.what=1003;
                        message.arg1=position;
                    }else {
                        message.what=1004;

                    }
                    message.sendToTarget();

                }else{
                    Message message=handler.obtainMessage();
                    message.what=1001;
                    message.arg1= R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();
                message.what=1001;
                message.arg1=errorCode;
                message.sendToTarget();
            }
        });

    }

    @Override
    public void deleteAll() {
          deleteMsg(null,0);
    }

    @Override
    public void agreeApply(final String fid, int position, final String userId) {
        JSONObject data=new JSONObject();
        data.put("userId", UserManager.get().getMyUserId());
        data.put("fid", fid);
        data.put("status", "1");
        ApiUtis.getInstance().postJSON(data, Constant.URL_FRIEND_APPLY_CHECK, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                   String code=jsonObject.getString("code");
                   if("0".equals(code)){
                       UserManager.get().addMyFriends( userId);
                       Message message=handler.obtainMessage();
                       Bundle bundle=new Bundle();
                       bundle.putString("userId",userId);
                       bundle.putString("fid",fid);
                       message.what=1002;
                       message.setData(bundle);

                       message.sendToTarget();
                   }else {
                       Message message=handler.obtainMessage();
                       message.what=1001;
                       message.arg1=R.string.agree_fail;
                       message.sendToTarget();
                   }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();
                message.what=1001;
                message.arg1=errorCode;
                message.sendToTarget();
            }
        });

    }

    private void  sendAgreeCmd(String  userId){




        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", 1001);
        JSONObject data = new JSONObject();
        data.put("userId", HTApp.getInstance().getUsername());
        data.put("nick", HTApp.getInstance().getUserNick());
        data.put("avatar", UserManager.get().getMyAvatar());
        data.put("reason", "");
        jsonObject.put("data", data);
        CmdMessage customMessage = new CmdMessage();
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setFrom(HTApp.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(userId);
        customMessage.setBody(jsonObject.toJSONString());
        customMessage.setChatType(ChatType.singleChat);
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


        HTMessage htMessage=HTMessage.createTextSendMessage(userId,"我们已经成为好友了,快来聊天吧~");
        data.put("action",50001);
        htMessage.setAttributes(data);
        HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
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

    @Override
    public void refuseApply(String userId) {

    }

    @Override
    public void start() {

    }
}
