package com.fanxin.app.fx;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.fanxin.app.Constant;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.alipay.PayResult;
import com.fanxin.app.alipay.SignUtils;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AlipayMeActivity extends BaseActivity {
    // 商户PID
    public static final String PARTNER = "";
    // 商户收款账号
    public static final String SELLER = "";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMT86ZbyVWP6zrZOTuwg3zDui8vjJVSpJBFxSrm5wf2zLh2mX8bGE9P5FY+VbmDfJizd0PfHTsnZoOcIQM1rSkhl8FVf/Ve7W3WxOddBSNZK2c5CISyQiU3yoLKJOTtabwJ3o77NIGtH2l3IcSv1jL4cOpZc0Ph3cQEt+SAOpB8hAgMBAAECgYAyp+sVwwmMZVHE9cw70pQyjBVs/+N4quo4hg62RfuJ1wrz2vXkMsmkh/gwbTfuN1Qk04HKRfmP1KQY5Tls2btHAJwjTZojQKaJ/AjAgO26i7hIMDIkE6GQoHUPJKh0JFz+STfPybnRDtf0Cz8vcY9lDlRDsXL/VBfSL5LLhDJFJQJBAPtozPo1SeXMhgl8U38ppM9FUox8nHK1/roUQW1v7ao0WX60XYqfIvs2Hm+BpbeSFf1fWCictt3ZaCG4cDaZ/9MCQQDIlbjEXS2dgoA95i+QYryeL5OJIW/uluTrcrNXjPS46nYVBS1/qqnqpkzB3apaXzyX7rOFoM8BAS41mcK9mMC7AkEAsZcoJB58Yt2EWTL/cDYke12GoEJt6QHyO9OPHBUSl0Z/aWdTJFahST7DZRT50KBa9C3jglyhODkYu6kjaw6BjQJBAL6srO5lUQZo1rWAyngrk/efbFUwJvIcGNLEvz0brkrV/pfuyxiQSGPZ4B9uMxEjdTtMWvVNL1paH+4uE1QeEB8CQQDySp58Qk8AiLOoQHRYL3Gdu2RgqnUxMZIOHa8B/do8J4SSGyySEe5QULTiEPiNIZVItnxd5h/h5AtM+j5Q6vpG";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SDK_PAY_FLAG: {
                PayResult payResult = new PayResult((String) msg.obj);
                 
                // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                // String resultInfo = payResult.getResult();

                String resultStatus = payResult.getResultStatus();

                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    Toast.makeText(AlipayMeActivity.this, "支付成功",
                            Toast.LENGTH_SHORT).show();
                    recordInServer();

                } else {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        Toast.makeText(AlipayMeActivity.this, "支付结果确认中",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        Toast.makeText(AlipayMeActivity.this, "支付失败",
                                Toast.LENGTH_SHORT).show();

                    }
                }
                break;
            }

            default:
                break;
            }
        };
    };
    private EditText et_name;
    private EditText et_note;
    private EditText et_money;
    String name  ;
    String content  ;
    String money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipayme);
        initView();

    }

    private void initView() {

        // TextView tv_100 = (TextView) this.findViewById(R.id.tv_100);
        // TextView tv_50 = (TextView) this.findViewById(R.id.tv_50);
        // TextView tv_1 = (TextView) this.findViewById(R.id.tv_1);
        // TextView tv_10 = (TextView) this.findViewById(R.id.tv_10);
        // TextView tv_5 = (TextView) this.findViewById(R.id.tv_5);
        // tv_100.setOnClickListener(new MyListener(100));
        // tv_50.setOnClickListener(new MyListener(50));
        // tv_10.setOnClickListener(new MyListener(10));
        // tv_5.setOnClickListener(new MyListener(5));
        // tv_1.setOnClickListener(new MyListener(1));
        // TextView tv_other = (TextView) this.findViewById(R.id.tv_other);
        // tv_other.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // showMyDialog();
        // }
        //
        // });
        et_name= (EditText) this.findViewById(R.id.et_name);
        et_note= (EditText) this.findViewById(R.id.et_note);
        et_money= (EditText) this.findViewById(R.id.et_money);
        Button btn_pay = (Button) this.findViewById(R.id.btn_pay);
        Button btn_thanks = (Button) this.findViewById(R.id.btn_thanks);
        btn_pay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                money = et_money.getText().toString().trim();
                if (TextUtils.isEmpty(money)) {
                    Toast.makeText(getApplicationContext(), "请输入金额",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                pay(money);
            }

        });
        btn_thanks.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(AlipayMeActivity.this,AlipayMeListActivity.class));
            }

        });
    }

    // class MyListener implements OnClickListener {
    //
    // private int money;
    //
    // public MyListener(int money) {
    //
    // this.money = money;
    //
    // }
    //
    // @Override
    // public void onClick(View v) {
    // pay(String.valueOf(money));
    // }
    //
    // }

    private void recordInServer() {
        name=et_name.getText().toString().trim();
        content=et_note.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            name = "匿名";

        }

        if (TextUtils.isEmpty(content)) {
            content = "捐赠者未留言...";

        }

        
        Map<String, String> map = new HashMap<String, String>();
        
        map.put("name", name);
        map.put("content", content);
        map.put("money", money);
        LoadDataFromServer task = new LoadDataFromServer(
                AlipayMeActivity.this, Constant.URL_ALIPAYME, map);
        task.getData(new DataCallBack(){

            @Override
            public void onDataCallBack(JSONObject data) {
                  if(data==null){
                      Toast.makeText(getApplicationContext(), "服务器端登记失败，请联系作者", Toast.LENGTH_LONG).show();
                      return;
                  }            
                  int code=data.getIntValue("code");
                  if(code!=1){
                      
                      Toast.makeText(getApplicationContext(), "服务器端登记失败，请联系作者", Toast.LENGTH_LONG).show();
                  }
            }
            
        });

    }

    /**
     * call alipay sdk pay. 调用SDK支付
     * 
     */
    public void pay(String money) {
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
                || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(this)
                    .setTitle("提醒")
                    .setMessage("请用正式体验包测试。开源版没有填入支付宝key，无法充值")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface, int i) {
                                    
                                    Intent intent = new Intent();        
                                    intent.setAction("android.intent.action.VIEW");    
                                    Uri content_url = Uri.parse("http://120.24.211.126/fanxin/download/Fanxin.apk");   
                                    intent.setData(content_url);  
                                    startActivity(intent);
                                    //
                                    finish();
                                }
                            }).show();
            return;
        }
        // 订单
        String orderInfo = getOrderInfo("打赏作者", "唯一QQ84543217",
                String.valueOf(money));

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(AlipayMeActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    // /**
    // * check whether the device has authentication alipay account.
    // * 查询终端设备是否存在支付宝认证账户
    // *
    // */
    // public void check(View v) {
    // Runnable checkRunnable = new Runnable() {
    //
    // @Override
    // public void run() {
    // // 构造PayTask 对象
    // PayTask payTask = new PayTask(PayDemoActivity.this);
    // // 调用查询接口，获取查询结果
    // boolean isExist = payTask.checkAccountIfExist();
    //
    // Message msg = new Message();
    // msg.what = SDK_CHECK_FLAG;
    // msg.obj = isExist;
    // mHandler.sendMessage(msg);
    // }
    // };
    //
    // Thread checkThread = new Thread(checkRunnable);
    // checkThread.start();
    //
    // }

    /**
     * get the sdk version. 获取SDK版本号
     * 
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     * 
     */
    public String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     * 
     */
    public String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     * 
     * @param content
     *            待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     * 
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

    // private void showMyDialog() {
    //
    // final AlertDialog dlg = new AlertDialog.Builder(this).create();
    // dlg.setCancelable(false);
    // dlg.show();
    //
    // Window window = dlg.getWindow();
    // // *** 主要就是在这里实现这种效果的.
    //
    // window.setContentView(R.layout.dialog_other_money);
    //
    // window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    // | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    // window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    //
    // // 为确认按钮添加事件,执行退出应用操作
    // final EditText et_money = (EditText) window.findViewById(R.id.et_money);
    // // et_money.setInputType(InputType.TYPE_CLASS_TEXT
    // // | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    //
    // TextView tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);
    // TextView tv_ok = (TextView) window.findViewById(R.id.tv_ok);
    //
    // tv_cancel.setOnClickListener(new View.OnClickListener() {
    //
    // public void onClick(View v) {
    //
    // dlg.cancel();
    // }
    // });
    //
    // tv_ok.setOnClickListener(new View.OnClickListener() {
    // public void onClick(View v) {
    //
    // pay(String.valueOf(et_money.getText().toString().trim()));
    // dlg.cancel();
    // }
    // });
    //
    // }
}
