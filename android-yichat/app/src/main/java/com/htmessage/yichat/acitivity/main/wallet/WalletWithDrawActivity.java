package com.htmessage.yichat.acitivity.main.wallet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.LoggerUtils;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.utils.Param;
import com.htmessage.yichat.utils.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：hanxuan
 * 类描述：WalletWithDrawActivity 描述:
 * 创建人：songlijie
 * 创建时间：2018/2/2 14:04
 * 邮箱:814326663@qq.com
 */
public class WalletWithDrawActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_title;
    private EditText edt_money, edt_name, edt_number;
    private Button btn_withdraw;
    private RelativeLayout rl_number;
    private int type = 1;
    private String openid = "";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_wallet_withdraw);
        getData();
        initView();
        initData();
        setListener();
    }

    private void getData() {
        type = getIntent().getIntExtra("type", 1);
        if (type == 2) {
            openid = getIntent().getStringExtra("openid");
            if (TextUtils.isEmpty(openid)) {
                finish();
                return;
            }
        }
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        edt_money = (EditText) findViewById(R.id.edt_money);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_number = (EditText) findViewById(R.id.edt_number);
        btn_withdraw = (Button) findViewById(R.id.btn_withdraw);
        rl_number = (RelativeLayout) findViewById(R.id.rl_number);
    }

    private void initData() {
        tv_title.setText(R.string.blance_withdraw);
        if (type != 1) {
            rl_number.setVisibility(View.GONE);
        } else {
            rl_number.setVisibility(View.VISIBLE);
        }
    }

    private void setListener() {
        btn_withdraw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_withdraw:
                final String amount = edt_money.getText().toString().trim();
                final String name = edt_name.getText().toString().trim();
                final String account = edt_number.getText().toString().trim();
                if (TextUtils.isEmpty(amount)) {
                    showToast(R.string.input_pay_ment_hint);
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    showToast(R.string.please_input_real_name);
                    return;
                }
                if (type == 1) {
                    if (TextUtils.isEmpty(account)) {
                        showToast(R.string.please_input_alipay_name);
                        return;
                    }
                }
                if (type == 2) {
                    double inputmoney = Validator.formatMoneyDouble(amount);
                    if (inputmoney < Validator.formatMoneyDouble("1")) {
                        showToast(R.string.withdraw_not_1);
                        return;
                    }
                }


                try {
                    if (Integer.parseInt(amount) < 0) {
                        showToast("请输入大于0的金额");

                        return;
                    }

                } catch (NumberFormatException e) {


                }


                if (type == 2) {
                    sendWxToService(name, openid, amount);
                } else {
                    sendAlipayToService(name, account, amount);
                }
                break;
        }
    }

    private void showToast(Object msg) {
        CommonUtils.showToastShort(getBaseContext(), msg);
    }

    /**
     * 发送阿里
     *
     * @param name
     * @param account
     * @param amount
     */
    private void sendAlipayToService(String name, String account, String amount) {
        CommonUtils.showDialogNumal(WalletWithDrawActivity.this, getString(R.string.applying));
        List<Param> params = new ArrayList<>();
        params.add(new Param("userId", HTApp.getInstance().getUsername()));
        params.add(new Param("name", name));
        params.add(new Param("account", account));
        params.add(new Param("amount", amount));
        new OkHttpUtils(this).post(params, HTConstant.WITHDRAW_BY_ALIPAY, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                LoggerUtils.e("---支付宝提现：" + jsonObject.toJSONString());
                CommonUtils.cencelDialog();
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        showToast(R.string.apply_success);
                        WalletWithDrawActivity.this.finish();
                        break;
                    case -1:
                        showToast(R.string.apply_failed);
                        break;
                    case -2:
                        showToast(R.string.amount_greater_than_wallet_amount);
                        break;
                    default:
                        showToast(R.string.unkonw_error);
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                showToast(R.string.unkonw_error);
                CommonUtils.cencelDialog();
            }
        });
    }

    /**
     * 发送微信
     *
     * @param name
     * @param openid
     * @param amount
     */
    private void sendWxToService(String name, String openid, String amount) {
        CommonUtils.showDialogNumal(WalletWithDrawActivity.this, getString(R.string.applying));
        List<Param> params = new ArrayList<>();
        params.add(new Param("userId", HTApp.getInstance().getUsername()));
        params.add(new Param("realname", name));
        params.add(new Param("amount", amount));
        params.add(new Param("openid", openid));
        new OkHttpUtils(this).post(params, HTConstant.WITHDRAW_BY_WECHAT, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                LoggerUtils.e("---微信提现：" + jsonObject.toJSONString());
                CommonUtils.cencelDialog();
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        showToast(R.string.apply_success);
                        WalletWithDrawActivity.this.finish();
                        break;
                    case -1:
                        showToast(R.string.apply_failed);
                        break;
                    case -2:
                        showToast(R.string.amount_greater_than_wallet_amount);
                        break;
                    default:
                        showToast(R.string.unkonw_error);
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                showToast(R.string.unkonw_error);
                CommonUtils.cencelDialog();
            }
        });
    }
}
