//
//  ProjectUIHelper.h
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/22.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "YRPickerManager.h"
#import "ProjectBrowseManager.h"

#define ProjectUIHelper_SearchBarH 60.0f
#define ProjectUIHelper_generalClickBtnH 50.0f
NS_ASSUME_NONNULL_BEGIN

@interface ProjectUIHelper : NSObject

+ (id)ProjectUIHelper_getInputControlViewWithFrame:(CGRect)frame inputStyle:(NSInteger)style;

+ (void)ProjectUIHelper_getAlertWithMsm:(NSString *)msg;

+ (void)ProjectUIHelper_getAlertNoShadowWithMsm:(NSString *)msg;

+ (void)ProjectUIHelper_getAlertWithAlertMessage:(NSString *)message clickBtns:(NSArray *)arr invocation:(void(^)(NSInteger row))click;

+ (id)ProjectUIHelper_getProgressWithText:(NSString *)text;

+ (id)ProjectUIHelper_getProgressValue:(double)value;

+ (id)ProjectUIHelper_getClickBtnWithFrame:(CGRect)frame title:(NSString *)title;

+ (id)projectActionSheetWithListArr:(NSArray *)listArr click:(void(^)(NSInteger row))click;

+ (id)projectCreateNumIconWithPosition:(CGPoint)postion num:(NSInteger)num;

+ (void)projectTabBar:(UITabBarController *)tabVC index:(NSInteger)index changeNum:(NSInteger)num;

+ (void)projectNumIcon:(UIView *)numIcon changeNum:(NSInteger)num;

/**
 *  可以拍照可以选择图片不能拍视频不能选择视频 0
 *  用户只能拍视频或者选视频 1
 *  只能在相册选择图片 2
 *  只能在相册选择图片视频 3
 *  既能拍照选照片又能录视频选视频 4
 *  只能拍照选照片 5
 *  只能在相册选择图片 1张 并剪裁 6
 */
+ (void)projectPhotoVideoPickerWWithType:(NSInteger)type invocation:(void(^)(YRPickerManager *manager,UINavigationController *nav))invocation;

/**
 *  可以拍照可以选择图片不能拍视频不能选择视频 0
 *  用户只能拍视频或者选视频 1
 *  只能在相册选择图片 2
 *  只能在相册选择图片视频 3
 *  既能拍照选照片又能录视频选视频 4
 *  只能拍照选照片 5
 *  只能在相册选择图片 1张 并剪裁 6
 */
+ (void)projectPhotoVideoPickerWWithType:(NSInteger)type pickNum:(NSInteger)num invocation:(void(^)(YRPickerManager *manager,UINavigationController *nav))invocation;

+ (UIFont *)helper_getCommonFontWithSize:(CGFloat)fontSize;

+ (void)helper_showImageBrowseWithDataSouce:(NSArray *)dataSource withSourceObjs:(NSArray *)objs currentIndex:(NSInteger)index;

+ (void)helper_showVideoBrowseWithDataSouce:(NSArray *)dataSource withSourceObjs:(NSArray *)objs currentIndex:(NSInteger)index;

+ (void)helper_showVideoBrowseWithDataSouce:(NSArray *)dataSource withSourceObjs:(NSArray *)objs currentIndex:(NSInteger)index corverImage:(UIImage *)cover;
@end

NS_ASSUME_NONNULL_END
