package com.htmessage.yichat.acitivity.chat.group.deletemember;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.domain.User;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.update.data.GroupInfoManager;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：fanxinht
 * 类描述：DeleteGroupMemberFragment 描述:
 * 创建人：songlijie
 * 创建时间：2018/3/21 16:10
 * 邮箱:814326663@qq.com
 */
public class DeleteGroupMemberFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, TextWatcher {
    private TextView tv_title;
    private Button btn_rtc;
    private ListView lv_group_member;
    private JSONArray membersJSONArray = new JSONArray();
    private JSONArray users = new JSONArray();//要删除的用户
    private String groupId;
    private HTGroup htGroup;
    private DeleteGroupMemberAdapter adapter;
    private EditText edt_search;
    private ImageView iv_clear, iv_back;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delete_group_member, container, false);
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
        btn_rtc.setOnClickListener(this);
        iv_clear.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        edt_search.addTextChangedListener(this);
    }

    private void initData() {
        tv_title.setText(R.string.check_delete_group_member);
        btn_rtc.setVisibility(View.VISIBLE);
        btn_rtc.setText(R.string.delete);
        refreshList(membersJSONArray);
        refreshMenebers();
    }

    private void initView() {
        tv_title = (TextView) getView().findViewById(R.id.tv_title);
        btn_rtc = (Button) getView().findViewById(R.id.btn_rtc);
        lv_group_member = (ListView) getView().findViewById(R.id.lv_group_member);
        edt_search = (EditText) getView().findViewById(R.id.edt_search);
        iv_clear = (ImageView) getView().findViewById(R.id.iv_clear);
        iv_back = (ImageView) getView().findViewById(R.id.iv_back);
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
        membersJSONArray = GroupInfoManager.getInstance().getGroupAllMembersFromLocal(groupId);
        if (membersJSONArray == null) {
            membersJSONArray = new JSONArray();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        JSONObject item = adapter.getItem(position);
        String userId = item.getString("userId");

        if (GroupInfoManager.getInstance().userIsManager(userId,groupId) || htGroup.getOwner().equals(userId)) {
            CommonUtils.showToastShort(getActivity(), "群主或者群管理员不可被删除");
            return;
        }
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.toggle();
        // 调整选定条目
        if (users.size() < 11) {
            if (checkBox.isChecked() == true) {
                users.add(item);
                adapter.addUser(item);
            } else {
                adapter.removeUser(item);
                users.remove(item);
            }
        } else {
            users.remove(item);
            checkBox.setChecked(false);
            adapter.removeUser(item);
            CommonUtils.showToastShort(getActivity(), R.string.just_delete_one);
        }
    }

    @Override
    public void onRefresh() {

    }

    private void refreshList(JSONArray userList) {
        adapter = new DeleteGroupMemberAdapter(getActivity(), userList);
        lv_group_member.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_rtc:
                if (users.size() == 0) {
                    CommonUtils.showToastShort(getActivity(), R.string.check_delete_group_member);
                    return;
                }
                CommonUtils.showDialogNumal(getActivity(), getString(R.string.deleting));
                deleteMembers(users);
//                for (int i = 0; i < users.size(); i++) {
//                    JSONObject user = users.getJSONObject(i);
//
//                    if (GroupInfoManager.getInstance().getGroupManagers(groupId).toJSONString().contains(user.getString("userId")) || htGroup.getOwner().equals(user.getString("userId"))) {
//                        CommonUtils.showToastShort(getActivity(), R.string.not_delete_self);
//                        break;
//                    }
//                    if (i == users.size() - 1) {
//                        deleteMembers(user );
//                    } else {
//                        deleteMembers(user.getString("userId"), false);
//                    }
//
//                }
                break;
            case R.id.iv_clear:
                edt_search.getText().clear();
                iv_clear.setVisibility(View.GONE);
                break;
            case R.id.iv_back:
                back();
                getActivity().finish();
                break;
        }
    }

    public void back() {
        if (adapter != null) {
            adapter.removeAllUser();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            iv_clear.setVisibility(View.VISIBLE);
            String newText = edt_search.getText().toString();
            JSONArray usersTemp = new JSONArray();
            for (int i = 0; i < membersJSONArray.size(); i++) {
                JSONObject user = membersJSONArray.getJSONObject(i);
                String nick = UserManager.get().getUserNick(user.getString("userId"));
                if (nick.contains(newText)) {
                    usersTemp.add(user);
                }

            }
            refreshList(usersTemp);
        } else {
            iv_clear.setVisibility(View.GONE);
            refreshList(membersJSONArray);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

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

    public void deleteMembers(final JSONArray memberUsers) {
        Map<String,String> members=new HashMap<>();
        for(int i=0;i<memberUsers.size();i++){
            JSONObject userJson=memberUsers.getJSONObject(i);
            members.put(userJson.getString("userId"),userJson.getString("nick"));
        }

        if (members.keySet().contains(HTApp.getInstance().getUsername())) {
            CommonUtils.showToastShort(getActivity(), R.string.can_not_remove_self);
            return;
        }



        HTClient.getInstance().groupManager().deleteMemberByNormal(groupId,members, htGroup.getOwner(), Constant.GROUP_DELETE_MEMBER_NOTIFY, new GroupManager.CallBack() {
            @Override
            public void onSuccess(String data) {
                //GroupInfoManager.getInstance().removMember(groupId, memberUserId);
                if ( getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            CommonUtils.cencelDialog();
                            CommonUtils.showToastShort(getActivity(), "删除成功");

                            getActivity().finish();


                        }
                    });
                }

            }

            @Override
            public void onFailure() {
                if ( getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            CommonUtils.cencelDialog();
                            CommonUtils.showToastShort(getActivity(), "删除失败");
                        }
                    });
                }
            }

            @Override
            public void onHTMessageSend(HTMessage htMessage) {
                LocalBroadcastManager.getInstance(HTApp.getContext()).sendBroadcast(new Intent(IMAction.ACTION_NEW_MESSAGE).putExtra("message",htMessage));

                CommonUtils.upLoadMessage(htMessage);
            }
        });
    }

    public void refreshMenebers() {


//        List<Param> params = new ArrayList<>();
//        params.add(new Param("gid", groupId));
//        params.add(new Param("uid", HTApp.getInstance().getUserId()));
//        new OkHttpUtils(getActivity()).post(params, HTConstant.URL_GROUP_MEMBERS, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONArray array = jsonObject.getJSONArray("data");
//                        arrayToList(array, membersJSONArray);
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//            }
//        });
    }
}
