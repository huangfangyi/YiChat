//
//  YRActionSheet.m
//  BussinessManager
//
//  Created by yunlian on 2017/4/11.
//  Copyright © 2017年 yunlian. All rights reserved.
//

#import "YRActionSheet.h"
#import "ServiceGlobalDef.h"

static YRActionSheet *sheet=nil;
static CGFloat animateTime=0.3;

@interface YRActionSheet ()

@property (nonatomic,strong) UIButton *backClearBtn;

@end

#define ScreenH [UIScreen mainScreen].bounds.size.height
#define ScreenW  [UIScreen mainScreen].bounds.size.width
@implementation YRActionSheet

- (id)initWithListArr:(NSArray *)listArr{
    if(!sheet)
    {
        self=[super init];
        [self prepareData];
        NSMutableArray *arr=[NSMutableArray arrayWithCapacity:0];
        
        [arr addObjectsFromArray:listArr];
        [arr addObject:@"取消"];
        
        [self makeUIWithListArr:arr];
    }
    else{
        
        NSMutableArray *arr=[NSMutableArray arrayWithCapacity:0];
        
        [arr addObjectsFromArray:listArr];
        [arr addObject:@"取消"];
        
        [self refreshDataWithArr:arr];
    }
    return self;
}
- (void)prepareData{
    self.labArr=[NSMutableArray arrayWithCapacity:0];
    self.btnArr=[NSMutableArray arrayWithCapacity:0];
    self.titleColor=[UIColor blackColor];
    self.titleFont=16;
    self.cellHeight=[YRGeneralApis yrGeneralApisGetScreenSuitable_H:50.0];
}
- (void)makeUIWithListArr:(NSArray *)listArr{
    [self.backView removeFromSuperview];
           self.backView=nil;
           [self.backClearBtn removeFromSuperview];
           self.backClearBtn = nil;
    
    __block CGFloat maxH = (self.cellHeight + 5) * 6;
    __block CGFloat total = (listArr.count)*(self.cellHeight + 5);
    
    self.frame=CGRectMake(0, ScreenH,ScreenW, total);
    [UIView animateWithDuration:animateTime animations:^{
        CGFloat h = total;
     
        
        if(h > maxH){
            h = maxH;
        }
        self.frame=CGRectMake(0, ScreenH - h,ScreenW, h);
    } completion:^(BOOL finished) {
        
        AppDelegate *appdelegate=(AppDelegate *)[[UIApplication sharedApplication] delegate];
           
           UIView *back=[YRGeneralApis yrGeneralApis_FactoryMakeViewWithFrame:CGRectMake(0, 0, ScreenW, ScreenH) backGroundColor: [UIColor blackColor]];
           back.alpha=0.4;
           back.userInteractionEnabled = NO;
           [appdelegate.window addSubview:back];
           self.backView=back;
           
           appdelegate.window.userInteractionEnabled = YES;
           
           UIButton *backClearBtn = [UIButton buttonWithType:UIButtonTypeCustom];
           backClearBtn.frame = back.bounds;
           [backClearBtn addTarget:self action:@selector(clearBtnMethod:) forControlEvents:UIControlEventTouchUpInside];
           [appdelegate.window  addSubview:backClearBtn];
           self.backClearBtn = backClearBtn;
           
           [appdelegate.window addSubview:self];
           [self.btnArr removeAllObjects];
           
           for (id temp in self.subviews) {
               [temp removeFromSuperview];
           }
           self.userInteractionEnabled=YES;
           
           UIScrollView *scroll = [[UIScrollView alloc] initWithFrame:self.bounds];
           [self addSubview:scroll];
            scroll.backgroundColor = [UIColor whiteColor];
           scroll.userInteractionEnabled = YES;
           CGFloat scrollContentH = maxH;
        
           if(scrollContentH < total){
               scrollContentH = total;
               scroll.bounces = YES;
               scroll.scrollEnabled = YES;
           }
           else{
               scroll.bounces = NO;
               scroll.scrollEnabled = NO;
           }
           
           scroll.contentSize = CGSizeMake(self.frame.size.width, scrollContentH);
           scroll.showsVerticalScrollIndicator = NO;
           scroll.showsHorizontalScrollIndicator = NO;
           
           for (int i=0; i<listArr.count; i++) {
               
               UILabel *lab=[YRGeneralApis  yrGeneralApis_FactoryMakeLabelWithFrame:CGRectMake(0, i*(self.cellHeight), ScreenW, self.cellHeight) andfont:PROJECT_TEXT_FONT_COMMON(self.titleFont) textColor:self.titleColor textAlignment:NSTextAlignmentCenter];
               [scroll addSubview:lab];
               
               lab.backgroundColor=[UIColor whiteColor];
               [self.labArr addObject:lab];
               lab.text=listArr[i];
               
               
               UIButton *btn=[YRGeneralApis yrGeneralApis_FactoryMakeClearButtonWithFrame:CGRectMake(lab.frame.origin.x, lab.frame.origin.y, lab.frame.size.width, lab.frame.size.height) target:self method:@selector(btnActionMethod:)];
               
               btn.userInteractionEnabled=YES;
               [scroll addSubview:btn];

               [self.btnArr addObject:btn];
               
               if(i!=listArr.count-1){
                   
                   UIView *view=[YRGeneralApis yrGeneralApis_FactoryMakeHorizontalLineWithPoint:CGPointMake(lab.frame.origin.x, (lab.frame.origin.y+lab.frame.size.height)) width:ScreenW - lab.frame.origin.x * 2];
                   
                   [scroll addSubview:view];
               }
           }
        
    }];
   
    
}
- (void)btnActionMethod:(UIButton *)btn{
    for (int i=0; i<self.btnArr.count; i++) {
        if(btn==self.btnArr[i]){
            if(self.delegate != nil){
                [self.delegate YRActionSheetDelegate:self GetCurrentClickBtnNumber:i];
            }
            if(self.click){
                self.click(i);
            }
        }
    }
    [self removeItem];
}
- (void)refreshDataWithArr:(NSArray *)listArr{
    [self.btnArr removeAllObjects];
    [self.labArr removeAllObjects];
    [self.backView removeFromSuperview];
    self.backView=nil;
    [self makeUIWithListArr:listArr];
    
}

- (void)clearBtnMethod:(UIButton *)btn{
    if(self.delegate != nil){
        [self.delegate YRActionSheetDelegate:self GetCurrentClickBtnNumber:-1];
    }
    
    if(self.click){
        self.click(-1);
    }
    
    [self removeItem];
}

- (void)removeItem{
    
   
    
    [UIView animateWithDuration:animateTime animations:^{
        self.frame=CGRectMake(0, ScreenH, self.frame.size.width, self.frame.size.height);
    } completion:^(BOOL finished) {
        [self.btnArr removeAllObjects];
        [self.labArr removeAllObjects];
        [self.backView removeFromSuperview];
        self.backView=nil;
        [self removeFromSuperview];
        [self.backClearBtn removeFromSuperview];
        self.backClearBtn = nil;
    }];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end

