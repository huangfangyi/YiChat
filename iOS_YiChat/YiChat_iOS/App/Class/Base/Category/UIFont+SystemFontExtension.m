//
//  UIFont+SystemFontExtension.m
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/3.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import "UIFont+SystemFontExtension.h"
#import <objc/message.h>
#import "YRGeneralApis.h"

@implementation UIFont (SystemFontExtension)

+ (void)load{
    Method newMethod = class_getClassMethod(self, @selector(extensionSystemFontOfSize:));
    Method method = class_getClassMethod(self, @selector(systemFontOfSize:));
    
    method_exchangeImplementations(newMethod,method);
}

+ (UIFont *)extensionSystemFontOfSize:(CGFloat)fontSize{
    return [UIFont extensionSystemFontOfSize:[YRGeneralApis yrGeneralApisGetScreenSuitable_W:fontSize]];
}


@end
