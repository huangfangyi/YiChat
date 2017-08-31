package com.htmessage.fanxinht.acitivity.addfriends.newfriend;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.addfriends.add.next.AddFriendsNextActivity;
import com.htmessage.fanxinht.acitivity.addfriends.invitefriend.ContactsInviteActivity;

/**
 * 项目名称：yichat0504
 * 类描述：NewFriendFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/4 17:17
 * 邮箱:814326663@qq.com
 */
public class NewFriendFragment extends Fragment implements NewFriendView,View.OnClickListener{
    private ListView listView;
    private TextView et_search, tv_add;
    private NewFriendsAdapter adapter;
    private NewFriendPrestener friendPrestener;
    private LinearLayout ll_invite_people;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View friendView = inflater.inflate(R.layout.activity_new_friends, container, false);
        initView(friendView);
        initData();
        setListener();
        return friendView;
    }

    private void initData() {
        friendPrestener.registerRecivier();
//         设置adapter
        adapter = new NewFriendsAdapter(getBaseContext(),friendPrestener.getAllInviteMessage());
        listView.setAdapter(adapter);
        friendPrestener.saveUnreadMessageCount(0);
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.listview);
        et_search = (TextView) view.findViewById(R.id.et_search);
        tv_add = (TextView) view.findViewById(R.id.tv_add);
        ll_invite_people = (LinearLayout) view.findViewById(R.id.ll_invite_people);
    }

    private void setListener() {
        et_search.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        ll_invite_people.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_search:
            case R.id.tv_add:
                friendPrestener.startActivity(getBaseActivity(),
                        AddFriendsNextActivity.class);
                break;
            case R.id.ll_invite_people:
                friendPrestener.startActivity(getBaseActivity(),
                        ContactsInviteActivity.class);
                break;
        }
    }

    @Override
    public void refresh() {
        friendPrestener.refresh();
        adapter.notifyDataSetChanged();
        friendPrestener.saveUnreadMessageCount(0);
    }

    @Override
    public void setPresenter(NewFriendPrestener presenter) {
        this.friendPrestener = presenter;
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }

    @Override
    public void onDestroy() {
        friendPrestener.onDestory();
        super.onDestroy();
    }
}
