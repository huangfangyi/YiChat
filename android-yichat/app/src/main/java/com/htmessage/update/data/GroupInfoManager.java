package com.htmessage.update.data;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.utils.LoggerUtils;
import com.htmessage.sdk.manager.MmvkManger;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by huangfangyi on 2019/7/31.
 * qq 84543217
 */
public class GroupInfoManager {
    private static GroupInfoManager groupInfoManager;
    private static Map<String, Boolean> groupInfoLoadedMap = new HashMap<>();


    public static GroupInfoManager getInstance() {
        if (groupInfoManager == null) {
            groupInfoManager = new GroupInfoManager();

        }
        return groupInfoManager;

    }


    public void initClear() {
        //清空之后，群聊天界面进入之后，重新初始化
        groupInfoLoadedMap.clear();
    }

    public void hasGroupInfoLoaded(String groupId) {
        LoggerUtils.d("hasGroupInfoLoaded");
        groupInfoLoadedMap.put(groupId, true);
    }

    public boolean isGroupInfoLoaded(String groupId) {

        if (groupInfoLoadedMap.containsKey(groupId) && (boolean) groupInfoLoadedMap.get(groupId)) {
            return true;
        }

        return false;
    }


    public void saveGroupInfoTemp(JSONObject groupInfo) {
        String groupId = groupInfo.getString("groupId");

        saveGroupManager(groupId, groupInfo.getJSONArray("adminList"));

        String silent = groupInfo.getString("groupSilentStatus");
        if ("1".equals(silent)) {
            setGroupSilent(groupId, true,false);
        } else {
            setGroupSilent(groupId, false,false);
        }
        setsilentUsers(groupId, groupInfo.getJSONArray("silentList"));
        int roleType = groupInfo.getInteger("roleType");
        setGroupRole(groupId, roleType);
    }


    public void saveGroupManager(String groupId, JSONArray jsonArray) {
        MmvkManger.getIntance().putJSONArray(groupId + "_managers", jsonArray);

    }

    public JSONArray getGroupManagers(String groupId) {
        JSONArray jsonArray = MmvkManger.getIntance().getJSONArray(groupId + "_managers");
        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }
        return jsonArray;
    }


    public boolean userIsManager(String userId,String groupId){
        JSONArray  jsonArray=getGroupManagers(groupId);
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            if(jsonObject.getString("userId").equals(userId)){
                return true;
            }
        }

        return false;
    }

    public void setsilentUsers(String groupId, JSONArray jsonArray) {


        MmvkManger.getIntance().putJSONArray(groupId + "_silentUser", jsonArray);
        if(jsonArray!=null){
            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                if(jsonObject.getString("userId").equals(UserManager.get().getMyUserId())){
                   setGroupSilent(groupId,true,true);
                    break;
                }
            }
        }

    }

    public JSONArray getsilentUsers(String groupId) {

        JSONArray jsonArray = MmvkManger.getIntance().getJSONArray(groupId + "_silentUser");
        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }
        return jsonArray;
    }

    public  void  deleteFromSilentUsersLocal(String groupId,String  userId){

        JSONArray jsonArray = MmvkManger.getIntance().getJSONArray(groupId + "_silentUser");
        if (jsonArray == null) {
            return;
        }
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            if(jsonObject.getString("userId").equals(userId)){
                jsonArray.remove(i);
                break;
            }
        }
        MmvkManger.getIntance().putJSONArray(groupId + "_silentUser", jsonArray);
    }



    private   void  addSilentUsersLocal(String groupId,String  userId){

        JSONArray jsonArray = MmvkManger.getIntance().getJSONArray(groupId + "_silentUser");
        if (jsonArray == null) {
            jsonArray=new JSONArray();
        }
        boolean contains=false;
        for(int i=0;i<jsonArray.size();i++){
             JSONObject jsonObject=jsonArray.getJSONObject(i);
            if(jsonObject.getString("userId").equals(userId)){
                contains=true;
                break;
            }

        }
        if(!contains){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("userId",userId);
            if(userId.equals(UserManager.get().getMyUserId())){
                jsonObject.put("nick", UserManager.get().getMyNick());
                jsonObject.put("avatar", UserManager.get().getMyAvatar());
            }else {
                jsonObject.put("nick", UserManager.get().getUserNick(userId));
                jsonObject.put("avatar", UserManager.get().getUserAvatar(userId));
            }

            jsonArray.add(jsonObject);
        }


        MmvkManger.getIntance().putJSONArray(groupId + "_silentUser", jsonArray);
    }



    public  void addSilentUsers(String groupId, String  userId){
         JSONArray jsonArray=getGroupManagers(groupId);
         for (int i=0;i<jsonArray.size();i++){
             JSONObject jsonObject=jsonArray.getJSONObject(i);
             if(jsonObject.getString("userId").equals(userId)){
                 return;
             }
         }
         HTGroup htGroup=HTClient.getInstance().groupManager().getGroup(groupId);
         if(htGroup!=null&&htGroup.getOwner().equals(userId)){
             return;
         }

         JSONObject body=new JSONObject();
         body.put("groupId",groupId);
         body.put("userId",userId);
         body.put("status",1);
         ApiUtis.getInstance().postJSON(body, Constant.URL_GROUP_SILENT_MEMBER, new ApiUtis.HttpCallBack() {
             @Override
             public void onResponse(JSONObject jsonObject) {
                 String code=jsonObject.getString("code");
                 if("0".equals(code)){
                     addSilentUsersLocal(groupId,userId);
                 }
             }

             @Override
             public void onFailure(int errorCode) {

             }
         });

        JSONObject data=new JSONObject();
        data.put("action",30004);
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


    public boolean isGroupSilent(String groupId) {
        boolean isSilent=MmvkManger.getIntance().getBoolean(groupId + "_GroupSilent", false);
        boolean isSilent_Single=MmvkManger.getIntance().getBoolean(groupId + "_GroupSilent_Single", false);

        boolean result=isSilent||isSilent_Single;

        return result ;

    }

    public void setGroupSilent(String groupId, boolean silent,boolean isSingle) {
        if(!isSingle){
            MmvkManger.getIntance().putBoolean(groupId + "_GroupSilent", silent);
        }else {
            MmvkManger.getIntance().putBoolean(groupId + "_GroupSilent_Single", silent);

        }


        //  groupSilentMap.put(groupId, silent);
    }

    public void setGroupRole(String groupId, int roleType) {
        MmvkManger.getIntance().putInt(groupId + "_GroupRole", roleType);

        // groupRoleMap.put(groupId, roleType);
    }

    public boolean isManager(String groupId) {
        int roleType = MmvkManger.getIntance().getInt(groupId + "_GroupRole", 0);

        if (roleType > 0) {
            return true;
        }

        HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(groupId);
        if (htGroup != null && UserManager.get().getMyUserId().equals(htGroup.getOwner())) {
            return true;

        }
        return false;
    }


    public void refreshManagerInserver(String groupId,CallBack callBack) {

        JSONObject data = new JSONObject();
        data.put("groupId", groupId);

        ApiUtis.getInstance().postJSON(data, Constant.URL_GROUP_MANAGERS, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    if (data != null) {
                        saveGroupManager(groupId, data);
                        if(callBack!=null){
                            callBack.onDataSuccess(data);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });


    }

   public interface CallBack{
        void onDataSuccess(JSONArray jsonArray);

    }


    public void getGroupAllMembersFromServer(final String groupId,CallBack callBack) {

        JSONObject data = new JSONObject();
        data.put("groupId", groupId);
        data.put("pageNo", 1);
        data.put("pageSize", 10000);
        ApiUtis.getInstance().postJSON(data, Constant.URL_GROUP_MEMBERS, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray != null) {
                        if(callBack!=null){
                            callBack.onDataSuccess(jsonArray);
                        }
                        MmvkManger.getIntance().putJSONArray(groupId + "_allMembers", jsonArray);
                        Set<String> memberUserIds = new HashSet<>();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject userInfo = jsonArray.getJSONObject(i);
                            UserManager.get().saveUserNickAvatar(userInfo.getString("userId")
                                    , userInfo.getString("nick"), userInfo.getString("avatar"));
                            memberUserIds.add(userInfo.getString("userId"));

                        }
                        MmvkManger.getIntance().putStringSet(groupId + "_allMembers_userId", memberUserIds);
                    }

                }
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });


    }

    public JSONArray getGroupAllMembersFromLocal(String groupId) {
        JSONArray jsonArray = MmvkManger.getIntance().getJSONArray(groupId + "_allMembers");
        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }
        return jsonArray;
    }


    public Set<String> getAllMemberUserIdFromLocal(String groupId) {
        Set<String> stringSet = MmvkManger.getIntance().getStringSet(groupId + "_allMembers_userId");
        if (stringSet == null) {
            stringSet = new HashSet<>();
        }
        return stringSet;
    }

    public void removMember(String groupId, String membersId) {
        JSONArray jsonArray = getGroupAllMembersFromLocal(groupId);
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (membersId.equals(jsonObject.getString("userId"))) {
                    jsonArray.remove(i);
                    break;
                }
            }
            MmvkManger.getIntance().putJSONArray(groupId + "_allMembers", jsonArray);

        }

        Set<String> allUserIds = getAllMemberUserIdFromLocal(groupId);
        allUserIds.remove(membersId);
        MmvkManger.getIntance().putStringSet(groupId + "_allMembers_userId", allUserIds);

    }

    public void removManager(String groupId, String userId) {
        JSONArray jsonArray = getGroupManagers(groupId);
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (userId.equals(jsonObject.getString("userId"))) {
                    jsonArray.remove(i);
                    break;
                }
            }
            saveGroupManager(groupId, jsonArray);
            if (UserManager.get().getMyUserId().equals(userId)) {
                setGroupRole(groupId, 0);
            }
        }

    }

    public void addManager(String groupId, String userId) {
        JSONObject userInfo = new JSONObject();
        userInfo.put("userId", userId);
        userInfo.put("nick", UserManager.get().getUserNick(userId));
        userInfo.put("avatar", UserManager.get().getUserAvatar(userId));
        JSONArray jsonArray = getGroupManagers(groupId);
        if (jsonArray == null) {
            jsonArray = new JSONArray();

        }
        if(!jsonArray.toJSONString().contains(userId)){
            jsonArray.add(userInfo);
        }

        if (UserManager.get().getMyUserId().equals(userId)) {
            setGroupRole(groupId, 1);
        }
        saveGroupManager(groupId, jsonArray);

    }

    public void addMembers(String groupId, List<String> addMembers) {
        if (addMembers == null) {
            return;
        }
        JSONArray jsonArray = getGroupAllMembersFromLocal(groupId);
        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }
        for (String userId : addMembers) {
            String nick = UserManager.get().getUserNick(userId);
            String avatar = UserManager.get().getUserAvatar(userId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            jsonObject.put("nick", nick);
            jsonObject.put("avatar", avatar);
            jsonArray.add(jsonObject);
        }

        MmvkManger.getIntance().putJSONArray(groupId + "_allMembers", jsonArray);
        Set<String> allUserIds = getAllMemberUserIdFromLocal(groupId);
        if (allUserIds == null) {
            return;
        }
        allUserIds.addAll(new HashSet<>(addMembers));

        MmvkManger.getIntance().putStringSet(groupId + "_allMembers_userId", allUserIds);

    }

    public void setAtTag(String groupId,boolean isAt){
        MmvkManger.getIntance().putBoolean(groupId + "_atMsg", isAt);
    }

    public boolean getAtTag(String groupId){
        return    MmvkManger.getIntance().getBoolean(groupId + "_atMsg", false);
    }

}
