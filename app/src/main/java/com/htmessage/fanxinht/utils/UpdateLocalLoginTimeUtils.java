package com.htmessage.fanxinht.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：UpdateLocalLoginTimeUtils 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/5 17:48
 * 邮箱:814326663@qq.com
 */
public class UpdateLocalLoginTimeUtils {
    private static String TAG = UpdateLocalLoginTimeUtils.class.getSimpleName();
    public static void sendLocalTimeToService(Context context){
        List<Param> params = new ArrayList<>();
        new OkHttpUtils(context).post(params, HTConstant.URL_SEND_LOCAL_LOGIN_TIME, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code){
                    case 1:
                        Log.d(TAG,"上传本地成功!");
                        break;
                    default:
                        Log.d(TAG,"上传本地失败!");
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.d(TAG,"上传本地失败!"+errorMsg);
            }
        });

    }

}
