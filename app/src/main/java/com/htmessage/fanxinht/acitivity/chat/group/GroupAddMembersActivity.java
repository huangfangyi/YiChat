package com.htmessage.fanxinht.acitivity.chat.group;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.utils.MessageUtils;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.acitivity.chat.ChatActivity;
import com.htmessage.fanxinht.acitivity.chat.PickContactAdapter;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.manager.ContactsManager;
import com.htmessage.fanxinht.utils.ACache;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroupAddMembersActivity extends BaseActivity {
    private ImageView iv_search;
    private TextView tv_checked;
    private ListView listView;
    /**
     * 是否为一个新建的群组
     */
    protected boolean isCreatingNewGroup;

    private PickContactAdapter contactAdapter;
    /**
     * group中一开始就有的成员
     */
    private List<String> exitingMembers = new ArrayList<String>();
    // 可滑动的显示选中用户的View
    private LinearLayout menuLinerLayout;

    // 选中用户总数,右上角显示

    private String userId = null;
    private String groupId = null;

    private String groupname;
    // 添加的列表
    private List<String> addList = new ArrayList<String>();

    //添加的朋友的列表
    private List<String> addUserNames = new ArrayList<String>();

    private List<User> friendList;
    private HorizontalScrollView horizonMenu;

    //private View viewSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        initView();
        groupId = getIntent().getStringExtra("groupId");
        userId = getIntent().getStringExtra("userId");
        if (groupId != null) {
            isCreatingNewGroup = false;
            JSONArray jsonArray = ACache.get(getApplicationContext()).getAsJSONArray(HTApp.getInstance().getUsername() + groupId);
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    exitingMembers.add(jsonArray.getJSONObject(i).getString(HTConstant.JSON_KEY_HXID));
                    addUserNames.add(jsonArray.getJSONObject(i).getString(HTConstant.JSON_KEY_NICK));
                }
            }
        } else if (userId != null) {
            isCreatingNewGroup = true;
            exitingMembers.add(userId);
            List<User> values = new ArrayList<User>(ContactsManager.getInstance().getContactList().values());
            for (int i = 0; i < values.size(); i++) {
                String username = values.get(i).getUsername();
                if (userId.equals(username)) {
                    addUserNames.add(values.get(i).getNick());
                }
            }
            addList.add(userId);
        } else {
            isCreatingNewGroup = true;
        }
        if (isCreatingNewGroup) {
            etGroupName.setVisibility(View.VISIBLE);
        } else {
            etGroupName.setVisibility(View.GONE);
        }

        // 获取好友列表
        friendList = new ArrayList<User>(ContactsManager.getInstance().getContactList().values());
        // 对list进行排序
        Collections.sort(friendList, new PinyinComparator() {
        });
        refreshList(friendList);


    }

    private EditText etGroupName;

    private void initView() {
        tv_checked = (TextView) this.findViewById(R.id.tv_save);
        listView = (ListView) findViewById(R.id.list);
        iv_search = (ImageView) this.findViewById(R.id.iv_search);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View headerView = layoutInflater.inflate(R.layout.item_group_create_header, null);

        etGroupName = (EditText) headerView.findViewById(R.id.et_header);
        horizonMenu = (HorizontalScrollView) this.findViewById(R.id.horizonMenu);

        //TODO 屏蔽headview
//        listView.addHeaderView(headerView);
        menuLinerLayout = (LinearLayout) this.findViewById(R.id.linearLayoutMenu);
        //   viewSearch  = LayoutInflater.from(this).inflate(R.layout.item_avatar, null);
//        ImageView images = (ImageView) viewSearch.findViewById(R.id.iv_avatar);
//        LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,   LinearLayout.LayoutParams.WRAP_CONTENT, 1);
//        menuLinerLayoutParames.setMargins(6, 6, 6, 6);
//        viewSearch.setTag("viewSearch");
//        menuLinerLayout.addView(viewSearch,0,menuLinerLayoutParames);
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
                    List<User> usersTemp = new ArrayList<User>();
                    for (User user : friendList) {
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

    private void refreshList(List<User> users) {
        contactAdapter = new PickContactAdapter(GroupAddMembersActivity.this, users, exitingMembers);
        listView.setAdapter(contactAdapter);
    }

    // 即时显示被选中用户的头像和昵称。
    public void showCheckImage(final Bitmap bitmap, User user) {

        if (exitingMembers.contains(user.getUsername()) && groupId != null) {
            return;
        }
        if (addList.contains(user.getUsername())) {
            return;
        }
        if (addUserNames.contains(user.getNick())) {
            return;
        }
        addList.add(user.getUsername());
        addUserNames.add(user.getNick());
        tv_checked.setText(getString(R.string.ok_xiaokuohao) + addList.size() + getString(R.string.xiaokuohao));
        if (addList.size() == 0) {

            iv_search.setImageResource(R.drawable.icon_search_gray);

        } else if (addList.size() == 1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (bitmap == null) {
                        iv_search.setImageResource(R.drawable.default_avatar);
                    } else {
                        iv_search.setImageBitmap(bitmap);
                    }

                }
            },200);

        } else {
            // 包含TextView的LinearLayout
            // 参数设置
            LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            View view = LayoutInflater.from(this).inflate(R.layout.item_avatar, null);
            ImageView images = (ImageView) view.findViewById(R.id.iv_avatar);
            menuLinerLayoutParames.setMargins(6, 6, 6, 6);
            // 设置id，方便后面删除
            view.setTag(user);
            if (bitmap == null) {
                images.setImageResource(R.drawable.default_avatar);
            } else {
                images.setImageBitmap(bitmap);
            }
            menuLinerLayout.addView(view, menuLinerLayoutParames);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    horizonMenu.fullScroll(HorizontalScrollView.FOCUS_RIGHT);

                }
            }, 200);
        }




    }

    public void deleteImage(User user) {
        View view = menuLinerLayout.findViewWithTag(user);
        menuLinerLayout.removeView(view);
        addList.remove(user.getUsername());
        addUserNames.remove(user.getNick());
        tv_checked.setText(getString(R.string.ok_xiaokuohao) + addList.size() + getString(R.string.xiaokuohao));
        if (addList.size() < 1) {
            iv_search.setImageResource(R.drawable.icon_search_gray);
        }
    }

    /**
     * 确认选择的members
     */
    public void save() {
        String groupName = etGroupName.getText().toString();
        if (TextUtils.isEmpty(groupName.trim()) && isCreatingNewGroup) {
//            Toast.makeText(getApplicationContext(), R.string.Group_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
//            return;
            if (addUserNames.size() != 0 && addUserNames != null) {
                for (int i = 0; i < addUserNames.size() - 1; i++) {
                    groupName += addUserNames.get(i) + "、";
                }
                groupName = groupName + addUserNames.get(addUserNames.size() - 1);
            } else {
                Toast.makeText(GroupAddMembersActivity.this, R.string.check_friend, Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), R.string.Group_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (addList.size() == 0) {
            Toast.makeText(GroupAddMembersActivity.this, R.string.check_friend, Toast.LENGTH_LONG).show();
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
            creatGroupNew(addList, groupName);
        } else {
            //已有群加人
            existsGroupAddMembers(addList);
        }
    }

    /**
     * 创建新群组
     */
    private void creatGroupNew(List<String> members, String groupName) {
        groupName = HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_NICK) + "、" + groupName;
        final ProgressDialog progressDialog = new ProgressDialog(GroupAddMembersActivity.this);
        progressDialog.setMessage(getString(R.string.Is_to_create_a_group_chat));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        HTClient.getInstance().groupManager().createGroup(members, groupName, "", "", new GroupManager.CallBack() {
            @Override
            public void onSuccess(String data) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.success_to_create_groups, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(GroupAddMembersActivity.this, ChatActivity.class).putExtra("userId", data).putExtra("chatType", MessageUtils.CHAT_GROUP));
                finish();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.Failed_to_create_groups, Toast.LENGTH_SHORT).show();
            }
        });


//        HTHttpUtils htHttpUtils = new HTHttpUtils(this);
//        htHttpUtils.creatGroup(members, groupName, HTApp.getInstance().getUsername(), new HTHttpUtils.HttpCallBack() {
//
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                progressDialog.dismiss();
//
//                     if(jsonObject!=null&&jsonObject.containsKey("command")&&jsonObject.getJSONObject("command").containsKey("fields")
//                             &&jsonObject.getJSONObject("command").getJSONArray("fields").size()>0){
//                            JSONObject group=jsonObject.getJSONObject("command").getJSONArray("fields").getJSONObject(0);
//                            if(group.containsKey("value")){
//                                String gid=group.getString("value");
//                                HTGroup htGroup=new HTGroup();
//                                htGroup.setGroupDesc("群描述");
//                                htGroup.setTime(System.currentTimeMillis());
//                                htGroup.setGroupId(gid);
//                                htGroup.setGroupName(groupName);
//                                htGroup.setOwner(HTApp.getInstance().getUsername());
//                                GroupManager.getInstance().saveGroup(htGroup);
//                                Toast.makeText(getApplicationContext(),"建群成功!",Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(GroupAddMembersActivity.this,ChatActivity.class).putExtra("userId",gid).putExtra("chatType", MessageUtils.CHAT_GROUP).putExtra("isNewGroup",true));
//                                finish();
//
//                            }
//                     }else {
//                         Toast.makeText(getApplicationContext(),"建群失败..",Toast.LENGTH_SHORT).show();
//                     }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                progressDialog.dismiss();
//                Toast.makeText(getApplicationContext(),"建群失败..",Toast.LENGTH_SHORT).show();
//
//            }
//        });


//
//        JSONObject myJson = new JSONObject();
//        myJson.put(HTConstant.JSON_KEY_HXID, HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_HXID));
//        myJson.put(HTConstant.JSON_KEY_NICK, HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_NICK));
//        myJson.put(HTConstant.JSON_KEY_AVATAR, HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_AVATAR));
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(myJson);
//
//        for (int i = 0; i < members.size(); i++) {
//            User user = ContactsManager.getInstance().getContactList()
//                    .get(members.get(i));
//            if (user != null) {
//                JSONObject json_member = new JSONObject();
//                json_member.put(HTConstant.JSON_KEY_HXID, user.getUsername());
//                json_member.put(HTConstant.JSON_KEY_NICK, user.getNick());
//                json_member.put(HTConstant.JSON_KEY_AVATAR, user.getAvatar());
//                jsonArray.add(json_member);
//            }
//            if (i > 8) {
//                //最多保存9个用户的头像昵称
//                break;
//            }
//        }
//        JSONObject finalJson = new JSONObject();
//        //jsonArray保存用户资料，最多9个人的
//        finalJson.put("jsonArray", jsonArray);
//        //群聊的真正群名称-默认以未命名为标识
//        finalJson.put("groupname", "未命名");
//        //调用SDK方法
//        creatEMGroup(finalJson.toJSONString(), members);
    }

    private void creatEMGroup(final String jsonInfo, final List<String> members) {
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("正在创建群组");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();

        String membersString = "";
        for (int i = 0; i < members.size(); i++) {
            if (i == 0) {
                membersString = members.get(i);
            } else {
                membersString = membersString + "#" + members.get(i);
            }
        }
        creatGroupInServer(jsonInfo, "temp", membersString, "false");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
//                    option.maxUsers = 200;
//                    option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
//                    EMClient.getInstance().groupManager().createGroup(jsonInfo, "temp", members.toArray(new String[0]), "nothing", option);
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            progressDialog.dismiss();
//                            setResult(Activity.RESULT_OK);
//                            finish();
//                        }
//                    });
//                } catch (final HyphenateException e) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            progressDialog.dismiss();
//                            Toast.makeText(GroupAddMembersActivity.this, "创建群聊失败:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//
//            }
//        }).start();
    }

    private void creatGroupInServer(String groupName, String desc, String members, String isPublic) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.Is_to_create_a_group_chat));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        List<Param> params = new ArrayList<>();
        params.add(new Param("owner", HTApp.getInstance().getUsername()));
        params.add(new Param("members", members));
        params.add(new Param("groupName", groupName));
        params.add(new Param("desc", desc));
        params.add(new Param("public", isPublic));

        new OkHttpUtils(this).post(params, HTConstant.URL_GROUP_CREATE, new OkHttpUtils.HttpCallBack() {
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
                Toast.makeText(getApplicationContext(), R.string.Failed_to_create_groups, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(String errorMsg) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.Failed_to_create_groups, Toast.LENGTH_SHORT).show();
            }
        });


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
//            String py1 = o1.getInitialLetter();
//            String py2 = o2.getInitialLetter();
//            // 判断是否为空""
//            if (isEmpty(py1) && isEmpty(py2))
//                return 0;
//            if (isEmpty(py1))
//                return -1;
//            if (isEmpty(py2))
//                return 1;
//            String str1 = "";
//            String str2 = "";
//            try {
//                str1 = ((o1.getInitialLetter()).toUpperCase()).substring(0, 1);
//                str2 = ((o2.getInitialLetter()).toUpperCase()).substring(0, 1);
//            } catch (Exception e) {
//                System.out.println("某个str为\" \" 空");
//            }
//            return str1.compareTo(str2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }


    private void existsGroupAddMembers(List<String> addMembers) {
        final ProgressDialog progressDialog = new ProgressDialog(GroupAddMembersActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.joining_group));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Map<String, String> map = new HashMap<>();
        for (String userId : addMembers) {
            User user = ContactsManager.getInstance().getContactList().get(userId);
            if (user != null) {
                map.put(userId, user.getNick());
            } else {
                map.put(userId, userId);
            }
        }
        HTClient.getInstance().groupManager().addMembers(map, groupId, new GroupManager.CallBack() {
            @Override
            public void onSuccess(String data) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.joining_group_success, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.joining_group_failed, Toast.LENGTH_SHORT).show();
//                setResult(RESULT_OK);
                finish();
            }
        });
    }


}
