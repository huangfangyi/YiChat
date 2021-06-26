//
//  YiChatGroupZhenView.h
//  YiChat_iOS
//
//  Created by mac on 2019/8/16.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
typedef void(^ZhengBlock)(NSString *text,BOOL isCancel);
@interface YiChatGroupZhenView : UIView
@property (nonatomic,copy) ZhengBlock zhengBlock;
@end

NS_ASSUME_NONNULL_END
