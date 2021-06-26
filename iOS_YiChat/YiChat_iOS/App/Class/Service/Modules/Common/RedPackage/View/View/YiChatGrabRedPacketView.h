//
//  YiChatGrabRedPacketView.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/10.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//  抢红包view

#import <UIKit/UIKit.h>
#import "YiChatRedPacketDetailModel.h"
NS_ASSUME_NONNULL_BEGIN

typedef void(^RedPacket)(YiChatRedPacketDetailModel *model,BOOL isJump);
@interface YiChatGrabRedPacketView : UIView

@property (nonatomic,strong) UIButton *luckButton;
@property (nonatomic,copy) RedPacket redPacketBlock;
@property (nonatomic,strong) YiChatRedPacketListModel *model;

@property (nonatomic,assign) BOOL isNo;//单聊红包对方还没有领取
@end

NS_ASSUME_NONNULL_END
