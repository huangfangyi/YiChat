//
//  ProjectSendCertifyView.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/14.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectSendCertifyView : UIView

+ (instancetype)buildObjWithFrame:(NSValue *)rectValue;

- (void)addInvocation:(NSDictionary *)invocation;

- (void)createUI;

- (void)sendCertify;

@end

NS_ASSUME_NONNULL_END
