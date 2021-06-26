package com.htmessage.yichat.acitivity.main.wallet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.uitls.WalletUtils;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.Validator;
import com.htmessage.yichat.widget.SetTelCountTimer;

public class AddBankCardActivity extends BaseActivity {
    private SetTelCountTimer telCountTimer;
    private String bankName, cardNo;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    CommonUtils.cencelDialog();
                    Toast.makeText(AddBankCardActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                    break;
                case 1001:
                    CommonUtils.cencelDialog();

                    finishTimeDown();
                    int resId=msg.arg1;
                    Toast.makeText(AddBankCardActivity.this,resId,Toast.LENGTH_SHORT).show();
                    break;
                case 2000:
                    CommonUtils.cencelDialog();

                    finishTimeDown();

                    Toast.makeText(AddBankCardActivity.this, "验证码已成功发送", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_addbankcard);
        setTitle("添加银行卡");
        bankName = this.getIntent().getStringExtra("bankName");
        cardNo = this.getIntent().getStringExtra("bankNo");
        //final TextView et_bankname = this.findViewById(R.id.et_bank_name);
        //    final EditText etCardNo = this.findViewById(R.id.et_card_num)
        //    ;

        TextView tvBankName = findViewById(R.id.tv_bankname);
        tvBankName.setText(bankName + "(" + cardNo.substring(0, 4) + ")");

        final EditText etName = this.findViewById(R.id.et_name);
        final EditText etIdcard = this.findViewById(R.id.et_idcard);
        final EditText etMobile = this.findViewById(R.id.et_mobile);
        final EditText et_code = this.findViewById(R.id.et_code);

        // EditText etCode = findViewById(R.id.et_code);
//        etCardNo.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.length() > 6) {
//                    String cardNo6 = (String) s.toString().subSequence(0, 6);
//
//                    et_bankname.setText(BankCardUtils.getname(cardNo6));
//                }
//            }
//        });
        Button btnOk = this.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                String code = et_code.getText().toString().trim();
//                if (TextUtils.isEmpty(code)) {
//
//                    Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
//                    return;
//
//                }
//                if (TextUtils.isEmpty(SMScode)) {
//
//                    Toast.makeText(getApplicationContext(), "请获取验证码", Toast.LENGTH_SHORT).show();
//                    return;
//
//                }
//                if (!code.equals(SMScode)) {
//
//                    Toast.makeText(getApplicationContext(), "验证码错误", Toast.LENGTH_SHORT).show();
//                    return;
//
//                }


//
//                String bankName = et_bankname.getText().toString().trim();
//                String cardNo = etCardNo.getText().toString().trim();
                String idCard = etIdcard.getText().toString().trim();
                String mobile = etMobile.getText().toString().trim();
                String name = etName.getText().toString().trim();
                if (TextUtils.isEmpty(bankName) || TextUtils.isEmpty(cardNo) || TextUtils.isEmpty(idCard) || TextUtils.isEmpty(name) || TextUtils.isEmpty(mobile)) {
                    Toast.makeText(AddBankCardActivity.this, "所有信息不可为空", Toast.LENGTH_SHORT).show();
                    return;


                }

                uploadServer(bankName, cardNo, mobile, name, idCard);

            }
        });

        Button btn_code = (Button) findViewById(R.id.btn_code);
        btn_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile=etMobile.getText().toString().trim();
                sendSMSCode(mobile);
            }
        });
        telCountTimer = new SetTelCountTimer(btn_code);
    }

    private void uploadServer(String bankName, String cardNo, String mobile, String name, String idCard) {
        CommonUtils.showDialogNumal(AddBankCardActivity.this, "正在处理....");

        JSONObject body = new JSONObject();
        body.put("name", name);
        body.put("mobile", mobile);
        body.put("idNumber", idCard);
        body.put("bankName", bankName);
        body.put("bankNumber", cardNo);
        ApiUtis.getInstance().postJSON(body, Constant.URL_bank_card_add, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null){
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    WalletUtils.getInstance().saveBankCardList(data);
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.sendToTarget();
                } else if ("270".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.vertify_bankcard_error;
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
                if(handler==null){
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });

//
//        List<Param> paramList = new ArrayList<>();
//        paramList.add(new Param("userId", HTApp.getInstance().getUsername()));
//        paramList.add(new Param("bank", bankName));
//        paramList.add(new Param("mobile", mobile));
//        paramList.add(new Param("bankcard", cardNo));
//        paramList.add(new Param("realname", name));
//        paramList.add(new Param("idcard", idCard));
//
//        new OkHttpUtils(AddBankCardActivity.this).post(paramList, HTConstant.ADD_BANK_CARD, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                CommonUtils.cencelDialog();
//
//                int code = jsonObject.getInteger("code");
//                if (code == 1) {
//                    Toast.makeText(AddBankCardActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
//                    setResult(RESULT_OK);
//                    finish();
//                } else {
//                    Toast.makeText(AddBankCardActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//
//                Toast.makeText(AddBankCardActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }


    private String SMScode = null;
    public void sendSMSCode(final String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(getApplicationContext(), "请输入手机号", Toast.LENGTH_SHORT).show();
           // startActivity(new Intent(AddBankCardActivity.this, BindMobileActivity.class));
            return;
        }
        if(! Validator.isMobile(mobile)){
            Toast.makeText(getApplicationContext(), "手机号码格式错误", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(AddBankCardActivity.this, BindMobileActivity.class));
            return;
        }

        CommonUtils.showDialogNumal(this, getString(R.string.sending));
        startTimeDown();


        JSONObject data = new JSONObject();
        data.put("mobile", mobile);
        ApiUtis.getInstance().postJSON(data, Constant.URL_SMS_SEND, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null){
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                      SMScode = jsonObject.getString("data");
                    Message message = handler.obtainMessage();
                    message.what = 2000;
                   // message.obj = SMScode;
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
                if(handler==null){
                    return;
                }
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });



//
//        List<Param> params = new ArrayList<Param>();
//        params.add(new Param("moblie", mobile));
//
//        new OkHttpUtils(this).post(params, Constant.URL_SMS_SEND, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                CommonUtils.cencelDialog();
//                int code = jsonObject.getInteger("code");
//
//                switch (code) {
//                    case 1:
//                        SMScode = jsonObject.getString("result");
//
//                        Toast.makeText(AddBankCardActivity.this, "验证码已成功发送", Toast.LENGTH_SHORT).show();
//
//
//                        break;
//                    case 2:
//                        Toast.makeText(AddBankCardActivity.this, "改手机号已经注册，请直接登录", Toast.LENGTH_SHORT).show();
//
//                        break;
//                    default:
//                        Toast.makeText(AddBankCardActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
//
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//                Toast.makeText(AddBankCardActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//

    }


    public void startTimeDown(){
        if (telCountTimer != null) {
            telCountTimer.start();
        }
    }

    public void finishTimeDown() {
        if (telCountTimer != null) {
            telCountTimer.onFinish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler=null;

    }
}
