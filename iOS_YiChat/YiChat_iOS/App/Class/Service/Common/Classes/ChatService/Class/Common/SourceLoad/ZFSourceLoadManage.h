//
//  ZFSourceLoadManage.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFSourceLoadManage : NSObject

@property (nonatomic,strong) NSArray *defaultChatEmojiArr;

@property (nonatomic,strong) NSArray *defaultChatGifEmojiArr;

@property (nonatomic,strong) UIView *emojiListView;

+ (id)sharedManage;

- (NSArray *)getEmojiTextList;

- (NSArray *)getEmojiGifTextList;
@end

NS_ASSUME_NONNULL_END

