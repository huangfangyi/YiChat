//
//  ProjectSearchMsgModel.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/1.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectSearchMsgModel : NSObject
@property (nonatomic ,strong) NSString *name;

@property (nonatomic ,strong) NSArray *messageArr;

@property (nonatomic ,strong) NSString *avatar;
@end

NS_ASSUME_NONNULL_END
