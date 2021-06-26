//
//  NSMutableAttributedString+AddFullUrl.m
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/4.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import "NSMutableAttributedString+AddFullUrl.h"

@implementation NSMutableAttributedString (AddFullUrl)

+ (NSMutableAttributedString *)createFullUrlStrWithString:(NSString *)string font:(UIFont *)font color:(UIColor *)color{
    
    NSMutableAttributedString *attrStr = [[NSMutableAttributedString alloc] initWithString:string attributes:@{NSFontAttributeName:font,NSForegroundColorAttributeName:color}];
    
    return attrStr;
}

- (void)addFullUrlWithWithColor:(UIColor *)color range:(NSRange)range url:(NSString *)url{
    [self addAttribute:NSForegroundColorAttributeName value:color range:range];
    [self addAttribute:NSLinkAttributeName value:url range:range];
}
@end
