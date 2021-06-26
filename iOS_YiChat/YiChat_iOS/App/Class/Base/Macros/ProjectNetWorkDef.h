//
//  ProjectNetWorkDef.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/13.
//  Copyright © 2019年 GSY. All rights reserved.
//

#ifndef ProjectNetWorkDef_h
#define ProjectNetWorkDef_h

#define CODE_NORMAL 201

#define REQUEST_CODE(a) [a objectForKey:@"statusCode"]

#define REQUEST_MSG(a) [[a objectForKey:@"error"] objectForKey:@"message"]

#define REQUEST_SUCCESS(a) [[a objectForKey:@"success"] intValue] == 1
//没登录
#define REQUEST_LOGINSTATE(a) [[a objectForKey:@"unAuthorizedRequest"] intValue] == 1

#define REQUEST_DATA(a) [a objectForKey:@"obj"]

#define REQUEST_Expire(a) [[a objectForKey:@"expire"] intValue] == 1

#define REQUEST_STATE(a) [a objectForKey:@"success"]

#define REQUEST_ALERT_NETWORKERROR @"网络故障～"

#define REQUEST_ALERT_WITHOUTLOGIN @"登录失效，请重新登录～"

#endif /* ProjectNetWorkDef_h */
