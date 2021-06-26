package com.htmessage.yichat.acitivity.chat.pick;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.domain.User;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 项目名称：YiChatDemoTwo
 * 类描述：PickAtUserActivity 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/15 11:20
 * 邮箱:814326663@qq.com
 */
public class PickAtUserActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private String groupId;
    private TextView tv_title;
    private EditText edt_search;
    private ListView list;
    private PickAtUserAdapter adapter;
    private ImageView iv_clear;

    /**
     * group中一开始就有的成员
     */
    private List<User> exitingMembers = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_pick_at_user);

        getData();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setTextChangedListener(edt_search);
        list.setOnItemClickListener(this);
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search.getText().clear();
            }
        });
    }

    private void refreshList(List<User> exitingMembers) {
        Collections.sort(exitingMembers, new PinyinComparator() {});
        adapter = new PickAtUserAdapter(this, exitingMembers);
        list.setAdapter(adapter);
    }

    private void initData() {
        tv_title.setText(R.string.Select_the_contact);
        // 对list进行排序
        refreshList(exitingMembers);
        if (getIsOwner(groupId, HTApp.getInstance().getUsername())) {
            addHeadView();
        }
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        list = (ListView) findViewById(R.id.list);
        edt_search = (EditText) findViewById(R.id.edt_search);
        iv_clear = (ImageView) findViewById(R.id.iv_clear);
    }

    private void addHeadView() {
        View view = LayoutInflater.from(this).inflate(R.layout.item_groups, list, false);
        ImageView avatarView = (ImageView) view.findViewById(R.id.iv_avatar);
        TextView textView = (TextView) view.findViewById(R.id.tv_name);
        textView.setText(getString(R.string.all_members));
        avatarView.setImageResource(R.drawable.default_group);
        list.addHeaderView(view);
    }

    private void getData() {
        groupId = getIntent().getStringExtra("groupId");
        if (TextUtils.isEmpty(groupId)) {
            finish();
            return;
        }
        JSONArray jsonArray = GroupInfoManager.getInstance().getGroupAllMembersFromLocal(groupId);
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                User user=new User(jsonObject);
                exitingMembers.add(user);
            }
        }else{
         }
    }

    private void setTextChangedListener(final EditText etSearch) {
        etSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    iv_clear.setVisibility(View.VISIBLE);
                    String str_s = etSearch.getText().toString().trim();
                    List<User> usersTemp = new ArrayList<User>();
                    for (User user : exitingMembers) {
                        if (user.getNick().contains(str_s)) {
                            usersTemp.add(user);
                        }
                    }
                    refreshList(usersTemp);
                } else {
                    iv_clear.setVisibility(View.GONE);
                    refreshList(exitingMembers);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getIsOwner(groupId, HTApp.getInstance().getUsername())) {
            if (position != 0) {
                User user = (User) list.getItemAtPosition(position);
                if (HTApp.getInstance().getUsername().equals(user.getUserId())) {
                    return;
                }
                setResult(RESULT_OK, new Intent().putExtra(HTConstant.JSON_KEY_USERID, user.getUserId()));
            } else {
                setResult(RESULT_OK, new Intent().putExtra(HTConstant.JSON_KEY_USERID, getString(R.string.all_members)));
            }
        } else {
            User user = (User) list.getItemAtPosition(position);
            if (HTApp.getInstance().getUsername().equals(user.getUserId())) {
                return;
            }
            setResult(RESULT_OK, new Intent().putExtra(HTConstant.JSON_KEY_USERID, user.getUserId()));
        }
        finish();
    }

    public class PinyinComparator implements Comparator<User> {
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

    private boolean getIsOwner(String groupId, String userId) {
        return  GroupInfoManager.getInstance().isManager(groupId);
    }


}
