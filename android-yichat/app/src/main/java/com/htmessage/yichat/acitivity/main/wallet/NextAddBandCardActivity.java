package com.htmessage.yichat.acitivity.main.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.BankCardUtils;

/**
 * Created by huangfangyi on 2019/6/4.
 * qq 84543217
 */
public class NextAddBandCardActivity extends BaseActivity {
    private EditText etBankNo;
    private TextView  tvBankName;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_next_addbandcard);

        setTitle("添加银行卡");
        etBankNo=findViewById(R.id.et_bandcard);
        tvBankName=findViewById(R.id.tv_bankname);
        etBankNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>6){
                    String cardNo6= (String) s.toString().subSequence(0,6);

                    tvBankName.setText( BankCardUtils.getname(cardNo6));
                }else {
                    tvBankName.setText("");
                }
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bankNo=etBankNo.getText().toString().trim();
                String bankName=tvBankName.getText().toString().trim();
                if(TextUtils.isEmpty(bankName)||TextUtils.isEmpty(bankNo)){
                    Toast.makeText(getApplicationContext(),"请输入正确的银行的账号",Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(NextAddBandCardActivity.this,AddBankCardActivity.class).putExtra("bankNo",bankNo).putExtra("bankName",bankName));
                finish();
            }
        });


    }
}
