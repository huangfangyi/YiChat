//
//  ZFChatResourceHelper.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
NS_ASSUME_NONNULL_BEGIN

@interface ZFChatResourceHelper : NSObject


+ (NSArray *)ZFResourceHelperGetChatEmojiArr;

+ (NSArray *)ZFResourceHelperGetChatEmojiTTextArr;

+ (NSArray *)ZFResourceHelperGetChatGIFEmojiArr;

+ (void)ZFResourceHelperResourceLoad;

+ (void)ZFResourceHelperLoadEmojiView;

+ (UIView *)ZFResourceHelperGetLoadEmojiView;

@end

NS_ASSUME_NONNULL_END

