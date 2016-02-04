package com.fanxin.app.fx;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.fx.others.ChatRoomAdapter;

@SuppressLint("InflateParams")
public class ChatRoomActivity extends BaseActivity {
    private ListView groupListView;
    protected List<EMGroup> grouplist;
    private ChatRoomAdapter groupAdapter;
    TextView tv_total;
    public static ChatRoomActivity instance;

 
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mychatroom);

        instance = this;

        grouplist = EMGroupManager.getInstance().getAllGroups();

        groupListView = (ListView) findViewById(R.id.groupListView);
        View headerView = LayoutInflater.from(this).inflate(
                R.layout.item_mychatroom_header, null);
        View footerView = LayoutInflater.from(this).inflate(
                R.layout.item_mychatroom_footer, null);
        tv_total = (TextView) footerView.findViewById(R.id.tv_total);
        tv_total.setText(String.valueOf(grouplist.size()) + "个群聊");
        groupAdapter = new ChatRoomAdapter(this, grouplist);
        groupListView.addHeaderView(headerView);
        groupListView.addFooterView(footerView);
        groupListView.setAdapter(groupAdapter);

        final ImageView iv_add = (ImageView) this.findViewById(R.id.iv_add);
        ImageView iv_search = (ImageView) this.findViewById(R.id.iv_search);
        iv_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AddPopWindow addPopWindow = new AddPopWindow(
                        ChatRoomActivity.this);
                addPopWindow.showPopupWindow(iv_add);
            }

        });
        iv_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // grouplist = EMGroupManager.getInstance().getAllGroups();
        // groupAdapter = new MyChatRoomAdapter(this, 1, grouplist);
        // groupListView.setAdapter(groupAdapter);
        // groupAdapter.notifyDataSetChanged();
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
