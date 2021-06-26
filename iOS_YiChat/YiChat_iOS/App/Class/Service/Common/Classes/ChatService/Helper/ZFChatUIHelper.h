//
//  ZFChatUIHelper.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "ProjectMapVC.h"


NS_ASSUME_NONNULL_BEGIN

@class YiChatGroupInfoModel;
@class YiChatUserModel;
@interface ZFChatUIHelper : NSObject

+ (UIViewController *)getChatVCWithChatId:(NSString *)chatId chatType:(NSString *)chatType;

+ (UIViewController *)getGroupChatVCWithGroupModel:(YiChatGroupInfoModel *)groupInfoModel;

+ (id)zfChatUIHelper_initialMapVCWithLocation:(CLLocationCoordinate2D)location address:(NSString *)address description:(NSString *)description;

+ (id)zfChatUIHelper_initialSendMapVCInvocation:(ProjectNavSendLocationHandle)invocation;

+ (UIViewController *)getGroupInfoVCWithGroupId:(NSString *)groupId;

+ (UIViewController *)getGroupInfoVCWithGroupModel:(YiChatGroupInfoModel *)groupModel;

+ (UIViewController *)getUserInfoVCWithUserId:(NSString *)userId;

+ (UIViewController *)getUserInfoVCWithUserModel:(YiChatUserModel *)userModel;

+ (NSString *)zfChatUIHelperConversationLastMessageTimeWithDate:(NSDate *)date;

+ (NSAttributedString *)tranlateStringToAttributedString:(NSString *)string font:(UIFont *)font;

@end

NS_ASSUME_NONNULL_END
