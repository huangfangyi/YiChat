//
//  YiChatMenuView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/24.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatMenuView.h"
#import "ServiceGlobalDef.h"

@interface YiChatMenuView ()
{
    
}

@property (nonatomic,assign) CGRect destinationFrame;
@property (nonatomic,strong) UIImageView *backImage;

@property (nonatomic,copy) HelperintergerObjFlagInvocation click;
@property (nonatomic,copy) HelperIntergeFlagInvocation backClick;
@end

@implementation YiChatMenuView

- (void)createUI{
    
    self.click = self.clickDic[@"click"];
    self.backClick = self.backClickDic[@"click"];
    
    [self makeUI];
}

+ (id)createWithFrame:(CGRect)frame{
    return [[self alloc] initWithFrame:frame];
}

- (void)endAnimateAnimate:(void(^)(BOOL finished))animate{
    WS(weakSelf);
    
    [UIView animateWithDuration:0.1 animations:^{
        weakSelf.backImage.frame = CGRectMake(weakSelf.destinationFrame.origin.x + weakSelf.destinationFrame.size.width / 2,weakSelf.destinationFrame.origin.y, 0, 0);
    } completion:^(BOOL finished) {
        animate(finished);
    }];
}

- (void)clean{
    [self endAnimateAnimate:^(BOOL finished) {
        for (id temp in self.subviews) {
            [temp removeFromSuperview];
        }
        [self removeFromSuperview];
    }];
}

- (void)makeUI{
    UIView *mask = [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height) backGroundColor:[UIColor clearColor]];
    [self addSubview:mask];
    
    UIButton *backClearClickBtn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height) target:self method:@selector(backClickMethod)];
    [self addSubview:backClearClickBtn];
    
    UIImage *back = [UIImage imageNamed:@"qipao.png"];
    WS(weakSelf);
    
    if(back != nil){
        
        NSMutableArray *items = [NSMutableArray arrayWithCapacity:0];
        
        [items addObjectsFromArray:[self getGroupChatItem]];
        
        CGFloat w = 0 ;
        CGFloat h = 0 ;
        if(items.count <= 2){
            back = [UIImage imageNamed:@"qipao.png"];
            w = 120.0;
            h = [ProjectHelper helper_GetWidthOrHeightIntoScale:back.size.width / back.size.height width:w height:0];
        }
        else{
            h = [self getBackHWithItemsCount:items.count];
            w = [ProjectHelper helper_GetWidthOrHeightIntoScale:back.size.width / back.size.height width:0 height:h];
        }
        UIImageView *img = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(self.frame.size.width - PROJECT_SIZE_NAV_BLANK - w + w / 2,PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + h / 2, 0, 0) andImg:back];
        _backImage = img;
        img.userInteractionEnabled = YES;
        [self addSubview:img];
        
        [UIView animateWithDuration:0.2 animations:^{
            img.frame = CGRectMake(self.frame.size.width - PROJECT_SIZE_NAV_BLANK - w + 5.0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, w, h);
            weakSelf.destinationFrame = img.frame;
            
        } completion:^(BOOL finished) {
            CGFloat itemH = [self getItemSingleH];
            CGFloat blank = 5.0;
            for (int i = 0; i<items.count; i++) {
                CGFloat IMGW = 22;
                CGFloat IMGH = IMGW;
                CGFloat x = 5.0;
                
                UIImage *imgIcon = [UIImage imageNamed:items[i][@"icon"]];
                if(imgIcon == nil){
                    IMGW = 0;
                    IMGH = 0;
                }
                
                UIImageView *icon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(blank * 2 ,i * itemH + itemH / 2 - IMGH / 2 + 25.0 / 520.0 * img.frame.size.height , IMGW, IMGH) andImg:imgIcon];
                [img addSubview:icon];
                
                x = icon.frame.origin.x + icon.frame.size.width + blank * 3;
                CGFloat w = img.frame.size.width - x - blank * 3 ;
                if(imgIcon == nil){
                    x = blank;
                    w = img.frame.size.width - x * 2;
                }
                
                UILabel *text = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(x,icon.frame.origin.y + icon.frame.size.height / 2 - itemH / 2, w, itemH) andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:[UIColor whiteColor] textAlignment:NSTextAlignmentLeft];
                [img addSubview:text];
                text.text = items[i][@"text"];
                
                if(imgIcon == nil){
                    text.textAlignment = NSTextAlignmentCenter;
                }
                
                if(i != items.count - 1){
                    UIView *line = [ProjectHelper helper_factoryMakeHorizontalLineWithPoint:CGPointMake(icon.frame.origin.x, text.frame.origin.y + text.frame.size.height - 1) width:(img.frame.size.width - icon.frame.origin.x * 2)];
                    line.backgroundColor = [UIColor whiteColor];
                    [img addSubview:line];
                }
                
                UIButton *btn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:CGRectMake(0,text.frame.origin.y, img.frame.size.width, img.frame.size.height) target:self method:@selector(itemClick:)];
                [img addSubview:btn];
                btn.tag = i + 10000;
            }
        }];
    }
}

- (void)backClickMethod{
    [self clean];
    if(self.backClick){
        self.backClick(-1);
    }
}

- (void)itemClick:(UIButton *)click{
    if(self.click){
        NSArray *data = [self getGroupChatItem];
        if(data.count - 1 >= click.tag - 10000){
            self.click(click.tag - 10000,[self getGroupChatItem][click.tag - 10000]);
        }
    }
}

- (NSArray *)getGroupChatItem{
    return @[
             @{@"icon":@"",@"text":@"发起群聊"},
             @{@"icon":@"",@"text":@"添加好友"},
             @{@"icon":@"",@"text":@"扫一扫"}
//             @{@"icon":@"",@"text":@"帮助"}
           ];
}

- (CGFloat)getBackHWithItemsCount:(NSInteger)count{
    CGFloat singleH = [self getItemSingleH];
    CGFloat total = singleH *count;
    if(total <= [self getItemSingleH] * 2){
        return [self getItemSingleH] * 2;
    }
    else{
        return total + 16.0;
    }
}

- (CGFloat)getItemSingleH{
    if([[self getGroupChatItem] count] <= 2){
        return 60.0;
    }
    else{
        return 40.0;
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
