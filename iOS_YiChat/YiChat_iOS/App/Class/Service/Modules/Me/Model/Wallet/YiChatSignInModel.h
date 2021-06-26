//
//  YiChatSignInModel.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/14.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatBassModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatSignInInfoModel : YiChatBassModel
@property (nonatomic,strong) NSString *memo;
@property (nonatomic,assign) BOOL isToday;
@property (nonatomic,assign) BOOL signStatus;
@end

@interface YiChatSignInListModel : YiChatBassModel
@property (nonatomic,strong) NSString *content;
@property (nonatomic,strong) NSArray *list;
@end

@interface YiChatSignInModel : YiChatBassModel
@property (nonatomic,strong) YiChatSignInListModel *data;
@end

NS_ASSUME_NONNULL_END
