package com.htmessage.yichat.acitivity.main.wallet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.utils.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2019/1/30.
 * qq 84543217
 */
public class BankCardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_bankpay);
        setTitle("银行卡充值");
        final EditText et_bankname=this.findViewById(R.id.et_bank_name);
        final EditText etCardNo=this.findViewById(R.id.et_card_num);
        final EditText etMoney=this.findViewById(R.id.et_money);
        final EditText etName=this.findViewById(R.id.et_name);
        Button btnOk=this.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bankName=et_bankname.getText().toString().trim();
                String cardNo=etCardNo.getText().toString().trim();

                String money=etMoney.getText().toString().trim();

                String name=etName.getText().toString().trim();
                if(TextUtils.isEmpty(bankName)||TextUtils.isEmpty(cardNo)||TextUtils.isEmpty(money)||TextUtils.isEmpty(name)){
                    Toast.makeText(BankCardActivity.this,"所有信息不可为空",Toast.LENGTH_SHORT).show();
                    return;


                }

                uploadServer(bankName,cardNo,money,name);

            }
        });

    }

    private void uploadServer(String bankName,String cardNo,String money,String name){
        List<Param> paramList=new ArrayList<>();
        paramList.add(new Param("userId", HTApp.getInstance().getUsername()));
        paramList.add(new Param("back",bankName));
        paramList.add(new Param("card",cardNo));
        paramList.add(new Param("money", money));
        paramList.add(new Param("name", name));

        new OkHttpUtils(BankCardActivity.this).post(paramList, HTConstant.CARD_PAY, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code =jsonObject.getInteger("code");
                if(code==1){
                    Toast.makeText(BankCardActivity.this,"提交成功，等待审核入账",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BankCardActivity.this,"提交失败",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(BankCardActivity.this,"提交失败",Toast.LENGTH_SHORT).show();

            }
        });
    }
}
