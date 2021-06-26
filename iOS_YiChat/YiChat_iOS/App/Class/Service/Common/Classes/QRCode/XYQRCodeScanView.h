//
//  XYQRCodeScanView.h
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/20.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^WSLMyQRCodeBlock)(void);

typedef void(^WSLFlashSwitchBlock)(BOOL open);

@interface XYQRCodeScanView : UIView

/**
 点击我的二维码的回调
 */
@property (nonatomic,copy) WSLMyQRCodeBlock myQRCodeBlock;

/**
 打开/关闭闪光灯的回调
 */
@property (nonatomic,copy) WSLFlashSwitchBlock flashSwitchBlock;

#pragma mark - 扫码区域

/**
 扫码区域 默认为正方形,x = 60, y = 100
 */
@property (nonatomic,assign)CGRect scanRetangleRect;
/**
 @brief  是否需要绘制扫码矩形框，默认YES
 */
@property (nonatomic, assign) BOOL isNeedShowRetangle;
/**
 @brief  矩形框线条颜色
 */
@property (nonatomic, strong, nullable) UIColor *colorRetangleLine;

#pragma mark - 矩形框(扫码区域)周围4个角

//4个角的颜色
@property (nonatomic, strong, nullable) UIColor* colorAngle;
//扫码区域4个角的宽度和高度 默认都为20
@property (nonatomic, assign) CGFloat photoframeAngleW;
@property (nonatomic, assign) CGFloat photoframeAngleH;
/**
 @brief  扫码区域4个角的线条宽度,默认6
 */
@property (nonatomic, assign) CGFloat photoframeLineW;

#pragma mark --动画效果

/**
 *  动画效果的图像
 */
@property (nonatomic,strong, nullable) UIImage * animationImage;
/**
 非识别区域颜色,默认 RGBA (0,0,0,0.5)
 */
@property (nonatomic, strong, nullable) UIColor * notRecoginitonArea;

/**
 *  开始扫描动画
 */
- (void)startScanAnimation;
/**
 *  结束扫描动画
 */
- (void)stopScanAnimation;

/**
 正在处理扫描到的结果
 */
- (void)handlingResultsOfScan;

/**
 完成扫描结果处理
 */
- (void)finishedHandle;


/**
 是否显示闪光灯开关
 @param show YES or NO
 */
- (void)showFlashSwitch:(BOOL)show;

@end

NS_ASSUME_NONNULL_END
