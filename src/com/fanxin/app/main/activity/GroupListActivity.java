package com.fanxin.app.main.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fanxin.app.R;
import com.fanxin.app.main.adapter.GroupsAdapter;
import com.fanxin.app.ui.BaseActivity;
import com.fanxin.app.ui.ChatActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;


public class GroupListActivity extends BaseActivity {
    private ListView groupListView;
    protected List<EMGroup> grouplist;
    private GroupsAdapter groupAdapter;
    TextView tv_total;
    public static GroupListActivity instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fx_activity_groups);
        instance = this;
        grouplist = EMClient.getInstance().groupManager().getAllGroups();
        groupListView = (ListView) findViewById(R.id.groupListView);
        View headerView = LayoutInflater.from(this).inflate(
                R.layout.fx_item_group_header, null);
        View footerView = LayoutInflater.from(this).inflate(
                R.layout.fx_item_group_footer, null);
        tv_total = (TextView) footerView.findViewById(R.id.tv_total);
        tv_total.setText(String.valueOf(grouplist.size()) + "个群聊");
        groupAdapter = new GroupsAdapter(this, grouplist);
        groupListView.addHeaderView(headerView);
        groupListView.addFooterView(footerView);
        groupListView.setAdapter(groupAdapter);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>0&&position<groupAdapter.getCount()+1){
                    startActivity(new Intent(GroupListActivity.this, ChatActivity.class).putExtra("userId",groupAdapter.getItem(position-1).getGroupId()).putExtra("chatType", EMMessage.ChatType.GroupChat));
                }
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
        grouplist = EMClient.getInstance().groupManager().getAllGroups();
        groupAdapter = new GroupsAdapter(this, grouplist);
        groupListView.setAdapter(groupAdapter);
        groupAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }
}
