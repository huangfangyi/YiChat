package com.fanxin.app.main.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.main.FXConstant;
import com.fanxin.app.main.db.ACache;
import com.fanxin.app.main.utils.OkHttpManager;
import com.fanxin.app.main.utils.Param;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2016/7/8.\
 * QQ:84543217
 */
public class GroupService extends Service{
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1000){
                //JSONArray jsonArray= (JSONArray) msg.obj;
                String groupId= (String) msg.obj;
                getGroupMembersInServer(groupId);

            }

        }

    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        refreshGroupInfo();
        return super.onStartCommand(intent, flags, startId);
    }

    private  void  refreshGroupInfo(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                //从服务器获取自己加入的和创建的群组列表，此api获取的群组sdk会自动保存到内存和db。
                try {
                    List<EMGroup> grouplist = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();//需异步处理
                    for(   EMGroup emGroup:   grouplist){
                        EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(emGroup.getGroupId());
                        if(group!=null&&group.getGroupId()!=null){
                            Message msg=handler.obtainMessage();
                            msg.what=1000;
                            msg.obj=group.getGroupId();
                            msg.sendToTarget();
                        }
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private  void   getGroupMembersInServer(final String groupId){

        List<Param> params = new ArrayList<>();
        params.add(new Param("groupId", groupId));
        OkHttpManager.getInstance().post(params, FXConstant.URL_GROUP_MEMBERS, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                if (code == 1000) {
                    if (jsonObject.containsKey("data") && jsonObject.get("data") instanceof JSONArray) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        ACache.get(getApplicationContext()).put(groupId,jsonArray);
                    }
                }

            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });


    }

}
