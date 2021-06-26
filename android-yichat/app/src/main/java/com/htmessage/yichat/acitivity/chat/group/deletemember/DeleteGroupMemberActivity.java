package com.htmessage.yichat.acitivity.chat.group.deletemember;

import android.os.Bundle;
import android.view.View;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

/**
 * 项目名称：fanxinht
 * 类描述：DeleteGroupMemberActivity 描述:
 * 创建人：songlijie
 * 创建时间：2018/3/21 16:07
 * 邮箱:814326663@qq.com
 */
public class DeleteGroupMemberActivity extends BaseActivity {
    private DeleteGroupMemberFragment fragment;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        findViewById(R.id.title).setVisibility(View.GONE);
        fragment     = new DeleteGroupMemberFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, fragment).commit();

    }

    @Override
    public void back(View view) {
        if (fragment!=null){
            fragment.back();
        }
        super.back(view);
    }
}
