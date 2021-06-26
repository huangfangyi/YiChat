package com.htmessage.yichat.utils;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.MmvkManger;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.forward.ForwardSingleActivity;

import java.io.File;

/**
 * Created by huangfangyi on 2017/7/8.
 * qq 84543217
 */

public class HTMessageUtils {
    /**
     * 创建撤回消息
     *
     * @param htMessage
     * @return
     */
    public static void makeToWithDrowMsg(HTMessage htMessage, String opId,String opNick) {
         JSONObject jsonObject=htMessage.getAttributes();
         if(jsonObject==null){
             jsonObject=new JSONObject();
         }
         jsonObject.put("action",6001);
         jsonObject.put("opId",opId);
        jsonObject.put("opNick",opNick);
         htMessage.setAttributes(jsonObject);
        HTClient.getInstance().messageManager().saveMessage(htMessage,false);

    }







    /**
     * 复制并转发
     *
     * @param copyType
     * @param localPath
     * @param message1
     */
    public static void showCopySendDialog(Context context, final String copyType, String localPath, final HTMessage message1, String imagePath) {
        if (message1.getType() == HTMessage.Type.TEXT) {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText(localPath);
            MmvkManger.getIntance().remove("myCopy");
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("copyType", copyType);
            jsonObject.put("localPath", localPath);
            jsonObject.put("msgId", message1.getMsgId());
            jsonObject.put("imagePath", imagePath);
            MmvkManger.getIntance().putString("myCopy", jsonObject.toJSONString());
        }
    }





    private static void showForwordDialog(Context context, final String forwordType, final String localPath, final HTMessage message1, String imagePath, String toChatUsername, JSONObject extJSON) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("forwordType", forwordType);
        jsonObject.put("localPath", localPath);
        jsonObject.put("msgId", message1.getMsgId());
        jsonObject.put("imagePath", imagePath);
        jsonObject.put("toChatUsername", toChatUsername);
        jsonObject.put("exobj", extJSON.toJSONString());
        Intent intent = new Intent(context, ForwardSingleActivity.class);
        intent.putExtra("obj", jsonObject.toJSONString());
        context.startActivity(intent);
    }





    public interface CallBack {
        void error();

        void completed(String localPath);
    }





    /**
     * 更新红包消息
     *
     * @param htMessage
     * @return
     */
    public static void updateRpMessage(HTMessage htMessage, Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(IMAction.RP_IS_HAS_OPEND).putExtra("message", htMessage));
    }

    /**
     * 下载朋友圈短视频
     *
     * @param context
     * @param videoPath
     * @param callBack
     */
    public static void loadVideoFromService(final Activity context, String videoPath, final CallBack callBack) {
        String videoName = videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.length());
        String dirFilePath = HTApp.getInstance().getVideoPath();
        final File file = new File(dirFilePath + "/" + videoName);
        if (file.exists()) {
            String absolutePath = file.getAbsolutePath();
            callBack.completed(absolutePath);
        } else {
            CommonUtils.showDialogNumal(context, context.getString(R.string.loading));
            new OkHttpUtils(context).loadFile(videoPath, file.getAbsolutePath(), new OkHttpUtils.DownloadCallBack() {
                @Override
                public void onSuccess() {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.cencelDialog();
                            callBack.completed(file.getAbsolutePath());
                        }
                    });
                }

                @Override
                public void onFailure(String message) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.error();
                            CommonUtils.cencelDialog();
                        }
                    });
                }
            });
        }
    }


}
