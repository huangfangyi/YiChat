package com.htmessage.fanxinht.acitivity.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageFileBody;
import com.htmessage.sdk.model.HTMessageImageBody;
import com.htmessage.sdk.model.HTMessageLocationBody;
import com.htmessage.sdk.model.HTMessageVideoBody;
import com.htmessage.sdk.model.HTMessageVoiceBody;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.chat.call.VideoOutgoingActivity;
import com.htmessage.fanxinht.acitivity.chat.call.VoiceOutgoingActivity;
import com.htmessage.fanxinht.acitivity.chat.file.browser.FileBrowserActivity;
import com.htmessage.fanxinht.acitivity.chat.location.BaiduMapActivity;
import com.htmessage.fanxinht.acitivity.chat.video.CaptureVideoActivity;
import com.htmessage.fanxinht.acitivity.prevideocall.PreVideoCallActivity;
import com.htmessage.fanxinht.utils.ACache;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.utils.HTMessageUtils;
import com.htmessage.fanxinht.utils.HTPathUtils;
import com.htmessage.fanxinht.utils.ImageUtils;
import com.htmessage.fanxinht.widget.HTAlertDialog;

import org.anyrtc.meet_kit.RTMeetKit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by dell on 2017/7/1.
 */


public class ChatPresenter implements ChatContract.Presenter {

    private List<HTMessage> htMessageList;
    private ChatContract.View chatView;
    private String chatTo;
    private static final int REQUEST_CODE_MAP = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private static final int REQUEST_CODE_LOCAL = 3;
    private static final int REQUEST_CODE_SELECT_VIDEO = 4;
    private static final int REQUEST_CODE_SELECT_FILE = 5;
    private File cameraFile;
    private int chatType = 1;
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    HTMessage htMessage = (HTMessage) msg.obj;
                    htMessageList.add(htMessage);
                    chatView.refreshListView();
                    break;
                case 1001:
                    chatView.refreshListView();
                    break;
                case 1002:
                    chatView.refreshListView();
                    break;
                case 1003:
                    String  filePath= (String) msg.obj;
                    sendImageMessage(filePath);
                    break;

            }

        }
    };
    private JSONObject extJSON = new JSONObject();
    public ChatPresenter(ChatContract.View view, String chatTo, int chatType) {
        this.chatTo = chatTo;
        this.chatType = chatType;
        chatView = view;
        chatView.setPresenter(this);
        htMessageList = HTClient.getInstance().messageManager().getMessageList(chatTo);
        extJSON.put(HTConstant.JSON_KEY_HXID, HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_HXID));
        extJSON.put(HTConstant.JSON_KEY_NICK, HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_NICK));
        extJSON.put(HTConstant.JSON_KEY_AVATAR, HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_AVATAR));
    }

    @Override
    public void start() {

    }

    public List<HTMessage> getMessageList() {
        return htMessageList;
    }

    @Override
    public void resendMessage(HTMessage htMessage) {
                 htMessageList.remove(htMessage);
                 HTClient.getInstance().messageManager().deleteMessage(htMessage.getUsername(), htMessage.getMsgId());
                 htMessage.setLocalTime(System.currentTimeMillis());
                 htMessage.setStatus(HTMessage.Status.CREATE);
                 sendMessage(htMessage);

                 chatView.refreshListView();
    }

    @Override
    public void deleteMessage(HTMessage htMessage) {
                    HTClient.getInstance().messageManager().deleteMessage(chatTo, htMessage.getMsgId());
                    htMessageList.remove(htMessage);
                     chatView.refreshListView();
    }

    @Override
    public void copyMessage(HTMessage htMessage) {
         HTMessageUtils.getCopyMsg((Activity) getContext(), htMessage, chatTo);
    }

    @Override
    public void forwordMessage(HTMessage htMessage) {
           HTMessageUtils.getForWordMessage((Activity) getContext(), htMessage, chatTo, extJSON);
    }

    @Override
    public void withdrowMessage(final HTMessage htMessage, final int position) {
                    long msgTime = htMessage.getTime();
                    long nowTime = System.currentTimeMillis();
                    if ((nowTime - msgTime) / (1000 * 60) < 30) {
                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage(getContext().getString(R.string.rebacking));
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        CmdMessage cmdMessage = new CmdMessage();
                        cmdMessage.setTo(chatTo);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("action", 6000);
                        jsonObject.put("msgId", htMessage.getMsgId());
                        cmdMessage.setBody(jsonObject.toString());
                        if (chatType == MessageUtils.CHAT_GROUP) {
                            cmdMessage.setChatType(ChatType.groupChat);
                        }
                        HTClient.getInstance().chatManager().sendCmdMessage(cmdMessage, new HTChatManager.HTMessageCallBack() {
                            @Override
                            public void onProgress() {

                            }

                            @Override
                            public void onSuccess() {
                                HTClient.getInstance().messageManager().deleteMessage(chatTo, htMessage.getMsgId());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        HTMessage message = HTMessageUtils.creatWithDrowMsg(htMessage);
                                        htMessageList.set(position, message);
                                        chatView.refreshListView();
                                    }
                                });

                            }

                            @Override
                            public void onFailure() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        CommonUtils.showToastShort(getActivity(), R.string.reback_failed);
                                    }
                                });
                            }
                        });

                    } else {
                        CommonUtils.showToastShort(getActivity(), R.string.reback_not_more_than_30);
                    }
    }

    /**
     * 收到撤回通知
     *
     * @param msgId
     */
    public void onMessageWithdrow(String msgId) {
        for (int i = 0; i < htMessageList.size(); i++) {
            HTMessage htMessage = htMessageList.get(i);
            if (htMessage.getMsgId().equals(msgId)) {
                HTMessage message = HTMessageUtils.creatWithDrowMsg(htMessage);
                htMessageList.set(i, message);
                chatView.refreshListView();
            }
        }
    }


    @Override
    public void onNewMessage(HTMessage htMessage) {
        if (htMessage.getUsername().contains(chatTo)) {
            if (!htMessageList.contains(htMessage)) {
                htMessageList.add(htMessage);
            }
            chatView.refreshListView();
            HTClient.getInstance().conversationManager().markAllMessageRead(chatTo);
        }

    }

    @Override
    public void onMeesageForward(HTMessage htMessage) {
        if (htMessage.getUsername().equals(chatTo)) {
            if (!htMessageList.contains(htMessage)) {
                htMessageList.add(htMessage);
            }
            chatView.refreshListView();
        }
    }

    @Override
    public void onMessageClear() {
        htMessageList.clear();
        chatView.refreshListView();
    }

    @Override
    public void sendCopyMessage(final String copyType, final String localPath, final HTMessage message1, String imagePath) {

        switch (copyType) {
            case "file":
                HTMessageFileBody fileBody = (HTMessageFileBody) message1.getBody();
                HTMessage emMessage = HTMessage.createFileSendMessage(chatTo, localPath, fileBody.getSize());
                sendMessage(emMessage);
                break;
            case "text":
                sendTextMessage(localPath);
                break;
            case "video":
                HTMessageVideoBody videoBody = (HTMessageVideoBody) message1.getBody();
                HTMessage emvideoMessage = HTMessage.createVideoSendMessage(chatTo, localPath, videoBody.getLocalPathThumbnail(), videoBody.getVideoDuration());
                sendMessage(emvideoMessage);
                break;
            case "voice":
                HTMessageVoiceBody voiceBody = (HTMessageVoiceBody) message1.getBody();
                HTMessage voiceMSg = HTMessage.createVoiceSendMessage(chatTo, localPath, voiceBody.getAudioDuration());
                sendMessage(voiceMSg);
                break;
            case "image":
                HTMessageImageBody imageBody = (HTMessageImageBody) message1.getBody();
                HTMessage message = HTMessage.createImageSendMessage(chatTo, localPath, imageBody.getSize());
                sendMessage(message);
                break;
            case "location":
                HTMessageLocationBody locationBody = (HTMessageLocationBody) message1.getBody();
                HTMessage locationSendMessage = HTMessage.createLocationSendMessage(chatTo, locationBody.getLatitude(), locationBody.getLongitude(), locationBody.getAddress(), localPath);
                sendMessage(locationSendMessage);
                break;
        }

    }

    private Activity getActivity(){
        return chatView.getBaseActivity();
    }
    @Override
    public void loadMoreMessages() {
        if (htMessageList == null || htMessageList.size() == 0) {
            return;
        }
        HTMessage message = htMessageList.get(0);
        if (message != null) {
            List<HTMessage> htMessages = HTClient.getInstance().messageManager().loadMoreMsgFromDB(chatTo, message.getTime(), 20);
            if (htMessages.size() == 0) {
                chatView.showNoMoreMessage();
            } else {
                Collections.reverse(htMessages);
                htMessageList.addAll(0, htMessageList);
                chatView.refreshListView();
            }
        } else {
            chatView.showNoMoreMessage();
        }
    }

    @Override
    public void onEditTextLongClick() {

    }

    @Override
    public void sendVideoMessage(String videoPath, String thumbPath, int duration) {
        HTMessage htMessage = HTMessage.createVideoSendMessage(chatTo, videoPath, thumbPath, duration);
        sendMessage(htMessage);
    }


    @Override
    public void sendTextMessage(String content) {
        HTMessage htMessage = HTMessage.createTextSendMessage(chatTo, content);
        sendMessage(htMessage);
    }

    @Override
    public HTMessage getMessageById(String msgId) {
        for (HTMessage htMessage : htMessageList) {
            if (htMessage.getMsgId().equals(msgId)) {
                return htMessage;
            }
        }
        return null;
    }

    @Override
    public void selectPicFromCamera() {

        if (!CommonUtils.isSdcardExist()) {
            Toast.makeText(getContext(), R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        cameraFile = new File(new HTPathUtils(chatTo, getContext()).getImagePath() + "/" + HTApp.getInstance().getUsername()
                + System.currentTimeMillis() + ".png");
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    @Override
    public void selectPicFromLocal() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(false)
                .setShowGif(false)
                .setPreviewEnabled(true)
                .start(getContext(), chatView.getFragment(), REQUEST_CODE_LOCAL);
    }

    @Override
    public void selectLocation() {
        startActivityForResult(new Intent(getContext(), BaiduMapActivity.class), REQUEST_CODE_MAP);

    }

    @Override
    public void selectVideo() {
        Intent intent = new Intent(getContext(), CaptureVideoActivity.class);
        HTPathUtils htPathUtils = new HTPathUtils(chatTo, getContext());
        String filePath = htPathUtils.getVideoPath() + "/" + System.currentTimeMillis() + ".mp4";
        intent.putExtra("EXTRA_DATA_FILE_NAME", filePath);
        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
    }

    @Override
    public void selectFile() {
        startActivityForResult(new Intent(getContext(), FileBrowserActivity.class), REQUEST_CODE_SELECT_FILE);
    }

    @Override
    public void selectCall() {
        HTAlertDialog dialog = new HTAlertDialog(getActivity(), null, new String[]{getContext().getString(R.string.attach_video_call), getContext().getString(R.string.attach_voice_call)});

        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        startVideoCall();
                        break;
                    case 1:
                        startVoiceCall();
                        break;
                }
            }
        });
    }

    /**
     * make a voice call
     */
    private void startVoiceCall() {
        Intent intent = new Intent(getActivity(), VoiceOutgoingActivity.class);
        intent.putExtra("mode", RTMeetKit.RTCVideoLayout.RTC_V_1X3.ordinal());
        intent.putExtra("isOutgoing", true);
        intent.putExtra("userId", chatTo);
        startActivity(intent);
    }

    /**
     * make a video call
     */
    private void startVideoCall() {
        if (chatType == MessageUtils.CHAT_GROUP){
            Intent intent = new Intent(getActivity(), PreVideoCallActivity.class);
            intent.putExtra("mode", RTMeetKit.RTCVideoLayout.RTC_V_1X3.ordinal());
            intent.putExtra("groupId", chatTo);
            intent.putExtra("isAgain", false);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getActivity(), VideoOutgoingActivity.class);
            intent.putExtra("mode", RTMeetKit.RTCVideoLayout.RTC_V_1X3.ordinal());
            intent.putExtra("isOutgoing", true);
            intent.putExtra("userId", chatTo);
            startActivity(intent);
        }
    }


    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_VIDEO: //send the video
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
                        File file = new File(new HTPathUtils(chatTo, getContext()).getVideoPath(), "th_video" + System.currentTimeMillis() + ".png");
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                            ThumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();
                            Bitmap bitmap = ImageUtils.decodeScaleImage(file.getAbsolutePath());
                            ACache.get(getContext()).put(file.getAbsolutePath(), bitmap);
                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: //send the file
                    if (data != null) {
                        String path = data.getStringExtra(FileBrowserActivity.EXTRA_DATA_PATH);
                        Uri uri = Uri.parse(path);
                        if (uri != null) {
                            sendFileByUri(uri, path);
                        }
                    }
                    break;
                case REQUEST_CODE_CAMERA:
                    if (cameraFile != null && cameraFile.exists()){
                        List<String> list=new ArrayList<>();
                        list.add(cameraFile.getAbsolutePath());
                        compressMore(list);
                    }

                    break;
                case REQUEST_CODE_LOCAL:
                    if (data != null) {
                        ArrayList<String> list = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        if (list != null) {
                            compressMore(list);
                        }
                    }
                    break;
                case REQUEST_CODE_MAP:
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    String locationAddress = data.getStringExtra("address");
                    String thumbailPath = data.getStringExtra("thumbnailPath");
                    if (locationAddress != null && !locationAddress.equals("") & new File(thumbailPath).exists()) {
                        sendLocationMessage(latitude, longitude, locationAddress, thumbailPath);
                    } else {
                        Toast.makeText(getContext(), R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void startActivity(Intent intent) {
        chatView.getFragment().startActivity(intent);
    }

    private void startActivityForResult(Intent intent, int REQUEST_CODE) {
        chatView.getFragment().startActivityForResult(intent, REQUEST_CODE);
    }

    private Context getContext() {
        return chatView.getBaseContext();
    }

    public void sendMessage(final HTMessage htMessage) {

        htMessage.setAttributes(extJSON.toJSONString());
        if (chatType == MessageUtils.CHAT_GROUP) {
            htMessage.setChatType(ChatType.groupChat);
        }
        HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {
                Message message = mainHandler.obtainMessage();
                message.what = 1000;
                message.obj = htMessage;
                mainHandler.sendMessage(message);

            }

            @Override
            public void onSuccess() {
                htMessage.setStatus(HTMessage.Status.SUCCESS);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                Message message = mainHandler.obtainMessage();
                message.what = 1001;
                message.obj = htMessage;
                mainHandler.sendMessage(message);
                Log.d("SMACK---->",htMessage.toXmppMessageBody());
            }

            @Override
            public void onFailure() {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                Message message = mainHandler.obtainMessage();
                message.what = 1002;
                message.obj = htMessage;
                mainHandler.sendMessage(message);

            }

        });


    }

    private void sendImageMessage(String imagePath) {

        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        Bitmap bitmap = rotateBitmap(bmp, readPictureDegree(imagePath));
//        BitmapFactory.Options options = new BitmapFactory.Options();
//
//        /**
//         * 最关键在此，把options.inJustDecodeBounds = true;
//         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
//         */
//        options.inJustDecodeBounds = true;
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */


        String size = bitmap.getWidth() + "," + bitmap.getHeight();
        Log.d("size---->", size);
        HTMessage htMessage = HTMessage.createImageSendMessage(chatTo, imagePath, size);
        sendMessage(htMessage);
    }


    /**
     * send file
     *
     * @param uri
     */
    private void sendFileByUri(Uri uri, String path) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = getContext().getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        } else {
            filePath = path;
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            Toast.makeText(getContext(), R.string.File_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        //limit the size < 10M
        if (file.length() > 10 * 1024 * 1024) {
            Toast.makeText(getContext(), R.string.The_file_is_not_greater_than_10_m, Toast.LENGTH_SHORT).show();
            return;
        }
        sendFileMessage(filePath, file.length());
    }
    private void sendFileMessage(String filePath, long fileSize) {
        HTMessage htMessage = HTMessage.createFileSendMessage(chatTo, filePath, fileSize);
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
    private void compressMore(final List<String> pathList) {
        final LinkedList<Runnable> taskList = new LinkedList<>();
        final ArrayList<String> newList = new ArrayList<>();//压缩后的图片路径
        final Handler handler = new Handler();
        class Task implements Runnable {
            String path;

            Task(String path) {
                this.path = path;
            }

            @Override
            public void run() {
                Luban.get(getContext())
                        .load(new File(path))                     //传人要压缩的图片
                        .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                        .setCompressListener(new OnCompressListener() { //设置回调
                            @Override
                            public void onStart() {
                                //  AppManager.I().currentActivity().showDialog("加载中...");
                            }

                            @Override
                            public void onSuccess(final File file) {
                                Message message=mainHandler.obtainMessage();
                                message.what=1003;
                                message.obj=file.getPath();
                                mainHandler.sendMessage(message);
                                newList.add(file.getPath());
                                if (!taskList.isEmpty()) {
                                    Runnable runnable = taskList.pop();
                                    handler.post(runnable);
                                } else {
                                    //完成之后的个人操作
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        }).launch();    //启动压缩
            }
        }
        //循环遍历原始路径 添加至linklist中
        for (String path : pathList) {
            taskList.add(new Task(path));
        }
        handler.post(taskList.pop());
    }


    private void sendLocationMessage(double latitude, double longitude, String locationAddress, String thumbailPath) {
        HTMessage htMessage = HTMessage.createLocationSendMessage(chatTo, latitude, longitude, locationAddress, thumbailPath);
        sendMessage(htMessage);
    }


    public void sendVoiceMessage(String filePath, int length) {

        HTMessage htMessage = HTMessage.createVoiceSendMessage(chatTo, filePath, length);
        sendMessage(htMessage);


    }






}
