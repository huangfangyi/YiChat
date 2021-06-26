package com.htmessage.yichat.acitivity.chat.group.managerlist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.yichat.domain.User;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.widget.HTAlertDialog;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.MsgUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ManagerListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView lv_group_member;
    private List<User> membersList = new ArrayList<>();
    private String groupId;
    private HTGroup htGroup;
    private EditText searchView;
    private ImageView iv_clear;
    private ManagerListAdapter adapter;


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1000:
                    Toast.makeText(getActivity(), R.string.set_successful, Toast.LENGTH_SHORT).show();
                        JSONArray managerList = GroupInfoManager.getInstance().getGroupManagers(groupId);
                    membersList.clear();
                    membersList.addAll(arrayToList(managerList)) ;
                    adapter.notifyDataSetChanged();
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        int status = bundle.getInt("status");
                        int action = 30002;
                        if (status == 0) {
                            action = 30003;
                        }
                        MsgUtils.getInstance().sendCancleOrSetManagerCmdMsg(bundle.getString("userId"), groupId, action);

                    }
                    break;
                case 1001:
                    int resId = msg.arg1;
                    Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();

                    break;

                case 1002:
                    JSONArray jsonArray = (JSONArray) msg.obj;
                    membersList.clear();
                    membersList.addAll(arrayToList(jsonArray));
                    adapter.notifyDataSetChanged();
                    break;
            }

        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_group_member, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        lv_group_member.setOnItemClickListener(this);
        iv_clear.setOnClickListener(this);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    iv_clear.setVisibility(View.VISIBLE);
                    String newText = searchView.getText().toString();
                    List<User> usersTemp = new ArrayList<User>();
                    for (User user : membersList) {
                        String username = user.getUserId();
                        String nick = user.getNick();
                        if (!TextUtils.isEmpty(nick) && nick.contains(newText)) {
                            usersTemp.add(user);
                        }

                    }
                    refreshListView(usersTemp);
                } else {
                    iv_clear.setVisibility(View.GONE);
                    refreshListView(membersList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initData() {
        refreshListView(membersList);
        GroupInfoManager.getInstance().refreshManagerInserver(groupId, new GroupInfoManager.CallBack() {
            @Override
            public void onDataSuccess(JSONArray jsonArray) {
                if (handler == null) {
                    return;
                }
                Message message = handler.obtainMessage();
                message.obj = jsonArray;
                message.what = 1002;
                message.sendToTarget();
            }
        });
    }

    private void initView() {
        lv_group_member = (ListView) getView().findViewById(R.id.lv_group_member);
        searchView = (EditText) getView().findViewById(R.id.edt_search);
        iv_clear = (ImageView) getView().findViewById(R.id.iv_clear);
    }

    private void getData() {
        groupId = getActivity().getIntent().getStringExtra("groupId");
        if (groupId == null) {
            getActivity().finish();
            return;
        }
        htGroup = HTClient.getInstance().groupManager().getGroup(groupId);
        if (htGroup == null) {
            getActivity().finish();
            return;
        }
        JSONArray managerList = GroupInfoManager.getInstance().getGroupManagers(groupId);
        membersList.addAll(arrayToList(managerList));

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final User user = adapter.getItem(position);
        if (UserManager.get().getMyUserId().equals(htGroup.getOwner())) {
            HTAlertDialog alertDialog = new HTAlertDialog(getContext(), null, new String[]{"取消管理员身份"});
            alertDialog.init(new HTAlertDialog.OnItemClickListner() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 0:
                            handManager(user.getUserId(), 0);
                            break;
                    }
                }
            });

        }

        //    CommonUtils.showGroupMemberAvatarClickDiaolog(getActivity(), user.getUserId(), groupId, htGroup.getOwner());
    }


    public void handManager(final String userId, final int status) {
        if(htGroup.getOwner().equals(userId)){
            CommonUtils.showToastShort(getContext(),"群主已经是超级管理员");
            return;
        }

        JSONObject data = new JSONObject();
        data.put("userIds", userId);
        data.put("groupId", groupId);
        //1是设置 0是取消
        data.put("status", status);
        ApiUtis.getInstance().postJSON(data, Constant.URL_GROUP_MANAGER, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (handler == null) {
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    if (status == 0) {
                        GroupInfoManager.getInstance().removManager(groupId, userId);
                    } else {
                        GroupInfoManager.getInstance().addManager(groupId, userId);

                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", userId);
                    bundle.putInt("status", status);
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.setData(bundle);
                    message.sendToTarget();
                } else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.set_failed;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });


    }


    private void refreshListView(List<User> membersJSONArray) {
        Collections.sort(membersJSONArray, new PinyinComparator() {
        });
        adapter = new ManagerListAdapter(getActivity(), membersJSONArray);
        lv_group_member.setAdapter(adapter);
    }


    private List<User> arrayToList(JSONArray data) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            User user = new User(jsonObject);
            users.add(user);
        }

        return users;


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear:
                searchView.getText().clear();
                iv_clear.setVisibility(View.GONE);
                break;
        }
    }

    public class PinyinComparator implements Comparator<User> {

        @SuppressLint("DefaultLocale")
        @Override
        public int compare(User o1, User o2) {
            String py1 = o1.getInitialLetter();
            String py2 = o2.getInitialLetter();
            if (py1.equals(py2)) {
                return o1.getNick().compareTo(o2.getNick());
            } else {
                if ("#".equals(py1)) {
                    return 1;
                } else if ("#".equals(py2)) {
                    return -1;
                }
                return py1.compareTo(py2);
            }
        }
    }


    @Override
    public void onDestroy() {
        handler = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

    }


}
