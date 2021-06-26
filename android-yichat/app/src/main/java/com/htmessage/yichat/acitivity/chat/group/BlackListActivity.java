package com.htmessage.yichat.acitivity.chat.group;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.update.Constant;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.chat.group.managerlist.ManagerListAdapter;
import com.htmessage.yichat.domain.User;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.widget.HTAlertDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by huangfangyi on 2019/7/16.
 * qq 84543217
 */
public class BlackListActivity extends BaseActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipyrefresh;
    private ListView lv_group_member;
    private List<User> membersJSONArray = new ArrayList<>();
    private String groupId;
    private HTGroup htGroup;
     private ManagerListAdapter adapter;
  //  private  OnManagerCancleBroadcastReceiver receiver;

    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
             switch (msg.what){
                 case 1000:
                     CommonUtils.cencelDialog();
                     User user= (User) msg.obj;
                     membersJSONArray.remove(user);
                     adapter.notifyDataSetChanged();
                     sendCmdToUser(user.getUserId());
                     break;

                 case 1001:
                     CommonUtils.cencelDialog();
                     int resId=msg.arg1;
                     Toast.makeText(BlackListActivity.this,resId,Toast.LENGTH_SHORT).show();
                     break;

                 case 1002:
                     JSONArray jsonArray= (JSONArray) msg.obj;
                     membersJSONArray.clear();
                     for(int i=0;i<jsonArray.size();i++){
                         JSONObject  jsonObject=jsonArray.getJSONObject(i);
                         User user1=new User(jsonObject);
                         membersJSONArray.add(user1);
                     }
                      adapter.notifyDataSetChanged();
                     break;


             }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_blacklist);
        setTitle("禁言列表");
        getData();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        lv_group_member.setOnItemClickListener(this);
        swipyrefresh.setOnRefreshListener(this);

    }

    private void initData() {
                 JSONArray jsonArray= GroupInfoManager.getInstance().getsilentUsers(groupId);
        if(jsonArray!=null){
            membersJSONArray.clear();

            for(int i=0;i<jsonArray.size();i++){
                JSONObject  jsonObject=jsonArray.getJSONObject(i);
                User user=new User(jsonObject);
                membersJSONArray.add(user);
            }

            refreshListView(membersJSONArray);
        }
    }

    private void initView() {
        swipyrefresh = (SwipeRefreshLayout) findViewById(R.id.swipyrefresh);
        lv_group_member = (ListView) findViewById(R.id.lv_group_member);
     }

    private void getData() {
        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {
            finish();
            return;
        }
        htGroup = HTClient.getInstance().groupManager().getGroup(groupId);
        if (htGroup == null) {
            finish();
            return;
        }





//        List<JSONObject> managerList = CommonUtils.getManagerList(BlackListActivity.this, groupId);
//        arrayToList(managerList, membersJSONArray);

//        receiver = new  OnManagerCancleBroadcastReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(IMAction.ACTION_SET_OR_CANCLE_GROUP_MANAGER);
//        LocalBroadcastManager.getInstance(BlackListActivity.this).registerReceiver(receiver, filter);
    }
//
//    private void arrayToList(List<User> membersJSONArray) {
//        JSONArray memberCache = MmvkManger.getIntance().getJSONArray(HTApp.getInstance().getUsername() + groupId);
//        if (memberCache == null) {
//            return;
//        }
//        for (int i = 0; i < memberCache.size(); i++) {
//            JSONObject jsonObjectTemp = memberCache.getJSONObject(i);
//            User user = CommonUtils.Json2User(jsonObjectTemp);
//            if (user.getUsername().equals(htGroup.getOwner())) {
//                if (!membersJSONArray.contains(user)) {
//                    membersJSONArray.add(user);
//                }
//            }
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       final User user= adapter.getItem(position);

        HTAlertDialog dialog = new HTAlertDialog(BlackListActivity.this, null, new String[]{"解除禁言"});

        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        CommonUtils.showDialogNumal(BlackListActivity.this,"正在处理");
                        JSONObject body=new JSONObject();
                        body.put("groupId",groupId);
                        body.put("userId",user.getUserId());
                        body.put("status",0);
                        ApiUtis.getInstance().postJSON(body, Constant.URL_GROUP_SILENT_MEMBER, new ApiUtis.HttpCallBack() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                              if(handler==null){
                                  return;
                              }
                                String code=jsonObject.getString("code");
                              if("0".equals(code)){
                                  GroupInfoManager.getInstance().deleteFromSilentUsersLocal(groupId,user.getUserId());
                                  Message message=handler.obtainMessage();
                                  message.what=1000;
                                  message.obj=user;
                                  message.sendToTarget();
                              }else {
                                  Message message=handler.obtainMessage();
                                  message.what=1001;
                                  message.arg1=R.string.api_error_5;
                                  message.sendToTarget();
                              }


                            }

                            @Override
                            public void onFailure(int errorCode) {
                                if(handler==null){
                                    return;
                                }
                                Message message=handler.obtainMessage();
                                message.what=1001;
                                message.arg1=errorCode;
                                message.sendToTarget();

                            }
                        });

//                        CommonUtils.showDialogNumal(BlackListActivity.this,"正在处理");
//                        List<Param> params=new ArrayList<>();
//                        params.add(new Param("userid",user.getUsername()));
//                        params.add(new Param("groupid",groupId));
//                        new OkHttpUtils(BlackListActivity.this).post(params, HTConstant.URL_NO_TALK_USER_CANCEL, new OkHttpUtils.HttpCallBack() {
//                            @Override
//                            public void onResponse(JSONObject jsonObject) {
//                                CommonUtils.cencelDialog();
//                                int code=jsonObject.getInteger("code");
//                                if(code==1){
//                                    CommonUtils.showToastShort(BlackListActivity.this,"解除禁言成功");
//                                    sendCmdToUser(user.getUsername());
//                                    refreshGroupManagerList();
//                                }
//
//                            }
//
//                            @Override
//                            public void onFailure(String errorMsg) {
//                                CommonUtils.cencelDialog();
//                                CommonUtils.showToastShort(BlackListActivity.this,"处理失败");
//
//
//                            }
//                        });





                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void sendCmdToUser(String userId){
        JSONObject data=new JSONObject();
        data.put("action",30005);
        data.put("data",groupId);

        CmdMessage customMessage = new CmdMessage();
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setFrom(HTApp.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(userId);
        customMessage.setBody( data.toJSONString());
        customMessage.setChatType(ChatType.singleChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(long timeStamp) {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void onRefresh() {
         refreshGroupManagerList();
    }

    public void refreshGroupManagerList() {

        JSONObject body=new JSONObject();
        body.put("groupId",groupId);
        ApiUtis.getInstance().postJSON(body, Constant.URL_GROUP_SILENT_LIST, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null){
                    return;
                }
                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    JSONArray data=jsonObject.getJSONArray("data");
                     GroupInfoManager.getInstance().setsilentUsers(groupId,data);
                     Message message=handler.obtainMessage();
                    message.what=1002;
                    message.obj=data;
                    message.sendToTarget();
                }else {
                    Message message=handler.obtainMessage();
                    message.what=1001;
                    message.arg1=R.string.api_error_5;
                    message.sendToTarget();
                }

            }

            @Override
            public void onFailure(int errorCode) {
                if(handler==null){
                    return;
                }
                Message message=handler.obtainMessage();
                message.what=1001;
                message.arg1=errorCode;
                message.sendToTarget();
            }
        });


//        JSONArray jsonArray= GroupInfoManager.getInstance().getsilentUsers(groupId);
//        if(jsonArray!=null){
//            membersJSONArray.clear();
//
//            for(int i=0;i<jsonArray.size();i++){
//                JSONObject  jsonObject=jsonArray.getJSONObject(i);
//                User user=new User(jsonObject);
//                membersJSONArray.add(user);
//            }
//
//            refreshListView(membersJSONArray);
//        }



//        List<Param> params = new ArrayList<>();
//        params.add(new Param("groupid", groupId));
//        params.add(new Param("userid", HTApp.getInstance().getUsername()));
//        new OkHttpUtils(BlackListActivity.this).post(params, HTConstant.URL_NO_TALK_LIST, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                swipyrefresh.setRefreshing(false);
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONArray data = jsonObject.getJSONArray("data");
//                         if (data != null && data.size() != 0) {
//                            arrayToList(data, membersJSONArray);
//                            refreshListView(membersJSONArray);
//                            break;
//                        }
////                         else {
////                            membersJSONArray.clear();
////                            arrayToList(membersJSONArray);
////                            refreshListView(membersJSONArray);
////                        }
//                    case -1:
//                        membersJSONArray.clear();
//                        refreshListView(membersJSONArray);
////                        membersJSONArray.clear();
////                        arrayToList(membersJSONArray);
////                        refreshListView(membersJSONArray);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                swipyrefresh.setRefreshing(false);
////                membersJSONArray.clear();
////                arrayToList(membersJSONArray);
////                refreshListView(membersJSONArray);
//            }
//        });
    }

    private void refreshListView(List<User> membersJSONArray) {
        Collections.sort(membersJSONArray, new  PinyinComparator() {
        });
        adapter = new ManagerListAdapter(BlackListActivity.this, membersJSONArray);
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


//    /**
//     * 删除成员或者设置取消成员管理员的监听
//     */
//    private class OnManagerCancleBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (IMAction.ACTION_SET_OR_CANCLE_GROUP_MANAGER.equals(intent.getAction())) {
//                String refreshGroupId = intent.getStringExtra("groupId");
//                String userId = intent.getStringExtra(HTConstant.JSON_KEY_USERID);
//                int action = intent.getIntExtra(HTConstant.JSON_KEY_LOGINID, 0);
//                if (groupId.equals(refreshGroupId)) {
//                    refreshGroupManagerList();
//                    if (HTApp.getInstance().getUsername().equals(userId)) {
//                        if (BlackListActivity.this != null) {
//                            if (action == 30002) {
//                                CommonUtils.showToastShort(BlackListActivity.this, R.string.owner_set_manager);
//                            } else {
//                                CommonUtils.showToastShort(BlackListActivity.this, R.string.owner_cancle_manager);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    @Override
    public void onDestroy() {
        handler=null;

        super.onDestroy();
    }
}
