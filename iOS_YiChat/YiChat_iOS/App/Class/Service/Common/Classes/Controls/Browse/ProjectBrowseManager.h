//
//  ProjectBrowseManager.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/4/10.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
NS_ASSUME_NONNULL_BEGIN

@interface ProjectBrowseManager : NSObject

+ (id)create;

/**
 *  datasouce 资源url
 *  obj cell 或者view
 */
- (void)showImageBrowseWithDataSouce:(NSArray *)dataSource withSourceObjs:(NSArray *)objs currentIndex:(NSInteger)index;

- (void)showVideoBrowseWithDataSouce:(NSArray *)dataSource withSourceObjs:(NSArray *)objs currentIndex:(NSInteger)index;

- (void)showVideoBrowseWithDataSouce:(NSArray *)dataSource withSourceObjs:(NSArray *)objs currentIndex:(NSInteger)index corverImage:(UIImage *)cover;

- (void)showImageBrowseWithIcons:(NSArray *)icons bgView:(UIView *)bgView currentIndex:(NSInteger)index;

- (void)showImageBrowseWithIcons:(NSArray *)icons bgViews:(NSArray *)bgViews currentIndex:(NSInteger)index;

- (UIView *)showBrowserWithPhasset:(NSArray *)assets index:(NSInteger)index;

@end

NS_ASSUME_NONNULL_END
