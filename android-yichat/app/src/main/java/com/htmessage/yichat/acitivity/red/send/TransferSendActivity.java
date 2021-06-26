package com.htmessage.yichat.acitivity.red.send;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.red.pay.RpPayActivity;
import com.htmessage.update.data.UserManager;
import com.htmessage.sdk.utils.MessageUtils;

public class TransferSendActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.acitivity_transfer);


        getData();
        initView();
        initData();
        setListener();


    }


//    private TextView tv_show_money, , tv_money_size, tv_notice, tv_input_money, tv_show_input_msg;
//    private EditText edt_input_message, edt_input_money;
//    private Button btn_inset_money;
//    private String userId;
//    private String msg = null;
//    boolean isTransfer=false;//红包false 转账true

   private EditText etMoney;
    private Button btnTransfer;
    private TextView tvRemark,tvRemarkSet;

    private TextView tvNick;
    private ImageView ivAvatar;

   

    private void setListener() {
        tvRemarkSet.setOnClickListener(this);
        btnTransfer.setOnClickListener(this);

    }

    private void initData() {

            tvNick.setText(UserManager.get().getUserNick(userId));
        UserManager.get().loadUserAvatar(this,UserManager.get().getUserAvatar(userId),ivAvatar);



         etMoney.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    private void initView() {
        tvRemark=findViewById(R.id.tv_remark);
        tvRemarkSet=findViewById(R.id.tv_set_remark);
        etMoney=findViewById(R.id.et_money);
        btnTransfer=findViewById(R.id.btn_transfer);
        tvNick=findViewById(R.id.tv_nick);
        ivAvatar=findViewById(R.id.iv_avatar);


    }

    private String userId;
    private void getData() {


        userId =  getIntent().getStringExtra(HTConstant.JSON_KEY_USERID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_transfer:
                String money = etMoney.getText().toString().trim();
                String input = tvRemark.getText().toString().trim();
//                if (TextUtils.isEmpty(money) || getString(R.string.money_numal).equals(Validator.formatMoney(money))) {
//                    showNotice(getString(R.string.money_is_no_0));
//                    return;
//                }
//                if (TextUtils.isEmpty(input)) {
//                    input = msg;
//                }
//                try{
//                    double moneyDoube = Double.parseDouble(money);
//                    double balance= MmvkManger.getIntance().getDouble(HTApp.get().getUserId()+"balance");
//                    if(moneyDoube>balance){
//                        showNotice("余额不足，请降低金额发送");
//                        return;
//                    }
//
//                }catch (NumberFormatException e){
//
//                }

                 Intent intent = new Intent(this, RpPayActivity.class);
                intent.putExtra("money", money);
                intent.putExtra("content", input);
                intent.putExtra("rundom", "1");
                intent.putExtra("chatType", MessageUtils.CHAT_SINGLE);
                intent.putExtra("isTransfer", true);
                intent.putExtra(HTConstant.JSON_KEY_USERID, userId);
                this.startActivityForResult(intent, 1000);
                break;
            case R.id.tv_set_remark:

                showSetDialog(tvRemark.getText().toString().trim());

                break;
        }
    }


    private void showSetDialog(String preContent){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(this,R.layout.widget_remark_transfer,null);
        builder.setView(view);
        final AlertDialog alertDialog =builder.show();

        Button tvOk=view.findViewById(R.id.btn_ok);
          Button tvCancel=view.findViewById(R.id.btn_cancel);
        final EditText editText=view.findViewById(R.id.et_remarks);
        if(preContent!=null){
            editText.setText(preContent);
        }
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=editText.getText().toString().trim();
                if(!TextUtils.isEmpty(content)){
                    tvRemark.setVisibility(View.VISIBLE);
                    tvRemark.setText(content);
                    tvRemarkSet.setText("修改");
                }else {
                    tvRemark.setText("");
                    tvRemark.setVisibility(View.GONE);
                    tvRemarkSet.setText("添加转账说明");
                }

                alertDialog.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode ==RESULT_OK && requestCode ==1000){
            if (data !=null){
                setResult(RESULT_OK,data);
                finish();
            }
        }else if (resultCode == RESULT_CANCELED){
//            CommonUtils.showToastShort(getBaseContext(),"支付取消");
//            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
