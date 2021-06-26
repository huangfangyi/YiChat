package com.htmessage.update.data;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.manager.MmvkManger;



/**
 * Created by huangfangyi on 2019/7/26.
 * qq 84543217
 */
public class SettingsManager {

    private static SettingsManager systemDataManager;

    public static SettingsManager getInstance() {
        if (systemDataManager == null) {
            systemDataManager = new SettingsManager();

        }
        return systemDataManager;

    }

    public void setShareJSON(JSONObject json){
        MmvkManger.getIntance().putJSON("share_JSON",json);
    }
    public JSONObject getShareJSON(){

        return MmvkManger.getIntance().getJSON("share_JSON");
    }

    public void savaDeviceId(String deviceId){
        MmvkManger.getIntance().putString("deviceId",deviceId);
    }

    public String getDeviceId(){
     return    MmvkManger.getIntance().getAsString("deviceId");
    }
    public void savaKeyboardHeight(int keyboardHeight){
        MmvkManger.getIntance().putInt("keyboardHeight",keyboardHeight);
    }
    public int getKeyboardHeight2(){
       return MmvkManger.getIntance().getInt("keyboardHeight2",0);
    }
    public int getKeyboardHeight(){
        return MmvkManger.getIntance().getInt("keyboardHeight",0);
    }
    public void savaKeyboardHeight2(int keyboardHeight){
        MmvkManger.getIntance().putInt("keyboardHeight2",keyboardHeight);
    }

    public boolean getSettingMsgNotification(){
        return MmvkManger.getIntance().getBoolean("MsgNotification",true);
    }

    public void setSettingMsgNotification(boolean isNotify){
        MmvkManger.getIntance().putBoolean("MsgNotification",isNotify);
    }

    public boolean getSettingMsgSound(){
        return MmvkManger.getIntance().getBoolean("MsgSound",true);

    }

    public boolean getSettingMsgVibrate(){
        return MmvkManger.getIntance().getBoolean("MsgVibrate",true);

    }

    public void setSettingMsgSound(boolean isNotify){
        MmvkManger.getIntance().putBoolean("MsgSound",isNotify);
    }

    public void setSettingMsgVibrate(boolean isNotify){
        MmvkManger.getIntance().putBoolean("MsgVibrate",isNotify);
    }
    public void setContactChangeUnread(boolean isUnread){
        MmvkManger.getIntance().putBoolean(UserManager.get().getMyUserId()+"_unreadContact",isUnread);
    }

    public boolean getContactChangeUnread(){
        return MmvkManger.getIntance().getBoolean(UserManager.get().getMyUserId()+"_unreadContact",false);
    }
    //设置某群或者某用户信息免打扰
    public void setNotifyGroupOrUser(String groupIdorUserId,boolean isNotify){
        MmvkManger.getIntance().putBoolean(UserManager.get().getMyUserId()+groupIdorUserId+"_isNotify",isNotify);

    }
    //查询某人或者某用户是否被设置了免打扰
    public boolean getNotifyGroupOrUser(String groupIdorUserId){
      return   MmvkManger.getIntance().getBoolean(UserManager.get().getMyUserId()+groupIdorUserId+"_isNotify",true);
    }


    public void setVersoinStatus(JSONObject jsonObject){
        MmvkManger.getIntance().putJSON("VersoinStatus",jsonObject);
    }

    public JSONObject getVersionStatus(){
      return   MmvkManger.getIntance().getJSON("VersoinStatus");
    }
    //查询语音消息播放状态
    public boolean getSettingMsgSpeaker(){
        return   MmvkManger.getIntance().getBoolean("_isSpeaker",true);
    }

    public void setSettingMsgSpeaker(boolean isSpeaker){
        MmvkManger.getIntance().putBoolean("_isSpeaker",isSpeaker);
    }
    public void setCreateGroupAuthStatus(boolean status){
        MmvkManger.getIntance().putBoolean(UserManager.get().getMyUserId()+"_setCreateGroupAuthStatus",status);
    }

    public boolean getCreateGroupAuthStatus(){
        return  MmvkManger.getIntance().getBoolean(UserManager.get().getMyUserId()+"_setCreateGroupAuthStatus",false);
    }

}
