//
//  YiChatRedPacketChoosePayView.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/3.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef enum : NSUInteger {
    RedPacketPayBalance,
    RedPacketPayAli,
    RedPacketPayWeChat,
} RedPacketPayType;
typedef void(^dissmissView)(void);
typedef void(^PayType)(RedPacketPayType payType,NSString *password);
@interface YiChatRedPacketChoosePayView : UIView
@property (nonatomic,weak) UIViewController *vc;
@property (nonatomic,copy) NSString *redMoney;
@property (nonatomic,copy) NSString *balance;
@property (nonatomic,copy) PayType payType;
@property (nonatomic,copy) dissmissView dissmiss;
@end

NS_ASSUME_NONNULL_END
