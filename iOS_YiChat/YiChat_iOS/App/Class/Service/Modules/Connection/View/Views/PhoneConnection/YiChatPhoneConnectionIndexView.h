//
//  YiChatPhoneConnectionIndexView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/28.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatPhoneConnectionIndexView : UIView

@property (nonatomic,copy) void(^yichatIndexViewClick)(NSInteger clickIndex);

- (id)initWithData:(NSArray *)charactersStrArr bgView:(UIView *)bgView;

- (void)updateUIWithData:(NSArray *)characters;

+ (CGFloat)getIndexViewHeightWithCharacters:(NSArray *)character;

@end

NS_ASSUME_NONNULL_END
