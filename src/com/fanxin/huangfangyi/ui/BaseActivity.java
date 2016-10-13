package com.fanxin.huangfangyi.ui;

import android.os.Bundle;
import android.view.View;

import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.easeui.ui.EaseBaseActivity;

public class BaseActivity extends EaseBaseActivity {

    /**
     * OKHTTP3请求
     */



    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        DemoApplication.getInstance().saveActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
      }

    @Override
    protected void onStart() {
        super.onStart();
      }

    public void back(View view) {
        finish();
    }

}
