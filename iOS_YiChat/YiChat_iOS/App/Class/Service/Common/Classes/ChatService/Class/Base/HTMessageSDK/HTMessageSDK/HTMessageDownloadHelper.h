//
//  HTMessageDownloadHelper.h
//  HTMessage
//
//  Created by 非夜 on 17/4/9.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HTMessage.h"

@interface HTMessageDownloadHelper : NSObject

- (void)downLoadActionWithMessage:(HTMessage *)message completion:(void(^)(HTMessage * message))completion;

@end
