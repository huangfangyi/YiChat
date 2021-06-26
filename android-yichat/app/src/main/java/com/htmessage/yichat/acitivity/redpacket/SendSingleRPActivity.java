package com.htmessage.yichat.acitivity.redpacket;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.red.pay.RpPayActivity;

/**
 * Created by huangfangyi on 2019/8/14.
 * qq 84543217
 */
public class SendSingleRPActivity extends BaseActivity {
    private TextView tvNotice;
    private EditText etMoney;
    private EditText etContent;
    private TextView tvShow;
    private Button btnSummit;
    private String userId;
    private int chatType = 2;
    private int num = 1;
    private EditText etNum;
    private TextView tvNotice2;


    @Override
    protected void onCreate(final Bundle arg0) {
        super.onCreate(arg0);
        chatType = getIntent().getIntExtra("chatType", 2);
        if (chatType == 1) {
            setContentView(R.layout.activity_send_single_rp);
        } else {
            setContentView(R.layout.activity_send_group_rp);
            etNum = findViewById(R.id.et_num);
            tvNotice2 = findViewById(R.id.tv_notice2);

            etNum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!TextUtils.isEmpty(s)) {
                        String num = s.toString();


                        if (Integer.valueOf(num) > 30) {
                            tvNotice2.setText("一次最多可发30个红包");
                            tvNotice2.setVisibility(View.VISIBLE);
                            //  btnSummit.setEnabled(false);
                            return;
                        }

//                        if (Integer.valueOf(num).equals(0)) {
//                            btnSummit.setEnabled(false);
//                            return;
//                        }
//                        //判断下金额是否正确
//                        String money=etMoney.getText().toString();
//                        if(!TextUtils.isEmpty(money)&&!(Double.valueOf(money)>200)&&Double.valueOf(money)>0){
//
//                            btnSummit.setEnabled(true);
//                        }
                        tvNotice2.setVisibility(View.GONE);
                    }
//                    else {
//                        btnSummit.setEnabled(false);
//                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }


        setTitle("发红包");
        userId = getIntent().getStringExtra("userId");
        tvNotice = this.findViewById(R.id.tv_notice);
        etMoney = this.findViewById(R.id.et_money);
        etContent = this.findViewById(R.id.et_content);
        tvShow = this.findViewById(R.id.tv_show_money);
        btnSummit = this.findViewById(R.id.btn_inset_money);
        tvShow.setText("￥0.00");
        etMoney.setFilters(new InputFilter[]{new InputMoneyFilter()});
      //  btnSummit.setEnabled(false);
        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    String money = s.toString();
                    if (chatType == 1) {

                        if (Double.valueOf(money) > 200) {
                            tvNotice.setText(R.string.red_notice_1);
                            tvNotice.setVisibility(View.VISIBLE);
                          //  btnSummit.setEnabled(false);
                            return;
                        }
                    } else {
                        if (Double.valueOf(money) > 6000) {
                            tvNotice.setText(R.string.red_notice_3);
                            tvNotice.setVisibility(View.VISIBLE);
                           // btnSummit.setEnabled(false);
                            return;
                        }

                    }
//                    if (Double.valueOf(money).equals((double) 0)) {
//                        btnSummit.setEnabled(false);
//                        return;
//                    }
//                    if(chatType==2){
//                        String num=etNum.getText().toString();
//                        if(!TextUtils.isEmpty(num)&&Integer.valueOf(num)>0&&Integer.valueOf(num)<31){
//                             btnSummit.setEnabled(true);
//                        }
//                    }else {
//
//                        btnSummit.setEnabled(true);
//                    }
                    tvNotice.setVisibility(View.GONE);

                }
//                else {
//                    btnSummit.setEnabled(false);
//                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    String money = s.toString();
                    while (money.startsWith("0") && money.length() > 1 && !".".equals(String.valueOf(money.charAt(1)))) {
                        money = money.substring(1);
                    }

                    tvShow.setText("￥" + money);
                } else {
                    tvShow.setText("￥" + "0.00");
                    //  btnSummit.setEnabled(false);
                }

            }
        });

        btnSummit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String money = tvShow.getText().toString();
                money = money.substring(1);
                if (Double.valueOf(money).equals((double) 0)) {
                    Toast.makeText(SendSingleRPActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();

                }
                if (chatType == 2) {
                    if(TextUtils.isEmpty(etNum.getText().toString())){
                        return;
                    }

                    num = Integer.valueOf(etNum.getText().toString());
                    if (Double.valueOf(money) > 6000) {
                        Toast.makeText(SendSingleRPActivity.this, R.string.red_notice_3, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (num < 1) {

                        Toast.makeText(SendSingleRPActivity.this, "请输入红包份数", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (num > 30) {

                    Toast.makeText(SendSingleRPActivity.this, "一次最多可发30个红包", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Double.valueOf(money).equals((double) 0)) {
                    Toast.makeText(SendSingleRPActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                double single = Double.valueOf(money) / num;
                if (single > 200) {
                    Toast.makeText(SendSingleRPActivity.this, R.string.red_notice_1, Toast.LENGTH_SHORT).show();
                    return;
                } else if (single < 0.01) {
                    Toast.makeText(SendSingleRPActivity.this, R.string.red_notice_2, Toast.LENGTH_SHORT).show();
                    return;
                }


                String content = etContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    content = getString(R.string.words_numal);
                }
                Intent intent = new Intent(SendSingleRPActivity.this, RpPayActivity.class);
                intent.putExtra("money", money);
                intent.putExtra("content", content);
                intent.putExtra("rundom", num);
                intent.putExtra("chatType", chatType);
                intent.putExtra("userId", userId);
                startActivityForResult(intent, 1000);


//                 startActivityForResult(new Intent(SendSingleRPActivity.this, RpPayActivity.class)
//                         .putExtra("money",money)
//                         .putExtra("content",content)
//                         ,1000);


            }
        });

    }


    private class InputMoneyFilter implements InputFilter {
        private InputMoneyFilter() {
        }

        public CharSequence filter(CharSequence var1, int var2, int var3, Spanned var4, int var5, int var6) {
            if (var1.toString().equals(".") && var5 == 0 && var6 == 0) {
                etMoney.setText("0" + var1 + var4);
                etMoney.setSelection(2);
            }
            return var5 >= 8 ? "" : (var4.toString().indexOf(".") != -1 && var4.length() - var4.toString().indexOf(".") > 2 && var4.length() - var5 < 3 ? "" : null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1000) {
            if (data != null) {
                setResult(RESULT_OK, data);
                finish();
            }
        } else if (resultCode == RESULT_CANCELED) {
//            CommonUtils.showToastShort(getBaseContext(),"支付取消");
//            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
