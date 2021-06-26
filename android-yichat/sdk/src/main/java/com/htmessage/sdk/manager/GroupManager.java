package com.htmessage.sdk.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.SDKConstant;
import com.htmessage.sdk.client.HTAction;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.data.dao.GroupDao;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageBody;
import com.htmessage.sdk.utils.HTHttpUtils;
import com.htmessage.sdk.utils.UploadFileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.measite.minidns.record.A;

/**
 * Created by huangfangyi on 2016/12/22.
 * qq 84543217
 */

public class GroupManager {

    //   private static GroupManager groupManager;
    private Context context;
    private GroupDao groupDao;
    private Map<String, HTGroup> allGroups = new HashMap<>();
    private String baseOssUrl = "";


//    synchronized public static void init(Context context) {
//        Log.d("Cache---->","MessageManager:"+ HTPreferenceManager.getInstance().getUser().getUsername());
//        groupManager = new GroupManager(context);
//
//    }

    public GroupManager(Context context) {
        this.context = context;
        if (SDKConstant.IS_LIMITLESS) {

            baseOssUrl = HTPreferenceManager.getInstance().getOssBaseUrl();
        } else {

            baseOssUrl = SDKConstant.baseOssUrl;
        }

        groupDao = new GroupDao(context);
        initAllGroup();

    }

//    public static GroupManager getInstance() {
//        if (groupManager == null) {
//            throw new RuntimeException("please init first!");
//        }
//        return groupManager;
//    }

    //获取所有消息
    private void initAllGroup() {
        if (allGroups == null || allGroups.size() == 0) {


            allGroups = groupDao.getAllGroups();

        }
        getGroupList(true, null);
    }


    private void getGroupList(final boolean isNotify, final GroupListCallBack groupListCallBack) {
        HTHttpUtils htHttpUtils = new HTHttpUtils(context);
        if (HTPreferenceManager.getInstance().getUser() == null) {
            return;
        }
        htHttpUtils.getGroupList(HTPreferenceManager.getInstance().getUser().getUsername(), new HTHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                     JSONArray groupArray=jsonObject.getJSONArray("data");

                    List<HTGroup> grouplist = new ArrayList<HTGroup>();
                    for (int i = 0; i < groupArray.size(); i++) {
                        JSONObject group = groupArray.getJSONObject(i);
                        grouplist.add(HTGroup.getHTGroup(group));
                    }
                    if (isNotify) {
                        saveGroupLiset(grouplist);
                        Intent intent = new Intent();
                        intent.setAction(HTAction.ACTION_GROUPLIST_LOADED);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    }

                    if (groupListCallBack != null) {
                        groupListCallBack.onSuccess(grouplist);
                        return;
                    }

                } else {
                    if (groupListCallBack != null) {
                        groupListCallBack.onFailure();
                    }
                }

//                JSONObject command = jsonObject.getJSONObject("command");
//
//                if (command != null) {
//
//                    JSONArray groups = command.getJSONArray("fields");
//                    if (groups != null) {
//
//                    }
//
//
//                }
//                if (groupListCallBack != null) {
//                    groupListCallBack.onFailure();
//
//                }
            }

            @Override
            public void onFailure(String errorMsg) {
                if (groupListCallBack != null) {
                    groupListCallBack.onFailure();
                }
            }
        });

    }


    public void loadGroupListFromServer(GroupListCallBack groupListCallBack) {
        getGroupList(false, groupListCallBack);
    }


    public interface GroupListCallBack {

        void onSuccess(List<HTGroup> htGroups);

        void onFailure();
    }


    public HTGroup getGroup(String groupId) {

        return allGroups.get(groupId);
    }


    // 获取所有群组
    public List<HTGroup> getAllGroups() {
        if (allGroups == null) {

            new ArrayList<>();
        }

        return new ArrayList<HTGroup>(allGroups.values());
    }


    public synchronized void saveGroup(HTGroup htGroup) {
        allGroups.put(htGroup.getGroupId(), htGroup);
        GroupDao groupDao = new GroupDao(context);
        groupDao.saveGroup(htGroup);
    }

    public synchronized void saveGroupLiset(List<HTGroup> htGroups) {
        allGroups.clear();
        for (HTGroup htGroup : htGroups) {

            allGroups.put(htGroup.getGroupId(), htGroup);
        }
        GroupDao groupDao = new GroupDao(context);
        groupDao.saveGroupList(htGroups);
    }

    public void deleteGroupLocalOnly(String groupId) {
        GroupDao groupDao = new GroupDao(context);
        groupDao.deleteGroup(groupId);
        allGroups.remove(groupId);
        HTClient.getInstance().messageManager().deleteUserMessage(groupId, true);
    }

//    public void deleteMemberByAdmin(final String groupId, final String userId, final String userNick, final CallBack callBack) {
//
//        HTHttpUtils htHttpUtils = new HTHttpUtils(context);
//        htHttpUtils.deleteMember(groupId, HTPreferenceManager.getInstance().getUser().getUsername(), userId, new HTHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                JSONObject command = jsonObject.getJSONObject("command");
//
//                if (command != null) {
//
//                    JSONArray fields = command.getJSONArray("fields");
//                    if (fields != null) {
//                        JSONObject result = fields.getJSONObject(0);
//                        if (result != null && result.containsKey("var") && result.getString("var").equals("code")) {
//
//                            int code = result.getInteger("value");
//                            if (code == 1) {
//
//                                JSONObject extJson = new JSONObject();
//                                extJson.put("action", 2004);
//                                extJson.put("uid", userId);
//                                extJson.put("nickName", userNick);
//                                sendNoticeMessage(extJson, groupId, userNick + "被踢出群聊", callBack);
//
//                                //发一条透传给被删除的用户
//
//                                //{"action":"2004":"data":"当前群id"}
//                                JSONObject cmdJson = new JSONObject();
//                                cmdJson.put("action", 2004);
//                                cmdJson.put("data", groupId);
//                                sendCustomMessage(cmdJson.toJSONString(), ChatType.singleChat, userId);
//
//                                JSONObject cmdJsonGroup = new JSONObject();
//                                cmdJsonGroup.put("action", 2008);
//                                cmdJsonGroup.put("data", groupId);
//                                sendCustomMessage(cmdJsonGroup.toJSONString(), ChatType.groupChat, groupId);
//                                callBack.onSuccess(null);
//                                return;
//
//                            }
//                        }
//                    }
//
//
//                }
//
//                callBack.onFailure();
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                callBack.onFailure();
//            }
//        });
//
//
//    }
//
//    public void deleteMemberByNormal(final String groupId, final String userId, final String userNick, final String adminId, final CallBack callBack) {
//
//        HTHttpUtils htHttpUtils = new HTHttpUtils(context);
//        htHttpUtils.deleteMember(groupId, adminId, userId, new HTHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                JSONObject command = jsonObject.getJSONObject("command");
//
//                if (command != null) {
//
//                    JSONArray fields = command.getJSONArray("fields");
//                    if (fields != null) {
//                        JSONObject result = fields.getJSONObject(0);
//                        if (result != null && result.containsKey("var") && result.getString("var").equals("code")) {
//
//                            int code = result.getInteger("value");
//                            if (code == 1) {
//
//                                JSONObject extJson = new JSONObject();
//                                extJson.put("action", 2004);
//                                extJson.put("uid", userId);
//                                extJson.put("nickName", userNick);
//                                sendNoticeMessage(extJson, groupId, userNick + "被踢出群聊", callBack);
//
//                                //发一条透传给被删除的用户
//
//                                //{"action":"2004":"data":"当前群id"}
//                                JSONObject cmdJson = new JSONObject();
//                                cmdJson.put("action", 2004);
//                                cmdJson.put("data", groupId);
//                                sendCustomMessage(cmdJson.toJSONString(), ChatType.singleChat, userId);
//                                JSONObject cmdJsonGroup = new JSONObject();
//                                cmdJsonGroup.put("action", 2008);
//                                cmdJsonGroup.put("data", groupId);
//                                sendCustomMessage(cmdJsonGroup.toJSONString(), ChatType.groupChat, groupId);
//                                callBack.onSuccess(null);
//                                return;
//
//                            }
//                        }
//                    }
//
//
//                }
//
//                callBack.onFailure();
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                callBack.onFailure();
//            }
//        });
//
//
//    }
//

//    public void deleteMemberByNormal(final String groupId, final String userId, final String userNick, final String adminId, final boolean isNotify, final CallBack callBack) {
//
//        HTHttpUtils htHttpUtils = new HTHttpUtils(context);
//        htHttpUtils.deleteMember(groupId, adminId, userId, new HTHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                JSONObject command = jsonObject.getJSONObject("command");
//
//                if (command != null) {
//
//                    JSONArray fields = command.getJSONArray("fields");
//                    if (fields != null) {
//                        JSONObject result = fields.getJSONObject(0);
//                        if (result != null && result.containsKey("var") && result.getString("var").equals("code")) {
//
//                            int code = result.getInteger("value");
//                            if (code == 1) {
//                                if (isNotify) {
//                                    JSONObject extJson = new JSONObject();
//                                    extJson.put("action", 2004);
//                                    extJson.put("uid", userId);
//                                    extJson.put("nickName", userNick);
//                                    sendNoticeMessage(extJson, groupId, userNick + "被踢出群聊", callBack);
//
//                                }
//
//                                //发一条透传给被删除的用户
//
//                                //{"action":"2004":"data":"当前群id"}
//                                JSONObject cmdJson = new JSONObject();
//                                cmdJson.put("action", 2004);
//                                cmdJson.put("data", groupId);
//                                sendCustomMessage(cmdJson.toJSONString(), ChatType.singleChat, userId);
//
//                                JSONObject cmdJsonGroup = new JSONObject();
//                                cmdJsonGroup.put("action", 2008);
//                                cmdJsonGroup.put("data", groupId);
//                                sendCustomMessage(cmdJsonGroup.toJSONString(), ChatType.groupChat, groupId);
//                                callBack.onSuccess(null);
//                                return;
//
//                            }
//                        }
//                    }
//
//
//                }
//
//                callBack.onFailure();
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                callBack.onFailure();
//            }
//        });
//
//
//    }
    public void deleteMemberByNormal(final String groupId, final Map<String ,String> members, final String adminId, final boolean isNotify, final CallBack callBack) {

        HTHttpUtils htHttpUtils = new HTHttpUtils(context);
        htHttpUtils.deleteMember(groupId, adminId, members, new HTHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONObject command = jsonObject.getJSONObject("command");

                if (command != null) {

                    JSONArray fields = command.getJSONArray("fields");
                    if (fields != null) {
                        JSONObject result = fields.getJSONObject(0);
                        if (result != null && result.containsKey("var") && result.getString("var").equals("code")) {

                            int code = result.getInteger("value");
                            if (code == 1) {

                                  //获取userid列表
                                  List<String> userIds=new ArrayList<>(members.keySet());
                                  String userId="";
                                  for(String userIdTemp:userIds){
                                      userId=userId+userIdTemp+",";
                                      JSONObject cmdJson = new JSONObject();
                                      cmdJson.put("action", 2004);
                                      cmdJson.put("data", groupId);
                                      sendCustomMessage(cmdJson.toJSONString(), ChatType.singleChat, userIdTemp);
                                  }
                                  userId=userId.substring(0,userId.length()-1);
                                if (isNotify) {
                                  //获取昵称的拼接
                                  List<String> userNickList=new ArrayList<>(members.values());
                                  String userNick="";
                                  for(String nickTemp:userNickList){
                                      userNick=userNick+nickTemp+"、";
                                  }
                                  userNick=userNick.substring(0,userNick.length()-1);

                                    JSONObject extJson = new JSONObject();
                                    extJson.put("action", 2004);
                                    extJson.put("uid", userId);
                                    extJson.put("nickName", userNick);
                                    sendNoticeMessage(extJson, groupId, userNick + "被踢出群聊", callBack);


//                                JSONObject cmdJsonGroup = new JSONObject();
//                                cmdJsonGroup.put("action", 2008);
//                                cmdJsonGroup.put("data", groupId);
//                                sendCustomMessage(cmdJsonGroup.toJSONString(), ChatType.groupChat, groupId);


                                }


                                //发一条透传给被删除的用户

                                //{"action":"2004":"data":"当前群id"}



                            }
                            callBack.onSuccess(null);
                            return;
                        }
                    }


                }

                callBack.onFailure();
            }

            @Override
            public void onFailure(String errorMsg) {
                callBack.onFailure();
            }
        });


    }


    public interface CallBack {
        void onSuccess(String data);

        void onFailure();

        void onHTMessageSend(HTMessage htMessage);
    }


    public void addMembers(final Map<String, String> members, final String groupId, final CallBack callBack) {

        final HTHttpUtils htHttpUtils = new HTHttpUtils(context);
        htHttpUtils.addMembers(members, groupId, HTPreferenceManager.getInstance().getUser().getUsername(), new HTHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONObject command = jsonObject.getJSONObject("command");
                if (command != null) {
                    JSONArray result = command.getJSONArray("fields");
                    if (result != null) {
                        JSONObject jsonObject1 = result.getJSONObject(0);
                        if (jsonObject1 != null) {
                            int code = jsonObject1.getInteger("value");
                            if (code == 1) {
                                //    deleteGroupLocalOnly(groupId);
                                HTGroup htGroup = getGroup(groupId);
                                if (htGroup != null) {
                                    JSONObject extJson = new JSONObject();
                                    extJson.put("action", 2003);
                                    extJson.put("groupName", htGroup.getGroupName());
                                    extJson.put("groupDescription", htGroup.getGroupDesc());
                                    extJson.put("groupAvatar", htGroup.getImgUrl());
                                    extJson.put("owner", htGroup.getOwner());
                                    JSONArray jsonArray = new JSONArray();
                                    String content = "";
                                    Iterator iter = members.entrySet().iterator();
                                    while (iter.hasNext()) {
                                        Map.Entry entry = (Map.Entry) iter.next();
                                        String key = (String) entry.getKey();
                                        String val = (String) entry.getValue();
                                        JSONObject memberJson = new JSONObject();
                                        memberJson.put("uid", key);
                                        memberJson.put("nickName", val);
                                        jsonArray.add(memberJson);
                                        content += " " + val;
                                    }
                                    extJson.put("members", jsonArray);
                                    sendNoticeMessage(extJson, groupId, content + "加入群聊", callBack);
                                }

//                                JSONObject cmdJsonGroup = new JSONObject();
//                                cmdJsonGroup.put("action", 2008);
//                                cmdJsonGroup.put("data", groupId);
//                                sendCustomMessage(cmdJsonGroup.toJSONString(), ChatType.groupChat, groupId);

                                //   htMessage.setBody();

                                callBack.onSuccess(null);
                                return;
                            }
                        }
                    }
                }

                callBack.onFailure();
            }

            @Override
            public void onFailure(String errorMsg) {
                callBack.onFailure();
            }
        });


    }


    public void addMembersByAdmin(final String userNick, final Map<String, String> members, final String groupId, final CallBack callBack) {

        final HTHttpUtils htHttpUtils = new HTHttpUtils(context);
        htHttpUtils.addMembers(members, groupId, HTPreferenceManager.getInstance().getUser().getUsername(), new HTHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONObject command = jsonObject.getJSONObject("command");
                if (command != null) {
                    JSONArray result = command.getJSONArray("fields");
                    if (result != null) {
                        JSONObject jsonObject1 = result.getJSONObject(0);
                        if (jsonObject1 != null) {
                            int code = jsonObject1.getInteger("value");
                            if (code == 1) {
                                //    deleteGroupLocalOnly(groupId);
                                HTGroup htGroup = getGroup(groupId);
                                if (htGroup != null) {
                                    JSONObject extJson = new JSONObject();
                                    extJson.put("action", 2003);
                                    extJson.put("groupName", htGroup.getGroupName());
                                    extJson.put("groupDescription", htGroup.getGroupDesc());
                                    extJson.put("groupAvatar", htGroup.getImgUrl());
                                    extJson.put("owner", htGroup.getOwner());
                                    JSONArray jsonArray = new JSONArray();
                                    String content = "";
                                    Iterator iter = members.entrySet().iterator();
                                    while (iter.hasNext()) {
                                        Map.Entry entry = (Map.Entry) iter.next();
                                        String key = (String) entry.getKey();
                                        String val = (String) entry.getValue();
                                        JSONObject memberJson = new JSONObject();
                                        memberJson.put("uid", key);
                                        memberJson.put("nickName", val);
                                        jsonArray.add(memberJson);
                                        content += " " + val;
                                    }
                                    extJson.put("members", jsonArray);
                                    sendNoticeMessage(extJson, groupId, userNick + "邀请了:" + content + "加入群聊", callBack);
                                }

//                                JSONObject cmdJsonGroup = new JSONObject();
//                                cmdJsonGroup.put("action", 2008);
//                                cmdJsonGroup.put("data", groupId);
//                                sendCustomMessage(cmdJsonGroup.toJSONString(), ChatType.groupChat, groupId);

                                //   htMessage.setBody();

                                callBack.onSuccess(null);
                                return;
                            }
                        }
                    }
                }

                callBack.onFailure();
            }

            @Override
            public void onFailure(String errorMsg) {
                callBack.onFailure();
            }
        });


    }

    android.os.Handler handler = new android.os.Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1000:

                    getGroupList(true, null);
                    break;

            }
        }
    };

    public void addMembersByNormal(final Map<String, String> members, final String admin, final String groupId, final CallBack callBack) {

        final HTHttpUtils htHttpUtils = new HTHttpUtils(context);
        htHttpUtils.addMembers(members, groupId, admin, new HTHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONObject command = jsonObject.getJSONObject("command");
                if (command != null) {
                    JSONArray result = command.getJSONArray("fields");
                    if (result != null) {
                        JSONObject jsonObject1 = result.getJSONObject(0);
                        if (jsonObject1 != null) {
                            int code = jsonObject1.getInteger("value");
                            if (code == 1) {
                                //    deleteGroupLocalOnly(groupId);
                                HTGroup htGroup = getGroup(groupId);
                                if (htGroup != null) {
                                    JSONObject extJson = new JSONObject();
                                    extJson.put("action", 2003);
                                    extJson.put("groupName", htGroup.getGroupName());
                                    extJson.put("groupDescription", htGroup.getGroupDesc());
                                    extJson.put("groupAvatar", htGroup.getImgUrl());
                                    extJson.put("owner", htGroup.getOwner());
                                    JSONArray jsonArray = new JSONArray();
                                    String content = "";
                                    Iterator iter = members.entrySet().iterator();
                                    while (iter.hasNext()) {
                                        Map.Entry entry = (Map.Entry) iter.next();
                                        String key = (String) entry.getKey();
                                        String val = (String) entry.getValue();
                                        JSONObject memberJson = new JSONObject();
                                        memberJson.put("uid", key);
                                        memberJson.put("nickName", val);
                                        jsonArray.add(memberJson);
                                        content += " " + val;
                                    }
                                    extJson.put("members", jsonArray);
                                    sendNoticeMessage(extJson, groupId, content + "加入群聊", callBack);
                                } else {
                                    //扫码进群的时候会出现本地没有该群信息


                                    handler.sendEmptyMessage(1000);
                                    JSONObject extJson = new JSONObject();
                                    extJson.put("action", 2003);
                                    extJson.put("groupName", "");
                                    extJson.put("groupDescription", "");
                                    extJson.put("groupAvatar", "");
                                    extJson.put("owner", "");
                                    JSONArray jsonArray = new JSONArray();
                                    String content = "";
                                    Iterator iter = members.entrySet().iterator();
                                    while (iter.hasNext()) {
                                        Map.Entry entry = (Map.Entry) iter.next();
                                        String key = (String) entry.getKey();
                                        String val = (String) entry.getValue();
                                        JSONObject memberJson = new JSONObject();
                                        memberJson.put("uid", key);
                                        memberJson.put("nickName", val);
                                        jsonArray.add(memberJson);
                                        content += " " + val;
                                    }
                                    extJson.put("members", jsonArray);
                                    sendNoticeMessage(extJson, groupId, content + "加入群聊", callBack);

                                }


                                //   htMessage.setBody();

                                callBack.onSuccess(null);
                                return;
                            }
                        }
                    }
                }

                callBack.onFailure();
            }

            @Override
            public void onFailure(String errorMsg) {
                callBack.onFailure();
            }
        });


    }


    public void addMembersWithContent(final Map<String, String> members, final String admin, final String groupId, final String msgContent, final CallBack callBack) {

        final HTHttpUtils htHttpUtils = new HTHttpUtils(context);
        htHttpUtils.addMembers(members, groupId, admin, new HTHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONObject command = jsonObject.getJSONObject("command");
                if (command != null) {
                    JSONArray result = command.getJSONArray("fields");
                    if (result != null) {
                        JSONObject jsonObject1 = result.getJSONObject(0);
                        if (jsonObject1 != null) {
                            int code = jsonObject1.getInteger("value");
                            if (code == 1) {
                                //    deleteGroupLocalOnly(groupId);
                                HTGroup htGroup = getGroup(groupId);
                                if (htGroup != null) {
                                    JSONObject extJson = new JSONObject();
                                    extJson.put("action", 2003);
                                    extJson.put("groupName", htGroup.getGroupName());
                                    extJson.put("groupDescription", htGroup.getGroupDesc());
                                    extJson.put("groupAvatar", htGroup.getImgUrl());
                                    extJson.put("owner", htGroup.getOwner());
                                    JSONArray jsonArray = new JSONArray();
                                    String content = "";
                                    Iterator iter = members.entrySet().iterator();
                                    while (iter.hasNext()) {
                                        Map.Entry entry = (Map.Entry) iter.next();
                                        String key = (String) entry.getKey();
                                        String val = (String) entry.getValue();
                                        JSONObject memberJson = new JSONObject();
                                        memberJson.put("uid", key);
                                        memberJson.put("nickName", val);
                                        jsonArray.add(memberJson);
                                        content += " " + val;
                                    }
                                    extJson.put("members", jsonArray);
                                    if (TextUtils.isEmpty(msgContent)) {
                                        sendNoticeMessage(extJson, groupId, content + "加入群聊", callBack);
                                    } else {
                                        sendNoticeMessage(extJson, groupId, msgContent, callBack);
                                    }

                                }

//                                JSONObject cmdJsonGroup = new JSONObject();
//                                cmdJsonGroup.put("action", 2008);
//                                cmdJsonGroup.put("data", groupId);
//                                sendCustomMessage(cmdJsonGroup.toJSONString(), ChatType.groupChat, groupId);

                                //   htMessage.setBody();

                                callBack.onSuccess(null);
                                return;
                            }
                        }
                    }
                }

                callBack.onFailure();
            }

            @Override
            public void onFailure(String errorMsg) {
                callBack.onFailure();
            }
        });


    }


    public void deleteGroup(final String groupId, final CallBack callBack) {
        HTHttpUtils htHttpUtils = new HTHttpUtils(context);
        htHttpUtils.deleteGroup(groupId, HTPreferenceManager.getInstance().getUser().getUsername(), new HTHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONObject command = jsonObject.getJSONObject("command");
                if (command != null) {
                    JSONArray fields = command.getJSONArray("fields");
                    if (fields != null) {
                        JSONObject value = fields.getJSONObject(0);
                        if (value != null) {
                            int code = value.getInteger("value");
                            if (code == 1) {
                                deleteGroupLocalOnly(groupId);
                                callBack.onSuccess(null);
                                return;
                            }
                        }
                    }
                }

                callBack.onFailure();
            }

            @Override
            public void onFailure(String errorMsg) {
                callBack.onFailure();
            }
        });

    }
    public static String filterEmoji(String source,String slipStr) {
        if(containsEmoji(source)){
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr);
        }else{
            return source;
        }
    }
    public static boolean containsEmoji(String value){
        boolean flag = false;
        try {
            Pattern p = Pattern
                    .compile("[^\\u0000-\\uFFFF]");
            Matcher m = p.matcher(value);
            flag = m.find();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public void createGroup(List<String> members, final String creatorNick, final String groupNameTemp, final String groupDesc, final String imgUrl, final CallBack callBack) {

        final String  groupName=filterEmoji(groupNameTemp,"");

        HTHttpUtils htHttpUtils = new HTHttpUtils(context);
        htHttpUtils.creatGroup(members, groupName, groupDesc, imgUrl, HTPreferenceManager.getInstance().getUser().getUsername(), new HTHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                if (jsonObject != null && jsonObject.containsKey("command") && jsonObject.getJSONObject("command").containsKey("fields")
                        && jsonObject.getJSONObject("command").getJSONArray("fields").size() > 0) {
                    JSONObject group = jsonObject.getJSONObject("command").getJSONArray("fields").getJSONObject(0);
                    if (group.containsKey("value") && group.containsKey("var") && group.getString("var").equals("gid")) {
                        String gid = group.getString("value");
                        HTGroup htGroup = new HTGroup();
                        htGroup.setGroupDesc(groupDesc);
                        htGroup.setTime(System.currentTimeMillis());
                        htGroup.setGroupId(gid);
                        htGroup.setImgUrl(imgUrl);
                        htGroup.setGroupName(groupName);
                        htGroup.setOwner(HTPreferenceManager.getInstance().getUser().getUsername());
                        saveGroup(htGroup);

                        JSONObject extJson = new JSONObject();
                        extJson.put("action", 2000);
                        extJson.put("groupName", groupName);
                        extJson.put("groupDescription", groupDesc);
                        extJson.put("groupAvatar", imgUrl);


                        sendNoticeMessage(extJson, gid, creatorNick + "创建了群聊:" + groupName, callBack);
//                       Toast.makeText(getApplicationContext(),"建群成功!",Toast.LENGTH_SHORT).show();
//                       startActivity(new Intent(GroupAddMembersActivity.this,ChatActivity.class).putExtra("userId",gid).putExtra("chatType", MessageUtils.CHAT_GROUP).putExtra("isNewGroup",true));
//                       finish();
                        callBack.onSuccess(gid);
                    }
                } else {
                    callBack.onFailure();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                callBack.onFailure();

            }
        });
    }


    public void createGroup(List<String> members, final String groupNameTemp, final String groupDesc, final String imgUrl, final CallBack callBack) {
        final String  groupName=filterEmoji(groupNameTemp,"");
        HTHttpUtils htHttpUtils = new HTHttpUtils(context);
        htHttpUtils.creatGroup(members, groupName, groupDesc, imgUrl, HTPreferenceManager.getInstance().getUser().getUsername(), new HTHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                if (jsonObject != null && jsonObject.containsKey("command") && jsonObject.getJSONObject("command").containsKey("fields")
                        && jsonObject.getJSONObject("command").getJSONArray("fields").size() > 0) {
                    JSONObject group = jsonObject.getJSONObject("command").getJSONArray("fields").getJSONObject(0);
                    if (group.containsKey("value") && group.containsKey("var") && group.getString("var").equals("gid")) {
                        String gid = group.getString("value");
                        HTGroup htGroup = new HTGroup();
                        htGroup.setGroupDesc(groupDesc);
                        htGroup.setTime(System.currentTimeMillis());
                        htGroup.setGroupId(gid);
                        htGroup.setImgUrl(imgUrl);
                        htGroup.setGroupName(groupName);
                        htGroup.setOwner(HTPreferenceManager.getInstance().getUser().getUsername());
                        saveGroup(htGroup);

                        JSONObject extJson = new JSONObject();
                        extJson.put("action", 2000);
                        extJson.put("groupName", groupName);
                        extJson.put("groupDescription", groupDesc);
                        extJson.put("groupAvatar", imgUrl);


                        sendNoticeMessage(extJson, gid, groupName + "创建成功", callBack);
//                       Toast.makeText(getApplicationContext(),"建群成功!",Toast.LENGTH_SHORT).show();
//                       startActivity(new Intent(GroupAddMembersActivity.this,ChatActivity.class).putExtra("userId",gid).putExtra("chatType", MessageUtils.CHAT_GROUP).putExtra("isNewGroup",true));
//                       finish();
                        callBack.onSuccess(gid);
                    }
                } else {
                    callBack.onFailure();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                callBack.onFailure();

            }
        });
    }


    public void leaveGroup(final String groupId, String userNick, final CallBack callBack) {

        HTHttpUtils htHttpUtils = new HTHttpUtils(context);
        htHttpUtils.leaveGroup(groupId, HTPreferenceManager.getInstance().getUser().getUsername(), userNick, new HTHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONObject command = jsonObject.getJSONObject("command");
                if (command != null) {
                    JSONArray result = command.getJSONArray("fields");
                    if (result != null) {
                        JSONObject jsonObject1 = result.getJSONObject(0);
                        if (jsonObject1 != null) {
                            int code = jsonObject1.getInteger("value");
                            if (code == 1) {
                                deleteGroupLocalOnly(groupId);
                                callBack.onSuccess(groupId);
                                return;
                            }
                        }
                    }
                }

                callBack.onFailure();
            }

            @Override
            public void onFailure(String errorMsg) {
                callBack.onFailure();
            }
        });

    }

    public void updateGroupName(String groupId, String groupName, String nickName, CallBack callBack) {
        HTGroup htGroup = getGroup(groupId);
        if (htGroup == null) {
            Toast.makeText(context, "group not exist", Toast.LENGTH_SHORT).show();
            callBack.onFailure();
            return;
        }
        updateGroupInfo(groupId, groupName, htGroup.getGroupDesc(), htGroup.getImgUrl(), null, nickName, callBack);
    }

    public void updateGroupDesc(String groupId, String groupDesc, String nickName, CallBack callBack) {
        HTGroup htGroup = getGroup(groupId);
        if (htGroup == null) {
            Toast.makeText(context, "group not exist", Toast.LENGTH_SHORT).show();
            callBack.onFailure();
            return;
        }
        updateGroupInfo(groupId, htGroup.getGroupName(), groupDesc, htGroup.getImgUrl(), null, nickName, callBack);
    }

    public void updateGroupImgUrlRemote(String groupId, String imgUrl, String nickName, CallBack callBack) {
        HTGroup htGroup = getGroup(groupId);
        if (htGroup == null) {
            Toast.makeText(context, "group not exist", Toast.LENGTH_SHORT).show();
            callBack.onFailure();
            return;
        }
        updateGroupInfo(groupId, htGroup.getGroupName(), htGroup.getGroupDesc(), imgUrl, null, nickName, callBack);
    }

    public void updateGroupImgUrlLocal(String groupId, String filePath, String nickName, final CallBack callBack) {


        HTGroup htGroup = getGroup(groupId);
        if (htGroup == null) {
            Toast.makeText(context, "group not exist", Toast.LENGTH_SHORT).show();
            callBack.onFailure();
            return;
        }


        updateGroupInfo(groupId, htGroup.getGroupName(), htGroup.getGroupDesc(), null, filePath, nickName, callBack);
    }

    private void updateGroupInfo(final String groupId, final String groupNameTemp, String groupDesc, String imgUrl, String filePath, final String nickName, final CallBack callBack) {
        if (TextUtils.isEmpty(groupId)) {
            callBack.onFailure();
            Toast.makeText(context, "groupId can't be null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(groupNameTemp)) {
            callBack.onFailure();
            Toast.makeText(context, "groupName can't be null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (groupDesc == null) {
            groupDesc = "";
        }
         final String  groupName=filterEmoji(groupNameTemp,"");

        final String finalGroupDesc = groupDesc;
        if (!TextUtils.isEmpty(filePath)) {
            final String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            new UploadFileUtils(context, fileName, filePath).uploadFile(new UploadFileUtils.UploadCallBack() {

                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    Log.d("UploadFileUtils", "onProgress");
                }

                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    Log.d("UploadFileUtils", "onSuccess");
                    updateGroupInfoInteral(groupId, groupName, finalGroupDesc, baseOssUrl + fileName, nickName, callBack);
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    Log.d("UploadFileUtils", "onFailure");
                    callBack.onFailure();
                }
            });

        } else {
            updateGroupInfoInteral(groupId, groupName, finalGroupDesc, imgUrl, nickName, callBack);

        }

    }

//    private Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//
//
//            updateGroupInfoInteral(groupId, groupName, finalGroupDesc, SDKConstant.baseOssUrl + fileName, nickName, callBack);
//
//        }
//    };


    private void updateGroupInfoInteral(final String groupId, final String groupName, final String groupDesc, final String imageUrl, final String nickName, final CallBack callBack) {

        HTHttpUtils htHttpUtils = new HTHttpUtils(context);
        htHttpUtils.updateGroupInfo(groupId, HTPreferenceManager.getInstance().getUser().getUsername(), groupName, groupDesc, imageUrl, new HTHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONObject command = jsonObject.getJSONObject("command");
                if (command != null) {
                    JSONArray result = command.getJSONArray("fields");
                    if (result != null) {
                        JSONObject jsonObject1 = result.getJSONObject(0);
                        if (jsonObject1 != null) {
                            int code = jsonObject1.getInteger("value");
                            if (code == 1) {
                                HTGroup htGroup = allGroups.get(groupId);
                                if (htGroup != null) {
                                    htGroup.setGroupName(groupName);
                                    htGroup.setGroupDesc(groupDesc);
                                    htGroup.setImgUrl(imageUrl);
                                    saveGroup(htGroup);
                                    JSONObject extJson = new JSONObject();
                                    extJson.put("action", 2001);
                                    extJson.put("groupName", groupName);
                                    extJson.put("groupDescription", groupDesc);
                                    extJson.put("groupAvatar", imageUrl);
                                    extJson.put("uid", HTPreferenceManager.getInstance().getUser().getUsername());
                                    extJson.put("nickName", nickName);
                                    sendNoticeMessage(extJson, groupId, "群资料已更新", callBack);
//                                  context.startService(new Intent(context, MessageService2.class).putExtra("TPYE",MessageService2.TYPE_CHAT))

                                }

                                callBack.onSuccess(groupId);
                                return;
                            }
                        }
                    }
                }

                callBack.onFailure();
            }

            @Override
            public void onFailure(String errorMsg) {
                callBack.onFailure();
            }
        });
    }

//    private void sendNoticeMessage(JSONObject extJson, String groupId, String content){
//         final HTMessage htMessage=new HTMessage();
//        String msgId= UUID.randomUUID().toString();
//        htMessage.setAttributes(extJson);
//        JSONObject textJson=new JSONObject();
//        textJson.put("content",content);
//        htMessage.setBody(new HTMessageBody(textJson));
//        htMessage.setStatus(HTMessage.Status.SUCCESS);
//        htMessage.setTime(System.currentTimeMillis());
//        htMessage.setTo(groupId);
//        htMessage.setMsgId(msgId);
//        htMessage.setChatType(ChatType.groupChat);
//        htMessage.setType(HTMessage.Type.TEXT);
//        htMessage.setLocalTime(System.currentTimeMillis());
//        htMessage.setDirect(HTMessage.Direct.SEND);
//        htMessage.setFrom(HTPreferenceManager.getInstance().getUser().getUsername());
//      //  HTMessageHelper.sendHTMessage(htMessage,context);
//
//       HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
//           @Override
//           public void onProgress() {
//
//           }
//
//           @Override
//           public void onSuccess(long timeStamp) {
//               htMessage.setTime(timeStamp);
////               HTClient.getInstance().messageManager().saveMessage(htMessage,false);
////                       List<Param> params = new ArrayList<>();
////                       String chatType = "1";
////                       if (htMessage.getChatType() == ChatType.groupChat) {
////                           chatType = "2";
////                           params.add(new Param("mid", htMessage.getMsgId()));
////                       }
////                       params.add(new Param("fromId", htMessage.getFrom()));
////                       params.add(new Param("toId", htMessage.getTo()));
////                       params.add(new Param("chattype", chatType));
////                       params.add(new Param("timeStamp", String.valueOf(htMessage.getTime())));
////                       String msgString = Base64.encode(htMessage.toXmppMessageBody());
////                       params.add(new Param("message", msgString));
////                       new NewOkHttpUtils().post(params, SDKConstant.HOST_API, new NewOkHttpUtils.HttpCallBack() {
////                           @Override
////                           public void onResponse(JSONObject jsonObject) {
////                              Log.d(" 上传聊天记录jsonObject:",jsonObject.toJSONString());
////                           }
////
////                           @Override
////                           public void onFailure(String errorMsg) {
////
////                           }
////                       });
//////
//
//
//           }
//
//           @Override
//           public void onFailure() {
//
//           }
//       });
//    }

    private void sendNoticeMessage(JSONObject extJson, String groupId, String content, final CallBack callBack) {
        final HTMessage htMessage = new HTMessage();
        String msgId = UUID.randomUUID().toString();
        htMessage.setAttributes(extJson);
        JSONObject textJson = new JSONObject();
        textJson.put("content", content);
        htMessage.setBody(new HTMessageBody(textJson));
        htMessage.setStatus(HTMessage.Status.SUCCESS);
        htMessage.setTime(System.currentTimeMillis());
        htMessage.setTo(groupId);
        htMessage.setMsgId(msgId);
        htMessage.setChatType(ChatType.groupChat);
        htMessage.setType(HTMessage.Type.TEXT);
        htMessage.setLocalTime(System.currentTimeMillis());
        htMessage.setDirect(HTMessage.Direct.SEND);
        htMessage.setFrom(HTPreferenceManager.getInstance().getUser().getUsername());
        //  HTMessageHelper.sendHTMessage(htMessage,context);

        HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess(long timeStamp) {
                htMessage.setTime(timeStamp);
                if (callBack != null) {
                    callBack.onHTMessageSend(htMessage);
                }
//               HTClient.getInstance().messageManager().saveMessage(htMessage,false);
//                       List<Param> params = new ArrayList<>();
//                       String chatType = "1";
//                       if (htMessage.getChatType() == ChatType.groupChat) {
//                           chatType = "2";
//                           params.add(new Param("mid", htMessage.getMsgId()));
//                       }
//                       params.add(new Param("fromId", htMessage.getFrom()));
//                       params.add(new Param("toId", htMessage.getTo()));
//                       params.add(new Param("chattype", chatType));
//                       params.add(new Param("timeStamp", String.valueOf(htMessage.getTime())));
//                       String msgString = Base64.encode(htMessage.toXmppMessageBody());
//                       params.add(new Param("message", msgString));
//                       new NewOkHttpUtils().post(params, SDKConstant.HOST_API, new NewOkHttpUtils.HttpCallBack() {
//                           @Override
//                           public void onResponse(JSONObject jsonObject) {
//                              Log.d(" 上传聊天记录jsonObject:",jsonObject.toJSONString());
//                           }
//
//                           @Override
//                           public void onFailure(String errorMsg) {
//
//                           }
//                       });
////


            }

            @Override
            public void onFailure() {

            }
        });
    }


    public interface GroupOperationCallBack {
        void callBack(HTMessage htMessage);
    }

    ;

    private void sendCustomMessage(String body, ChatType chatType, String chatTo) {
        CmdMessage customMessage = new CmdMessage();
        customMessage.setBody(body);
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setFrom(HTPreferenceManager.getInstance().getUser().getUsername());
        customMessage.setTo(chatTo);
        customMessage.setChatType(chatType);
//       HTMessageHelper.sendCustomMessage(customMessage,context);
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


}
