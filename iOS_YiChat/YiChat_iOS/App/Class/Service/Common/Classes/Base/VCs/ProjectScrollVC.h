//
//  ProjectScrollVC.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/29.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "NavProjectVC.h"

NS_ASSUME_NONNULL_BEGIN

@interface ProjectScrollVC : NavProjectVC

@property (nonatomic,strong) UIScrollView *cScroll;

@property (nonatomic,strong) NSArray <NSNumber *>*rowsNumSet;
@property (nonatomic) NSInteger sectionNum;

@property (nonatomic) CGRect scrollFrame;

@property (nonatomic) CGSize contentSize;

@property (nonatomic,assign) CGSize reallyContentSize;

@property (nonatomic,strong) UIView *headerView;

@property (nonatomic,strong) UIView *footerView;

- (void)changeScrollFrame:(CGRect)rect;

- (void)xySroll_reloadData;

- (CGFloat)XYScrollController_CellHWithIndex:(NSIndexPath *)index;

- (CGFloat)XYScrollController_SectionHWithIndex:(NSInteger)section;

- (CGFloat)XYScrollController_FooterHWithIndex:(NSInteger)section;

- (CGFloat)XYScroll_getHeaderBeginPositionWithHeader:(NSInteger)section;

- (CGFloat)XYScroll_getFooterBeginPositionWithSection:(NSInteger)section;

- (CGFloat)XYScroll_getRowBeginPositionWithIndex:(NSIndexPath *)index;

- (CGFloat)XYScroll_getHeaderViewBeginPosition;

- (CGFloat)XYScroll_getFooterViewBeginPosition;

- (void)XYScroll_addSubView;

- (void)XYScroll_makeUIForHeaderWithSection:(NSInteger)section;

- (void)XYScroll_makeUIForRow:(NSIndexPath *)row;

- (void)XYScroll_makeUIForFooterWithSection:(NSInteger)section;

@end

NS_ASSUME_NONNULL_END
