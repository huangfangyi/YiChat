package com.htmessage.yichat.acitivity.chat;

import android.graphics.Bitmap;

import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.manager.MmvkManger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2019/8/17.
 * qq 84543217
 */
public class ChatFileManager {


    private static ChatFileManager chatFileManager;
    private static List<HTMessage> htMessageList = new ArrayList<>();

    public static ChatFileManager get() {
        if (chatFileManager == null) {
            chatFileManager = new ChatFileManager();

        }
        return chatFileManager;

    }

    public List<HTMessage> getImageOrVideoMessage() {

        return htMessageList;

    }

    public void addImageOrVideoMessage(HTMessage htMessage) {
        if(!htMessageList.contains(htMessage)){
            htMessageList.add(htMessage);
        }
    }

    public void removeImageOrVideoMessage(HTMessage htMessage) {
        htMessageList.remove(htMessage);
    }

    public void clearImageOrVideoMessage() {
        htMessageList.clear();
    }

    public void setLocalPath(String msgId, String localPath, HTMessage.Type type) {
        if (localPath != null && new File(localPath).exists()) {
            if (type == HTMessage.Type.VIDEO) {
                MmvkManger.getIntance().putString(msgId + "_video", localPath);

            } else if (type == HTMessage.Type.IMAGE) {

                MmvkManger.getIntance().putString(msgId + "_image", localPath);

            } else if (type == HTMessage.Type.VOICE) {

                MmvkManger.getIntance().putString(msgId + "_voice", localPath);

            }
        }
    }

    public String getLocalPath(String msgId, HTMessage.Type type) {
        if (type == HTMessage.Type.VIDEO) {
            return MmvkManger.getIntance().getAsString(msgId + "_video");

        } else if (type == HTMessage.Type.IMAGE) {

            return MmvkManger.getIntance().getAsString(msgId + "_image");

        } else if (type == HTMessage.Type.VOICE) {

            return MmvkManger.getIntance().getAsString(msgId + "_voice");

        }


        return null;
    }

    public void setMsgImageBitmap(String msgId, Bitmap bitmap) {

        MmvkManger.getIntance().putBtimap(msgId + "_bitmap", bitmap);

    }

    public Bitmap getMsgImageBitmap(String msgId) {
        return MmvkManger.getIntance().getBitmap(msgId + "_bitmap");
    }


}
