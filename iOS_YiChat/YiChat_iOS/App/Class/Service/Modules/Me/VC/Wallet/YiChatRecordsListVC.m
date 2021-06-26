//
//  YiChatRecordsListVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/22.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatRecordsListVC.h"
#import "HMSegmentedControl.h"
#import "YiChatTransactionRecordsVC.h"

@interface YiChatRecordsListVC ()
@property (strong, nonatomic) UIPageViewController* pages;
@property (nonatomic, strong) HMSegmentedControl* segmentedControl;
@property (nonatomic, strong) NSMutableArray* viewControllerArray;
@property (assign, nonatomic) NSInteger currentPageIndex;
@end

@implementation YiChatRecordsListVC

+ (id)initialVC{
    YiChatRecordsListVC *walletVC = [YiChatRecordsListVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"RecordsList") leftItem:nil rightItem:nil];
    walletVC.hidesBottomBarWhenPushed = YES;
    return walletVC;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.viewControllerArray = [NSMutableArray new];
    self.view.backgroundColor = [UIColor groupTableViewBackgroundColor];
    self.currentPageIndex = 0;
    [self setUI];
    [self setupContainerView];
    // Do any additional setup after loading the view.
}

-(void)setUI{
    self.segmentedControl = [[HMSegmentedControl alloc] initWithSectionTitles:@[@"全部", @"收入",@"支出"]];
    self.segmentedControl.autoresizingMask = UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleWidth;
    self.segmentedControl.frame = CGRectMake(0, PROJECT_SIZE_STATUSH + PROJECT_SIZE_NAVH, self.view.frame.size.width, 40);
    self.segmentedControl.selectionStyle = HMSegmentedControlSelectionStyleTextWidthStripe;
    self.segmentedControl.selectionIndicatorLocation = HMSegmentedControlSelectionIndicatorLocationDown;
    self.segmentedControl.verticalDividerEnabled = NO;
    self.segmentedControl.selectionIndicatorHeight = 1.5f;
    self.segmentedControl.selectionIndicatorColor = PROJECT_COLOR_APPMAINCOLOR;
    [self.segmentedControl setTitleFormatter:^NSAttributedString*(HMSegmentedControl* segmentedControl, NSString* title, NSUInteger index, BOOL selected) {
        NSAttributedString* attString = [[NSAttributedString alloc] initWithString:title attributes:@{ NSForegroundColorAttributeName : selected ? PROJECT_COLOR_APPMAINCOLOR : [UIColor darkGrayColor],                                                                                                                                                                                                    NSFontAttributeName : [UIFont systemFontOfSize:14] }];
        return attString;
    }];
    [self.segmentedControl addTarget:self action:@selector(segmentedControlChangedValue:) forControlEvents:UIControlEventValueChanged];
    [self.view addSubview:self.segmentedControl];
}

- (void)setupContainerView{
    YiChatTransactionRecordsVC* v1 = [[YiChatTransactionRecordsVC alloc]init];
    v1.recordType = RecordTypeAll;
    YiChatTransactionRecordsVC* v2 = [[YiChatTransactionRecordsVC alloc]init];
    v2.recordType = RecordTypeIncome;
    YiChatTransactionRecordsVC* v3 = [[YiChatTransactionRecordsVC alloc]init];
    v3.recordType = RecordTypeSpending;
    [self.viewControllerArray addObjectsFromArray:@[v1,v2,v3]];
    
    self.pages = [[UIPageViewController alloc] initWithTransitionStyle:UIPageViewControllerTransitionStyleScroll navigationOrientation:UIPageViewControllerNavigationOrientationHorizontal options:nil];
    [self.pages.view setFrame:CGRectZero];
    [self.pages setViewControllers:@[ [self.viewControllerArray objectAtIndex:0] ] direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:nil];
    [self addChildViewController:self.pages];
    [self.view addSubview:self.pages.view];
    [self.pages.view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.segmentedControl.mas_bottom).offset(2);
        make.left.and.right.and.bottom.mas_equalTo(0);
    }];
}



#pragma Segment Delegate
- (void)segmentedControlChangedValue:(HMSegmentedControl*)segmentedControl{
    NSInteger index = segmentedControl.selectedSegmentIndex;
    __weak typeof(self) weakSelf = self;
    
    if (index > self.currentPageIndex) {
        [self.pages setViewControllers:@[ [self.viewControllerArray objectAtIndex:segmentedControl.selectedSegmentIndex] ] direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:^(BOOL finished) {
            weakSelf.currentPageIndex = index;
        }];
    }
    else {
        [self.pages setViewControllers:@[ [self.viewControllerArray objectAtIndex:segmentedControl.selectedSegmentIndex] ] direction:UIPageViewControllerNavigationDirectionReverse animated:YES completion:^(BOOL finished) {
            weakSelf.currentPageIndex = index;
        }];
    }
}


@end
