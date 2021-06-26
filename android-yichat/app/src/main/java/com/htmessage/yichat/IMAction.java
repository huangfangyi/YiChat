package com.htmessage.yichat;

/**
 * Created by huangfangyi on 2017/2/10.
 * qq 84543217
 */

public class IMAction {
    public static final String ACTION_INVITE_MESSAGE = "action_invite_message";
    public static final String ACTION_NEW_MESSAGE = "action_new_message";
    public static final String ACTION_REMOVED_FROM_GROUP = "action_removed_from_group";
     public static final String ACTION_CONFLICT = "action_conflict";
    public static final String ACTION_CONNECTION_CHANAGED = "action_connection_changed";
    //消息撤回
    public static final String ACTION_MESSAGE_WITHDROW = "action_message_withdrow";
    //转发消息
    public static final String ACTION_MESSAGE_FORWORD = "action_message_forword";
    //清空消息
    public static final String ACTION_MESSAGE_EMPTY = "ACTION_MESSAGE_EMPTY";
    //删除好友通知
    public static final String CMD_DELETE_FRIEND = "DELETE_FRIEND";
    //本地删除好友
    public static final String DELETE_FRIEND_LOCAL = "DELETE_FRIEND_LOCAL";
    //资料更新的通知
    public static final String ACTION_UPDATE_INFO = "ACTION_UPDATE_INFO";
   /* //刷新所有列表
    public static final String ACTION_REFRESH_ALL_LIST = "ACTION_REFRESH_ALL_LIST";*/
    //解除禁言
    public static final String ACTION_HAS_CANCLED_NO_TALK = "ACTION_HAS_CANCLED_NO_TALK";
    //被禁言
    public static final String ACTION_HAS_NO_TALK = "ACTION_HAS_NO_TALK";

//    //    备注好友
//    public static final String ACTION_REMARK_FRIEND = "ACTION_REMARK_FRIEND";
    //离开或者退出删除群组
    public static final String ACTION_DELETE_GROUP = "ACTION_DELETE_GROUP";

     //    微信登录的Action
    public static final String LOGIN_BY_WECHAT_RESULT = "LOGIN_BY_WECHAT_RESULT";
    //删除了好友刷新通知
//    public static final String REFRESH_CONTACTS_LIST = "REFRESH_CONTACTS_LIST";
    //群主对群设置或者取消了管理员
    public static final String ACTION_SET_OR_CANCLE_GROUP_MANAGER = "ACTION_SET_OR_CANCLE_GROUP_MANAGER";
    //更新群资料
    public static final String ACTION_UPDATE_CHAT_TITLE = "ACTION_UPDATE_CHAT_TITLE";
    //添加好友的通知已经发出去
    public static final String ACTION_ADD_FIREND_SENDEND = "ACTION_ADD_FIREND_SENDEND";
    //设置支付密码成功
    public static final String SET_PAY_PWD_SUCCESS = "SET_PAY_PWD_SUCCESS";
    //红包已领取
    public static final String RP_IS_HAS_OPEND = "RP_IS_HAS_OPEND";
    // 二维码已付款
    public static final String QRCODE_IS_PAYED = "QRCODE_IS_PAYED";
    //    微信,支付宝支付的Action
    public static final String PAY_BY_WECHAT_RESULT = "PAY_BY_WECHAT_RESULT";
    public static final String VERSION_UPDATE = "VERSION_UPDATE";

    public static final String RED_PACKET_HAS_GOT = "RED_PACKET_HAS_GOT";
    //群发布新公告
    public static final String NEW_GROUP_NOTICE = "NEW_GROUP_NOTICE";
    //好友设置了备注
    public static final String USER_REMARK= "USER_REMARK";

    //好友设置了备注
    public static final String XMPP_LOGIN_OR_RELOGIN= "XMPP_LOGIN_OR_RELOGIN";

    public static final String NO_TALK_USER = "NO_TALK_USER";
    public static final String NO_TALK_USER_CANCEL = "NO_TALK_USER_CANCEL";
    public static final String REFRESH_LOCAL_MESSGAE = "REFRESH_LOCAL_MESSGAE";
}
