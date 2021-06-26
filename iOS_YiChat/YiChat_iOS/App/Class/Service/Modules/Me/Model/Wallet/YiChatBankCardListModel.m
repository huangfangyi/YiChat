//
//  YiChatBankCardListModel.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/13.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatBankCardListModel.h"

@implementation YiChatBankCardInfoModel
+ (NSDictionary *)mj_replacedKeyFromPropertyName{
    return @{@"cardID" : @"id"//前边的是你想用的key，后边的是返回的key
             };
}
@end

@implementation YiChatBankCardListModel
+ (NSDictionary *)mj_objectClassInArray{
    return @{@"data" : @"YiChatBankCardInfoModel"};
}
@end
