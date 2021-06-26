//
//  YiChatRedPacketInPutView.m
//  YiChat_iOS
//
//  Created by mac on 2019/6/27.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatRedPacketInPutView.h"

@interface YiChatRedPacketInPutView ()<UITextFieldDelegate>
@property (nonatomic,strong) UILabel *redPacketNumLa;
@property (nonatomic,strong) UITextField *redPacketNumField;
@property (nonatomic,strong) UILabel *unitLa;
@end

@implementation YiChatRedPacketInPutView

-(instancetype)init{
    if (self = [super init]) {
        [self setUI];
    }
    return self;
}

-(instancetype)initWithFrame:(CGRect)frame{
    if (self = [super initWithFrame:frame]) {
        [self setUI];
    }
    return self;
}

-(void)setUI{
    self.redPacketNumLa = [[UILabel alloc]initWithFrame:CGRectZero];
    self.redPacketNumLa.font = [UIFont systemFontOfSize:15];
    [self addSubview:self.redPacketNumLa];
    [self.redPacketNumLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(2);
        make.centerY.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(100, 20));
    }];
    
    self.redPacketNumField = [[UITextField alloc]initWithFrame:CGRectZero];
    self.redPacketNumField.textAlignment = NSTextAlignmentRight;
    self.redPacketNumField.delegate = self;
    self.redPacketNumField.keyboardType = UIKeyboardTypeDecimalPad;
    [self addSubview:self.redPacketNumField];
    [self.redPacketNumField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.right.mas_equalTo(-45);
        make.height.mas_equalTo(30);
        make.width.mas_equalTo(PROJECT_SIZE_WIDTH - 70);
    }];

    self.unitLa = [[UILabel alloc]initWithFrame:CGRectZero];
    self.unitLa.font = [UIFont systemFontOfSize:14];
    [self addSubview:self.unitLa];
    [self.unitLa mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(0);
        make.centerY.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(35, 20));
    }];
    
    [self.redPacketNumField addTarget:self action:@selector(changedTextField:) forControlEvents:UIControlEventEditingChanged];
}

#pragma mark -给每个cell中的textfield添加事件，只要值改变就调用此函数
-(void)changedTextField:(UITextField *)textField{
    NSLog(@"值是---%@",textField.text);
    NSString *text = textField.text;
    if (self.textFieldTag == 0) {
        if (text.integerValue > RedPacketNum) {
//            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"红包数量不能超过100个"];
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"红包数量不能超过%d个",RedPacketNum]];
            self.redPacketNumField.text = @"";
            text = @"";
        }
    }else{
        if (self.isGroup) {
            if (text.floatValue > RedPacketMoney) {
//                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"红包总金额不能超过12000元"];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"红包数量不能超过%d个",RedPacketMoney]];
                self.redPacketNumField.text = @"";
                text = @"";
            }
        }
    }
    if ([self.delegate respondsToSelector:@selector(textFieldInPutChangeText:tag:)]) {
        [self.delegate textFieldInPutChangeText:text tag:self.textFieldTag];
    }
}

-(void)setUnit:(NSString *)unit{
    _unit = unit;
    self.unitLa.text = unit;
}

-(void)setRedPacketTitle:(NSString *)redPacketTitle{
    _redPacketTitle = redPacketTitle;
    self.redPacketNumLa.text = redPacketTitle;
}

-(void)setPlaceholder:(NSString *)placeholder{
    _placeholder = placeholder;
    self.redPacketNumField.placeholder = placeholder;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
