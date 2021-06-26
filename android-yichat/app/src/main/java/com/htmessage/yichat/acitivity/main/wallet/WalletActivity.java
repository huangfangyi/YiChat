package com.htmessage.yichat.acitivity.main.wallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.uitls.WalletUtils;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.main.pay.activity.PayMentActivity;
import com.htmessage.yichat.acitivity.main.pay.utils.PayUtils;
import com.htmessage.yichat.acitivity.red.history.RpHistoryActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.LoggerUtils;
import com.htmessage.yichat.utils.Validator;


public class WalletActivity extends BaseActivity implements View.OnClickListener {
     private TextView  tv_adavance, tv_title;
     private Button btn_recharge, btn_withdraw;
     private int TO_QRCODE_PAY = 51;

     private Handler handler=new Handler(Looper.getMainLooper()){
         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             switch (msg.what){
                 case 1000:
                     Double balance= (Double) msg.obj;
                     tv_adavance.setText(String.format(getString(R.string.pay_money), Validator.formatMoney(balance)));

                     break;
                 case 1001:
                     int resId=msg.arg1;
                     Toast.makeText(WalletActivity.this,resId,Toast.LENGTH_SHORT).show();
                     break;
             }
          }
     };


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_wallet);
         initView();
        initData();
        setListener();
        showRightTextView("签到", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WalletActivity.this,SignActivity.class));
            }
        });
    }



    private void setListener() {
        btn_recharge.setOnClickListener(this);

        btn_withdraw.setOnClickListener(this);
        this.findViewById(R.id.rl_record).setOnClickListener(this);
        this.findViewById(R.id.rl_redpacket).setOnClickListener(this);
        this.findViewById(R.id.rl_safe).setOnClickListener(this);
        this.findViewById(R.id.rl_bank).setOnClickListener(this);
    }

    private void initData() {
         tv_title.setText(R.string.wallet);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getBlance();
    }

    private void initView() {
         tv_title = (TextView) findViewById(R.id.tv_title);
         tv_adavance = (TextView) findViewById(R.id.tv_adavance);
        btn_recharge = (Button) findViewById(R.id.btn_recharge);
         btn_withdraw = (Button) findViewById(R.id.btn_withdraw);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recharge:

                startActivity(new Intent(WalletActivity.this, PrePayActivity.class) );

                break;

            case R.id.btn_rtc:
                startActivityForResult(new Intent(WalletActivity.this, PayMentActivity.class), TO_QRCODE_PAY);
                break;
            case R.id.btn_withdraw:
                startActivity(new Intent(WalletActivity.this, WithDrawActivity.class));
                break;
            case R.id.rl_redpacket:
                startActivity(new Intent(WalletActivity.this, RpHistoryActivity.class));
                break;
            case R.id.rl_safe:
                startActivity(new Intent(WalletActivity.this, PayPasswordActivity.class));
                break;
            case R.id.rl_record:
                startActivity(new Intent(WalletActivity.this, TradeRecordsActivity.class));
                break;
            case R.id.rl_bank:
                startActivity(new Intent(WalletActivity.this, PreAddBandCardActivity.class));
                break;
        }
    }


    /**
     * 获取余额
     */
    private void getBlance() {

        JSONObject body=new JSONObject();
        ApiUtis.getInstance().postJSON(body, Constant.URL_BALANCE, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    JSONObject data=jsonObject.getJSONObject("data");
                    Double balance=data.getDouble("balance");
                    //本地缓存---
                    WalletUtils.getInstance().saveBalance(balance);
                    Message message=handler.obtainMessage();
                    message.what=1000;
                    message.obj=balance;
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

//        List<Param> params = new ArrayList<>();
//        params.add(new Param("userId", HTApp.getInstance().getUsername()));
//        new OkHttpUtils(getBaseContext()).post(params, Constant.URL_BALANCE, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        //JSONObject data = jsonObject.getJSONObject("data");
//                        String balance = jsonObject.getString("balance");
//                        MmvkManger.getIntance().putDouble(HTApp.getInstance().getUsername() + "balance", Double.parseDouble(balance));
//                        break;
//                    default:
//                        tv_adavance.setText(String.format(getString(R.string.pay_money), Validator.formatMoney(0)));
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                tv_adavance.setText(String.format(getString(R.string.pay_money), Validator.formatMoney(0)));
//            }
//        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TO_QRCODE_PAY && resultCode == Activity.RESULT_OK) {
            getBlance();
        } else {
            LoggerUtils.e("---支付成功后的code:" + resultCode);
            showPayResult(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 显示支付结果
     *
     * @param data
     */
    private void showPayResult(Intent data) {

        PayUtils.onPayResult(data, new PayUtils.onPayResultListenr() {
            @Override
            public void success() {
                CommonUtils.showToastLong(getBaseContext(), R.string.recharge_success);
            }

            @Override
            public void faile() {
                 CommonUtils.showToastLong(getBaseContext(), R.string.recharge_failed);
            }

            @Override
            public void cancle() {
                 CommonUtils.showToastLong(getBaseContext(), R.string.pay_cancle);
            }
        });
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }



    private void showToast(Object msg) {
        CommonUtils.showToastShort(getBaseContext(), msg);
    }
}
