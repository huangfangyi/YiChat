//
//  HTMessageCategoryUploader.h
//  HTMessage
//
//  Created by 非夜 on 17/1/5.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HTMessage.h"

@interface HTMessageCategoryUploader : NSObject

+ (void)sendMessage:(HTMessage *)message  withProgressBlock:(void(^)(CGFloat progress))progressBlocked andMessageId:(NSString *)messageId andSendResult:(void(^)(BOOL isSuccess,HTMessage * kMessage))resultBlocked;

@end
