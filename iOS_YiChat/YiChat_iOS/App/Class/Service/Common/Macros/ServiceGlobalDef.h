//
//  ServiceGlobalDef.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#ifndef ServiceGlobalDef_h
#define ServiceGlobalDef_h

#import "ProjectUIHelper.h"
#import "ProjectDef.h"
#import "ProjectLauageManage.h"
#import "NSError+DefaultError.h"

//bid: com.hefei.weiliaoios
//无短信、qq登录

//微聊：47.106.34.37
//聊吧：47.106.64.136
//YiChat：47.107.90.151
#define YiChatProject_NetWork_IsNeedResponseDataAes 1

#define YiChatProject_NetWork_XMPPIP @"XXXX"

#define YiChatProject_NetWork_BaseUrl [NSString stringWithFormat:@"http://%@:8015",YiChatProject_NetWork_XMPPIP]

#define YiChatProject_NetWork_SecretKey @"XXXXX"

#define YiChatProject_NetWork_OSSAccessKey @"XXXXX"
#define YiChatProject_NetWork_OSSSecretKey @"XXXXX"
#define YiChatProject_NetWork_OSSEndPoint @"XXXXX"
#define YiChatProject_NetWork_OSSBucket @"XXXXX"

#define YiChatProject_NetWork_ChatFileHost [NSString stringWithFormat:@"http://%@.%@/",YiChatProject_NetWork_OSSBucket,YiChatProject_NetWork_OSSEndPoint]

#define YiChatProject_Map_Key @""

#define YiChatProject_WeiChat_AppKey @"wxdf5e6194b7530e77"
#define YiChatProject_WeiChat_kRedirectURI    @"https://api.weibo.com/oauth2/default.html"
#define YiChatProject_WeiChat_WechatSecrectKey   @"76d2ac23cbd5821c96ef085e9126b925"

#define YiChatProject_JGPUSH_AppKey @"d1868bff0556de8260dc4e31"
#define YiChatProject_QQ_AppId @"1109844332"

#define YiChatProject_CreateGroupNum 5000
//短信验证权限 1 需要 0 不需要
#define YiChatProjext_CertifyPower 0
//阿里支付 1 需要 0 不需要
#define YiChatProjext_IsNeedAliPay 0
//微信支付 1 需要 0 不需要
#define YiChatProjext_IsNeedWeChat 0
//红包，钱包权限 1 需要 0 不需要
#define YiChatProject_IsNeedRedPackge 1
//是否控制建群权限 1 需要 0 不需要
#define YiChatProject_IsControlCreatGroupPower 1
//是否需要显示被移除群聊提示 1 需要 0 不需要
#define YiChatProject_IsNeedAppearMemberRemovedAlert 1
//是否需要qq登录 1 需要 0 不需要
#define YiChatProject_IsNeedQQLogin 0
//是否需要微信登录 1 需要 0 不需要
#define YiChatProject_IsNeedWeChatLogin 1

#define YiChatProject_IsNeedRefreshChatListBtn 1
#define YiChatProject_IsNeedRefreshGroupChatListBtn 1
#define YiChatProject_IsNeedRefreshSingleChatListBtn 1

#define YiChatProject_IsNeedGifEmoji 1

//是否红包撤回 1 需要 0 不需要
#define YiChatProject_IsBackRedPackge 0

#define YiChatProject_IsNeedMainAdvertisement 0
//是否需要上苹果商店  1 需要 0 不需要
#define YiChatProject_IsUpAppStore 0

#define YiChatProject_IsNeedQianDao 0

#define YiChatProject_Group_GroupNameLimitLength 22

#define PROJECT_COLOR_NAVBACKCOLOR [UIColor whiteColor]
#define PROJECT_COLOR_STATUSBACKCOLOR [UIColor whiteColor]
#define PROJECT_COLOR_NAVTEXTCOLOR  [UIColor whiteColor]

#define PROJECT_COLOR_APPBACKCOLOR [UIColor colorWithRed:241.0/255.0 green:241.0/255.0 blue:245.0/255.0 alpha:1]
#define PROJECT_COLOR_APPMAINCOLOR PROJECT_COLOR_BlLUE

#define PROJECT_COLOR_APPTEXT_MAINCOLOR PROJECT_COLOR_TEXTCOLOR_BLACK
#define PROJECT_COLOR_APPTEXT_SUBCOLOR PROJECT_COLOR_TEXTGRAY

#define PROJECT_COLOR_TABBARBACKCOLOR  [UIColor whiteColor]
#define PROJECT_COLOR_TABBARTEXTCOLOR_SELECTE PROJECT_COLOR_APPMAINCOLOR
#define PROJECT_COLOR_TABBARTEXTCOLOR_UNSELECTE PROJECT_COLOR_TEXTGRAY


#define PROJECT_TEXT_FONT_COMMON(a)  [ProjectUIHelper helper_getCommonFontWithSize:a]

#define PROJECT_TEXT_LOCALIZE_NAME(a) [[ProjectLauageManage sharedLanguage] getAppearWordWithKey:a]

#define PROJECT_TEXT_APPNAME PROJECT_TEXT_LOCALIZE_NAME(@"appName")

#define PROJECT_SIZE_CLICKBTN_H 45.0f
#define PROJECT_SIZE_INPUT_CELLH 45.0f
#define PROJECT_SIZE_COMMON_CELLH 55.0f

#define PROJECT_SIZE_CONVERSATION_CELLH 60.0f

#define PROJECT_SIZE_FRIENDCARD_COMMON_CELLH 45.0f
#define PROJECT_SIZE_FRIENDCARD_INFO_H 90.0f

#define PROJECT_ICON_USERDEFAULT @"fx_default_useravatar.png"
#define PROJECT_ICON_GROUPDEFAULT @"group_avatar.png"
#define PROJECT_ICON_LOADEEROR @""

#define PROJECT_GLOBALNODISTURB [NSString stringWithFormat:@"GlobalNoDisturb%@",[[YiChatUserManager defaultManagaer] getUserIdStr]]

#define PROJECT_NODISTURB [NSString stringWithFormat:@"NoDisturb%@",[[YiChatUserManager defaultManagaer] getUserIdStr]]

#define TableViewRectMake CGRectMake(0, PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH, self.view.frame.size.width, PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH) - PROJECT_SIZE_SafeAreaInset.bottom)
#endif
