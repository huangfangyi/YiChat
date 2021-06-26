//
//  YiChatGroupNoticeModel.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/18.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatBassModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatGroupNoticeInfoModel : YiChatBassModel
@property (nonatomic,strong) NSString *userId;
@property (nonatomic,strong) NSString *avatar;
@property (nonatomic,strong) NSString *nick;
@property (nonatomic,strong) NSString *title;
@property (nonatomic,strong) NSString *content;
@property (nonatomic,strong) NSString *time;
@property (nonatomic,strong) NSString *noticeId;
@property (nonatomic,strong) NSString *timeDesc;
@end

@interface YiChatGroupNoticeModel : YiChatBassModel
@property (nonatomic,strong) NSArray *data;
@end

NS_ASSUME_NONNULL_END
