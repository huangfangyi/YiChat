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
package com.fanxin.huangfangyi.main.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.db.InviteMessgeDao;
import com.fanxin.huangfangyi.db.UserDao;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.activity.GroupListActivity;
import com.fanxin.huangfangyi.main.activity.NewFriendsActivity;
import com.fanxin.huangfangyi.main.activity.UserDetailsActivity;
import com.fanxin.huangfangyi.main.activity.LiveActivity;
import com.fanxin.huangfangyi.main.activity.LivesActivity;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.easeui.domain.EaseUser;
import com.fanxin.easeui.ui.EaseContactListFragment;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.NetUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**

 * contact list
 *
 */
public class ContactListFragment extends EaseContactListFragment implements View.OnClickListener {

    private static final String TAG = ContactListFragment.class.getSimpleName();
    private TextView tvUnread;
    private InviteMessgeDao inviteMessgeDao;

    @Override
    protected void initView() {
        super.initView();

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.fx_item_contact_list_header, null);
        headerView.findViewById(R.id.re_newfriends).setOnClickListener(this);
        headerView.findViewById(R.id.re_chatroom).setOnClickListener(this);
        headerView.findViewById(R.id.re_tag).setOnClickListener(this);
        headerView.findViewById(R.id.re_public).setOnClickListener(this);
        tvUnread = (TextView) headerView.findViewById(R.id.tv_unread);
        listView.addHeaderView(headerView);
        this.titleBar.setVisibility(View.GONE);
        getView().findViewById(R.id.search_bar_view).setVisibility(View.GONE);
        registerForContextMenu(listView);
    }

    @Override
    public void refresh() {
        Map<String, EaseUser> m = DemoHelper.getInstance().getContactList();
        if (m instanceof Hashtable<?, ?>) {
            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>) m).clone();
        }
        setContactsMap(m);
        super.refresh();
        if (inviteMessgeDao == null) {
            inviteMessgeDao = new InviteMessgeDao(getActivity());
        }
        if (inviteMessgeDao.getUnreadMessagesCount() > 0) {
            tvUnread.setVisibility(View.VISIBLE);
        } else {
            tvUnread.setVisibility(View.GONE);
        }
    }


    @Override
    protected void setUpView() {
        titleBar.setRightImageResource(R.drawable.em_add);
        titleBar.setRightLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                NetUtils.hasDataConnection(getActivity());
            }
        });
        //设置联系人数据
        Map<String, EaseUser> m = DemoHelper.getInstance().getContactList();
        if (m instanceof Hashtable<?, ?>) {
            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>) m).clone();
        }
        setContactsMap(m);
        super.setUpView();
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    EaseUser user = ((EaseUser) listView.getItemAtPosition(position));
                    if (user != null && user.getUserInfo() != null) {
                        startActivity(new Intent(getActivity(), UserDetailsActivity.class).putExtra(FXConstant.KEY_USER_INFO, user.getUserInfo()));
                    }


                } catch (NullPointerException e) {
                    e.printStackTrace();

                }

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_newfriends:
                // 进入申请与通知页面
                startActivity(new Intent(getActivity(), NewFriendsActivity.class));
                break;
            case R.id.re_chatroom:
                // 进入群聊列表页面
                startActivity(new Intent(getActivity(), GroupListActivity.class));
                break;
            case R.id.re_tag:
                //进入直播间
//                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("chatType", 3).
//                        putExtra("userId",FXConstant.FXLIVE_CHATROOM_ID));
                startActivity(new Intent(getActivity(), LivesActivity.class));
                break;
            case R.id.re_public:
                //进入Robot列表页面
                startActivity(new Intent(getActivity(), LiveActivity.class));
                break;

            default:
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        toBeProcessUser = (EaseUser) listView.getItemAtPosition(((AdapterContextMenuInfo) menuInfo).position);
        toBeProcessUsername = toBeProcessUser.getUsername();
        getActivity().getMenuInflater().inflate(R.menu.em_context_contact_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_contact) {
            //TODO 此接口需要调用后端接口
            //    Toast.makeText(getContext(),"此接口改调凡信后端接口，待更新...",Toast.LENGTH_LONG).show();

            deleteContact(toBeProcessUser);


            return true;
        }
//        else if (item.getItemId() == R.id.add_to_blacklist) {
//            //TODO 此接口需要调用后端接口
//            Toast.makeText(getContext(), "此接口改调凡信后端接口，待更新...", Toast.LENGTH_LONG).show();
//            //moveToBlacklist(toBeProcessUsername);
//            return true;
//        }
        return super.onContextItemSelected(item);
    }

    /**
     * delete contact
     *
     * @param
     */
    public void deleteContact(final EaseUser tobeDeleteUser) {
        final String st1 = getResources().getString(R.string.deleting);
        final String st2 = getResources().getString(R.string.Delete_failed);
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        List<Param> paramList = new ArrayList<>();
        paramList.add(new Param("hxid_from", tobeDeleteUser.getUsername()));
        paramList.add(new Param("hxid_to", DemoHelper.getInstance().getCurrentUsernName()));
        OkHttpManager.getInstance().post(paramList, FXConstant.URL_DELETE_FRIEND, new OkHttpManager.HttpCallBack() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                pd.dismiss();
                int code = jsonObject.getInteger("code");
                if (code == 1000) {
                    InviteMessgeDao dao = new InviteMessgeDao(getActivity());
                    dao.deleteMessage(toBeProcessUser.getUsername());
                    UserDao userDao = new UserDao(getActivity());
                    userDao.deleteContact(tobeDeleteUser.getUsername());
                    DemoHelper.getInstance().getContactList().remove(tobeDeleteUser.getUsername());
                    contactList.remove(tobeDeleteUser);
                    contactListLayout.refresh();
                    sendCmdDeleteMsg(toBeProcessUser.getUsername());
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), st2, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                pd.dismiss();
                Toast.makeText(getContext(), st2, Toast.LENGTH_SHORT).show();

            }
        });


//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    EMClient.getInstance().contactManager().deleteContact(tobeDeleteUser.getUsername());
//                    // remove user from memory and database
//                    UserDao dao = new UserDao(getActivity());
//                    dao.deleteContact(tobeDeleteUser.getUsername());
//                    DemoHelper.getInstance().getContactList().remove(tobeDeleteUser.getUsername());
//                    getActivity().runOnUiThread(new Runnable() {
//                        public void run() {
//                            pd.dismiss();
//                            contactList.remove(tobeDeleteUser);
//                            contactListLayout.refresh();
//
//                        }
//                    });
//                } catch (final Exception e) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        public void run() {
//                            pd.dismiss();
//                            Toast.makeText(getActivity(), st2 + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                }
//            }
//        }).start();

    }

    private void sendCmdDeleteMsg(String hxid) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        //支持单聊和群聊，默认单聊，
        cmdMsg.setChatType(EMMessage.ChatType.Chat);
        //action可以自定义
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(FXConstant.CMD_DELETE_FRIEND);
        cmdMsg.setReceipt(hxid);
        cmdMsg.addBody(cmdBody);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);

    }


}
