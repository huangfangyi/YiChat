package com.htmessage.yichat.acitivity.chat.group.managerlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.chat.group.allmember.AllGroupMembersActivity;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.update.data.UserManager;

/**
 * 项目名称：KTZ
 * 类描述：ManagerListActivity 描述:
 * 创建人：songlijie
 * 创建时间：2018/4/26 13:07
 * 邮箱:814326663@qq.com
 */
public class ManagerListActivity extends BaseActivity {
    private  ManagerListFragment fragment;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.manager_list);
        fragment= (ManagerListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment ==null){
            fragment = new ManagerListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame,fragment).commit();
        }
       final String groupId =  getIntent().getStringExtra("groupId");
       if(UserManager.get().getMyUserId().equals(HTClient.getInstance().groupManager().getGroup(groupId).getOwner())){
           //如果我是群主，右上角出现添加按钮
           showRightView(R.drawable.add_icon, new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   startActivityForResult(new Intent(ManagerListActivity.this, AllGroupMembersActivity.class).putExtra("groupId",groupId).putExtra("type",1),1000);
               }
           });
       }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000&&resultCode== Activity.RESULT_OK){
            String userId=data.getStringExtra("userId");
            fragment.handManager(userId,1);
        }
    }
}
