package com.htmessage.yichat.acitivity.chat.group.allmember;

import android.os.Bundle;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

/**
 * 项目名称：fanxinht
 * 类描述：AllGroupMembersActivity 描述:查看全体群成员的
 * 创建人：songlijie
 * 创建时间：2018/3/22 17:09
 * 邮箱:814326663@qq.com
 */
public class AllGroupMembersActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.see_all_group_number);
        AllGroupMemberFragment  fragment = (AllGroupMemberFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new AllGroupMemberFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame,fragment).commit();
        }
    }
}
