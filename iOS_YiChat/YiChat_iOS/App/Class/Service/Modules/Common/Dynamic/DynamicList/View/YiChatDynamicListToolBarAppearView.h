//
//  YiChatDynamicListToolBarAppearView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/14.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface YiChatDynamicListToolBarAppearView : UIView

@property (nonatomic,copy) void(^YiChatDynamicListToolBarAppearViewLikeClick)(NSString *trendId,NSInteger idnex);
@property (nonatomic,copy) void(^YiChatDynamicListToolBarAppearViewCommitClick)(NSString *trendId,NSInteger idnex);

@property (nonatomic,strong) NSString *trendId;

@property (nonatomic,assign) NSInteger index;

+ (id)create;

- (void)changeToOriginPosition:(CGPoint)point;

- (void)beginAppearToPoint:(CGPoint)point isAnimate:(BOOL)isAnimate;

- (void)disappearToPoint:(CGPoint)point isAnimate:(BOOL)isAnimate invacation:(void(^)(void))invcation;

@end

NS_ASSUME_NONNULL_END
