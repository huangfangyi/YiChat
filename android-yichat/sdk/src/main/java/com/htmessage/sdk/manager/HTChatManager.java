package com.htmessage.sdk.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.htmessage.sdk.SDKConstant;
import com.htmessage.sdk.client.HTAction;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.CallMessage;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.CurrentUser;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageFileBody;
import com.htmessage.sdk.model.HTMessageImageBody;
import com.htmessage.sdk.model.HTMessageLocationBody;
import com.htmessage.sdk.model.HTMessageVideoBody;
import com.htmessage.sdk.model.HTMessageVoiceBody;
import com.htmessage.sdk.service.MessageService;
import com.htmessage.sdk.utils.UploadFileUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by huangfangyi on 2017/2/13.
 * qq 84543217
 */

public class HTChatManager {

    private Context context;
    private MyReceiver receiver;
    private Map<String, HTMessageCallBack> htMessageCallBackMap = new HashMap<>();
    private String baseOssUrl="";

    public HTChatManager(Context context) {

        this.context = context;
        if(SDKConstant.IS_LIMITLESS){
            baseOssUrl= HTPreferenceManager.getInstance().getOssBaseUrl();

        }else {
            baseOssUrl= SDKConstant.baseOssUrl;
        }
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HTAction.ACTION_RESULT_MESSAGE);
        intentFilter.addAction(HTAction.ACTION_RESULT_MESSAGE_CMD);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);


    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getAction().equals(HTAction.ACTION_RESULT_MESSAGE) || intent.getAction().equals(HTAction.ACTION_RESULT_MESSAGE_CMD)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String msgId = intent.getStringExtra("data");
                        boolean result = intent.getBooleanExtra("result", true);
                        long timeStamp=intent.getLongExtra("time",0);

                        if (htMessageCallBackMap.containsKey(msgId)) {
                            if (result) {
                                HTMessage htMessage=  HTClient.getInstance().messageManager().getMssage(msgId);
                                if(htMessage!=null){
                                    htMessage.setStatus(HTMessage.Status.SUCCESS);
                                    htMessage.setTime(timeStamp);
                                    HTClient.getInstance().messageManager().saveMessage(htMessage,false);
                                }

                                htMessageCallBackMap.get(msgId).onSuccess(timeStamp);
                            } else {
                                htMessageCallBackMap.get(msgId).onFailure();
                            }


                        }
                    }
                }).start();


            }
        }
    }

//    public static HTChatManager getInstance() {
//        if (htChatManager == null) {
//            throw new RuntimeException("please init first");
//        }
//        return htChatManager;
//    }
//
//    public static void init(Context context) {
//        if (htChatManager == null) {
//            htChatManager = new HTChatManager(context);
//        }
//    }

    public void sendMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                htMessageCallBack.onProgress();
                htMessageCallBackMap.put(htMessage.getMsgId(), htMessageCallBack);
                Log.d("sendAMessage:","---->SDK  sendMessage---->"+htMessage.getMsgId());

//                if(htMessage.getChatType()== ChatType.groupChat&&!HTClient.getInstance().groupManager().getAllGroups().contains(htMessage.getTo())){
//                    htMessageCallBack.onFailure();
//                }
                if (htMessage.getType() == HTMessage.Type.TEXT) {
                    sendXmppMessage(htMessage);

                } else if (htMessage.getType() == HTMessage.Type.IMAGE) {
                    HTMessageImageBody htMessageImageBody= (HTMessageImageBody) htMessage.getBody();
                    sendImageMessage(htMessage, htMessageCallBack, htMessageImageBody.getLocalPath());

                } else if (htMessage.getType() == HTMessage.Type.VOICE) {
                    sendVoiceMessage(htMessage, htMessageCallBack);
                } else if (htMessage.getType() == HTMessage.Type.VIDEO) {
                    sendVideoMessage(htMessage, htMessageCallBack);
                } else if (htMessage.getType() == HTMessage.Type.LOCATION) {
                    sendLocationMessage(htMessage, htMessageCallBack);
                } else if (htMessage.getType() == HTMessage.Type.FILE) {
                    sendFileMessage(htMessage, htMessageCallBack);

                }

            }
        }).start();


    }

    private void sendFileMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack) {
        final HTMessageFileBody htMessageFileBody = (HTMessageFileBody) htMessage.getBody();
        new UploadFileUtils(context, htMessageFileBody.getFileName(), htMessageFileBody.getLocalPath()).asyncUploadFile(new UploadFileUtils.UploadCallBack() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {

                htMessageFileBody.setRemotePath(baseOssUrl + htMessageFileBody.getFileName());
                htMessage.setBody(htMessageFileBody);
                // htMessage.setStatus(HTMessage.Status.SUCCESS);
                sendXmppMessage(htMessage);

            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                htMessageCallBack.onFailure();
            }
        });
    }
//
//    private void sendImageMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack) {
//        //先上传图片至服务器
//        final HTMessageImageBody htMessageImageBody = (HTMessageImageBody) htMessage.getBody();
//
//
//        Luban.get(context)
//                .load(new File(htMessageImageBody.getLocalPath()))                     //传人要压缩的图片
//                .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
//                .setCompressListener(new OnCompressListener() { //设置回调
//
//                    @Override
//                    public void onStart() {
//                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
//                    }
//
//                    @Override
//                    public void onSuccess(File file) {
//                        // TODO 压缩成功后调用，返回压缩后的图片文件
//                        sendImageMessage(htMessage, htMessageCallBack, file.getAbsolutePath());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        // TODO 当压缩过去出现问题时调用
//                        sendImageMessage(htMessage, htMessageCallBack, htMessageImageBody.getLocalPath());
//                    }
//                }).launch();    //启动压缩
//
//
//    }


    private void sendImageMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack, String filePath) {
        final HTMessageImageBody htMessageImageBody = (HTMessageImageBody) htMessage.getBody();
        final String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        final String fileName=HTPreferenceManager.getInstance().getUser().getUsername()+ System.currentTimeMillis()+suffix;
        new UploadFileUtils(context, fileName, filePath).asyncUploadFile(new UploadFileUtils.UploadCallBack() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {

                htMessageImageBody.setRemotePath(baseOssUrl +fileName);
                htMessageImageBody.setFileName(fileName);
                htMessage.setBody(htMessageImageBody);
                // htMessage.setStatus(HTMessage.Status.SUCCESS);
                sendXmppMessage(htMessage);

            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                htMessageCallBack.onFailure();
            }
        });

     }

    private void sendVoiceMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack) {
        final HTMessageVoiceBody htMessageVoiceBody = (HTMessageVoiceBody) htMessage.getBody();

        new UploadFileUtils(context, htMessageVoiceBody.getFileName(), htMessageVoiceBody.getLocalPath()).asyncUploadFile(new UploadFileUtils.UploadCallBack() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                htMessageVoiceBody.setRemotePath(baseOssUrl + htMessageVoiceBody.getFileName());
                htMessage.setBody(htMessageVoiceBody);

                sendXmppMessage(htMessage);

            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                htMessageCallBack.onFailure();

            }
        });
    }

    private void sendVideoMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack) {

        final HTMessageVideoBody htMessageVideoBody = (HTMessageVideoBody) htMessage.getBody();
        String thumbPath = htMessageVideoBody.getLocalPathThumbnail();
        final String filePath = htMessageVideoBody.getLocalPath();
        final String fileNameThumbnail = thumbPath.substring(thumbPath.lastIndexOf("/") + 1);
        new UploadFileUtils(context, fileNameThumbnail, thumbPath).asyncUploadFile(new UploadFileUtils.UploadCallBack() {

            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {


                new UploadFileUtils(context, htMessageVideoBody.getFileName(), filePath).asyncUploadFile(new UploadFileUtils.UploadCallBack() {

                    @Override
                    public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

                    }

                    @Override
                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                        htMessageVideoBody.setRemotePath(baseOssUrl + htMessageVideoBody.getFileName());
                        htMessageVideoBody.setThumbnailRemotePath(baseOssUrl + fileNameThumbnail);
                        htMessage.setBody(htMessageVideoBody);
                        htMessage.setStatus(HTMessage.Status.SUCCESS);

                        sendXmppMessage(htMessage);
                    }

                    @Override
                    public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {

                        htMessage.setStatus(HTMessage.Status.FAIL);
                        HTClient.getInstance().messageManager().saveMessage(htMessage, false);
//                                sendMessageCallBack.onFailure(htMessage);
                        htMessageCallBack.onFailure();


                    }
                });


            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {

                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                htMessageCallBack.onFailure();
            }

        });

    }

    private void sendLocationMessage(final HTMessage htMessage, final HTMessageCallBack htMessageCallBack) {
        final HTMessageLocationBody htMessageLocationBody = (HTMessageLocationBody) htMessage.getBody();

        new UploadFileUtils(context, htMessageLocationBody.getFileName(), htMessageLocationBody.getLocalPath()).asyncUploadFile(new UploadFileUtils.UploadCallBack() {

            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {

                //  body.put(MessageUtils.REMOTE_PATH, htMessage.);
                //  body.put(MessageUtils.REMOTE_PATH_THUMBNAIL, baseOssUrl + fileName + MessageUtils.reSize);
                htMessageLocationBody.setRemotePath(baseOssUrl + htMessageLocationBody.getFileName());
                htMessage.setBody(htMessageLocationBody);
                sendXmppMessage(htMessage);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                htMessageCallBack.onFailure();

            }
        });

    }

    private void sendXmppMessage(HTMessage htMessage) {
        Intent intent = new Intent(context, MessageService.class);
        intent.putExtra("TYPE", MessageService.TYPE_CHAT);
        intent.putExtra("chatTo", htMessage.getTo());
        intent.putExtra("body", htMessage.toXmppMessageBody());
        intent.putExtra("chatType", htMessage.getChatType().ordinal() + 1);
        intent.putExtra("msgId", htMessage.getMsgId());
      //  intent.setAction(MessageService2.ACTIONN_SEND_MESSAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context. startForegroundService(intent);
        } else {
            context. startService(intent);
        }
         //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        Log.d("sendAMessage:","---->SDK  sendBroadcast---->"+htMessage.getMsgId());

        HTClient.getInstance().messageManager().saveMessage(htMessage, false);

    }


    public void sendCmdMessage(final CmdMessage cmdMessage, final HTMessageCallBack htMessageCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                htMessageCallBackMap.put(cmdMessage.getMsgId(), htMessageCallBack);
                htMessageCallBack.onProgress();
                 Intent intent = new Intent(context, MessageService.class);
               // Intent intent = new Intent();
                intent.putExtra("TYPE", MessageService.TYPE_CHAT_CMD);
                intent.putExtra("chatTo", cmdMessage.getTo());
                intent.putExtra("body", cmdMessage.toXmppMessage());
                intent.putExtra("msgId", cmdMessage.getMsgId());
                intent.putExtra("chatType", cmdMessage.getChatType().ordinal() + 1);
               // intent.setAction(MessageService2.ACTIONN_SEND_MESSAGE_CMD);
                // context.startService(intent);
              //  LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context. startForegroundService(intent);
                } else {
                    context. startService(intent);
                }
            }
        }).start();

    }

    public void sendCallMessage(final CallMessage callMessage, final HTMessageCallBack htMessageCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                htMessageCallBackMap.put(callMessage.getMsgId(), htMessageCallBack);
                htMessageCallBack.onProgress();
                 Intent intent = new Intent(context, MessageService.class);
                //Intent intent = new Intent();
                intent.putExtra("TYPE", MessageService.TYPE_CHAT_CMD);
                intent.putExtra("chatTo", callMessage.getTo());
                intent.putExtra("body", callMessage.toXmppMessage());
                intent.putExtra("msgId", callMessage.getMsgId());
                intent.putExtra("chatType", callMessage.getChatType().ordinal() + 1);

               // intent.setAction(MessageService2.ACTIONN_SEND_MESSAGE_CMD);
                // context.startService(intent);
            //    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
               // context.startService(intent);
              //  new Intent(MessageService2.this, MessageService2.class)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context. startForegroundService(intent);
                } else {
                    context. startService(intent);
                }


            }
        }).start();

    }

    public interface HTMessageCallBack {

        void onProgress();

        void onSuccess(long timeStamp);

        void onFailure();
    }


}
