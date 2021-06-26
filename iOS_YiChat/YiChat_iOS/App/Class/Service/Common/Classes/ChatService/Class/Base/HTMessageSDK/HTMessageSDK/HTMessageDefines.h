//
//  HTMessageDefines.h
//  HTMessage
//
//  Created by 非夜 on 16/10/21.
//  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#ifndef HTMessageDefines_h
#define HTMessageDefines_h

#import "HTClient.h"

//IM服务器IP
#define kXMPP_HOST [[HTClient sharedInstance] config_xmppIP]
//#ifdef DEBUG
#define kXMPP_PORT      5222
//#else
//#define kXMPP_PORT      25222
//#endif
//#ifdef DEBUG
#define IM_HOST_URL    [NSString stringWithFormat:@"http://%@:19080/rest/adhoc/",kXMPP_HOST]
#define IM_HOST_NEW_URL    [NSString stringWithFormat:@"http://%@:8015/",kXMPP_HOST]

//业务服务器地址，有两个php接口需要用到
#define BUSINESS_HOST [[HTClient sharedInstance] config_serviceIP]

#define CHAT_FILE_HOST [[HTClient sharedInstance] config_chatFileHost]//趣聊

#define kXMPP_DOMAIN    @"app.im"
#define kXMPP_SUBDOMAIN @"muc"
#define kXMPP_RESOURCE  @"mobile"
#define kXMPP_USERNAME  @"kXMPP_USERNAME"
#define kXMPP_PASSWORD  @"kXMPP_PASSWORD"

#define OSSAccessKey  [[HTClient sharedInstance] config_OSSAccessKey]
#define OSSSecretKey [[HTClient sharedInstance] config_OSSSecretKey]
#define OSSEndPoint  [[HTClient sharedInstance] config_OSSEndPoint]
#define OSSBucket   [[HTClient sharedInstance] config_OSSBucket]


static NSString * MONTAGE_URL = @"?x-oss-process=image/resize,m_fill,h_240,w_240";

// HTMessage 静态字符串
// 通过接收到的消息创建群组
static NSString * HT_NEW_GROUP_CREATED = @"HT_NEW_GROUP_CREATED";
static NSString * HT_UPDATE_GROUP_INFO = @"HT_UPDATE_GROUP_INFO";
static NSString * HT_DELETE_GROUP_INFO = @"HT_DELETE_GROUP_INFO";

// 文件目录
static NSString * HT_FILE_PATH = @"_data/";

#define DLog(s, ...) NSLog( @"%s [Line %d] %@", __PRETTY_FUNCTION__, __LINE__,[NSString stringWithFormat:(s), ##__VA_ARGS__] )


//#else
//#define IM_HOST_URL @"http://139.196.170.89:29080/rest/adhoc/"
//#endif

//#define IM_HOST_URL @"http://106.14.239.204:19080/rest/adhoc/"
//#define IM_HOST_URL @"http://139.196.170.89:19080/rest/adhoc/"



#endif /* HTMessageDefines_h */
