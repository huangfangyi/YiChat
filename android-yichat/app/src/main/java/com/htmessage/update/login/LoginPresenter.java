package com.htmessage.update.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.Param;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;
import com.htmessage.update.data.SettingsManager;
import com.htmessage.update.data.UserManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by huangfangyi on 2017/6/21.
 * qq 84543217
 */

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View loginView;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (loginView == null) {
                return;
            }
            switch (msg.what) {
                case 1000:
                    //业务服务器登录成功，取回资料，下一步登录IM SDK
                    JSONObject userJson = (JSONObject) msg.obj;
                    loginIm(userJson);
                    break;
                case 1001:
                    //登录IM服务器成功，登录工作完成
                    loginView.cancelDialog();
                    loginView.showToast(R.string.login_success);
                    loginView.onLoginSuccessed();
                    break;
                case 1002:
                     loginView.cancelDialog();
                    int stringResId=msg.arg1;
                    loginView.showToast(stringResId);
                    break;
                case 1003:
                    //获取微信授权的Token 成功
                    Bundle data = msg.getData();
                    getWxInfo(data.getString("openid"), data.getString("access_token"));
                    break;
                case 1004:
                    //获取微信用户openId,头像 昵称成功，进行业务服务器登录
                    Bundle data1 = msg.getData();
                    loginByThird(data1.getString("unionid"),data1.getString("openid"), data1.getString("headimgurl"), data1.getString("nickname"), 0);
                    break;
                case 1005:
                    //获取微信用户openId,头像 昵称成功，进行业务服务器登录
                    Bundle data2 = msg.getData();
                    loginByThird(data2.getString("unionid"),data2.getString("openid"), data2.getString("headimgurl"), data2.getString("nickname"), 1);
                    break;


            }
        }
    };

    public LoginPresenter(LoginContract.View loginView) {
        this.loginView = loginView;
        this.loginView.setPresenter(this);

    }

    @SuppressLint("HardwareIds")
    @Override
    public void requestServer(String mobile, String password) {
        JSONObject data = new JSONObject();
        data.put("mobile",mobile);
        data.put("password", password);
        data.put("platform", "0");
        String deviceId = SettingsManager.getInstance().getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            data.put("deviceId", deviceId);
        }

        ApiUtis.getInstance().postJSON(data, Constant.URL_LOGIN, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                Message message = handler.obtainMessage();
                if ("0".equals(code)) {
                    JSONObject userJson = jsonObject.getJSONObject("data");

                    message.what = 1000;
                    message.obj = userJson;

                } else if ("004".equals(code)) {
                    //账号被封禁
                    message.what = 1002;
                    message.arg1 = R.string.api_error_10;
                 } else if ("005".equals(code)) {
                    //账号异常
                    message.what = 1002;
                    message.arg1 = R.string.api_error_11;
                 } else if ("112".equals(code)) {
                    //设备被封禁
                    message.what = 1002;
                    message.arg1 = R.string.api_error_10;
                 } else if ("114".equals(code)) {
                    //账号不存在
                    message.what = 1002;
                    message.arg1 = R.string.api_error_16;
                 } else if ("115".equals(code)) {
                    //密码错误
                    message.what = 1002;
                    message.arg1 = R.string.api_error_17;
                 } else {
                    message.what = 1002;
                    message.arg1 = R.string.api_error_0;
                }
                message.sendToTarget();
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1002;
                message.arg1 = errorCode;
            }
        });


    }


    private void loginIm(final JSONObject userJson) {
        String userId = userJson.getString("userId");
        String password = userJson.getString("imPassword");
        //设置本地数据在登录IM之前是因为，IM登录之后回调登录成功之后，会唤起/api/config接口，此时会没有设置token，导致错误
        UserManager.get().saveMyUser(userJson);
        HTClient.getInstance().login(userId, password, new HTClient.HTCallBack() {
            @Override
            public void onSuccess() {
                 Message message = handler.obtainMessage();
                message.what = 1001;
                message.sendToTarget();
            }

            @Override
            public void onError() {
                UserManager.get().clearMyData();
                Message message = handler.obtainMessage();
                message.what = 1002;
                message.arg1 = R.string.im_error_1;
                message.sendToTarget();
            }
        });

    }




    /**
     * 获取微信授权的Token
     */
    public void getWxToken(String code) {


        List<Param> params = new ArrayList<>();
        params.add(new Param("appid", HTConstant.WX_APP_ID_LOGIN));
        params.add(new Param("secret", HTConstant.WX_APP_SECRET_LOGIN));
        params.add(new Param("code", code));
        params.add(new Param("grant_type", "authorization_code"));
        ApiUtis.getInstance().postForm(params, Constant.WX_APP_OAUTH2_URL, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                 String unionid = jsonObject.getString("unionid");

                // String refresh_token = jsonObject.getString("refresh_token");
                //  String expires_in = jsonObject.getString("expires_in");
                if(!jsonObject.containsKey("access_token")||!jsonObject.containsKey("openid")){
                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.arg1=R.string.api_error_18;
                    message.sendToTarget();
                    return;
                }

                String access_token = jsonObject.getString("access_token");
               String openid = jsonObject.getString("openid");
                Message message = handler.obtainMessage();
                message.what = 1003;
                Bundle bundle = new Bundle();
                bundle.putString("openid", openid);
                bundle.putString("access_token", access_token);
                message.setData(bundle);
                message.sendToTarget();
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1002;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });


    }

    @Override
    public void loginByQQ(String token,String openId, String nickname, String avatar) {
         String Url="https://graph.qq.com/oauth2.0/me?access_token="+token+"&unionid=1";
        ApiUtis.getInstance().get(  Url, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                  if(jsonObject!=null&&jsonObject.containsKey("unionid")){
                      Message message=handler.obtainMessage();

                      Bundle bundle = new Bundle();
                      bundle.putString("nickname", nickname);
                      bundle.putString("headimgurl", avatar);
                      bundle.putString("unionid", jsonObject.getString("unionid"));
                      bundle.putString("openid", openId);
                      message.setData(bundle);
                      message.what=1005;
                      message.sendToTarget();
                  }else {
                      Message message=handler.obtainMessage();

                      Bundle bundle = new Bundle();
                      bundle.putString("nickname", nickname);
                      bundle.putString("headimgurl", avatar);
                      bundle.putString("unionid", openId);
                      bundle.putString("openid", openId);
                      message.setData(bundle);
                      message.what=1005;
                      message.sendToTarget();
                  }

            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();

                Bundle bundle = new Bundle();
                bundle.putString("nickname", nickname);
                bundle.putString("headimgurl", avatar);
                bundle.putString("unionid", openId);
                bundle.putString("openid", openId);
                message.setData(bundle);
                message.what=1005;
                message.sendToTarget();
            }
        });
    }


    /**
     * 获取到Token后请求服务器获取个人信息
     */
    private void getWxInfo( final String openid,String access_token) {


        List<Param> paramsList = new ArrayList<>();
        paramsList.add(new Param("access_token", access_token));
        paramsList.add(new Param("openid", openid));

        ApiUtis.getInstance().postForm(paramsList, Constant.WX_APP_USERINFO_URL, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                if(!jsonObject.containsKey("nickname")||!jsonObject.containsKey("headimgurl")){
                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.arg1=R.string.api_error_19;
                    message.sendToTarget();
                    return;
                }

                String nickname = jsonObject.getString("nickname");
//                String sex = jsonObject.getString("sex");
//                String province = jsonObject.getString("province");
//                String city = jsonObject.getString("city");
//                String country = jsonObject.getString("country");
                String unionid= jsonObject.getString("unionid");
                String headimgurl = jsonObject.getString("headimgurl");
                Message message = handler.obtainMessage();
                message.what = 1004;
                Bundle bundle = new Bundle();
                bundle.putString("nickname", nickname);
                bundle.putString("headimgurl", headimgurl);
                bundle.putString("unionid", unionid);
                bundle.putString("openid", openid);
                message.setData(bundle);
                message.sendToTarget();

            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1002;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });
    }

    /**
     * 通过第三方登录
     */
    private void loginByThird(String unionid, String openid,String thirdAvatar, String thirdNickname, final int thirdType) {

        JSONObject data = new JSONObject();
        data.put("type", thirdType+"");
        data.put("nick", thirdNickname);
        data.put("avatar", thirdAvatar);
        data.put("uniqueCode", unionid);
        data.put("openId", openid);
        data.put("platform", 1);
        String deviceId = SettingsManager.getInstance().getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            data.put("deviceId", deviceId);
        }

        ApiUtis.getInstance().postJSON(data, Constant.URL_THIRD_LOGIN, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                if(!jsonObject.containsKey("code")){
                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.arg1=R.string.api_error_20;
                    message.sendToTarget();
                    return;
                }

                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONObject userJson = jsonObject.getJSONObject("data");
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.obj = userJson;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1002;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });


    }


    @Override
    public void start() {

    }
}
