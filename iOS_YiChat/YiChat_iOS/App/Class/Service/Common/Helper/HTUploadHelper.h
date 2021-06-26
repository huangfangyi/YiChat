//
//  HTUploadHelper.h
//  HTMessage
//
//  Created by 非夜 on 16/11/22.
//  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface HTUploadHelper : NSObject

- (id)initWithData:(id)data withProgressBlock:(void(^)(CGFloat progress))progressBlocked andMessageId:(NSString *)messageId andSendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked;

- (void)uploadObjectAsync;
@end
