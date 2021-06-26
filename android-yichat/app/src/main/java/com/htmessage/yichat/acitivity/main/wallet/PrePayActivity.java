package com.htmessage.yichat.acitivity.main.wallet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.main.pay.utils.PayUtils;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.widget.HTAlertDialog;

/**
 * Created by huangfangyi on 2019/3/11.
 * qq 84543217
 */
public class PrePayActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_prepay);
        setTitle("充值");
        final EditText etMoney=this.findViewById(R.id.et_money);
        Button btnPay=this.findViewById(R.id.btn_pay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String money=etMoney.getText().toString().trim();
                if(TextUtils.isEmpty(money)){
                    Toast.makeText(PrePayActivity.this,"请输入充值金额",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isNumber(money)||!(Double.parseDouble(money)>0)){
                    Toast.makeText(PrePayActivity.this,"请输入正确的金额",Toast.LENGTH_SHORT).show();
                    return;
                }
                //CommonUtils.showDialogNumal(PrePayActivity.this,"");
               // payInServer(money);

                new HTAlertDialog(PrePayActivity.this,"选择支付方式",new String[]{"微信支付","支付宝支付"}).init(new HTAlertDialog.OnItemClickListner() {
                    @Override
                    public void onClick(int position) {
                        switch (position){

                            case 0:
                                wxPayInServer(money);
                                break;
                            case 1:
                                aliPayInServer(money);
                                break;
                        }
                    }
                });

            }
        });


        receiver = new PayBroadcastReceiver();
        IntentFilter fileter = new IntentFilter();
        fileter.addAction(IMAction.PAY_BY_WECHAT_RESULT);
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(receiver, fileter);
    }

private  PayBroadcastReceiver receiver;
    public static boolean isNumber(String str)
    {
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private void  aliPayInServer(String money){
//
        PayUtils.payByAliPay(PrePayActivity.this, money,0, new PayUtils.PayBackListener() {
            @Override
            public void paySuccess() {
                Toast.makeText(getApplicationContext(),"充值成功",Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void payFailed() {
                Toast.makeText(getApplicationContext(),"充值失败",Toast.LENGTH_SHORT).show();
              //  finish();
            }

            @Override
            public void payCancled() {
                Toast.makeText(getApplicationContext(),"充值取消",Toast.LENGTH_SHORT).show();

            }
        });

//        List<Param> params=new ArrayList<>();
//        params.add(new Param("money",money));
//        params.add(new Param("userId",HTApp.get().getUserId()));
//
//         new OkHttpUtils(PrePayActivity.this).postJSON(params, HTConstant.URL_GET_PAY, new OkHttpUtils.HttpCallBack() {
//             @Override
//             public void onResponse(JSONObject jsonObject) {
//                 CommonUtils.cencelDialog();
//                 Log.d("jsonObject---->",jsonObject.toJSONString());
//                 int code=jsonObject.getInteger("code");
//                 if(code==1){
//                     String url=jsonObject.getString("payurl");
//                     startActivity(new Intent(PrePayActivity.this, WebViewActivity.class).putExtra("title", "充值").putExtra("url", url));
//
//                 }else {
//
//                     Toast.makeText(PrePayActivity.this,"充值请求失败",Toast.LENGTH_SHORT).show();
//                 }
//
//             }
//
//             @Override
//             public void onFailure(String errorMsg) {
//                 CommonUtils.cencelDialog();
//                 Toast.makeText(PrePayActivity.this,"充值请求失败",Toast.LENGTH_SHORT).show();
//
//
//             }
//         });
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    private void  wxPayInServer(String money){
//
        PayUtils.payByWeChat(PrePayActivity.this, money,0);

//        List<Param> params=new ArrayList<>();
//        params.add(new Param("money",money));
//        params.add(new Param("userId",HTApp.get().getUserId()));
//
//         new OkHttpUtils(PrePayActivity.this).postJSON(params, HTConstant.URL_GET_PAY, new OkHttpUtils.HttpCallBack() {
//             @Override
//             public void onResponse(JSONObject jsonObject) {
//                 CommonUtils.cencelDialog();
//                 Log.d("jsonObject---->",jsonObject.toJSONString());
//                 int code=jsonObject.getInteger("code");
//                 if(code==1){
//                     String url=jsonObject.getString("payurl");
//                     startActivity(new Intent(PrePayActivity.this, WebViewActivity.class).putExtra("title", "充值").putExtra("url", url));
//
//                 }else {
//
//                     Toast.makeText(PrePayActivity.this,"充值请求失败",Toast.LENGTH_SHORT).show();
//                 }
//
//             }
//
//             @Override
//             public void onFailure(String errorMsg) {
//                 CommonUtils.cencelDialog();
//                 Toast.makeText(PrePayActivity.this,"充值请求失败",Toast.LENGTH_SHORT).show();
//
//
//             }
//         });
    }


    /**
     * 微信支付监听
     */
    private class PayBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IMAction.PAY_BY_WECHAT_RESULT.equals(intent.getAction())) {
                String wxpay = intent.getStringExtra(HTConstant.KEY_PAY_WECHAT);
                switch (wxpay) {
                    case "0":
                        Toast.makeText(getApplicationContext(),"充值成功",Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case "-1":
                        CommonUtils.showToastShort(context, R.string.pay_failed);

                        break;
                    case "-2":
                        Log.d("PayBroadcastReceiver---","PayBroadcastReceiver");
                        CommonUtils.showToastShort(context, R.string.pay_cancle);

                        break;
                }
            }
        }
    }


}
