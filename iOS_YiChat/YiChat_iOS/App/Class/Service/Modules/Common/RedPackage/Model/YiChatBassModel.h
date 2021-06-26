//
//  YiChatBassModel.h
//  YiChat_iOS
//
//  Created by mac on 2019/7/18.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatBassModel : NSObject
@property (nonatomic,assign) NSInteger code;
@property (nonatomic,copy) NSString *msg;
@property (nonatomic,assign) NSInteger pageNo;
@property (nonatomic,assign) NSInteger count;
@property (nonatomic,assign) NSInteger pageSize;
@property (nonatomic,assign) BOOL success;
@end

NS_ASSUME_NONNULL_END
