package com.htmessage.yichat.acitivity.main.wallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.uitls.WalletUtils;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.main.profile.info.profile.ProfileActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.Validator;
import com.htmessage.yichat.widget.SetTelCountTimer;

/**
 * Created by huangfangyi on 2019/6/4.
 * qq 84543217
 */
public class PayPasswordActivity extends BaseActivity implements View.OnClickListener {

    // private RelativeLayout re_password_origin;
    //   private EditText et_password_origin;
    private EditText et_password;
    private EditText et_code;
    private EditText et_password_confire;
    private Button btn_ok;
    private SetTelCountTimer telCountTimer;
    String mobile;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 1000:
                    CommonUtils.cencelDialog();
                    finishTimeDown();
                    Toast.makeText(PayPasswordActivity.this, R.string.send_sms_succ, Toast.LENGTH_SHORT).show();
                    break;
                case 1001:
                    CommonUtils.cencelDialog();
                    finishTimeDown();
                    int errorMsg = msg.arg1;
                    Toast.makeText(PayPasswordActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    finish();
                    break;


            }

        }
    };


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_paypassword);
        //检查该用户是否绑定了手机号（微信登录用户不绑定手机号）
        mobile = UserManager.get().getMyUser().getString("mobile");
        if (TextUtils.isEmpty(mobile)) {

            AlertDialog.Builder exceptionBuilder = new AlertDialog.Builder(PayPasswordActivity.this);

            exceptionBuilder.setTitle("请先绑定手机号");
            exceptionBuilder.setMessage("第三方登录用户需要先绑定手机号");
            exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    Intent intent = new Intent(PayPasswordActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();

                }
            });
            exceptionBuilder.setCancelable(false);
            exceptionBuilder.show();
        }
        initView();
        initData();


    }

    private void initView() {
//        re_password_origin=findViewById(R.id.re_password_origin);
//        et_password_origin=findViewById(R.id.et_password_origin);

        et_password = findViewById(R.id.et_password);
        et_code = findViewById(R.id.et_code);
        et_password_confire = findViewById(R.id.et_password_confire);
        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        Button btn_code = (Button) findViewById(R.id.btn_code);
        btn_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSCode(mobile);
            }
        });
        telCountTimer = new SetTelCountTimer(btn_code);

        if (!Constant.isSMS) {
            findViewById(R.id.rl_smscode).setVisibility(View.GONE);
        }
    }

    boolean isChange = false;

    private void initData() {
        if (WalletUtils.getInstance().isSetPayPassword()) {
            //   re_password_origin.setVisibility(View.VISIBLE);
            setTitle("修改支付密码");
            isChange = true;
        } else {
            //  re_password_origin.setVisibility(View.GONE);
            setTitle("设置支付密码");
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                //   String originPassword=et_password_origin.getText().toString().trim();
                final String password = et_password.getText().toString().trim();
                String confire = et_password_confire.getText().toString().trim();
                String code = et_code.getText().toString().trim();
                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confire)) {

                    Toast.makeText(getApplicationContext(), "密码不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!Validator.isNumber(password) || password.length() != 6) {

                    Toast.makeText(getApplicationContext(), "请输入6位数字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confire)) {

                    Toast.makeText(getApplicationContext(), "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Constant.isSMS) {

                    if (TextUtils.isEmpty(code)) {

                        Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
                        return;

                    }
                    if (TextUtils.isEmpty(SMScode)) {

                        Toast.makeText(getApplicationContext(), "请获取验证码", Toast.LENGTH_SHORT).show();
                        return;

                    }
                    if (!code.equals(SMScode)) {

                        Toast.makeText(getApplicationContext(), "验证码错误", Toast.LENGTH_SHORT).show();
                        return;

                    }
                }
                CommonUtils.showDialogNumal(PayPasswordActivity.this, "正在处理....");
                JSONObject body=new JSONObject();
                body.put("password",password);
                ApiUtis.getInstance().postJSON(body, Constant.URL_paypsw_set, new ApiUtis.HttpCallBack() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                      String code=jsonObject.getString("code");
                      if("0".equals(code)){
                          Message message=handler.obtainMessage();
                          message.what=1001;
                          message.arg1=R.string.set_successful;
                          WalletUtils.getInstance().setPayPassword(true);
                          message.sendToTarget();
                      }else {
                          Message message=handler.obtainMessage();
                          message.what=1001;
                          message.arg1=R.string.api_error_5;

                          message.sendToTarget();
                      }
                    }

                    @Override
                    public void onFailure(int errorCode) {
                        Message message=handler.obtainMessage();
                        message.what=1001;
                        message.arg1=errorCode;

                        message.sendToTarget();
                    }
                });



            //    CommonUtils.setPayPassword(PayPasswordActivity.this, password);
              //  CommonUtils.cencelDialog();


                break;
        }
    }


    private String SMScode = null;

    public void sendSMSCode(final String mobile) {
//        if(TextUtils.isEmpty(mobile)){
//            Toast.makeText(getApplicationContext(),"请先绑定手机号", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(PayPasswordActivity.this, BindMobileActivity.class));
//            return;
//        }

        CommonUtils.showDialogNumal(this, getString(R.string.sending));
        startTimeDown();


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
}
