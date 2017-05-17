package com.fanxin.huangfangyi.main;

import android.os.Environment;

/**
 * Created by ustc on 2016/6/27.
 */
public class FXConstant {
    //服务器端
    public static final String HOST = "http://120.24.211.126/fanxin3/";
    public static final String URL_REGISTER = HOST + "register.php";
    public static final String URL_LOGIN = HOST + "login.php";
    public static final String URL_FriendList = HOST + "getMyFriends.php";
    public static final String URL_AVATAR= HOST + "upload/";
    public static final String URL_Search_User = HOST + "search_friends.php";
    public static final String URL_Get_UserInfo = HOST + "get_userinfo.php";
    public static final String URL_UPDATE_Groupnanme = HOST + "update_groupname.php";
    public static final String URL_UPDATE = HOST + "update.php";
    public static final String URL_ADD_FRIEND=HOST + "accept_friend.php";
    public static final String URL_DELETE_FRIEND=HOST + "deleteFriend.php";
    //朋友圈接口
    // 服务器端
    public static final String URL_PUBLISH = HOST + "publish.php";
    public static final String URL_SOCIAL = HOST + "social.php";
    public static final String URL_SOCIAL_PHOTO = HOST + "upload/";
    public static final String URL_SOCIAL_COMMENT = HOST + "comment.php";
    public static final String URL_SOCIAL_GOOD = HOST + "social_good.php";
    public static final String URL_SOCIAL_GOOD_CANCEL = HOST + "social_good_cancel.php";
    public static final String URL_SOCIAL_DELETE_COMMENT = HOST + "social_comment_delete.php";
    public static final String URL_SOCIAL_DELETE = HOST + "social_delete.php";
    public static final String URL_SOCIAL_FRIEND = HOST + "social_friend.php";
    public static final String URL_GROUP_MEMBERS = HOST + "groupMembers.php";
    public static final String URL_GROUP_ADD_MEMBERS = HOST + "addMembers.php";
    public static final String URL_GROUP_CREATE = HOST + "groupCreate.php";

    public static final String JSON_KEY_NICK ="nick";
    public static final String JSON_KEY_HXID ="hxid";
    public static final String JSON_KEY_FXID ="fxid";
    public static final String JSON_KEY_SEX ="sex";
    public static final String JSON_KEY_AVATAR ="avatar";
    public static final String JSON_KEY_CITY ="city";
    public static final String JSON_KEY_PROVINCE ="province";
    public static final String JSON_KEY_TEL ="tel";
    public static final String JSON_KEY_SIGN ="sign";


    //消息撤回
    public static final String FX_REVOKE_MESSAGE = "FX_REVOKE_MESSAGE";
    public static final String REVOKE_MESSAGE_ID = "REVOKE_MESSAGE_ID";
    public static final String IS_MESSAGE_REVOKE="IS_REVOKE_MESSAGE";
    public static final String IS_MESSAGE_REVOKE_SEND="IS_MESSAGE_REVOKE_SEND";

    public static final String DIR_AVATAR = Environment.getExternalStorageDirectory().toString()+"/fanxin/";
    //进入用户详情页传递json字符串
    public static final String KEY_USER_INFO="userInfo";


    //添加好友通知
    public static final String CMD_ADD_FRIEND="ADD_FRIEND";
    public static final String CMD_AGREE_FRIEND="AGREE_FRIEND";
    public static final String CMD_REFUSE_FRIEND="REFUSE_FRIEND";
    public static final String CMD_ADD_REASON="ADD_REASON";
    //删除好友通知
    public static final String CMD_DELETE_FRIEND="DELETE_FRIEND";
    public static final String FXLIVE_CHATROOM_ID="218352836658856384";


    public static final String RTEM_URL="rtmp://live.hkstv.hk.lxdns.com/live/hks";
    public static final String RTEM_URL_LIVE= "rtmp://publish3.cdn.ucloud.com.cn/ucloud/";
    //云账户红包群id
    public static final String REDPACKET_GROUP_ID="230584771716055476";



}
