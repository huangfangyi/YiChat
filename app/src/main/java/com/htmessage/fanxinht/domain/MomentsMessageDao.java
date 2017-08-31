package com.htmessage.fanxinht.domain;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.manager.DBManager;

import java.util.List;


/**
 * Created by huangfangyi on 2017/7/23.
 * qq 84543217
 */

public class MomentsMessageDao {

    public static final String TABLE_NAME = "moments_notice";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_USERID = "userid";
    public static final String COLUMN_NAME_USERNICK = "usernick";
    public static final String COLUMN_NAME_AVATAR = "avatar";
    public static final String COLUMN_NAME_TIME= "time";
    public static final String COLUMN_NAME_CONTENT = "content";
    public static final String COLUMN_NAME_IMAGEURL = "image_url";
    public static final String COLUMN_NAME_TYPE = "type";
    public static final String COLUMN_NAME_MOMENTS_ID= "moments_id";
    public static final String COLUMN_NAME_STATUS= "status";


    public MomentsMessageDao(Context context){

    }

    public void sendMomentsCmd(String imageUrl,String momentsId,String content,int type,String sendTo){
        if(sendTo.equals(HTApp.getInstance().getUsername())){
            return;

        }
        JSONObject data=new JSONObject();
        data.put("userId", HTApp.getInstance().getUsername());
        data.put("nickname",HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_NICK));
        data.put("avatar",HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_AVATAR));
        data.put("imageUrl",imageUrl);
        if(content==null){
            content="not a comment";
        }
        data.put("content",content);
        data.put("type",type);
        data.put("mid",momentsId);

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("data",data);
        jsonObject.put("action",7000);

        CmdMessage cmdMessage=new CmdMessage();
        cmdMessage.setTo(sendTo);
        cmdMessage.setBody(jsonObject.toString());
        HTClient.getInstance().chatManager().sendCmdMessage(cmdMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess() {
                Log.d("sendMomentsCmd----->","onSuccess");

            }

            @Override
            public void onFailure() {
                Log.d("sendMomentsCmd----->","onFailure");
            }
        });
     }


    public void savaMomentsMessage(MomentsMessage momentsMessage){
        DBManager.getInstance().saveMomentsNotice(momentsMessage);
    }


    public int getUnreadMoments(){

        return  DBManager.getInstance().getMomentsUnReadCount();
    }

    public MomentsMessage getLastMomentsMessage(){

        return  DBManager.getInstance().getLastMomentsMessage();
    }


    public List<MomentsMessage>  getMomentsMessageList(){

        return  DBManager.getInstance().getMomentsMessageList();
    }

    public void clearMomentsUnread(){

        DBManager.getInstance().clearMomentsUnReadCount();
    }

    public void deleteAllMomentsMessage(){

        DBManager.getInstance().deleteAllMomentsMessage();
    }
}
