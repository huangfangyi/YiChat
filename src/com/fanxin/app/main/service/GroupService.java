package com.fanxin.app.main.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

/**
 * Created by huangfangyi on 2016/7/8.\
 * QQ:84543217
 */
public class GroupService extends Service{
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
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
