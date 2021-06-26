//
//  YiChatBankCardListVC.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/29.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^chooseBankBlock)(NSString *bankName,NSString *bankNum);

@interface YiChatBankCardListVC : NavProjectVC
+ (id)initialVC;
@property (nonatomic,assign) BOOL isWithdrawal;

@property (nonatomic,copy) chooseBankBlock chooseBank;
@end

NS_ASSUME_NONNULL_END
