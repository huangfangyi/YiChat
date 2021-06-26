//
//  YiChatRedPacketListModel.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/13.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatBassModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatRedPacketListInfoModel : YiChatBassModel
@property (nonatomic,assign) double money;
@property (nonatomic,assign) NSInteger luckCount;
@property (nonatomic,assign) NSInteger count;
@end



@interface YiChatRedPacketModel : YiChatBassModel
@property (nonatomic,strong) YiChatRedPacketListInfoModel *data;
@end

NS_ASSUME_NONNULL_END
