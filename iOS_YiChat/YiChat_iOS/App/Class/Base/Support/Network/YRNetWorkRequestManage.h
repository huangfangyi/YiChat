//
//  YRNetWorkRequestManage.h
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/17.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YRNetWorkTask.h"
#import "YRNetWorkOperation.h"
#import "YRNetWorkRequest.h"
NS_ASSUME_NONNULL_BEGIN


@interface YRNetWorkRequestManage : NSObject

@property (nonatomic,strong) NSMutableArray *requestTask;

+ (instancetype)sharedManager;

- (YRNetWorkRequestManage *(^)(NSString *baseUrl))addBaseUrl;

- (void)addRequestTask:(id)task;

- (void)stopTaskWithName:(NSArray *)name;

- (void)removeRequstTask:(YRNetWorkTask *)task;

@end

NS_ASSUME_NONNULL_END
