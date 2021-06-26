package com.htmessage.yichat.acitivity.chat.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.update.Constant;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.data.SettingsManager;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.uitls.MsgUtils;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.chat.GroupSetingsGridApdater;
import com.htmessage.yichat.acitivity.chat.group.allmember.AllGroupMembersActivity;
import com.htmessage.yichat.acitivity.chat.group.managerlist.ManagerListActivity;
import com.htmessage.yichat.acitivity.chat.group.qrcode.GroupQrCodeActivity;
import com.htmessage.yichat.acitivity.chat.search.SearchChatHistoryActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.SwitchButton;
import com.htmessage.yichat.widget.ExpandGridView;

import java.util.ArrayList;
import java.util.List;


public class ChatSettingGroupActivity extends BaseActivity implements OnClickListener {



    private TextView tv_groupname;
    private TextView tv_groupDesc, tv_see_all;

     private RelativeLayout rl_switch_block_groupmsg;
    private RelativeLayout re_clear, re_search, re_group_qrcode, re_manager,re_blacklist;
    //全员禁言
    private RelativeLayout rl_switch_no_talk;
    private SwitchButton switch_no_talk,switch_block_groupmsg;

      // 删除并退出

    private Button exitBtn;

    public String groupId;

    private boolean isOwner = false;

    private GroupSetingsGridApdater adapter;
    public static ChatSettingGroupActivity instance;
    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_GROUP_NAME = 100;
    private static final int REQUEST_GROUP_DESE = 200;

    private String userId;
    private JSONArray membersJSONArray = new JSONArray();
    private ExpandGridView userGridview;
    private HTGroup htGroup;
     private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    //获取前14名群成员，完成界面加载
                    JSONArray data= (JSONArray) msg.obj;
                    membersJSONArray.clear();
                    membersJSONArray.addAll(data);
                    adapter.notifyDataSetChanged();
//                    if(data.size()>=14){
//                        tv_see_all.setVisibility(View.VISIBLE);
//                    }else {
//                        tv_see_all.setVisibility(View.GONE);
//                    }
                    break;
                case 1001:
                    //全群禁言设置
                    CommonUtils.cencelDialog();
                    boolean isSilent= (boolean) msg.obj;
                    if(isSilent){
                        switch_no_talk.openSwitch();
                        GroupInfoManager.getInstance().setGroupSilent(groupId,true,false);
                        MsgUtils.getInstance().sendNoTalkOrCancleCmdMessage(30000, groupId, htGroup.getGroupName());
                    }else {
                        switch_no_talk.closeSwitch();
                        GroupInfoManager.getInstance().setGroupSilent(groupId,false,false);
                        MsgUtils.getInstance().sendNoTalkOrCancleCmdMessage(30001, groupId, htGroup.getGroupName());

                    }

                    break;
                case 1002:
                    //全群禁言设置
                    CommonUtils.cencelDialog();
                    int resId=msg.arg1;
                    showToast(resId);

                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_setting_group);

        getData();
        initView();
        initData();
    }

    private void getData() {
        instance = this;
        userId = HTApp.getInstance().getUsername();
        // 获取传过来的groupid
        groupId = getIntent().getStringExtra("groupId");
        if (TextUtils.isEmpty(groupId)) {
            finish();
            return;
        }
        htGroup = HTClient.getInstance().groupManager().getGroup(groupId);
        if (htGroup == null) {
            finish();
            return;
        }
        setTitle("群组详情("+GroupInfoManager.getInstance().getGroupAllMembersFromLocal(groupId).size()+"人)");
        //  CommonUtils.requestOpenGroupList(getBaseContext());
    }
    private void showToast(int resId){
        Toast.makeText(ChatSettingGroupActivity.this,resId,Toast.LENGTH_SHORT).show();
    }

    private void initView() {
         tv_groupname = (TextView) findViewById(R.id.tv_groupname);
        tv_groupDesc = (TextView) findViewById(R.id.tv_groupDesc);
//         re_red_settings = (RelativeLayout) findViewById(R.id.re_red_settings);
//        re_boom_settings = (RelativeLayout) findViewById(R.id.re_boom_settings);
        rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);
        re_clear = (RelativeLayout) findViewById(R.id.re_clear);
        re_search = (RelativeLayout) findViewById(R.id.re_search);
        switch_block_groupmsg = (SwitchButton) findViewById(R.id.switch_block_groupmsg);
         rl_switch_no_talk = (RelativeLayout) findViewById(R.id.rl_switch_no_talk);
        switch_no_talk = (SwitchButton) findViewById(R.id.switch_no_talk);
        re_group_qrcode = (RelativeLayout) findViewById(R.id.re_group_qrcode);
        re_manager = (RelativeLayout) findViewById(R.id.re_manager);
        re_blacklist = (RelativeLayout) findViewById(R.id.re_blacklist);

        userGridview = (ExpandGridView) findViewById(R.id.gridview);
        exitBtn = (Button) findViewById(R.id.btn_exit_grp);
        tv_see_all = (TextView) findViewById(R.id.tv_see_all);
        exitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                exitGroup();
            }
        });
    }

    private void initData() {
        if (GroupInfoManager.getInstance().isManager(groupId)) {
            isOwner = true;
        } else {
            isOwner = false;
        }

        if (UserManager.get().getMyUserId().equals(htGroup.getOwner())) {
            exitBtn.setText(R.string.delete_group);
        } else {
            exitBtn.setText(R.string.exit_group);
        }
        if (isOwner) {
            rl_switch_no_talk.setVisibility(View.VISIBLE);
            checkNoTalkOrNo(getBaseContext(), groupId);
            re_blacklist.setVisibility(View.VISIBLE);
            re_manager.setVisibility(View.VISIBLE);
        } else {
            re_manager.setVisibility(View.GONE);
            rl_switch_no_talk.setVisibility(View.GONE);
            re_blacklist.setVisibility(View.GONE);
        }

        //初始化显示免打扰的状态

        if(SettingsManager.getInstance().getNotifyGroupOrUser(groupId)){
            switch_block_groupmsg.closeSwitch();
        }else {
            switch_block_groupmsg.openSwitch();
        }


        tv_groupname.setText(TextUtils.isEmpty(htGroup.getGroupName()) ? htGroup.getGroupId() : htGroup.getGroupName());
        tv_groupDesc.setText(TextUtils.isEmpty(htGroup.getGroupDesc()) ? "" : htGroup.getGroupDesc());

        JSONArray jsonArray=GroupInfoManager.getInstance().getGroupAllMembersFromLocal(groupId);


        if(jsonArray!=null&&jsonArray.size()>0){
            membersJSONArray.addAll(jsonArray.subList(0,Math.min(jsonArray.size(),13)));

        }
        adapter = new GroupSetingsGridApdater(this, membersJSONArray, isOwner, groupId);
        userGridview.setAdapter(adapter);
//        if(membersJSONArray.size()>=14){
//            tv_see_all.setVisibility(View.VISIBLE);
//        }else {
//            tv_see_all.setVisibility(View.GONE);
//        }
        this.findViewById(R.id.re_change_groupname).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ChatSettingGroupActivity.this, UpdateGroupActivity.class).putExtra("groupId", groupId).putExtra("type", UpdateGroupActivity.TYPE_GROUP_NAME), REQUEST_GROUP_NAME);

            }
        });
        this.findViewById(R.id.re_change_groupDesc).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ChatSettingGroupActivity.this, UpdateGroupActivity.class).putExtra("groupId", groupId).putExtra("type", UpdateGroupActivity.TYPE_GROUP_DESC), REQUEST_GROUP_DESE);
            }
        });
        this.findViewById(R.id.re_change_groupImgUrl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatSettingGroupActivity.this, UpdateGroupImgUrlActivity.class).putExtra("groupId", groupId).putExtra("groupName", htGroup.getGroupName()));
            }
        });




        switch_no_talk.setOnClickListener(this);
        switch_block_groupmsg.setOnClickListener(this);
        re_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearGroupHistory();
            }
        });
        re_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatSettingGroupActivity.this, SearchChatHistoryActivity.class).putExtra(HTConstant.JSON_KEY_USERID, groupId));
            }
        });
        this.findViewById(R.id.re_group_notice).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatSettingGroupActivity.this, GroupNoticeActivity.class).putExtra("groupId", groupId).putExtra("groupName", htGroup.getGroupName()));

            }
        });
        rl_switch_block_groupmsg.setOnClickListener(this);
         tv_see_all.setOnClickListener(this);
        re_group_qrcode.setOnClickListener(this);
        re_manager.setOnClickListener(this);

        re_blacklist.setOnClickListener(this);

        //获取14个以内的群成员
        get14LastMembers(1,14);
    }


    /**
     * 清空群聊天记录
     */
    public void clearGroupHistory() {
        CommonUtils.showDialogNumal(this, getString(R.string.clear));
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                HTClient.getInstance().messageManager().deleteUserMessage(groupId,false);
               // HTClient.getInstance().conversationManager().deleteConversationAndMessage(groupId);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_EMPTY).putExtra("id", groupId));
//                List<String> users = new ArrayList<>();
//                users.add(HTApp.getInstance().getUsername());
                CommonUtils.cencelDialog();

            }

        }, 2000);
    }


    /**
     * 删除群成员
     * 当删除的是自己，则就是退群或者解散群
     */
    protected void exitGroup() {
        if (userId.equals(htGroup.getOwner())) {
            //自己是群主，解散群
            deleteGroup();
        } else {
            leaveGroup();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOwner = false;
        instance = null;
        handler=null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    break;
                case REQUEST_GROUP_NAME:
                    if (data != null) {
                        String value = data.getStringExtra("value");
                        if (value != null) {
                            tv_groupname.setText(value);
                            htGroup.setGroupName(value);
                            HTClient.getInstance().groupManager().saveGroup(htGroup);
                        }
                    }
                    break;
                case REQUEST_GROUP_DESE:
                    if (data != null) {
                        String value = data.getStringExtra("value");
                        if (value != null) {
                            tv_groupDesc.setText(value);
                            htGroup.setGroupDesc(value);
                            HTClient.getInstance().groupManager().saveGroup(htGroup);
                        }
                    }
                    break;
            }
        }
    }



    public void get14LastMembers(int pageNo, int pageSize) {
//        JSONObject data=new JSONObject();
//        data.put("groupId",groupId);
//        data.put("pageNo",pageNo);
//        data.put("pageSize",pageSize);
//        ApiUtis.getInstance().postJSON(data, Constant.URL_GROUP_MEMBERS, new ApiUtis.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                String code=jsonObject.getString("code");
//                if("0".equals(code)){
//                    JSONArray jsonArray=jsonObject.getJSONArray("data");
//                    //
//                    GroupInfoManager.getInstance().set14LastGroupMembers(groupId,jsonArray);
//                    Message message=handler.obtainMessage();
//                    message.obj=jsonArray;
//                    message.what=1000;
//                    message.sendToTarget();
//                }
//            }
//
//            @Override
//            public void onFailure(int errorCode) {
//
//            }
//        });


//        List<Param> params = new ArrayList<>();
//        params.add(new Param("gid", groupId));
//        params.add(new Param("uid", HTApp.getInstance().getUserId()));
//        new OkHttpUtils(this).post(params, HTConstant.URL_GROUP_MEMBERS, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONArray jsonArray = jsonObject.getJSONArray("data");
//                        if (jsonArray != null && jsonArray.size() != 0) {
//                            MmvkManger.getIntance().putJSONArray(HTApp.getInstance().getUserId() + groupId, jsonArray);
//                            arrayToList(jsonArray, membersJSONArray);
//                            adapter.notifyDataSetChanged();
//                        }
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//
//            }
//        });
    }

    public void startAddMembers() {
        if (GroupInfoManager.getInstance().isManager(groupId)) {
            startActivityForResult((new Intent(ChatSettingGroupActivity.this, GroupAddMembersActivity.class).putExtra("groupId", groupId)),
                    REQUEST_CODE_ADD_USER);

        } else {
            CommonUtils.showToastShort(ChatSettingGroupActivity.this, "该群群成员无权限邀请人进群");

        }


    }
//
//    private void arrayToList(JSONArray jsonArray, List<User> jsonObjects) {
//        jsonObjects.clear();
//
//        if (jsonArray.size() <= 30) {
//            tv_see_all.setVisibility(View.GONE);
//        } else {
//            tv_see_all.setVisibility(View.VISIBLE);
//        }
//    }


    private void deleteGroup() {
        CommonUtils.showDialogNumal(this, getString(R.string.deleting));
        HTClient.getInstance().groupManager().deleteGroup(groupId, new GroupManager.CallBack() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(IMAction.ACTION_DELETE_GROUP).putExtra("userId", groupId));
                        CommonUtils.showToastShort(getApplicationContext(), R.string.delete_sucess);
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(getApplicationContext(), R.string.delete_failed);
                    }
                });
            }

            @Override
            public void onHTMessageSend(HTMessage htMessage) {
           //     LocalBroadcastManager.getInstance(HTApp.getContext()).sendBroadcast(new Intent(IMAction.ACTION_NEW_MESSAGE).putExtra("message",htMessage));


            }
        });
    }


    private void leaveGroup() {
        CommonUtils.showDialogNumal(this, getString(R.string.exting_group));
        HTClient.getInstance().groupManager().leaveGroup(groupId, HTApp.getInstance().getUserNick(), new GroupManager.CallBack() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();

                        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(IMAction.ACTION_DELETE_GROUP).putExtra("userId", groupId));
                        CommonUtils.showToastShort(getApplicationContext(), R.string.exting_group_success);
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(getApplicationContext(), R.string.exting_group_failed);
                    }
                });
            }

            @Override
            public void onHTMessageSend(HTMessage htMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.upLoadMessage(htMessage);
                    }
                });
            }
        });
    }



    /**
     * 禁言或者取消禁言
     *
     * @param groupId
     * @param  isSilent  true-禁言  false-解除禁言
     */
    private void noTalkAllOrCancle(final String groupId, final boolean isSilent) {
        CommonUtils.showDialogNumal(ChatSettingGroupActivity.this, R.string.being_setting);
        JSONObject data=new JSONObject();
        data.put("groupId",groupId);
        if(isSilent){
            data.put("status",1);
        }else {
            data.put("status",0);
        }
        ApiUtis.getInstance().postJSON(data, Constant.URL_GROUP_SILENT, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null){
                    return;
                }
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    Message message=handler.obtainMessage();
                    message.what=1001;
                    message.obj=isSilent;
                    message.sendToTarget();
                }else {
                    Message message=handler.obtainMessage();
                    message.what=1002;
                    message.arg1=R.string.set_failed;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                if(handler==null){
                    return;
                }
                Message message=handler.obtainMessage();
                message.what=1002;
                message.arg1=errorCode;
                message.sendToTarget();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        GroupInfoManager.getInstance().getGroupAllMembersFromServer(groupId, new GroupInfoManager.CallBack() {
            @Override
            public void onDataSuccess(JSONArray jsonArray) {
                if(handler==null){
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle("群组详情("+GroupInfoManager.getInstance().getGroupAllMembersFromLocal(groupId).size()+"人)");
                        if(jsonArray!=null&&jsonArray.size()>0){
                            membersJSONArray.clear();
                            membersJSONArray.addAll(jsonArray.subList(0,Math.min(jsonArray.size(),13)));
                            adapter.notifyDataSetChanged();

                        }
                    }
                });
            }
        });

    }

    /**
     * 查询是否被禁言
     *
     * @param context
     * @param groupId
     */
    private void checkNoTalkOrNo(Context context, final String groupId) {
        if (GroupInfoManager.getInstance().isGroupSilent(groupId)) {
            switch_no_talk.openSwitch();
        } else {
            switch_no_talk.closeSwitch();
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_block_groupmsg:
                if (switch_block_groupmsg.isSwitchOpen() ) {
                    //消息不通知变为通知
                    switch_block_groupmsg.closeSwitch();
                    SettingsManager.getInstance().setNotifyGroupOrUser(groupId,true);

                } else {
                    //消息通知变为不通知
                    switch_block_groupmsg.openSwitch();
                    SettingsManager.getInstance().setNotifyGroupOrUser(groupId,false);
                 }

                break;
            case R.id.switch_no_talk:
                if (!isOwner) {
                    CommonUtils.showToastShort(getBaseContext(), R.string.notalk_group_just_owner);
                    return;
                }
                if (switch_no_talk.isSwitchOpen()&&GroupInfoManager.getInstance().isGroupSilent(groupId)) {
                    //不禁言全群
                    noTalkAllOrCancle(groupId, false);
                } else if(!switch_no_talk.isSwitchOpen()&&!GroupInfoManager.getInstance().isGroupSilent(groupId)){
                    //禁言全群
                    noTalkAllOrCancle(groupId, true);
                }
                break;
            case R.id.tv_see_all:
                startActivity(new Intent(ChatSettingGroupActivity.this, AllGroupMembersActivity.class).putExtra("groupId", groupId));
                break;
            case R.id.re_group_qrcode:
                JSONObject object = new JSONObject();
                object.put("groupId", groupId);
                object.put("groupAvatar", htGroup.getImgUrl());
                object.put("groupName", htGroup.getGroupName());
                object.put("creator", htGroup.getOwner());
                startActivity(new Intent(ChatSettingGroupActivity.this, GroupQrCodeActivity.class).putExtra("groupObj", object.toJSONString()));
                break;
            case R.id.re_manager:
                startActivity(new Intent(ChatSettingGroupActivity.this, ManagerListActivity.class).putExtra("groupId", groupId));
                break;
            case R.id.re_blacklist:

                startActivity(new Intent(ChatSettingGroupActivity.this, BlackListActivity.class).putExtra("groupId", groupId));


                break;
        }
    }


    /**
     * 显示禁言的view
     *
     * @param notalk
     */
    private void showNoTalkView(boolean notalk) {
        if (notalk) {
            checkNoTalkOrNo(ChatSettingGroupActivity.this, groupId);
            rl_switch_no_talk.setVisibility(View.VISIBLE);
        } else {
            rl_switch_no_talk.setVisibility(View.GONE);
        }
    }




}
