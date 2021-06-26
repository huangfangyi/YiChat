//
//  YiChatVersionUpdateView.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/23.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatVersionUpdateView.h"
static NSString *cancel = @"暂不更新";
static NSString *update = @"前往更新";

@implementation YiChatVersionUpdateView

-(instancetype)initWithFrame:(CGRect)frame isMandatory:(BOOL)isMandatory version:(NSString *)version{
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor whiteColor];
        self.layer.cornerRadius = 10;
        self.layer.masksToBounds = YES;
        
        UILabel *la = [[UILabel alloc]initWithFrame:CGRectZero];
        la.text = @"版本更新";
        la.textAlignment = NSTextAlignmentCenter;
        [self addSubview:la];
        [la mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(10);
            make.left.right.mas_equalTo(0);
            make.height.mas_equalTo(20);
        }];
        
        UILabel *contentLa = [[UILabel alloc] initWithFrame:CGRectZero];
        contentLa.text = [NSString stringWithFormat:@"有新的%@版本可以更新！",version];
        contentLa.textAlignment = NSTextAlignmentCenter;
        [self addSubview:contentLa];
        [contentLa mas_makeConstraints:^(MASConstraintMaker *make) {
            make.center.mas_equalTo(0);
            make.left.right.mas_equalTo(0);
            make.height.mas_equalTo(40);
        }];
        
        NSMutableArray *btnTitleArr = [NSMutableArray new];
        if (isMandatory) {
            [btnTitleArr addObjectsFromArray:@[update]];
        }else{
            [btnTitleArr addObjectsFromArray:@[cancel,update]];
        }
        
        
        CGFloat spacing = 30.0;
        CGFloat leftSpacing = 15.0;
        CGFloat w = (frame.size.width - btnTitleArr.count * spacing) / btnTitleArr.count;
        for (NSInteger i = 0; i < btnTitleArr.count; i++) {
            UIButton *btn = [[UIButton alloc] initWithFrame:CGRectZero];
            [btn setTitle:btnTitleArr[i] forState:UIControlStateNormal];
            [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
            btn.layer.masksToBounds = YES;
            btn.layer.cornerRadius = 5;
            [btn addTarget:self action:@selector(click:) forControlEvents:UIControlEventTouchUpInside];
            [self addSubview:btn];
            if ([btnTitleArr[i] isEqualToString:cancel]) {
                btn.backgroundColor = [UIColor redColor];
            }else{
                btn.backgroundColor = PROJECT_COLOR_APPMAINCOLOR;
            }
            [btn mas_makeConstraints:^(MASConstraintMaker *make) {
                make.bottom.mas_equalTo(-15);
                make.size.mas_equalTo(CGSizeMake(w, 35));
                make.left.mas_equalTo(leftSpacing);
            }];
            leftSpacing = leftSpacing + w + spacing;
        }
    }
    
    return self;
}

-(void)click:(UIButton *)sender{
    if ([sender.titleLabel.text isEqualToString:cancel]) {
        self.versionBlock(YES);
    }else{
        self.versionBlock(NO);
    }
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
