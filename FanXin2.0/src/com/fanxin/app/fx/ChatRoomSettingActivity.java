package com.fanxin.app.fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.activity.FXAlertDialog;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.domain.User;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LoadUserAvatar;
import com.fanxin.app.fx.others.LocalUserInfo;
import com.fanxin.app.fx.others.TopUser;
import com.fanxin.app.fx.others.TopUserDao;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;
import com.fanxin.app.fx.others.LoadUserAvatar.ImageDownloadedCallBack;
import com.fanxin.app.widget.ExpandGridView;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.NetUtils;

@SuppressLint({ "SimpleDateFormat", "SdCardPath", "ClickableViewAccessibility",
        "InflateParams" })
public class ChatRoomSettingActivity extends BaseActivity implements
        OnClickListener {
    private TextView tv_groupname;
    // 成员总数

    private TextView tv_m_total;
    // 成员总数
    int m_total = 0;
    // 成员列表
    private ExpandGridView gridview;
    // 修改群名称、置顶、、、、
    private RelativeLayout re_change_groupname;
    private RelativeLayout rl_switch_chattotop;
    private RelativeLayout rl_switch_block_groupmsg;
    private RelativeLayout re_clear;

    // 状态变化
    private ImageView iv_switch_chattotop;
    private ImageView iv_switch_unchattotop;
    private ImageView iv_switch_block_groupmsg;
    private ImageView iv_switch_unblock_groupmsg;
    // 删除并退出

    private Button exitBtn;

    private String hxid;
    // 群名称
    private String group_name;
    // 是否是管理员
    boolean is_admin = false;
    List<User> members = new ArrayList<User>();
    String longClickUsername = null;

    private String groupId;

    private EMGroup group;
    private GridAdapter adapter;

    public static ChatRoomSettingActivity instance;
    private ProgressDialog progressDialog;
    private JSONObject jsonObject;
    private JSONArray jsonarray;

    // 置顶列表
    private Map<String, TopUser> topMap = new HashMap<String, TopUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_groupchatsetting_activity);
        instance = this;
        hxid = LocalUserInfo.getInstance(ChatRoomSettingActivity.this)
                .getUserInfo("hxid");
        topMap = MYApplication.getInstance().getTopUserList();
        initView();
        initData();
        updateGroup();
    }

    private void initView() {
        progressDialog = new ProgressDialog(ChatRoomSettingActivity.this);
        tv_groupname = (TextView) findViewById(R.id.tv_groupname);

        tv_m_total = (TextView) findViewById(R.id.tv_m_total);

        gridview = (ExpandGridView) findViewById(R.id.gridview);

        re_change_groupname = (RelativeLayout) findViewById(R.id.re_change_groupname);
        rl_switch_chattotop = (RelativeLayout) findViewById(R.id.rl_switch_chattotop);
        rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);
        re_clear = (RelativeLayout) findViewById(R.id.re_clear);

        iv_switch_chattotop = (ImageView) findViewById(R.id.iv_switch_chattotop);
        iv_switch_unchattotop = (ImageView) findViewById(R.id.iv_switch_unchattotop);
        iv_switch_block_groupmsg = (ImageView) findViewById(R.id.iv_switch_block_groupmsg);
        iv_switch_unblock_groupmsg = (ImageView) findViewById(R.id.iv_switch_unblock_groupmsg);
        exitBtn = (Button) findViewById(R.id.btn_exit_grp);

    }

    private void initData() {

        // 获取传过来的groupid
        groupId = getIntent().getStringExtra("groupId");
        // 获取本地该群数据
        group = EMGroupManager.getInstance().getGroup(groupId);
        if (group == null) {
            try {
                // 去网络中查找该群
                group = EMGroupManager.getInstance()
                        .getGroupFromServer(groupId);
                if (group == null) {
                    Toast.makeText(ChatRoomSettingActivity.this, "该群已经被解散...",
                            Toast.LENGTH_SHORT).show();
                    setResult(100);
                    finish();
                    return;

                }
            } catch (EaseMobException e) {

                e.printStackTrace();
                return;
            }

        }

        // 获取封装的群名（里面封装了显示的群名和群组成员的信息）
        String group_name_temp = group.getGroupName();
        // 转化成json，然后解析
        jsonObject = JSONObject.parseObject(group_name_temp);
        // 获取显示的群名
        group_name = jsonObject.getString("groupname");
        // 获取群成员信息
        jsonarray = jsonObject.getJSONArray("jsonArray");

        tv_groupname.setText(group_name);
        m_total = jsonarray.size();
        tv_m_total.setText("(" + String.valueOf(m_total) + ")");
        // 解析群组成员信息
        for (int i = 0; i < m_total; i++) {
            JSONObject json = jsonarray.getJSONObject(i);
            User user = new User();
            user.setUsername(json.getString("hxid"));
            user.setAvatar(json.getString("avatar"));
            user.setNick(json.getString("nick"));
            members.add(user);
        }
        // 显示群组成员头像和昵称
        showMembers(members);
        // 判断是否是群主，是群主有删成员的权限，并显示减号按钮
        if (hxid.equals(group.getOwner())) {
            is_admin = true;
        }

        re_change_groupname.setOnClickListener(this);
        rl_switch_chattotop.setOnClickListener(this);
        rl_switch_block_groupmsg.setOnClickListener(this);

        re_clear.setOnClickListener(this);

        exitBtn.setOnClickListener(this);

    }

    // 显示群成员头像昵称的gridview
    @SuppressLint("ClickableViewAccessibility")
    private void showMembers(List<User> members) {
        adapter = new GridAdapter(this, members);
        gridview.setAdapter(adapter);

        // 设置OnTouchListener,为了让群主方便地推出删除模》
        gridview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (adapter.isInDeleteMode) {
                        adapter.isInDeleteMode = false;
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                    break;
                default:
                    break;
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rl_switch_block_groupmsg: // 屏蔽群组
            if (iv_switch_block_groupmsg.getVisibility() == View.VISIBLE) {
                System.out.println("change to unblock group msg");
                try {
                    EMGroupManager.getInstance().unblockGroupMessage(groupId);
                    iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
                    iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    // todo: 显示错误给用户
                }
            } else {
                System.out.println("change to block group msg");
                try {
                    EMGroupManager.getInstance().blockGroupMessage(groupId);
                    iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
                    iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    // todo: 显示错误给用户
                }
            }
            break;

        case R.id.re_clear: // 清空聊天记录
            progressDialog.setMessage("正在清空群消息...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            // 按照你们要求必须有个提示，防止记录太少，删得太快，不提示
            clearGroupHistory();
            break;
        case R.id.re_change_groupname:
            showNameAlert();
            break;
        case R.id.rl_switch_chattotop:
            // 当前状态是已经置顶,点击后取消置顶
            if (iv_switch_chattotop.getVisibility() == View.VISIBLE) {

                iv_switch_chattotop.setVisibility(View.INVISIBLE);
                iv_switch_unchattotop.setVisibility(View.VISIBLE);

                if (topMap.containsKey(group.getGroupId())) {

                    topMap.remove(group.getGroupId());
                    TopUserDao topUserDao = new TopUserDao(
                            ChatRoomSettingActivity.this);

                    topUserDao.deleteTopUser(group.getGroupId());
                }

            } else {

                // 当前状态是未置顶点击后置顶

                iv_switch_chattotop.setVisibility(View.VISIBLE);
                iv_switch_unchattotop.setVisibility(View.INVISIBLE);

                if (!topMap.containsKey(group.getGroupId())) {
                    TopUser topUser = new TopUser();
                    topUser.setTime(System.currentTimeMillis());
                    // 1---表示是群组
                    topUser.setType(1);
                    topUser.setUserName(group.getGroupId());
                    Map<String, TopUser> map = new HashMap<String, TopUser>();
                    map.put(group.getGroupId(), topUser);
                    topMap.putAll(map);
                    TopUserDao topUserDao = new TopUserDao(
                            ChatRoomSettingActivity.this);
                    topUserDao.saveTopUser(topUser);

                }

            }

            break;

        case R.id.btn_exit_grp:

            deleteMembersFromGroup(hxid);
            break;

        default:
            break;
        }

    }

    /**
     * 清空群聊天记录
     */
    public void clearGroupHistory() {

        EMChatManager.getInstance().clearConversation(group.getGroupId());
        progressDialog.dismiss();

    }

    /**
     * 群组成员gridadapter
     * 
     * @author admin_new
     * 
     */
    private class GridAdapter extends BaseAdapter {

        public boolean isInDeleteMode;
        private List<User> objects;
        Context context;
        private LoadUserAvatar avatarLoader;

        public GridAdapter(Context context, List<User> objects) {

            this.objects = objects;
            this.context = context;
            isInDeleteMode = false;
            avatarLoader = new LoadUserAvatar(context, "/sdcard/fanxin/");
        }

        @Override
        public View getView(final int position, View convertView,
                final ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.social_chatsetting_gridview_item, null);
            }
            ImageView iv_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            TextView tv_username = (TextView) convertView
                    .findViewById(R.id.tv_username);
            ImageView badge_delete = (ImageView) convertView
                    .findViewById(R.id.badge_delete);

            // 最后一个item，减人按钮

            if (position == getCount() - 1 && is_admin) {
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.drawable.icon_btn_deleteperson);

                if (isInDeleteMode) {
                    // 正处于删除模式下，隐藏删除按钮
                    convertView.setVisibility(View.GONE);
                } else {

                    convertView.setVisibility(View.VISIBLE);
                }

                iv_avatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        isInDeleteMode = true;
                        notifyDataSetChanged();
                    }

                });

            } else if ((is_admin && position == getCount() - 2)
                    || (!is_admin && position == getCount() - 1)) { // 添加群组成员按钮
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.drawable.jy_drltsz_btn_addperson);
                // 正处于删除模式下,隐藏添加按钮
                if (isInDeleteMode) {
                    convertView.setVisibility(View.GONE);
                } else {
                    convertView.setVisibility(View.VISIBLE);
                }
                iv_avatar.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                            // 进入选人页面
                            startActivity((new Intent(
                                    ChatRoomSettingActivity.this,
                                    CreatChatRoomActivity.class).putExtra(
                                    "groupId", groupId)));
                         
                    }
                });
            }

            else { // 普通item，显示群组成员

                User user = objects.get(position);
                String usernick = user.getNick();
                final String userhid = user.getUsername();
                final String useravatar = user.getAvatar();
                tv_username.setText(usernick);
                iv_avatar.setImageResource(R.drawable.default_useravatar);
                iv_avatar.setTag(useravatar);
                if (useravatar != null && !useravatar.equals("")) {
                    Bitmap bitmap = avatarLoader.loadImage(iv_avatar,
                            useravatar, new ImageDownloadedCallBack() {

                                @Override
                                public void onImageDownloaded(
                                        ImageView imageView, Bitmap bitmap) {
                                    if (imageView.getTag() == useravatar) {
                                        imageView.setImageBitmap(bitmap);

                                    }
                                }

                            });

                    if (bitmap != null) {

                        iv_avatar.setImageBitmap(bitmap);

                    }

                }

                // demo群组成员的头像都用默认头像，需由开发者自己去设置头像
                if (isInDeleteMode) {
                    // 如果是删除模式下，显示减人图标
                    convertView.findViewById(R.id.badge_delete).setVisibility(
                            View.VISIBLE);
                } else {
                    convertView.findViewById(R.id.badge_delete).setVisibility(
                            View.INVISIBLE);
                }
                iv_avatar.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isInDeleteMode) {
                            // 如果是删除自己，return
                            if (EMChatManager.getInstance().getCurrentUser()
                                    .equals(userhid)) {
                                startActivity(new Intent(
                                        ChatRoomSettingActivity.this,
                                        FXAlertDialog.class).putExtra("msg",
                                        "不能删除自己"));
                                return;
                            }
                            if (!NetUtils.hasNetwork(getApplicationContext())) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        getString(R.string.network_unavailable),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            deleteMembersFromGroup(userhid);
                        } else {
                            // 正常情况下点击user，可以进入用户详情或者聊天页面等等
                            // startActivity(new
                            // Intent(GroupDetailsActivity.this,
                            // ChatActivity.class).putExtra("userId",
                            // user.getUsername()));

                        }
                    }

                });

            }
            return convertView;
        }

        @Override
        public int getCount() {
            if (is_admin) {
                return objects.size() + 2;
            } else {

                return objects.size() + 1;

            }

        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
    }

    protected void updateGroup() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMGroup returnGroup = EMGroupManager.getInstance()
                            .getGroupFromServer(groupId);
                    // 更新本地数据
                    EMGroupManager.getInstance().createOrUpdateLocalGroup(
                            returnGroup);

                    runOnUiThread(new Runnable() {
                        public void run() {

                            if (group != null) {
                                System.out.println("group msg is blocked:"
                                        + group.getMsgBlocked());
                                // 设置初始屏蔽初始状态
                                if (group.getMsgBlocked()) {
                                    iv_switch_block_groupmsg
                                            .setVisibility(View.VISIBLE);
                                    iv_switch_unblock_groupmsg
                                            .setVisibility(View.INVISIBLE);
                                } else {
                                    iv_switch_block_groupmsg
                                            .setVisibility(View.INVISIBLE);
                                    iv_switch_unblock_groupmsg
                                            .setVisibility(View.VISIBLE);
                                }
                                // 设置置顶的初始状态

                                if (topMap.containsKey(group.getGroupId())) {

                                    // 当前状态是已经置顶

                                    iv_switch_chattotop
                                            .setVisibility(View.VISIBLE);
                                    iv_switch_unchattotop
                                            .setVisibility(View.INVISIBLE);

                                } else {
                                    // 当前状态是未置顶
                                    iv_switch_chattotop
                                            .setVisibility(View.INVISIBLE);
                                    iv_switch_unchattotop
                                            .setVisibility(View.VISIBLE);

                                }
                            }

                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });
                }
            }
        }).start();
    }

    private void showNameAlert() {

        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.social_alertdialog);
        // 设置能弹出输入法
        dlg.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // 为确认按钮添加事件,执行退出应用操作
        Button ok = (Button) window.findViewById(R.id.btn_ok);
        final EditText ed_name = (EditText) window.findViewById(R.id.ed_name);

        ok.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            public void onClick(View v) {
                final String newName = ed_name.getText().toString().trim();

                if (TextUtils.isEmpty(newName)) {
                    return;
                }

                try {
                    JSONObject newJSON = new JSONObject();
                    newJSON.put("groupname", newName);
                    newJSON.put("jsonArray", jsonarray);
                    String updateStr = newJSON.toJSONString();
                    // 如果是群主直接调用本地SDK的API
                    if (is_admin) {
                        EMGroupManager.getInstance().changeGroupName(groupId,
                                updateStr);

                    }
                    // 非群员成员需要调用服务器端代码...
                    else {
                        updateGroupName(groupId, updateStr);

                    }
                    progressDialog.dismiss();
                    tv_groupname.setText(newName);
                    group_name = newName;
                    Toast.makeText(ChatRoomSettingActivity.this, "修改成功",
                            Toast.LENGTH_LONG).show();
                } catch (EaseMobException e) {
                    Toast.makeText(ChatRoomSettingActivity.this, "修改失败",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                dlg.cancel();
            }
        });
        // 关闭alert对话框架
        Button cancel = (Button) window.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dlg.cancel();
            }
        });

    }

    /**
     * 删除群成员
     * 
     * @param username
     */
    protected void deleteMembersFromGroup(final String username) {
        final ProgressDialog deleteDialog = new ProgressDialog(
                ChatRoomSettingActivity.this);
        // 当删除的是自己的时候,意味着就是退群。群主退群是要解散群的，所以要有判断
        if (hxid.equals(username)) {
            deleteDialog.setMessage("正在退出...");
            deleteDialog.setCanceledOnTouchOutside(false);
            deleteDialog.show();
            // 非群主退出
            if (!is_admin) {

                try {

                    JSONObject newJSON = new JSONObject();
                    newJSON.put("groupname", group_name);
                    for (int n = 0; n < jsonarray.size(); n++) {
                        JSONObject jsontemp = (JSONObject) jsonarray.get(n);
                        if (jsontemp.getString("hxid").equals(username)) {
                            jsonarray.remove(jsontemp);
                        }
                    }

                    newJSON.put("jsonArray", jsonarray);
                    String updateStr = newJSON.toJSONString();
                    // 群成员退出以后要更新群信息，也就封装的群名..
                    updateGroupName(groupId, updateStr);
                    EMGroupManager.getInstance().exitFromGroup(groupId);
                    deleteDialog.dismiss();
                    Toast.makeText(ChatRoomSettingActivity.this, "退出成功",
                            Toast.LENGTH_LONG).show();
                    setResult(100);
                    finish();
                } catch (EaseMobException e) {
                    deleteDialog.dismiss();
                    Toast.makeText(ChatRoomSettingActivity.this, "退出失败",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            // 群主退群
            else {

                try {
                    EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
                    deleteDialog.dismiss();
                    Toast.makeText(ChatRoomSettingActivity.this, "退出成功",
                            Toast.LENGTH_LONG).show();
                    setResult(100);
                    finish();
                } catch (EaseMobException e) {
                    deleteDialog.dismiss();
                    Toast.makeText(ChatRoomSettingActivity.this, "退出失败",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }// 异步执行
            }

        }
        // 群主删群成员操作
        else {
            deleteDialog.setMessage("正在移除...");
            deleteDialog.setCanceledOnTouchOutside(false);
            deleteDialog.show();
            try {
                EMGroupManager.getInstance().removeUserFromGroup(groupId,
                        username);
                for (int i = 0; i < members.size(); i++) {
                    User user = members.get(i);
                    if (user.getUsername().equals(username)) {
                        // 移除被删成员信息
                        members.remove(user);
                        adapter.notifyDataSetChanged();
                        m_total = members.size();
                        tv_m_total.setText("(" + String.valueOf(m_total) + ")");
                        JSONObject newJSON = new JSONObject();
                        newJSON.put("groupname", group_name);
                        // 在封装数据里面取出删除成员，并且更新
                        for (int n = 0; n < jsonarray.size(); n++) {

                            JSONObject jsontemp = (JSONObject) jsonarray.get(n);
                            if (jsontemp.getString("hxid").equals(username)) {
                                jsonarray.remove(jsontemp);
                            }
                        }

                        newJSON.put("jsonArray", jsonarray);
                        String updateStr = newJSON.toJSONString();
                        Log.e("updateStr------>>>>>0", updateStr);

                        EMGroupManager.getInstance().changeGroupName(groupId,
                                updateStr);

                    }

                }

                deleteDialog.dismiss();
                Toast.makeText(ChatRoomSettingActivity.this, "移除成功",
                        Toast.LENGTH_LONG).show();
            } catch (EaseMobException e) {
                deleteDialog.dismiss();
                Toast.makeText(ChatRoomSettingActivity.this, "移除失败",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }// 异步执行

        }

    }

    private void updateGroupName(String groupId, String updateStr) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("groupId", groupId);
        map.put("groupName", updateStr);
        LoadDataFromServer task = new LoadDataFromServer(
                ChatRoomSettingActivity.this, Constant.URL_UPDATE_Groupnanme,
                map);

        task.getData(new DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                if (data != null) {
                    int code = data.getInteger("code");

                    if (code != 1) {
                        // 通知管理员。。。

                    }

                }
            }
        });

    }

    public void back(View view) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

}
