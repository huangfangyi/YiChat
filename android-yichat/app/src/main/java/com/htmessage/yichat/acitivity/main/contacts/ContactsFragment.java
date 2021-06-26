package com.htmessage.yichat.acitivity.main.contacts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htmessage.sdk.model.HTMessage;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.forward.ForwardSingleActivity;
import com.htmessage.yichat.acitivity.chat.group.GroupListActivity;
import com.htmessage.yichat.acitivity.details.UserDetailActivity;
import com.htmessage.yichat.acitivity.friends.addfriend.AddFriendsPreActivity;
import com.htmessage.yichat.acitivity.friends.newfriend.NewFriendsActivity;
import com.htmessage.yichat.domain.User;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.widget.HTAlertDialog;
import com.htmessage.yichat.widget.Sidebar;

/**
 * contact list
 */
public class ContactsFragment extends Fragment implements View.OnClickListener, ContactsView, ContactsRecAdapter.OnItemClickListener, Sidebar.OnTouchingLetterChangedListener {
    private ContactsRecAdapter adapter;
    private RecyclerView listView;
    private Sidebar sidebar;
    private TextView tv_search, floating_header;
    private ContactsPresenter contactsPresenter;
    private LinearLayout ll_search;
    private GridLayoutManager layoutManager;


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
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        ll_search.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        adapter.setListener(this);
        sidebar.setOnTouchingLetterChangedListener(this);
    }

    private void getData() {

    }

    private void initData() {
        showSiderBar();
        layoutManager = new GridLayoutManager(getActivity(), 1);
        listView.setLayoutManager(layoutManager);
        adapter = new ContactsRecAdapter(getActivity(),contactsPresenter.getContactsListLocal() );
        listView.setAdapter(adapter);
        contactsPresenter.refreshContactsInServer();
        registerBroadReciever();
    }

    private void initView() {
        listView = (RecyclerView) getView().findViewById(R.id.list);
        ll_search = (LinearLayout) getView().findViewById(R.id.ll_search);
        tv_search = (TextView) getView().findViewById(R.id.tv_search);
        floating_header = (TextView) getView().findViewById(R.id.floating_header);
        sidebar = (Sidebar) getView().findViewById(R.id.sidebar);
     }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = adapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            layoutManager.scrollToPositionWithOffset(position, 0);
        }
    }

    @Override
    public void onTopClick(int type) {
        switch (type) {
            case 1://新的好友
                startActivityForResult(new Intent(getActivity(), NewFriendsActivity.class), 10086);
                contactsPresenter.clearInvitionCount();
                break;
            case 2://群聊
                startActivity(new Intent(getActivity(), GroupListActivity.class));
                break;
            case 3://群发消息
                CommonUtils.showInputDialog(getBaseContext(),"群发至好友或者群","请输入内容",null,new CommonUtils.DialogClickListener(){
                    @Override
                    public void onCancleClock() {

                    }

                    @Override
                    public void onPriformClock(String msg) {
                        if(TextUtils.isEmpty(msg)){
                            return;
                        }

                       HTMessage htMessage = HTMessage.createTextSendMessage(UserManager.get().getMyUserId(), msg);
                       startActivity(new Intent(getActivity(), ForwardSingleActivity.class).putExtra("htMessage",htMessage));

                    }
                });
                break;

            case 5://客服 添加新的好友
                startActivity(new Intent(getActivity(), AddFriendsPreActivity.class));

                break;
        }
    }

    @Override
    public void onItemClick(int position, User user) {

        startActivity(new Intent(getActivity(), UserDetailActivity.class).putExtra("userId", user.getUserId()));



    }

    @Override
    public void onItemLongClick(int position, User user) {
        showItemDialog(user);
    }

    @Override
    public void onBottomClick() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
            case R.id.tv_search:
                this.startActivityForResult(new Intent(getActivity(), SearchContactsActivity.class),1000);
                break;
        }

    }

    @Override
    public void setPresenter(ContactsPresenter presenter) {
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
    public void showItemDialog(final User user) {
        HTAlertDialog dialog = new HTAlertDialog(getContext(), null, new String[]{getResources().getString(R.string.delete)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        contactsPresenter.deleteContacts(user);
                        break;

                }

            }
        });

    }

    @Override
    public void showSiderBar() {
        sidebar.setVisibility(View.VISIBLE);
        sidebar.setTextView(floating_header);
    }

    @Override
    public void showInvitionCount(int count) {
        if (getActivity() == null) {
            return;
        }
        if (adapter != null) {
            adapter.setUnReadText(count);
        }
        contactsListener.showInvitionCount(count);
    }

    @Override
    public void showContactsCount(int count) {
        if (getActivity() == null) {
            return;
        }
        if (adapter != null) {
            adapter.showAllUser(count);
        }
    }



    @Override
    public void refreshALL() {
        //防止请求结果回来之后界面已跳转销毁
        if (getActivity() == null) {
            return;
        }
        adapter.notifyDataSetChanged();
        showContactsCount(contactsPresenter.getContactsCount());
    }

    @Override
    public void refreshItem(int position) {
        Log.d("position---",position+"");
        adapter.notifyItemChanged(position);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (IMAction.ACTION_INVITE_MESSAGE.equals(action)) {
                if(intent.getIntExtra("type",0)==0){
                    contactsPresenter.getApplyUnread();
                }
                contactsPresenter.refreshContactsInLocal();
            } else if (IMAction.CMD_DELETE_FRIEND.equals(action)  ) {
                String userId = intent.getStringExtra("userId");
                contactsPresenter.deleteContactsFromCMD(userId);

             }else if(IMAction.USER_REMARK.equals(action)){
                 String userId=intent.getStringExtra("userId");
                 contactsPresenter.updateUser(userId);
            }

        }
    }

    private MyBroadcastReceiver myBroadcastReceiver;

    private void registerBroadReciever() {
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
         intentFilter.addAction(IMAction.ACTION_INVITE_MESSAGE);
        intentFilter.addAction(IMAction.CMD_DELETE_FRIEND);
      //   intentFilter.addAction(IMAction.DELETE_FRIEND_LOCAL);
         intentFilter.addAction(IMAction.USER_REMARK);

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
        if (resultCode == Activity.RESULT_OK&&requestCode==1000) {
            contactsPresenter.refreshContactsInLocal();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        contactsPresenter.getApplyUnread();
    }
}
