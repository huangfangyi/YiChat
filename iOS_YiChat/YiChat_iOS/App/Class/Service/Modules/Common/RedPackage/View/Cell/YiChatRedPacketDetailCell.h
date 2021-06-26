//
//  YiChatRedPacketDetailCell.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/11.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "YiChatRedPacketDetailModel.h"
NS_ASSUME_NONNULL_BEGIN

@interface YiChatRedPacketDetailCell : UITableViewCell
@property (nonatomic,assign) BOOL isLuck;
@property (nonatomic,strong) YiChatRedPacketInfoModel *model;
@end

NS_ASSUME_NONNULL_END
