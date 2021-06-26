package com.htmessage.yichat.acitivity.chat.group;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.HTApp;
 import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageTextBody;

/**
 * Created by huangfangyi on 2017/2/5.
 * qq 84543217
 */

public class GroupNoticeMessageUtils {
//    创建群：             	群普通消息  message.ext = {"action":"2000","groupName":"群名称","groupDescription":"群描述","groupAvatar":"群头像"}
//               			message.body.content ="\"某某群\"创建成功"
//    更新群名称：			群普通消息  message.ext = {"action":"2001","groupName":"群名称","groupAvatar":"群头像","groupDescription":"群描述","uid":"用户id","nickName":"用户昵称"}  									UI展示为：群主自己 -> "你修改了群信息"  群成员 -> ""某人"修改群信息"
//    往群里加人：          群普通消息  message.ext = {"action":"2003","groupName":"群名称","groupDescription":"群描述","groupAvatar":"群头像","members":[{"uid":"第一个用户id","nickName":"第一个用户昵称"},{"uid":"第二个用户id","nickName":"第二个用户昵称"}]}
//    从群里移除群成员：	群普通消息  message.ext = {"action":"2004","uid":"被移除的群成员id","nickName":"被移除的群成员昵称"}
//    个人透传              message.body = {"action":"2004":"data":"当前群id"}
    private final static String ACTION="action";
    private final static String GROUP_NAME="groupName";
    private final static String GROUP_DESCRIPTION="groupDescription";
    private final static String GROUP_AVATAR="groupAvatar";
    private final static String UID="uid";
    private final static String NICK_NAME="nickName";
    private final static String MEMBERS="members";


    public static  String getGroupNoticeContent(HTMessage   htMessage){
        String content="";
         //附加字段json
        int action=htMessage.getIntAttribute(ACTION,0);
        if(action!=0){
            String groupName=htMessage.getStringAttribute("groupName");
            String groupDescription=htMessage.getStringAttribute("groupDescription");
            String groupAvatar=htMessage.getStringAttribute("groupAvatar");
            String uid=htMessage.getStringAttribute("uid");
            String nickName=htMessage.getStringAttribute("nickName");
            JSONArray members=htMessage.getJSONArrayAtrribute("members");
//            if(attribute.containsKey(GROUP_NAME)){
//                groupName=attribute.getString(GROUP_NAME);
//            }
//            String uid="";
//            if(attribute.containsKey(UID)){
//                uid=attribute.getString(UID);
//            }
//            String nickName="";
//            if(attribute.containsKey(NICK_NAME)){
//                nickName=attribute.getString(NICK_NAME);
//            }
//            JSONArray members=new JSONArray();
//            if(attribute.containsKey(MEMBERS)){
//               members=attribute.getJSONArray(MEMBERS);
//            }
            switch (action){
                case 2000:
                    content="群聊 "+"\""+groupName+"\""+" 创建成功";
                    break;
                case 2001:
                    if(HTApp.getInstance().getUsername().equals(uid)){
                        content="你 修改了群资料";
                    }else{
                        content="\""+nickName+"\""+" 修改了群资料";
                    }
                    break;
                case 2003:
                HTMessageTextBody body= (HTMessageTextBody) htMessage.getBody();
                    content= body.getContent();
//                    String membersList="";
//                    if(members!=null){
//                        for(int i=0;i<members.size();i++){
//                            JSONObject jsonObject=members.getJSONObject(i);
//                            if(i==0){
//                                membersList+="\""+jsonObject.getString(NICK_NAME);
//                            }else {
//                                membersList+="、"+jsonObject.getString(NICK_NAME);
//                            }
//                        }
//
//                    }
//
//                    content=membersList+"\""+" 加入了群聊";
                    break;
                case 2004:
                    content="\""+nickName+"\""+" 被移除群聊";
                    break;

                case 6001:
                    String opId = htMessage.getStringAttribute("opId");
                    if (TextUtils.isEmpty(opId)) {
                        String userNick = htMessage.getStringAttribute("nick");
                        content = userNick + "撤回了一条消息";
                    } else {
                        String msgFrom = htMessage.getFrom();
                        if (opId.equals(msgFrom)) {
                            //如果操作者和消息发出者是同一个人
                            if (opId.equals(UserManager.get().getMyUserId())) {
                                content = "你撤回了一条消息";
                            } else {
                                content = UserManager.get().getUserNick(opId) + "撤回了一条消息";
                            }
                        } else {
                            //操作者和发出者不是同一个人，只能说明是管理员撤回了别人的消息
                            if (opId.equals(UserManager.get().getMyUserId())) {
                                content = "你撤回了" + UserManager.get().getUserNick(msgFrom) + "消息";
                            } else {
                                if (msgFrom.equals(UserManager.get().getMyUserId())) {
                                    content = "管理员撤回了你的消息";
                                } else {
                                    content = "管理员撤回了" + UserManager.get().getUserNick(msgFrom) + "的消息";

                                }
                            }

                        }

                    }
                    break;
            }
         }
        return content;
    }
}
