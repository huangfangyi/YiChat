package com.htmessage.yichat.acitivity.password;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.Validator;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;
import com.htmessage.update.login.LoginActivity;

public class PasswordPrester implements PasswordBasePrester {
    private PasswordView passwordView;
    String SMScode;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (passwordView == null) {
                return;
            }
            switch (msg.what) {

                case 1000:
                    CommonUtils.cencelDialog();
                    SMScode = (String) msg.obj;
                    passwordView.onSendSMSCodeSuccess(SMScode);
                    Toast.makeText(passwordView.getBaseActivity(), R.string.send_sms_succ, Toast.LENGTH_SHORT).show();
                    break;
                case 1001:
                    CommonUtils.cencelDialog();
                    int errorMsg = msg.arg1;
                    Toast.makeText(passwordView.getBaseActivity(), errorMsg, Toast.LENGTH_SHORT).show();

                    break;


                case 1002:
                    //设置
                    CommonUtils.cencelDialog();
                    Toast.makeText(passwordView.getBaseActivity(), R.string.update_success, Toast.LENGTH_SHORT).show();
                    passwordView.getBaseActivity().finish();

                    break;
            }

        }
    };


    public PasswordPrester(PasswordView passwordView) {
        this.passwordView = passwordView;
        this.passwordView.setPresenter(this);
    }

    @Override
    public void sendSMSCode(final String mobile, String countryName, String countryCode) {
        if (TextUtils.isEmpty(mobile)) {
            passwordView.showToast(R.string.mobile_not_be_null);
            return;
        }
        if (countryName.equals(passwordView.getBaseContext().getString(R.string.china)) && countryCode.equals(passwordView.getBaseContext().getString(R.string.country_code))) {
            if (!Validator.isMobile(mobile)) {
                passwordView.showToast(R.string.please_input_true_mobile);
                return;
            }
        }

        CommonUtils.showDialogNumal(passwordView.getBaseActivity(), passwordView.getBaseActivity().getString(R.string.sending));
        passwordView.startTimeDown();

        JSONObject data = new JSONObject();
        data.put("mobile", mobile);
        ApiUtis.getInstance().postJSON(data, Constant.URL_SMS_SEND, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    String SMScode = jsonObject.getString("data");
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.obj = SMScode;
                    message.sendToTarget();
                } else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });

//        List<Param> params = new ArrayList<Param>();
//        params.add(new Param("moblie", mobile));
//
//        new OkHttpUtils(passwordView.getBaseActivity()).post(params, HTConstant.URL_SMS, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                CommonUtils.cencelDialog();
//                int code=jsonObject.getInteger("code");
//
//                switch (code) {
//                    case 1:
//                     String    SMScode=jsonObject.getString("result");
//                        passwordView.onSendSMSCodeSuccess(SMScode);
//                        Toast.makeText(passwordView.getBaseActivity(),"验证码已成功发送",Toast.LENGTH_SHORT).show();
//
//
//                        break;
//                    case 2:
//                        Toast.makeText(passwordView.getBaseActivity(),"改手机号已经注册，请直接登录",Toast.LENGTH_SHORT).show();
//
//                        break;
//                    default:
//                        Toast.makeText(passwordView.getBaseActivity(),"获取验证码失败",Toast.LENGTH_SHORT).show();
//
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//                Toast.makeText(passwordView.getBaseActivity(),"获取验证码失败",Toast.LENGTH_SHORT).show();
//
//            }
//        });
//


    }

    @Override
    public void resetPassword(final String cacheCode1, String smsCode, String password, String confimPwd, String mobile,boolean isReset) {
        if (TextUtils.isEmpty(mobile)) {
            passwordView.showToast(R.string.mobile_not_be_null);
            return;
        }
        if (Constant.isSMS) {
            if (TextUtils.isEmpty(smsCode) || TextUtils.isEmpty(SMScode)) {
                passwordView.showToast(R.string.please_input_code);
                return;
            }


            if (!smsCode.equals(smsCode)) {
                passwordView.showToast(R.string.code_is_wrong);
                return;
            }
        }
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confimPwd)) {
            passwordView.showToast(R.string.new_password_cannot_be_empty);
            return;
        }
        if (!Validator.isPassword(password) || !Validator.isPassword(confimPwd)) {
            passwordView.showToast(R.string.pwd_tips);
            return;
        }
        if (!password.equals(confimPwd)) {
            passwordView.showToast(R.string.Two_input_password);
            return;
        }
        CommonUtils.showDialogNumal(passwordView.getBaseActivity(), passwordView.getBaseActivity().getString(R.string.are_reset_password));

        JSONObject data = new JSONObject();
        if(!isReset){
            data.put("mobile",mobile);
        }

        data.put("password", password);
        ApiUtis.getInstance().postJSON(data, Constant.URL_INFO_UPDATE, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.sendToTarget();

                } else if("117".equals(code)){
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.bind_mobile_fail;
                    message.sendToTarget();

                }else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.update_fail;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });

//
//        List<Param> params = new ArrayList<Param>();
//        params.add(new Param("newPassword", password));
//        params.add(new Param("tel", mobile));
//        new OkHttpUtils(passwordView.getBaseContext()).post(params, HTConstant.URL_RESETPASSWORD, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                CommonUtils.cencelDialog();
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        passwordView.clearCacheCode();
//                        passwordView.showToast(R.string.password_reset_success);
//                        logOut(passwordView.getIsReset());
//                        break;
//                    default:
//                        passwordView.showToast(R.string.password_reset_failed);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//                passwordView.showToast(R.string.password_reset_failed);
//            }
//        });
    }

    @Override
    public void bindMobile(String cacheCode1, String smsCode, String password, String confimPwd, final String mobile, boolean isBind) {
        if (TextUtils.isEmpty(mobile)) {
            passwordView.showToast(R.string.mobile_not_be_null);
            return;
        }

        if (isBind) {
            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confimPwd)) {
                passwordView.showToast(R.string.new_password_cannot_be_empty);
                return;
            }
            if (!Validator.isPassword(password) || !Validator.isPassword(confimPwd)) {
                passwordView.showToast(R.string.pwd_tips);
                return;
            }

        }
        if (Constant.isSMS) {
            if (!smsCode.equals(SMScode)) {
                passwordView.showToast(R.string.code_is_wrong);
                return;
            }
            if (TextUtils.isEmpty(smsCode) || TextUtils.isEmpty(SMScode)) {
                passwordView.showToast(R.string.please_input_code);
                return;

            }

        }
        if (!password.equals(confimPwd)) {
            passwordView.showToast(R.string.Two_input_password);
            return;
        }

        CommonUtils.showDialogNumal(passwordView.getBaseActivity(), "正在提交....");


        JSONObject data = new JSONObject();
        if (isBind) {
            data.put("password", password);
        }
        data.put("mobile", mobile);
        ApiUtis.getInstance().postJSON(data, Constant.URL_INFO_UPDATE, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.sendToTarget();

                }else if("117".equals(code)){
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.update_mobile_used;
                    message.sendToTarget();
                } else
                    {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.update_fail;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });


//
//        List<Param> params = new ArrayList<Param>();
//
//
//        if (isBind) {
//
//            params.add(new Param("password", password));
//        }
//        params.add(new Param("userId", HTApp.getInstance().getUserId()));
//        params.add(new Param("mobile", mobile));
//        new OkHttpUtils(passwordView.getBaseContext()).post(params, HTConstant.URL_BINDMOBILE, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                CommonUtils.cencelDialog();
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        passwordView.clearCacheCode();
//                        passwordView.showToast("设置成功");
//
//                        passwordView.getBaseActivity().finish();
//                        //   logOut(passwordView.getIsReset());
//                        break;
//                    default:
//                        passwordView.showToast("设置失败");
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//                passwordView.showToast("设置失败");
//            }
//        });
//
    }


    @Override
    public void start() {

    }


}
