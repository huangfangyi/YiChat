package com.fanxin.app.main.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import com.fanxin.app.Constant;
import com.fanxin.app.DemoApplication;
import com.fanxin.app.DemoHelper;
import com.fanxin.app.R;
import com.fanxin.app.main.adapter.PickContactAdapter;
import com.fanxin.app.main.utils.OkHttpManager;
import com.fanxin.app.main.utils.Param;
import com.fanxin.app.ui.BaseActivity;
import com.fanxin.app.ui.ChatActivity;
import com.fanxin.easeui.domain.EaseUser;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;


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
    int total = 0;
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
            total = 1;
            addList.add(userId);
        } else {
            isCreatingNewGroup = true;
        }

        // 获取好友列表
        friendList = new ArrayList<EaseUser>(DemoHelper.getInstance().getContactList().values());
        // 对list进行排序
        Collections.sort(friendList, new PinyinComparator() {});
        refreshList(friendList);


    }

    private void initView() {
        tv_checked = (TextView) this.findViewById(R.id.tv_save);
        listView = (ListView) findViewById(R.id.list);
        iv_search = (ImageView) this.findViewById(R.id.iv_search);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View headerView = layoutInflater.inflate(R.layout.fx_item_group_create_header,
                null);
        headerView.findViewById(R.id.tv_header).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupAddMembersActivity.this,
                        GroupListActivity.class));
                finish();
            }

        });
        listView.addHeaderView(headerView);
        menuLinerLayout = (LinearLayout) this
                .findViewById(R.id.linearLayoutMenu);
        //设置监听
        setTextChangedListener((EditText) this.findViewById(R.id.et_search));
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position > 0) {
                    EaseUser user = contactAdapter.getItem(position - 1);
                    if (exitingMembers.contains(user.getUsername())) {
                        return;
                    }
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                    checkBox.toggle();
                    if (checkBox.isChecked()) {
                        showCheckImage(contactAdapter.getBitmap(position - 1), user);
                    } else {
                        deleteImage(user);
                    }
                }
            }
        });
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

    private void showCheckImage(Bitmap bitmap, EaseUser user) {

        if (exitingMembers.contains(user.getUsername()) && groupId != null) {
            return;
        }
        if (addList.contains(user.getUsername())) {
            return;
        }
        total++;
        // 包含TextView的LinearLayout
        // 参数设置
        LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                108, 108, 1);
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
        tv_checked.setText("确定(" + total + ")");
        if (total > 0) {
            if (iv_search.getVisibility() == View.VISIBLE) {
                iv_search.setVisibility(View.GONE);
            }
        }
        addList.add(user.getUsername());
    }

    private void deleteImage(EaseUser user) {
        View view = menuLinerLayout.findViewWithTag(user);
        menuLinerLayout.removeView(view);
        total--;
        tv_checked.setText("确定(" + total + ")");
        addList.remove(user.getUsername());
        if (total < 1) {
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
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在创建群组");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                    option.maxUsers = 200;
                    option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    EMClient.getInstance().groupManager().createGroup(jsonInfo, "temp", members.toArray(new String[0]), "nothing", option);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(GroupAddMembersActivity.this, "创建群聊失败:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();
    }


    private void existsGroupAddMembers(final List<String> members) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在处理...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //如果是管理员-加人
        if (DemoHelper.getInstance().getCurrentUsernName().equals(group.getOwner())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().addUsersToGroup(groupId, members.toArray(new String[0]));//需异步处理
                        updateGroupInfo(members,progressDialog);
                    } catch (HyphenateException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "群主加人失败...", Toast.LENGTH_LONG).show();

                            }
                        });
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().inviteUser(groupId,  members.toArray(new String[0]), null);//需异步处理
                        updateGroupInfo(members,progressDialog);
                    } catch (HyphenateException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "群成员加人失败...", Toast.LENGTH_LONG).show();
                            }
                        });
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void updateGroupInfo(List<String> members, final ProgressDialog progressDialog) throws HyphenateException {
       try {
           JSONObject oldjson = JSONObject.parseObject(groupname);
           JSONArray oldjsonArray = oldjson.getJSONArray("jsonArray");
           final String groupName = oldjson.getString("groupname");
           for (int i = 0; i < members.size(); i++) {
               EaseUser user = DemoHelper.getInstance().getContactList()
                       .get(members.get(i));
               if (user != null) {
                   JSONObject json_member = new JSONObject();
                   json_member.put("hxid", user.getUsername());
                   json_member.put("nick", user.getNick());
                   json_member.put("avatar", user.getAvatar());
                   oldjsonArray.add(json_member);
               }
           }
           JSONObject finalJson = new JSONObject();
           finalJson.put("jsonArray", oldjsonArray);
           finalJson.put("groupname", groupName);
           final String groupJSON = finalJson.toJSONString();
           if (DemoHelper.getInstance().getCurrentUsernName().equals(group.getOwner())) {
               EMClient.getInstance().groupManager().changeGroupName(groupId,groupJSON);
               startActivity(new Intent(getApplicationContext(),
                       ChatActivity.class).putExtra("userId", groupId)
                       .putExtra("chatType", EMMessage.ChatType.GroupChat)
                       .putExtra("groupName", groupName));
               finish();
           } else {
                //非群主只能通过后端rest api进行修改群资料
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       updateGroupName(groupId, groupJSON,groupName,progressDialog);
                   }
               });

           }
       }catch (JSONException e){
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   progressDialog.dismiss();
                   Toast.makeText(getApplicationContext(),"群资料处理失败..",Toast.LENGTH_LONG).show();
               }
           });
       }
    }


    private void updateGroupName(final String groupId, String groupJSON, final String groupName , final ProgressDialog progressDialog) {
        List<Param> params=new ArrayList<>();
        params.add(new Param("groupId", groupId));
        params.add(new Param("groupName", groupJSON));
        OkHttpManager.getInstance().post(params, FXConstant.URL_UPDATE_Groupnanme, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                int code = jsonObject.getInteger("code");
                if (code == 1) {
                    startActivity(new Intent(getApplicationContext(),
                            ChatActivity.class).putExtra("userId", groupId)
                            .putExtra("chatType", EMMessage.ChatType.GroupChat)
                            .putExtra("groupName", groupName));
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"群成员更新群资料失败,code:"+code,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(String errorMsg) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"群成员更新群资料失败...",Toast.LENGTH_LONG).show();
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
