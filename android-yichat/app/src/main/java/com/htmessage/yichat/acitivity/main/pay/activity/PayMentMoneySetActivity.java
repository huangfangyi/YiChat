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
import android.widget.TextView;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.Validator;

/**
 * 项目名称：hanxuan
 * 类描述：PayMentMoneySetActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/27 17:09
 * 邮箱:814326663@qq.com
 */
public class PayMentMoneySetActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_title, tv_modify_remarks, tv_add_remarks;
    private EditText cet_set_money;
    private ImageView iv_clear;
    private Button btn_next;
    private String message = null;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_pay_ment_money_set);
        getData();
        initView();
        initData();
        setListener();
    }

    private void getData() {

    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_modify_remarks = (TextView) findViewById(R.id.tv_modify_remarks);
        tv_add_remarks = (TextView) findViewById(R.id.tv_add_remarks);
        cet_set_money = (EditText) findViewById(R.id.cet_set_money);
        iv_clear = (ImageView) findViewById(R.id.iv_clear);
        btn_next = (Button) findViewById(R.id.btn_next);
    }

    private void initData() {
        tv_title.setText(R.string.set_pay_money);
    }

    private void setListener() {
        iv_clear.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        tv_add_remarks.setOnClickListener(this);
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
                if (s.toString().contains(".") && ((CharSequence) s).length() - 1 - ((CharSequence) s).toString().indexOf(".") > 2) {
                    s = ((CharSequence) s).toString().subSequence(0, ((CharSequence) s).toString().indexOf(".") + 3);
                    cet_set_money.setText((CharSequence) s);
                    cet_set_money.setSelection(((CharSequence) s).length());
                }

                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    cet_set_money.setText((CharSequence) s);
                    cet_set_money.setSelection(2);
                }
                if (s.toString().startsWith("0") && ((CharSequence) s).toString().trim().length() > 1 && !((CharSequence) s).toString().substring(1, 2).equals(".")) {
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear:
                cet_set_money.getText().clear();
                break;
            case R.id.tv_add_remarks:
                CommonUtils.showPayMentMessageInputDialog(PayMentMoneySetActivity.this, message, new CommonUtils.OnReChargeDialogClickListener() {
                    @Override
                    public void onPriformClock(String money) {
                        message = money;
                        showMsg(money);
                    }

                    @Override
                    public void onCancleClock() {
                        showMsg(message);
                    }
                });
                break;
            case R.id.btn_next:
                String money = cet_set_money.getText().toString().trim();
                if (TextUtils.isEmpty(money)) {
                    CommonUtils.showToastShort(getBaseContext(), R.string.input_money);
                    return;
                }
                if (Validator.formatMoneyFloat(money) > Validator.formatMoneyFloat(1000)){
                    CommonUtils.showToastShort(getBaseContext(),R.string.payment_more);
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("money", money);
                intent.putExtra("message", message);
                setResult(RESULT_OK, intent);
                finish();
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

}
