//
//  YiChatDynamicListToolBarAppearView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/14.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatDynamicListToolBarAppearView.h"
#import "YiChatDynamicUIConfigure.h"
#import "ServiceGlobalDef.h"

@interface YiChatDynamicListToolBarAppearView ()

@property (nonatomic,weak) YiChatDynamicUIConfigure *uiConfigure;

@property (nonatomic,assign) CGSize singleSize;

@property (nonatomic,strong) NSArray *btnArr;

@property (nonatomic,strong) UILabel *likeLab;

@property (nonatomic,strong) UILabel *commitLab;

@property (nonatomic,assign) BOOL isAppear;

@end

@implementation YiChatDynamicListToolBarAppearView

+ (id)create{
    YiChatDynamicListToolBarAppearView *toolBarAppear = [YiChatDynamicListToolBarAppearView new];
    toolBarAppear.uiConfigure = [YiChatDynamicUIConfigure initialUIConfigure];
    CGFloat w = 0;
    CGFloat h = 0;
    
    w += toolBarAppear.uiConfigure.dynamicCommitClickSize.width;
    h = toolBarAppear.uiConfigure.dynamicCommitClickSize.height;
    
    w += toolBarAppear.uiConfigure.dynamicPraiseClickSize.width;
    
    toolBarAppear.frame = CGRectMake(0, 0,120.0, 30.0);
    
    toolBarAppear.singleSize = CGSizeMake(toolBarAppear.frame.size.width / 2, toolBarAppear.frame.size.height);
    
    [toolBarAppear makeUI];
    
    return toolBarAppear;
}

- (void)makeUI{
    self.layer.cornerRadius = 5.0;
    self.clipsToBounds = YES;
    
    UIImage *like = _uiConfigure.dynamicLikeClickIcon;
    UIImage *commit = _uiConfigure.dynamicCommitClickIcon;
    
    CGSize likeSize =_uiConfigure.dynamicLikeIconSize;
    CGSize commitSize = _uiConfigure.dynamicCommitIconSize;
    
    CGFloat blank = 0;
    CGFloat totalw = 0;
    CGFloat w = 0;
    CGFloat h = 0;
    CGFloat textW = 30;
    UIImage *appearIcon = nil;
    NSString *text = nil;
    NSMutableArray *btnArr = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < 2; i ++) {
        
        if(i == 0){
            w = likeSize.width;
            h = likeSize.height;
            totalw = w + blank + textW;
            appearIcon = like;
            text = @"赞";
        }
        else if(i == 1){
            w = commitSize.width;
            h = commitSize.height;
            totalw = w + blank + textW;
            appearIcon = commit;
            text = @"评论";
        }
        
        UIView *back = [[UIView alloc] initWithFrame:CGRectMake(i * _singleSize.width, 0, _singleSize.width, _singleSize.height)];
        [self addSubview:back];
        back.backgroundColor = [UIColor grayColor];
        
        UIImageView *icon = [UIImageView new];
        icon.frame = CGRectMake(back.frame.size.width / 2 - totalw / 2, back.frame.size.height / 2 - h / 2, w, h);
        [back addSubview:icon];
        icon.image = appearIcon;
        icon.userInteractionEnabled = NO;
        
        
        UILabel *lab = [UILabel new];
        lab.frame = CGRectMake(icon.frame.origin.x + icon.frame.size.width + blank , 0,textW , back.frame.size.height);
        lab.font = PROJECT_TEXT_FONT_COMMON(13);
        lab.text = text;
        lab.textColor = [UIColor whiteColor];
        lab.textAlignment = NSTextAlignmentLeft;
        [back addSubview:lab];
        lab.userInteractionEnabled = NO;
        if(i == 0){
            _likeLab = lab;
        }
        else{
            _commitLab = lab;
        }
        
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        [self addSubview:btn];
        btn.frame = back.frame;
        [btn addTarget:self action:@selector(clickMethod:) forControlEvents:UIControlEventTouchUpInside];
        
        [btnArr addObject:btn];
    }
    
    _btnArr = btnArr;
    
    
}

- (void)changeToOriginPosition:(CGPoint)point{
    self.frame = CGRectMake(point.x, point.y, self.frame.size.width, self.frame.size.height);
}

- (void)beginAppearToPoint:(CGPoint)point isAnimate:(BOOL)isAnimate{
    CGRect rect =  CGRectMake(point.x, point.y, self.frame.size.width, self.frame.size.height);
    if(_isAppear){
        self.frame = rect;
        return;
    }
    _isAppear = YES;
    
    if(isAnimate){
        self.alpha = 0;
        [UIView animateWithDuration:0.3 animations:^{
            self.alpha = 1;
            self.frame = rect;
        } completion:^(BOOL finished) {
           
        }];
    }
    else{
        self.frame = rect;
    }
}

- (void)disappearToPoint:(CGPoint)point isAnimate:(BOOL)isAnimate invacation:(void(^)(void))invcation{
    CGRect rect =  CGRectMake(point.x, point.y, self.frame.size.width, self.frame.size.height);
    _isAppear = NO;
    if(isAnimate){
        [UIView animateWithDuration:0.3 animations:^{
            self.frame = rect;
            self.alpha = 0;
        } completion:^(BOOL finished) {
            invcation();
        }];
    }
    else{
        self.frame = rect;
        invcation();
    }
}

- (void)clickMethod:(UIButton *)btn{
    for (int i = 0; i < _btnArr.count; i ++) {
        if(_btnArr[i] == btn){
            if(i == 0){
                if(self.YiChatDynamicListToolBarAppearViewLikeClick){
                    self.YiChatDynamicListToolBarAppearViewLikeClick(self.trendId, self.index);
                }
            }
            else if(i == 1){
                if(self.YiChatDynamicListToolBarAppearViewCommitClick){
                    self.YiChatDynamicListToolBarAppearViewCommitClick(self.trendId, self.index);
                }
            }
        }
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
