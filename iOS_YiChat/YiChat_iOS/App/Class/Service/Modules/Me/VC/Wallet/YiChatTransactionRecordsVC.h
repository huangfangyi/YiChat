//
//  YiChatTransactionRecordsVC.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/23.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef enum : NSUInteger {
    RecordTypeAll = 0,//全部
    RecordTypeIncome,//收入
    RecordTypeSpending,//支出
} RecordType;

@interface YiChatTransactionRecordsVC : UIViewController
@property (nonatomic,assign) RecordType recordType;
@end

NS_ASSUME_NONNULL_END
