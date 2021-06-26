//
//  TabBarProjectVC.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/13.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TabBarProjectVC : UITabBarController

- (TabBarProjectVC *(^)(void))addConfigure;

- (TabBarProjectVC *(^)(NSArray *classArr))addClassArr;

- (TabBarProjectVC *(^)(NSArray *textArr))addTextArr;

- (TabBarProjectVC *(^)(NSArray *arr))addDarkIconsArr;

- (TabBarProjectVC *(^)(NSArray *arr))addLightIconsArr;

- (TabBarProjectVC *(^)(void))addUI;

- (void)tabBarProjectVCSelecteIndex:(NSInteger)index;

- (void)addIconNum:(NSInteger)num index:(NSInteger)index;

- (void)removeIconNumWithIndex:(NSInteger)index;
@end

NS_ASSUME_NONNULL_END
