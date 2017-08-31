/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htmessage.fanxinht.acitivity.main.contacts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.addfriends.newfriend.NewFriendsActivity;
import com.htmessage.fanxinht.acitivity.main.details.UserDetailsActivity;
import com.htmessage.fanxinht.acitivity.chat.group.GroupListActivity;
import com.htmessage.fanxinht.anyrtc.activity.AnyLiveStartActivity;
import com.htmessage.fanxinht.anyrtc.activity.AnyLiveWatchActivity;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.widget.HTAlertDialog;

/**
 * contact list
 */
public class FragmentContacts extends Fragment implements View.OnClickListener, ContactsView {
    private ContactsAdapter adapter;
    private ListView listView;
    private Sidebar sidebar;
    private TextView tv_unread;
    private TextView tv_total;
    private ContactsPresenter contactsPresenter;

    public interface ContactsListener {
        void showInvitionCount(int count);
    }

    private ContactsListener contactsListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        contactsPresenter = new ContactsPresenter(this);
        if (context instanceof ContactsListener) {
            contactsListener = (ContactsListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contactlist, container, false);
        listView = (ListView) root.findViewById(R.id.list);

        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_contact_list_footer,
                null);
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.item_contact_list_header,
                null);
        listView.addHeaderView(headView);
        listView.addFooterView(footerView);
        sidebar = (Sidebar) root.findViewById(R.id.sidebar);
        tv_unread = (TextView) headView.findViewById(R.id.tv_unread);
        tv_total = (TextView) footerView.findViewById(R.id.tv_total);

        showSiderBar();

        headView.findViewById(R.id.re_newfriends).setOnClickListener(this);
        headView.findViewById(R.id.re_chatroom).setOnClickListener(this);
        headView.findViewById(R.id.re_tag).setOnClickListener(this);
        headView.findViewById(R.id.re_public).setOnClickListener(this);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new ContactsAdapter(getActivity(), contactsPresenter.getContactsListInDb());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position != adapter.getCount() + 1) {
                    User user = adapter.getItem(position - 1);
                    startActivity(new Intent(getActivity(), UserDetailsActivity.class)
                            .putExtra(HTConstant.KEY_USER_INFO, user.getUserInfo()));
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0 && position != adapter.getCount() + 1) {
                    User user = adapter.getItem(position - 1);
                    showItemDialog(user);
                }

                return true;
            }
        });
        contactsPresenter.refreshContactsInServer();
        registerBroadReciever();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_newfriends:
                startActivityForResult(new Intent(getActivity(), NewFriendsActivity.class),10086);
                contactsPresenter.clearInvitionCount();
                break;
            case R.id.re_chatroom:
                startActivity(new Intent(getActivity(), GroupListActivity.class));
                break;
            case R.id.re_tag://直播看
                startActivity(new Intent(getActivity(), AnyLiveWatchActivity.class));
                break;
            case R.id.re_public://进行直播
                startActivity(new Intent(getActivity(), AnyLiveStartActivity.class));
                break;
        }

    }

    @Override
    public void setPresenter(ContactsPresenter presenter) {

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
    public void showItemDialog(final User user) {
        HTAlertDialog HTAlertDialog = new HTAlertDialog(getContext(), null, new String[]{getResources().getString(R.string.delete)});
        HTAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        contactsPresenter.deleteContacts(user.getUsername());
                        break;
                    case 1:
                        contactsPresenter.moveUserToBlack(user.getUsername());
                        break;
                }
                refresh();
            }
        });

    }

    @Override
    public void showSiderBar() {
        sidebar.setVisibility(View.VISIBLE);
        sidebar.setListView(listView);
    }

    @Override
    public void showInvitionCount(int count) {
        if (count != 0) {
            tv_unread.setVisibility(View.VISIBLE);
            // tv_unread.setText(  count+"");

        } else {
            tv_unread.setVisibility(View.GONE);
        }
        contactsListener.showInvitionCount(count);
    }

    @Override
    public void showContactsCount(int count) {
        tv_total.setText(count + getString(R.string.more_people));

    }

    @Override
    public void refresh() {
        adapter.notifyDataSetChanged();
        showContactsCount(contactsPresenter.getContactsCount());
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (IMAction.ACTION_CONTACT_CHANAGED.equals(action)) {
                contactsPresenter.refreshContactsInLocal();
            } else if (IMAction.ACTION_INVITE_MESSAGE.equals(action)) {
                showInvitionCount(1);
            } else if (IMAction.CMD_DELETE_FRIEND.equals(action)) {
                String userId = intent.getStringExtra(HTConstant.JSON_KEY_HXID);
                contactsPresenter.deleteContactOnBroast(userId);
                refresh();
            }
        }
    }

    private MyBroadcastReceiver myBroadcastReceiver;

    private void registerBroadReciever() {
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMAction.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(IMAction.ACTION_INVITE_MESSAGE);
        intentFilter.addAction(IMAction.CMD_DELETE_FRIEND);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(myBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            showInvitionCount(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
