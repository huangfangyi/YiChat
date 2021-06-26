package com.htmessage.yichat.acitivity.main.pay.paymentdetails;

import android.os.Bundle;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

/**
 * 项目名称：hanxuan
 * 类描述：PayMentListActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/28 15:18
 * 邮箱:814326663@qq.com
 */
public class PayMentListActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.pay_list_history);
        PayMentListFragment fragment = (PayMentListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = new PayMentListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, fragment).commit();
        }
    }
}
