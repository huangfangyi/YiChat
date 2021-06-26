package com.htmessage.yichat.acitivity.main.pay.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.weight.PswInputView;
import com.htmessage.yichat.acitivity.main.pay.utils.PayUtils;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.LoggerUtils;
import com.htmessage.sdk.manager.MmvkManger;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.utils.Param;
import com.htmessage.yichat.utils.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：
 * 类描述： 扫码支付相关
 * 创建人：songlijie
 * 创建时间：2017/11/7 14:27
 * 邮箱:814326663@qq.com
 */
public class PayMentPayActivity extends Activity implements View.OnClickListener, PswInputView.InputCallBack {
    private TextView tv_pay_title, tv_redenvelope_name, tv_redenvelope_amount, tv_paytype_name, tv_pswd_tips, tv_forget_pswd;
    private ImageView iv_exit, iv_paytype_icon;
    private PswInputView pswinputview;
    private Button btn_pay;
    private LinearLayout layout_paytype, rootLayout;
    private boolean isChargePay = true;
    private int payWayType = 0;
    private String userId;
    private String balance = "0.00";
    private boolean payPasss = false;
    private int PAY_WAYS = 50;
    private String money = "0.00";
    private String content = "";
    private PayBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_pay_dialog);
         getData();
        initView();
        initData();
        setListener();
    }

    private void initData() {
         tv_redenvelope_name.setText(R.string.transfer_qrcode);
        setPayType(payWayType);
    }

    private void setListener() {
        iv_exit.setOnClickListener(this);
       // layout_paytype.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
        pswinputview.setInputCallBack(this);
        tv_forget_pswd.setOnClickListener(this);
    }

    private void initView() {
        tv_pay_title = (TextView) findViewById(R.id.tv_pay_title);
        tv_redenvelope_name = (TextView) findViewById(R.id.tv_redenvelope_name);
        tv_redenvelope_amount = (TextView) findViewById(R.id.tv_redenvelope_amount);
        tv_paytype_name = (TextView) findViewById(R.id.tv_paytype_name);
        tv_pswd_tips = (TextView) findViewById(R.id.tv_pswd_tips);
        tv_forget_pswd = (TextView) findViewById(R.id.tv_forget_pswd);

        iv_exit = (ImageView) findViewById(R.id.iv_exit);
        iv_paytype_icon = (ImageView) findViewById(R.id.iv_paytype_icon);

        pswinputview = (PswInputView) findViewById(R.id.pswinputview);
        btn_pay = (Button) findViewById(R.id.btn_pay);
     //   layout_paytype = (LinearLayout) findViewById(R.id.layout_paytype);
        rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
    }

    private void getData() {
        isChargePay = true;
        userId = getIntent().getStringExtra(HTConstant.JSON_KEY_USERID);
        money = getIntent().getStringExtra("money");
        content = getIntent().getStringExtra("content");
        if (TextUtils.isEmpty(userId)) {
            finish();
            return;
        }
         getBlance();
        receiver = new PayBroadcastReceiver();
        IntentFilter fileter = new IntentFilter();
        fileter.addAction(IMAction.PAY_BY_WECHAT_RESULT);
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(receiver, fileter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_exit:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.btn_pay:
                if (!isChargePay) {
                    rootLayout.setVisibility(View.INVISIBLE);
                    if (payWayType == 1) {
                        payByAli(money,userId,content);
                    } else if (payWayType == 2) {
                        PayUtils.payByWeChat(PayMentPayActivity.this,money,2);
                    }else if (payWayType ==3){
                        PayUtils.getTnFromUnionPayService(PayMentPayActivity.this,money);
                    }
                }else{
                     if ("0.00".equals(balance) || !"0.00".equals(money) && Validator.formatMoneyFloat(money) > Validator.formatMoneyFloat(balance)) {
                        CommonUtils.showToastShort(PayMentPayActivity.this, getString(R.string.money_isNull));
                        return;
                    }
                    if (payPasss == false) {
                        CommonUtils.showToastShort(PayMentPayActivity.this, getString(R.string.pay_pwd_is_empty));
                        return;
                    }
                }
                break;
            case R.id.layout_paytype:
//                startActivityForResult(new Intent(PayMentPayActivity.this, PayMentPayWaysActivity.class), PAY_WAYS);
//                pswinputview.clearResult();
//                rootLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_forget_pswd:
                rootLayout.setVisibility(View.INVISIBLE);
//                 if (payPasss==true) {
//                    CommonUtils.showChangePayPwdDialog(PayMentPayActivity.this, HTApp.get().getUsertel(), new CommonUtils.OnReChargeDialogClickListener() {
//
//                        @Override
//                        public void onPriformClock(final String money) {
//                            rootLayout.setVisibility(View.VISIBLE);
//                            CommonUtils.changePayPassword(PayMentPayActivity.this,money);
//                        }
//
//                        @Override
//                        public void onCancleClock() {
//                            rootLayout.setVisibility(View.VISIBLE);
//                        }
//                    });
//                }
                break;
        }
    }

    public void setPayType(int payType) {
        rootLayout.setVisibility(View.VISIBLE);
        tv_redenvelope_amount.setText(String.format(getString(R.string.pay_money), Validator.formatMoney(money)));
        if (payType == 0) {
            isChargePay = true;
            payWayType = 0;
            iv_paytype_icon.setImageResource(R.drawable.charge_icon);
            tv_paytype_name.setText(String.format(getString(R.string.pay_ways_change), Validator.formatMoney(balance)));
        } else if (payType == 1) {
            payWayType = 1;
            isChargePay = false;
            iv_paytype_icon.setImageResource(R.drawable.rp_ic_alipay);
            tv_paytype_name.setText(R.string.pay_ways_alipay);
        } else if (payType == 2) {
            payWayType = 2;
            isChargePay = false;
            iv_paytype_icon.setImageResource(R.drawable.rp_ic_wx);
            tv_paytype_name.setText(R.string.pay_ways_wechatpay);
        } else if (payType == 3) {
            payWayType = 3;
            isChargePay = false;
            iv_paytype_icon.setImageResource(R.drawable.union_pay);
            tv_paytype_name.setText(R.string.pay_ways_unionpay);
        }
        if (isChargePay) {
            pswinputview.setVisibility(View.VISIBLE);
            tv_forget_pswd.setVisibility(View.VISIBLE);
            btn_pay.setVisibility(View.GONE);
            tv_pswd_tips.setVisibility(View.VISIBLE);
        } else {
            pswinputview.setVisibility(View.GONE);
            btn_pay.setVisibility(View.VISIBLE);
            tv_forget_pswd.setVisibility(View.GONE);
            tv_pswd_tips.setVisibility(View.GONE);
        }
    }

    @Override
    public void onInputFinish(String result) {
        if (isChargePay && payWayType == 0) {
            if (payPasss==false) {
                return;
            }

                        transferMoney(money, userId, content,result);
                        pswinputview.clearResult();



        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PAY_WAYS){
            int payType = data.getIntExtra("payType", 0);
            if (payType == 0) {
                getBlance();
            }
            setPayType(payType);
        } else {
            LoggerUtils.e("---支付成功后的code:"+resultCode);
            showPayResult(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 显示支付结果
     * @param data
     */
    private void showPayResult(Intent data){

        PayUtils.onPayResult(data, new PayUtils.onPayResultListenr() {
            @Override
            public void success() {
                transferMoney(money, userId, content,null);
            }

            @Override
            public void faile() {
                rootLayout.setVisibility(View.VISIBLE);
                CommonUtils.showToastShort(getBaseContext(),R.string.pay_failed);
            }

            @Override
            public void cancle() {
                rootLayout.setVisibility(View.VISIBLE);
                CommonUtils.showToastShort(getBaseContext(),R.string.pay_cancle);
            }
        });
    }

    /**
     *二维码转账
     * @param money
     * @param userId
     * @param content
     */
    private void transferMoney(String money, final String userId, String content,String password) {
        CommonUtils.showDialogNumal(PayMentPayActivity.this, getString(R.string.paying));
        List<Param> params = new ArrayList<>();
        params.add(new Param("userId", HTApp.getInstance().getUsername()));
        params.add(new Param("toUserId", userId));
        params.add(new Param("money", Validator.formatMoney(money)));
        params.add(new Param("remarks", content));
        new OkHttpUtils(getBaseContext()).post(params, HTConstant.TRANSFER_MONEY, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                CommonUtils.cencelDialog();
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                         setResult(RESULT_OK);
                        finish();
                        break;
                    case -1:
                        CommonUtils.showToastShort(PayMentPayActivity.this, getString(R.string.money_isNull));
                        break;
                    case -2:
                        CommonUtils.showToastShort(PayMentPayActivity.this, getString(R.string.user_not_exit));
                        break;
                    default:
                        CommonUtils.showToastShort(getBaseContext(),R.string.payment_failed);
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                CommonUtils.cencelDialog();
                CommonUtils.showToastShort(getBaseContext(),R.string.payment_failed);
            }
        });
    }

    /**
     * 获取账户余额
     */
    private void getBlance() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("userId", HTApp.getInstance().getUsername()));
        new OkHttpUtils(getBaseContext()).post(params, HTConstant.GET_BALANCE, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                LoggerUtils.e( "----获取余额:" + jsonObject.toJSONString());
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                         balance = jsonObject.getString("balance");
                        MmvkManger.getIntance().putDouble(HTApp.getInstance().getUsername()+"balance",Double.parseDouble(balance));
                        iv_paytype_icon.setImageResource(R.drawable.charge_icon);
                        tv_paytype_name.setText(String.format(getString(R.string.pay_ways_change), Validator.formatMoney(balance)));
                        if ("0.00".equals(balance)) {
                            pswinputview.setVisibility(View.GONE);
                            btn_pay.setVisibility(View.VISIBLE);
                            tv_forget_pswd.setVisibility(View.GONE);
                            tv_pswd_tips.setVisibility(View.GONE);
                        } else {
                            if (payPasss==false) {
                                pswinputview.setVisibility(View.VISIBLE);
                                tv_forget_pswd.setVisibility(View.VISIBLE);
                                btn_pay.setVisibility(View.GONE);
                                tv_pswd_tips.setVisibility(View.VISIBLE);
                            } else {
                                pswinputview.setVisibility(View.GONE);
                                btn_pay.setVisibility(View.VISIBLE);
                                tv_forget_pswd.setVisibility(View.GONE);
                                tv_pswd_tips.setVisibility(View.GONE);
                            }
                        }
                        break;
                    default:
                        balance = "0.00";
                        iv_paytype_icon.setImageResource(R.drawable.charge_icon);
                        tv_paytype_name.setText(String.format(getString(R.string.pay_ways_change), Validator.formatMoney(balance)));
                        pswinputview.setVisibility(View.GONE);
                        btn_pay.setVisibility(View.VISIBLE);
                        tv_forget_pswd.setVisibility(View.GONE);
                        tv_pswd_tips.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                balance = "0.00";
                iv_paytype_icon.setImageResource(R.drawable.charge_icon);
                tv_paytype_name.setText(String.format(getString(R.string.pay_ways_change), Validator.formatMoney(balance)));
                pswinputview.setVisibility(View.GONE);
                btn_pay.setVisibility(View.VISIBLE);
                tv_forget_pswd.setVisibility(View.GONE);
                tv_pswd_tips.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 支付包支付
     *
     * @param money
     */
    private void payByAli(final String money,final String userId ,final String conetnt) {
        PayUtils.payByAliPay(PayMentPayActivity.this, money,2, new PayUtils.PayBackListener() {
            @Override
            public void paySuccess() {
                transferMoney(money, userId, conetnt,null);
            }

            @Override
            public void payFailed() {
                CommonUtils.showToastShort(getBaseContext(), R.string.payment_failed);
                rootLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void payCancled() {
                CommonUtils.showToastShort(getBaseContext(), R.string.payment_failed);
                rootLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 微信支付的监听
     */
    private class PayBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IMAction.PAY_BY_WECHAT_RESULT.equals(intent.getAction())) {
                String wxpay = intent.getStringExtra(HTConstant.KEY_PAY_WECHAT);
                switch (wxpay) {
                    case "0":
                        transferMoney(money,userId,content,null);
                        break;
                    case "-1":
                        CommonUtils.showToastShort(context, R.string.payment_failed);
                        rootLayout.setVisibility(View.VISIBLE);
                        break;
                    case "-2":
                        CommonUtils.showToastShort(context, R.string.payment_failed);
                        rootLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(receiver);
        }
        super.onDestroy();
    }
}
