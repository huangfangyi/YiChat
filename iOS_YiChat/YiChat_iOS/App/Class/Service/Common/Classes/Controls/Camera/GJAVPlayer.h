//
//  GJAVPlayer.h
//  GJCamera
//
//  Created by 郭杰 on 2018/9/22.
//  Copyright © 2018年 郭杰. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface GJAVPlayer : UIView
    
    @property (copy, nonatomic) NSURL *videoUrl;

- (instancetype)initWithFrame:(CGRect)frame withShowInView:(UIView *)bgView url:(NSURL *)url;


- (void)stopPlayer;

@end
