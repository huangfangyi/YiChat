package com.htmessage.update.data;


import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.update.uitls.WalletUtils;
import com.htmessage.yichat.R;
import com.htmessage.sdk.manager.MmvkManger;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by huangfangyi on 2019/7/24.
 * qq 84543217
 */
public class UserManager {

    private static UserManager loginUserManager;

    public static UserManager get() {
        if (loginUserManager == null) {
            loginUserManager = new UserManager();

        }
        return loginUserManager;

    }

    public void setUserRemark(String userId,String remark){
        if (remark != null) {
            MmvkManger.getIntance().putString(userId + "_remark", remark);
        }

    }

    public void saveMyUser(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        MmvkManger.getIntance().putJSON("KEY_LOGIN_USER_ALL", jsonObject);
        MmvkManger.getIntance().putString("KEY_LOGIN_USER_USERID", jsonObject.getString("userId"));
        MmvkManger.getIntance().putString("KEY_LOGIN_USER_NICK", jsonObject.getString("nick"));
        MmvkManger.getIntance().putString("KEY_LOGIN_USER_AVATAR", jsonObject.getString("avatar"));
        MmvkManger.getIntance().putString("KEY_LOGIN_USER_TOKEN", jsonObject.getString("token"));
        int payPasswordStatus=jsonObject.getInteger("payPasswordStatus");
        if(payPasswordStatus==1){
            WalletUtils.getInstance().setPayPassword(true);
        }

    }

    public JSONObject getMyUser() {
        return MmvkManger.getIntance().getJSON("KEY_LOGIN_USER_ALL");
    }


    public String getMyUserId() {
        return MmvkManger.getIntance().getAsString("KEY_LOGIN_USER_USERID");
    }

    public String getMyNick() {
        return MmvkManger.getIntance().getAsString("KEY_LOGIN_USER_NICK");
    }

    public String getMyAvatar() {
        return MmvkManger.getIntance().getAsString("KEY_LOGIN_USER_AVATAR");
    }

    public String getToken() {
        return MmvkManger.getIntance().getAsString("KEY_LOGIN_USER_TOKEN");
    }

    public String getUserNick(String userId) {


        //先读本地
        String nick = MmvkManger.getIntance().getAsString(userId + "_remark");
        if (TextUtils.isEmpty(nick)) {

            return getUserRealNick(userId);
        }
        return nick;
    }


    public String getUserRealNick(String userId){
        //先读本地
        String nick = MmvkManger.getIntance().getAsString(userId + "_nick");
        if (TextUtils.isEmpty(nick)) {
            getUserInfoFromServer(userId);
            return userId;
        }
        return nick;
    }

    public String getUserAvatar(String userId) {
        //先读本地
        String avatar = MmvkManger.getIntance().getAsString(userId + "_avatar");
        if (TextUtils.isEmpty(avatar)) {

            getUserInfoFromServer(userId);
            return "";
        }
        return avatar;
    }
    public String getUserRemark(String userId) {

        return  MmvkManger.getIntance().getAsString(userId + "_remark");
    }



    private void getUserInfoFromServer(String userId) {
        JSONObject data = new JSONObject();
        data.put("userId", userId);
        ApiUtis.getInstance().postJSON(data, Constant.URL_USER_INFO, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                if ("0".equals(jsonObject.getString("code"))) {
                    String userId = jsonObject.getString("userId");
                    String nick = jsonObject.getString("nick");
                    String remark = jsonObject.getString("remark");
                    String avatar = jsonObject.getString("avatar");
                    //项目中如果备注存在，则直接显示用户的备注
                    if (!TextUtils.isEmpty(remark)) {
                        nick = remark;
                    }
                    //存本地
                    MmvkManger.getIntance().putString(userId + "_nick", nick);
                    MmvkManger.getIntance().putString(userId + "_avatar", avatar);
                    MmvkManger.getIntance().putString(userId + "_remark", remark);
//                    //发通知告知该用户资料已取得，在需要地方UI处更新头像昵称
//                    UserInfoEvent event = new UserInfoEvent();
//                    event.setUserId(userId);
//                    event.setNick(nick);
//                    event.setAvatar(avatar);
//                    EventBus.getDefault().postSticky(event);
                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });

    }

    /**
     * 加载默认头像
     *
     * @param context
     * @param
     * @param imageView
     */
    public void loadUserAvatar(Context context, final String avatarUrl, final ImageView imageView) {
         if (context == null) {
             imageView.setImageResource(R.drawable.default_avatar);
            return;
        }

        if (TextUtils.isEmpty(avatarUrl)) {
            imageView.setImageResource(R.drawable.default_avatar);
            return;

        }

        Glide.with(context).load(avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context
     * @param
     * @param imageView
     */
    public void loadImage(Context context, final String avatarUrl, final ImageView imageView,int defaulResId) {
        if (context == null) {
            return;
        }

        if (TextUtils.isEmpty(avatarUrl)) {
            imageView.setImageResource(defaulResId);
            return;

        }

        Glide.with(context).load(avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaulResId).into(imageView);
    }


    public void saveUserInfo(JSONObject userJson) {
        String userId = userJson.getString("userId");
        String nick = userJson.getString("nick");
        String remark = userJson.getString("remark");
        String avatar = userJson.getString("avatar");

        MmvkManger.getIntance().putString(userId + "_nick", nick);
        MmvkManger.getIntance().putString(userId + "_avatar", avatar);
        if (remark != null) {
            MmvkManger.getIntance().putString(userId + "_remark", remark);
        }
        MmvkManger.getIntance().putString(userId + "_userInfo", userJson.toJSONString());

    }
    public JSONArray getMyFrindsJsonArray(){
        JSONArray jsonArray=MmvkManger.getIntance().getJSONArray(getMyUserId() + "_MyFrinds");
        if(jsonArray==null){
            jsonArray=new JSONArray();
        }
        return jsonArray;
    }

    public void saveMyFrindsJsonArray(JSONArray jsonArray){
        MmvkManger.getIntance().putJSONArray(getMyUserId() + "_MyFrinds",jsonArray);
    }

    public void saveUserNickAvatar(String userId, String nick, String avatar) {
        MmvkManger.getIntance().putString(userId + "_nick", nick);
        MmvkManger.getIntance().putString(userId + "_avatar", avatar);
    }

    public void saveFriends(Set<String> friends) {

        MmvkManger.getIntance().putStringSet(getMyUserId() + "_friends", friends);

    }


    public  void addMyFriends(String userId){
        Set<String> stringSet=getFriends();
        if(stringSet==null){
            stringSet=new HashSet<>();


        }
        stringSet.add(userId);
        saveFriends(stringSet);

        JSONArray jsonArray=getMyFrindsJsonArray();
        if(jsonArray.toJSONString().contains(userId)){
            return;
        }
        if(jsonArray==null){
           jsonArray=new JSONArray();
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("userId",userId);
        jsonObject.put("nick",UserManager.get().getUserNick(userId));
        jsonObject.put("avatar",UserManager.get().getUserAvatar(userId));
        jsonArray.add(jsonObject);
        saveMyFrindsJsonArray(jsonArray);





    }


    public  void  deleteMyFriends(String userId){
        Set<String> stringSet=getFriends();
        if(stringSet==null){
           return;
        }
        stringSet.remove(userId);
        saveFriends(stringSet);

        JSONArray jsonArray=getMyFrindsJsonArray();
        if(jsonArray==null){
           return;
        }
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            if(userId.equals(jsonObject.getString("userId"))){

                jsonArray.remove(i);
                break;

            }
        }

        saveMyFrindsJsonArray(jsonArray);
    }


    public Set<String> getFriends() {
        Set<String> friends = MmvkManger.getIntance().getStringSet(getMyUserId() + "_friends");

        return friends != null ? friends : new HashSet<String>();
    }


//
//    public List<User> getFriendUsers(){
//        List<User> users=new ArrayList<>();
//
//    }

    public void clearMyData() {
        MmvkManger.getIntance().remove("KEY_LOGIN_USER_USERID");
        MmvkManger.getIntance().remove("KEY_LOGIN_USER_ALL");
        MmvkManger.getIntance().remove("KEY_LOGIN_USER_NICK");
        MmvkManger.getIntance().remove("KEY_LOGIN_USER_AVATAR");
        MmvkManger.getIntance().remove("KEY_LOGIN_USER_TOKEN");
        MmvkManger.getIntance().remove("payPasswordStatus");
    }


}
