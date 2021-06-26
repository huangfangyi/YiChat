package com.htmessage.yichat.acitivity.red.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htmessage.update.uitls.WalletUtils;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.Validator;

/**
 * 项目名称：CityBz
 * 类描述：RedPayWaysActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/7 10:38
 * 邮箱:814326663@qq.com
 */
public class RedPayWaysActivity extends Activity implements View.OnClickListener {
    private TextView tv_pay_changes, tv_cancle;
    private LinearLayout ll_pay_changs, ll_pay_alipay, ll_pay_wxpay,ll_pay_unionpay;
    private int payType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_ways);
        getData();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        ll_pay_changs.setOnClickListener(this);
        ll_pay_alipay.setOnClickListener(this);
        ll_pay_wxpay.setOnClickListener(this);
        tv_cancle.setOnClickListener(this);
        ll_pay_unionpay.setOnClickListener(this);
    }

    private void initData() {
        getBlance();
    }

    private void initView() {
        tv_pay_changes = (TextView) findViewById(R.id.tv_pay_changes);
        tv_cancle = (TextView) findViewById(R.id.tv_cancle);
        ll_pay_changs = (LinearLayout) findViewById(R.id.ll_pay_changs);
        ll_pay_alipay = (LinearLayout) findViewById(R.id.ll_pay_alipay);
        ll_pay_wxpay = (LinearLayout) findViewById(R.id.ll_pay_wxpay);
        ll_pay_unionpay = (LinearLayout) findViewById(R.id.ll_pay_unionpay);
    }

    private void getData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_pay_changs:
                payType= 0;
                break;
            case R.id.ll_pay_alipay:
                payType=1;
                break;
            case R.id.ll_pay_wxpay:
                payType=2;
                break;
            case R.id.ll_pay_unionpay:
                payType = 3;
                break;
            case R.id.tv_cancle:
                payType=-1;
                break;
        }
        Intent intent = getIntent();
        intent.putExtra("payType",payType);
        setResult(RESULT_OK,intent);
        finish();
    }
    private void getBlance() {
        String balance = WalletUtils.getInstance().getBalance()+"";
        tv_pay_changes.setText(String.format(getString(R.string.pay_ways_change), Validator.formatMoney(balance)));

//        List<Param> params = new ArrayList<>();
//        params.add(new Param("userId", HTApp.getInstance().getUsername()));
//        new OkHttpUtils(getBaseContext()).post(params, HTConstant.GET_BALANCE, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e("----获取余额:" + jsonObject.toJSONString());
//                int code = jsonObject.getIntValue("code");
//                switch (code){
//                    case 1:
//                         String balance = jsonObject.getString("balance");
//                        tv_pay_changes.setText(String.format(getString(R.string.pay_ways_change), Validator.formatMoney(balance)));
//                        MmvkManger.getIntance().putDouble(HTApp.getInstance().getUsername()+"balance",Double.parseDouble(balance));
//                        break;
//                    default:
//                        tv_pay_changes.setText(String.format(getString(R.string.pay_ways_change), Validator.formatMoney(0)));
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                tv_pay_changes.setText(String.format(getString(R.string.pay_ways_change), Validator.formatMoney(0)));
//            }
//        });
    }
}
