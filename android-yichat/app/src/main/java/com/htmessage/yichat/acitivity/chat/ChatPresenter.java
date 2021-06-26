package com.htmessage.yichat.acitivity.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.update.Constant;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.uitls.GifSizeFilter;
import com.htmessage.update.uitls.MsgUtils;
import com.htmessage.update.uitls.WidgetUtils;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.card.CheckCardActivity;
import com.htmessage.yichat.acitivity.chat.file.browser.FileBrowserActivity;
import com.htmessage.yichat.acitivity.chat.pick.PickAtUserActivity;
import com.htmessage.yichat.acitivity.chat.video.CameraActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.HTMessageUtils;
import com.htmessage.yichat.utils.HTPathUtils;
import com.htmessage.sdk.manager.MmvkManger;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;

import static com.htmessage.yichat.acitivity.chat.video.CameraActivity.RESULT_CODE_PERMISS_REJECT;
import static com.htmessage.yichat.acitivity.chat.video.CameraActivity.RESULT_CODE_RETURN_PHOTO;
import static com.htmessage.yichat.acitivity.chat.video.CameraActivity.RESULT_CODE_RETURN_VIDEO;


/**
 * Created by dell on 2017/7/1.
 */


public class ChatPresenter implements ChatContract.Presenter {

    private List<HTMessage> htMessageList = new ArrayList<>();
    private ChatContract.View chatView;
    private String chatTo;
    private static final int REQUEST_CODE_MAP = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private static final int REQUEST_CODE_LOCAL = 3;
    private static final int REQUEST_CODE_SELECT_FILE = 5;
    public static final int REQUEST_CODE_SELECT_RP = 6;
    private static final int REQUEST_CODE_SELECT_TRANSFER = 7;
    private static final int REQUEST_CODE_SELECT_AT_USER = 8;
    private static final int REQUEST_CODE_SELECT_NEAR_IMAGE = 9;
    private static final int REQUEST_CODE_SEND_CARD = 10;
    private int chatType = 1;

    private JSONObject userInfoJson = new JSONObject();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 3000:
                    //获取到最新的20条消息,初始化的时候调用
                    if (msg.obj != null) {
                        List<HTMessage> htMessages = (List<HTMessage>) msg.obj;
                        htMessageList.clear();
                        htMessageList.addAll(htMessages);
                        chatView.initRecyclerView(htMessageList);
//                        if(htMessages.size()<15){
//                            getMoreGroupMsgFromDb();
//                        }
                    }

                    break;
                case 3001:
                    //拉取更多消息
                    if (msg.obj != null) {
                        List<HTMessage> htMessages = (List<HTMessage>) msg.obj;
                        htMessageList.addAll(0, htMessages);
                        chatView.loadMoreMessageRefresh(0, htMessages.size());
                    } else {
                        //chatView.showToast();
                    }
                    break;
                case 3002:
                    //发送一条消息，初始状态-发送中
                    HTMessage htMessage = (HTMessage) msg.obj;
                    if (!htMessageList.contains(htMessage)) {
                        htMessageList.add(htMessage);
                        addToShowDetailsList(htMessage);

                        chatView.insertRecyclerView(htMessageList.size(), 1, 2);
                        Log.d("sendAMessage:", "---->Handler  sendMessage---->" + htMessage.getMsgId());

                    }

                    break;
                case 3003:
                    //发送的消息成功了。状态变更
                    HTMessage htMessage1 = (HTMessage) msg.obj;
                    if (htMessageList.contains(htMessage1)) {

                        chatView.updateRecyclerView(htMessageList.indexOf(htMessage1));
                        CommonUtils.upLoadMessage(htMessage1);
                    }
                    break;
                case 3004:
                    //收到一个新消息
                    HTMessage htMessageReceived = (HTMessage) msg.obj;
                    if (!htMessageList.contains(htMessageReceived)) {
                        htMessageList.add(htMessageReceived);
                        addToShowDetailsList(htMessageReceived);
                        chatView.insertRecyclerView(htMessageList.size(), 1, 1);
                    }

                    break;

                case 3005:
                    //调用sdk发送最终的消息
                    HTMessage htMessageFinal = (HTMessage) msg.obj;
                    ChatPresenter.this.sendMessage(htMessageFinal);
                    break;

                case 3006:
                    chatView.onGroupInfoLoaded();
                    break;
//                case 3007:
//                    //发送的文件不存在
//                    //  CommonUtils.showToastShort(getContext(), R.string.File_does_not_exist);
//                    break;
                case 3008:
                    //发送的文件大于10M
                    chatView.showToast(R.string.size_10M);
                    break;
                case 3009:
                    //发送压缩之后的图片
                    Bundle bundleImages = msg.getData();

                    String filePath = bundleImages.getString("filePath");
                    String sizeImage = bundleImages.getString("size");
                    sendImageMessage(filePath, sizeImage);
                    break;
                case 4000:
                    //非管理员只能撤回指定时间内的消息
                    //  CommonUtils.showToastShort(getActivity(), R.string.reback_not_more_than_30);
                    break;
                case 4001:

                    CmdMessage cmdMessage = (CmdMessage) msg.obj;
                    sendWithdrawCmd(cmdMessage);


                    break;
                case 4002:
                    HTMessage htMessageWithdraw = (HTMessage) msg.obj;
                    if (htMessageWithdraw != null) {
                        int position = msg.arg1;
                        htMessageList.set(position, htMessageWithdraw);
                        chatView.updateRecyclerView(position);

                    }
                    if (msg.arg2 == 1) {
                        updateMessageInServer(htMessageWithdraw.getMsgId(), htMessageWithdraw);
                    }

                    break;
                case 4003:
                    int resId = msg.arg1;
                    chatView.showToast(resId);

                    break;
                case 4004:
                    Bundle videoBundle = msg.getData();
                    String videoPath = videoBundle.getString("videoPath");
                    String thumbPath = videoBundle.getString("thumbPath");
                    int duration = videoBundle.getInt("duration");
                    String size = videoBundle.getString("size");
                    sendVideoMessage(videoPath, thumbPath, duration, size);
                    break;
                case 4005:
                    getMoreGroupMsgFromDb();


                    break;
                case 6001:
                    //进去红包详情页
                    JSONObject data = (JSONObject) msg.obj;
                    chatView.startToDetailRp(data);
                    break;
                case 6002:
                    //进入红包领取页
                    JSONObject data1 = (JSONObject) msg.obj;
                    chatView.startToDialogRP(data1);

                    break;
                case 7000:
                    JSONArray noticeList = (JSONArray) msg.obj;
                    if (noticeList != null && noticeList.size() != 0) {
                        JSONObject dataJson = noticeList.getJSONObject(noticeList.size() - 1);
                        String title = dataJson.getString("title");
                        String id = dataJson.getString("noticeId");
                        String content = dataJson.getString("content");
                        String preId = MmvkManger.getIntance().getAsString("group_notice" + HTApp.getInstance().getUsername() + chatTo);
                        if (!TextUtils.isEmpty(id) && !id.equals(preId)) {
                            chatView.showNewNoticeDialog(title, content, id);
                        }
                    }
                    break;
//
            }
        }
    };


    public ChatPresenter(ChatContract.View view) {
        chatView = view;
        chatView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void sendZhenMessage() {

    }

    @Override
    public void initData(Bundle bundle) {

        chatType = bundle.getInt("chatType", MessageUtils.CHAT_SINGLE);
        chatTo = bundle.getString("userId");
        //附加字段中备注消息发送者信息
        userInfoJson.put("userId", UserManager.get().getMyUserId());
        userInfoJson.put("nick", UserManager.get().getMyNick());
        userInfoJson.put("avatar", UserManager.get().getMyAvatar());

        chatView.initRecyclerView(htMessageList);
        if (chatType == 2) {
            //应用每次启动后，群信息只获取一次。设置一个全局静态变量去做标记存取
            getGroupInfo(chatTo);
            getNewNotice();
        } else {
            getChatHistoryInDb(chatTo);
        }


    }


    private void getGroupInfo(String groupId) {
        if (GroupInfoManager.getInstance().isGroupInfoLoaded(chatTo)) {
            getChatHistoryInDb(chatTo);
            //获取所有群成员


        } else {
            getGroupInfoInServer(chatTo);

            GroupInfoManager.getInstance().getGroupAllMembersFromServer(chatTo,null);
        }
    }

    public void getGroupInfoInServer(String groupId) {

        JSONObject data = new JSONObject();
//            HTMessage  htMessage=HTClient.getInstance().messageManager().getLastMessage(chatTo);
//            if(htMessage!=null){
//                data.put("timestamp", htMessage.getTime());
//            }
        data.put("groupId", Long.parseLong(groupId));
        ApiUtis.getInstance().postJSON(data, Constant.URL_GROUP_INFO, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONObject data = jsonObject.getJSONObject("data");

                    //获取最近的15条消息
                    JSONArray jsonArray = data.getJSONArray("lastList");
                    handleMsgData(jsonArray, false);
                    //下一步需要临时保存群信息，但是最近的20条消息不需要存入临时列表
                    data.remove("lastList");
                    GroupInfoManager.getInstance().saveGroupInfoTemp(data);
                    MmvkManger.getIntance().putJSON(groupId + "_groupInfo_cache", data);
                    Message message = handler.obtainMessage();
                    message.what = 3006;
                    message.sendToTarget();
                    GroupInfoManager.getInstance().hasGroupInfoLoaded(chatTo);
                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });

    }


    private void getChatHistoryInDb(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<HTMessage> htMessages = HTClient.getInstance().messageManager().getMessageList(userId);
                for (int i = 0; i < htMessages.size(); i++) {
                    HTMessage htMessage = htMessages.get(i);
                    addToShowDetailsList(htMessage);
                    if (htMessage.getStatus() == HTMessage.Status.CREATE) {
                        htMessage.setStatus(HTMessage.Status.FAIL);
                        htMessages.set(i, htMessage);
                        HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                    }
                }


                Message message = handler.obtainMessage();
                message.what = 3000;
                message.obj = htMessages;
                message.sendToTarget();
            }
        }).start();

    }


    @Override
    public void resendMessage(final HTMessage htMessage) {
        int index = htMessageList.indexOf(htMessage);
        htMessageList.remove(htMessage);
        removeShowDetailsList(htMessage);
        chatView.deleteItemRecyclerView(index);
        htMessage.setStatus(HTMessage.Status.CREATE);
        sendMessage(htMessage);


    }

    @Override
    public void deleteSingChatMessage(final HTMessage htMessage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HTClient.getInstance().messageManager().deleteMessage(chatTo, htMessage.getMsgId());

            }
        }).start();
        int index = htMessageList.indexOf(htMessage);
        htMessageList.remove(htMessage);
        removeShowDetailsList(htMessage);
        chatView.deleteItemRecyclerView(index);

    }


    //撤回一条消息
    @Override
    public void withdrowMessage(final HTMessage htMessage, final int positionTemp) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                CmdMessage cmdMessage = new CmdMessage();
                cmdMessage.setTo(chatTo);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action", 6000);
                jsonObject.put("msgId", htMessage.getMsgId());
                jsonObject.put("opId", UserManager.get().getMyUserId());
                jsonObject.put("opNick", UserManager.get().getMyNick());

                cmdMessage.setBody(jsonObject.toString());
                if (chatType == MessageUtils.CHAT_GROUP) {
                    cmdMessage.setChatType(ChatType.groupChat);
                }
                Message message = handler.obtainMessage();
                message.what = 4001;
                message.obj = cmdMessage;
                message.sendToTarget();
                int position = htMessageList.indexOf(htMessage);
                HTMessageUtils.makeToWithDrowMsg(htMessage, UserManager.get().getMyUserId(), UserManager.get().getMyNick());
                removeShowDetailsList(htMessage);
                Message message1 = handler.obtainMessage();
                message1.obj = htMessage;
                message1.what = 4002;
                message1.arg1 = position;
                message1.arg2 = 1;
                message1.sendToTarget();


            }
        }).start();


    }

    private void sendWithdrawCmd(CmdMessage cmdMessage) {


        HTClient.getInstance().chatManager().sendCmdMessage(cmdMessage, new HTChatManager.HTMessageCallBack() {
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

    /**
     * 收到撤回通知
     */
    @Override
    public void onMessageWithdrow(HTMessage htMessageWD) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < htMessageList.size(); i++) {
                    HTMessage htMessage = htMessageList.get(i);
                    if (htMessage.getMsgId().equals(htMessageWD.getMsgId())) {
                        removeShowDetailsList(htMessage);
                        Message message = handler.obtainMessage();
                        message.obj = htMessageWD;
                        message.what = 4002;
                        message.arg1 = i;
                        message.arg2 = 0;
                        message.sendToTarget();
                    }
                }
            }
        }).start();

    }

    /**
     * 更新撤回消息服务器端
     *
     * @param msgId
     */
    private void updateMessageInServer(final String msgId, final HTMessage message) {

        JSONObject data = new JSONObject();
        data.put("messageId", msgId);
        try {
            data.put("content", URLEncoder.encode(message.toXmppMessageBody(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiUtis.getInstance().postJSON(data, Constant.URL_MESSAGE_UPDATE, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

            }

            @Override
            public void onFailure(int errorCode) {

            }
        });


    }

    @Override
    public void onNewMessage(final HTMessage htMessage) {
        if (htMessage.getUsername().equals(chatTo)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int action = htMessage.getIntAttribute("action", 0);
                    String msgFrom = htMessage.getStringAttribute("msgFrom");
//                    if (action == 10004 && !TextUtils.isEmpty(msgFrom) && !msgFrom.equals(HTApp.getInstance().getUsername())) {
//                        //红包领取通知
//                    } else {
                    Message message = handler.obtainMessage();
                    message.obj = htMessage;
                    message.what = 3004;
                    message.sendToTarget();
                    HTClient.getInstance().conversationManager().markAllMessageRead(chatTo);
                    //  }

                }
            }).start();
        }
    }


    /**
     * 选择个人名片
     */
    @Override
    public void startCardSend(Activity activity) {
        activity.startActivityForResult(new Intent(activity, CheckCardActivity.class), REQUEST_CODE_SEND_CARD);
    }
    private Map<String ,String > atUserMap=new HashMap<>();

    @Override
    public void setAtUser(String nick, String userId) {
        Log.d("nick---5",nick);
        Log.d("nick---6",userId);
        atUserMap.put(nick,userId);
    }

    @Override
    public boolean isHasAt(String userId) {
        return atUserMap.containsValue(userId);
    }
    @Override
    public boolean isHasAtNick(String nick) {
        Log.d("nick---9",nick);
        return atUserMap.containsKey(nick);
    }
    @Override
    public void startChooseAtUser() {
        chatView.getBaseActivity(). startActivityForResult(new Intent(chatView.getBaseContext(), PickAtUserActivity.class).
                putExtra("groupId", chatTo), REQUEST_CODE_SELECT_AT_USER);
    }

    @Override
    public void deleteAtUser(String nick) {
        atUserMap.remove(nick);
    }

    @Override
    public void refreshHistory() {
        JSONObject data = new JSONObject();
        data.put("referId", chatTo);
        data.put("referType", chatType);


        ApiUtis.getInstance().postJSON(data, Constant.URL_CHAT_HISTORY, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    Log.d("handleList--->refresh", jsonArray.toJSONString());
                    handleMsgData(jsonArray, false);
                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });


    }

    @Override
    public void onMeesageForward(HTMessage htMessage) {
        if (htMessage.getUsername().equals(chatTo)) {
            htMessageList.add(htMessage);
        }
    }

    @Override
    public void onMessageClear() {
        //单聊只需要清除本地聊天记录。群聊暂不清理（如果清理，需要设置服务器端取消息的起始时间戳，可以通过更新进群时间实现）
        htMessageList.clear();
        chatView.notifyClear();
    }

    @Override
    public void onOpenRedpacket(HTMessage htMessage, String packetId) {

        JSONObject body = new JSONObject();
        body.put("packetId", packetId);
        ApiUtis.getInstance().postJSON(body, Constant.URL_RedPacket_Detail, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {

                    JSONObject data = jsonObject.getJSONObject("data");
                    data.put("msgId", htMessage.getMsgId());

//                    "data": {
//                        "list": [
//                        {
//                            "userId": 1,
//                                "nick": "测试一下",
//                                "avatar": "sssss",
//                                "money": 1.18,
//                                "moneyDesc": "1.18元",
//                                "receiveTime": "2019-06-12 18:35:41",
//                                "maxStatus": 0
//                        }
//        ],
//                        "packetId": 19,
//                                "content": "111122",
//                                "nick": "测试一下",
//                                "userId": 1,
//                                "avatar": "sssss",
//                                "status": 1,
//                                "num": 100,
//                                "receiveNum": 1,
//                                "money": 20,
//                                "receiveMoney": 1.18
//                    },
                    int receiveStatus = data.getInteger("status");
                    String userId = data.getString("userId");
                    if (chatType == 1) {
                        if (!UserManager.get().getMyUserId().equals(userId) && receiveStatus == 0) {
                            //跳领取页
                            Message message = handler.obtainMessage();
                            message.what = 6002;
                            message.obj = data;
                            message.sendToTarget();

                        } else {

                            //如果我是接收方，跳到红包详情页
                            Message message = handler.obtainMessage();
                            message.what = 6001;
                            message.obj = data;
                            message.sendToTarget();
                        }

                    } else {

                        if (receiveStatus == 0) {
                            //如果群红包是未领取状态或者我未领取，并且群红包未领完，进入领取页
                            Message message = handler.obtainMessage();
                            message.what = 6002;
                            message.obj = data;
                            message.sendToTarget();
                        } else {
                            //进入详情页
                            Message message = handler.obtainMessage();
                            message.what = 6001;
                            message.obj = data;
                            message.sendToTarget();
                        }


                    }


                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.sendToTarget();

                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });

    }


    @Override
    public void loadMoreMessages() {
        if (htMessageList == null || htMessageList.size() == 0) {
            return;
        }
        final HTMessage htMessage = htMessageList.get(0);

        if (chatType == MessageUtils.CHAT_GROUP) {
            getMessageListFromServer(chatTo, htMessage.getTime());
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                List<HTMessage> htMessages = HTClient.getInstance().messageManager().loadMoreMsgFromDB(chatTo, htMessage.getTime(), 15);
                if (htMessages.size() == 0) {
                    Message message = handler.obtainMessage();
                    message.what = 4003;
                    message.arg1 = R.string.no_more_msg;
                    message.sendToTarget();
                } else {
                    for (int i = 0; i < htMessages.size(); i++) {
                        HTMessage htMessage = htMessages.get(i);
                        addToShowDetailsList(htMessage);
                        if (htMessage.getStatus() == HTMessage.Status.CREATE) {
                            htMessage.setStatus(HTMessage.Status.FAIL);
                            htMessages.set(i, htMessage);
                            HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                        }
                    }
                    Message message = handler.obtainMessage();
                    message.what = 3001;
                    message.obj = htMessages;
                    message.sendToTarget();

                }
            }
        }).start();

    }

    private void getMoreGroupMsgFromDb() {
        if (htMessageList == null || htMessageList.size() == 0) {
            //此时历史记录全为空
            getChatHistoryInDb(chatTo);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final HTMessage htMessageLast = htMessageList.get(0);
                    List<HTMessage> htMessages = HTClient.getInstance().messageManager().loadMoreMsgFromDB(chatTo, htMessageLast.getTime(), 15);
                    if (htMessages.size() == 0) {
                        Message message = handler.obtainMessage();
                        message.what = 4003;
                        message.arg1 = R.string.no_more_msg;
                        message.sendToTarget();
                    } else {
                        for (int i = 0; i < htMessages.size(); i++) {
                            HTMessage htMessage = htMessages.get(i);
                            addToShowDetailsList(htMessage);
                            if (htMessage.getStatus() == HTMessage.Status.CREATE) {
                                htMessage.setStatus(HTMessage.Status.FAIL);
                                htMessages.set(i, htMessage);
                                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                            }
                        }
                        Message message = handler.obtainMessage();
                        message.what = 3001;
                        message.obj = htMessages;
                        message.sendToTarget();

                    }
                }
            }).start();


        }
    }


    private void sendVideoMessage(final String videoPath, String thumbPath, final int duration, String size) {
        File file = new File(videoPath);
        if (TextUtils.isEmpty(videoPath) || TextUtils.isEmpty(thumbPath) || !file.exists() || !new File(thumbPath).exists()) {
            Message message = handler.obtainMessage();
            message.what = 4003;
            message.arg1 = R.string.video_path_error;
            message.sendToTarget();
            return;
        }
        if (file.length() > 10 * 1024 * 1024) {
            Message message = handler.obtainMessage();
            message.what = 4003;
            message.arg1 = R.string.size_10M;
            message.sendToTarget();
            return;
        }

        HTMessage htMessage = HTMessage.createVideoSendMessage(chatTo, videoPath, thumbPath, duration, size);
        sendMessage(htMessage);
        ChatFileManager.get().setLocalPath(htMessage.getMsgId(), videoPath, htMessage.getType());
    }


    @Override
    public void sendTextMessage(final String content) {
        HTMessage htMessage = HTMessage.createTextSendMessage(chatTo, content);
        if(chatType==2&&content.contains("@")&&content.contains(" ")){
            //String nick=content.substring(content.indexOf("@"),content.indexOf(" "));
            String[] atUsers=content.split("@");
            String atUserId="";
            for(int i=0;i<atUsers.length;i++){
                String userString=atUsers[i];
                 if(userString.endsWith(" ")){
                    userString= userString.substring(0,userString.length()-1);
                     String userId=atUserMap.get(userString);
                    if(userId!=null){
                        atUserId=atUserId+userId+",";
                    }
                }else if(userString.contains(" ")) {
                     userString=userString.substring(0,userString.indexOf(" "));
                     String userId=atUserMap.get(userString);
                     if(userId!=null){
                         atUserId=atUserId+userId+",";
                     }
                 }
            }
            if(!TextUtils.isEmpty(atUserId)){
                atUserId=atUserId.substring(0,atUserId.length()-1);
                Log.d("ext---",atUserId);
                htMessage.setAttribute("atUser",atUserId);
             }

        }

        sendMessage(htMessage);
    }


    @Override
    public void selectPicFromCamera(Activity activity) {

        Matisse.from(activity)
                .choose(MimeType.ofAll())
                .countable(true)
                .maxSelectable(1)
                .maxOriginalSize(10)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_LOCAL);
    }

    @Override
    public void selectPicFromLocal(Activity activity) {
        activity.startActivityForResult(new Intent(activity, CameraActivity.class).putExtra("onlyPhotograph", false), REQUEST_CODE_CAMERA);//请自定义最后一个参数：常量

    }


    @Override
    public void onResult(int requestCode, int resultCode, Intent data, final Context context) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_CODE_RETURN_PHOTO) {//拍照
                //照片路径
                String photoPath = data.getStringExtra("path");

                List<String> list = new ArrayList<>();
                list.add(photoPath);
                compressMore(list, context);

            } else if (resultCode == RESULT_CODE_RETURN_VIDEO) {//摄像
                //视频第一帧图片路径
                String firstVideoPicture = data.getStringExtra("path");
                //视频路径，该路径为已压缩过的视频路径
                String videoPath = data.getStringExtra("videoUrl");

                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(videoPath);
                    mediaPlayer.prepare();
                    int duration = mediaPlayer.getDuration() / 1000;
                    int height = mediaPlayer.getVideoHeight();
                    int width = mediaPlayer.getVideoWidth();

//
//                    File file = new File(new HTPathUtils(chatTo, context).getVideoPath(), "th_video" + System.currentTimeMillis() + ".png");
//                    FileOutputStream fos = new FileOutputStream(file);
//                    Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
//                    ThumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                    fos.close();
                    mediaPlayer.release();

                    Bundle bundle = new Bundle();
                    bundle.putString("videoPath", videoPath);
                    bundle.putString("thumbPath", firstVideoPicture);
                    bundle.putInt("duration", duration);
                    bundle.putString("size", width + "," + height);
                    Message message = handler.obtainMessage();
                    message.what = 4004;
                    message.setData(bundle);
                    message.sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == RESULT_CODE_PERMISS_REJECT) {
                Message message = handler.obtainMessage();
                message.what = 4003;
                message.arg1 = R.string.no_permission;
                message.sendToTarget();
            }
        }


        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
//                case REQUEST_CODE_SELECT_VIDEO: //send the video
//                    if (data != null) {
//                        int duration = data.getIntExtra("dur", 0);
//                        String videoPath = data.getStringExtra("path");
//                        sendVideoMessage(videoPath, duration);
//                    }
//                    break;
                case REQUEST_CODE_SELECT_FILE: //send the file
                    if (data != null) {
                        String path = data.getStringExtra(FileBrowserActivity.EXTRA_DATA_PATH);
                        Uri uri = Uri.parse(path);
                        if (uri != null) {
                            //  sendFileByUri(uri, path);
                        }
                    }
                    break;
//                case REQUEST_CODE_CAMERA:
////                    if (cameraFile != null && cameraFile.exists()) {
////                        List<String> list = new ArrayList<>();
////                        list.add(cameraFile.getAbsolutePath());
////                        compressMore(list);
////                    }

//                    break;
                case REQUEST_CODE_LOCAL:
                    if (data != null) {
                        // ArrayList<String> list = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        List<Uri> list = Matisse.obtainResult(data);
                        List<String> images = new ArrayList<>();
                        final List<String> videos = new ArrayList<>();
                        for (Uri uri : list) {
                            if (uri.toString().contains("images")) {
                                images.add(WidgetUtils.getPhotoPathFromContentUri(context, uri));
                            } else if (uri.toString().contains("video")) {
                                videos.add(WidgetUtils.getPhotoPathFromContentUri(context, uri));
                            }
                        }
                        compressMore(images, context);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (String url : videos) {
                                    MediaPlayer mediaPlayer = new MediaPlayer();
                                    try {
                                        mediaPlayer.setDataSource(url);
                                        mediaPlayer.prepare();
                                        int duration = mediaPlayer.getDuration() / 1000;
                                        mediaPlayer.release();
                                        File file = new File(new HTPathUtils(chatTo, context).getVideoPath(), "th_video" + System.currentTimeMillis() + ".png");
                                        FileOutputStream fos = new FileOutputStream(file);
                                        Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                                        ThumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                        fos.close();

                                        Bundle bundle = new Bundle();
                                        bundle.putString("videoPath", url);
                                        bundle.putString("thumbPath", file.getAbsolutePath());
                                        bundle.putInt("duration", duration);
                                        Message message = handler.obtainMessage();
                                        message.what = 4004;
                                        message.setData(bundle);
                                        message.sendToTarget();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }).start();


                    }
                    break;
                case REQUEST_CODE_MAP:
//                    double latitude = data.getDoubleExtra("latitude", 0);
//                    double longitude = data.getDoubleExtra("longitude", 0);
//                    String locationAddress = data.getStringExtra("address");
//                    String thumbailPath = data.getStringExtra("thumbnailPath");
//                    if (!TextUtils.isEmpty(locationAddress) && new File(thumbailPath).exists()) {
//                        sendLocationMessage(latitude, longitude, locationAddress, thumbailPath);
//                    } else {
//                        // CommonUtils.showToastShort(getContext(), R.string.unable_to_get_loaction);
//                    }
                    break;
                case REQUEST_CODE_SELECT_RP:
//                    if (data != null) {
//                        EnvelopeBean singleRpbean = JrmfRpClient.getEnvelopeInfo(data);
//                        sendRedMessage(singleRpbean);
//                    }

                    if (data != null) {
                        String extra = data.getStringExtra("data");
                        JSONObject object = JSONObject.parseObject(extra);
                        sendRedMessage(object);
                    }
                    break;
                case REQUEST_CODE_SELECT_TRANSFER:
                    if (data != null) {
                        String extra = data.getStringExtra("data");
                        JSONObject object = JSONObject.parseObject(extra);
                        sendTransferMessage(object);

                    }

                case REQUEST_CODE_SELECT_NEAR_IMAGE:
                    if (data != null) {
                        String path = data.getStringExtra("path");
                        List<String> list = new ArrayList<>();
                        list.add(path);
                        compressMore(list, context);
                    }
                    break;
                case REQUEST_CODE_SEND_CARD:
                    if (data != null) {
                        String user = data.getStringExtra("user");
                        sendCardMessage(user);
                    }
                    break;
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {

                        String userId = data.getStringExtra(HTConstant.JSON_KEY_USERID);

                        if(isHasAt(userId)){
                            return;
                        }
                        String realNick=UserManager.get().getUserRealNick(userId);
                        if(userId.equals("全体成员")){
                            realNick="全体成员";
                            userId="all";
                        }

                        chatView.setAtUserStytle(realNick,true);
                        setAtUser(realNick,userId);

                    }
                    break;
                default:
                    break;
            }
        }
    }


    private void sendCardMessage(final String user) {


        if (TextUtils.isEmpty(user)) {
            return;
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(user);
            JSONObject extJSON = new JSONObject();
            extJSON.put("action", 10007);
            extJSON.put("cardUserId", jsonObject.getString("userId"));
            extJSON.put("cardUserNick", UserManager.get().getUserRealNick(jsonObject.getString("userId")));
            extJSON.put("cardUserAvatar", jsonObject.getString(HTConstant.JSON_KEY_AVATAR));
            HTMessage message = HTMessage.createTextSendMessage(chatTo, chatView.getBaseContext().getString(R.string.self_card));
            message.setAttributes(extJSON);
            sendMessage(message);
        } catch (Exception e) {
        }


    }
    private long preTime=0;
    private void sendMessage(final HTMessage htMessage) {
        long currentTime=System.currentTimeMillis();
        if((currentTime-preTime)/1000 <1){
            chatView.showToast(R.string.send_too_much);
            return;
        }

        JSONObject ext = htMessage.getAttributes();

        if (ext == null) {
            ext = new JSONObject();
        }
        Log.d("ext--->",ext.toJSONString());
        ext.putAll(userInfoJson);
        htMessage.setAttributes(ext);
        if (chatType == 2) {
            htMessage.setChatType(ChatType.groupChat);
        }else {
            //强制好友可发消息
//           if( !UserManager.get().getFriends().contains(chatTo)){
//             LocalBroadcastManager.getInstance(chatView.getBaseContext()).sendBroadcast(new Intent(IMAction.CMD_DELETE_FRIEND).putExtra("userId", chatTo));
//               return;
//           }

        }

        if (GroupInfoManager.getInstance().isGroupSilent(chatTo) && !GroupInfoManager.getInstance().isManager(chatTo)) {
            chatView.showToast(R.string.has_no_talk);
            return;
        }


        HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {
                Log.d("sendAMessage:", "---->APP  sendMessage---->" + htMessage.getMsgId());
                Message message = handler.obtainMessage();
                message.what = 3002;
                message.obj = htMessage;
                handler.sendMessage(message);
            }

            @Override
            public void onSuccess(long timeStamp) {
                htMessage.setStatus(HTMessage.Status.SUCCESS);
                htMessage.setTime(timeStamp);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);

                Message message = handler.obtainMessage();
                message.what = 3003;
                message.obj = htMessage;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure() {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                Message message = handler.obtainMessage();
                message.what = 3003;
                message.obj = htMessage;
                handler.sendMessage(message);

            }

        });

        //清除@标记
        atUserMap.clear();
        preTime=System.currentTimeMillis();
    }

    private void sendImageMessage(final String imagePath, String size) {

        HTMessage htMessage = HTMessage.createImageSendMessage(chatTo, imagePath, size);
        sendMessage(htMessage);

    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return degree;
        }
        return degree;
    }

    /**
     * 旋转图片，使图片保持正确的方向。
     *
     * @param bitmap  原始图片
     * @param degrees 原始图片的角度
     * @return Bitmap 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (null != bitmap) {
            bitmap.recycle();
        }
        return bmp;
    }


    /**
     * 压缩多图
     *
     * @param pathList 传入的为图片原始路径
     */

    @SuppressLint("CheckResult")
    private void compressMore(final List<String> pathList, final Context context) {
        Flowable.just(pathList)
                .subscribeOn(Schedulers.io())
                .map(new Function<List<String>, List<File>>() {
                    @Override
                    public List<File> apply(@NonNull List<String> list) throws Exception {
                        // 同步方法直接返回压缩后的文件
                        return Luban.with(context).load(list).get();
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        for (int i = 0; i < files.size(); i++) {
                            File file = files.get(i);
                            String filePath=file.getPath();
                            if(TextUtils.isEmpty(filePath)){
                                filePath=file.getAbsolutePath();
                            }
                            Bitmap bmp = BitmapFactory.decodeFile(filePath);
                            Bitmap bitmap = rotateBitmap(bmp, readPictureDegree(filePath));
                            String size = bitmap.getWidth() + "," + bitmap.getHeight();
                            Bundle bundle = new Bundle();
                            bundle.putString("size", size);

                            bundle.putString("filePath",filePath);
                            Message message = handler.obtainMessage();
                            message.what = 3009;
                            message.setData(bundle);
                            handler.sendMessageDelayed(message, i * 1000);
                        }
                    }
                });

    }


    @Override
    public void sendVoiceMessage(final String filePath, final int length) {
        HTMessage htMessage = HTMessage.createVoiceSendMessage(chatTo, filePath, length);
        sendMessage(htMessage);
        ChatFileManager.get().setLocalPath(htMessage.getMsgId(), filePath, htMessage.getType());

    }


    public void sendRedMessage(final JSONObject singleRpbean) {

        JSONObject extJSON = new JSONObject();
        extJSON.putAll(userInfoJson);
//                extJSON.put("action", 10001); //@{@"action":@"10001",@"envId":envId,@"envName":envName,@"envMsg":envMsg}
//                extJSON.put("envId", singleRpbean.getEnvelopesID());
//                extJSON.put("envName", chatView.getBaseActivity().getString(R.string.red_content));
//                extJSON.put("envMsg", singleRpbean.getEnvelopeMessage());
        extJSON.put("action", 10001); //@{@"action":@"10001",@"envId":envId,@"envName":envName,@"envMsg":envMsg}
        extJSON.put("envId", singleRpbean.getString("redpacketId"));
        extJSON.put("envName", chatView.getBaseActivity().getString(R.string.red_content));
        extJSON.put("envMsg", singleRpbean.getString("content"));
        HTMessage htMessage = HTMessage.createTextSendMessage(chatTo, chatView.getBaseActivity().getString(R.string.red_message));
        htMessage.setAttributes(extJSON);
        sendMessage(htMessage);


    }

    public void sendTransferMessage(final JSONObject singleRpbean) {

        JSONObject extJSON = new JSONObject();
        extJSON.putAll(userInfoJson);
//                extJSON.put("action", 10001); //@{@"action":@"10001",@"envId":envId,@"envName":envName,@"envMsg":envMsg}
//                extJSON.put("envId", singleRpbean.getEnvelopesID());
//                extJSON.put("envName", chatView.getBaseActivity().getString(R.string.red_content));
//                extJSON.put("envMsg", singleRpbean.getEnvelopeMessage());
        extJSON.put("action", 10002); //@{@"action":@"10001",@"envId":envId,@"envName":envName,@"envMsg":envMsg}
        extJSON.put("transferId", singleRpbean.getString("redpacketId"));
        extJSON.put("amountStr", singleRpbean.getString("money"));
        extJSON.put("msg", singleRpbean.getString("content"));
        HTMessage htMessage = HTMessage.createTextSendMessage(chatTo, chatView.getBaseActivity().getString(R.string.transfer_message));
        htMessage.setAttributes(extJSON);
        sendMessage(htMessage);


    }


    @SuppressLint("CheckResult")
    private void getMessageListFromServer(String chatTo, long timestamp) {
        JSONObject data = new JSONObject();
        data.put("referId", chatTo);
        data.put("referType", chatType);
        if(timestamp!=0){
            data.put("time", timestamp);
        }

        ApiUtis.getInstance().postJSON(data, Constant.URL_CHAT_HISTORY, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    Log.d("handleList--->2", jsonArray.toJSONString());
                    handleMsgData(jsonArray, true);
                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });


    }


    private void handleMsgData(JSONArray jsonArray, boolean isMore) {
        //是否是加载更多
        List<HTMessage> messages = new ArrayList<>();
        if (jsonArray != null) {

            if (jsonArray.size() == 0) {
                Message message = handler.obtainMessage();
                message.what = 4005;
                message.sendToTarget();
            } else {

                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject msgJSON = jsonArray.getJSONObject(i);
                    String base64String = msgJSON.getString("content");
                    String userId = msgJSON.getString("userId");
                    String nick = msgJSON.getString("nick");
                    String avatar = msgJSON.getString("avatar");
                    long time = msgJSON.getLong("time");
                    //保存用户信息
                    UserManager.get().saveUserNickAvatar(userId, nick, avatar);
                    //转化成消息列表
                    HTMessage htMessage = MsgUtils.getInstance().stringToMessage(base64String, time);
                    if (htMessage != null) {
                        messages.add(htMessage);
                        addToShowDetailsList(htMessage);
                        //消息存库，应用未关闭的情况下次进入该界面，直接取存库的消息
                        HTClient.getInstance().messageManager().saveMessage(htMessage, false);

                    }

                }
                Message message = handler.obtainMessage();
                message.what = 3000;
                if (isMore) {
                    message.what = 3001;

                }
                message.obj = messages;
                message.sendToTarget();

            }


        }
    }

    private void addToShowDetailsList(HTMessage htMessage) {
        if (htMessage.getType() == HTMessage.Type.VIDEO || htMessage.getType() == HTMessage.Type.IMAGE) {
            ChatFileManager.get().addImageOrVideoMessage(htMessage);
        }
    }

    private void removeShowDetailsList(HTMessage htMessage) {
        if (htMessage.getType() == HTMessage.Type.VIDEO || htMessage.getType() == HTMessage.Type.IMAGE) {
            ChatFileManager.get().removeImageOrVideoMessage(htMessage);
        }
    }


    @SuppressLint("StringFormatInvalid")
    @Override
    public void sendRedCmdMessage(String whoisRed, String msgId) {

        String content = "红包领取通知";
        HTMessage htMessage = HTMessage.createTextSendMessage(chatTo, content);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("action", 10004);
        jsonObject1.put("msgId", msgId);
        jsonObject1.put("msgFrom", whoisRed);
        jsonObject1.put("msgFromNick", UserManager.get().getUserNick(whoisRed));
        htMessage.setAttributes(jsonObject1);
        sendMessage(htMessage);
    }


    private void getNewNotice() {

        JSONObject body = new JSONObject();
        body.put("groupId", chatTo);

        ApiUtis.getInstance().postJSON(body, Constant.URL_GROUP_NOTICE_LIST, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    Message message = handler.obtainMessage();
                    message.obj = data;
                    message.what = 7000;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });


    }
}
