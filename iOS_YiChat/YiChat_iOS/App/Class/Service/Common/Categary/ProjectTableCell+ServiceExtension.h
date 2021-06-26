//
//  ProjectTableCell+ServiceExtension.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/20.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectTableCell.h"

NS_ASSUME_NONNULL_BEGIN

@interface ProjectTableCell (ServiceExtension)

- (void)imageLoadIconWithUrl:(NSString *)url placeHolder:(UIImage *)placeHolder imageControl:(UIImageView *)imageControl;

- (void)imageLoadIconDealSizeWithUrl:(NSString *)url placeHolder:(UIImage *)placeHolder imageControl:(UIImageView *)imageControl;

@end

NS_ASSUME_NONNULL_END
