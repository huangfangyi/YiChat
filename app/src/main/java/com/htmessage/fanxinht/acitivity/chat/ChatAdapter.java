package com.htmessage.fanxinht.acitivity.chat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.chat.location.BaiduMapActivity;
import com.htmessage.fanxinht.acitivity.chat.voice.VoicePlayClickListener;
import com.htmessage.fanxinht.acitivity.chat.weight.emojicon.SmileUtils;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.manager.ContactsManager;
import com.htmessage.fanxinht.acitivity.main.details.UserDetailsActivity;
import com.htmessage.fanxinht.acitivity.chat.group.GroupNoticeMessageUtils;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.acitivity.chat.video.ChatViewVideo;
import com.htmessage.fanxinht.utils.ACache;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.DateUtils;
import com.htmessage.fanxinht.utils.OpenFileUtils;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTMessageBody;
import com.htmessage.sdk.model.HTMessageFileBody;
import com.htmessage.sdk.model.HTMessageImageBody;
import com.htmessage.sdk.model.HTMessageLocationBody;
import com.htmessage.sdk.model.HTMessageTextBody;
import com.htmessage.sdk.model.HTMessageVoiceBody;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.fanxinht.utils.HTPathUtils;
import com.htmessage.fanxinht.utils.ImageUtils;
import com.htmessage.fanxinht.acitivity.showbigimage.ShowBigImageActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by huangfangyi on 2016/11/24.
 * qq 84543217
 */

public class ChatAdapter extends BaseAdapter {
    private List<HTMessage> msgs;
    private Activity context;
    private LayoutInflater inflater;
    private static final int MESSAGE_TEXT_RECEIVED = 0;
    private static final int MESSAGE_TEXT_SEND = 1;
    private static final int MESSAGE_IMAGE_RECEIVED = 2;
    private static final int MESSAGE_IMAGE_SEND = 3;
    private static final int MESSAGE_VOICE_RECEIVED = 4;
    private static final int MESSAGE_VOICE_SEND = 5;
    private static final int MESSAGE_FILE_RECEIVED = 6;
    private static final int MESSAGE_FILE_SEND = 7;
    private static final int MESSAGE_VEDIO_RECEIVED = 8;
    private static final int MESSAGE_VEDIO_SEND = 9;
    private static final int MESSAGE_LOCATION_RECEIVED = 10;
    private static final int MESSAGE_LOCATION_SEND = 11;
    private static final int MESSAGE_ACTIVTTY_RECEIVED = 12;
    private static final int MESSAGE_ACTIVTTY_SEND = 13;

    private String chatTo;
    private int chatType;
    private List<HTMessage> imageMsgs = new ArrayList<>();

    public ChatAdapter(List<HTMessage> msgs, Activity context, String chatTo, int chatType) {
        this.msgs = msgs;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.chatTo = chatTo;
        this.chatType = chatType;
        imageMsgs.clear();
        for (int i = 0; i < msgs.size(); i++) {
            HTMessage emMessage = msgs.get(i);
            if (emMessage.getType() == HTMessage.Type.IMAGE) {
                imageMsgs.add(emMessage);
            }
        }

    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public HTMessage getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        HTMessage message = getItem(position);
        return getItemViewType(message);

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (int i = 0; i < msgs.size(); i++) {
            HTMessage emMessage = msgs.get(i);
            if (emMessage.getType() == HTMessage.Type.IMAGE) {
                if (!imageMsgs.contains(emMessage)) {
                    imageMsgs.add(emMessage);
                }
            }
        }
    }

    public void updateList(List<HTMessage> htMessageList) {
        this.msgs = htMessageList;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 14;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HTMessage message = getItem(position);
        int viewType = getItemViewType(position);
        if (convertView == null) {
            convertView = getViewByType(viewType, parent);
        }
        ChatViewHolder holder = (ChatViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ChatViewHolder();
            handleViewAndHolder(viewType, convertView, holder, message);
        }
        handleData(holder, viewType, message, position);

        return convertView;
    }


    private int getItemViewType(HTMessage htMessage) {
        HTMessage.Type type = htMessage.getType();
        if (type == HTMessage.Type.TEXT) {
            if (htMessage.getIntAttribute("action", 0) == 3000) {
                return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_ACTIVTTY_RECEIVED : MESSAGE_ACTIVTTY_SEND;
            } else {
                return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_TEXT_RECEIVED : MESSAGE_TEXT_SEND;
            }
        } else if (type == HTMessage.Type.IMAGE) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_IMAGE_RECEIVED : MESSAGE_IMAGE_SEND;
        } else if (type == HTMessage.Type.VOICE) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_VOICE_RECEIVED : MESSAGE_VOICE_SEND;
        } else if (type == HTMessage.Type.VIDEO) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_VEDIO_RECEIVED : MESSAGE_VEDIO_SEND;
        } else if (type == HTMessage.Type.FILE) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_FILE_RECEIVED : MESSAGE_FILE_SEND;
        } else if (type == HTMessage.Type.LOCATION) {
            return htMessage.getDirect() == HTMessage.Direct.RECEIVE ? MESSAGE_LOCATION_RECEIVED : MESSAGE_LOCATION_SEND;
        }
        return 0;
    }


    private View getViewByType(int viewType, ViewGroup parent) {
        switch (viewType) {
            case MESSAGE_TEXT_RECEIVED:
                return inflater.inflate(R.layout.row_received_message, parent, false);
            case MESSAGE_IMAGE_RECEIVED:
                return inflater.inflate(R.layout.row_received_picture, parent, false);
            case MESSAGE_VOICE_RECEIVED:
                return inflater.inflate(R.layout.row_received_voice, parent, false);
            case MESSAGE_FILE_RECEIVED:
                return inflater.inflate(R.layout.row_received_file, parent, false);
            case MESSAGE_VEDIO_RECEIVED:
                return inflater.inflate(R.layout.row_received_video, parent, false);
            case MESSAGE_LOCATION_RECEIVED:
                return inflater.inflate(R.layout.row_received_location, parent, false);
            case MESSAGE_TEXT_SEND:
                return inflater.inflate(R.layout.row_sent_message, parent, false);
            case MESSAGE_IMAGE_SEND:
                return inflater.inflate(R.layout.row_sent_picture, parent, false);
            case MESSAGE_VOICE_SEND:
                return inflater.inflate(R.layout.row_sent_voice, parent, false);
            case MESSAGE_FILE_SEND:
                return inflater.inflate(R.layout.row_sent_file, parent, false);
            case MESSAGE_VEDIO_SEND:
                return inflater.inflate(R.layout.row_sent_video, parent, false);
            case MESSAGE_LOCATION_SEND:
                return inflater.inflate(R.layout.row_sent_location, parent, false);
            case MESSAGE_ACTIVTTY_SEND: //活动
                return inflater.inflate(R.layout.row_sent_activity, parent, false);
            case MESSAGE_ACTIVTTY_RECEIVED:
                return inflater.inflate(R.layout.row_received_activity, parent, false);
            default:
                return inflater.inflate(R.layout.row_sent_message, parent, false);
        }
    }


    private void handleViewAndHolder(int viewType, View convertView, ChatViewHolder holder, final HTMessage htMessage) {
        holder.reBubble = (RelativeLayout) convertView.findViewById(R.id.bubble);
        holder.reBubble.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
        holder.timeStamp = (TextView) convertView.findViewById(R.id.timestamp);
        holder.tv_ack_msg = (TextView) convertView.findViewById(R.id.tv_ack_msg);
        holder.tv_delivered = (TextView) convertView.findViewById(R.id.tv_delivered);
        if (htMessage.getDirect() == HTMessage.Direct.RECEIVE) {
            //接收消息,在群聊时可以显示群成员名称
            holder.tvNick = (TextView) convertView.findViewById(R.id.tv_userid);
        } else {
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
            holder.ivMsgStatus = (ImageView) convertView.findViewById(R.id.msg_status);
        }

        if (htMessage.getType() == HTMessage.Type.TEXT || htMessage.getType() == HTMessage.Type.LOCATION || htMessage.getType() == HTMessage.Type.FILE) {
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
        }
        if (viewType == MESSAGE_IMAGE_SEND || viewType == MESSAGE_IMAGE_RECEIVED) {
            holder.ivContent = (ImageView) convertView.findViewById(R.id.image);
        }
        if (viewType == MESSAGE_VOICE_SEND || viewType == MESSAGE_VOICE_RECEIVED) {
            holder.tvDuration = (TextView) convertView.findViewById(R.id.tv_length);
            holder.ivVoice = (ImageView) convertView.findViewById(R.id.iv_voice);
            if (viewType == MESSAGE_VOICE_RECEIVED) {
                holder.ivUnread = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
            }
        }

        if (htMessage.getType() == HTMessage.Type.VIDEO) {
            holder.tvDuration = (TextView) convertView.findViewById(R.id.chatting_length_iv);
            holder.ivContent = (ImageView) convertView.findViewById(R.id.iv_content);
        }


        if (htMessage.getType() == HTMessage.Type.LOCATION) {
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            holder.ivContent = (ImageView) convertView.findViewById(R.id.image);
        }
        if (htMessage.getType() == HTMessage.Type.TEXT) {
            holder.reMain = (RelativeLayout) convertView.findViewById(R.id.re_main);
        }
        if (viewType == MESSAGE_ACTIVTTY_SEND || viewType == MESSAGE_ACTIVTTY_RECEIVED) {
            holder.tvMonth = (TextView) convertView.findViewById(R.id.tv_month);
            holder.tvDay = (TextView) convertView.findViewById(R.id.tv_day);
            holder.tvPlace = (TextView) convertView.findViewById(R.id.tv_place);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        }
        if (htMessage.getType() == HTMessage.Type.FILE) {
            holder.tvFileSize = (TextView) convertView.findViewById(R.id.tv_file_size);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
        }
        holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = htMessage.getAttributes();
                if (jsonObject != null) {
                    context.startActivity(new Intent(context, UserDetailsActivity.class).putExtra(HTConstant.KEY_USER_INFO, jsonObject.toJSONString()));
                }
            }
        });
        convertView.setTag(holder);

    }


    private void handleData(ChatViewHolder holder, int viewType, final HTMessage message, int position) {

        if (message.getDirect() == HTMessage.Direct.SEND && message.getStatus() == HTMessage.Status.FAIL) {
            holder.ivMsgStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onResendViewClick != null) {
                        onResendViewClick.resendMessage(message);
                    }
                }
            });
        }
        HTMessageBody htMessageBody = message.getBody();
        if (htMessageBody == null) {
            return;
        }
        if (position == 0) {
            holder.timeStamp.setText(DateUtils.getTimestampString(new Date(message
                    .getTime())));
            holder.timeStamp.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            if (DateUtils.isCloseEnough(message.getTime(), getItem(position - 1).getTime())) {
                holder.timeStamp.setVisibility(View.GONE);
            } else {
                holder.timeStamp.setText(DateUtils.getTimestampString(new Date(
                        message.getTime())));
                holder.timeStamp.setVisibility(View.VISIBLE);
            }
        }

        if (chatType == MessageUtils.CHAT_GROUP && message.getDirect() == HTMessage.Direct.RECEIVE) {
            holder.tvNick.setVisibility(View.VISIBLE);

        } else if (chatType == MessageUtils.CHAT_SINGLE && message.getDirect() == HTMessage.Direct.RECEIVE) {
            holder.tvNick.setVisibility(View.GONE);
        }
        String nick = message.getStringAttribute(HTConstant.JSON_KEY_NICK);
        String avatar = message.getStringAttribute(HTConstant.JSON_KEY_AVATAR);
        if (message.getDirect() == HTMessage.Direct.SEND) {
            if (message.getFrom().equals(HTApp.getInstance().getUsername())) {
                JSONObject userJson = HTApp.getInstance().getUserJson();
                if (userJson.containsKey(HTConstant.JSON_KEY_AVATAR)) {
                    avatar = userJson.getString(HTConstant.JSON_KEY_AVATAR);
                }
            }
        }
        if (!TextUtils.isEmpty(nick)) {
            if (chatType == MessageUtils.CHAT_GROUP && message.getDirect() == HTMessage.Direct.RECEIVE) {
                holder.tvNick.setText(nick);
                Map<String, User> contactList = ContactsManager.getInstance().getContactList();
                ArrayList<User> users = new ArrayList<>();
                if (contactList.containsKey(message.getFrom())) {
                    User user = contactList.get(message.getFrom());
                    user.setAvatar(avatar);
                    users.add(user);
                    contactList.put(message.getFrom(), user);
                    ContactsManager.getInstance().saveContact(user);
                    ArrayList<User> users1 = new ArrayList<>(contactList.values());
                    ContactsManager.getInstance().saveContactList(users1);
                }
            }
        }
        Glide.with(context).load(avatar).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(holder.ivAvatar);
        HTMessage.Status status = message.getStatus();
        if (message.getDirect() == HTMessage.Direct.SEND) {
            if (status == HTMessage.Status.CREATE) {
                holder.progressBar.setVisibility(View.VISIBLE);
            } else {
                holder.progressBar.setVisibility(View.GONE);
            }
            if (status == HTMessage.Status.FAIL) {
                holder.ivMsgStatus.setVisibility(View.VISIBLE);
            } else {
                holder.ivMsgStatus.setVisibility(View.GONE);
            }
//            if (status == HTMessage.Status.SUCCESS) {
//                holder.tv_delivered.setVisibility(View.VISIBLE);
//            } else {
//                holder.tv_delivered.setVisibility(View.INVISIBLE);
//            }
            //TODO 判断是否已读 暂作处理是
//            if (status == HTMessage.Status.READ){
//                holder.tv_ack_msg.setVisibility(View.VISIBLE);
//            }else{
//                holder.tv_ack_msg.setVisibility(View.INVISIBLE);
//            }
        }
        if (message.getType() == HTMessage.Type.TEXT) {
            int action = message.getIntAttribute("action", 0);
            if (action < 2006 && action > 1999) {
                holder.timeStamp.setVisibility(View.VISIBLE);
                holder.reMain.setVisibility(View.GONE);
                holder.timeStamp.setText(GroupNoticeMessageUtils.getGroupNoticeContent(message));
                return;
            } else if (action == 3000) {

                holder.reMain.setVisibility(View.VISIBLE);
                final String content = ((HTMessageTextBody) htMessageBody).getContent();
                if (content != null) {
                    final JSONObject jsonObject = JSONObject.parseObject(content);
                    String time = jsonObject.getString("time");
                    if (time != null) {
                        try {
                            int day = Integer.parseInt(time.substring(time.length() - 2, time.length()));
                            int month = Integer.parseInt(time.substring(time.length() - 4, time.length() - 2));
                            holder.tvMonth.setText(month + "月");
                            holder.tvDay.setText(day + "");
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    if (jsonObject.containsKey("title")) {

                        holder.tvTitle.setText(jsonObject.getString("title"));
                    }
                    if (jsonObject.containsKey("place")) {
                        holder.tvPlace.setText("地点:" + jsonObject.getString("place"));
                    }
                }

            } else if (action == 6001) {
                holder.timeStamp.setVisibility(View.VISIBLE);
                holder.reMain.setVisibility(View.GONE);
                holder.timeStamp.setText(((HTMessageTextBody) htMessageBody).getContent());
            } else {
                holder.reMain.setVisibility(View.VISIBLE);
                holder.tvContent.setText(SmileUtils.getSmiledText(context, ((HTMessageTextBody) htMessageBody).getContent()),
                        TextView.BufferType.SPANNABLE);
            }

        }
        if (message.getType() == HTMessage.Type.IMAGE) {
            showImageView(message, holder, true, false);
        } else if (message.getType() == HTMessage.Type.VOICE) {
            showVoiceView(message, holder);
        } else if (message.getType() == HTMessage.Type.VIDEO) {
            showVideoView(message, holder);
        } else if (message.getType() == HTMessage.Type.LOCATION) {
            showLocationView(message, holder);

        } else if (message.getType() == HTMessage.Type.FILE) {
            showFileView(message, holder);
        }
    }

    private void showFileView(final HTMessage htMessage, ChatViewHolder holder) {
        final HTMessageFileBody htMessageFileBody = (HTMessageFileBody) htMessage.getBody();
        String size = getPrintSize(htMessageFileBody.getSize());
        final String fileName = htMessageFileBody.getFileName();
        holder.tvContent.setText(fileName);
        holder.tvFileSize.setText(size);
        final String localPath = htMessageFileBody.getLocalPath();
        final String remotePath = htMessageFileBody.getRemotePath();
        holder.reBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(localPath)) {
                    File localFile = new File(localPath);
                    if (localFile.exists()) {
                        OpenFileUtils.openFile(localFile, context);
                    } else {
                        downLoadFile(htMessageFileBody, htMessage, remotePath, fileName);
                    }
                } else {
                    downLoadFile(htMessageFileBody, htMessage, remotePath, fileName);
                }
            }
        });
    }

    /**
     * 下载文件并打开
     *
     * @param htMessageFileBody 文件体
     * @param htMessage         消息
     * @param remotePath        远程地址
     * @param fileName          文件名字
     */
    private void downLoadFile(final HTMessageFileBody htMessageFileBody, final HTMessage htMessage, String remotePath, String fileName) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.downwaiting));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        HTPathUtils htPathUtils = new HTPathUtils(chatTo, context);
        final String filePath = htPathUtils.getFilePath().getAbsolutePath() + "/" + fileName;
        Log.d("filePath11--->", filePath);
        new OkHttpUtils(context).loadFile(remotePath, filePath, new OkHttpUtils.DownloadCallBack() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                File file = new File(filePath);
                if (file.exists()) {
                    htMessageFileBody.setLocalPath(filePath);
                    htMessage.setBody(htMessageFileBody);
                    HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                    OpenFileUtils.openFile(file, context);
                }
            }

            @Override
            public void onFailure(String message) {
                progressDialog.dismiss();
                Toast.makeText(context, context.getString(R.string.Failed_to_download_file) + message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static String getPrintSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + " B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + " KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + " MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + " GB";
        }
    }

    private void showImageView(final HTMessage htMessage, ChatViewHolder holder, boolean isReSize, boolean isLocMsg) {
        if (!isReSize) {
            holder.ivContent.setImageResource(R.drawable.location2);
        } else {
            holder.ivContent.setImageResource(R.drawable.default_image);
        }
        String localPath = null;
        if (!isLocMsg) {
            HTMessageImageBody htMessageImageBody = (HTMessageImageBody) htMessage.getBody();
            localPath = htMessageImageBody.getLocalPath();
        } else {
            HTMessageLocationBody htMessageLocationBody = (HTMessageLocationBody) htMessage.getBody();
            localPath = htMessageLocationBody.getLocalPath();
        }


        if (!TextUtils.isEmpty(localPath)) {
            Bitmap bitmap = ACache.get(context.getApplicationContext()).getAsBitmap(htMessage.getMsgId());
            if (bitmap == null) {
                Log.d("bitmap3---->", "null");
                if (new File(localPath).exists()) {
                    bitmap = ImageUtils.decodeScaleImage(localPath);
                    if (bitmap != null) {
                        ACache.get(context.getApplicationContext()).put(htMessage.getMsgId(), bitmap);
                        holder.ivContent.setImageBitmap(bitmap);
                    }
                } else {
                    downLoadImageFromServer(htMessage, holder.ivContent, isReSize, isLocMsg);
                }

            } else {
                holder.ivContent.setImageBitmap(bitmap);
            }
        } else {
            downLoadImageFromServer(htMessage, holder.ivContent, isReSize, isLocMsg);
        }
        final String finalLocalPath = localPath;
        holder.reBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalLocalPath == null || !new File(finalLocalPath).exists()) {
                    downLoadBigImageAndOpen(htMessage);
                } else {
                    context.startActivity(new Intent(context, ShowBigImageActivity.class).putExtra("localPath", finalLocalPath));
                }
            }
        });
    }


    private void downLoadImageFromServer(final HTMessage htMessage, final ImageView imageView, boolean isReSize, final boolean isLocMsg) {
        //下载缩略图,显示并且保存至缓存.
        String remotePath = null;
        String fileName = null;
        if (!isLocMsg) {
            HTMessageImageBody htMessageImageBody = (HTMessageImageBody) htMessage.getBody();
            remotePath = htMessageImageBody.getRemotePath();
            fileName = htMessageImageBody.getFileName();
        } else {
            HTMessageLocationBody htMessageLocationBody = (HTMessageLocationBody) htMessage.getBody();
            remotePath = htMessageLocationBody.getRemotePath();
            fileName = htMessageLocationBody.getFileName();
        }
        if (!TextUtils.isEmpty(remotePath)) {
            if (isReSize) {
                remotePath = remotePath + HTConstant.reSize;
            }
            HTPathUtils htPathUtils = new HTPathUtils(chatTo, context);
            final String filePath = htPathUtils.getImagePath().getAbsolutePath() + "/mini" + fileName;
            Log.d("filePath11--->", filePath);
            new OkHttpUtils(context).loadFile(remotePath, filePath, new OkHttpUtils.DownloadCallBack() {
                @Override
                public void onSuccess() {
                    if (new File(filePath).exists()) {

                        final Bitmap bitmap = ImageUtils.decodeScaleImage(filePath);
                        if (!isLocMsg) {

                            HTMessageImageBody htMessageImageBody = (HTMessageImageBody) htMessage.getBody();
                            htMessageImageBody.setLocalPath(filePath);
                            htMessage.setBody(htMessageImageBody);
                        } else {
                            HTMessageLocationBody htMessageLocationBody = (HTMessageLocationBody) htMessage.getBody();
                            htMessageLocationBody.setLocalPath(filePath);
                            htMessage.setBody(htMessageLocationBody);
                        }
                        HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                        ACache.get(context.getApplicationContext()).put(htMessage.getMsgId(), bitmap);

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(String message) {

                }
            });


        }

    }

    private void downLoadBigImageAndOpen(final HTMessage htMessage) {
        final HTMessageImageBody htMessageImageBody = (HTMessageImageBody) htMessage.getBody();

        String remotePath = htMessageImageBody.getRemotePath();
        String fileName = htMessageImageBody.getFileName();
        if (TextUtils.isEmpty(remotePath) || TextUtils.isEmpty(fileName)) {
            return;
        }
        final Dialog progressDialog = HTApp.getInstance().createLoadingDialog(context, context.getString(R.string.loading));
        progressDialog.show();
        HTPathUtils pathUtils = new HTPathUtils(chatTo, context);
        final String filePath = pathUtils.getImagePath().getAbsolutePath() + "/" + fileName;
        new OkHttpUtils(context).loadFile(remotePath, filePath, new OkHttpUtils.DownloadCallBack() {
            @Override
            public void onSuccess() {

                (context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
                if (new File(filePath).exists()) {
                    final Bitmap bitmap = ImageUtils.decodeScaleImage(filePath);
                    ACache.get(context.getApplicationContext()).put(htMessage.getMsgId(), bitmap);
                    htMessageImageBody.setLocalPath(filePath);
                    htMessage.setBody(htMessageImageBody);
                    (context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                    HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                    context.startActivity(new Intent(context, ShowBigImageActivity.class).putExtra("localPath", filePath));
                }
            }

            @Override
            public void onFailure(String message) {
                (context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }


    private void showVoiceView(final HTMessage htMessage, final ChatViewHolder holder) {
        HTMessageVoiceBody htMessageVoiceBody = (HTMessageVoiceBody) htMessage.getBody();

        if (htMessageVoiceBody == null) {
            return;
        }
        int len = htMessageVoiceBody.getAudioDuration();

        if (len > 0) {
            holder.tvDuration.setText(len + "\"");
            holder.tvDuration.setVisibility(View.VISIBLE);
        } else {
            holder.tvDuration.setVisibility(View.INVISIBLE);
        }
        if (VoicePlayClickListener.playMsgId != null
                && VoicePlayClickListener.playMsgId.equals(htMessage.getMsgId()) && VoicePlayClickListener.isPlaying) {
            AnimationDrawable voiceAnimation;
            if (htMessage.getDirect() == HTMessage.Direct.RECEIVE) {
                holder.ivVoice.setImageResource(R.anim.voice_from_icon);
            } else {
                holder.ivVoice.setImageResource(R.anim.voice_to_icon);
            }
            voiceAnimation = (AnimationDrawable) holder.ivVoice.getDrawable();
            voiceAnimation.start();
        } else {
            if (htMessage.getDirect() == HTMessage.Direct.RECEIVE) {
                holder.ivVoice.setImageResource(R.drawable.ad1);
            } else {
                holder.ivVoice.setImageResource(R.drawable.adj);
            }
        }

        if (htMessage.getDirect() == HTMessage.Direct.RECEIVE) {
            if (htMessage.getStatus() == HTMessage.Status.SUCCESS || htMessage.getStatus() == HTMessage.Status.ACKED) {
                holder.ivUnread.setVisibility(View.INVISIBLE);
            } else {
                holder.ivUnread.setVisibility(View.VISIBLE);
            }
        }

        holder.reBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VoicePlayClickListener(htMessage, chatTo, holder.ivVoice, holder.ivUnread, ChatAdapter.this, (context)).onClick(holder.reBubble);
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
            holder.reBubble.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if (VoicePlayClickListener.currentPlayListener != null && VoicePlayClickListener.isPlaying) {
                        VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                    }
                }
            });
        }
    }


    private void showVideoView(HTMessage message, ChatViewHolder holder) {
        ChatViewVideo chatViewVideo = new ChatViewVideo(message, chatTo, context, holder, ChatAdapter.this);
        chatViewVideo.initView();
    }


    public static class ChatViewHolder {

        public RelativeLayout reMain;
        public RelativeLayout reBubble;

        public ImageView ivAvatar;
        public TextView tvNick;
        public TextView timeStamp;
        public ImageView ivMsgStatus;
        //文本消息,位置消息,文件消息
        public TextView tvContent;
        //图片消息,视频消息,位置消息,文件消息
        public ImageView ivContent;
        //语音消息
        public TextView tvDuration;
        public ImageView ivUnread;
        public ImageView ivVoice;
        //活动消息
        public TextView tvMonth;
        public TextView tvDay;
        public TextView tvTitle;
        public TextView tvPlace;
        //发送消息
        public ProgressBar progressBar;

        //文件消息
        public TextView tvFileSize;
        //消息状态
        public TextView tv_ack_msg;
        //送达显示
        public TextView tv_delivered;
    }

    private void showLocationView(final HTMessage message, ChatViewHolder holder) {
        final HTMessageLocationBody htMessageLocationBody = (HTMessageLocationBody) message.getBody();
        holder.tvContent.setText(htMessageLocationBody.getAddress());
        showImageView(message, holder, false, true);
        holder.reBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, BaiduMapActivity.class);
                intent.putExtra("latitude", htMessageLocationBody.getLatitude());
                intent.putExtra("longitude", htMessageLocationBody.getLongitude());
                intent.putExtra("address", htMessageLocationBody.getAddress());
                context.startActivity(intent);
            }
        });
    }

    private OnResendViewClick onResendViewClick;

    public void setOnResendViewClick(OnResendViewClick onResendViewClick) {
        this.onResendViewClick = onResendViewClick;
    }

    interface OnResendViewClick {
        void resendMessage(HTMessage htMessage);
    }

}




 
