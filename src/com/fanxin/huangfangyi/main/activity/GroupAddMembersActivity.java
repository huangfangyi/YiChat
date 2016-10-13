package com.fanxin.huangfangyi.main.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.adapter.PickContactAdapter;
import com.fanxin.huangfangyi.main.utils.GroupUitls;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.huangfangyi.ui.BaseActivity;
import com.fanxin.easeui.domain.EaseUser;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class GroupAddMembersActivity extends BaseActivity {
    private ImageView iv_search;
    private TextView tv_checked;
    private ListView listView;
   //是否新建群
    protected boolean isCreatingNewGroup;
    private PickContactAdapter contactAdapter;
    private List<String> exitingMembers = new ArrayList<String>();
    // 可滑动的显示选中用户的View
    private LinearLayout menuLinerLayout;
    // 选中用户总数,右上角显示
    private String userId = null;
    private String groupId = null;
    private String groupname;
    // 添加的列表
    private List<String> addList = new ArrayList<String>();
    private List<EaseUser> friendList;
    private EMGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fx_activity_group_create);
        initView();
        groupId = getIntent().getStringExtra("groupId");
        userId = getIntent().getStringExtra("userId");
        if (groupId != null) {
            isCreatingNewGroup = false;
            group = EMClient.getInstance().groupManager().getGroup(groupId);
            if (group != null) {
                exitingMembers = group.getMembers();
                groupname = group.getGroupName();
            }
        } else if (userId != null) {
            isCreatingNewGroup = true;
            exitingMembers.add(userId);
            addList.add(userId);
        } else {
            isCreatingNewGroup = true;
        }
        // 获取好友列表
        friendList = new ArrayList<EaseUser>(DemoHelper.getInstance().getContactList().values());
        // 对list进行排序
        Collections.sort(friendList, new PinyinComparator() {
        });
        refreshList(friendList);


    }

    private void initView() {
        tv_checked = (TextView) this.findViewById(R.id.tv_save);
        listView = (ListView) findViewById(R.id.list);
        iv_search = (ImageView) this.findViewById(R.id.iv_search);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        View headerView = layoutInflater.inflate(R.layout.fx_item_group_create_header,
//                null);
//        headerView.findViewById(R.id.tv_header).setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(GroupAddMembersActivity.this,
//                        GroupListActivity.class));
//                finish();
//            }
//
//        });
//        listView.addHeaderView(headerView);
        menuLinerLayout = (LinearLayout) this
                .findViewById(R.id.linearLayoutMenu);
        //设置监听
        setTextChangedListener((EditText) this.findViewById(R.id.et_search));
        tv_checked.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                save();
            }

        });
    }


    private void setTextChangedListener(final EditText etSearch) {
        etSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    String str_s = etSearch.getText().toString().trim();
                    List<EaseUser> usersTemp = new ArrayList<EaseUser>();
                    for (EaseUser user : friendList) {
                        if (user.getNick().contains(str_s)) {
                            usersTemp.add(user);
                        }
                    }
                    refreshList(usersTemp);
                } else {
                    refreshList(friendList);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void refreshList(List<EaseUser> users) {
        contactAdapter = new PickContactAdapter(GroupAddMembersActivity.this, users, exitingMembers);
        listView.setAdapter(contactAdapter);
    }

    // 即时显示被选中用户的头像和昵称。
    public void showCheckImage(Bitmap bitmap, EaseUser user) {

        if (exitingMembers.contains(user.getUsername()) && groupId != null) {
            return;
        }
        if (addList.contains(user.getUsername())) {
            return;
        }

        // 包含TextView的LinearLayout
        // 参数设置
        LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                128, 128, 1);
        View view = LayoutInflater.from(this).inflate(
                R.layout.fx_item_avatar, null);
        ImageView images = (ImageView) view.findViewById(R.id.iv_avatar);
        menuLinerLayoutParames.setMargins(6, 6, 6, 6);
        // 设置id，方便后面删除
        view.setTag(user);
        if (bitmap == null) {
            images.setImageResource(R.drawable.fx_default_useravatar);
        } else {
            images.setImageBitmap(bitmap);
        }
        menuLinerLayout.addView(view, menuLinerLayoutParames);

        if (addList.size() > 0) {
            if (iv_search.getVisibility() == View.VISIBLE) {
                iv_search.setVisibility(View.GONE);
            }
        }
        addList.add(user.getUsername());
        tv_checked.setText("确定(" + addList.size() + ")");
    }

    public void deleteImage(EaseUser user) {
        View view = menuLinerLayout.findViewWithTag(user);
        menuLinerLayout.removeView(view);
        addList.remove(user.getUsername());
        tv_checked.setText("确定(" + addList.size() + ")");
        if (addList.size()  < 1) {
            if (iv_search.getVisibility() == View.GONE) {
                iv_search.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 确认选择的members
     */
    public void save() {
        if (addList.size() == 0) {
            Toast.makeText(GroupAddMembersActivity.this, "请选择好友", Toast.LENGTH_LONG).show();
            return;
        }
        if (isCreatingNewGroup) {
            //只有一個人，直接进入聊天界面
            if (addList.size() == 1) {
                String userId = addList.get(0);
                startActivity(new Intent(getApplicationContext(),
                        ChatActivity.class).putExtra("userId", userId));
                finish();
                return;
            }
            //否则进入创建群组
            creatGroupNew(addList);
        } else {
            //已有群加人
            existsGroupAddMembers(addList);
        }
    }

    /**
     * 创建新群组
     */
    private void creatGroupNew(List<String> members) {
        JSONObject myJson = new JSONObject();
        myJson.put(FXConstant.JSON_KEY_HXID, DemoApplication.getInstance().getUserJson().getString(FXConstant.JSON_KEY_HXID));
        myJson.put(FXConstant.JSON_KEY_NICK, DemoApplication.getInstance().getUserJson().getString(FXConstant.JSON_KEY_NICK));
        myJson.put(FXConstant.JSON_KEY_AVATAR, DemoApplication.getInstance().getUserJson().getString(FXConstant.JSON_KEY_AVATAR));
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(myJson);
        for (int i = 0; i < members.size(); i++) {
            EaseUser user = DemoHelper.getInstance().getContactList()
                    .get(members.get(i));
            if (user != null) {
                JSONObject json_member = new JSONObject();
                json_member.put(FXConstant.JSON_KEY_HXID, user.getUsername());
                json_member.put(FXConstant.JSON_KEY_NICK, user.getNick());
                json_member.put(FXConstant.JSON_KEY_AVATAR, user.getAvatar());
                jsonArray.add(json_member);
            }
            if (i > 8) {
                //最多保存9个用户的头像昵称
                break;
            }
        }
        JSONObject finalJson = new JSONObject();
        //jsonArray保存用户资料，最多9个人的
        finalJson.put("jsonArray", jsonArray);
        //群聊的真正群名称-默认以未命名为标识
        finalJson.put("groupname", "未命名");
        //调用SDK方法
        creatEMGroup(finalJson.toJSONString(), members);
    }
    private void creatEMGroup(final String jsonInfo, final List<String> members) {
        String membersString = "";
        for (int i = 0; i < members.size(); i++) {
            if (i == 0) {
                membersString = members.get(i);
            } else {

                membersString =membersString+ "#" + members.get(i);
            }
        }
        creatGroupInServer(jsonInfo,  "temp", membersString, "false");
    }
    private void creatGroupInServer(String groupName, String desc, String members, String isPublic) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在创建群组");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        List<Param> params = new ArrayList<>();
        params.add(new Param("owner", DemoHelper.getInstance().getCurrentUsernName()));
        params.add(new Param("members", members));
        params.add(new Param("groupName", groupName));
        params.add(new Param("desc", desc));
        params.add(new Param("public", isPublic));

        OkHttpManager.getInstance().post(params, FXConstant.URL_GROUP_CREATE, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                if (code == 1000) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data.containsKey("data")) {
                        JSONObject jsonGroupId = data.getJSONObject("data");
                        String groupId = jsonGroupId.getString("groupid");
                        if (!TextUtils.isEmpty(groupId)) {
                            progressDialog.dismiss();
                            setResult(Activity.RESULT_OK);
                            finish();
                            return;

                        }
                    }
                }

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "创建失败...", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(String errorMsg) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "创建失败...", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void existsGroupAddMembers(final List<String> members) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加人...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        GroupUitls.getInstance().addMembersToGroup(groupId, members, exitingMembers, new GroupUitls.CallBack() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"加人成功",Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();

             }

            @Override
            public void onError() {
                progressDialog.dismiss();
                 Toast.makeText(getApplicationContext(),"加人失败 ...",Toast.LENGTH_SHORT).show();
            }
        });

    }


    public class PinyinComparator implements Comparator<EaseUser> {
        @Override
        public int compare(EaseUser o1, EaseUser o2) {
            String py1 = o1.getInitialLetter();
            String py2 = o2.getInitialLetter();
            // 判断是否为空""
            if (isEmpty(py1) && isEmpty(py2))
                return 0;
            if (isEmpty(py1))
                return -1;
            if (isEmpty(py2))
                return 1;
            String str1 = "";
            String str2 = "";
            try {
                str1 = ((o1.getInitialLetter()).toUpperCase()).substring(0, 1);
                str2 = ((o2.getInitialLetter()).toUpperCase()).substring(0, 1);
            } catch (Exception e) {
                System.out.println("某个str为\" \" 空");
            }
            return str1.compareTo(str2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }

}
