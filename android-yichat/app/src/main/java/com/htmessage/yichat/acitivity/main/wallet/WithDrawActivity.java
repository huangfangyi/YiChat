package com.htmessage.yichat.acitivity.main.wallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

public class WithDrawActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_check;
    private      String[] items3 ;
    private int currentIndex=0;
    private TextView tv_yu_e,tv_note;
    private EditText et_money;
    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    int resId=msg.arg1;
                    Toast.makeText(WithDrawActivity.this,resId,Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 1001:
                    int resId1=msg.arg1;
                    Toast.makeText(WithDrawActivity.this,resId1,Toast.LENGTH_SHORT).show();
                    break;
                case 1002:
                    tv_note.setText(WalletUtils.getInstance().getWithDrawConfig().getString("text"));
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_withdraw);
        this.findViewById(R.id.tv_add).setOnClickListener(this);
        tv_check=this.findViewById(R.id.tv_check);
        tv_yu_e=this.findViewById(R.id.yu_e);
        tv_note=this.findViewById(R.id.tv_note);
        tv_check.setOnClickListener(this);
        this.findViewById(R.id.btn_ok).setOnClickListener(this);
        et_money=this.findViewById(R.id.et_money);
        //防止科学计数
        tv_yu_e.setText("余额:"+   WalletUtils.getInstance().getBalance()+"");
        getCards();
        showRightTextView("提现明细", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WithDrawActivity.this,WithDrawListActivity.class));
            }
        });
        cards=WalletUtils.getInstance().getBankCardList();
        getWithdrawInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_add:
                startActivityForResult(new Intent(WithDrawActivity.this,PreAddBandCardActivity.class),1000);
                break;
            case R.id.tv_check:

                showBankCardList();
                break;
            case R.id.btn_ok:
                uploadServer();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==1000){
                getCards();

            }
        }

    }

    private      JSONArray cards;
    private void getCards(){
        JSONObject body=new JSONObject();
        ApiUtis.getInstance().postJSON(body, Constant.URL_bankcard_list, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    cards=jsonObject.getJSONArray("data");
                    handleCards(cards);
                    WalletUtils.getInstance().saveBankCardList(cards);

                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });




    }

    private void    handleCards(JSONArray cards){
        if(cards==null){
            items3=new String[0];
            return;

        }
        items3=new String[cards.size()];
        for(int i=0;i<cards.size();i++){
            JSONObject jsonObject=cards.getJSONObject(i);
            String bank=jsonObject.getString("bankName");
            String bankNo=jsonObject.getString("bankNumber");
            if(bankNo==null){
                bankNo="";
            }
            if(bank==null){
                bank="";
            }
            if(bankNo.length()>4){
                bankNo=bankNo.substring(bankNo.length()-4,bankNo.length());
            }
            String show=bank+"("+bankNo+")";
            items3[i]=show;

        }


    }


    private void showBankCardList(){
        if(cards==null||cards.size()==0){

            Toast.makeText(WithDrawActivity.this,"请绑定银行卡",Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog alertDialog3 = new AlertDialog.Builder(this)
//                .setTitle("选择您喜欢的老湿")
//                .setIcon(R.mipmap.ic_launcher)
                .setItems(items3, new DialogInterface.OnClickListener() {//添加列表
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currentIndex=i;
                        tv_check.setText(items3[i]);

                    }
                })
                .create();
        alertDialog3.show();


    }
    private void uploadServer(){
        final String money=et_money.getText().toString().trim();
        if(TextUtils.isEmpty(money)){
            Toast.makeText(WithDrawActivity.this,"金额不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv_check.getText().toString().equals("选取提现银行卡")){
            Toast.makeText(WithDrawActivity.this,"请选择提现的银行",Toast.LENGTH_SHORT).show();
            return;
        }
        Double realMoney=0.00;
        try {
            double minMoney=WalletUtils.getInstance().getWithDrawConfig().getDouble("minLimit");
            Log.d("minMoney--->1",minMoney+"");
            double moneyInt=Double.parseDouble(money);
            if ( moneyInt< minMoney){
                Toast.makeText(WithDrawActivity.this,"最低提现金额为"+minMoney+"元",Toast.LENGTH_SHORT).show();
                return;
            }
            double rate=WalletUtils.getInstance().getWithDrawConfig().getDouble("rate");
            realMoney=(moneyInt)*(1-rate/100);


        }catch (NumberFormatException e){
            Log.d("minMoney--->2",e.getMessage()+"");

        }

        try {
            double moneyDouble=Double.parseDouble(money);
            double currentMoney=WalletUtils.getInstance().getBalance();
            if(moneyDouble>currentMoney){
                Toast.makeText(WithDrawActivity.this,"余额不足",Toast.LENGTH_SHORT).show();
                return;
            }
            final String cardNo=cards.getJSONObject(currentIndex).getString("bankNumber");
            if(cardNo==null){
                Toast.makeText(WithDrawActivity.this,"银行卡信息出错",Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(WithDrawActivity.this);
            builder.setTitle("提现");
            builder.setMessage("提现成功实际到账"+String.format("%.2f", realMoney)+"元");
            //  builder.setIcon(R.mipmap.ic_launcher_round);
            //点击对话框以外的区域是否让对话框消失
            builder.setCancelable(true);
            //设置正面按钮
            builder.setPositiveButton("继续提现", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    withdrawInServer(money,cardNo);
                    dialog.dismiss();
                }
            });
            //设置反面按钮
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();


        }catch (NumberFormatException e){
            Toast.makeText(WithDrawActivity.this,"金额请输入数字",Toast.LENGTH_SHORT).show();
            return;
        }




    }

    private void getWithdrawInfo(){

        JSONObject body=new JSONObject();
        ApiUtis.getInstance().postJSON(body, Constant.URL_WITHDRAW_CONFIG, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    JSONObject data=jsonObject.getJSONObject("data");
                    WalletUtils.getInstance().setWithDrawConfig(data);
                    Message message=handler.obtainMessage();
                    message.what=1002;
                    message.obj=data;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });

    }


    private void withdrawInServer(String money,String cardNo){
        EditText etMemo=findViewById(R.id.et_memo);
        String memo=etMemo.getText().toString().trim();

        JSONObject body=new JSONObject();
        body.put("money",money);
        body.put("bankNumber",cardNo);
        body.put("memo","用户提现-Android端");
        if(!TextUtils.isEmpty(memo)){
            body.put("memo",memo);

        }
        ApiUtis.getInstance().postJSON(body, Constant.URL_withdraw_apply, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null){
                    return;
                }
                String code=jsonObject.getString("code");
                if("0".equals(code)){


                    JSONObject data=jsonObject.getJSONObject("data");
                    if(data!=null&&data.containsKey("balance")){
                        Double balance=data.getDouble("balance");
                        WalletUtils.getInstance().saveBalance(balance);
                    }

                    Message message=handler.obtainMessage();
                    message.what=1000;
                    message.arg1=R.string.withdraw_succ;
                    message.sendToTarget();
                }else {
                    Message message=handler.obtainMessage();
                    message.what=1001;
                    message.arg1=R.string.withdraw_fail;
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
//        params.add(new Param("money", money));
//        params.add(new Param("bankcard", cardNo));
//        new OkHttpUtils(getBaseContext()).post(params, HTConstant.REQUEST_WITHDRAW, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        //JSONObject data = jsonObject.getJSONObject("data");
//                        Toast.makeText(WithDrawActivity.this,"提现申请成功",Toast.LENGTH_LONG).show();
//                        finish();
//                        break;
//                    default:
//                        Toast.makeText(WithDrawActivity.this,"提现申请失败",Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                Toast.makeText(WithDrawActivity.this,"提现申请失败",Toast.LENGTH_SHORT).show();
//
//            }
//        });
//


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler=null;
    }
}
