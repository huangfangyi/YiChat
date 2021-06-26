//
//  XYQRCodeScan.h
//  XY_iOS
//
//  Created by Yang Rui on 2018/12/20.
//  Copyright © 2018 Yang Rui. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN


@interface XYQRCodeScan : UIViewController

@property (nonatomic,copy) void(^XYQRCodeScanInvocation)(NSString *scanStr);

@end

NS_ASSUME_NONNULL_END
