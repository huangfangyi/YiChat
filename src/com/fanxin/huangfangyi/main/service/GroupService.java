package com.fanxin.huangfangyi.main.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.fanxin.huangfangyi.main.utils.GroupUitls;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

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
                Bundle bundle=msg.getData();
                String groupId= bundle.getString("groupId");
                String groupName=bundle.getString("groupName");
                GroupUitls.getInstance(). getGroupMembersInServer(groupId,groupName,null);
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
        try{


        String groupId=intent.getStringExtra("groupId");
        String groupName=intent.getStringExtra("groupName");
        refreshGroupInfo(groupId,groupName);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private  void  refreshGroupInfo(String groupId,String groupName){

        if(TextUtils.isEmpty(groupId)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //从服务器获取自己加入的和创建的群组列表，此api获取的群组sdk会自动保存到内存和db。
                    try {
                        List<EMGroup> grouplist = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();//需异步处理
                        for(   EMGroup emGroup:   grouplist){
                            EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(emGroup.getGroupId());
                            if(group!=null&&group.getGroupId()!=null){
                                Bundle bundle=new Bundle();
                                bundle.putString("groupId",group.getGroupId());
                                bundle.putString("groupName",group.getGroupName());
                                Message msg=handler.obtainMessage();
                                msg.what=1000;
                                msg.setData(bundle);
                                msg.sendToTarget();
                            }
                        }
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }else {

            GroupUitls.getInstance().getGroupMembersInServer(groupId,groupName, new GroupUitls.MembersCallBack() {
                @Override
                public void onSuccess(JSONArray jsonArray) {

                }

                @Override
                public void onFailure() {

                }
            });
        }

    }


}
