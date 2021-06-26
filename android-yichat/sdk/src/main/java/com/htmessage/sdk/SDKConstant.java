package com.htmessage.sdk;

/**
 * Created by huangfangyi on 2016/9/14.
 * qq 84543217
 */
public class SDKConstant {
    //是否是无限授权版本：
    public  static  final  boolean IS_LIMITLESS=false;
    //是否支持群离线消息
    public  static  final  boolean IS_GROUP_OFFLINE=false;

    //透传
    public static final  int TYPE_MESSGAE_CMD =1000;
    //正常消息体
    public static final  int TYPE_MESSGAE_HT=2000;
    //群被删除
    public static final  int TYPE_MESSGAE_GROUP_DESTROY=4000;
    //有人主动退出
    public static final  int TYPE_MESSGAE_GROUP_LEAVE=3000;
    //音视频通话透传消息
    public static final  int TYPT_CALL=5000;
    public  static final String FX_MSG_KEY_TYPE="type";
    public  static final String FX_MSG_KEY_DATA="data";
 

    public static final String SERVER_NAME = "app.im";
    public static final String SERVER_DOMAIN = "@app.im";
    public static final String SERVER_DOMAIN_MUC = "@muc.app.im";
    public static final int PORT = 5222;

    public static final String HOST = "";//api服务器ip
    //阿里云OSS
      public static final String accessKeyId = " ";
    public static final String accessKeySecret = " ";
    public static final String endpoint =" ";
    public static final String bucket = " ";
    public static final String URL_GROUP_LIST = "http://"+HOST+":8015/api/group/my/list";


    public static final String baseOssUrl = "http://"+bucket+"."+endpoint+"/";

}

