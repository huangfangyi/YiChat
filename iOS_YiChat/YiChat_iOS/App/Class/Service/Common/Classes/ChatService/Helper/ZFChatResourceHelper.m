//
//  ZFChatResourceHelper.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatResourceHelper.h"
#import "ZFChatGlobal.h"
#import "ZFSourceLoadManage.h"
#import "ZFChatEmojiView.h"

@implementation ZFChatResourceHelper

+ (NSArray *)ZFResourceHelperGetChatEmojiArr{
    return [[ZFSourceLoadManage sharedManage] defaultChatEmojiArr];
}

+ (NSArray *)ZFResourceHelperGetChatEmojiTTextArr{
    return [[ZFSourceLoadManage sharedManage] getEmojiTextList];
}

+ (NSArray *)ZFResourceHelperGetChatGIFEmojiArr{
    return [[ZFSourceLoadManage sharedManage] defaultChatGifEmojiArr];
}

+ (void)ZFResourceHelperLoadEmojiView{
    
    ZFChatEmojiView *emoji = [[ZFChatEmojiView alloc] initWithFrame:CGRectMake(0,0, PROJECT_SIZE_WIDTH, 200)];
    [emoji createUI];
    
    ZFSourceLoadManage *load = [ZFSourceLoadManage sharedManage];
    load.emojiListView = emoji;
    
}

+ (void)ZFResourceHelperResourceLoad{
    [self ZFResourceHelperLoadEmojiView];
}

+ (UIView *)ZFResourceHelperGetLoadEmojiView{
    ZFSourceLoadManage *load = [ZFSourceLoadManage sharedManage];
    return load.emojiListView;
}
@end

