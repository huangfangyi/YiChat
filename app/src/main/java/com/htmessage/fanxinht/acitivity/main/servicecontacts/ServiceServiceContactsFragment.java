package com.htmessage.fanxinht.acitivity.main.servicecontacts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.chat.ChatActivity;
import com.htmessage.fanxinht.acitivity.main.contacts.Sidebar;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.utils.MessageUtils;

/**
 * 项目名称：PersonalTailor
 * 类描述：ServiceServiceContactsFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/31 17:45
 * 邮箱:814326663@qq.com
 */
public class ServiceServiceContactsFragment extends Fragment implements ServiceContactsNewView {
    private MsgBroadcastReceiver broadcastReceiver;
    private ServiceContactsAdapter adapter;
    private ListView listView;
    private Sidebar sidebar;
    private ServiceContactsPresenter contactsPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        contactsPresenter = new ServiceContactsPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contactlist, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
        initData();
        showSiderBar();
        broadcastReceiver = new MsgBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMAction.ACTION_NEW_MESSAGE);
        intentFilter.addAction(IMAction.ACTION_MESSAGE_WITHDROW);
        intentFilter.addAction(IMAction.ACTION_REFRESH_ALL_LIST);
        LocalBroadcastManager.getInstance(getBaseActivity()).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void initData() {
        adapter = new ServiceContactsAdapter(getActivity(), contactsPresenter.getUserListFormCache());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServiceUser user = adapter.getItem(position);
                HTClient.getInstance().conversationManager().markAllMessageRead(user.getUsername());
                if (HTApp.getInstance().getUsername().equals(user.getUsername())) {
                    showToast(getString(R.string.do_not_talk_self));
                } else {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("userId", user.getUsername());
                    intent.putExtra("userNick", user.getNick());
                    intent.putExtra("userAvatar", user.getAvatar());
                    intent.putExtra("chatType", MessageUtils.CHAT_SINGLE);
                    startActivity(intent);
                }
                refresh();
            }
        });
    }

    private void initView(View root) {
        listView = (ListView) root.findViewById(R.id.list);
        sidebar = (Sidebar) root.findViewById(R.id.sidebar);
    }

    @Override
    public void showToast(String msg) {
        CommonUtils.showToastShort(getActivity(), msg);
    }

    @Override
    public void onResume() {
        contactsPresenter.getData();
        super.onResume();
    }

    @Override
    public void showSiderBar() {
        sidebar.setVisibility(View.GONE);
        sidebar.setListView(listView);
    }


    @Override
    public void refresh() {
        adapter.notifyDataSetChanged();
    }


    @Override
    public void setPresenter(ServiceContactsPresenter presenter) {
        this.contactsPresenter = presenter;
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
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(getBaseActivity()).unregisterReceiver(broadcastReceiver);
        }
        contactsPresenter.onDestory();
        super.onDestroy();
    }

    private class MsgBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IMAction.ACTION_NEW_MESSAGE.equals(intent.getAction()) || IMAction.ACTION_MESSAGE_WITHDROW.equals(intent.getAction())) {

                HTMessage htMessage = intent.getParcelableExtra("message");

                contactsPresenter.onNewMessage(htMessage);

            } else if (IMAction.ACTION_REFRESH_ALL_LIST.equals(intent.getAction())) {
                contactsPresenter.getData();
            }
        }
    }
}
