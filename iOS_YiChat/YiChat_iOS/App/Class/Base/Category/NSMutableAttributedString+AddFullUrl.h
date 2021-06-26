//
//  NSMutableAttributedString+AddFullUrl.h
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/4.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSMutableAttributedString (AddFullUrl)

+ (NSMutableAttributedString *)createFullUrlStrWithString:(NSString *)string font:(UIFont *)font color:(UIColor *)color;

- (void)addFullUrlWithWithColor:(UIColor *)color range:(NSRange)range url:(NSString *)url;
@end

NS_ASSUME_NONNULL_END
