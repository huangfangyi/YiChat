package com.htmessage.yichat.acitivity.main.pay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.utils.Param;
import com.htmessage.yichat.utils.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：hanxuan
 * 类描述：QrCodePayMentActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/28 10:52
 * 邮箱:814326663@qq.com
 */
public class QrCodePayMentActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title, tv_nickname, tv_modify_remarks, tv_add_remarks, tv_money, tv_remarks;
    private ImageView iv_header, iv_clear;
    private EditText cet_set_money;
    private Button btn_next;
    private LinearLayout lly_no_money, lly_have_money;
    private String nick, avatar, userId;
    private String money = "0.00";
    private String msg = "";
    private int QRCODE_PAY_REQUEST_CODE = 40;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_qrcode_payment);
        getData();
        initView();
        initData();
        setListener();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_modify_remarks = (TextView) findViewById(R.id.tv_modify_remarks);
        tv_add_remarks = (TextView) findViewById(R.id.tv_add_remarks);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_remarks = (TextView) findViewById(R.id.tv_remarks);
        iv_header = (ImageView) findViewById(R.id.iv_header);
        iv_clear = (ImageView) findViewById(R.id.iv_clear);
        cet_set_money = (EditText) findViewById(R.id.cet_set_money);
        btn_next = (Button) findViewById(R.id.btn_next);
        lly_have_money = (LinearLayout) findViewById(R.id.lly_have_money);
        lly_no_money = (LinearLayout) findViewById(R.id.lly_no_money);
    }

    private void initData() {
        tv_title.setText(R.string.transfer_qrcode);
        if (TextUtils.isEmpty(nick)) {
            nick = userId;
        }
        if (TextUtils.isEmpty(avatar)) {
            avatar = "false";
        }
        tv_nickname.setText(nick);
        UserManager.get().loadUserAvatar(getBaseContext(), avatar, iv_header);
        showMoney(money);
        showMsg(msg);
    }

    private void setListener() {
        btn_next.setOnClickListener(this);
        tv_add_remarks.setOnClickListener(this);
        iv_clear.setOnClickListener(this);
        cet_set_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    iv_clear.setVisibility(View.VISIBLE);
                } else {
                    iv_clear.setVisibility(View.GONE);
                }
                if (((CharSequence) s).toString().contains(".") && ((CharSequence) s).length() - 1 - ((CharSequence) s).toString().indexOf(".") > 2) {
                    s = ((CharSequence) s).toString().subSequence(0, ((CharSequence) s).toString().indexOf(".") + 3);
                    cet_set_money.setText((CharSequence) s);
                    cet_set_money.setSelection(((CharSequence) s).length());
                }

                if (((CharSequence) s).toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    cet_set_money.setText((CharSequence) s);
                    cet_set_money.setSelection(2);
                }

                if (((CharSequence) s).toString().startsWith("0") && ((CharSequence) s).toString().trim().length() > 1 && !((CharSequence) s).toString().substring(1, 2).equals(".")) {
                    cet_set_money.setText(((CharSequence) s).subSequence(0, 1));
                    cet_set_money.setSelection(1);
                }
                String input = cet_set_money.getText().toString().trim();
                if (Validator.formatMoneyFloat(input) > Validator.formatMoneyFloat(1000)){
                    CommonUtils.showToastShort(getBaseContext(),R.string.payment_more);
                    CommonUtils.deleteChar(cet_set_money);
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getData() {
        String data = getIntent().getStringExtra("data");
        JSONObject object = JSONObject.parseObject(data);
        nick = object.getString("nick");
        userId = object.getString("userId");
        avatar = object.getString("avatar");
        money = object.getString("money");
        msg = object.getString("content");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear:
                cet_set_money.getText().clear();
                break;
            case R.id.tv_add_remarks:
                CommonUtils.showPayMentMessageInputDialog(QrCodePayMentActivity.this, msg, new CommonUtils.OnReChargeDialogClickListener() {
                    @Override
                    public void onPriformClock(String money) {
                        msg = money;
                        showMsg(money);
                    }

                    @Override
                    public void onCancleClock() {
                        showMsg(msg);
                    }
                });
                break;
            case R.id.btn_next:
                if (HTApp.getInstance().getUsername().equals(userId)) {
                    CommonUtils.showToastShort(getBaseContext(), R.string.qrcode_pay_dot_self);
                    return;
                }
                String paymoney = getMoney();
                if ("0.00".equals(Validator.formatMoney(paymoney)) || TextUtils.isEmpty(paymoney)) {
                    CommonUtils.showToastShort(getBaseContext(), R.string.trans_little_tip);
                    return;
                }
                if (Validator.formatMoneyFloat(paymoney) > Validator.formatMoneyFloat(1000)){
                    CommonUtils.showToastShort(getBaseContext(),R.string.payment_more);
                    return;
                }
                 Intent intent = new Intent(QrCodePayMentActivity.this, PayMentPayActivity.class);
                intent.putExtra(HTConstant.JSON_KEY_USERID, userId);
                intent.putExtra("money", paymoney);
                intent.putExtra("content", msg);
                startActivityForResult(intent, QRCODE_PAY_REQUEST_CODE);
                break;

        }
    }

    private void showMsg(String message) {
        if (!TextUtils.isEmpty(message)) {
            tv_modify_remarks.setText(message);
            tv_add_remarks.setText(R.string.change_msg);
            tv_modify_remarks.setVisibility(View.VISIBLE);
        } else {
            tv_modify_remarks.setText("");
            tv_add_remarks.setText(R.string.pay_ment_message);
            tv_modify_remarks.setVisibility(View.GONE);
        }
    }

    private void showMoney(String money) {
        if (TextUtils.isEmpty(money) || "0.00".equals(money)) {
            lly_have_money.setVisibility(View.GONE);
            lly_no_money.setVisibility(View.VISIBLE);
        } else {
            tv_money.setText(Validator.formatMoney(money));
            lly_have_money.setVisibility(View.VISIBLE);
            lly_no_money.setVisibility(View.GONE);
        }
    }

    private String getMoney() {
        String moeny = cet_set_money.getText().toString().trim();
        return TextUtils.isEmpty(moeny) ? money : moeny;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == QRCODE_PAY_REQUEST_CODE) {
            CommonUtils.showToastShort(getBaseContext(), R.string.city_pay_succ);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void transferMoney(String money) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("userId", HTApp.getInstance().getUsername()));
        params.add(new Param("toUserId", userId));
        params.add(new Param("money", money));
        params.add(new Param("remarks", msg));
        new OkHttpUtils(getBaseContext()).post(params, HTConstant.TRANSFER_MONEY, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });
    }
}
