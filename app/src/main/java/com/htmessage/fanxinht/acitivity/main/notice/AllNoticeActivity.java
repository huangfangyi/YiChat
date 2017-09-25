package com.htmessage.fanxinht.acitivity.main.notice;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

/**
 * 项目名称：YiChat
 * 类描述：AllNoticeActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/2 16:48
 * 邮箱:814326663@qq.com
 */
public class AllNoticeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.notice);
        AllNoticeFragment fragment = (AllNoticeFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new AllNoticeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame,fragment);
            transaction.commit();
        }
        new AllNoticePresenter(fragment);
    }
}
