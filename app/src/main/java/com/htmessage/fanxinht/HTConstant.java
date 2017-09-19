package com.htmessage.fanxinht;

import android.os.Environment;

/**
 * Created by ustc on 2016/6/27.
 */
public class HTConstant {
    //服务器端
    public static final String HOST = "http://xxxxxxx/api/";
    public static final String URL_AVATAR = HOST + "upload/";
    public static final String URL_REGISTER = HOST + "register";//注册
    public static final String URL_LOGIN = HOST + "login";//登录
    public static final String URL_THIRDLOGIN = HOST + "thirdLogin";//第三方登录
    public static final String URL_FriendList = HOST + "fetchFriends";//获取好友列表
    public static final String URL_Search_User = HOST + "searchUser";//查询好友
    public static final String URL_Get_UserInfo = HOST + "getUserInfo";//获取详情
    public static final String URL_UPDATE = HOST + "update";//更新
    public static final String URL_RESETPASSWORD = HOST + "resetPassword";//更新密码
    public static final String URL_ADD_FRIEND = HOST + "addFriend"; //添加好友
    public static final String URL_DELETE_FRIEND = HOST + "removeFriend";//删除好友
    public static final String URL_ADD_BLACKLIST = HOST + "addBlackList";//添加黑名单
    //    朋友圈接口
//     服务器端
    public static final String URL_PUBLISH = HOST + "publish";//发布动态
    public static final String URL_SOCIAL = HOST + "fetchTimeline";//获取动态列表
    public static final String URL_SOCIAL_DELETE = HOST + "removeTimeline";//删除动态
    public static final String URL_SOCIAL_FRIEND = HOST + "fetchOtherTimeline";//获取好友朋友圈列表
    public static final String URL_SOCIAL_COMMENT = HOST + "commentTimeline";//朋友圈动态评论
    public static final String URL_SOCIAL_DELETE_COMMENT = HOST + "deleteCommentTimeline";//删除朋友圈动态评论
    public static final String URL_SOCIAL_REPLY_COMMENT = HOST + "replyCommentTimeline";//回复朋友圈动态评论
    public static final String URL_SOCIAL_DELETE_REPLY_COMMENT = HOST + "deleteReplyCommentTimeline";//删除朋友圈动态评论回复
    public static final String URL_SOCIAL_GOOD = HOST + "praiseTimeline";//点赞
    public static final String URL_SOCIAL_GOOD_CANCEL = HOST + "deletePraiseTimeline";//取消点赞
    public static final String URL_SOCIAL_GET_PRAISELIST = HOST + "fetchTimelineParises";//获取赞列表
    public static final String URL_SOCIAL_GET_COMMENTLIST = HOST + "fetchTimelineComments";//获取评论列表
    public static final String URL_SOCIAL_GET_DETAIL = HOST + "dynamicInfo";//获取评论列表

    //群相关接口
    public static final String GROUP_HOST = "http://xxxxxxxx/group/";
    public static final String URL_GROUP_CREATE = GROUP_HOST + "groupCreate.php";
    public static final String URL_GROUP_MEMBERS = GROUP_HOST + "mucMembers.php";
    public static final String URL_CHECK_UPDATE = HOST + "version.php";    //查询更新
    public static final String URL_UPLOAD_MOMENT_BACKGROUND = HOST + "uploadpic";//上传朋友圈背景图片
    public static final String URL_GET_RECENTLY_PEOPLE = HOST + "getRecentlyUser";//获取最近上线的人
    public static final String URL_SEND_LOCAL_LOGIN_TIME = HOST + "updateLocalTimestamp";//获取最近上线的人
    public static final String URL_SEND_CONTANCTS = HOST + "filteruser";//上传联系人到服务器
    //文件/及图片上传接口
    public static final String baseImgUrl = "http://fanxin-file-server.oss-cn-shanghai.aliyuncs.com/";
    //?x-oss-process=image/resize,m_fill,h_100,w_100
//    缩略图处理---等高宽
    public static final String baseImgUrl_set = "?x-oss-process=image/resize,m_fill,h_300,w_300";
    //缩略图处理,固定搞
    public static final String reSize = "?x-oss-process=image/resize,h_300";


    public static final String JSON_KEY_NICK = "nick";
    public static final String JSON_KEY_HXID = "userId";
    public static final String JSON_KEY_FXID = "fxid";
    public static final String JSON_KEY_SEX = "sex";
    public static final String JSON_KEY_AVATAR = "avatar";
    public static final String JSON_KEY_CITY = "city";
    public static final String JSON_KEY_PASSWORD = "hx_password";
    public static final String JSON_KEY_PROVINCE = "province";
    public static final String JSON_KEY_TEL = "tel";
    public static final String JSON_KEY_SIGN = "sign";
    public static final String JSON_KEY_ROLE = "role";
    public static final String JSON_KEY_BIGREGIONS = "bigRegions";
    public static final String JSON_KEY_SESSION = "session";
    //添加好友的原因
    public static final String CMD_ADD_REASON = "ADD_REASON";


    public static final String DIR_AVATAR = Environment.getExternalStorageDirectory().toString() + "/yiChat/";

    //进入用户详情页传递json字符串
    public static final String KEY_USER_INFO = "userInfo";
    //修改用户资料的广播
    public static final String KEY_CHANGE_TYPE = "type";

    //web扫描授权相关
    public static final String JSON_KEY_LOGINID = "loginId";
    public static final String JSON_KEY_STATUS = "status";
    public static final String JSON_KEY_APPNAME = "loginName";
    public static final String JSON_KEY_APPICON = "loginIcon";
    //APP授权相关
    public static final String JSON_KEY_THIRDAPPNAME = "appname";
    public static final String JSON_KEY_PACKAGENAME = "packagename";
    public static final String JSON_KEY_THIRDAPPICON = "appicon";
    public static final String JSON_KEY_ISWEB = "isWeb";
    //Buglykey
    public  static final String BUGLY_KEY = "";
    //授权接口相关
    public static final String URL_AUTH_URL = HOST + "authorize";//修改授权状态
    public static final String KEY_AUTH_SUCCESS = "1";//修改授权状态 1成功
    public static final String KEY_AUTH_FAILED = "2";//修改授权状态 2 取消
    //音视频相关
    public static final String DEVELOPERID = "";
    public static final String APPID = "";
    public static final String APPKEY = "";
    public static final String APPTOKEN = "";

    public final static String gHttpLiveListUrl = "http://%s/anyapi/V1/livelist?AppID=%s&DeveloperID=%s";
    public final static String gHttpRecordUrl = "http://%s/anyapi/V1/recordrtmp?AppID=%s&DeveloperID=%s&AnyrtcID=%s&Url=%s&ResID=%s";
    public final static String gHttpCloseRecUrl = "http://%s/anyapi/V1/closerecrtmp?AppID=%s&DeveloperID=%s&VodSvrID=%s&VodResTag=%s";
    /**
     * rtmp 推流地址
     */
    public static final String RTMP_PUSH_URL = "";
    /**
     * rtmp 拉流地址
     */
    public static final String RTMP_PULL_URL = "";
    /**
     * hls 地址
     */
    public static final String HLS_URL = "";

    //关于第三方登录
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String QQ_APP_ID = "";
    //申请的开发appid
    public static final String WX_APP_ID = "";
    public static final String WX_APP_SECRET = "";
    //关于微信的信息授权地址
    public static final String WX_APP_OAUTH2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    //关于微信的获取用户地址
    public static final String WX_APP_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo";
    //微博的APP验证权限
    public static final String SINA_APP_KEY = "";
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html"; //返回地址 使用微博默认的
    public static final String SCOPE = "all";// QQ 微博申请的权限
    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String CHAT_ROOM = "item_chatroom";
    public static final String CHAT_ROBOT = "item_robots";
    public static final int SINGLE_CHAT = 1;
    public static final int GROUP_CHAT = 2;
    /**
     * 智能匹配模版发送接口的http地址
     */
    public static final String URI_SEND_SMS = "http://106.ihuyi.com/webservice/sms.php?method=Submit";
    public static final String URI_SEND_USERNAME = "";
    //短信验证的key
    public static final String SMSAPPKEY = "";
    //短信模板//设置您要发送的内容(内容必须和某个模板匹配。以下例子匹配的是系统提供的1号模板）
    public static final String SMSTEXT = "您的验证码是：【%s】。请不要把验证码泄露给其他人。如非本人操作，可不用理会！";
}
