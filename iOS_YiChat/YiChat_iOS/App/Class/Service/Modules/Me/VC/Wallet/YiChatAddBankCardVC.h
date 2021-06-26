//
//  YiChatAddBankCardVC.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/24.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NavProjectVC.h"
NS_ASSUME_NONNULL_BEGIN

@interface YiChatAddBankCardVC : NavProjectVC
+ (id)initialVC;
@property (nonatomic,assign) NSInteger type;
@property (nonatomic,copy) NSString *bankcard;
@property (nonatomic,copy) NSString *bank;
@end

NS_ASSUME_NONNULL_END
