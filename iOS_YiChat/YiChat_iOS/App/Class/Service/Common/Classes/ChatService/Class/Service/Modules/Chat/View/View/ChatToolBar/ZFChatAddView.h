//
//  ZFChatAddView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ZFChatAddView : UIView

- (void)createUI;

- (void)makeUI;

- (void)changeAddViewWithPower:(NSInteger)power;
    
- (void)changeAddViewWithSingleChat;

- (void)addInvocation:(NSDictionary *)dic;

- (void)XYChatAddView_appearWithAnimate:(void(^)(void))animate;

- (void)XYChatAddView_disappearWithAnimate:(void(^)(void))finishInvocation;
@end

@interface ZFChatAddSingle : UIView

@property (nonatomic,strong) NSDictionary *dataSourceDic;

- (void)addInvocation:(NSDictionary *)dic;

- (void)makeUI;

@end

NS_ASSUME_NONNULL_END
