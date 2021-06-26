//
//  YiChatGroupSelectePersonView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectBaseCollectiionView.h"

NS_ASSUME_NONNULL_BEGIN

@interface YiChatGroupSelectePersonView : ProjectBaseCollectiionView

- (id)initWithFrame:(CGRect)frame;

- (void)changeSelectePersons:(NSArray *)persons invocation:(void(^)(CGRect frame))selectePersonChangedInvocation;

@end

NS_ASSUME_NONNULL_END
