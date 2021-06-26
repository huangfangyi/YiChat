//
//  ProjectClickView.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/1.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^ProjectClickInvocation)(NSString *identify);


@interface ProjectClickView : UIView

@property (nonatomic,strong)  NSString *identify;

@property (nonatomic,strong)  id model;

@property (nonatomic,strong)  UIImageView *icon;

@property (nonatomic,strong)  UILabel *lab;

@property (nonatomic,copy) ProjectClickInvocation clickInvocation;

- (id)initWithFrame:(CGRect)frame bgView:(UIView *)vgView;

+ (id)createClickViewWithFrame:(CGRect)frame
                         title:(NSString *)title
                          type:(NSInteger)type;

@end

@interface ProjectClickViewModel : NSObject

@property (nonatomic,strong) NSString *icon;

@property (nonatomic,strong) NSString *text;

@end



NS_ASSUME_NONNULL_END
