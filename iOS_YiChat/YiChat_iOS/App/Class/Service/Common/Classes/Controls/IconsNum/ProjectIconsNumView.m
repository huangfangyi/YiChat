//
//  ProjectIconsNumView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/18.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectIconsNumView.h"
#import "ServiceGlobalDef.h"
@interface ProjectIconsNumView ()
{
    NSInteger _num;
    
    UILabel *_cNumLab;
}

@end

@implementation ProjectIconsNumView

+ (id)createUIWithFrame:(CGRect)frame num:(NSInteger)num{
    return [[self alloc] initWithFrame:frame num:num];
}

- (id)initWithFrame:(CGRect)frame num:(NSInteger)num{
    self = [super initWithFrame:frame];
    if(self){
        _num = num;
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
    self.layer.cornerRadius = self.frame.size.height / 2;
    self.clipsToBounds = YES;
    self.backgroundColor = [UIColor redColor];
    
    CGFloat h = 32.0;
    
    _cNumLab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(self.frame.size.width / 2 - h / 2, self.frame.size.height / 2 - h / 2,h, h) andfont:PROJECT_TEXT_FONT_COMMON(10) textColor:[UIColor whiteColor] textAlignment:NSTextAlignmentCenter];
    [self addSubview:_cNumLab];
    
    [self updateNum:_num];
}

- (void)updateNum:(NSInteger)num{
    _num = num;
    
    NSString *numStr = [self getNumStrWithNum:_num];
    if(numStr == nil){
        self.hidden = YES;
        _cNumLab.hidden = YES;
    }
    else{
        self.hidden = NO;
        if(num <= 0){
            _cNumLab.hidden = YES;
        }
        else{
            _cNumLab.hidden = NO;
            _cNumLab.text = numStr;
        }
    }
}

- (NSString *)getNumStrWithNum:(NSInteger)num{
    if(num <= 0){
        return nil;
    }
    else if(num <= 99){
        return [NSString stringWithFormat:@"%ld",num];
    }
    else if(num > 99){
        return [[NSString stringWithFormat:@"%d",99] stringByAppendingString:@"+"];
    }
    return nil;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
