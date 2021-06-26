//
//  ProjectIconTextSelecte.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/8.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectIconTextSelecte.h"
#import "ProjectDef.h"

@interface ProjectIconTextSelecte ()

@end

@implementation ProjectIconTextSelecte

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    _icon = [UIImageView new];
    [self addSubview:_icon];
    
    _text = [UILabel new];
    [self addSubview:_text];
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = self.bounds;
    [self addSubview:btn];
    [btn addTarget:self action:@selector(selecteMethod:) forControlEvents:UIControlEventTouchUpInside];
}

- (void)selecteMethod:(UIButton *)btn{
    if(self.SelecteInvocation){
        self.SelecteInvocation(_selecteState,self.index);
    }
}

- (void)updateUIForState{
    if(_selecteState == YES){
        _icon.image = _selecteIcon;
        _text.text = _selecteText;
    }
    else{
        _icon.image = _unselecteIcon;
        _text.text = _unselecteText;
    }
}

- (void)configureWithSelecteIcon:(UIImage *)icon
                   unselecteIcon:(UIImage *)unselecteIcon
                      seleteText:(NSString *)selecteText
                   unselecteText:(NSString *)unselectetext
                           state:(BOOL)state
                           index:(NSInteger)index{
    _selecteIcon = icon;
    _unselecteIcon = unselecteIcon;
    _selecteText = selecteText;
    _unselecteText = unselectetext;
    _selecteState = state;
    _index = index;
}



- (void)updateUIFrame:(void(^)(UIImageView *icon,UILabel *lab))invocation{
    if(invocation){
        invocation(self.icon,self.text);
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
