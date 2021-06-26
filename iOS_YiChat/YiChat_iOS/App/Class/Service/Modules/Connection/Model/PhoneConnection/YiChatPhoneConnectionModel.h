//
//  YiChatPhoneConnectionModel.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseModel.h"


NS_ASSUME_NONNULL_BEGIN

@interface YiChatContactEntity : ProjectBaseModel

@property (nonatomic,strong) NSString *connectionName;

@property (nonatomic,strong) NSString *phoneNum;

@property (nonatomic,assign) BOOL isSelecte;

@end

@interface YiChatPhoneConnectionModel : ProjectBaseModel

- (void)fetchPhoneConnectionDataSuccess:(void(^)(NSArray *connectEntityArr))success
                                   fail:(void(^)(NSString *errorMsg,NSInteger code))fail
                           isNeedFectch:(BOOL)isNeedFetch;

/**
 *  @{@"S":@[YiChatContactEntity]}
 */
+ (void)matchConnectionEntitys:(NSArray *)entityArr withCharactersUp:(void(^)(NSArray *connectEntityDicArr))invocation;

@end





NS_ASSUME_NONNULL_END
