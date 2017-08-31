package com.htmessage.fanxinht.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageBody;
import com.htmessage.sdk.model.HTMessageFileBody;
import com.htmessage.sdk.model.HTMessageImageBody;
import com.htmessage.sdk.model.HTMessageLocationBody;
import com.htmessage.sdk.model.HTMessageTextBody;
import com.htmessage.sdk.model.HTMessageVideoBody;
import com.htmessage.sdk.model.HTMessageVoiceBody;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.chat.forward.ForwardSingleActivity;

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
    public static HTMessage creatWithDrowMsg(HTMessage htMessage) {
        String text = null;
        JSONObject jsonObject = htMessage.getAttributes();
        String userId = jsonObject.getString(HTConstant.JSON_KEY_HXID);
        String nick = jsonObject.getString(HTConstant.JSON_KEY_NICK);
        if (HTApp.getInstance().getUsername().equals(userId)) {
            text = HTApp.getContext().getString(R.string.revoke_content);
        } else {
            text = String.format(HTApp.getContext().getString(R.string.revoke_content_someone), nick);
        }
        jsonObject.put("action", 6001);
        HTMessage message1 = HTMessage.createTextSendMessage(htMessage.getUsername(), text);
        message1.setMsgId(htMessage.getMsgId());
        message1.setChatType(htMessage.getChatType());
        message1.setDirect(htMessage.getDirect());
        message1.setAttributes(jsonObject.toJSONString());
        message1.setTime(htMessage.getTime());
        message1.setLocalTime(htMessage.getLocalTime());
        message1.setFrom(htMessage.getFrom());
        message1.setTo(htMessage.getTo());
        message1.setStatus(htMessage.getStatus());
        message1.setExt(htMessage.getExt());
        HTClient.getInstance().messageManager().updateMessageInDB(message1);
        return message1;
    }


    /**
     * 获取copy的信息
     *
     * @param context
     * @param message
     */
    public static void getCopyMsg(Activity context, HTMessage message, String toChatUserName) {
        String copyType = "";
        String fileName = "";
        String localUrl = "";
        String remotePath = "";
        HTMessage.Type type = message.getType();
        if (type == HTMessage.Type.IMAGE) {
            copyType = "image";
            HTMessageImageBody body = (HTMessageImageBody) message.getBody();
            fileName = body.getFileName();
            remotePath = body.getRemotePath();
            localUrl = body.getLocalPath();
        } else if (type == HTMessage.Type.VOICE) {
            HTMessageVoiceBody body = (HTMessageVoiceBody) message.getBody();
            copyType = "voice";
            remotePath = body.getRemotePath();
            fileName = body.getFileName();
            localUrl = body.getLocalPath();
        } else if (type == HTMessage.Type.TEXT) {
            copyType = "text";
            localUrl = ((HTMessageTextBody) message.getBody()).getContent();
        } else if (type == HTMessage.Type.FILE) {
            copyType = "file";
            HTMessageFileBody body = (HTMessageFileBody) message.getBody();
            remotePath = body.getRemotePath();
            fileName = body.getFileName();
            localUrl = body.getLocalPath();
        } else if (type == HTMessage.Type.VIDEO) {
            HTMessageVideoBody body = (HTMessageVideoBody) message.getBody();
            copyType = "video";
            remotePath = body.getRemotePath();
            fileName = body.getFileName();
            localUrl = body.getLocalPath();
        } else if (type == HTMessage.Type.LOCATION) {
            HTMessageLocationBody body = (HTMessageLocationBody) message.getBody();
            copyType = "location";
            localUrl = body.getLocalPath();
            remotePath = body.getRemotePath();
            fileName = body.getFileName();
        }
        String msgId = message.getMsgId();
        if (!TextUtils.isEmpty(localUrl)) {
            switch (copyType) {
                case "text":
                    showCopySendDialog(context, copyType, localUrl, message, null);
                    break;
                default:
                    getFilePath(context, copyType, message, msgId, fileName, remotePath, toChatUserName, null);
                    break;
            }
        } else {
            switch (copyType) {
                case "text":
                    showCopySendDialog(context, copyType, localUrl, message, null);
                    break;
                default:
                    getFilePath(context, copyType, message, msgId, fileName, remotePath, toChatUserName, null);
                    break;
            }
        }
    }

    /**
     * 下载copy文件并复制
     *
     * @param context
     * @param copyType
     * @param message
     * @param msgId
     * @param fileName
     * @param remotePath
     */
    public static void getFilePath(final Activity context, final String copyType, final HTMessage message, String msgId, final String fileName, final String remotePath, String toChatUserName) {
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        String filePath = null;
        if (!TextUtils.isEmpty(msgId) && !TextUtils.isEmpty(fileType) && !TextUtils.isEmpty(remotePath)) {
//            final File file = new File(HTApp.getInstance().getDirFilePath() + fileName);
            HTPathUtils pathUtils = new HTPathUtils(toChatUserName, context);
            if (message.getType() == HTMessage.Type.VOICE) {
                filePath = pathUtils.getVoicePath().getAbsolutePath() + "/" + fileName;
            } else if (message.getType() == HTMessage.Type.IMAGE) {
                filePath = pathUtils.getImagePath().getAbsolutePath() + "/" + fileName;
            } else if (message.getType() == HTMessage.Type.FILE) {
                filePath = pathUtils.getFilePath().getAbsolutePath() + "/" + fileName;
            } else if (message.getType() == HTMessage.Type.VIDEO) {
                filePath = pathUtils.getVideoPath().getAbsolutePath() + "/" + fileName;
            }
            File file = new File(filePath);
            if (file.exists()) {
                switch (copyType) {
                    case "image":
                        showCopySendDialog(context, copyType, file.getAbsolutePath(), message, file.getAbsolutePath());
                        break;
                    default:
                        showCopySendDialog(context, copyType, file.getAbsolutePath(), message, null);
                        break;
                }
                return;
            }
            loadMessageFile(message, false,toChatUserName, context, new CallBack() {
                @Override
                public void error() {
                    CommonUtils.showToastShort(context, R.string.copy_failed);
                }

                @Override
                public void completed(String localPath) {
                    File file1 = new File(localPath);
                    switch (copyType) {
                        case "image":
                            showCopySendDialog(context, copyType, file1.getAbsolutePath(), message, file1.getAbsolutePath());
                            break;
                        default:
                            showCopySendDialog(context, copyType, file1.getAbsolutePath(), message, null);
                            break;
                    }
                    CommonUtils.showToastShort(context, R.string.copy_success);
                }
            });
//
//            final ProgressDialog dialog = new ProgressDialog(context);
//            dialog.setMessage(context.getString(R.string.copying));
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            final String path =  HTApp.getInstance().getDirFilePath() + fileName;
//            dialog.show();
//            new OkHttpUtils(context).loadFile(remotePath, path, new OkHttpUtils.DownloadCallBack() {
//                @Override
//                public void onSuccess() {
//                    if (dialog != null && dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                    File file1 = new File(path);
//                    switch (copyType) {
//                        case "image":
//                            showCopySendDialog(context, copyType, file1.getAbsolutePath(), message, file1.getAbsolutePath());
//                            break;
//                        case "file":
//                        case "voice":
//                        case "video":
//                        case "location":
//                            showCopySendDialog(context, copyType, file1.getAbsolutePath(), message, null);
//                            break;
//                    }
//                    ((Activity) context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(context, R.string.copy_success, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                 }
//
//                @Override
//                public void onFailure(String message) {
//                    if (dialog != null && dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                    ((Activity) context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(context, R.string.copy_failed, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
        }
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
            ACache.get(context).remove("myCopy");
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("copyType", copyType);
            jsonObject.put("localPath", localPath);
            jsonObject.put("msgId", message1.getMsgId());
            jsonObject.put("imagePath", imagePath);
            ACache.get(context).put("myCopy", jsonObject.toJSONString());
        }
    }

    /**
     * 获取copy的信息
     *
     * @param context
     * @param message
     */
    public static void getForWordMessage(Activity context, HTMessage message, final String toChatUsername, final JSONObject userJson) {
        String copyType = "";
        String fileName = "";
        String localUrl = "";
        String remotePath = "";
        HTMessage.Type type = message.getType();
        if (type == HTMessage.Type.IMAGE) {
            copyType = "image";
            HTMessageImageBody body = (HTMessageImageBody) message.getBody();
            fileName = body.getFileName();
            remotePath = body.getRemotePath();
            localUrl = body.getLocalPath();
        } else if (type == HTMessage.Type.VOICE) {
            HTMessageVoiceBody body = (HTMessageVoiceBody) message.getBody();
            copyType = "voice";
            remotePath = body.getRemotePath();
            fileName = body.getFileName();
            localUrl = body.getLocalPath();
        } else if (type == HTMessage.Type.TEXT) {
            copyType = "text";
            localUrl = ((HTMessageTextBody) message.getBody()).getContent();
        } else if (type == HTMessage.Type.FILE) {
            copyType = "file";
            HTMessageFileBody body = (HTMessageFileBody) message.getBody();
            remotePath = body.getRemotePath();
            fileName = body.getFileName();
            localUrl = body.getLocalPath();
        } else if (type == HTMessage.Type.VIDEO) {
            HTMessageVideoBody body = (HTMessageVideoBody) message.getBody();
            copyType = "video";
            remotePath = body.getRemotePath();
            fileName = body.getFileName();
            localUrl = body.getLocalPath();
        } else if (type == HTMessage.Type.LOCATION) {
            HTMessageLocationBody body = (HTMessageLocationBody) message.getBody();
            copyType = "location";
            localUrl = body.getLocalPath();
            remotePath = body.getRemotePath();
            fileName = body.getFileName();
        }
        String msgId = message.getMsgId();
        if (!TextUtils.isEmpty(localUrl)) {
            switch (copyType) {
                case "text":
                    showForwordDialog(context, copyType, localUrl, message, null, toChatUsername, userJson);
                    break;
                default:
                    getFilePath(context, copyType, message, msgId, fileName, remotePath, toChatUsername, userJson);
                    break;
            }
        } else {
            switch (copyType) {
                case "text":
                    showForwordDialog(context, copyType, localUrl, message, null, toChatUsername, userJson);
                    break;
                default:
                    getFilePath(context, copyType, message, msgId, fileName, remotePath, toChatUsername, userJson);
                    break;
            }
        }
    }

    /**
     * 下载copy文件并复制
     *
     * @param context
     * @param copyType
     * @param message
     * @param msgId
     * @param fileName
     * @param remotePath
     */
    public static void getFilePath(final Activity context, final String copyType, final HTMessage message, String msgId, final String fileName, final String remotePath, final String toChatUsername, final JSONObject userJson) {
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        String filePath = null;
        if (!TextUtils.isEmpty(msgId) && !TextUtils.isEmpty(fileType) && !TextUtils.isEmpty(remotePath)) {
//            final File file = new File(HTApp.getInstance().getDirFilePath() + fileName);
            HTPathUtils pathUtils = new HTPathUtils(toChatUsername, context);
            if (message.getType() == HTMessage.Type.VOICE) {
                filePath = pathUtils.getVoicePath().getAbsolutePath() + "/" + fileName;
            } else if (message.getType() == HTMessage.Type.IMAGE) {
                filePath = pathUtils.getImagePath().getAbsolutePath() + "/" + fileName;
            } else if (message.getType() == HTMessage.Type.FILE) {
                filePath = pathUtils.getFilePath().getAbsolutePath() + "/" + fileName;
            } else if (message.getType() == HTMessage.Type.VIDEO) {
                filePath = pathUtils.getVideoPath().getAbsolutePath() + "/" + fileName;
            }
            File file = new File(filePath);
            if (file.exists()) {
                switch (copyType) {
                    case "image":
                        if (userJson != null) {
                            showForwordDialog(context, copyType, file.getAbsolutePath(), message, file.getAbsolutePath(), toChatUsername, userJson);
                        } else {
                            showCopySendDialog(context, copyType, file.getAbsolutePath(), message, file.getAbsolutePath());
                        }
                        break;
                    default:
                        if (userJson != null) {
                            showForwordDialog(context, copyType, file.getAbsolutePath(), message, null, toChatUsername, userJson);
                        } else {
                            showCopySendDialog(context, copyType, file.getAbsolutePath(), message, null);
                        }
                        break;
                }
                return;
            }

            loadMessageFile(message,false, toChatUsername, context, new CallBack() {
                @Override
                public void error() {
                    if (userJson == null) {
                        CommonUtils.showToastShort(context, R.string.copy_failed);
                    }
                }

                @Override
                public void completed(String localPath) {
                    HTMessageBody body = message.getBody();
                    body.bodyJson.put("localPath", localPath);
                    message.setBody(body);
                    HTClient.getInstance().messageManager().saveMessage(message, false);
                    File file1 = new File(localPath);
                    switch (copyType) {
                        case "image":
                            if (userJson != null) {
                                showForwordDialog(context, copyType, file1.getAbsolutePath(), message, file1.getAbsolutePath(), toChatUsername, userJson);
                            } else {
                                showCopySendDialog(context, copyType, file1.getAbsolutePath(), message, file1.getAbsolutePath());
                            }
                            break;
                        default:
                            if (userJson != null) {
                                showForwordDialog(context, copyType, file1.getAbsolutePath(), message, null, toChatUsername, userJson);
                            } else {
                                showCopySendDialog(context, copyType, file1.getAbsolutePath(), message, null);
                            }
                            break;
                    }
                    if (userJson == null) {
                        CommonUtils.showToastShort(context, R.string.copy_success);
                    }
                }
            });
//
//            final ProgressDialog dialog = new ProgressDialog(context);
//            dialog.setMessage(context.getString(R.string.forword_get));
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            final String path = HTApp.getInstance().getDirFilePath() + fileName;
//            dialog.show();
//            new OkHttpUtils(context).loadFile(remotePath, path, new OkHttpUtils.DownloadCallBack() {
//                @Override
//                public void onSuccess() {
//                    if (dialog != null && dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                    File file1 = new File(path);
//                    switch (copyType) {
//                        case "image":
//                            showForwordDialog(context, copyType, file1.getAbsolutePath(), message, file1.getAbsolutePath(),toChatUsername,userJson);
//                            break;
//                        case "file":
//                        case "voice":
//                        case "video":
//                        case "location":
//                            showForwordDialog(context, copyType, file1.getAbsolutePath(), message, null,toChatUsername,userJson);
//                            break;
//                    }
//                 }
//
//                @Override
//                public void onFailure(String message) {
//                    if (dialog != null && dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }
//            });
        }
    }


    /**
     * 转发
     *
     * @param forwordType
     * @param localPath
     * @param message1
     * @param imagePath
     * @param toChatUsername
     * @param extJSON
     */
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

    /**
     * 下载文件
     *
     * @param htMessage
     * @param chatTo
     * @param context
     * @param callBack
     */
    public static void loadMessageFile(HTMessage htMessage,boolean isVideoThumb, String chatTo, final Activity context, final CallBack callBack) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(context.getString(R.string.loading));
        if(!isVideoThumb){
            progressDialog.show();
        }

        HTPathUtils pathUtils = new HTPathUtils(chatTo, context);
        String remotePath = "";
        String fileName = "";
        String filePath = null;
        if (htMessage.getType() == HTMessage.Type.VOICE) {
            HTMessageVoiceBody htMessageVoiceBody = (HTMessageVoiceBody) htMessage.getBody();
            remotePath = htMessageVoiceBody.getRemotePath();
            fileName = htMessageVoiceBody.getFileName();
            filePath = pathUtils.getVoicePath().getAbsolutePath() + "/" + fileName;
        } else if (htMessage.getType() == HTMessage.Type.IMAGE) {
            HTMessageImageBody htMessageImageBody = (HTMessageImageBody) htMessage.getBody();
            remotePath = htMessageImageBody.getRemotePath();
            fileName = htMessageImageBody.getFileName();
            filePath = pathUtils.getImagePath().getAbsolutePath() + "/" + fileName;
        } else if (htMessage.getType() == HTMessage.Type.FILE) {
            //TODO 文件消息待处理
            HTMessageFileBody htMessageFileBody = (HTMessageFileBody) htMessage.getBody();
            remotePath = htMessageFileBody.getRemotePath();
            fileName = htMessageFileBody.getFileName();
            filePath = pathUtils.getFilePath().getAbsolutePath() + "/" + fileName;
        } else if (htMessage.getType() == HTMessage.Type.VIDEO) {
            HTMessageVideoBody htMessageVideoBody = (HTMessageVideoBody) htMessage.getBody();

            if(isVideoThumb){
                remotePath = htMessageVideoBody.getThumbnailRemotePath();
                fileName =remotePath.substring(remotePath.lastIndexOf("/")+1);
                filePath = pathUtils.getVideoPath().getAbsolutePath() + "/" + fileName;
            }else {
                remotePath = htMessageVideoBody.getRemotePath();
                fileName = htMessageVideoBody.getFileName();
                filePath = pathUtils.getVideoPath().getAbsolutePath() + "/" + fileName;
            }

        }
        final String finalFilePath = filePath;
        new OkHttpUtils(context).loadFile(remotePath, filePath, new OkHttpUtils.DownloadCallBack() {
            @Override
            public void onSuccess() {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        callBack.completed(finalFilePath);
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.error();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }


    public interface CallBack {
        void error();

        void completed(String localPath);
    }

    /**
     * 更新消息,收到已读回执
     *
     * @param htMessage
     */
    public static HTMessage updateHTMessage(HTMessage htMessage) {
        htMessage.setStatus(HTMessage.Status.READ);
        HTClient.getInstance().messageManager().updateMessageInDB(htMessage);
        return htMessage;
    }

    /**
     * 更新消息已发送已读回执
     *
     * @param htMessage
     */
    public static HTMessage updateHTMessageCmdAsk(HTMessage htMessage) {
        htMessage.setStatus(HTMessage.Status.ACKED);
        HTClient.getInstance().messageManager().updateMessageInDB(htMessage);
        return htMessage;
    }

}
