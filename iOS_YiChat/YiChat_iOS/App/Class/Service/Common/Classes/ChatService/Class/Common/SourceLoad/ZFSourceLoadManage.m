//
//  ZFSourceLoadManage.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFSourceLoadManage.h"

static ZFSourceLoadManage *manage = nil;

static NSArray *chatDefualtEmojiResource = nil;
static NSArray *chatDefualtGifEmojiResource = nil;
@interface ZFSourceLoadManage ()

@end

@implementation ZFSourceLoadManage

+ (void)load{
    NSMutableArray *defaultChatIcons = [NSMutableArray arrayWithCapacity:0];
    for (int i = 1; i < 140; i++) {
        if(i != 44){
            NSString *name = [NSString stringWithFormat:@"emoji_%d@3x%@",i,@".png"];
            UIImage *img = [UIImage imageNamed:name];
            
            if(img != nil){
                [defaultChatIcons addObject:img];
            }
        }
    }
    chatDefualtEmojiResource = defaultChatIcons;
    
    NSMutableArray *defaultChatGifIcons = [NSMutableArray arrayWithCapacity:0];
    for (int i = 1; i < 102; i++) {
    //        if(i != 44){
                NSString *name = [NSString stringWithFormat:@"gemoji_%d%@",i,@".gif"];
                UIImage *img = [UIImage imageNamed:name];
                NSLog(@"defaultChatGifIcons ==   %d",i);
                if(img != nil){
                    [defaultChatGifIcons addObject:img];
                }else{
                    NSString *jgpName = [NSString stringWithFormat:@"gemoji_%d%@",i,@".jpg"];
                    UIImage *jpg = [UIImage imageNamed:jgpName];
                    if (jpg == nil) {
                        NSString *jpegName = [NSString stringWithFormat:@"gemoji_%d%@",i,@".jpeg"];
                        UIImage *jpeg = [UIImage imageNamed:jpegName];
                        if (!jpeg) {
                            [defaultChatGifIcons addObject:jpeg];
                        }
                    }else{
                        [defaultChatGifIcons addObject:jpg];
                    }
                }
    //        }
        }
//    chatDefualtEmojiResource = defaultChatIcons;
    chatDefualtGifEmojiResource = defaultChatGifIcons;
}

+ (id)sharedManage{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manage = [[self alloc] init];
        manage.defaultChatEmojiArr = chatDefualtEmojiResource;
        manage.defaultChatGifEmojiArr = chatDefualtGifEmojiResource;
        
    });
    return manage;
}


- (NSArray *)getEmojiTextList{
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
    for (int i = 1; i < 140; i ++) {
        if(i != 44){
            [arr addObject:[NSString stringWithFormat:@"[emoji_%d]",i]];
        }
    }
    return arr;
}

- (NSArray *)getEmojiGifTextList{
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
    for (int i = 1; i < 61; i ++) {
//        if(i != 44){
            [arr addObject:[NSString stringWithFormat:@"[gemoji_%d]",i]];
//        }
    }
    return arr;
}

@end

