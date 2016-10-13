package com.fanxin.huangfangyi.main.utils;

import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.easeui.domain.EaseUser;
import com.fanxin.easeui.utils.EaseCommonUtils;

/**
 * Created by huangfangyi on 2016/7/5.\
 * QQ:84543217
 */
public class JSONUtil {

    public static EaseUser Json2User(JSONObject userJson) {
        EaseUser easeUser = new EaseUser(userJson.getString(FXConstant.JSON_KEY_HXID));
        easeUser.setNick(userJson.getString(FXConstant.JSON_KEY_NICK));
        easeUser.setAvatar(userJson.getString(FXConstant.JSON_KEY_AVATAR));
        easeUser.setUserInfo(userJson.toJSONString());
        EaseCommonUtils.setUserInitialLetter(easeUser);
        return easeUser;
    }

    public static JSONObject User2Json(EaseUser user) {
        JSONObject jsonObject = new JSONObject();
        String userInfo = user.getUserInfo();
        try {
            if (userInfo != null) {

                jsonObject = JSONObject.parseObject(userInfo);
            }
        } catch (JSONException e) {

              Log.d("JSONUtil----->>","User2Json error");
        }

        return jsonObject;

    }


}

