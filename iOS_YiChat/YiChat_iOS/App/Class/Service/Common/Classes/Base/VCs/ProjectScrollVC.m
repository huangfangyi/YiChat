//
//  ProjectScrollVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectScrollVC.h"

@interface ProjectScrollVC ()

@end

@implementation ProjectScrollVC

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)changeScrollFrame:(CGRect)rect{
    _scrollFrame = rect;
    self.cScroll.frame = rect;
}

- (UIScrollView *)cScroll{
    if(!_cScroll){
        UIScrollView *scroll = [[UIScrollView alloc] initWithFrame:_scrollFrame];
        scroll.showsVerticalScrollIndicator = YES;
        scroll.showsHorizontalScrollIndicator = NO;
        scroll.scrollEnabled = YES ;
        scroll.bounces = YES;
        _cScroll =  scroll;
    }
    return _cScroll;
}

- (CGSize)contentSize{
    
    CGFloat h = self.reallyContentSize.height;
    if(h <= self.cScroll.frame.size.height){
        h = self.cScroll.frame.size.height + 10.0;
    }
    return CGSizeMake(_scrollFrame.size.width, h);
}

- (CGSize)reallyContentSize{
    CGFloat h = 0;
    h += self.headerView.frame.size.height;
    for (int i = 0; i < _sectionNum; i++) {
        
        h += [self XYScrollController_SectionHWithIndex:i];
        
        NSInteger rowsNum = _rowsNumSet[i].integerValue;
        
        for (int j = 0; j <rowsNum; j++) {
            h += [self XYScrollController_CellHWithIndex:[NSIndexPath indexPathForRow:j inSection:i]];
        }
        
        h += [self XYScrollController_FooterHWithIndex:i];
    }
    
    h += self.footerView.frame.size.height;
    
     return CGSizeMake(_scrollFrame.size.width, h);
}

- (CGFloat)XYScrollController_CellHWithIndex:(NSIndexPath *)index{
    return 0.0001f;
}

- (CGFloat)XYScrollController_SectionHWithIndex:(NSInteger)section{
    return 0.0001f;
}

- (CGFloat)XYScrollController_FooterHWithIndex:(NSInteger)section{
    return 0.00001f;
}

- (CGFloat)XYScroll_getHeaderBeginPositionWithHeader:(NSInteger)section{
    
    CGFloat h = 0;
    h += self.headerView.frame.size.height;
    for (int i = 0; i < self.sectionNum; i++) {
        if(i == section){
            return h;
        }
        h += [self XYScrollController_SectionHWithIndex:i];
        for (int j = 0; j < self.rowsNumSet[i].integerValue; j++) {
            h += [self XYScrollController_CellHWithIndex:[NSIndexPath indexPathForRow:j inSection:i]];
        }
        h += [self XYScrollController_FooterHWithIndex:i];
    }
    h+= self.footerView.frame.size.height;
    
    return 0;
}

- (CGFloat)XYScroll_getFooterBeginPositionWithSection:(NSInteger)section{
    CGFloat h = 0;
    
    for (int i = 0; i < self.sectionNum; i++) {
        
        h += [self XYScrollController_SectionHWithIndex:i];
        for (int j = 0; j < self.rowsNumSet[i].integerValue; j++) {
            h += [self XYScrollController_CellHWithIndex:[NSIndexPath indexPathForRow:j inSection:i]];
        }
        if(i == section){
            return h;
        }
        h += [self XYScrollController_FooterHWithIndex:i];
        
    }
    
    return 0;
}

- (CGFloat)XYScroll_getRowBeginPositionWithIndex:(NSIndexPath *)index{
    CGFloat h = 0;
    
    for (int i = 0; i < self.sectionNum; i++) {
        
        h += [self XYScrollController_SectionHWithIndex:i];
        for (int j = 0; j < self.rowsNumSet[i].integerValue; j++) {
            if (index.section == i && index.row == j) {
                return h;
            }
            h += [self XYScrollController_CellHWithIndex:[NSIndexPath indexPathForRow:j inSection:i]];
        }
        h += [self XYScrollController_FooterHWithIndex:i];
        
    }
    
    return 0;
}

- (CGFloat)XYScroll_getHeaderViewBeginPosition{
    return 0;
}

- (CGFloat)XYScroll_getFooterViewBeginPosition{
    CGFloat h = [self XYScroll_getFooterBeginPositionWithSection:self.sectionNum - 1] + [self XYScrollController_FooterHWithIndex:self.sectionNum - 1];
    
    return h;
    
}

- (void)xySroll_reloadData{
    for (id temp in _cScroll.subviews) {
        [temp removeFromSuperview];
    }
    if(self.headerView != nil){
        self.headerView = nil;
    }
    if(self.footerView != nil){
        self.footerView = nil;
    }
    
    [self XYScroll_addSubView];
}

- (void)XYScroll_addSubView{
    if(self.headerView != nil){
        [_cScroll addSubview:self.headerView];
    }
    for (int i = 0; i < self.sectionNum; i++) {
        [self XYScroll_makeUIForHeaderWithSection:i];
        NSInteger rowNum = self.rowsNumSet[i].integerValue;
        for (int j = 0; j < rowNum; j++) {
            [self XYScroll_makeUIForRow:[NSIndexPath indexPathForRow:j inSection:i]];
        }
        [self XYScroll_makeUIForFooterWithSection:i];
    }
    if(self.footerView != nil){
        [_cScroll addSubview:self.footerView];
    }
}

- (void)XYScroll_makeUIForHeaderWithSection:(NSInteger)section{
    
}

- (void)XYScroll_makeUIForRow:(NSIndexPath *)row{
    
}

- (void)XYScroll_makeUIForFooterWithSection:(NSInteger)section{
    
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
