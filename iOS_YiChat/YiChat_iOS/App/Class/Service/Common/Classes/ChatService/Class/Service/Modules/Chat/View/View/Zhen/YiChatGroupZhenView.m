//
//  YiChatGroupZhenView.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/16.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupZhenView.h"

@interface YiChatGroupZhenView ()
@property (nonatomic,strong) UITextField *textField;
@end

@implementation YiChatGroupZhenView

-(instancetype)initWithFrame:(CGRect)frame{
    if (self = [super initWithFrame:frame]) {
//        UIView *bg = [[UIView alloc]initWithFrame:CGRectMake(0, 0, PROJECT_SIZE_WIDTH - 60, 170)];
        if (@available(iOS 13.0, *)) {
           // self.overrideUserInterfaceStyle = UIUserInterfaceStyleLight;
        } else {
            // Fallback on earlier versions
        }
        self.backgroundColor = [UIColor whiteColor];
        self.layer.masksToBounds = YES;
        self.layer.cornerRadius = 5;
        UILabel *la = [[UILabel alloc] initWithFrame:CGRectMake(0, 10, PROJECT_SIZE_WIDTH - 60, 20)];
        la.text = @"您确定要震群内所有人吗？";
        la.textAlignment = NSTextAlignmentCenter;
        [self addSubview:la];
        
        self.textField = [[UITextField alloc]initWithFrame:CGRectZero];
        self.textField.layer.masksToBounds = YES;
        self.textField.layer.cornerRadius = 2;
        self.textField.layer.borderColor = [UIColor groupTableViewBackgroundColor].CGColor;
        self.textField.layer.borderWidth = 0.5;
        self.textField.placeholder = @"请填写要震的内容点击确认直接震";
        [self addSubview:self.textField];
        [self.textField mas_makeConstraints:^(MASConstraintMaker *make) {
            make.center.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(PROJECT_SIZE_WIDTH - 100, 40));
        }];
        
        UIView *line = [[UIView alloc]initWithFrame:CGRectZero];
        line.backgroundColor = [UIColor grayColor];
        line.alpha = 0.6;
        [self addSubview:line];
        [line mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.mas_equalTo(-45);
            make.centerX.mas_equalTo(0);
            make.size.mas_equalTo(CGSizeMake(PROJECT_SIZE_WIDTH - 60, 0.5));
        }];
        
        UIView *line1 = [[UIView alloc]initWithFrame:CGRectZero];
        line1.backgroundColor = [UIColor grayColor];
        line1.alpha = 0.6;
        [self addSubview:line1];
        [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(line.mas_bottom).offset(0);
            make.centerX.mas_equalTo(0);
            make.bottom.mas_equalTo(0);
            make.width.mas_equalTo(0.5);
        }];
        
        UIButton *cancel = [[UIButton alloc]initWithFrame:CGRectZero];
        cancel.tag = 0;
        [cancel setTitle:@"取消" forState:UIControlStateNormal];
        [cancel setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        [cancel addTarget:self action:@selector(promptAction:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:cancel];
        [cancel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.bottom.mas_equalTo(0);
            make.right.equalTo(line1.mas_left).offset(0);
            make.top.equalTo(line.mas_bottom).offset(0);
        }];
        
        UIButton *determine = [[UIButton alloc]initWithFrame:CGRectZero];
        determine.tag = 1;
        [determine setTitle:@"确定" forState:UIControlStateNormal];
        [determine setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        [determine addTarget:self action:@selector(promptAction:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:determine];
        [determine mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.bottom.mas_equalTo(0);
            make.left.equalTo(line1.mas_right).offset(0);
            make.top.equalTo(line.mas_bottom).offset(0);
        }];

    }
    return self;
}

//透传震动消息
-(void)promptAction:(UIButton *)sender{
    [self endEditing:YES];
    BOOL isCancel = NO;
    if (sender.tag == 0) {
        isCancel = YES;
    }
    NSString *str = @"";
    if (self.textField.text.length == 0 || self.textField.text == nil || [self.textField.text isEqualToString:@""]) {
        str = @"震所有群友上线";
    }else{
        str = self.textField.text;
    }
    self.zhengBlock(str,isCancel);
}
@end
