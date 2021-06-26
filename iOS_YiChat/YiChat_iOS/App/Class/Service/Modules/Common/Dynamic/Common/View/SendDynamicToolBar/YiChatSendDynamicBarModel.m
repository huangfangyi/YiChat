//
//  YiChatSendDynamicBarModel.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/11.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatSendDynamicBarModel.h"

@implementation YiChatSendDynamicBarModel

- (BOOL)isVideo{
    if(_type == YiChatSendDynamicBarModelTypeVideo){
        return YES;
    }
    else{
        return NO;
    }
}

@end
