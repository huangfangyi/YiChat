package com.htmessage.yichat.acitivity.chat.group.allmember;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.details.UserDetailActivity;
import com.htmessage.yichat.domain.User;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.yichat.widget.HTAlertDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 项目名称：fanxinht
 * 类描述：AllGroupMemberFragment 描述:
 * 创建人：songlijie
 * 创建时间：2018/3/22 17:10
 * 邮箱:814326663@qq.com
 */
public class AllGroupMemberFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
     private ListView lv_group_member;
    private List<User> membersJSONArray = new ArrayList<>();
    private String groupId;
    private HTGroup htGroup;
    private EditText searchView;
    private ImageView iv_clear;
    private AllGroupMemberAdapter adapter;

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
                    for (User user : membersJSONArray) {
                        String username = user.getUserId();
                        String nick = UserManager.get().getUserNick(username);
                        if(nick.contains(newText)){
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
        });
    }

    private void initData() {
        refreshList(membersJSONArray);
        refreshMenebers();
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

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = adapter.getItem(position);
         //是否是增加群管理员
        int type=getActivity().getIntent().getIntExtra("type",0);
        if(type==1){
            //选择群成员作为管理员
            Intent intent=new Intent();
            intent.putExtra("userId",user.getUserId());

            getActivity().setResult(Activity.RESULT_OK,intent);

            getActivity().finish();
            return;
        }

         if(GroupInfoManager.getInstance().isManager(groupId)){
             new HTAlertDialog(getActivity(),null,new String[]{"查看资料","禁言"}).init(new HTAlertDialog.OnItemClickListner() {
                 @Override
                 public void onClick(int position) {
                     switch (position){
                         case 0:
                             startActivity(new Intent(getActivity(), UserDetailActivity.class).putExtra("userId",user.getUserId()));

                             break;
                         case 1:
                             GroupInfoManager.getInstance().addSilentUsers(groupId,user.getUserId());
                             break;
                     }


                 }
             });


        }

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

    @Override
    public void onRefresh() {

    }

    private void refreshList(List<User> userList) {
        adapter = new AllGroupMemberAdapter(getActivity(), userList);
        lv_group_member.setAdapter(adapter);
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



    public void refreshMenebers() {


        new Thread(new Runnable() {
              @Override
              public void run() {
                  JSONArray members= GroupInfoManager.getInstance().getGroupAllMembersFromLocal(groupId);
                  final List<User> users=new ArrayList<>();
                  if(members!=null){
                      for(int i=0;i<members.size();i++){
                          JSONObject jsonObject=members.getJSONObject(i);
                          User user=new User(jsonObject);
                          users.add(user);
                      }

                  }
                  Collections.sort(users, new PinyinComparator() {
                  });
                  if(AllGroupMemberFragment.this!=null&&getActivity()!=null){
                      getActivity().runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              membersJSONArray.clear();
                              membersJSONArray.addAll(users);
                              refreshList(membersJSONArray);
                          }
                      });
                  }

              }
          }).start();
    }

}
