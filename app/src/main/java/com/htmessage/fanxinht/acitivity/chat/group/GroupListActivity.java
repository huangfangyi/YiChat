package com.htmessage.fanxinht.acitivity.chat.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.chat.ChatActivity;
 import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;


import java.util.ArrayList;
import java.util.List;


public class GroupListActivity extends BaseActivity {
    private ListView groupListView;
    protected List<HTGroup> grouplist=new ArrayList<>();
    private GroupsListAdapter groupAdapter;
    private  TextView tv_total;
    public static GroupListActivity instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        instance = this;
        grouplist=HTClient.getInstance().groupManager().getAllGroups();
        groupListView = (ListView) findViewById(R.id.groupListView);
        View headerView = LayoutInflater.from(this).inflate(
                R.layout.item_group_header, null);
        View footerView = LayoutInflater.from(this).inflate(
                R.layout.item_group_footer, null);
        tv_total = (TextView) footerView.findViewById(R.id.tv_total);
        tv_total.setText(String.valueOf(grouplist.size()) +getString(R.string.group_size));
        groupAdapter = new GroupsListAdapter(this, grouplist);
        groupListView.addHeaderView(headerView);
        groupListView.addFooterView(footerView);
        groupListView.setAdapter(groupAdapter);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(GroupListActivity.this, ChatActivity.class).putExtra("userId",groupAdapter.getItem(position-1).getGroupId()).putExtra("chatType", MessageUtils.CHAT_GROUP));
//                    finish();
            }
        });
        this.findViewById(R.id.iv_add).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupListActivity.this, GroupAddMembersActivity.class));
            }

        });



    }

    @Override
    public void onResume() {
        refresh();
        super.onResume();

    }

    private void refresh() {
        grouplist.clear();
        grouplist.addAll(HTClient.getInstance().groupManager().getAllGroups());
        groupAdapter.notifyDataSetChanged();
      //  int count=GroupManager.getInstance().getAllGroups().size();
        tv_total.setText(grouplist.size()+getString(R.string.group_size));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }




}
