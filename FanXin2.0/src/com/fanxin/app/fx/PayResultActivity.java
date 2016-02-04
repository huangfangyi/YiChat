package com.fanxin.app.fx;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;

public class PayResultActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);

        initView();

    }

    private void initView() {
        String nick = this.getIntent().getStringExtra("nick");
        String money = this.getIntent().getStringExtra("money");

        TextView tv_money = (TextView) findViewById(R.id.tv_money);
        TextView tv_nick = (TextView) findViewById(R.id.tv_nick);

        tv_nick.setText("转账给：" + nick);
        tv_money.setText("￥" + money);
        Button btn_ok = (Button) findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });

    }

}
