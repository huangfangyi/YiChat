package com.fanxin.app.fx;

import android.os.Bundle;

import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;

public class RecordsDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_records_detail);
        initView();

    }

    private void initView() {
        String jsonStr = this.getIntent().getStringExtra("json");
        if (jsonStr == null) {
            finish();
            return;
        }
        
        JSONObject json = JSONObject.parseObject(jsonStr);
        String fHxid = json.getString("fHxid");
        String tHxid = json.getString("tHxid");
        String money = json.getString("money");
        String time = json.getString("time");
        String type = json.getString("type");
        String state = json.getString("state");
        String number = json.getString("number");
        TextView tv_money = (TextView) this.findViewById(R.id.tv_money);
        TextView tv_type = (TextView) this.findViewById(R.id.tv_type);

        TextView tv_state = (TextView) this.findViewById(R.id.tv_state);

        TextView tv_time = (TextView) this.findViewById(R.id.tv_time);

        TextView tv_number = (TextView) this.findViewById(R.id.tv_number);
        tv_money.setText("￥" + money);
        if (type.equals("1")) {
            tv_type.setText("提现");
            if (state.equals("1")) {
                tv_state.setText("提现正在处理");
            } else {
                tv_state.setText("提现成功");
            }
        } else if (type.equals("2")) {
            tv_type.setText("充值");
        } else {
            tv_type.setText("转账");
        }
        tv_number.setText(number);
        tv_time.setText(time);
    }
}
