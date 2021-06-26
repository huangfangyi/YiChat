//
//  YiChatBankCardListModel.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/13.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatBassModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatBankCardInfoModel : YiChatBassModel
@property (nonatomic,strong) NSString *bankNumber;
@property (nonatomic,strong) NSString *bankName;
@property (nonatomic,strong) NSString *cardID;
@end

@interface YiChatBankCardListModel : YiChatBassModel
@property (nonatomic,strong) NSArray *data;
@end

NS_ASSUME_NONNULL_END
