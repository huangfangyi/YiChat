//
//  ProjectIconsNumView.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/18.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectIconsNumView : UIView

+ (id)createUIWithFrame:(CGRect)frame num:(NSInteger)num;

- (void)updateNum:(NSInteger)num;
@end

NS_ASSUME_NONNULL_END
