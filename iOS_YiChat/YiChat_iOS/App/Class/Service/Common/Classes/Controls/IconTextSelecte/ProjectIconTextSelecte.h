//
//  ProjectIconTextSelecte.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/8.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProjectIconTextSelecte : UIView

@property (nonatomic,assign) BOOL selecteState;

@property (nonatomic,strong) NSString *selecteText;

@property (nonatomic,strong) NSString *unselecteText;

@property (nonatomic,strong) UIImage *selecteIcon;

@property (nonatomic,strong) UIImage *unselecteIcon;

@property (nonatomic,strong) UIImageView *icon;
@property (nonatomic,strong) UILabel *text;

@property (nonatomic,assign) NSInteger index;

@property (nonatomic,copy) void(^SelecteInvocation)(BOOL state,NSInteger index);

- (void)configureWithSelecteIcon:(UIImage *)icon
                   unselecteIcon:(UIImage *)unselecteIcon
                      seleteText:(NSString *)selecteText
                   unselecteText:(NSString *)unselectetext
                           state:(BOOL)state
                           index:(NSInteger)index;


- (void)makeUI;

- (void)updateUIForState;

- (void)updateUIFrame:(void(^)(UIImageView *icon,UILabel *lab))invocation;

@end

NS_ASSUME_NONNULL_END
