package com.htmessage.fanxinht.acitivity.main.about;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

/**
 * 项目名称：FanXinHT0831
 * 类描述：AboutUsActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/9/25 13:52
 * 邮箱:814326663@qq.com
 */
public class AboutUsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.call_us);
        AboutUsFragment fragment = (AboutUsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = new AboutUsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, fragment);
            transaction.commit();
        }
    }
}
