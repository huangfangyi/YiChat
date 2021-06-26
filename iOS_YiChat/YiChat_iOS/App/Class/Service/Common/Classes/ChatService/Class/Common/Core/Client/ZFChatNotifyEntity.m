//
//  ZFChatNotifyEntity.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/14.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatNotifyEntity.h"
#import "ZFChatGlobal.h"
@implementation ZFChatNotifyEntity

- (id)initWithChatNotifyStyle:(ZFChatNotifyStyle)style{
    self = [super init];
    if(self){
        _notifyName = [ZFChatNotify getNotifyNameWithStyle:style];
        _style = style;
    }
    return self;
}

- (id)initWithChatNotifyStyle:(ZFChatNotifyStyle)style target:(id)target sel:(SEL)selector{
    self = [super init];
    if(self){
        _notifyName = [ZFChatNotify getNotifyNameWithStyle:style];
        _target = target;
        _selector = selector;
        _style = style;
    }
    return self;
}

- (id)initWithChatNotifyStyle:(ZFChatNotifyStyle)style superNotifyName:(NSString *)superNotfyName target:(id)target sel:(SEL)selector{
    self = [super init];
    if(self){
        _superResponseNotifyName = superNotfyName;
        _notifyName = [ZFChatNotify getNotifyNameWithStyle:style];
        _target = target;
        _selector = selector;
        _style = style;
    }
    return self;
}

- (void)addNotify{
    if(self.target && self.selector && self.notifyName){
        [[NSNotificationCenter defaultCenter] addObserver:self.target selector:self.selector name:self.notifyName object:nil];
    }
}

- (void)addSuperNotify{
    if(self.target && self.selector && self.superResponseNotifyName){
        [[NSNotificationCenter defaultCenter] addObserver:self.target selector:self.selector name:self.superResponseNotifyName object:nil];
    }
}

- (void)removeMotify{
    if(self.target && self.notifyName){
        [[NSNotificationCenter defaultCenter] removeObserver:self.target name:self.notifyName object:nil];
    }
}

- (void)removeSuperNotify{
    if(self.target  && self.superResponseNotifyName){
         [[NSNotificationCenter defaultCenter] removeObserver:self.target name:self.superResponseNotifyName object:nil];
    }
}

- (void)postNotifyWithContent:(id)content{
    if(self.notifyName){
        [ProjectHelper helper_getGlobalThread:^{
          [[NSNotificationCenter defaultCenter] postNotificationName:self.notifyName object:content];
        }];
    }
}

@end
