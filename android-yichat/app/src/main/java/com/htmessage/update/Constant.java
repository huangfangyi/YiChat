package com.htmessage.update;

import com.htmessage.sdk.SDKConstant;

/**
 * Created by huangfangyi on 2019/7/20.
 * qq 84543217
 */
public class Constant {
    //注册是否需要验证码
public static boolean isSMS = false      ;
    public static boolean GROUP_DELETE_MEMBER_NOTIFY = false;
     public static final String BASE_IP = "http://"+ SDKConstant.HOST +":8015";
    public static boolean isRedpacketCanWithdraw=true;
    public static int MaxGroupCount=5000;
    public static final String URL_REGISTER = BASE_IP + "/api/user/register";
    public static final String URL_LOGIN = BASE_IP + "/api/login";
    public static final String URL_THIRD_LOGIN = BASE_IP + "/api/login/third";
    public static final String URL_SMS_SEND = BASE_IP + "/api/sms/send";
    public static final String URL_USER_SEARCH = BASE_IP + "/api/user/search";
    public static final String URL_FRIEND_LIST = BASE_IP + "/api/friend/list";
    public static final String URL_USER_INFO = BASE_IP + "/api/user/info";
    public static final String URL_FRIEND_APPLY = BASE_IP + "/api/friend/apply";
    public static final String URL_FRIEND_APPLY_LIST = BASE_IP + "/api/friend/apply/list";
    //处理好友申请
    public static final String URL_FRIEND_APPLY_CHECK = BASE_IP + "/api/friend/apply/check";
    //上传聊天消息
    public static final String URL_UPLOAD_MESSAGE = BASE_IP + "/api/message/upload";

    //获取群信息
    public static final String URL_GROUP_INFO = BASE_IP + "/api/group/info";


    //关于微信的信息授权地址
    public static final String WX_APP_OAUTH2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    //关于微信的获取用户地址
    public static final String WX_APP_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo";
    //群历史消息获取
    public static final String URL_CHAT_HISTORY = BASE_IP + "/api/message/list";
    //群成员列表
    public static final String URL_GROUP_MEMBERS = BASE_IP + "/api/group/user/list";
    //获取管理员列表

    public static final String URL_GROUP_MANAGERS = BASE_IP + "/api/group/admin/list";
    //更新自己最近一次获取历史消息的时间戳

    public static final String URL_UPDATE_TIMESTAMP = BASE_IP + "/api/user/timestamp/update";

    //获取历史群消息的未读数+最近一条消息，用于初始化会话页+版本信息（做提示更新及强制更新）
    public static final String URL_CONFIG = BASE_IP + "/api/config";
    //检查token
    public static final String URL_CHECK_TOKEN = BASE_IP + "/api/check/token";
    //更新用户资料
    public static final String URL_INFO_UPDATE = BASE_IP + "/api/user/info/update";

    //更新服务器上的消息(例如消息撤回)
    public static final String URL_MESSAGE_UPDATE = BASE_IP + "/api/message/update";
    //搜索群历史消息
    public static final String URL_MESSAGE_SEARCH = BASE_IP + "/api/message/search/list";
    //获取最新版本信息
    public static final String URL_VERSION = BASE_IP + "/api/version";

    //群禁言的设置及取消
    public static final String URL_GROUP_SILENT = BASE_IP + "/api/group/silent/set";
    //单个群成员的禁言及取消
    public static final String URL_GROUP_SILENT_MEMBER = BASE_IP + "/api/group/member/silent/set";

    //单个禁言列表
    public static final String URL_GROUP_SILENT_LIST = BASE_IP + "/api/group/silent/list";
    //管理员的设置及取消
    public static final String URL_GROUP_MANAGER = BASE_IP + "/api/group/admin/set";
    //管理员的设置及取消
    public static final String URL_FRIEND_DELETE = BASE_IP + "/api/friend/delete";


    //申请的开发appid
    public static final String WX_APP_ID = "wx360b94b93b0dd095";
    public static final String WX_APP_SECRET = "8e055caf65eb791711e25a79e2f4a8db";
    //Buglykey
    public static final String BUGLY_KEY = "b587b394d4";

    //红包钱包相关API
    //查询余额
    public static final String URL_BALANCE = BASE_IP + "/api/user/balance";
    //创建单聊红包
    public static final String URL_RedPacket_SINGE = BASE_IP + "/api/packet/create/single";
    //创建群聊红包
    public static final String URL_RedPacket_GROUP = BASE_IP + "/api/packet/create/group";

    //领取红包
    public static final String URL_RedPacket_Receive = BASE_IP + "/api/packet/receive";

    //红包详情、
    public static final String URL_RedPacket_Detail = BASE_IP + "/api/packet/detail";
    //银行列表
    public static final String URL_bankcard_list = BASE_IP + "/api/user/bank/card/list";
    //设置支付密码
    public static final String URL_paypsw_set = BASE_IP + "/api/user/pay/password/set";

    //朋友圈相关

    //点赞
    public static final String URL_trend_praise = BASE_IP + "/api/trend/praise";
    //取消点赞

    public static final String URL_trend_praise_cancel = BASE_IP + "/api/trend/praise/cancle";
    //删除评论
    public static final String URL_trend_comment_delete = BASE_IP + "/api/trend/comment/delete";
    //发表一个评论
    public static final String URL_trend_comment = BASE_IP + "/api/trend/comment";
    //删除一条动态
    public static final String URL_trend_delete = BASE_IP + "/api/trend/delete";

    //获取一个动态的详情

    public static final String URL_trend_detail = BASE_IP + "/api/trend/detail";
    //发布朋友圈
    public static final String URL_trend_publish = BASE_IP + "/api/trend/publish";
    //查看自己或者朋友的相册
    public static final String URL_trend_list = BASE_IP + "/api/trend/list";
    //设置朋友圈背景
    public static final String URL_trend_background = BASE_IP + "/api/trend/background/set";
    //我的朋友圈动态列表
    public static final String URL_trend_friend_list = BASE_IP + "/api/trend/friend/list";

    public static final String PAY_BY_WECHAT = BASE_IP + "/api/pay/weixin/pre";//微信充值
    public static final String PAY_BY_ALIPAY = BASE_IP + "/api/pay/alipay/pre";//支付宝充值
    public static final String URL_withdraw_list = BASE_IP + "/api/user/withdraw/list";//支付宝充值
    //领取红包汇总
    public static final String URL_packet_receive_info = BASE_IP + "/api/packet/receive/info";
    public static final String URL_packet_receive_list = BASE_IP + "/api/packet/receive/list";
    //发送红包汇总
    public static final String URL_packet_send_info = BASE_IP + "/api/packet/send/info";
    public static final String URL_packet_send_list = BASE_IP + "/api/packet/send/list";
    //交易记录
    public static final String URL_balance_list=BASE_IP +"/api/user/balance/list";
    //增加绑定银行卡
    public static final String URL_bank_card_add=BASE_IP +"/api/user/bank/card/add";
    //获取银行卡列表
    public static final String URL_bank_card_list=BASE_IP +"/api/user/bank/card/list";
    //删除一张银行卡
    public static final String URL_bank_card_delete=BASE_IP +"/api/user/bank/card/delete";
    //申请提现
    public static final String URL_withdraw_apply=BASE_IP +"/api/user/withdraw/apply";
    // 签到
    public static final String URL_SIGN=BASE_IP +"/api/user/sign";

    //群公告查看
    public static final String URL_GROUP_NOTICE_LIST=BASE_IP +"/api/group/notice/last";
    //群公告删除
    public static final String URL_GROUP_NOTICE_DELETE=BASE_IP +"/api/group/notice/delete";

    //群公告发布
    public static final String URL_GROUP_NOTICE_publish=BASE_IP +"/api/group/notice/publish";
    //群公告发布
    public static final String URL_REMARK=BASE_IP +"/api/friend/remark/set";
    //群公告发布
    public static final String URL_friend_apply_delete=BASE_IP +"/api/friend/apply/delete";
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String QQ_APP_ID = "1107515407";

    public static final String  XIEYI_REGISTER="http://baidu.com";

    public static final String  URL_WITHDRAW_CONFIG=BASE_IP +"/api/user/withdraw/config";
    //好友申请的未读数
    public static final String URL_APPLY_UNREAD=BASE_IP +"/api/friend/apply/unread";

}
