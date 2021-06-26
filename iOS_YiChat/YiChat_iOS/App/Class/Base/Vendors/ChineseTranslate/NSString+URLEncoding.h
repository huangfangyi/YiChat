//
//  NSString+URLEncoding.h
//  DoubanBook
//
//  Created by zhangcheng on 19/02/2012.
//  Copyright (c) 2012  www.mobiletrain.org. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSString(URLEncoding)

- (NSString *)urlEncodeString;

- (NSString *)URLDecodedString;
    
- (NSString *)specialURLDecodedString;

@end
