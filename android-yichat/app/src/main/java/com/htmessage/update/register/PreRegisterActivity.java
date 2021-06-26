package com.htmessage.update.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.Validator;
import com.htmessage.yichat.widget.SetTelCountTimer;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;

/**
 * Created by huangfangyi on 2019/4/22.
 * qq 84543217
 */
public class PreRegisterActivity extends BaseActivity {

    private Button btn_code, btn_next;
    private EditText et_usertel, et_code;
    private SetTelCountTimer telCountTimer;
    private String SMScode;
    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1000:
                    startTimeDown();
                    Toast.makeText(PreRegisterActivity.this, R.string.send_sms_succ, Toast.LENGTH_SHORT).show();
                    break;
                case 1001:
                    int errorMsg = msg.arg1;
                    Toast.makeText(PreRegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_pre_register);

        et_usertel = (EditText) findViewById(R.id.et_usertel);
        //获取国家code
        btn_code = (Button) findViewById(R.id.btn_code);
        et_code = (EditText) findViewById(R.id.et_code);
        telCountTimer = new SetTelCountTimer(btn_code);
        btn_next = (Button) findViewById(R.id.btn_next);
        telCountTimer.setListener(new SetTelCountTimer.OnCountTimerFinishListener() {
            @Override
            public void onFinish() {
                setMobileEnable(true);
            }
        });
        if(!Constant.isSMS){
            this.findViewById(R.id.rl_smscode).setVisibility(View.GONE);
        }
        btn_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = et_usertel.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(PreRegisterActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();

                    return;

                }

                if (!Validator.isMobile(mobile)) {
                    Toast.makeText(PreRegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();

                    return;
                }


                sendSmsCode(mobile);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = et_usertel.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(PreRegisterActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();

                    return;

                }

                if (!Validator.isMobile(mobile)) {
                    Toast.makeText(PreRegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();

                    return;
                }

                if( Constant.isSMS) {
                    String currentCode = et_code.getText().toString().trim();

                    if (TextUtils.isEmpty(currentCode)) {
                        Toast.makeText(PreRegisterActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    if (TextUtils.isEmpty(SMScode)) {
                        Toast.makeText(PreRegisterActivity.this, "请获取验证码", Toast.LENGTH_SHORT).show();

                        return;
                    }

                    if (!currentCode.equals(SMScode)) {
                        Toast.makeText(PreRegisterActivity.this, "验证码不正确", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                startActivity(new Intent(PreRegisterActivity.this, RegisterActivity.class).putExtra("tel", mobile));
                finish();
            }
        });

    }

    public void setMobileEnable(boolean enable) {
        if (et_usertel != null) {
            et_usertel.setEnabled(enable);
        }
    }


    public void startTimeDown() {
        if (telCountTimer != null) {
            telCountTimer.start();
        }
    }


    public void finishTimeDown() {
        if (telCountTimer != null) {
            telCountTimer.onFinish();
        }
    }

    public void sendSmsCode(final String mobile) {
        JSONObject data = new JSONObject();
        data.put("mobile", mobile);
        ApiUtis.getInstance().postJSON(data, Constant.URL_SMS_SEND, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    SMScode = jsonObject.getString("data");
                    Message message = handler.obtainMessage();
                    message.what = 1000;
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

//

    }

}
