//
//  YiChatMassView.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/19.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^MassBlock)(NSString *content,BOOL isSend);

@interface YiChatMassView : UIView
@property (nonatomic,copy) MassBlock massBlock;
@end

NS_ASSUME_NONNULL_END
