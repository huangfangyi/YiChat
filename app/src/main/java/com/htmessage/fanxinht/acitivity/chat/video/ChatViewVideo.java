package com.htmessage.fanxinht.acitivity.chat.video;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.chat.ChatAdapter;
import com.htmessage.fanxinht.utils.ACache;
import com.htmessage.fanxinht.utils.DateUtils;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTMessageVideoBody;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.fanxinht.utils.HTMessageUtils;
import com.htmessage.fanxinht.utils.ImageUtils;

import java.io.File;


/**
 * Created by huangfangyi on 2016/12/6.
 * qq 84543217
 */

public class ChatViewVideo {
    private HTMessage htMessage;
    private String chatTo;
    private Activity activity;
    private ChatAdapter.ChatViewHolder holder;

    public ChatViewVideo(HTMessage htMessage, String chatTo, Activity activity, ChatAdapter.ChatViewHolder holder, BaseAdapter adapter) {
        this.htMessage = htMessage;
        this.chatTo = chatTo;
        this.activity = activity;
        this.holder = holder;
    }

    public void initView() {
        final HTMessageVideoBody htMessageVideoBody = (HTMessageVideoBody) htMessage.getBody();
        int duration = htMessageVideoBody.getVideoDuration();

        if (duration > 0) {
            String time = DateUtils.toTime(duration);
            holder.tvDuration.setText(time);
        }

        holder.ivContent.setImageResource(R.drawable.default_image);
        String localThumbnailPath=htMessageVideoBody.getLocalPathThumbnail();
        if (localThumbnailPath!=null) {
             //先从本地缓存中寻找缓存
            Bitmap bitmap = ACache.get(activity).getAsBitmap(localThumbnailPath);
            if (bitmap == null) {
                //看看本地缩略图是否存在
                File file = new File(localThumbnailPath);
                if (file.exists()) {
                    bitmap = ImageUtils.decodeScaleImage(file.getAbsolutePath());
                    if (bitmap != null) {
                        holder.ivContent.setImageBitmap(bitmap);
                    } else {
                        Log.d("ChatViewVideo--->", "get thumbnailImage From localPath  faile");
                    }

                } else {
                    //此时尝试依赖网络
                    loadThumbnailFromServer();
                }

            } else {
                holder.ivContent.setImageBitmap(bitmap);
            }
        } else {

            loadThumbnailFromServer();

        }
        final String localPath = htMessageVideoBody.getLocalPath();


        holder.reBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localPath != null) {

                    Intent intent = new Intent(activity, VideoPlayActivity.class);
                    intent.putExtra(VideoPlayActivity.VIDEO_NAME, htMessageVideoBody.getFileName());
                    intent.putExtra(VideoPlayActivity.VIDEO_PATH, localPath);
                    activity.startActivity(intent);
                } else {
                    loadVideoFromServer();
                }
            }
        });

    }


    private void loadThumbnailFromServer() {

        HTMessageUtils.loadMessageFile(htMessage,true, chatTo, activity, new HTMessageUtils.CallBack() {
            @Override
            public void error() {

            }

            @Override
            public void completed(String localPath) {
                String localThumbnailPath = localPath;
                HTMessageVideoBody htMessageVideoBody= (HTMessageVideoBody) htMessage.getBody();
                htMessageVideoBody.setLocalPathThumbnail(localThumbnailPath);
                htMessage.setBody(htMessageVideoBody);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                Bitmap bitmap = ImageUtils.decodeScaleImage(localThumbnailPath);
                if (bitmap != null) {
                    ACache.get(activity).put(localThumbnailPath, bitmap);
                    holder.ivContent.setImageBitmap(bitmap);
                }
            }
        });
    }


    private void loadVideoFromServer() {
        HTMessageUtils.loadMessageFile(htMessage,false, chatTo, activity, new HTMessageUtils.CallBack() {
            @Override
            public void error() {

            }

            @Override
            public void completed(String localPath) {

                if (localPath != null) {
                    HTMessageVideoBody htMessageVideoBody= (HTMessageVideoBody) htMessage.getBody();
                    htMessageVideoBody.setLocalPath(localPath);
                    htMessage.setBody(htMessageVideoBody);
                    HTClient.getInstance().messageManager().updateSuccess(htMessage);
                    Intent intent = new Intent(activity, VideoPlayActivity.class);
                    intent.putExtra(VideoPlayActivity.VIDEO_NAME, htMessageVideoBody.getFileName());
                    intent.putExtra(VideoPlayActivity.VIDEO_PATH, localPath);
                    activity.startActivity(intent);
                }
            }
        });
    }
}








