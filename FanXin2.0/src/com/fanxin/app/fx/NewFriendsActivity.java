package com.fanxin.app.fx;

import java.util.List;

import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.db.InviteMessgeDao;
import com.fanxin.app.domain.InviteMessage;
import com.fanxin.app.domain.User;
import com.fanxin.app.fx.others.NewFriendsAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 申请与通知
 * 
 */
public class NewFriendsActivity extends BaseActivity {
    private ListView listView;
    List<InviteMessage> msgs;
    InviteMessgeDao dao;
    NewFriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newfriendsmsg);
        // DemoApplication.getInstance().addActivity(this);

        listView = (ListView) findViewById(R.id.listview);
        TextView et_search = (TextView) findViewById(R.id.et_search);
        TextView tv_add = (TextView) findViewById(R.id.tv_add);
        et_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewFriendsActivity.this,
                        AddFriendsTwoActivity.class));
            }

        });
        tv_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewFriendsActivity.this,
                        AddFriendsOneActivity.class));
            }

        });

        dao = new InviteMessgeDao(this);
        msgs = dao.getMessagesList();
        // 设置adapter
        adapter = new NewFriendsAdapter(this, msgs);
        listView.setAdapter(adapter);
        User userTemp = MYApplication.getInstance().getContactList()
                .get(Constant.NEW_FRIENDS_USERNAME);
        if (userTemp != null && userTemp.getUnreadMsgCount() != 0) {
            userTemp.setUnreadMsgCount(0);
        }

        MYApplication.getInstance().getContactList()
                .get(Constant.NEW_FRIENDS_USERNAME).setUnreadMsgCount(0);
       
    }

   
    public void back(View v) {
        finish();
    }

}
