//
//  ZFChatVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableVC.h"

NS_ASSUME_NONNULL_BEGIN
@class YiChatGroupInfoModel;
@interface ZFChatVC : ProjectTableVC

+ (id)initialVCWithChatId:(NSString *)chatId chatType:(NSString *)chatType;

+ (id)initialVCWithGroupModel:(YiChatGroupInfoModel *)model;

- (NSString *)getChatId;
@end

NS_ASSUME_NONNULL_END
