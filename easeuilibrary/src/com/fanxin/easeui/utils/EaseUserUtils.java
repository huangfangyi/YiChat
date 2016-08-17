package com.fanxin.easeui.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanxin.easeui.EaseConstant;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.fanxin.easeui.controller.EaseUI;
import com.fanxin.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.fanxin.easeui.domain.EaseUser;
import com.hyphenate.exceptions.HyphenateException;

public class EaseUserUtils {

    static EaseUserProfileProvider userProvider;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);

        return null;
    }

    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	EaseUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            String avatarUrl=user.getAvatar();
            if(!avatarUrl.contains("http:")){
                avatarUrl= EaseConstant.URL_AVATAR+avatarUrl;

            }
            try {
                int avatarResId = Integer.parseInt(avatarUrl);
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(imageView);
            }
        }else{

            Glide.with(context).load(R.drawable.fx_default_useravatar).into(imageView);
        }
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getNick() != null){
        		textView.setText(user.getNick());
        	}else{
        		textView.setText(username);
        	}
        }
    }

    /**
     * set user avatar
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView, EMMessage msg) {
        EaseUser user = getUserInfo(username);
        if (user != null && user.getAvatar() != null) {
            String avatarUrl = user.getAvatar();
            if (!avatarUrl.contains("http:")) {
                avatarUrl = EaseConstant.URL_AVATAR + avatarUrl;

            }
            try {
                int avatarResId = Integer.parseInt(avatarUrl);
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(imageView);
            }
        } else {
            try {
                String userInfo = msg.getStringAttribute("userInfo");
                JSONObject jsonObject = JSONObject.parseObject(userInfo);
                String avatarUrl = jsonObject.getString("avatar");
                if (!avatarUrl.contains("http:")) {
                    avatarUrl = EaseConstant.URL_AVATAR + avatarUrl;
                }
                Glide.with(context).load(avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(imageView);
            } catch (HyphenateException e) {
                Glide.with(context).load(R.drawable.fx_default_useravatar).into(imageView);
                e.printStackTrace();
            } catch (JSONException e) {

                Glide.with(context).load(R.drawable.fx_default_useravatar).into(imageView);
                e.printStackTrace();
            }


        }
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username, TextView textView, EMMessage msg) {
        if (textView != null) {
            EaseUser user = getUserInfo(username);
                try {
                    String userInfo = msg.getStringAttribute("userInfo");
                    JSONObject jsonObject = JSONObject.parseObject(userInfo);
                    String nick = jsonObject.getString("nick");
                    textView.setText(nick);
                } catch (HyphenateException e) {
                    textView.setText(username);
                    if (user != null && user.getNick() != null) {
                        textView.setText(user.getNick());
                    }
                    e.printStackTrace();
                } catch (JSONException e) {

                    textView.setText(username);
                    if (user != null && user.getNick() != null) {
                        textView.setText(user.getNick());
                    }
                    e.printStackTrace();
                }


        }
    }

}
