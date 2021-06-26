//
//  YiChatMenuView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/24.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatMenuView : UIView

@property (nonatomic,strong) NSDictionary *clickDic;
@property (nonatomic,strong) NSDictionary *backClickDic;

- (void)createUI;

- (void)clean;

@end

NS_ASSUME_NONNULL_END
