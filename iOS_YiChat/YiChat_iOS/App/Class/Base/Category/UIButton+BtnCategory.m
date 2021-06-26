//
//  UIButton+BtnCategory.m
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/6.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import "UIButton+BtnCategory.h"
#import <objc/message.h>

static char *btnIdentifierKey = "btnIdentifier_BtnCategory";
@implementation UIButton (BtnCategory)

-(void)setBtnIdentifier:(NSString *)btnIdentifier{
    /*
     objc_AssociationPolicy参数使用的策略：
     OBJC_ASSOCIATION_ASSIGN;            //assign策略
     OBJC_ASSOCIATION_COPY_NONATOMIC;    //copy策略
     OBJC_ASSOCIATION_RETAIN_NONATOMIC;  // retain策略
     
     OBJC_ASSOCIATION_RETAIN;
     OBJC_ASSOCIATION_COPY;
     */
    /*
     关联方法：
     objc_setAssociatedObject(id object, const void *key, id value, objc_AssociationPolicy policy);
     
     参数：
     * id object 给哪个对象的属性赋值
     const void *key 属性对应的key
     id value  设置属性值为value
     objc_AssociationPolicy policy  使用的策略，是一个枚举值，和copy，retain，assign是一样的，手机开发一般都选择NONATOMIC
     */
    
    objc_setAssociatedObject(self, btnIdentifierKey, btnIdentifier, OBJC_ASSOCIATION_COPY_NONATOMIC);
}

-(NSString *)btnIdentifier{
    return objc_getAssociatedObject(self, btnIdentifierKey);
}



@end
