//
//  QSNetworkTool.h
//  HTMessage
//
//  Created by 非夜 on 17/2/13.
//  Copyright © 2017年 Hefei Palm Peak Technology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>


typedef void (^QSSuccessBlock)(NSDictionary *dic,NSData *data);
typedef void (^QSFailureBlock)(NSError *error);

@interface QSNetworkTool : NSObject

@property (nonatomic,assign)BOOL isSemaphoreSignal;

+ (NSString *)serializeParameters:(id)parameters;

- (void)requestWithMutableURLRequest:(NSMutableURLRequest *)request success:(QSSuccessBlock)QSSuccessBlock failure:(QSFailureBlock)QSFailureBlock;

- (void)requestHTTPSWithCerPath:(NSString *)cerPath withMutableURLRequest:(NSMutableURLRequest *)request success:(QSSuccessBlock)QSSuccessBlock failure:(QSFailureBlock)QSFailureBlock;

@end
