package com.fanxin.huangfangyi.main.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.db.UserDao;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.utils.JSONUtil;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.easeui.domain.EaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangfangyi on 2016/7/7.\
 * QQ:84543217
 */
public class ContactsService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ContactsService---->",flags+"");
        getContactsInServer();

        return super.onStartCommand(intent, flags, startId);
    }


    public void getContactsInServer() {

        if (!DemoHelper.getInstance().isLoggedIn()) {
            return;
        }
        List<Param> params = new ArrayList<Param>();
        params.add(new Param("hxid", DemoHelper.getInstance().getCurrentUsernName()));
        OkHttpManager.getInstance().post(params, FXConstant.URL_FriendList, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getInteger("code");
                if (code == 1000) {
                    JSONArray josnArray = jsonObject.getJSONArray("friends");
                    Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
                    if (josnArray != null) {
                        for (int i = 0; i < josnArray.size(); i++) {
                            JSONObject friend = josnArray.getJSONObject(i);
                            EaseUser easeUser = JSONUtil.Json2User(friend);
                            userlist.put(easeUser.getUsername(), easeUser);
                        }
                        // save the contact list to cache
                        DemoHelper.getInstance().getContactList().clear();
                        DemoHelper.getInstance().getContactList().putAll(userlist);
                        // save the contact list to database
                        UserDao dao = new UserDao(getApplicationContext());
                        List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
                        dao.saveContactList(users);

                    }

                }

            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });


    }
}
