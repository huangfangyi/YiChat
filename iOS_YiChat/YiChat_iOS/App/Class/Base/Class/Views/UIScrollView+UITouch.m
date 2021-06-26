//
//  UIScrollView+UITouch.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/9/2.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "UIScrollView+UITouch.h"
#import <objc/runtime.h>

//处理手写键盘crash
@implementation UIScrollView (UITouch)
    
-(NSString* )getClassName{
    return NSStringFromClass([self class]);
}
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    
    if(![[self getClassName] hasPrefix:@"UIKB"]){
        [[self nextResponder] touchesBegan:touches withEvent:event];
    }
    [super touchesBegan:touches withEvent:event];
}
    
-(void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    if(![[self getClassName] hasPrefix:@"UIKB"]){
        [[self nextResponder] touchesMoved:touches withEvent:event];
    }
    [super touchesMoved:touches withEvent:event];
}
    
- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    if(![[self getClassName] hasPrefix:@"UIKB"]){
        [[self nextResponder] touchesEnded:touches withEvent:event];
    }
    [super touchesEnded:touches withEvent:event];
}

@end
