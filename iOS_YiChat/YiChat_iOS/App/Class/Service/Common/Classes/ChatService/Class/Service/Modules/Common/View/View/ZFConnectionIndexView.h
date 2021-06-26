//
//  ZFConnectionIndexView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFConnectionIndexView : UIView

@property (nonatomic,copy) void(^zfIndexViewClick)(NSInteger clickIndex);

- (id)initWithData:(NSArray *)charactersStrArr bgView:(UIView *)bgView;

- (void)updateUIWithData:(NSArray *)characters;

+ (CGFloat)getIndexViewHeightWithCharacters:(NSArray *)character;

@end

NS_ASSUME_NONNULL_END
