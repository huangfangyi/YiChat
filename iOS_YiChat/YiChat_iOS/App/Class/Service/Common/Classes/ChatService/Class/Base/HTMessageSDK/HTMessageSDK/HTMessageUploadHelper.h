//
//  HTMessageUploadHelper.h
//  HTMessage
//
//  Created by 非夜 on 16/11/22.
//  Copyright © 2016年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface HTMessageUploadHelper : NSObject

- (id)initWithData:(id)data withProgressBlock:(void(^)(CGFloat progress))progressBlocked andMessageId:(NSString *)messageId andSendResult:(void(^)(BOOL isSuccess,NSString *remotePath))resultBlocked;
- (void)uploadObjectAsync;
@property (nonatomic,assign)NSInteger messageType;
@property (nonatomic,strong)NSString *fileName;
@end
