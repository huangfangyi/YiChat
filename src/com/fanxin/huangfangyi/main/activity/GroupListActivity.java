package com.fanxin.huangfangyi.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.adapter.GroupsAdapter;
import com.fanxin.huangfangyi.ui.BaseActivity;
import com.fanxin.easeui.EaseConstant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;


public class GroupListActivity extends BaseActivity {
    private ListView groupListView;
    protected List<EMGroup> grouplist;
    private GroupsAdapter groupAdapter;
    private  TextView tv_total;
    public static GroupListActivity instance;
    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
             switch (msg.what) {
                case 0:
                    refresh();
                    break;
                case 1:
                    Toast.makeText(GroupListActivity.this, R.string.Failed_to_get_group_chat_information, Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        };
    };

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
                    startActivity(new Intent(GroupListActivity.this, ChatActivity.class).putExtra("userId",groupAdapter.getItem(position-1).getGroupId()).putExtra("chatType", EaseConstant.CHATTYPE_GROUP));
                }
            }
        });
        this.findViewById(R.id.iv_add).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupListActivity.this, GroupAddMembersActivity.class));
            }

        });

        new Thread(){
            @Override
            public void run(){
                try {
                    EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    handler.sendEmptyMessage(0);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();

    }

    @Override
    public void onResume() {
        refresh();
        super.onResume();

    }

    private void refresh() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            grouplist = EMClient.getInstance().groupManager().getAllGroups();
                            groupAdapter = new GroupsAdapter(GroupListActivity.this, grouplist);
                            groupListView.setAdapter(groupAdapter);
                            groupAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

            }
        }).start();


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
