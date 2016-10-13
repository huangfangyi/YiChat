package com.fanxin.huangfangyi.main.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.db.ACache;
import com.fanxin.huangfangyi.main.service.GroupService;
import com.fanxin.easeui.domain.EaseUser;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2016/10/8.
 * qq 84543217
 */

public class GroupUitls {

    private static final int UPDATE_GROUP_NAME = 1000;


    private Handler hanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_GROUP_NAME:

                    Bundle bundle = msg.getData();
                    String groupId = bundle.getString("groupId");
                    String groupName = bundle.getString("groupName");

                    updateGroupName(groupId, groupName, null);
                    break;
            }

        }
    };


    private static Context context;
    private static GroupUitls groupUitls;


    public GroupUitls(Context context) {
        this.context = context;

    }


    public static synchronized void init(Context context) {
        if (groupUitls == null) {
            groupUitls = new GroupUitls(context);
        }

    }


    public static GroupUitls getInstance() {

        if (groupUitls == null) {

            throw new RuntimeException("please init GroupUtils first");
        }

        return groupUitls;
    }

    public void updateGroupName(String groupId, String groupName, final CallBack callBack) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("groupId", groupId));
        params.add(new Param("groupName", groupName));
        OkHttpManager.getInstance().post(params, FXConstant.URL_UPDATE_Groupnanme, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (callBack == null) {
                    return;
                }
                int code = jsonObject.getIntValue("code");
                if (code == 1) {
                    callBack.onSuccess();
                } else {
                    callBack.onError();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                if (callBack != null) {
                    callBack.onError();
                }

            }
        });
    }

    public void addMembersToGroup(final String groupId, List<String> memebers, final List<String> exitingMembers, final CallBack callBack) {
        final String[] newmembers = memebers.toArray(new String[0]);
        List<Param> params = new ArrayList<>();
        String membersStr = "";
        for (int i = 0; i < newmembers.length; i++) {
            if (i == 0) {
                membersStr = newmembers[0];
            } else {
                membersStr = membersStr + "#" + newmembers[i];
            }
        }

        params.add(new Param("groupId", groupId));
        params.add(new Param("members", membersStr));
        OkHttpManager.getInstance().post(params, FXConstant.URL_GROUP_ADD_MEMBERS, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                if (code == 1000) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data.containsKey("data") && data.get("data") instanceof JSONObject && data.getJSONObject("data").containsKey("newmembers") && data.getJSONObject("data").get("newmembers") instanceof JSONArray) {

                        JSONArray jsonArray = data.getJSONObject("data").getJSONArray("newmembers");
                        if (jsonArray != null && jsonArray.size() != 0) {
                            checkGroupName(groupId, newmembers, exitingMembers);
                            callBack.onSuccess();
                            context.startService(new Intent(context, GroupService.class));
                            return;
                        }
                    }

                }
                callBack.onError();
            }

            @Override
            public void onFailure(String errorMsg) {
                callBack.onError();
            }
        });

    }


    private void checkGroupName(final String groupId, final String[] newmembers, final List<String> exitingMembers) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EMGroup emGroup = EMClient.getInstance().groupManager().getGroup(groupId);
                String groupName = emGroup.getGroupName();
                JSONObject groupJson = JSONObject.parseObject(groupName);
                JSONArray jsonArray = groupJson.getJSONArray("jsonArray");
                if (exitingMembers.size() < 9) {
                    int add = Math.min(9 - exitingMembers.size(), newmembers.length);
                    for (int i = 0; i < add; i++) {
                        String hxid = newmembers[i];
                        EaseUser easeUser = DemoHelper.getInstance().getContactList().get(hxid);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("avatar", easeUser.getAvatar());
                        jsonObject.put("nick", easeUser.getNick());
                        jsonObject.put("hxid", easeUser.getUsername());
                        jsonArray.add(jsonObject);
                    }
                    groupJson.put("jsonArray", jsonArray);
                }

                Message message = hanlder.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("groupId", groupId);
                bundle.putString("groupName", groupJson.toJSONString());
                message.setData(bundle);
                message.what = UPDATE_GROUP_NAME;
                message.sendToTarget();
            }
        }).start();


    }

    public interface CallBack {
        void onSuccess();

        void onError();
    }


    public void checkGroupNameWhenDetele(final String groupName, final String groupId, final String hxid) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isChange = false;
                try {
                    JSONObject jsonObject = JSONObject.parseObject(groupName);
                    JSONArray jsonArray = jsonObject.getJSONArray("jsonArray");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject userJson = jsonArray.getJSONObject(i);
                        if (hxid.equals(userJson.getString("hxid"))) {
                            jsonArray.remove(userJson);
                            isChange = true;
                        }
                    }
                    if (isChange) {
                        jsonObject.put("jsonArray", jsonArray);
                        Message message = hanlder.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("groupId", groupId);
                        bundle.putString("groupName", jsonObject.toJSONString());
                        message.setData(bundle);
                        message.what = UPDATE_GROUP_NAME;
                        message.sendToTarget();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }


    public void getGroupMembersInServer(final String groupId, final String groupName, final MembersCallBack membersCallBack) {

        List<Param> params = new ArrayList<>();
        params.add(new Param("groupId", groupId));
        OkHttpManager.getInstance().post(params, FXConstant.URL_GROUP_MEMBERS, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                if (jsonObject.containsKey("code")) {
                    int code = Integer.parseInt(jsonObject.getString("code"));
                    if (code == 1000) {


                        if (jsonObject.containsKey("data") && jsonObject.get("data") instanceof JSONArray) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (jsonArray != null && jsonArray.size() != 0) {

                                ACache.get(context).put(groupId, jsonArray);

                                if (membersCallBack != null) {
                                    membersCallBack.onSuccess(jsonArray);
                                    checkGroupNameWhenUpdate(jsonArray, groupName, groupId);

                                    return;
                                }
                            }


                        }
                    }

                    if (membersCallBack != null) {
                        membersCallBack.onFailure();

                    }

                }
            }

            @Override
            public void onFailure(String errorMsg) {
                if (membersCallBack != null) {
                    membersCallBack.onFailure();

                }
            }
        });


    }

    public interface MembersCallBack {
        void onSuccess(JSONArray jsonArray);

        void onFailure();
    }


    private void checkGroupNameWhenUpdate(JSONArray existedMembers, String groupName, String groupId) {


        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
        if (TextUtils.isEmpty(groupName) || existedMembers == null || existedMembers.size() == 0) {

            return;
        }


        try {
            JSONObject jsonObject = JSONObject.parseObject(groupName);
            JSONArray jsonArray = jsonObject.getJSONArray("jsonArray");

            if (jsonArray.size() > 8) {
                return;

            }

            if (existedMembers.size() == jsonArray.size()) {
                return;

            }
            //检查现在的数据是否还包含在群成员列表里,不在的则删除

            for (int i = 0; i < jsonArray.size(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                boolean isCotains = false;
                for (int n = 0; n < existedMembers.size(); n++) {
                    JSONObject jsonObject2 = existedMembers.getJSONObject(n);
                    if (jsonObject1.getString("hxid").equals(jsonObject2.getString("hxid"))) {
                        isCotains = true;

                    }

                }

                if (!isCotains) {
                    jsonArray.remove(jsonObject1);
                }

            }

            if (jsonArray.size() < 9 && existedMembers.size() > jsonArray.size()) {

                for (int i = 0; i < existedMembers.size(); i++) {
                    JSONObject jsonObject1 = existedMembers.getJSONObject(i);
                    boolean isContains = false;
                    for (int n = 0; n < jsonArray.size(); n++) {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(n);
                        if (jsonObject1.getString("hxid").equals(jsonObject2.getString("hxid"))) {
                            isContains = true;
                        }
                    }
                    if (!isContains) {
                        jsonArray.add(jsonObject1);
                    }

                }
            }
            jsonObject.put("jsonArray", jsonArray);
            Message message = hanlder.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("groupId", groupId);
            bundle.putString("groupName", jsonObject.toJSONString());
            message.setData(bundle);
            message.what = UPDATE_GROUP_NAME;
            message.sendToTarget();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
