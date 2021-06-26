package com.htmessage.yichat.acitivity.red.pay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.uitls.WalletUtils;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.weight.PswInputView;
import com.htmessage.yichat.acitivity.main.pay.utils.PayUtils;
import com.htmessage.yichat.acitivity.main.wallet.PayPasswordActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.LoggerUtils;
import com.htmessage.yichat.utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目名称：CityBz
 * 类描述：RpPayActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/7 14:27
 * 邮箱:814326663@qq.com
 */
public class RpPayActivity extends Activity implements View.OnClickListener, PswInputView.InputCallBack {
    private TextView tv_pay_title, tv_redenvelope_name, tv_redenvelope_amount, tv_paytype_name, tv_pswd_tips, tv_forget_pswd;
    private ImageView iv_exit, iv_paytype_icon;
    private PswInputView pswinputview;
    private Button btn_pay;
    private LinearLayout layout_paytype, rootLayout;
    private boolean isChargePay = true;
    private int payWayType = 0;
    private String money, content, userId;
    private int rundom = 1;
    private int chatType = MessageUtils.CHAT_SINGLE;
    private String balance = "0.00";
    private boolean payPasss = false;
    private PayBroadcastReceiver receiver;
    private int PAY_WAYS = 50;

    private boolean isTransfer = false;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    CommonUtils.cencelDialog();
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    Intent intent = new Intent();
                    intent.putExtra("data", jsonObject.toJSONString());
                    RpPayActivity.this.setResult(RESULT_OK, intent);
                    RpPayActivity.this.finish();

                    ApiUtis.getInstance().postJSON(new JSONObject(), Constant.URL_BALANCE, new ApiUtis.HttpCallBack() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            String code=jsonObject.getString("code");
                            if("0".equals(code)){
                                JSONObject data=jsonObject.getJSONObject("data");
                                Double balance=data.getDouble("balance");
                                //本地缓存---
                                WalletUtils.getInstance().saveBalance(balance);

                            }
                        }

                        @Override
                        public void onFailure(int errorCode) {

                        }
                    });


                    break;
                case 1001:
                    CommonUtils.cencelDialog();
                    int resId = msg.arg1;
                    Toast.makeText(RpPayActivity.this, resId, Toast.LENGTH_SHORT).show();
                    break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_pay_dialog);
        isTransfer = getIntent().getBooleanExtra("isTransfer", false);
        balance = WalletUtils.getInstance().getBalance() + "";
        payPasss = WalletUtils.getInstance().isSetPayPassword();
        if (!payPasss) {
            payWayType = 1;

        }
        getData();
        initView();
        initData();
        setListener();
    }

    private void initData() {
        setPayType(payWayType);

    }

    private void setListener() {
        iv_exit.setOnClickListener(this);
        layout_paytype.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
        pswinputview.setInputCallBack(this);
        tv_forget_pswd.setOnClickListener(this);
    }

    private void initView() {
        tv_pay_title = (TextView) findViewById(R.id.tv_pay_title);
        tv_redenvelope_name = (TextView) findViewById(R.id.tv_redenvelope_name);
        tv_redenvelope_amount = (TextView) findViewById(R.id.tv_redenvelope_amount);
        tv_paytype_name = (TextView) findViewById(R.id.tv_paytype_name);
        tv_pswd_tips = (TextView) findViewById(R.id.tv_pswd_tips);
        tv_forget_pswd = (TextView) findViewById(R.id.tv_forget_pswd);

        iv_exit = (ImageView) findViewById(R.id.iv_exit);
        iv_paytype_icon = (ImageView) findViewById(R.id.iv_paytype_icon);

        pswinputview = (PswInputView) findViewById(R.id.pswinputview);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        layout_paytype = (LinearLayout) findViewById(R.id.layout_paytype);
        rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
        if (isTransfer) {
            tv_redenvelope_name.setText("转账");
        }
    }

    private void getData() {
        isChargePay = true;
        money = getIntent().getStringExtra("money");
        content = getIntent().getStringExtra("content");
        rundom = getIntent().getIntExtra("rundom",1);
        chatType = getIntent().getIntExtra("chatType", MessageUtils.CHAT_SINGLE);
        userId = getIntent().getStringExtra(HTConstant.JSON_KEY_USERID);
        if (TextUtils.isEmpty(userId)) {
            finish();
            return;
        }
        receiver = new PayBroadcastReceiver();
        IntentFilter fileter = new IntentFilter();
        fileter.addAction(IMAction.PAY_BY_WECHAT_RESULT);
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(receiver, fileter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_exit:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.btn_pay:
                if (!isChargePay) {
                    rootLayout.setVisibility(View.INVISIBLE);
                    if (payWayType == 1) {
                        payByAli(userId, money, rundom, content);
                    } else if (payWayType == 2) {
                        PayUtils.payByWeChat(RpPayActivity.this, money,1);
                    } else if (payWayType == 3) {
                        PayUtils.getTnFromUnionPayService(RpPayActivity.this, money);
                    }
                } else {
                    if ("0.00".equals(balance) || !"0.00".equals(money) && Validator.formatMoneyFloat(money) > Validator.formatMoneyFloat(balance)) {
                        CommonUtils.showToastShort(RpPayActivity.this, getString(R.string.money_isNull));
                        return;
                    }

                }
                break;
            case R.id.layout_paytype:
                startActivityForResult(new Intent(RpPayActivity.this, RedPayWaysActivity.class), PAY_WAYS);
                pswinputview.clearResult();
                rootLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_forget_pswd:
                rootLayout.setVisibility(View.INVISIBLE);
                startActivity(new Intent(RpPayActivity.this, PayPasswordActivity.class));
                finish();
                break;
        }
    }

    public void setPayType(int payType) {
        rootLayout.setVisibility(View.VISIBLE);
        tv_redenvelope_amount.setText(String.format(getString(R.string.pay_money), Validator.formatMoney(money)));
        if (payType == 0) {
            isChargePay = true;
            payWayType = 0;
            iv_paytype_icon.setImageResource(R.drawable.charge_icon);
            tv_paytype_name.setText(String.format(getString(R.string.pay_ways_change), Validator.formatMoney(balance)));
        } else if (payType == 1) {
            payWayType = 1;
            isChargePay = false;
            iv_paytype_icon.setImageResource(R.drawable.rp_ic_alipay);
            tv_paytype_name.setText(R.string.pay_ways_alipay);
        } else if (payType == 2) {
            payWayType = 2;
            isChargePay = false;
            iv_paytype_icon.setImageResource(R.drawable.rp_ic_wx);
            tv_paytype_name.setText(R.string.pay_ways_wechatpay);
        } else if (payType == 3) {
            payWayType = 3;
            isChargePay = false;
            iv_paytype_icon.setImageResource(R.drawable.union_pay);
            tv_paytype_name.setText(R.string.pay_ways_unionpay);
        }
        if (isChargePay) {
            pswinputview.setVisibility(View.VISIBLE);
            tv_forget_pswd.setVisibility(View.VISIBLE);
            btn_pay.setVisibility(View.GONE);
            tv_pswd_tips.setVisibility(View.VISIBLE);
        } else {
            pswinputview.setVisibility(View.GONE);
            btn_pay.setVisibility(View.VISIBLE);
            tv_forget_pswd.setVisibility(View.GONE);
            tv_pswd_tips.setVisibility(View.GONE);
        }
    }

    @Override
    public void onInputFinish(String result) {
        if (isChargePay && payWayType == 0) {
            if (payPasss == false) {
                Toast.makeText(getApplicationContext(),"请前往钱包设置支付密码",Toast.LENGTH_SHORT).show();
                return;
            }


            sendRp(userId, money, rundom, content, "0", result);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAY_WAYS && RESULT_OK == resultCode) {
            int payType = data.getIntExtra("payType", 0);
            if (payType == 0) {
                if (!payPasss) {

                    CommonUtils.showToastShort(RpPayActivity.this, getString(R.string.pay_pwd_is_empty));
                    finish();
                    return;
                }

            }
            setPayType(payType);
        } else {
            LoggerUtils.e("---支付成功后的code:" + resultCode);
            //  showPayResult(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 发送红包
     *
     * @param toUserId
     * @param money
      * @param content
     */
    private void sendRp(String toUserId, String money, int num, String content, String payType, String password) {
        CommonUtils.showDialogNumal(RpPayActivity.this, getString(R.string.sending));
        if (chatType == MessageUtils.CHAT_SINGLE || isTransfer) {
            sendSingleRp(toUserId, money, content, payType, password);
        } else {
            sendGroupRp(toUserId, money, num, content, payType, password);
        }
    }

    /**
     * 发送群聊红包
     *
     * @param toUserId
     * @param money
      * @param content
     */
    private void sendGroupRp(final String toUserId, final String money,int num, final String content, final String payType, String password) {

        JSONObject data = new JSONObject();
        data.put("num", num);
        data.put("money", money);
        data.put("groupId", toUserId);
        data.put("content", content);
        if(!TextUtils.isEmpty(password)){
            data.put("password", password);
        }
        data.put("type", Integer.valueOf(payType));
        ApiUtis.getInstance().postJSON(data, Constant.URL_RedPacket_GROUP, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONObject data = jsonObject.getJSONObject("data");

//                    "data": {
//                        "packetId": 13,
//                                "content": "111122",
//                                "money": 0.01,
//                                "status": 0
//                    },

                    JSONObject object = new JSONObject();
                    object.put("toUserId", toUserId);
                    object.put("redpacketId", data.getString("packetId"));
                    object.put("money", money);
                    object.put("content", content);
                    object.put("rednum", "1");

                    Message message = handler.obtainMessage();
                    message.obj = object;
                    message.what = 1000;
                    message.sendToTarget();

                } else if ("211".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.arg1 = R.string.money_isNull;
                    message.what = 1001;
                    message.sendToTarget();

                } else if ("008".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.arg1 = R.string.pay_psw_error;
                    message.what = 1001;
                    message.sendToTarget();
                } else {
                    Message message = handler.obtainMessage();
                    message.arg1 = R.string.send_rp_error;
                    message.what = 1001;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.arg1 = errorCode;
                message.what = 1001;
                message.sendToTarget();
            }
        });


//        List<Param> params = new ArrayList<>();
//        params.add(new Param("userId", HTApp.getInstance().getUsername()));
//        params.add(new Param("toUserId", toUserId));
//        params.add(new Param("money", money));
//        params.add(new Param("rednum", size));
//        params.add(new Param("content", content));
//
//
//        params.add(new Param("payType", payType));
//        String url = HTConstant.CREATE_GROUP_RP;
//        if (!TextUtils.isEmpty(getNumber(content))) {
//            params.add(new Param("boom", getNumber(content)));
//            url = HTConstant.URL_CREATE_BOOMPACKET;
//        }
//        if (getNumber(content).length() > 1) {
//            params.add(new Param("boomtype", "2"));
//        } else {
//            params.add(new Param("boomtype", "1"));
//        }


//        new OkHttpUtils(getBaseContext()).post(params, url, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                CommonUtils.cencelDialog();
//                LoggerUtils.e("----发送群红包:" + jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        // JSONObject data = jsonObject.getJSONObject("data");
//
//                        String redpacketId = jsonObject.getString("data");
//                        Intent intent = new Intent();
//                        JSONObject object = new JSONObject();
//                        object.put("toUserId", toUserId);
//                        object.put("redpacketId", redpacketId);
//                        object.put("money", money);
//                        object.put("rednum", size);
//                        object.put("content", content);
//                        intent.putExtra("data", object.toJSONString());
//                        RpPayActivity.this.setResult(RESULT_OK, intent);
//                        RpPayActivity.this.finish();
//
//                        break;
//                    case -1:
//                        CommonUtils.showToastShort(getBaseContext(), R.string.money_is_not_enought);
//                        break;
//                    case -2:
//                        CommonUtils.showToastShort(getBaseContext(), R.string.money_is_not_enought);
//                        // CommonUtils.showToastShort(getBaseContext(), R.string.user_not_exit);
//                        break;
//                    case -3:
//                        CommonUtils.showToastShort(getBaseContext(), R.string.param_wrong);
//                        break;
//                    default:
//                        CommonUtils.showToastShort(getBaseContext(), R.string.send_rp_failed);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//                CommonUtils.showToastShort(getBaseContext(), R.string.send_rp_failed);
//            }
//        });
    }

    public String getNumber(String string) {


        List<String> digitList = new ArrayList<String>();
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(string);
        String result = m.replaceAll("");
        if (!TextUtils.isEmpty(result)) {
            return result.substring(result.length() - 1, result.length());
        }

        return "";
//        if (!string.contains("/")) {
//            return "";
//
//        }
//        List<String> digitList = new ArrayList<String>();
//        String stringNum = string.substring(string.indexOf("/") + 1);
//        Log.d("stringNum--->", stringNum);
//        for (int i = 0; i < stringNum.length(); i++) {
//            String cString =   String.valueOf(stringNum.charAt(i));
//            if (cString.equals("零")) {
//                digitList.add("0");
//            }
//           else if (cString.equals("一")) {
//                digitList.add("1");
//            }
//          else   if (cString.equals("二")) {
//                digitList.add("2");
//            }
//            else if (cString.equals("三")) {
//                digitList.add("3");
//            }
//            else if (cString.equals("四")) {
//                digitList.add("4");
//            }
//            else if (cString.equals("五")) {
//                digitList.add("5");
//            }
//            else if (cString.equals("六")) {
//                digitList.add("6");
//            }
//            else  if (cString.equals("七")) {
//                digitList.add("7");
//            }
//
//            else if (cString.equals("八")) {
//                digitList.add("8");
//            }
//
//            else if (cString.equals("九")) {
//                digitList.add("9");
//            }
//
//            else if(isNumeric(cString)){
//                digitList.add(cString);
//            }else {
//                break;
//            }


        //System.out.print(c + " ");
    }

//
//        Pattern p = Pattern.compile("[^0-9]");
//        Matcher m = p.matcher(string);
//        String result = m.replaceAll("");
//        for (int i = 0; i < result.length(); i++) {
//            digitList.add(result.substring(i, i + 1));
//
//        }


//        String numberString = "";
//
//        for (int i = 0; i < digitList.size(); i++) {
//            if (i != digitList.size() - 1) {
//                numberString = numberString + digitList.get(i) + ",";
//            } else {
//                numberString = numberString + digitList.get(i);
//            }
//        }
//
//
//        return numberString;

    // }


    //方法四：
    public final static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim()))
            return s.matches("^[0-9]*$");
        else
            return false;
    }

    /**
     * 发送单聊红包
     *
     * @param toUserId
     * @param money
     * @param content
     */
    private void sendSingleRp(final String toUserId, final String money, final String content, final String payType, String password) {

        JSONObject data = new JSONObject();
        data.put("receiveUserId", toUserId);
        data.put("money", money);
        data.put("content", content);
        if(!TextUtils.isEmpty(password)){
            data.put("password", password);
        }

        data.put("type", Integer.valueOf(payType));
        ApiUtis.getInstance().postJSON(data, Constant.URL_RedPacket_SINGE, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONObject data = jsonObject.getJSONObject("data");

//                    "data": {
//                        "packetId": 13,
//                                "content": "111122",
//                                "money": 0.01,
//                                "status": 0
//                    },

                    JSONObject object = new JSONObject();
                    object.put("toUserId", toUserId);
                    object.put("redpacketId", data.getString("packetId"));
                    object.put("money", money);
                    object.put("content", content);
                    object.put("rednum", "1");

                    Message message = handler.obtainMessage();
                    message.obj = object;
                    message.what = 1000;
                    message.sendToTarget();

                } else if ("211".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.arg1 = R.string.money_isNull;
                    message.what = 1001;
                    message.sendToTarget();

                } else if ("008".equals(code)) {
                    Message message = handler.obtainMessage();
                    message.arg1 = R.string.pay_psw_error;
                    message.what = 1001;
                    message.sendToTarget();
                } else {
                    Message message = handler.obtainMessage();
                    message.arg1 = R.string.send_rp_error;
                    message.what = 1001;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.arg1 = errorCode;
                message.what = 1001;
                message.sendToTarget();
            }
        });
//
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("userId", HTApp.getInstance().getUsername()));
//        params.add(new Param("toUserId", toUserId));
//        params.add(new Param("money", money));
//        params.add(new Param("content", content));
//        params.add(new Param("payType", payType));
//        new OkHttpUtils(getBaseContext()).post(params, HTConstant.CREATE_SINGLE_RP, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                CommonUtils.cencelDialog();
//                LoggerUtils.e("----发送单聊红包:" + jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//
//
//                        String redpacketId = jsonObject.getString("data");
//
//                        JSONObject object = new JSONObject();
//                        object.put("toUserId", toUserId);
//                        object.put("redpacketId", redpacketId);
//                        object.put("money", money);
//                        object.put("content", content);
//                        object.put("rednum", "1");
//                        Intent intent = new Intent();
//                        intent.putExtra("data", object.toJSONString());
//                        RpPayActivity.this.setResult(RESULT_OK, intent);
//                        RpPayActivity.this.finish();
//
//                        break;
//                    case -2:
//                        CommonUtils.showToastShort(getBaseContext(), R.string.money_is_not_enought);
//                        break;
//                    case -3:
//                        CommonUtils.showToastShort(getBaseContext(), R.string.user_not_exit);
//                        break;
//                    case -4:
//                        CommonUtils.showToastShort(getBaseContext(), R.string.param_wrong);
//                        break;
//                    default:
//                        CommonUtils.showToastShort(getBaseContext(), R.string.send_rp_failed);
//                        break;
//                }
//
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.cencelDialog();
//                CommonUtils.showToastShort(getBaseContext(), R.string.send_rp_failed);
//            }
//        });
//    }
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
                           sendRp(userId, money, rundom, content, "1",null);
                        break;
                    case "-1":
                        CommonUtils.showToastShort(context, R.string.pay_failed);
                        rootLayout.setVisibility(View.VISIBLE);
                        break;
                    case "-2":
                        CommonUtils.showToastShort(context, R.string.pay_cancle);
                        rootLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    /**
     * 支付宝支付
     *
     * @param userId
     * @param money
     * @param rundom
     * @param content
     */
    private void payByAli(final String userId, final String money, final int rundom, final String content) {
        PayUtils.payByAliPay(RpPayActivity.this, money,1, new PayUtils.PayBackListener() {
            @Override
            public void paySuccess() {
                  sendRp(userId, money, rundom, content, "2",null);
            }

            @Override
            public void payFailed() {
                CommonUtils.showToastShort(getBaseContext(), R.string.pay_failed);
                rootLayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void payCancled() {
                CommonUtils.showToastShort(getBaseContext(), R.string.pay_cancle);
                rootLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}
