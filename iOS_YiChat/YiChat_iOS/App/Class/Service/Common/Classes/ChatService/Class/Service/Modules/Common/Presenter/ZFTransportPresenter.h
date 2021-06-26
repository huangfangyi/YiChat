//
//  ZFTransportPresenter.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@class HTMessage;

@interface ZFTransportPresenter : NSObject

@property (nonatomic,copy) void(^ZFTransportPresenterTransPortProgress)(NSInteger num,NSInteger totalNum);

@property (nonatomic,strong) NSString *type;

- (id)initWithMessage:(HTMessage *)message;

- (void)transportMsgTo:(NSArray *)to invocation:(void(^)(void))completion;

@end

NS_ASSUME_NONNULL_END
