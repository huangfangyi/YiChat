package com.htmessage.sdk.client;

/**
 * Created by huangfangyi on 2017/2/13.
 * qq 84543217
 */

public class HTAction {
    //连接状态的广播通知
    public static String ACTION_CONNECTION="action_connection";
    //登录IM服务器的通知
    public static String ACTION_LOGIN="action_login";
    //退出登录的通知
    public static String ACTION_LOGOUT="action_logout";
    //收到一般消息
    public static String ACTION_MESSAGE="action_message";
    //收到透传消息
    public static String ACTION_MESSAGE_CMD="action_message_cmd";
    //收到音视频通话消息
    public static String ACTION_MESSAGE_CALL="action_message_call";
    //发送消息的结果通知
    public static String ACTION_RESULT_MESSAGE="action_result_messge";
    public static String ACTION_RESULT_MESSAGE_CMD="action_result_messge_cmd";
    //注册的通知回调
    public static String ACTION_REGISTER="action_register";
    //群列表加载完成的通知回调
    public static String ACTION_GROUPLIST_LOADED ="action_grouplist_loaded";
    //群被删除的通知回调
    public static String ACTION_GROUP_DELETED ="action_grouplist_deleted";
    //某群成员退出群的通知回调
    public static String ACTION_GROUP_LEAVED ="action_grouplist_leaved";
    //


}
