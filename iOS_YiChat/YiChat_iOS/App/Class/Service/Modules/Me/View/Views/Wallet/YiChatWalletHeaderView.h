//
//  YiChatWalletHeaderView.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/25.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^WalletHeaderBlock)(BOOL isWithdrawal);

@interface YiChatWalletHeaderView : UIView
@property (nonatomic,strong) NSString *balance;

@property (nonatomic,copy) WalletHeaderBlock walletHeaderBlock;
@end

NS_ASSUME_NONNULL_END
