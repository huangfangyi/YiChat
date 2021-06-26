//
//  YiChatRedPacketInfoVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/7/23.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//  

#import "YiChatRedPacketInfoVC.h"
#import "HMSegmentedControl.h"
#import "YiChatRedPacketListVC.h"

@interface YiChatRedPacketInfoVC ()
@property (strong, nonatomic) UIPageViewController* pages;
@property (nonatomic, strong) HMSegmentedControl* segmentedControl;
@property (nonatomic, strong) NSMutableArray* viewControllerArray;
@property (assign, nonatomic) NSInteger currentPageIndex;
@end

@implementation YiChatRedPacketInfoVC

+ (id)initialVC{
    YiChatRedPacketInfoVC *walletVC = [YiChatRedPacketInfoVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"RedPacketInfo") leftItem:nil rightItem:nil];
    walletVC.hidesBottomBarWhenPushed = YES;
    return walletVC;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    self.title = @"我的红包";
    self.viewControllerArray = [NSMutableArray new];
    self.currentPageIndex = 0;
    [self setUI];
    [self setupContainerView];
    // Do any additional setup after loading the view.
}


-(void)setUI{
    self.segmentedControl = [[HMSegmentedControl alloc] initWithSectionTitles:@[@"收到的红包", @"发出的红包"]];
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
    YiChatRedPacketListVC* v1 = [[YiChatRedPacketListVC alloc]init];
    v1.isSend = NO;;
    YiChatRedPacketListVC* v2 = [[YiChatRedPacketListVC alloc]init];
    v2.isSend = YES;
    [self.viewControllerArray addObjectsFromArray:@[v1,v2]];
    
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

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
