package com.htmessage.yichat.acitivity.red;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

/**
 * 项目名称：Treasure
 * 类描述：RedDetailActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/9/27 16:31
 * 邮箱:814326663@qq.com
 */
public class RedDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.app_red_message);
        RedDetailFragment fragment = new RedDetailFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentFrame, fragment);
        transaction.commit();
        fragment.setArguments(getIntent().getExtras());
    }
}
