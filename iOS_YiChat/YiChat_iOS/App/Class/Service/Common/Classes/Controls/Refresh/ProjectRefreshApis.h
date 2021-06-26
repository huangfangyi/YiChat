//
//  ProjectRefreshApis.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/6.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
NS_ASSUME_NONNULL_BEGIN

@interface ProjectRefreshApis : NSObject

+ (NSArray *)projectRefreshApis_getGifImagesWithName:(NSString *)name;

+ (NSArray *)getRefresAnimateImages;
@end

NS_ASSUME_NONNULL_END
