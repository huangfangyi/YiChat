package com.htmessage.yichat;

import android.os.Environment;

/**
 * Created by ustc on 2016/6/27.
 */
public class HTConstant {

    public static final String BASE_IP = "http://47.111.177.232";

    //服务器端
    public static final String HOST = BASE_IP + "/api/";


    //获取首页显示未读
    //获取最近20条消息
    //更新用户进群时间

    /**
     * 群相关信息
     */
    public static final String URL_GROUP_MEMBERS = HOST + "getmucMembers";

    /**
     * 封禁全员
     */


    //关于CacheKey
    public static final String SMALL_FROM_MOMENT = "SMALL_FROM_MOMENT";


    //文件/及图片上传接口
    public static final String baseImgUrl = HTClientHelper.baseOssUrl;


    public static final String JSON_KEY_NICK = "nick";
    public static final String JSON_KEY_USERID = "userId";
    public static final String JSON_KEY_AVATAR = "avatar";
    //添加好友的原因

    public static final String DIR_AVATAR = Environment.getExternalStorageDirectory().toString() + "/WeTalk/";
    //    缩略图处理---等高宽
    public static final String baseImgUrl_set = "?x-oss-process=image/resize,m_fill,h_480,w_480";
    public static final String baseVideoUrl_set = "?x-oss-process=video/snapshot,t_3000,f_jpg,w_600,h_600,m_fast";


    public static final String chat_baseImgUrl_set = "?x-oss-process=image/resize,m_lfit,h_480,w_480/format,png";
    public static final String chat_baseVideoUrl_set = "?x-oss-process=video/snapshot,t_1000,f_jpg,w_0,h_0,m_fast,ar_auto";


    //申请的开发appid
    public static final String WX_APP_ID = "wx86b0301c2f0c788f";
    public static final String WX_APP_SECRET = "6dc9674cf9aba8223b1db7770821da30";
    //申请的开发appid
    public static final String WX_APP_ID_LOGIN = "wxdf5e6194b7530e77";
    public static final String WX_APP_SECRET_LOGIN  = "76d2ac23cbd5821c96ef085e9126b925";
    /**
     * 关于红包
     */
    private static final String RED_HOST = BASE_IP + "/Wallet/";
    public static final String REBACK_TRANSFER = RED_HOST + "singleRedBack";//领取红包
    public static final String GET_BALANCE = RED_HOST + "getBalance";//查询余额
    public static final String WITH_DRAW_LIST = RED_HOST + "getWithdrawList";//提现列表


    /**
     * 关于二维码转账
     */
    public static final String TRANSFER_MONEY = RED_HOST + "moneyTransfer";//转账

    public static final String TRANSFER_LOGS = RED_HOST + "getTransferLogs";//交易记录
    /**
     * 关于提现
     */
    //更新红包
    public static final String WITHDRAW_BY_ALIPAY = RED_HOST + "hanxuanWithdraw";//提现申请
    //微信提现
    public static final String WITHDRAW_BY_WECHAT = RED_HOST + "hanxuanWxwithdraw";//寒暄提现
    public static final String SET_APPEAR = RED_HOST + "setappear";//寒暄提现
    //银行卡充值

    public static final String CARD_PAY = RED_HOST + "addcardpay";

    //微信支付的结果
    public static final String KEY_PAY_WECHAT = "KEY_PAY_WECHAT";
    //魔方互动的SDK的SECRET
    public static final String MOFANG_SECRET = "fce5f936-f43d-4b90-96ad-0ec04c07f818";

}
